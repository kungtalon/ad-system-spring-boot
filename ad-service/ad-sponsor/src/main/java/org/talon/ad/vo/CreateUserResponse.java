package org.talon.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {
    private Long userId;
    private String userName;
    private String token;
    private Date createTime;
    private Date updateTime;

}
