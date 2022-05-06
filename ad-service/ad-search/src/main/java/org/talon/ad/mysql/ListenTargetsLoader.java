package org.talon.ad.mysql;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.talon.ad.mysql.dto.ParseTemplate;
import org.talon.ad.mysql.dto.TableTemplate;
import org.talon.ad.mysql.dto.JsonTable;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class ListenTargetsLoader {

    private ParseTemplate template;

    private final JdbcTemplate jdbcTemplate;

    private final String SQL_SCHEMA = "" +
            "select table_schema, table_name, column_name, ordinal_position " +
            "from information_schema.columns " +
            "where " +
            "table_schema = ? " +
            "and " +
            "table_name = ? ";

    @Autowired
    public ListenTargetsLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void init() {
        loadJson("listen_targets.json");
    }

    public TableTemplate getTable(String tableName) {
        return template.getTableTemplateMap().get(tableName);
    }

    private void loadJson(String filePath) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = cl.getResourceAsStream(filePath);
        Gson gson = new Gson();

        try {
            JsonTable jsonTable = gson.fromJson(
                    IOUtils.toString(inputStream, Charsets.UTF_8),
                    JsonTable.class
            );
            this.template = ParseTemplate.parse(jsonTable);
            loadMeta();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("fail to parse json file!");
        }

    }

    private void loadMeta() {
        for (Map.Entry<String, TableTemplate> entry :
                template.getTableTemplateMap().entrySet()){
            TableTemplate table = entry.getValue();

            List<String> updateFields = table.getOpTypeFieldSetMap().get(
                    OpType.UPDATE
            );
            List<String> insertFields = table.getOpTypeFieldSetMap().get(
                    OpType.ADD
            );
            List<String> deleteFields = table.getOpTypeFieldSetMap().get(
                    OpType.DELETE
            );

            jdbcTemplate.query(SQL_SCHEMA, new Object[]{
                    template.getDatabase(), table.getTableName()
            }, (rs, i) -> {
                int pos = rs.getInt("ORDINAL_POSITION");
                String colName = rs.getString("COLUMN_NAME");

                if ((null != updateFields && updateFields.contains(colName))
                || (null != insertFields && insertFields.contains(colName))
                || (null != deleteFields && deleteFields.contains(colName))) {
                    table.getColIndexToNameMap().put(pos - 1, colName);
                }

                return null;
            });
        }
    }

}
