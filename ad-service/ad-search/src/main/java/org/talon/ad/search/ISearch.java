package org.talon.ad.search;

import org.talon.ad.exception.AdException;
import org.talon.ad.search.vo.SearchRequest;
import org.talon.ad.search.vo.SearchResponse;

public interface ISearch {

    // the request contains a list of adSlots
    // so response is a map, in which each adSlot has a creative
    SearchResponse fetchAds(SearchRequest request) throws AdException;
}
