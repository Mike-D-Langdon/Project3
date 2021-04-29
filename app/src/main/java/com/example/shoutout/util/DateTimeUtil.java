package com.example.shoutout.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A collection of date- and time-related utility methods.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class DateTimeUtil {

    /**
     * Compact date format donning the pattern of <code>M/d/yyyy</code>. So the following
     * examples would correspond:
     * <ul>
     *     <li>1/1/2000</li>
     *     <li>6/7/1900</li>
     *     <li>2/20/2038</li>
     * </ul>
     */
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("M/d/yyyy");

    /**
     * Slightly longer date format donning the pattern of <code>MMM, d, yyyy</code>. So the
     * following examples would correspond:
     * <ul>
     *     <li>Jan 1, 2000</li>
     *     <li>Jun 7, 1900</li>
     *     <li>Feb 20, 2038</li>
     * </ul>
     */
    private static final SimpleDateFormat LONGER_DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    /**
     * Parse a date format corresponding to {@link #SIMPLE_DATE_FORMAT}
     * @param str The pre-formatted date to parse
     * @return The parsed date. Will return null if string could not be parsed.
     */
    public static Date parseSimpleDate(String str) {
        try {
            return SIMPLE_DATE_FORMAT.parse(str);
        } catch (ParseException ignored) {}
        return null;
    }

    /**
     * Format a {@link Date} object into a string according to {@link #SIMPLE_DATE_FORMAT}.
     * @param date The date object to format
     * @return The string-formatted date
     */
    public static String formatSimpleDate(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

    /**
     * Format a {@link Date} object into a string according to {@link #LONGER_DATE_FORMAT}.
     * @param date The date object to format
     * @return The string-formatted date
     */
    public static String formatLongerDate(Date date) {
        return LONGER_DATE_FORMAT.format(date);
    }

}
