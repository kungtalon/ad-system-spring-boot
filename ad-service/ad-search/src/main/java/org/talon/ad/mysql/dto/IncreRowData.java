package org.talon.ad.mysql.dto;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.mysql.OpType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncreRowData {

    private String tableName;

    private String level;

    private OpType op;

    private List<Map<String, String>> newValueMap = new ArrayList<>();
}
