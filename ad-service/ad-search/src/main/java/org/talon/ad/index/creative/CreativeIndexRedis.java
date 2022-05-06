package org.talon.ad.index.creative;

import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.talon.ad.index.IndexAware;
import org.talon.ad.index.adplan.AdPlanObject;
import org.talon.ad.index.adunit.AdUnitObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class CreativeIndexRedis implements IndexAware<Long, CreativeObject> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_PREFIX = "creative:";

    @Override
    public CreativeObject get(Long key) {
        return (CreativeObject) redisTemplate.opsForValue().get(REDIS_PREFIX + key);
    }

    @Override
    public void add(Long key, CreativeObject value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_PREFIX + key))) {
            log.info("key already exists!");
            return;
        }
        redisTemplate.opsForValue().set(REDIS_PREFIX + key, value);
    }

    @Override
    public void update(Long key, CreativeObject value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        CreativeObject oldObject = (CreativeObject) valueOperations.get(REDIS_PREFIX + key);
        if (null == oldObject) {
            valueOperations.set(REDIS_PREFIX + key, value);
        } else {
            oldObject.update(value);
            valueOperations.set(REDIS_PREFIX + key, oldObject);
        }
    }

    @Override
    public void delete(Long key, CreativeObject value) {
        redisTemplate.delete(REDIS_PREFIX + key);
    }

    public List<CreativeObject> retrieveByIds(Collection<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }

        List<CreativeObject> result = new ArrayList<>();

        ids.forEach(u -> {
            CreativeObject object = get(u);
            if (null == object) {
                log.error("CreativeObject not found in retrieveByIds : {}", u);
                return ;
            }
            result.add(object);
        });
        return result;
    }
}
