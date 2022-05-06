package org.talon.ad.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;
import org.talon.ad.exception.AdException;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Zelong
 * On 2022/5/2
 **/

public class CommonUtils {

    private static final String[] parsePatterns = {
            "yyyy-MM-dd",
            "yyyyMMdd",
            "yyyy/MM/dd",
            "yyyy.MM.dd"
    };

    public static String md5(String value) {
        return DigestUtils.md5Hex(value).toUpperCase();
    }

    public static Date parseStringDate(String dateString) throws AdException {
        try {
            return DateUtils.parseDate(dateString, parsePatterns);
        }
        catch (ParseException e) {
            throw new AdException(e.getMessage());
        }
    }
}
