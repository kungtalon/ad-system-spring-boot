package org.talon.ad.mysql.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.ec.ECEncryptor;
import org.codehaus.plexus.util.StringUtils;
import org.mortbay.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talon.ad.mysql.ListenTargetsLoader;
import org.talon.ad.mysql.dto.BinlogRowData;
import org.talon.ad.mysql.dto.TableTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zelong
 * On 2022/5/4
 * A manager of listeners, all customized listeners will be registered here
 * A router for events, convert raw eventData to BinlogRowData
 **/
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {

    private String dbName;
    private String tableName;

    private Map<String, IListener> listenerMap = new HashMap<>();

    private ListenTargetsLoader listenTargets;

    @Autowired
    public AggregationListener(ListenTargetsLoader listenTargets) {
        this.listenTargets = listenTargets;
    }

    private String genKey(String dbName, String tableName) {
        return dbName + ":" + tableName;
    }

    public void register(String dbName, String tableName, IListener listener) {
        log.info("register : {} - {}", dbName, tableName);
        this.listenerMap.put(genKey(dbName, tableName), listener);
    }

    @Override
    public void onEvent(Event event) {
        EventType type = event.getHeader().getEventType();
        log.debug("event type: {}", type);

        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }

        if (type != EventType.EXT_UPDATE_ROWS
                && type != EventType.EXT_WRITE_ROWS
                && type != EventType.EXT_DELETE_ROWS) {
            return ;
        }

        // check whether tableName and dbName has been filled
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("table map data event missing");
            return ;
        }

        // get listeners bonded with targets
        String key = genKey(this.dbName, this.tableName);
        IListener listener = this.listenerMap.get(key);
        if (null == listener) {
            // no listener registered with this table
            // ignore it
            log.debug("skip {}", key);
            return ;
        }

        try {
            BinlogRowData rowData = buildRowData(event.getData());

            if (rowData == null) {
                return ;
            }

            rowData.setEventType(type);
            listener.onEvent(rowData);


        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        } finally {
            this.dbName = "";
            this.tableName = "";
        }
    }

    private List<Serializable[]> getAfterValues(EventData eventData) {
        // after values are defined in EventData
        // they are list of arrays
        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        } else if (eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        } else if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }
        return Collections.emptyList();
    }

    private BinlogRowData buildRowData(EventData eventData) {
        TableTemplate table = listenTargets.getTable(tableName);

        if (null == table) {
            log.warn("table {} not found", tableName);
            return null;
        }

        List<Map<String, String>> afterMapList = new ArrayList<>();

        for (Serializable[] after : getAfterValues(eventData)){
            // why is it two dim array?
            Map<String, String> afterMap = new HashMap<>();

            int colLen = after.length;

            for (int ix = 0; ix < colLen; ++ix) {
                // get the name of the current column
                String colName = table.getColIndexToNameMap().get(ix);

                if (null == colName) {
                    log.debug("ignore column : {}", ix);
                    continue;
                }

                String colValue = after[ix].toString();
                afterMap.put(colName, colValue);
            }
            afterMapList.add(afterMap);
        }

        BinlogRowData rowData = new BinlogRowData();
        rowData.setAfter(afterMapList);
        rowData.setTable(table);

        return rowData;
    }
}
