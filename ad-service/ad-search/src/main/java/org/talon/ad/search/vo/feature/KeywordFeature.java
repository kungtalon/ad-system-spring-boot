package org.talon.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Set;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordFeature extends SearchFeature {

    private List<String> keywords;
}
