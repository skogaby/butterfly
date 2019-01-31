package com.buttongames.butterfly.util;

import com.buttongames.butterfly.Main;
import com.google.common.io.ByteStreams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for converting between NFC IDs (the internal ID) and card numbers
 * (the ID on the back of the card) for e-amusement passes. Right now it just calls out
 * to a borrowed Python script, need to port this to a native implementation.
 * @author anonymous
 */
@Component
public class CardIdUtils {

    /**
     * Says whether or not this server is running in maintenance mode.
     */
    @Value(PropertyNames.HOME_DIR)
    private String homeDirectory;

    /**
     * Converts an internal NFC ID to a display ID for e-amusement passes.
     * @param id The NFC ID for the pass
     * @return The display ID for the pass
     */
    public String encodeCardId(final String id) {
        try {
            // TODO: Port the python script
            // let the script be loadable either from the home directory or from the resources dir (when running in IDE)
            Path path = Paths.get(this.homeDirectory, "cardconv.py");

            if (!Files.exists(path)) {
                path = Paths.get(Main.class.getResource("/cardconv.py").toURI());
            }

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
