package org.talon.ad.service;

import org.talon.ad.exception.AdException;
import org.talon.ad.vo.AdUnitRequest;
import org.talon.ad.vo.AdUnitResponse;
import org.talon.ad.vo.unitcondition.*;

public interface IAdUnitService {

    AdUnitResponse createUnit(AdUnitRequest request) throws AdException;

    AdUnitResponse updateUnit(AdUnitRequest request) throws AdException;

    void deleteUnit(AdUnitRequest request) throws AdException;

    AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request) throws AdException;

    AdUnitInterestResponse createUnitInterest(AdUnitInterestRequest request) throws AdException;

    AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request) throws AdException;

    CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException;

}
