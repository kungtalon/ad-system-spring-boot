package org.talon.ad.search.filter;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.keyword.UnitKeywordIndexRedis;
import org.talon.ad.search.vo.feature.KeywordFeature;
import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 **/

@Component
public class KeywordFilter implements IFilter {

    public Set<Long> filter(Collection<Long> unitIds, SearchFeature feature) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptySet();
        }

        KeywordFeature keywordFeature = (KeywordFeature) feature;

        if (CollectionUtils.isEmpty(keywordFeature.getKeywords())){
            return (Set<Long>) unitIds;
        }

        return unitIds.stream().filter((unitId) ->
                DataTable.of(UnitKeywordIndexRedis.class)
                .match(unitId, keywordFeature.getKeywords())
        ).collect(Collectors.toSet());
    }
}
