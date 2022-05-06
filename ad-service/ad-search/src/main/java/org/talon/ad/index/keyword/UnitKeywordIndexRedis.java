package org.talon.ad.index.keyword;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.talon.ad.index.IndexAware;
import org.talon.ad.utils.CommonUtils;

import javax.annotation.Resource;
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
public class UnitKeywordIndexRedis implements IndexAware<String, Set<Long>> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEYWORD_UNIT_PREFIX = "keyword_unit_set:";
    private static final String UNIT_KEYWORD_PREFIX = "unit_keyword_set:";

    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }

        Set<String> result = redisTemplate.opsForSet().members(KEYWORD_UNIT_PREFIX + key);
        if (result == null) {
            return Collections.emptySet();
        }

        return result.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    @Override
    public void add(String key, Set<Long> value) {
        List<String> valueString = new ArrayList<>();
        value.forEach(l -> valueString.add(l.toString()));
        redisTemplate.opsForSet().add(KEYWORD_UNIT_PREFIX + key, valueString.toArray(new String[0]));

        for(Long unitId : value) {
            redisTemplate.opsForSet().add(UNIT_KEYWORD_PREFIX + unitId, key);
        }

    }

    @Override
    public void update(String key, Set<Long> value) {
        log.info("keyword index do not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        redisTemplate.opsForSet().remove(KEYWORD_UNIT_PREFIX + key, value.stream().map(String::valueOf).collect(Collectors.toList()));

        for (Long unitId : value) {
            redisTemplate.opsForSet().remove(UNIT_KEYWORD_PREFIX + unitId, key);
        }
    }

    public boolean match(Long unitId, List<String> keywords) {
        // check if a unit has all the keywords required
        if (redisTemplate.hasKey(UNIT_KEYWORD_PREFIX + unitId) &&
                redisTemplate.opsForSet().size(UNIT_KEYWORD_PREFIX + unitId) > 0){
            Set<String> unitKeywords = redisTemplate.opsForSet().members(UNIT_KEYWORD_PREFIX + unitId);

            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }

        return false;
    }
}
