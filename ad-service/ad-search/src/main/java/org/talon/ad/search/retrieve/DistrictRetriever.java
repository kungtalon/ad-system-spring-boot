package org.talon.ad.search.retrieve;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.IndexAware;
import org.talon.ad.index.district.UnitDistrictIndexRedis;
import org.talon.ad.index.district.UnitDistrictObject;
import org.talon.ad.search.filter.DistrictFilter;
import org.talon.ad.search.vo.feature.DistrictFeature;
import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.*;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
public class DistrictRetriever implements IRetriever {

    @Override
    public Set<Long> retrieve(SearchFeature feature) {

        DistrictFeature districtFeature = (DistrictFeature) feature;
        if (CollectionUtils.isEmpty(districtFeature.getDistricts())) {
            return Collections.emptySet();
        }

        Optional<Set<Long>> optionalResult = districtFeature.getDistricts().stream().map(
                d -> DataTable.of(UnitDistrictIndexRedis.class).get(d.toString())
        ).reduce((l, r) -> {
            Set<Long> tmp = new HashSet<>(l);
            tmp.addAll(r);
            return tmp;
        });

        return optionalResult.orElseGet(Collections::emptySet);
    }
}
