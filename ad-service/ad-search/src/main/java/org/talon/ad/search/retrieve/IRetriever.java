package org.talon.ad.search.retrieve;

import org.talon.ad.search.vo.feature.SearchFeature;

import java.util.Set;

public interface IRetriever {

    Set<Long> retrieve(SearchFeature feature);
}
