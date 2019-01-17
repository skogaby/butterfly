package com.buttongames.butterfly.util;

import com.google.common.io.ByteStreams;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for converting between NFC IDs (the internal ID) and card numbers
 * (the ID on the back of the card) for e-amusement passes. Right now it just calls out
 * to a borrowed Python script, need to port this to a native implementation.
 * @author anonymous
 */
public class CardIdUtils {

    /**
     * Converts an internal NFC ID to a display ID for e-amusement passes.
     * @param id The NFC ID for the pass
     * @return The display ID for the pass
     */
    public static String encodeCardId(final String id) {
        try {
            // TODO: Port the python script
            final Path path = Paths.get(ClassLoader.getSystemResource("cardconv.py").toURI());
            final ProcessBuilder builder = new ProcessBuilder("python",
                    "-c",
                    "import cardconv; print(cardconv.CardCipher.encode('" + id + "'));");
            builder.directory(path.getParent().toFile());

            final Process process = builder.start();
            final InputStream stdout = process.getInputStream();

            return new String(ByteStreams.toByteArray(stdout), StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
