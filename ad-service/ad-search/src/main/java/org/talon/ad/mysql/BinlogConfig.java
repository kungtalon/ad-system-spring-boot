package org.talon.ad.mysql;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Component
@ConfigurationProperties(prefix = "adconf.mysql")
@EnableEncryptableProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinlogConfig {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String binlogName;

    private Long position;

}
