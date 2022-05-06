package org.talon.ad.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.talon.ad.datamodel.AdPlan;
import org.talon.ad.exception.AdException;
import org.talon.ad.service.IAdPlanService;
import org.talon.ad.vo.AdPlanGetRequest;
import org.talon.ad.vo.AdPlanRequest;
import org.talon.ad.vo.AdPlanResponse;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/2
 * All is POST method
 * @RequestBody requires a body in the POST request
 * the body will be converted into the AdPlanRequest type
 **/
@Slf4j
@RestController
public class AdPlanController {

    private IAdPlanService adPlanService;
    private final Gson gson;

    @Autowired
    public AdPlanController(IAdPlanService adPlanService) {
        this.adPlanService = adPlanService;
        this.gson = new Gson();
    }

    @PostMapping("/create/adPlan")
    public AdPlanResponse createAdPlan (
            @RequestBody AdPlanRequest request) throws AdException {
        log.info("ad-sponsor: createAdPlan -> {}", gson.toJson(request));
        return adPlanService.createAdPlan(request);
    }

    @PostMapping("/get/adPlan")
    public List<AdPlan> getAdPlanByIds(
            @RequestBody AdPlanGetRequest request
            ) throws AdException {
        log.info("ad-sponsor: getAdPlanByIds -> {}", gson.toJson(request));
        return adPlanService.getAdPlanByIds(request);
    }

    @PostMapping("/update/adPlan")
    public AdPlanResponse updateAdPlan(
            @RequestBody AdPlanRequest request) throws AdException {
        log.info("ad-sponsor: updateAdPlan -> {}", gson.toJson(request));
        return adPlanService.updateAdPlan(request);
    }

    @PostMapping("/delete/adPlan")
    public void deleteAdPlan(
            @RequestBody AdPlanRequest request) throws AdException {
        log.info("ad-sponsor: deleteAdPlan -> {}", gson.toJson(request));
        adPlanService.deleteAdPlan(request);
    }

}
