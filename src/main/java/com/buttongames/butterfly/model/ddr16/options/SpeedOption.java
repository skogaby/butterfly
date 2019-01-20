package com.buttongames.butterfly.model.ddr16.options;

import java.util.HashMap;

/**
 * Enum for the various speed options in-game for DDR A.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public enum SpeedOption {
    X_0_25(0x0),
    X_0_50(0x1),
    X_0_75(0x2),
    X_1_00(0x3),
    X_1_25(0x4),
    X_1_50(0x5),
    X_1_75(0x6),
    X_2_00(0x7),
    X_2_25(0x8),
    X_2_50(0x9),
    X_2_75(0xA),
    X_3_00(0xB),
    X_3_25(0xC),
    X_3_50(0xD),
    X_3_75(0xE),
    X_4_00(0xF),
    X_4_50(0x11),
    X_5_00(0x13),
    X_5_50(0x15),
    X_6_00(0x17),
    X_6_50(0x19),
    X_7_00(0x1B),
    X_7_50(0x1D),
    X_8_00(0x1F);

    private static final HashMap<Integer, SpeedOption> valueToOptionMap = new HashMap<>();

    private final int val;

    static {
        for (SpeedOption opt : SpeedOption.values()) {
            valueToOptionMap.put(opt.getVal(), opt);
        }
    }

    private SpeedOption(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    public static SpeedOption optionForValue(int value) {
        if (!valueToOptionMap.containsKey(value)) {
            throw new IllegalArgumentException();
        }

        return valueToOptionMap.get(value);
    }
}
