package com.example.shoutout.util;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Small utility class for quickly generating a pseudo-random string-based ID.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class UID {

    /**
     * The constant length of all UIDs.
     */
    public static final int STRING_LENGTH = 12;

    /**
     * An array of all chars used in the creation of UIDs. This includes the following:
     * <code>0-9a-z</code>, so all numeric and lowercase alphabetic characters.
     */
    private static final char[] AVAILABLE_CHARS;

    /**
     * Secure random object used to generate the UIDs.
     */
    private static final SecureRandom RAND;

    static {
        RAND = new SecureRandom();
        // create a temp arraylist to store chars, then convert it to an array later
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

    /**
     * Generate a random string-based ID. It will have a length of {@link #STRING_LENGTH}.
     * @return A random string-based ID with numeric and lowercase alphabetic characters
     */
    public static String generate() {
        char[] chars = new char[STRING_LENGTH];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = AVAILABLE_CHARS[RAND.nextInt(AVAILABLE_CHARS.length)];
        }
        return new String(chars);
    }

}
