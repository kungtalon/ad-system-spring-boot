package org.talon.ad.index.embedding;

import jdk.nashorn.internal.runtime.ScriptEnvironment;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.EnableMBeanExport;
import org.talon.ad.exception.AdException;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/5
 **/
@Data
@AllArgsConstructor
public class Embedding {

    private Double[] vector;
    private Integer len;

    public Embedding(int len) {
        this.len = len;
        this.vector = new Double[len];
    }

    public Embedding(Double[] array) {
        this.len = array.length;
        this.vector = array;
    }

    public Embedding(List<Double> arrayList) {
        this.len = arrayList.size();
        this.vector = arrayList.toArray(new Double[0]);
    }

    public int size() {
        return len;
    }

    protected Double v(int idx) throws AdException {
        if (idx >= len) {
            throw new AdException("Index out of bound!");
        }
        return vector[idx];
    }

    public Double dot(Embedding another) throws AdException {
        if (another.size() != this.len) {
            throw new AdException("Mismatch of two vector for dot product!");
        }
        double result = 0;
        for (int ix = 0; ix < len; ++ix) {
            result += vector[ix] * another.v(ix);
        }
        return result;
    }

    public Embedding sum(Embedding another) throws AdException {
        if (another.size() != this.len) {
            throw new AdException("Mismatch of two vector for dot product!");
        }
        Double[] result = new Double[len];
        for (int ix = 0; ix < len; ++ix) {
            result[ix] = vector[ix] + another.v(ix);
        }
        return new Embedding(result);
    }

    public static Embedding meanPooling(List<Embedding> embeddings) throws AdException {
        if (CollectionUtils.isEmpty(embeddings)) {
            return null;
        }

        Embedding result = embeddings.get(0);
        for (int ix = 1; ix < embeddings.size(); ++ix) {
            result = embeddings.get(ix).sum(result);
        }
        return result;
    }

}
