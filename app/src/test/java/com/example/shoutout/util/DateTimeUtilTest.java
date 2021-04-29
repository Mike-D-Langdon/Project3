package com.example.shoutout.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * A collection of unit tests for the {@link DateTimeUtil} utility class.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class DateTimeUtilTest {

    @Test
    public void testParseSimpleDate() {
        assertEquals(new Date(946713600000L), DateTimeUtil.parseSimpleDate("1/1/2000"));
        assertEquals(new Date(-2195395200000L), DateTimeUtil.parseSimpleDate("6/7/1900"));
        assertEquals(new Date(2150265600000L), DateTimeUtil.parseSimpleDate("2/20/2038"));
    }

    @Test
    public void testFormatSimpleDate() {
        assertEquals("1/1/2000", DateTimeUtil.formatSimpleDate(new Date(946713600000L)));
        assertEquals("6/7/1900", DateTimeUtil.formatSimpleDate(new Date(-2195395200000L)));
        assertEquals("2/20/2038", DateTimeUtil.formatSimpleDate(new Date(2150265600000L)));
    }

    @Test
    public void testFormatLongerDate() {
        assertEquals("Jan 1, 2000", DateTimeUtil.formatLongerDate(new Date(946713600000L)));
        assertEquals("Jun 7, 1900", DateTimeUtil.formatLongerDate(new Date(-2195395200000L)));
        assertEquals("Feb 20, 2038", DateTimeUtil.formatLongerDate(new Date(2150265600000L)));
    }

}