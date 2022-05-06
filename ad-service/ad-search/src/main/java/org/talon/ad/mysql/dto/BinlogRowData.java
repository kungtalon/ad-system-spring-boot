package org.talon.ad.mysql.dto;

import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
public class BinlogRowData {
    private TableTemplate table;
    private EventType eventType;
    private List<Map<String, String>> after;
    private List<Map<String, String>> before;
}
