package com.buttongames.butterflycore.util;

import com.buttongames.butterflycore.cardconv.A;
import org.springframework.stereotype.Component;

/**
 * Utility class for converting between NFC IDs (the internal ID) and card numbers
 * (the ID on the back of the card) for e-amusement passes.
 * @author anonymous
 */
@Component
public class CardIdUtils {

    /**
     * Converts an internal NFC ID to a display ID for e-amusement passes.
     * @param id The NFC ID for the pass
     * @return The display ID for the pass
     */
    public String encodeCardId(final String id) {
        return new A().toKonamiID(id);
    }
}
