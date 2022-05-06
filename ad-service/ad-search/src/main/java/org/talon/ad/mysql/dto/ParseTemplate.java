package org.talon.ad.mysql.dto;

import lombok.Data;
import org.talon.ad.mysql.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Zelong
 * On 2022/5/4
 **/

@Data
public class ParseTemplate {

    private String database;
    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    public static ParseTemplate parse(JsonTable jsonTable) {

        ParseTemplate parseTemplate = new ParseTemplate();
        parseTemplate.setDatabase(jsonTable.getDatabase());

        for(JsonSubTable table : jsonTable.getTableList()){
            String name = table.getTableName();
            Integer level = table.getLevel();

            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(name);
            tableTemplate.setLevel(level.toString());
            parseTemplate.tableTemplateMap.put(name, tableTemplate);

            Map<OpType, List<String>> opTypeFieldSetMap = tableTemplate.getOpTypeFieldSetMap();

            for (JsonSubTable.Column column : table.getInsert()) {
                getOrCreate(
                        OpType.ADD,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }

            for (JsonSubTable.Column column : table.getUpdate()) {
                getOrCreate(
                        OpType.UPDATE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }

            for (JsonSubTable.Column column : table.getDelete()) {
                getOrCreate(
                        OpType.DELETE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }
        }
        return parseTemplate;
    }

    private static <T, R> R getOrCreate(T key, Map<T, R> map, Supplier<R> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }

}
