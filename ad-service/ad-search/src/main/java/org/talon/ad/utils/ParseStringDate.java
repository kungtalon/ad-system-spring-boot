package org.talon.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
public class ParseStringDate {

    public static Date parseBinlogDate(String dateStr) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.US
            );
            return DateUtils.addHours(dateFormat.parse(dateStr), 4);
        } catch (Exception ex) {
            log.error("parse string date error: {}", dateStr);
            return null;
        }
    }
}
