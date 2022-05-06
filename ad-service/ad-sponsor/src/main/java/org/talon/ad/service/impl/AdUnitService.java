package org.talon.ad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.constant.Consts;
import org.talon.ad.datamodel.AdPlan;
import org.talon.ad.datamodel.AdUnit;
import org.talon.ad.datamodel.unitcondition.AdUnitDistrict;
import org.talon.ad.datamodel.unitcondition.AdUnitInterest;
import org.talon.ad.datamodel.unitcondition.AdUnitKeyword;
import org.talon.ad.datamodel.unitcondition.CreativeUnit;
import org.talon.ad.exception.AdException;
import org.talon.ad.repository.IAdPlanRepository;
import org.talon.ad.repository.IAdUnitRepository;
import org.talon.ad.repository.ICreativeRepository;
import org.talon.ad.repository.unitcondition.IAdUnitDistrictRepository;
import org.talon.ad.repository.unitcondition.IAdUnitInterestRepository;
import org.talon.ad.repository.unitcondition.IAdUnitKeywordRepository;
import org.talon.ad.repository.unitcondition.ICreativeUnitRepository;
import org.talon.ad.service.IAdUnitService;
import org.talon.ad.vo.AdUnitRequest;
import org.talon.ad.vo.AdUnitResponse;
import org.talon.ad.vo.unitcondition.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

@Service
public class AdUnitService implements IAdUnitService {

    private IAdUnitRepository unitRepository;
    private IAdPlanRepository planRepository;

    private IAdUnitKeywordRepository unitKeywordRepository;
    private IAdUnitInterestRepository unitInterestRepository;
    private IAdUnitDistrictRepository unitDistrictRepository;

    private ICreativeRepository creativeRepository;
    private ICreativeUnitRepository creativeUnitRepository;

    @Autowired
    public AdUnitService(IAdUnitRepository unitRepository,
                         IAdPlanRepository planRepository,
                         IAdUnitKeywordRepository unitKeywordRepository,
                         IAdUnitInterestRepository unitInterestRepository,
                         IAdUnitDistrictRepository unitDistrictRepository,
                         ICreativeRepository creativeRepository,
                         ICreativeUnitRepository creativeUnitRepository) {
        this.unitRepository = unitRepository;
        this.planRepository = planRepository;
        this.unitKeywordRepository = unitKeywordRepository;
        this.unitInterestRepository = unitInterestRepository;
        this.unitDistrictRepository = unitDistrictRepository;
        this.creativeRepository = creativeRepository;
        this.creativeUnitRepository = creativeUnitRepository;
    }

    @Override
    public AdUnitResponse createUnit(AdUnitRequest request) throws AdException {
        if (!request.createValidate()) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdPlan> plan = planRepository.findById(request.getPlanId());
        if (!plan.isPresent()) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        AdUnit oldUnit = unitRepository.findByPlanIdAndUnitName(
                request.getPlanId(), request.getUnitName()
        );
        if (oldUnit != null) {
            throw new AdException(Consts.ErrorMsg.UNIT_NAME_EXISTS_ERROR);
        }

        AdUnit newUnit = unitRepository.save(
                new AdUnit(
                        request.getPlanId(), request.getUnitName(),
                        request.getPositionType(), request.getBudget()
                )
        );

        return new AdUnitResponse(
                newUnit.getId(), newUnit.getUnitName()
        );
    }

    @Override
    public AdUnitResponse updateUnit(AdUnitRequest request) throws AdException {
        if (request.getId() == null) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdUnit> adUnitOptional = unitRepository.findById(request.getId());
        if (!adUnitOptional.isPresent()) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        AdUnit adUnit = adUnitOptional.get();
        if (request.getUnitName() != null) {
            String newUnitName = request.getUnitName();
            Long planId = adUnit.getPlanId();
            // make sure no conflict unit name under the same ad plan
            AdUnit conflictAdUnit = unitRepository.findByPlanIdAndUnitName(
                    planId, newUnitName
            );
            if (conflictAdUnit != null) {
                throw new AdException(Consts.ErrorMsg.UNIT_NAME_EXISTS_ERROR);
            }

            adUnit.setUnitName(request.getUnitName());
        }

        if (request.getBudget() != null && request.getBudget() >= 0) {
            adUnit.setBudget(request.getBudget());
        }

        if (request.getPositionType() != null) {
            adUnit.setPositionType(request.getPositionType());
        }

        unitRepository.save(adUnit);
        return new AdUnitResponse(
                adUnit.getId(), adUnit.getUnitName()
        );
    }

    @Override
    public void deleteUnit(AdUnitRequest request) throws AdException {
        if (request.getId() == null) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdUnit> adUnitOptional = unitRepository.findById(request.getId());
        if (!adUnitOptional.isPresent()) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        AdUnit adUnit = adUnitOptional.get();
        adUnit.setUnitStatus(CommonStatus.INVALID.getStatus());
        adUnit.setUpdateTime(new Date());
        unitRepository.save(adUnit);
    }

    @Override
    public AdUnitKeywordResponse createUnitKeyword(
            AdUnitKeywordRequest request) throws AdException {

        List<Long> unitIds = request.getUnitKeyWords().stream()
                .map(AdUnitKeywordRequest.UnitKeyWord::getUnitId)
                .collect(Collectors.toList());

        if (!isRelatedUnitExists(unitIds)) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<Long> ids = Collections.emptyList();
        List<AdUnitKeyword> unitKeywords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(request.getUnitKeyWords())) {
            request.getUnitKeyWords().forEach(i ->
                    unitKeywords.add(
                            new AdUnitKeyword(i.getUnitId(), i.getKeyword())
                    ));
            ids = unitKeywordRepository.saveAll(unitKeywords).stream()
                    .map(AdUnitKeyword::getId)
                    .collect(Collectors.toList());
        }

        return new AdUnitKeywordResponse(ids);
    }

    @Override
    public AdUnitInterestResponse createUnitInterest(
            AdUnitInterestRequest request) throws AdException {
        List<Long> unitIds = request.getUnitInterests().stream()
                .map(AdUnitInterestRequest.UnitInterest::getUnitId)
                .collect(Collectors.toList());

        if (!isRelatedUnitExists(unitIds)) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<Long> ids = Collections.emptyList();
        List<AdUnitInterest> unitInterests = new ArrayList<>();
        if (!CollectionUtils.isEmpty(request.getUnitInterests())) {
            request.getUnitInterests().forEach(i ->
                    unitInterests.add(
                            new AdUnitInterest(i.getUnitId(), i.getInterestTag())
                    ));
            ids = unitInterestRepository.saveAll(unitInterests).stream()
                    .map(AdUnitInterest::getId)
                    .collect(Collectors.toList());
        }

        return new AdUnitInterestResponse(ids);
    }

    @Override
    public AdUnitDistrictResponse createUnitDistrict(
            AdUnitDistrictRequest request) throws AdException {
        List<Long> unitIds = request.getUnitDistricts().stream()
                .map(AdUnitDistrictRequest.UnitDistrict::getUnitId)
                .collect(Collectors.toList());

        if (!isRelatedUnitExists(unitIds)) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<Long> ids = Collections.emptyList();
        List<AdUnitDistrict> unitDistricts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(request.getUnitDistricts())) {
            request.getUnitDistricts().forEach(i ->
                    unitDistricts.add(
                            new AdUnitDistrict(
                                    i.getUnitId(),
                                    i.getCountry(),
                                    i.getState(),
                                    i.getCity()
                            )
                    ));
            ids = unitDistrictRepository.saveAll(unitDistricts).stream()
                    .map(AdUnitDistrict::getId)
                    .collect(Collectors.toList());
        }

        return new AdUnitDistrictResponse(ids);
    }

    @Override
    public CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException {
        List<Long> unitIds = new ArrayList<>();
        List<Long> creativeIds = new ArrayList<>();
        request.getCreativeUnitItems().stream()
                .forEach(item -> {
                    unitIds.add(item.getUnitId());
                    creativeIds.add(item.getCreativeId());
                });
        if (!isRelatedCreativeExists(creativeIds) || !isRelatedUnitExists(unitIds)){
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<CreativeUnit> creativeUnits = new ArrayList<>();
        request.getCreativeUnitItems().forEach(i -> creativeUnits.add(
                new CreativeUnit(i.getCreativeId(), i.getUnitId())
        ));

        List<Long> ids = creativeUnitRepository.saveAll(creativeUnits).stream()
                .map(CreativeUnit::getCreativeId)
                .collect(Collectors.toList());

        return new CreativeUnitResponse(ids);
    }

    private boolean isRelatedUnitExists(List<Long> unitIds) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return false;
        }

        return unitRepository.findAllById(unitIds).size() == new HashSet<>(unitIds).size();
    }

    private boolean isRelatedCreativeExists(List<Long> creativeIds) {
        if (CollectionUtils.isEmpty(creativeIds)) return false;

        return creativeRepository.findAllById(creativeIds).size() == new HashSet<>(creativeIds).size();
    }

}
