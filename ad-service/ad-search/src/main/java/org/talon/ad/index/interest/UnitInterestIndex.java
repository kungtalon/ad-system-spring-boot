package org.talon.ad.index.interest;

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
public class UnitInterestIndex implements IndexAware<String, Set<Long>> {

    private static Map<String, Set<Long>> interestUnitMap;
    private static Map<Long, Set<String>> unitInterestMap;

    static {
        interestUnitMap = new ConcurrentHashMap<>();
        unitInterestMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }

        Set<Long> result = interestUnitMap.get(key);
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("before add : {}", unitInterestMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(
                key, interestUnitMap, ConcurrentSkipListSet::new
        );
        unitIdSet.addAll(value);

        for(Long unitId : value) {
            Set<String> interestSet = CommonUtils.getOrCreate(
                    unitId, unitInterestMap, ConcurrentSkipListSet::new
            );
            interestSet.add(key);
        }

        log.info("after add : {}", unitInterestMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.info("interest index do not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("before delete : {}", unitInterestMap);
        Set<Long> unitIds = CommonUtils.getOrCreate(
                key, interestUnitMap, ConcurrentSkipListSet::new
        );
        unitIds.removeAll(value);

        for (Long unitId : value) {
            Set<String> interestSet = CommonUtils.getOrCreate(
                    unitId, unitInterestMap, ConcurrentSkipListSet::new
            );
            interestSet.remove(key);
        }

        log.info("after delete : {}", unitInterestMap);
    }

    public boolean match(Long unitId, List<String> interestTags) {
        // check if a unit has all the keywords required
        if (unitInterestMap.containsKey(unitId) &&
                !CollectionUtils.isEmpty(unitInterestMap.get(unitId))){
            Set<String> unitInterests = unitInterestMap.get(unitId);

            return CollectionUtils.isSubCollection(interestTags, unitInterests);
        }

        return false;
    }
}
