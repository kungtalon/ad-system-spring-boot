package org.talon.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.mysql.OpType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableTemplate {
    private String tableName;
    private String level;
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();

    private Map<Integer, String> colIndexToNameMap = new HashMap<>();


}
