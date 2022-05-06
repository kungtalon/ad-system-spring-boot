package org.talon.ad.search;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.exception.AdException;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.adunit.AdUnitIndexRedis;
import org.talon.ad.index.adunit.AdUnitObject;
import org.talon.ad.index.creative.CreativeIndexRedis;
import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.index.creativeunit.CreativeUnitIndexRedis;
import org.talon.ad.search.retrieve.IRetriever;
import org.talon.ad.search.selector.ISelector;
import org.talon.ad.search.vo.SearchRequest;
import org.talon.ad.search.vo.SearchResponse;
import org.talon.ad.search.vo.feature.*;
import org.talon.ad.search.vo.media.AdSlot;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class AsyncSearch implements ISearch {

    private final Map<String, IRetriever> searchRetrievers;

    private final List<ISelector> adSelectors;

    private final Gson gson = new Gson();

    @Autowired
    public AsyncSearch(Map<String, IRetriever> searchRetrievers,
                       List<ISelector> adSelectors) {
        this.searchRetrievers = searchRetrievers;
        this.adSelectors = adSelectors;
    }

    @PostConstruct
    public void init() {
        adSelectors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    @Override
    public SearchResponse fetchAds(SearchRequest request) throws AdException {

        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();

        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        InterestFeature interestFeature = request.getFeatureInfo().getInterestFeature();

        List<RetrieverFeaturePair> retrieverFeaturePairs = Arrays.asList(
                new RetrieverFeaturePair(searchRetrievers.get("keywordRetriever"), keywordFeature),
                new RetrieverFeaturePair(searchRetrievers.get("districtRetriever"), districtFeature),
                new RetrieverFeaturePair(searchRetrievers.get("interestRetriever"), interestFeature)
        );

        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlots2Ads = response.getAdSlot2Ads();

        for (AdSlot adSlot : adSlots) {
            Set<Long> targetUnitIdSet;

            Set<Long> adUnitIdSet = new HashSet<>();

            FutureTask<Set<Long>> futureRetrievalTasks[] = new FutureTask[retrieverFeaturePairs.size()];
            for (int ix = 0; ix < retrieverFeaturePairs.size(); ++ ix) {
                RetrieverFeaturePair pair = retrieverFeaturePairs.get(ix);
                Callable<Set<Long>> call = () -> pair.getRetriever().retrieve(pair.getFeature());
                FutureTask<Set<Long>> future = new FutureTask<>(call);
                futureRetrievalTasks[ix] = future;
            }

            ExecutorService executor = Executors.newFixedThreadPool(2);
            for (int ix = 0; ix < futureRetrievalTasks.length; ++ ix){
                executor.submit(futureRetrievalTasks[ix]);
            }

            try {
                for (int ix = 0; ix < futureRetrievalTasks.length; ++ix){
                    adUnitIdSet.addAll(futureRetrievalTasks[ix].get(250, TimeUnit.MILLISECONDS));
                }
            } catch (ExecutionException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }

            // filter candidate ad units by position type
            targetUnitIdSet = DataTable.of(
                    AdUnitIndexRedis.class
            ).filterByPositionType(adUnitIdSet, adSlot.getPositionType());

            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndexRedis.class)
                    .retrieveByIds(targetUnitIdSet);

            unitObjects = filterInvalidUnits(unitObjects);

            List<Long> creativeIds = DataTable.of(CreativeUnitIndexRedis.class)
                    .getCreativeIdsByAdUnits(unitObjects);

            List<CreativeObject> creatives = DataTable.of(CreativeIndexRedis.class)
                    .retrieveByIds(creativeIds);

            creatives = filterCreativeByAdSlot(
                    creatives,
                    adSlot.getWidth(),
                    adSlot.getHeight(),
                    adSlot.getSupportedTypes()
            );

            adSlots2Ads.put(adSlot.getAdSlotCode(), buildCreativeResponse(creatives, request));
        }

        log.info("fetchAds: {} - {}",
                gson.toJson(request),
                gson.toJson(response));

        return response;
    }

    @Data
    @AllArgsConstructor
    public static class RetrieverFeaturePair {
        private IRetriever retriever;
        private SearchFeature feature;
    }

    private List<AdUnitObject> filterInvalidUnits(List<AdUnitObject> unitObjects) {
        if (CollectionUtils.isEmpty(unitObjects)) {
            return unitObjects;
        }

        return unitObjects.stream().filter(object -> {
                Integer unitStatus = object.getUnitStatus();
                Integer planStatus = object.getPlanObject().getPlanStatus();
                return unitStatus.equals(CommonStatus.VALID.getStatus()) &&
                        planStatus.equals(CommonStatus.VALID.getStatus());
            }).collect(Collectors.toList());
    }

    private List<CreativeObject> filterCreativeByAdSlot(List<CreativeObject> creativeObjects,
                                        Integer width,
                                        Integer height,
                                        List<Integer> supportedTypes) {
        if (CollectionUtils.isEmpty(creativeObjects)) {
            return creativeObjects;
        }

        return creativeObjects.stream().filter(object -> {
            if (!object.getAuditStatus().equals(CommonStatus.VALID.getStatus())) {
                return false;
            }

            if (object.getWidth() > width || object.getHeight() > height) {
                return false;
            }

            if (!supportedTypes.contains(object.getType())) {
                return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    private List<SearchResponse.Creative> buildCreativeResponse(List<CreativeObject> creativeObjects,
                                                                SearchRequest request) {
        if (CollectionUtils.isEmpty(creativeObjects)) {
            return Collections.emptyList();
        }

        List<CreativeObject> result = new ArrayList<>();
        for (ISelector selector : adSelectors) {
            result = selector.select(creativeObjects, request);
            if (CollectionUtils.isNotEmpty(result)) {
                break;
            }
        }

        return result.stream().map(SearchResponse::convert).collect(Collectors.toList());
    }
}
