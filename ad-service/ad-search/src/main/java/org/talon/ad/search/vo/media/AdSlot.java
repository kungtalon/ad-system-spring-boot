package org.talon.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSlot {

    private String adSlotCode;

    private Integer positionType;

    private Integer height;
    private Integer width;

    private List<Integer> supportedTypes;

    // minimum bidding amount
    // which is the cost for exposing the ad
    private Long minCpm;
}
