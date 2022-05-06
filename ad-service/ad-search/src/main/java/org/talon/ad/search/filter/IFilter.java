package org.talon.ad.search.filter;

import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.Collection;
import java.util.Set;

public interface IFilter {

    Set<Long> filter(Collection<Long> ids, SearchFeature feature);
}
