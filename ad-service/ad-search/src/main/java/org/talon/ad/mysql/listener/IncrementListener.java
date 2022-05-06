package org.talon.ad.mysql.listener;

import com.github.shyiko.mysql.binlog.event.EventType;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.talon.ad.mysql.Consts;
import org.talon.ad.mysql.OpType;
import org.talon.ad.mysql.dto.BinlogRowData;
import org.talon.ad.mysql.dto.IncreRowData;
import org.talon.ad.mysql.dto.TableTemplate;
import org.talon.ad.sender.ISender;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class IncrementListener implements IListener {

    @Resource(name = "indexSender")
    private ISender sender;

    @Resource
    private AggregationListener aggregationListener;

    @Override
    @PostConstruct
    public void register() {
        log.info("IncrementListener register db and table info");
        Consts.table2Db.forEach((k, v) -> {
            aggregationListener.register(v, k, this);
        });
    }

    @Override
    public void onEvent(BinlogRowData eventData) {
        TableTemplate tableTemplate = eventData.getTable();
        EventType eventType = eventData.getEventType();

        // convert event data to increData
        IncreRowData rowData = new IncreRowData();

        rowData.setTableName(tableTemplate.getTableName());
        rowData.setLevel(eventData.getTable().getLevel());
        OpType opType = OpType.to(eventType);
        rowData.setOp(opType);

        // get the field list
        List<String> fieldList = tableTemplate.getOpTypeFieldSetMap().get(opType);
        if (null == fieldList) {
            log.warn("{} not supported by {}", opType, tableTemplate.getTableName());
            return;
        }

        Gson gson = new Gson();
        for(Map<String, String> afterMap : eventData.getAfter()) {
            Map<String, String> copyMap = gson.fromJson(gson.toJson(afterMap), HashMap.class);
            rowData.getNewValueMap().add(copyMap);
        }

        sender.send(rowData);
    }
}
