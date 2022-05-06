package org.talon.ad.index.adplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.client.vo.AdPlan;
import org.talon.ad.dump.table.AdPlanTable;

import java.util.Date;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdPlanObject {

    private Long planId;
    private Long userId;
    private Integer planStatus;
    private Date startDate;
    private Date endDate;

    public void update(AdPlanObject newObject) {

        if (null != newObject.getPlanId()) {
            this.planId = newObject.getPlanId();
        }
        if (null != newObject.getUserId()) {
            this.userId = newObject.getUserId();
        }
        if (null != newObject.getPlanStatus()) {
            this.planStatus = newObject.getPlanStatus();
        }
        if (null != newObject.getStartDate()) {
            this.startDate = newObject.getStartDate();
        }
        if (null != newObject.getEndDate()) {
            this.endDate = newObject.getEndDate();
        }
    }

    public AdPlanObject(AdPlanTable t) {
        planId = t.getId();
        userId = t.getUserId();
        planStatus = t.getPlanStatus();
        startDate = t.getStartDate();
        endDate = t.getStartDate();
    }
}
