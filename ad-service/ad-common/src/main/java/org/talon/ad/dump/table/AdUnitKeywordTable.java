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
public class AdUnitKeywordTable extends AdTable {
    private Long unitId;
    private String keyword;
}
