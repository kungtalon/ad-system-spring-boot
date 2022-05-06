package org.talon.ad.service;

import org.talon.ad.datamodel.AdPlan;
import org.talon.ad.exception.AdException;
import org.talon.ad.vo.AdPlanGetRequest;
import org.talon.ad.vo.AdPlanRequest;
import org.talon.ad.vo.AdPlanResponse;

import java.util.List;

public interface IAdPlanService {
    AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException;

    List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException;

    AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException;

    void deleteAdPlan(AdPlanRequest request) throws AdException;
}
