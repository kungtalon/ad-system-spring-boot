package org.talon.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestFeature extends SearchFeature {
    private List<String> interestTags;
}
