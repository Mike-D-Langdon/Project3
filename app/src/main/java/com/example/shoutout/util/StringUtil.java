package com.example.shoutout.util;

public class StringUtil {

    public static String getFileNameExtension(String fileName) {
        final int indexOf = fileName.lastIndexOf('.');
        if (indexOf >= 0) {
            return fileName.substring(indexOf);
        }
        return "";
    }

    public static String getFileNameWithoutExtension(String fileName) {
        final int indexOf = fileName.lastIndexOf('.');
        if (indexOf >= 0) {
            return fileName.substring(0, indexOf);
        }
        return fileName;
    }

}
