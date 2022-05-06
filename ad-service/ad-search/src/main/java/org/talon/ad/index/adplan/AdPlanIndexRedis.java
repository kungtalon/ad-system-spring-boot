package org.talon.ad.index.adplan;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class AdPlanIndexRedis implements IndexAware<Long, AdPlanObject> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_PREFIX = "adplan:";

    @Override
    public AdPlanObject get(Long key) {
        Gson gson = new Gson();
        Object o = redisTemplate.opsForValue().get(REDIS_PREFIX + key);
        log.info("Get the object in AdPlanIndexRedis -> {}", gson.toJson(o));
        return (AdPlanObject) o;
    }

    @Override
    public void add(Long key, AdPlanObject value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_PREFIX + key))) {
            log.info("key already exists!");
            return;
        }
        redisTemplate.opsForValue().set(REDIS_PREFIX + key, value);
    }

    @Override
    public void update(Long key, AdPlanObject value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        AdPlanObject oldObject = (AdPlanObject) valueOperations.get(REDIS_PREFIX + key);
        if (null == oldObject) {
            valueOperations.set(REDIS_PREFIX + key, value);
        } else {
            oldObject.update(value);
            valueOperations.set(REDIS_PREFIX + key, oldObject);
        }
    }

    @Override
    public void delete(Long key, AdPlanObject value) {
        redisTemplate.delete(REDIS_PREFIX + key);
    }
}
