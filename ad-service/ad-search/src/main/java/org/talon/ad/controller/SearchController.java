package org.talon.ad.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.talon.ad.annotation.IgnoreResponseAdvice;
import org.talon.ad.client.SponsorClient;
import org.talon.ad.client.vo.AdPlan;
import org.talon.ad.client.vo.AdPlanGetRequest;
import org.talon.ad.exception.AdException;
import org.talon.ad.search.ISearch;
import org.talon.ad.search.vo.SearchRequest;
import org.talon.ad.search.vo.SearchResponse;
import org.talon.ad.vo.CommonResponse;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Slf4j
@RestController
public class SearchController {

    private final ISearch searchService;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final SponsorClient sponsorClient;

    @Autowired
    public SearchController(RestTemplate restTemplate,
                            @Qualifier("sponsorClientOnError") SponsorClient sponsorClient,
                            @Qualifier("asyncSearch") ISearch search) {
        this.restTemplate = restTemplate;
        this.sponsorClient = sponsorClient;
        this.searchService = search;
        this.gson = new Gson();
    }

    @PostMapping("/getAds")
    public SearchResponse retrieveAds(
            @RequestBody SearchRequest request) throws AdException {
        log.info("ad-search: getAds -> {}", gson.toJson(request));
        return searchService.fetchAds(request);
    }

    @IgnoreResponseAdvice
    @PostMapping("/getAdPlans")
    public CommonResponse<List<AdPlan>> getAdPlans(
            @RequestBody AdPlanGetRequest request
    ) throws AdException {
        log.info("ad-search: getAdPlans -> {}", gson.toJson(request));
        return sponsorClient.getAdPlans(request);
    }

    @SuppressWarnings("all")
    @IgnoreResponseAdvice
    @PostMapping("/getAdPlansByRibbon")
    public CommonResponse<List<AdPlan>> getAdPlanByRibbon (
            @RequestBody AdPlanGetRequest request
        ) throws AdException {
        log.info("ad-search: getAdPlanByRibbon -> {}", gson.toJson(request));
        return restTemplate.postForEntity(
                "http://eureka-client-ad-sponsor/ad-sponsor/get/adPlan",
                request,
                CommonResponse.class
        ).getBody();
    }

}
