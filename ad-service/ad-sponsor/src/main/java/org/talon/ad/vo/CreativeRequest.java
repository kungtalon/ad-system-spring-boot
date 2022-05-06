package org.talon.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.aether.util.StringUtils;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.datamodel.Creative;

import java.util.Date;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeRequest {

    private String name;
    private Integer type;
    private Integer format;
    private Integer height;
    private Integer width;
    private Long size;
    private Integer duration;
    private Long userId;
    private String url;

    public Creative convertToEntity() {
        Creative creative = new Creative();
        creative.setName(name);
        creative.setType(type);
        creative.setFormat(format);
        creative.setHeight(height);
        creative.setWidth(width);
        creative.setSize(size);
        creative.setDuration(duration);
        creative.setAuditStatus(CommonStatus.VALID.getStatus());
        creative.setUserId(userId);
        creative.setUrl(url);
        creative.setCreateTime(new Date());
        creative.setUpdateTime(creative.getCreateTime());

        return creative;
    }

    public boolean validate() {
        if (StringUtils.isEmpty(name) || type == null || type < 0
            || format == null || format < 0 || userId == null || url == null) {
            return false;
        }
        return true;
    }

}
