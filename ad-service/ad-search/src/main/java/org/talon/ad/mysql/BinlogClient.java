package org.talon.ad.mysql;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.mortbay.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talon.ad.mysql.listener.AggregationListener;

import java.io.IOException;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class BinlogClient {

    private BinaryLogClient client;

    private BinlogConfig config;

    private AggregationListener aggListener;

    @Autowired
    public BinlogClient(BinlogConfig config, AggregationListener aggListener) {
        this.config = config;
        this.aggListener = aggListener;
    }

    public void connect() {
        new Thread(() -> {
            client = new BinaryLogClient(
                    config.getHost(),
                    config.getPort(),
                    config.getUsername(),
                    config.getPassword()
            );
            if (!StringUtils.isEmpty(config.getBinlogName())
            && !config.getPort().equals(-1L)) {
                client.setBinlogFilename(config.getBinlogName());
                client.setBinlogPosition(config.getPosition());
            }

            client.registerEventListener(aggListener);

            try {
                log.info("connecting to mysql start");
                client.connect();
                log.info("connected to mysql!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }

    public void close() {
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
