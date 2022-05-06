package org.talon.ad.utils;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Zelong
 * On 2022/5/3
 **/

public class CommonUtils {

    public static <K, V> V getOrCreate(K key, Map<K, V> map, Supplier<V> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }

    public static String concat(String... args) {
        StringBuilder result = new StringBuilder();
        for (String arg: args) {
            result.append(arg);
            result.append("-");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

}
