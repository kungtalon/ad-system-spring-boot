package org.talon.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String userName;

    public boolean validate() {
        return !StringUtils.isEmpty(userName);
    }
}
