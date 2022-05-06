package org.talon.ad.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.bouncycastle.asn1.util.Dump;
import org.bouncycastle.util.test.TestFailedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.talon.ad.Application;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.datamodel.AdEntity;
import org.talon.ad.datamodel.AdPlan;
import org.talon.ad.datamodel.AdUnit;
import org.talon.ad.datamodel.Creative;
import org.talon.ad.datamodel.unitcondition.AdUnitDistrict;
import org.talon.ad.datamodel.unitcondition.AdUnitInterest;
import org.talon.ad.datamodel.unitcondition.AdUnitKeyword;
import org.talon.ad.datamodel.unitcondition.CreativeUnit;
import org.talon.ad.dump.AdTable;
import org.talon.ad.dump.DumpConstant;
import org.talon.ad.dump.table.*;
import org.talon.ad.exception.AdException;
import org.talon.ad.repository.IAdPlanRepository;
import org.talon.ad.repository.IAdUnitRepository;
import org.talon.ad.repository.ICreativeRepository;
import org.talon.ad.repository.unitcondition.IAdUnitDistrictRepository;
import org.talon.ad.repository.unitcondition.IAdUnitInterestRepository;
import org.talon.ad.repository.unitcondition.IAdUnitKeywordRepository;
import org.talon.ad.repository.unitcondition.ICreativeUnitRepository;
import org.talon.ad.vo.unitcondition.AdUnitDistrictRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
            webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DumpDataService {
    @Autowired
    private IAdPlanRepository planRepository;
    @Autowired
    private IAdUnitRepository unitRepository;
    @Autowired
    private ICreativeRepository creativeRepository;
    @Autowired
    private ICreativeUnitRepository creativeUnitRepository;
    @Autowired
    private IAdUnitDistrictRepository unitDistrictRepository;
    @Autowired
    private IAdUnitInterestRepository unitInterestRepository;
    @Autowired
    private IAdUnitKeywordRepository unitKeywordRepository;

    private static Gson gson;

    static {
        gson = new Gson();
    }

    @Test
    public void dumpTables() throws AdException {

        DumpProcessor<AdPlan, AdPlanTable> adPlanDumper = newDumpProcessor(AdPlan.class);
        assert adPlanDumper != null;
        adPlanDumper.dump(AdPlan.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_PLAN));

        DumpProcessor<AdUnit, AdUnitTable> adUnitDumper = newDumpProcessor(AdUnit.class);
        assert adUnitDumper != null;
        adUnitDumper.dump(AdUnit.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_UNIT));

        DumpProcessor<Creative, AdCreativeTable> creativeDumper = newDumpProcessor(Creative.class);
        assert creativeDumper != null;
        creativeDumper.dump(Creative.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_CREATIVE));

        DumpProcessor<Creative, AdCreativeTable> creativeUnitDumper = newDumpProcessor(CreativeUnit.class);
        assert creativeDumper != null;
        creativeUnitDumper.dump(CreativeUnit.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_CREATIVE_UNIT));

        DumpProcessor<AdUnitDistrict, AdUnitDistrictTable> districtDumper = newDumpProcessor(AdUnitDistrict.class);
        assert districtDumper != null;
        districtDumper.dump(AdUnitDistrict.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_UNIT_DISTRICT));

        DumpProcessor<AdUnitInterest, AdUnitInterestTable> interestDumper = newDumpProcessor(AdUnitInterest.class);
        assert interestDumper != null;
        interestDumper.dump(AdUnitInterest.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_UNIT_INTEREST));

        DumpProcessor<AdUnitKeyword, AdUnitKeywordTable> keywordDumper = newDumpProcessor(AdUnitKeyword.class);
        assert keywordDumper != null;
        keywordDumper.dump(AdUnitKeyword.class, String.format("%s%s",
                DumpConstant.DATA_ROOT_DIR,
                DumpConstant.AD_UNIT_KEYWORD));

    }

    private DumpProcessor newDumpProcessor(Class cls) throws AdException {
        if (AdPlan.class.equals(cls)) {
            List<AdPlan> adPlans = planRepository.findAllByPlanStatus(
                    CommonStatus.VALID.getStatus()
            );
            List<AdPlanTable> adPlanTables = new ArrayList<>();
            return new DumpProcessor<>(adPlans, adPlanTables);
        } else if (AdUnit.class.equals(cls)) {
            List<AdUnit> adUnits = unitRepository.findAllByUnitStatus(
                    CommonStatus.VALID.getStatus()
            );
            List<AdUnitTable>  adUnitTables = new ArrayList<>();
            return new DumpProcessor<>(adUnits, adUnitTables);
        } else if (Creative.class.equals(cls)) {
            List<Creative> creatives = creativeRepository.findAll();
            List<AdCreativeTable> creativeTables = new ArrayList<>();
            return new DumpProcessor<>(creatives, creativeTables);
        } else if (CreativeUnit.class.equals(cls)) {
            List<CreativeUnit> creativeUnits = creativeUnitRepository.findAll();
            List<AdCreativeUnitTable> creativeUnitTables = new ArrayList<>();
            return new DumpProcessor<>(creativeUnits, creativeUnitTables);
        } else if (AdUnitDistrict.class.equals(cls)) {
            List<AdUnitDistrict> unitDistricts = unitDistrictRepository.findAll();
            List<AdUnitDistrictTable> unitDistrictTables = new ArrayList<>();
            return new DumpProcessor<>(unitDistricts, unitDistrictTables);
        } else if (AdUnitInterest.class.equals(cls)) {
            List<AdUnitInterest> unitInterests = unitInterestRepository.findAll();
            List<AdUnitInterestTable> unitInterestTables = new ArrayList<>();
            return new DumpProcessor<>(unitInterests, unitInterestTables);
        } else if (AdUnitKeyword.class.equals(cls)) {
            List<AdUnitKeyword> unitKeywords = unitKeywordRepository.findAll();
            List<AdUnitKeywordTable> unitKeywordTables = new ArrayList<>();
            return new DumpProcessor<>(unitKeywords, unitKeywordTables);
        }
        throw new AdException("Wrong class type! Got " + cls);
    }

    @AllArgsConstructor
    private class DumpProcessor<T extends AdEntity, Y extends AdTable> {

        private List<T> objects;
        private List<Y> objectTables;

        public void dump(Class cls, String fileName) throws AdException {
            if (CollectionUtils.isEmpty(objects)) {
                return ;
            }
            objects.forEach(o -> objectTables.add((Y) convertToTable(cls, o)));

            Path path = Paths.get(fileName);
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Y objectTable : objectTables) {
                    writer.write(gson.toJson(objectTable));
                    writer.newLine();
                }
            } catch (IOException e) {
                log.error("dump ad unit table error!");
            }
        }
    }

    private AdTable convertToTable(Class cls, AdEntity e) {
        if (AdPlan.class.equals(cls)) {
            return convertToTable((AdPlan) e);
        } else if (AdUnit.class.equals(cls)) {
            return convertToTable((AdUnit) e);
        } else if (Creative.class.equals(cls)) {
            return convertToTable((Creative) e);
        } else if (CreativeUnit.class.equals(cls)) {
            return convertToTable((CreativeUnit) e);
        } else if (AdUnitDistrict.class.equals(cls)) {
            return convertToTable((AdUnitDistrict) e);
        } else if (AdUnitInterest.class.equals(cls)) {
            return convertToTable((AdUnitInterest) e);
        } else if (AdUnitKeyword.class.equals(cls)) {
            return convertToTable((AdUnitKeyword) e);
        }
        log.error("Class type mismatch! Got " + cls);
        return null;
    }

    private AdTable convertToTable(CreativeUnit u) {
        return new AdCreativeUnitTable(
                u.getCreativeId(),
                u.getUnitId()
        );
    }

    private AdTable convertToTable(AdUnitKeyword k) {
        return new AdUnitKeywordTable(
                k.getUnitId(),
                k.getKeyword()
        );
    }

    private AdTable convertToTable(AdUnitInterest it) {
        return new AdUnitInterestTable(
                it.getUnitId(),
                it.getInterestTag()
        );
    }

    private AdTable convertToTable(AdUnitDistrict ud) {
        return new AdUnitDistrictTable(
                ud.getUnitId(),
                ud.getCountry(),
                ud.getState(),
                ud.getCity()
        );
    }

    private AdTable convertToTable(AdPlan p) {
        return new AdPlanTable(
                p.getId(),
                p.getUserId(),
                p.getPlanStatus(),
                p.getStartDate(),
                p.getEndDate()
        );
    }

    private AdTable convertToTable(AdUnit u) {
        return new AdUnitTable(
                u.getId(),
                u.getUnitStatus(),
                u.getPositionType(),
                u.getPlanId()
        );
    }

    private AdTable convertToTable(Creative c) {
        return new AdCreativeTable(
                c.getId(),
                c.getName(),
                c.getType(),
                c.getFormat(),
                c.getHeight(),
                c.getWidth(),
                c.getAuditStatus(),
                c.getUrl()
        );
    }

}
