package org.talon.ad.vo.unitcondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitKeywordRequest {

    private List<UnitKeyWord> unitKeyWords;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitKeyWord {
        private Long unitId;
        private String keyword;
    }

}
