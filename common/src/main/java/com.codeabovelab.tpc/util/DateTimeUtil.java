package com.codeabovelab.tpc.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 */
public class DateTimeUtil {
    public static String toUTCString(Date date) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC));
    }
}
