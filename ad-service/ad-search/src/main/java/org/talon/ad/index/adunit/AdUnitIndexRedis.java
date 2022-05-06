package org.talon.ad.index.adunit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class AdUnitIndexRedis implements IndexAware<Long, AdUnitObject> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_PREFIX = "adunit:";

    @Override
    public AdUnitObject get(Long key) {
        return (AdUnitObject) redisTemplate.opsForValue().get(REDIS_PREFIX + key);
    }

    @Override
    public void add(Long key, AdUnitObject value) {
        if (redisTemplate.hasKey(REDIS_PREFIX + key)) {
            log.info("key already exists!");
            return ;
        }
        redisTemplate.opsForValue().set(REDIS_PREFIX + key, value);
    }

    @Override
    public void update(Long key, AdUnitObject value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        AdUnitObject oldObject = (AdUnitObject) valueOperations.get(REDIS_PREFIX + key);
        if (null == oldObject) {
            valueOperations.set(REDIS_PREFIX + key, value);
        } else {
            oldObject.update(value);
            valueOperations.set(REDIS_PREFIX + key, oldObject);
        }
    }

    @Override
    public void delete(Long key, AdUnitObject value) {
        redisTemplate.delete(REDIS_PREFIX + key);
    }

    public Set<Long> filterByPositionType(Integer positionType) {
        Set<Long> adUnitIds = new HashSet<>();

        redisTemplate.keys(REDIS_PREFIX + "*").forEach(k -> {
            AdUnitObject v = (AdUnitObject) redisTemplate.opsForValue().get(k);
            if (v != null && AdUnitObject.checkAdSlotSupportedType(positionType, v.getPositionType()));
            adUnitIds.add(v.getUnitId());
        });

        return adUnitIds;
    }

    public Set<Long> filterByPositionType(Collection<Long> adUnitIds, Integer positionType) {

        return adUnitIds.stream().filter(k -> {
            AdUnitObject v = (AdUnitObject) redisTemplate.opsForValue().get(REDIS_PREFIX + k);
            return v != null && AdUnitObject.checkAdSlotSupportedType(positionType, v.getPositionType());
        }).collect(Collectors.toSet());

    }

    public List<AdUnitObject> retrieveByIds(Collection<Long> adUnitIds) {
        if (CollectionUtils.isEmpty(adUnitIds)) {
            return Collections.emptyList();
        }

        List<AdUnitObject> result = new ArrayList<>();

        adUnitIds.forEach(u -> {
            AdUnitObject object = get(u);
            if (object == null) {
                log.error("AdUnitObject not found in retrieveByIds : {}", u);
                return ;
            }
            result.add(object);
        });
        return result;
    }
}
