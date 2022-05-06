package org.talon.ad.repository.unitcondition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talon.ad.datamodel.unitcondition.AdUnitDistrict;
import org.talon.ad.datamodel.unitcondition.CreativeUnit;

public interface ICreativeUnitRepository extends JpaRepository<CreativeUnit, Long> {
}
