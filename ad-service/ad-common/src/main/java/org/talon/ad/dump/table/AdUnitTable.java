package org.talon.ad.dump.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.AdTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitTable extends AdTable {

    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;

}
