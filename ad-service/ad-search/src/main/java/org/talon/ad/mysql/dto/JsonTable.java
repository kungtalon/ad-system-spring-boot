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
public class JsonTable {

    private String database;
    private List<JsonSubTable> tableList;

}
