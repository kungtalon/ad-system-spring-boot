package org.talon.ad.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.search.vo.feature.DistrictFeature;
import org.talon.ad.search.vo.feature.FeatureRelation;
import org.talon.ad.search.vo.feature.InterestFeature;
import org.talon.ad.search.vo.feature.KeywordFeature;
import org.talon.ad.search.vo.media.AdSlot;
import org.talon.ad.search.vo.media.App;
import org.talon.ad.search.vo.media.Device;
import org.talon.ad.search.vo.media.Geo;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // the identifier for the media who sends the request
    private String mediaId;

    // basic info of the request
    private RequestInfo requestInfo;

    // how to find wanted ad
    private FeatureInfo featureInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        private String requestId;
        private List<AdSlot> adSlots;
        private App app;
        private Geo geo;
        private Device device;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo {
        private KeywordFeature keywordFeature;
        private DistrictFeature districtFeature;
        private InterestFeature interestFeature;
        private FeatureRelation relation = FeatureRelation.AND;
    }
}
