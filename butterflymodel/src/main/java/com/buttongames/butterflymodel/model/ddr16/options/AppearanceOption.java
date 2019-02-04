package com.buttongames.butterflymodel.model.ddr16.options;

import java.util.HashMap;

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

    private static final HashMap<Integer, AppearanceOption> valueToOptionMap = new HashMap<>();

    private final int val;

    static {
        for (AppearanceOption opt : AppearanceOption.values()) {
            valueToOptionMap.put(opt.getVal(), opt);
        }
    }

    private AppearanceOption(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    public static AppearanceOption optionForValue(int value) {
        if (!valueToOptionMap.containsKey(value)) {
            throw new IllegalArgumentException();
        }

        return valueToOptionMap.get(value);
    }
}
