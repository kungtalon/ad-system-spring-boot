package org.talon.ad.search.selector;

import edu.emory.mathcs.backport.java.util.Collections;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.search.vo.SearchRequest;

import java.util.List;
import java.util.Random;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
@Order(value = 100)
public class RandomSelector implements ISelector{

    @Override
    public List<CreativeObject> select(List<CreativeObject> creativeObjectList,
                                       SearchRequest request) {
        CreativeObject randomObject = creativeObjectList.get(
                Math.abs(new Random().nextInt()) % creativeObjectList.size()
        );
        return Collections.singletonList(randomObject);
    }
}
