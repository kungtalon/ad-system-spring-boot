package org.talon.ad.search.selector;

import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.search.vo.SearchRequest;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/4
 * Selector classes will select the best creatives for a request
 **/

public interface ISelector {

    List<CreativeObject> select(List<CreativeObject> creativeObjectList, SearchRequest request);

}
