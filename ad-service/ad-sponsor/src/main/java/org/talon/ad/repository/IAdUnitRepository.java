package org.talon.ad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talon.ad.datamodel.AdUnit;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

public interface IAdUnitRepository extends JpaRepository<AdUnit, Long> {

    AdUnit findByPlanIdAndUnitName(Long planId, String unitName);

    List<AdUnit> findAllByUnitStatus(Integer unitStatus);
}
