package org.talon.ad.search.filter;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.interest.UnitInterestIndexRedis;
import org.talon.ad.search.vo.feature.InterestFeature;
import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
public class InterestFilter implements IFilter{

    public Set<Long> filter(Collection<Long> ids, SearchFeature feature) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }

        InterestFeature interestFeature = (InterestFeature) feature;
        return ids.stream().filter(
                (id) -> DataTable.of(UnitInterestIndexRedis.class)
                        .match(id, interestFeature.getInterestTags())
        ).collect(Collectors.toSet());
    }
}
