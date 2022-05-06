package org.talon.ad.vo.unitcondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.datamodel.unitcondition.AdUnitInterest;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitInterestRequest {
    private List<UnitInterest> unitInterests;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitInterest {
        private Long unitId;
        private String interestTag;
    }

}
