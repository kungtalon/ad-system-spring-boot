package org.talon.ad.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Zelong
 * On 2022/5/2
 * Creative contains the ad materials shown to users
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ad_creative")
public class Creative extends AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "format", nullable = false)
    private Integer format;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "audit_status", nullable = false)
    private Integer auditStatus;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

}
