package com.example.shoutout.util;

import java.security.SecureRandom;
import java.util.ArrayList;

public class UID {

    private static final int STRING_LENGTH = 12;
    private static final char[] AVAILABLE_CHARS;
    private static final SecureRandom RAND;

    static {
        RAND = new SecureRandom();
        ArrayList<Character> chars = new ArrayList<>(36);
        for (char c = '0'; c <= '9'; c++) {
            chars.add(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            chars.add(c);
        }
        AVAILABLE_CHARS = new char[chars.size()];
        for (int i = 0; i < AVAILABLE_CHARS.length; i++) {
            AVAILABLE_CHARS[i] = chars.get(i);
        }
    }

    public static String generate() {
        char[] chars = new char[STRING_LENGTH];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = AVAILABLE_CHARS[RAND.nextInt(AVAILABLE_CHARS.length)];
        }
        return new String(chars);
    }

}
