package org.talon.ad.index.embedding;

import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.talon.ad.index.IndexAware;
import org.talon.ad.index.embedding.Embedding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Slf4j
@Component
public class CreativeEmbeddingIndexRedis implements IndexAware<Long, Embedding> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_PREFIX = "creative_embedding:";

    @Override
    public Embedding get(Long key) {
        return (Embedding) redisTemplate.opsForValue().get(REDIS_PREFIX + key);
    }

    @Override
    public void add(Long key, Embedding value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_PREFIX + key))) {
            log.info("key already exists!");
            return;
        }
        redisTemplate.opsForValue().set(REDIS_PREFIX + key, value);
    }

    @Override
    public void update(Long key, Embedding value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(REDIS_PREFIX + key, value);
    }

    @Override
    public void delete(Long key, Embedding value) {
        redisTemplate.delete(REDIS_PREFIX + key);
    }

    public List<Embedding> retrieveByIds(Collection<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }

        List<Embedding> result = new ArrayList<>();

        ids.forEach(u -> {
            Embedding e = get(u);
            if (null == e) {
                log.error("CreativeObject not found in retrieveByIds : {}", u);
                return ;
            }
            result.add(e);
        });
        return result;
    }
}
