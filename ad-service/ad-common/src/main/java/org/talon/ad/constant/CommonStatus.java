package org.talon.ad.constant;

import lombok.Getter;

@Getter
public enum CommonStatus {

    INVALID(0, "is invalid"),
    VALID(1, "is valid"),
    UNVERIFIED(2, "not verified");

    private final Integer status;
    private final String desc;

    CommonStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
