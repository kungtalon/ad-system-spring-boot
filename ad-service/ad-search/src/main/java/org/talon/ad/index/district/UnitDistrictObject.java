package org.talon.ad.index.district;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.table.AdUnitDistrictTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDistrictObject {
    private Long unitId;
    private String country;
    private String state;
    private String city;

    public UnitDistrictObject(AdUnitDistrictTable t) {
        unitId = t.getUnitId();
        country = t.getCountry();
        state = t.getState();
        city = t.getCity();
    }
}
