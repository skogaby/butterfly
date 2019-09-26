package com.buttongames.butterflycore.util;

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
        final Instant instant = Instant.ofEpochMilli(millis);

        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
