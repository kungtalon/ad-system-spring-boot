package org.talon.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String deviceCode;

    private String mac;

    private String ip;

    private String model;

    private String displaySize;

    private String screenSize;

    // serial id from the device
    private String serialName;

}
