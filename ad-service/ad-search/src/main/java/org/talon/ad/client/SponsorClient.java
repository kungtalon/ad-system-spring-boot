package org.talon.ad.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talon.ad.client.vo.AdPlan;
import org.talon.ad.client.vo.AdPlanGetRequest;
import org.talon.ad.exception.AdException;
import org.talon.ad.vo.CommonResponse;

import java.util.List;

@FeignClient(value = "eureka-client-ad-sponsor",  fallback = SponsorClientOnError.class)
public interface SponsorClient {

    @RequestMapping(value = "/ad-sponsor/get/adPlan", method= RequestMethod.POST)
    CommonResponse<List<AdPlan>> getAdPlans(
            @RequestBody AdPlanGetRequest request) throws AdException;
}
