package org.talon.ad.search.retrieve;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.district.UnitDistrictIndexRedis;
import org.talon.ad.index.interest.UnitInterestIndexRedis;
import org.talon.ad.search.vo.feature.DistrictFeature;
import org.talon.ad.search.vo.feature.InterestFeature;
import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
public class InterestRetriever implements IRetriever {

    @Override
    public Set<Long> retrieve(SearchFeature feature) {

        InterestFeature interestFeature = (InterestFeature) feature;
        if (CollectionUtils.isEmpty(interestFeature.getInterestTags())) {
            return Collections.emptySet();
        }

        Optional<Set<Long>> optionalResult = interestFeature.getInterestTags().stream().map(
                d -> DataTable.of(UnitInterestIndexRedis.class).get(d)
        ).reduce((l, r) -> {
            Set<Long> tmp = new HashSet<>(l);
            tmp.addAll(r);
            return tmp;
        });

        return optionalResult.orElseGet(Collections::emptySet);
    }
}
