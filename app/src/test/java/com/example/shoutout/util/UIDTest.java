package com.example.shoutout.util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * A collection of unit test cases for the {@link UID} utility class.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class UIDTest {

    @Test
    public void testGenerate() {
        Set<String> uids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            final String uid = UID.generate();
            // constant length
            assertEquals(UID.STRING_LENGTH, uid.length());
            // proper characters
            assertTrue(uid.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')));
            // unique
            assertTrue(uids.add(UID.generate()));
        }
    }

}