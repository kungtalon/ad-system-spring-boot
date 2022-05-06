package org.talon.ad.index.creative;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.dump.table.AdCreativeTable;

/**
 * Created by Zelong
 * On 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreativeObject {
    private Long id;
    private String name;
    private Integer type;
    private Integer format;
    private Integer height;
    private Integer width;
    private Integer auditStatus;
    private String url;

    public void update(CreativeObject newObject) {
        if (null != newObject.getId()) {
            this.id = newObject.getId();
        }
        if (null != newObject.getName()) {
            this.name = newObject.getName();
        }
        if (null != newObject.getType()) {
            this.type = newObject.getType();
        }
        if (null != newObject.getHeight()) {
            this.height = newObject.getHeight();
        }
        if (null != newObject.getWidth()) {
            this.width = newObject.getWidth();
        }
        if (null != newObject.getAuditStatus()) {
            this.auditStatus = newObject.getAuditStatus();
        }
        if (null != newObject.getUrl()) {
            this.url = newObject.getUrl();
        }
    }

    public CreativeObject(AdCreativeTable t) {
        this.id = t.getCreativeId();
        this.name = t.getName();
        this.type = t.getType();
        this.height = t.getHeight();
        this.width = t.getWidth();
        this.auditStatus = t.getAuditStatus();
        this.url = t.getUrl();
    }

}
