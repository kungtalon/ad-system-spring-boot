package org.talon.ad.constant;

public enum CreativeMaterialFormat {

    JPG(1, "jpg"),
    PNG(2, "png"),
    GIF(3, "gif"),

    MP4(4, "mp4"),
    AVI(5, "avi"),
    MEPG(6, "mepg"),

    TXT(7, "txt");

    private int format;
    private String desc;

    CreativeMaterialFormat(int format, String desc) {
        this.format = format;
        this.desc = desc;
    }
}
