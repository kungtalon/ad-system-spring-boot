package org.talon.ad.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.talon.ad.mysql.BinlogClient;
import org.talon.ad.mysql.BinlogConfig;

import javax.annotation.Resource;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
public class BinlogRunner implements CommandLineRunner {

    @Resource(name = "binlogClient")
    private BinlogClient client;

    @Override
    public void run(String... strings) throws Exception{
        log.info("Coming in BinlogUnner");
        client.connect();
    }
}
