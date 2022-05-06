package org.talon.ad.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.talon.ad.datamodel.unitcondition.AdUnitInterest;
import org.talon.ad.exception.AdException;
import org.talon.ad.service.IAdUnitService;
import org.talon.ad.vo.AdUnitRequest;
import org.talon.ad.vo.AdUnitResponse;
import org.talon.ad.vo.unitcondition.*;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

@Slf4j
@RestController
public class AdUnitController {
    private final IAdUnitService adUnitService;
    private final Gson gson;

    @Autowired
    public AdUnitController(IAdUnitService adUnitService) {
        this.adUnitService = adUnitService;
        this.gson = new Gson();
    }

    @PostMapping("/create/adUnit")
    public AdUnitResponse createUnit(
            @RequestBody AdUnitRequest request
            ) throws AdException {
        log.info("ad-sponsor: createUnit -> {}", gson.toJson(request));
        return adUnitService.createUnit(request);
    }

    @PostMapping("/update/adUnit")
    public AdUnitResponse updateUnit(
            @RequestBody AdUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: updateUnit -> {}", gson.toJson(request));
        return adUnitService.updateUnit(request);
    }

    @PostMapping("/delete/adUnit")
    public void deleteUnit(
            @RequestBody AdUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: deleteUnit -> {}", gson.toJson(request));
        adUnitService.deleteUnit(request);
    }

    @PostMapping("/create/unitKeyword")
    public AdUnitKeywordResponse createUnitKeyword(
            @RequestBody AdUnitKeywordRequest request
            ) throws AdException {
        log.info("ad-sponsor: unitKeyword -> {}", gson.toJson(request));
        return adUnitService.createUnitKeyword(request);
    }

    @PostMapping("/create/unitInterest")
    public AdUnitInterestResponse createUnitInterest(
            @RequestBody AdUnitInterestRequest request
            ) throws AdException {
        log.info("ad-sponsor: unitInterest -> {}", gson.toJson(request));
        return adUnitService.createUnitInterest(request);
    }

    @PostMapping("/create/unitDistrict")
    public AdUnitDistrictResponse createUnitDistrict(
            @RequestBody AdUnitDistrictRequest request
    ) throws AdException {
        log.info("ad-sponsor: unitDistrict -> {}", gson.toJson(request));
        return adUnitService.createUnitDistrict(request);
    }

    @PostMapping("/create/creativeUnit")
    public CreativeUnitResponse createCreativeUnit(
            @RequestBody CreativeUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: creativeUnit -> {}", gson.toJson(request));
        return adUnitService.createCreativeUnit(request);
    }
}
