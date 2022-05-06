package org.talon.ad.search;

import com.google.gson.Gson;
import com.sun.jndi.toolkit.dir.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.exception.AdException;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.adunit.AdUnitIndexRedis;
import org.talon.ad.index.adunit.AdUnitObject;
import org.talon.ad.index.creative.CreativeIndexRedis;
import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.index.creativeunit.CreativeUnitIndexRedis;
import org.talon.ad.search.filter.IFilter;
import org.talon.ad.search.selector.ISelector;
import org.talon.ad.search.vo.SearchRequest;
import org.talon.ad.search.vo.SearchResponse;
import org.talon.ad.search.vo.feature.*;
import org.talon.ad.search.vo.media.AdSlot;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class SyncSearch implements ISearch {

    private final Map<String, IFilter> searchFilters;

    private final ISelector adSelector;

    private final Gson gson = new Gson();

    @Autowired
    public SyncSearch(Map<String, IFilter> featureFilters,
                      @Qualifier("randomSelector") ISelector adSelector) {
        this.searchFilters = featureFilters;
        this.adSelector = adSelector;
    }

    @Override
    public SearchResponse fetchAds(SearchRequest request) throws AdException {

        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();

        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        InterestFeature interestFeature = request.getFeatureInfo().getInterestFeature();

        List<FilterFeaturePair> filterFeaturePairs = Arrays.asList(
                new FilterFeaturePair(searchFilters.get("keywordFilter"), keywordFeature),
                new FilterFeaturePair(searchFilters.get("districtFilter"), districtFeature),
                new FilterFeaturePair(searchFilters.get("interestFilter"), interestFeature)
        );

        FeatureRelation relation = request.getFeatureInfo().getRelation();

        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlots2Ads = response.getAdSlot2Ads();

        for (AdSlot adSlot : adSlots) {
            Set<Long> targetUnitIdSet;

            // retrieve candidate ad units by position type
            Set<Long> adUnitIdSet = DataTable.of(
                    AdUnitIndexRedis.class
            ).filterByPositionType(adSlot.getPositionType());

            if (relation == FeatureRelation.AND) {
                targetUnitIdSet = getANDResultSet(adUnitIdSet, filterFeaturePairs);
            } else {
                targetUnitIdSet = getORResultSet(adUnitIdSet, filterFeaturePairs);
            }

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
    public static class FilterFeaturePair {
        private IFilter filter;
        private SearchFeature feature;
    }

    private Set<Long> getANDResultSet(Set<Long> candidateIds, List<FilterFeaturePair> pairs) {
        Set<Long> result = new HashSet<>();

        for (FilterFeaturePair p : pairs){
            result = p.getFilter().filter(candidateIds, p.getFeature());
        }
        return result;
    }

    private Set<Long> getORResultSet(Set<Long> candidateIds, List<FilterFeaturePair> pairs) {
        Optional<Set<Long>> opUnitIdSet = pairs.stream()
                .map(p -> p.getFilter().filter(candidateIds, p.getFeature()))
                .reduce((l, r) -> {
                    Set<Long> union = new HashSet<>(l);
                    union.addAll(r);
                    return union;
                });
        return opUnitIdSet.orElse(Collections.emptySet());
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

        List<CreativeObject> selected = adSelector.select(
                creativeObjects,
                request
        );

        return selected.stream().map(SearchResponse::convert).collect(Collectors.toList());
    }
}
