package com.buttongames.butterfly.util;

/**
 * Simple class for program-wide constants that a lot of classes need.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class Constants {

    /**
     * Name of the app.
     */
    public static final String APP_NAME = "butterfly";

    /**
    * Name of the HTTP header that contains the crypto key, if present.
    */
    public static final String CRYPT_KEY_HEADER = "X-Eamuse-Info";

    /**
     * Name of the HTTP header that says whether the packet is compressed.
     */
    public static final String COMPRESSION_HEADER = "X-Compress";

    /**
     * Value for the X-Compress header to indicate the packet is compressed.
     */
    public static final String LZ77_COMPRESSION = "lz77";
}
