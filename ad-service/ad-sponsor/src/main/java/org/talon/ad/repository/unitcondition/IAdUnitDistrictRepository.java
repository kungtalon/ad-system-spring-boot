package org.talon.ad.repository.unitcondition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talon.ad.datamodel.unitcondition.AdUnitDistrict;
import org.talon.ad.datamodel.unitcondition.AdUnitKeyword;

public interface IAdUnitDistrictRepository extends JpaRepository<AdUnitDistrict, Long> {
}
