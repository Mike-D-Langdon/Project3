package com.example.shoutout.util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class UIDTest {

    @Test
    public void testGenerate() {
        Set<String> uids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            final String uid = UID.generate();
            // constant length
            assertEquals(12, uid.length());
            // proper characters
            assertTrue(uid.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')));
            // unique
            assertTrue(uids.add(UID.generate()));
        }
    }

}