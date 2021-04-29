package com.example.shoutout.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A collection of unit test cases for the {@link StringUtil} utility class.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class StringUtilTest {

    @Test
    public void testGetFileNameExtension() {
        assertEquals(".jpg",    StringUtil.getFileNameExtension("mypicture.jpg"));
        assertEquals("",        StringUtil.getFileNameExtension("myscript"));
        assertEquals(".exe",    StringUtil.getFileNameExtension("mygame.exe"));
        assertEquals(".png",    StringUtil.getFileNameExtension("weird_file.final.png"));
    }

    @Test
    public void testGetFileNameWithoutExtension() {
        assertEquals("mypicture",           StringUtil.getFileNameWithoutExtension("mypicture.jpg"));
        assertEquals("myscript",            StringUtil.getFileNameWithoutExtension("myscript"));
        assertEquals("mygame",              StringUtil.getFileNameWithoutExtension("mygame.exe"));
        assertEquals("weird_file.final",    StringUtil.getFileNameWithoutExtension("weird_file.final.png"));
    }

}