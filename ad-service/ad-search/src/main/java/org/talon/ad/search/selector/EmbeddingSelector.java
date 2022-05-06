package org.talon.ad.search.selector;

import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.talon.ad.exception.AdException;
import org.talon.ad.index.DataTable;
import org.talon.ad.index.creative.CreativeObject;
import org.talon.ad.index.embedding.CreativeEmbeddingIndexRedis;
import org.talon.ad.index.embedding.Embedding;
import org.talon.ad.index.embedding.QueryEmbeddingIndexRedis;
import org.talon.ad.search.vo.SearchRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/5
 * Select the creative with the highest dot-product
 * with the query embedding
 **/
@Slf4j
@Component
@Order(value = 0)
public class EmbeddingSelector implements ISelector {


    @Override
    public List<CreativeObject> select(List<CreativeObject> creativeObjectList,
                                       SearchRequest request) {
        try {
            List<String> queries = request.getFeatureInfo().getKeywordFeature().getKeywords();
            List<Embedding> queryEmbeddings = DataTable.of(QueryEmbeddingIndexRedis.class)
                    .retrieveByKeys(queries);
            if (CollectionUtils.isEmpty(queryEmbeddings)) {
                return Collections.emptyList();
            }

            Embedding queryEmbbedding = Embedding.meanPooling(queryEmbeddings);
            List<Embedding> creativeEmbeddings = DataTable.of(CreativeEmbeddingIndexRedis.class)
                    .retrieveByIds(creativeObjectList.stream()
                            .map(CreativeObject::getId).collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(creativeEmbeddings)) {
                return Collections.emptyList();
            }

            Pair<Double, CreativeObject> selectResult =
                    Pair.of(Double.MIN_VALUE, creativeObjectList.get(0));

            for (int ix = 0; ix < creativeObjectList.size(); ++ix){
                Double prod = queryEmbbedding.dot(creativeEmbeddings.get(ix));
                if (prod > selectResult.getFirst()) {
                    selectResult = Pair.of(prod, creativeObjectList.get(ix));
                }
            }

            log.info("Embedding selector selected the creative {} with a score of {}",
                    selectResult.getSecond(),
                    selectResult.getFirst());
            return Collections.singletonList(selectResult.getSecond());
        } catch (AdException ex) {
            log.error("Failed to select by embeddings...");
        }
        return Collections.emptyList();
    }
}
