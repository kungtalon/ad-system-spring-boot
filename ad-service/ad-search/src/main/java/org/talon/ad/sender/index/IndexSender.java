package org.talon.ad.sender.index;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.talon.ad.client.vo.AdPlan;
import org.talon.ad.dump.table.*;
import org.talon.ad.handler.AdLevelDataHandlerRedis;
import org.talon.ad.index.DataLevel;
import org.talon.ad.mysql.Consts;
import org.talon.ad.mysql.dto.IncreRowData;
import org.talon.ad.sender.ISender;
import org.talon.ad.utils.CommonUtils;
import org.talon.ad.utils.ParseStringDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class IndexSender implements ISender {

    private static final Gson gson = new Gson();

    @Override
    public void send(IncreRowData rowData) {
        String level = rowData.getLevel();

        if (DataLevel.LEVEL2.getLevel().equalsIgnoreCase(level)) {
            log.info("Get LEVEL2 increRowData: {}", gson.toJson(rowData));
            sendLevel2RowData(rowData);
        } else if (DataLevel.LEVEL3.getLevel().equalsIgnoreCase(level)) {
            log.info("Get LEVEL3 increRowData: {}", gson.toJson(rowData));
            sendLevel3RowData(rowData);
        } else if (DataLevel.LEVEL4.getLevel().equalsIgnoreCase(level)) {
            log.info("Get LEVEL4 increRowData: {}", gson.toJson(rowData));
            sendLevel4RowData(rowData);
        } else {
            log.error("IncreRowData ERROR : {}", gson.toJson(rowData));
        }
    }

    private void sendLevel2RowData(IncreRowData rowData) {

        if (rowData.getTableName().equals(
                Consts.AD_PLAN_TABLE_INFO.TABLE_NAME)) {
            List<AdPlanTable> planTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getNewValueMap()) {

                AdPlanTable planTable = new AdPlanTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Consts.AD_PLAN_TABLE_INFO.COLUMN_ID:
                            planTable.setId(Long.valueOf(v));
                            break;
                        case Consts.AD_PLAN_TABLE_INFO.COLUMN_USER_ID:
                            planTable.setUserId(Long.valueOf(v));
                            break;
                        case Consts.AD_PLAN_TABLE_INFO.COLUMN_PLAN_STATUS:
                            planTable.setPlanStatus(Integer.valueOf(v));
                            break;
                        case Consts.AD_PLAN_TABLE_INFO.COLUMN_START_DATE:
                            planTable.setStartDate(
                                    ParseStringDate.parseBinlogDate(v)
                            );
                            break;
                        case Consts.AD_PLAN_TABLE_INFO.COLUMN_END_DATE:
                            planTable.setEndDate(
                                    ParseStringDate.parseBinlogDate(v)
                            );
                            break;
                    }
                });

                planTables.add(planTable);
            }

            planTables.forEach(p ->
                    AdLevelDataHandlerRedis.handleLevel2(p, rowData.getOp()));
        } else if (rowData.getTableName().equals(
                Consts.AD_CREATIVE_TABLE_INFO.TABLE_NAME
        )) {
            List<AdCreativeTable> creativeTables = new ArrayList<>();

            for (Map<String, String> fieldValeMap :
                    rowData.getNewValueMap()) {

                AdCreativeTable creativeTable = new AdCreativeTable();

                fieldValeMap.forEach((k, v) -> {
                    switch (k) {
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_ID:
                            creativeTable.setCreativeId(Long.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_TYPE:
                            creativeTable.setType(Integer.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_FORMAT:
                            creativeTable.setFormat(Integer.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_HEIGHT:
                            creativeTable.setHeight(Integer.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_WIDTH:
                            creativeTable.setWidth(Integer.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_AUDIT_STATUS:
                            creativeTable.setAuditStatus(Integer.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_TABLE_INFO.COLUMN_URL:
                            creativeTable.setUrl(v);
                            break;
                    }
                });

                creativeTables.add(creativeTable);
            }

            creativeTables.forEach(c ->
                    AdLevelDataHandlerRedis.handleLevel2(c, rowData.getOp()));
        }
    }

    private void sendLevel3RowData(IncreRowData rowData) {

        if (rowData.getTableName().equals(
                Consts.AD_UNIT_TABLE_INFO.TABLE_NAME)) {

            List<AdUnitTable> unitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getNewValueMap()) {

                AdUnitTable unitTable = new AdUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Consts.AD_UNIT_TABLE_INFO.COLUMN_ID:
                            unitTable.setUnitId(Long.valueOf(v));
                            break;
                        case Consts.AD_UNIT_TABLE_INFO.COLUMN_UNIT_STATUS:
                            unitTable.setUnitStatus(Integer.valueOf(v));
                            break;
                        case Consts.AD_UNIT_TABLE_INFO.COLUMN_POSITION_TYPE:
                            unitTable.setPositionType(Integer.valueOf(v));
                            break;
                        case Consts.AD_UNIT_TABLE_INFO.COLUMN_PLAN_ID:
                            unitTable.setPlanId(Long.valueOf(v));
                            break;
                    }
                });

                unitTables.add(unitTable);
            }

            unitTables.forEach(u ->
                    AdLevelDataHandlerRedis.handleLevel3(u, rowData.getOp()));
        } else if (rowData.getTableName().equals(
                Consts.AD_CREATIVE_UNIT_TABLE_INFO.TABLE_NAME
        )) {
            List<AdCreativeUnitTable> creativeUnitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getNewValueMap()) {

                AdCreativeUnitTable creativeUnitTable = new AdCreativeUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Consts.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_CREATIVE_ID:
                            creativeUnitTable.setCreativeId(Long.valueOf(v));
                            break;
                        case Consts.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_UNIT_ID:
                            creativeUnitTable.setUnitId(Long.valueOf(v));
                            break;
                    }
                });

                creativeUnitTables.add(creativeUnitTable);
            }

            creativeUnitTables.forEach(
                    u -> AdLevelDataHandlerRedis.handleLevel3(u, rowData.getOp())
            );
        }
    }

    private void sendLevel4RowData(IncreRowData rowData) {

        switch (rowData.getTableName()) {

            case Consts.AD_UNIT_DISTRICT_TABLE_INFO.TABLE_NAME:
                List<AdUnitDistrictTable> districtTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getNewValueMap()) {

                    AdUnitDistrictTable districtTable = new AdUnitDistrictTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Consts.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_UNIT_ID:
                                districtTable.setUnitId(Long.valueOf(v));
                                break;
                            case Consts.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_COUNTRY:
                                districtTable.setCountry(v);
                                break;
                            case Consts.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_STATE:
                                districtTable.setState(v);
                                break;
                            case Consts.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_CITY:
                                districtTable.setCity(v);
                                break;
                        }
                    });

                    districtTables.add(districtTable);
                }

                districtTables.forEach(
                        d -> AdLevelDataHandlerRedis.handleLevel4(d, rowData.getOp())
                );
                break;
            case Consts.AD_UNIT_INTEREST_TABLE_INFO.TABLE_NAME:
                List<AdUnitInterestTable> itTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getNewValueMap()) {

                    AdUnitInterestTable itTable = new AdUnitInterestTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Consts.AD_UNIT_INTEREST_TABLE_INFO.COLUMN_UNIT_ID:
                                itTable.setUnitId(Long.valueOf(v));
                                break;
                            case Consts.AD_UNIT_INTEREST_TABLE_INFO.COLUMN_INTEREST_TAG:
                                itTable.setInterestTag(v);
                                break;
                        }
                    });
                    itTables.add(itTable);
                }
                itTables.forEach(
                        i -> AdLevelDataHandlerRedis.handleLevel4(i, rowData.getOp())
                );
                break;
            case Consts.AD_UNIT_KEYWORD_TABLE_INFO.TABLE_NAME:

                List<AdUnitKeywordTable> keywordTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getNewValueMap()) {
                    AdUnitKeywordTable keywordTable = new AdUnitKeywordTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Consts.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_UNIT_ID:
                                keywordTable.setUnitId(Long.valueOf(v));
                                break;
                            case Consts.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_KEYWORD:
                                keywordTable.setKeyword(v);
                                break;
                        }
                    });
                    keywordTables.add(keywordTable);
                }

                keywordTables.forEach(
                        k -> AdLevelDataHandlerRedis.handleLevel4(k, rowData.getOp())
                );
                break;
        }
    }
}
