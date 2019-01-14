package com.buttongames.butterfly.util;

import java.util.Random;

/**
 * Simple class containing some helper methods to manipulate strings.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class StringUtils {

    /**
     * Returns a random hex string with the specified length.
     * @param length The number of characters to generate.
     * @return A random hex string with the specified length
     */
    public static String getRandomHexString(final int length){
        final Random r = new Random();
        final StringBuffer sb = new StringBuffer();

        while(sb.length() < length){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, length).toUpperCase();
    }
}
