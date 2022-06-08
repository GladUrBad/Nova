package com.gladurbad.nova.util.math;

import java.util.HashSet;
import java.util.Set;

public class Sensitivity {
    public static final Set<Float> CONSTANTS = new HashSet<>();

    static {
        /*
         * What are these random numbers you ask?
         * This is how the sensitivity slider calculates the possible values for the mouse sensitivity.
         * Except instead of getting the raw value we convert it to a grid resolution first so it's easier to use.
         */
        for (int i = 218; i < 368; i++) {
            float value = (float)(i - 222) / (float)(142);

            if (value >= 0.0 && value <= 1.0) {
                float factor = value * 0.6F + 0.2F;
                float constant = factor * factor * factor * 0.15F * 8.0F;

                CONSTANTS.add(constant);
            }
        }
    }
}
