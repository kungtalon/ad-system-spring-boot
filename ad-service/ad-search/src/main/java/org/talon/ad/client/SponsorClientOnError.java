package org.talon.ad.client;

import org.springframework.stereotype.Component;
import org.talon.ad.client.vo.AdPlan;
import org.talon.ad.client.vo.AdPlanGetRequest;
import org.talon.ad.exception.AdException;
import org.talon.ad.vo.CommonResponse;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/3
 * A fallback handler when an error occurs when calling feign
 **/

@Component
public class SponsorClientOnError implements SponsorClient {

    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) throws AdException {
        return new CommonResponse<>(-1, "eureka-client-ad-sponsor error");
    }
}
