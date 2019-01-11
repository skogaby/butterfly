package com.buttongames.butterfly.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Simple class with utility functions for dealing with time.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class TimeUtils {

    /**
     * Convert an epoch timestamp to a LocalDateTime.
     * @param millis
     * @return
     */
    public static LocalDateTime timeFromEpoch(final long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
}
