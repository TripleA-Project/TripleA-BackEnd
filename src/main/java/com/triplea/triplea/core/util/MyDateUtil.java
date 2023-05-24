package com.triplea.triplea.core.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MyDateUtil {
    public static String toStringFormat(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
