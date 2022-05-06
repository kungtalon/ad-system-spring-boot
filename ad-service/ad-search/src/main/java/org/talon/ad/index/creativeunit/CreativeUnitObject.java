package org.talon.ad.index.creativeunit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.table.AdCreativeUnitTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreativeUnitObject {

    private Long creativeId;
    private Long unitId;
    // use concatted string creativeId-unitId as key

    public CreativeUnitObject(AdCreativeUnitTable t) {
        creativeId = t.getCreativeId();
        unitId = t.getUnitId();
    }
}
