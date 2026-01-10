package com.capstoneproject.smartattendance.util;

import java.security.SecureRandom;

public class RandomStringUtil {

    private static final String CHARACTERS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom random = new SecureRandom();

    private RandomStringUtil() {
        // prevent object creation
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(
                random.nextInt(CHARACTERS.length())
            ));
        }
        return sb.toString();
    }
}
