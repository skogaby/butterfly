package com.buttongames.butterfly.model.ddr16.options;

/**
 * Enum for the various appearance options in-game for DDR A.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public enum AppearanceOption {
    VISIBLE(0),
    HIDDEN_PLUS(3),
    SUDDEN_PLUS(4),
    HIDDEN_AND_SUDDEN_PLUS(5),
    STEALTH(6);

    /**
     * The value to put in the profile response for this option.
     */
    private final int val;

    private AppearanceOption(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }
}
