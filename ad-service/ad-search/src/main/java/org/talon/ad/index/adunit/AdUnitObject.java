package org.talon.ad.index.adunit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.constant.BusinessConsts;
import org.talon.ad.dump.table.AdUnitTable;
import org.talon.ad.index.adplan.AdPlanObject;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitObject {

    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;

    private AdPlanObject planObject;

    void update(AdUnitObject newObject) {
        if (null != newObject.getUnitId()) {
            this.unitId = newObject.getUnitId();
        }
        if (null != newObject.getUnitStatus()) {
            this.unitStatus = newObject.getUnitStatus();
        }
        if (null != newObject.getPositionType()) {
            this.positionType = newObject.getPositionType();
        }
        if (null != newObject.getPlanId()) {
            this.planId = newObject.getPlanId();
        }
        if (null != newObject.planObject) {
            this.planObject = newObject.planObject;
        }
    }

    public AdUnitObject(AdUnitTable t) {
        this.unitId = t.getUnitId();
        this.unitStatus = t.getUnitStatus();
        this.positionType = t.getPositionType();
        this.planId = t.getPlanId();
    }

    private static boolean isFullScreen(int positionType) {
        return (positionType & BusinessConsts.PositionType.FULLSCREEN) > 0;
    }

    private static boolean isPreRoll(int positionType) {
        return (positionType & BusinessConsts.PositionType.PREROLL) > 0;
    }

    private static boolean isMidRoll(int positionType) {
        return (positionType & BusinessConsts.PositionType.MIDROLL) > 0;
    }

    private static boolean isPostRoll(int positionType) {
        return (positionType & BusinessConsts.PositionType.POSTROLL) > 0;
    }

    private static boolean isFeeds(int positionType) {
        return (positionType & BusinessConsts.PositionType.FEEDS) > 0;
    }

    public static boolean checkAdSlotSupportedType(int adSlotType, int positionType) {
        switch (adSlotType) {
            case BusinessConsts.PositionType.FULLSCREEN:
                return isFullScreen(positionType);
            case BusinessConsts.PositionType.PREROLL:
                return isPreRoll(positionType);
            case BusinessConsts.PositionType.MIDROLL:
                return isMidRoll(positionType);
            case BusinessConsts.PositionType.POSTROLL:
                return isPostRoll(positionType);
            case BusinessConsts.PositionType.FEEDS:
                return isFeeds(positionType);
            default:
                return false;
        }
    }
}
