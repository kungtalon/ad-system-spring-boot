package org.talon.ad.index.creativeunit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;
import org.talon.ad.index.adunit.AdUnitObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class CreativeUnitIndexRedis implements IndexAware<String, CreativeUnitObject> {

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_PREFIX = "creativeunit:";
    private static final String CREATIVE_UNIT_PREFIX = "creative_unit_set:";
    private static final String UNIT_CREATIVE_PREFIX = "unit_creative_set:";

    @Override
    public CreativeUnitObject get(String key) {
        return (CreativeUnitObject) objectRedisTemplate.opsForValue().get(REDIS_PREFIX + key);
    }

    @SuppressWarnings("all")
    @Override
    public void add(String key, CreativeUnitObject value) {
        if(objectRedisTemplate.hasKey(REDIS_PREFIX + key)) {
            log.info("key already exists!");
            return ;
        }

        // add to objectRedis
        objectRedisTemplate.opsForValue().set(REDIS_PREFIX + key, value);

        // add to creativeUnitRedis
        stringRedisTemplate.opsForSet().
                add(CREATIVE_UNIT_PREFIX + value.getCreativeId(), value.getUnitId().toString());

        // add to unitCreativeRedis
        stringRedisTemplate.opsForSet().
                add(UNIT_CREATIVE_PREFIX + value.getUnitId(), value.getCreativeId().toString());

    }

    @Override
    public void update(String key, CreativeUnitObject value) {
        log.info("CreativeUnitIndex does not support update");
    }

    @Override
    public void delete(String key, CreativeUnitObject value) {

        // delete from objectRedis
        objectRedisTemplate.delete(REDIS_PREFIX + key);

        // delete from creativeUnitRedis
        stringRedisTemplate.opsForSet().remove(CREATIVE_UNIT_PREFIX + value.getCreativeId(), value.getUnitId().toString());

        // delete from unitCreativeRedis
        stringRedisTemplate.opsForSet().remove(UNIT_CREATIVE_PREFIX + value.getUnitId(), value.getCreativeId().toString());
    }

    public List<Long> getCreativeIdsByAdUnits(List<AdUnitObject> unitObjects) {
        if (CollectionUtils.isEmpty(unitObjects)) {
            return Collections.emptyList();
        }

        Set<Long> result = new HashSet<>();

        for (AdUnitObject unitObject: unitObjects) {
            Set<String> creativeIds = stringRedisTemplate.opsForSet()
                    .members(UNIT_CREATIVE_PREFIX + unitObject.getUnitId());
            if (CollectionUtils.isNotEmpty(creativeIds)){
                result.addAll(creativeIds.stream()
                        .map(Long::valueOf).collect(Collectors.toSet())
                );
            }
        }
        return new ArrayList<>(result);
    }
}
