package org.talon.ad.index;

import com.google.gson.Gson;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.talon.ad.dump.DumpConstant;
import org.talon.ad.dump.table.*;
import org.talon.ad.handler.AdLevelDataHandler;
import org.talon.ad.handler.AdLevelDataHandlerRedis;
import org.talon.ad.mysql.OpType;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Component
@DependsOn("dataTable")
public class IndexFileLoader {

    @PostConstruct
    public void init() {
        Gson gson = new Gson();

        List<String> adPlanStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_PLAN
        ));
        adPlanStrings.forEach(p-> AdLevelDataHandlerRedis.handleLevel2(
            gson.fromJson(p, AdPlanTable.class), OpType.ADD
        ));

        List<String> adCreativeStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_CREATIVE
        ));
        adCreativeStrings.forEach(p-> AdLevelDataHandlerRedis.handleLevel2(
                gson.fromJson(p, AdCreativeTable.class), OpType.ADD
        ));

        List<String> adUnitStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_UNIT
        ));
        adUnitStrings.forEach(u -> AdLevelDataHandlerRedis.handleLevel3(
                gson.fromJson(u, AdUnitTable.class), OpType.ADD
        ));

        List<String> creativeUnitStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_CREATIVE_UNIT
        ));
        creativeUnitStrings.forEach(u -> AdLevelDataHandlerRedis.handleLevel3(
                gson.fromJson(u, AdCreativeUnitTable.class), OpType.ADD
        ));

        List<String> unitDistrictStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_UNIT_DISTRICT
        ));
        unitDistrictStrings.forEach(u -> AdLevelDataHandlerRedis.handleLevel4(
                gson.fromJson(u, AdUnitDistrictTable.class), OpType.ADD
        ));

        List<String> unitInterestStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_UNIT_INTEREST
        ));
        unitInterestStrings.forEach(u -> AdLevelDataHandlerRedis.handleLevel4(
                gson.fromJson(u, AdUnitInterestTable.class), OpType.ADD
        ));

        List<String> unitKeywordStrings = loadDumpData(String.format(
                "%s%s", DumpConstant.DATA_ROOT_DIR, DumpConstant.AD_UNIT_KEYWORD
        ));
        unitKeywordStrings.forEach(u -> AdLevelDataHandlerRedis.handleLevel4(
                gson.fromJson(u, AdUnitKeywordTable.class), OpType.ADD
        ));

    }

    private List<String> loadDumpData(String fileName) throws RuntimeException {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
