package org.talon.ad.index.district;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;
import org.talon.ad.utils.CommonUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Zelong
 * On 2022/5/3
 **/

@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {
    private static Map<String, Set<Long>> districtUnitMap;
    private static Map<Long, Set<String>> unitDistrictMap;


    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }

        Set<Long> result = districtUnitMap.get(key);
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("before add : {}", unitDistrictMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(
                key, districtUnitMap, ConcurrentSkipListSet::new
        );
        unitIdSet.addAll(value);

        for(Long unitId : value) {
            Set<String> districts = CommonUtils.getOrCreate(
                    unitId, unitDistrictMap, ConcurrentSkipListSet::new
            );
            districts.add(key);
        }

        log.info("after add : {}", unitDistrictMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.info("district index do not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("before delete : {}", unitDistrictMap);
        Set<Long> unitIds = CommonUtils.getOrCreate(
                key, districtUnitMap, ConcurrentSkipListSet::new
        );
        unitIds.removeAll(value);

        for (Long unitId : value) {
            Set<String> districts = CommonUtils.getOrCreate(
                    unitId, unitDistrictMap, ConcurrentSkipListSet::new
            );
            districts.remove(key);
        }

        log.info("after delete : {}", unitDistrictMap);
    }

    public boolean match(Long unitId, List<String> districts) {
        // check if a unit has all the keywords required
        if (unitDistrictMap.containsKey(unitId) &&
                !CollectionUtils.isEmpty(unitDistrictMap.get(unitId))){
            Set<String> unitDistricts = unitDistrictMap.get(unitId);

            return CollectionUtils.isSubCollection(districts, unitDistricts);
        }

        return false;
    }

}
