package org.talon.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonSubTable {

    private String tableName;
    private Integer level;

    private List<Column> insert;
    private List<Column> update;
    private List<Column> delete;

    @Data
    public static class Column {
        private String column;
    }
}
