package org.talon.ad.handler;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.talon.ad.dump.table.*;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.IndexAware;
import org.talon.ad.index.adplan.AdPlanIndex;
import org.talon.ad.index.adplan.AdPlanIndexRedis;
import org.talon.ad.index.adplan.AdPlanObject;
import org.talon.ad.index.adunit.AdUnitIndex;
import org.talon.ad.index.adunit.AdUnitIndexRedis;
import org.talon.ad.index.adunit.AdUnitObject;
import org.talon.ad.index.creative.CreativeIndex;
import org.talon.ad.index.creative.CreativeIndexRedis;
import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.index.creativeunit.CreativeUnitIndex;
import org.talon.ad.index.creativeunit.CreativeUnitIndexRedis;
import org.talon.ad.index.creativeunit.CreativeUnitObject;
import org.talon.ad.index.district.UnitDistrictObject;
import org.talon.ad.index.interest.UnitInterestObject;
import org.talon.ad.index.keyword.UnitKeywordIndex;
import org.talon.ad.index.keyword.UnitKeywordIndexRedis;
import org.talon.ad.index.keyword.UnitKeywordObject;
import org.talon.ad.mysql.OpType;
import org.talon.ad.utils.CommonUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zelong
 * On 2022/5/3
 * 1. The indexes should be divided into different levels,
 *   some indexes rely on others.
 *
 **/
@Slf4j
public class AdLevelDataHandlerRedis {

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public static void handleLevel2(AdPlanTable planTable, OpType op) {
        AdPlanObject planObject = new AdPlanObject(planTable);
        handleBinlogEvent(
                DataTable.of(AdPlanIndexRedis.class),
                planObject.getPlanId(),
                planObject,
                op
        );

    }

    public static void handleLevel2(AdCreativeTable creativeTable, OpType op) {
        CreativeObject creativeObject = new CreativeObject(creativeTable);
        handleBinlogEvent(
                DataTable.of(CreativeIndexRedis.class),
                creativeObject.getId(),
                creativeObject,
                op
        );
    }

    public static void handleLevel3(AdUnitTable unitTable, OpType op) {
        AdPlanObject planObject = DataTable.of(AdPlanIndexRedis.class).get(unitTable.getPlanId());
        if (planObject == null) {
            log.error("handleLevel3 found AdPlanObject non-existent error: {}",
                    unitTable.getPlanId());
            return;
        }

        AdUnitObject unitObject = new AdUnitObject(unitTable);
        unitObject.setPlanObject(planObject);
        handleBinlogEvent(
                DataTable.of(AdUnitIndexRedis.class),
                unitObject.getUnitId(),
                unitObject,
                op
        );
    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable, OpType op) {
        if (op == OpType.UPDATE) {
            log.error("CreativeUnitIndex does not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndexRedis.class).get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(CreativeIndexRedis.class).get(creativeUnitTable.getCreativeId());
        if (unitObject == null || creativeObject == null) {
            log.error("handleLevel3 found creativeunit index error: {}",
                    gson.toJson(creativeUnitTable));
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(creativeUnitTable);
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndexRedis.class),
                CommonUtils.concat(
                        creativeUnitObject.getUnitId().toString(),
                        creativeUnitObject.getCreativeId().toString()
                ),
                creativeUnitObject,
                op
        );
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable, OpType op) {
        if (op == OpType.UPDATE) {
            log.error("UnitKeyword does not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndexRedis.class).get(keywordTable.getUnitId());
        if (unitObject == null) {
            log.error("handleLevel4 found unit not exist: {}",
                    keywordTable.getUnitId());
            return;
        }

        UnitKeywordObject unitKeywordObject = new UnitKeywordObject(keywordTable);
        Set<Long> value = new HashSet<>(Collections.singleton(unitKeywordObject.getUnitId()));
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndexRedis.class),
                unitKeywordObject.getKeyword(),
                value,
                op
        );

    }

    public static void handleLevel4(AdUnitInterestTable interestTable, OpType op) {
        if (op == OpType.UPDATE) {
            log.error("InterestIndex does not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndexRedis.class).get(interestTable.getUnitId());
        if (unitObject == null) {
            log.error("handleLevel4 found unit not exist: {}",
                    interestTable.getUnitId());
            return;
        }

        UnitInterestObject unitInterestObject = new UnitInterestObject(interestTable);
        Set<Long> value = new HashSet<>(Collections.singleton(unitInterestObject.getUnitId()));
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndexRedis.class),
                unitInterestObject.getInterestTag(),
                value,
                op
        );

    }

    public static void handleLevel4(AdUnitDistrictTable districtTable, OpType op) {
        if (op == OpType.UPDATE) {
            log.error("DistrictIndex does not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndexRedis.class).get(districtTable.getUnitId());
        if (unitObject == null) {
            log.error("handleLevel4 found unit not exist: {}",
                    districtTable.getUnitId());
            return;
        }

        UnitDistrictObject unitDistrictObject = new UnitDistrictObject(districtTable);
        Set<Long> value = new HashSet<>(Collections.singleton(unitDistrictObject.getUnitId()));
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndexRedis.class),
                CommonUtils.concat(
                        unitDistrictObject.getCountry(),
                        unitDistrictObject.getState(),
                        unitDistrictObject.getCity()
                ),
                value,
                op
        );

    }

    private static <K, V> void handleBinlogEvent(
            IndexAware<K, V> index,
            K key,
            V value,
            OpType type
    ) {
        log.info("Handling new binlog event: key: {}, value: {}, type: {}",
                gson.toJson(key),
                gson.toJson(value),
                type);
        switch (type) {
            case ADD:
                index.add(key,value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            default:
                break;
        }
    }

}
