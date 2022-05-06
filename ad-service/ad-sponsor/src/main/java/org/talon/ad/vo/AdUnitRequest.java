package org.talon.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.aether.util.StringUtils;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitRequest {

    private Long id;
    private Long planId;
    private String unitName;

    private Integer positionType;
    private Long budget;

    public boolean createValidate() {
        return null != planId && !StringUtils.isEmpty(unitName) &&
                positionType != null && budget != null &&
                positionType >= 0 && budget >= 0;
    }
}
