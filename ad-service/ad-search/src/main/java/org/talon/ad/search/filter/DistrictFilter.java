package org.talon.ad.search.filter;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.district.UnitDistrictIndexRedis;
import org.talon.ad.search.vo.feature.DistrictFeature;
import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
public class DistrictFilter implements IFilter{

    @Override
    public Set<Long> filter(Collection<Long> ids, SearchFeature feature) {
            if (CollectionUtils.isEmpty(ids)) {
                return Collections.emptySet();
            }

            DistrictFeature districtFeature = (DistrictFeature) feature;
            if (CollectionUtils.isEmpty(districtFeature.getDistricts())) {
                return (Set<Long>) ids;
            }
            List<String> districts = districtFeature.getDistricts()
                    .stream().map(d -> d.toString()).collect(Collectors.toList());
            return ids.stream().filter((unitId) ->
                    DataTable.of(UnitDistrictIndexRedis.class)
                            .match(unitId, districts)
            ).collect(Collectors.toSet());
    }
}
