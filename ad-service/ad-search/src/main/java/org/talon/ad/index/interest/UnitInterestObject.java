package org.talon.ad.index.interest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.table.AdUnitInterestTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitInterestObject {

    private Long unitId;
    private String interestTag;

    public UnitInterestObject(AdUnitInterestTable t) {
        unitId = t.getUnitId();
        interestTag = t.getInterestTag();
    }
}
