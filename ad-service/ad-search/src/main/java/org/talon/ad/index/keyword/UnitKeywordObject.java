package org.talon.ad.index.keyword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.table.AdUnitKeywordTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitKeywordObject {

    private Long unitId;
    private String keyword;

    public UnitKeywordObject(AdUnitKeywordTable t) {
        unitId = t.getUnitId();
        keyword = t.getKeyword();
    }
}
