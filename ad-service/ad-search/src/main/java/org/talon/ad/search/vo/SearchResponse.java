package org.talon.ad.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.index.creative.CreativeObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    public Map<String, List<Creative>> adSlot2Ads = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creative {
        private Long creativeId;
        private String url;
        private Integer width;
        private Integer height;
        private Integer type;
        private Integer format;

        // monitor the exposure event of the ad
        private List<String> showMonitorUrl =
                Arrays.asList("www.talonad.com/api/show", "www.talonad.com/api/show");
        // monitor the click event of the ad
        private List<String> clickMonitorUrl =
                Arrays.asList("www.talonad.com/api/click", "www.talonad.com/api/click");

    }

    public static Creative convert(CreativeObject o) {
        Creative creative = new Creative();
        creative.setCreativeId(o.getId());
        creative.setType(o.getType());
        creative.setFormat(o.getFormat());
        creative.setHeight(o.getHeight());
        creative.setWidth(o.getWidth());
        creative.setUrl(o.getUrl());
        return creative;
    }
}
