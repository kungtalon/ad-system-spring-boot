package org.talon.ad.dump.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.AdTable;

import java.util.Date;

/**
 * Created by Zelong
 * On 2022/5/3
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanTable extends AdTable {
    private Long id;
    private Long userId;
    private Integer planStatus;
    private Date startDate;
    private Date endDate;
}
