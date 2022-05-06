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
public class Geo {

    private Float latitude;
    private Float longitude;

    private String country;
    private String state;
    private String city;
}
