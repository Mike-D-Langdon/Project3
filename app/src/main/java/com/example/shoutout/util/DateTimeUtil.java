package com.example.shoutout.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static long formatDateTime(LocalDateTime dt) {
        return dt.toEpochSecond(ZONE_OFFSET);
    }

    public static LocalDateTime parseDateTime(long epochSecond) {
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZONE_OFFSET);
    }

    public static String formatDate(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parseDate(String isoDateStr) {
        return LocalDate.from(DATE_FORMATTER.parse(isoDateStr));
    }

}
