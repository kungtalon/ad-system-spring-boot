package org.talon.ad.index.embedding;

import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.talon.ad.index.IndexAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class QueryEmbeddingIndexRedis implements IndexAware<String, Embedding> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_PREFIX = "query_embedding:";

    @Override
    public Embedding get(String key) {
        return (Embedding) redisTemplate.opsForValue().get(REDIS_PREFIX + key);
    }

    @Override
    public void add(String key, Embedding value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_PREFIX + key))) {
            log.info("key already exists!");
            return;
        }
        redisTemplate.opsForValue().set(REDIS_PREFIX + key, value);
    }

    @Override
    public void update(String key, Embedding value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(REDIS_PREFIX + key, value);
    }

    @Override
    public void delete(String key, Embedding value) {
        redisTemplate.delete(REDIS_PREFIX + key);
    }

    public List<Embedding> retrieveByKeys(Collection<String> keys){
        if (CollectionUtils.isEmpty(keys)){
            return Collections.emptyList();
        }

        List<Embedding> result = new ArrayList<>();

        keys.forEach(u -> {
            Embedding e = get(u);
            if (null == e) {
                log.error("Embedding not found in retrieveByIds : {}", u);
                return ;
            }
            result.add(e);
        });
        return result;
    }
}
