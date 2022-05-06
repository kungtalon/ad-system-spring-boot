package org.talon.ad.index.district;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;
import org.talon.ad.utils.CommonUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/3
 **/

@Slf4j
@Component
public class UnitDistrictIndexRedis implements IndexAware<String, Set<Long>> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String DISTRICT_UNIT_PREFIX = "district_unit_set:";
    private static final String UNIT_DISTRICT_PREFIX = "unit_district_set:";


    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }

        Set<String> result = redisTemplate.opsForSet()
                .members(DISTRICT_UNIT_PREFIX + key);
        if (result == null) {
            return Collections.emptySet();
        }

        return result.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    @Override
    public void add(String key, Set<Long> value) {
        List<String> valueString = new ArrayList<>();
        value.forEach(l -> valueString.add(l.toString()));
        redisTemplate.opsForSet().add(DISTRICT_UNIT_PREFIX + key, valueString.toArray(new String[0]));

        for(Long unitId : value) {
            redisTemplate.opsForSet().add(UNIT_DISTRICT_PREFIX + unitId, key);
        }
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.info("district index do not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        redisTemplate.opsForSet().remove(DISTRICT_UNIT_PREFIX + key, value.stream().map(String::valueOf).collect(Collectors.toList()));

        for (Long unitId : value) {
            redisTemplate.opsForSet().remove(UNIT_DISTRICT_PREFIX + unitId, key);
        }
    }

    public boolean match(Long unitId, List<String> districts) {
        // check if a unit has all the keywords required
        if (redisTemplate.hasKey(UNIT_DISTRICT_PREFIX + unitId) &&
                redisTemplate.opsForSet().size(UNIT_DISTRICT_PREFIX + unitId) > 0){
            Set<String> unitDistricts = redisTemplate.opsForSet().members(UNIT_DISTRICT_PREFIX + unitId);

            return CollectionUtils.isSubCollection(districts, unitDistricts);
        }

        return false;
    }

}
