package org.talon.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class App {

    private String appCode;

    private String appName;

    private String packageName;
    // the activity that calls the ad service
    private String activityName;

}
