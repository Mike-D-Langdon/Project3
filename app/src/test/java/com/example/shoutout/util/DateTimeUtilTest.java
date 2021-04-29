package com.example.shoutout.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilTest {

    @Test
    public void testParseSimpleDate() {
        assertEquals(new Date(2000, 1, 1), DateTimeUtil.parseSimpleDate("1/1/2000"));
        assertEquals(new Date(1900, 6, 7), DateTimeUtil.parseSimpleDate("6/7/1900"));
        assertEquals(new Date(2038, 2, 20), DateTimeUtil.parseSimpleDate("2/20/2038"));
    }

    public void testFormatSimpleDate() {
        assertEquals("1/1/2000", DateTimeUtil.formatSimpleDate(new Date(2000, 1, 1)));
        assertEquals("6/7/1900", DateTimeUtil.formatSimpleDate(new Date(1900, 6, 7)));
        assertEquals("2/20/2038", DateTimeUtil.formatSimpleDate(new Date(2038, 2, 20)));
    }

    public void testFormatLongerDate() {
        assertEquals("Jan 1, 2000", DateTimeUtil.formatSimpleDate(new Date(2000, 1, 1)));
        assertEquals("Jun 7, 1900", DateTimeUtil.formatSimpleDate(new Date(1900, 6, 7)));
        assertEquals("Feb 20, 2038", DateTimeUtil.formatSimpleDate(new Date(2038, 2, 20)));
    }

}