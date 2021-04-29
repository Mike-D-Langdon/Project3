package com.example.shoutout.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static final SimpleDateFormat
            SIMPLE_DATE_FORMAT = new SimpleDateFormat("M/d/yyyy"),
            LONGER_DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    public static Date parseSimpleDate(String str) {
        try {
            return SIMPLE_DATE_FORMAT.parse(str);
        } catch (ParseException ignored) {}
        return null;
    }

    public static String formatSimpleDate(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

    public static String formatLongerDate(Date date) {
        return LONGER_DATE_FORMAT.format(date);
    }

}
