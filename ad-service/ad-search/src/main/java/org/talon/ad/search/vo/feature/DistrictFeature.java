package org.talon.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.utils.CommonUtils;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictFeature extends SearchFeature {

    private List<DistrictType> districts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DistrictType {
        private String country;
        private String state;
        private String city;

        public String toString() {
            return CommonUtils.concat(country, state, city);
        }
    }
}
