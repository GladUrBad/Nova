package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.check.handler.RotationHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;
import com.gladurbad.nova.util.math.Sensitivity;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MouseTracker extends Tracker {

    public MouseTracker(PlayerData data) {
        super(data);
    }

    private final List<Float> samples = new ArrayList<>();
    private float resolution;
    private boolean processed;

    public void update(PlayerLocation to, PlayerLocation from) {
        // Get the rotation deltas.
        float deltaYaw = to.getYaw() - from.getYaw();
        float deltaPitch = to.getPitch() - from.getPitch();

        // Make sure the player is rotating.
        if (to.getYaw() != from.getYaw() || to.getPitch() != from.getPitch()) {
            Float gridResolutionYaw = null,
                    gridResolutionPitch = null,
                    frameErrorYaw = null,
                    frameErrorPitch = null;

            /*
             * Okay so what we're doing here is finding the constant which is the most fitting to the actual client sensitivity.
             * Yes, we are brute-forcing 200 values, every tick, per player. I don't care, but your CPU might.
             */
            for (float resolution : Sensitivity.CONSTANTS) {
                // This is a better way of checking error than just the modulo itself.
                float errorYaw = deltaYaw % resolution;
                errorYaw = Math.min(errorYaw, Math.abs(errorYaw - resolution));

                float errorPitch = deltaPitch % resolution;
                errorPitch = Math.min(errorPitch, Math.abs(errorPitch - resolution));

                if (frameErrorYaw == null || errorYaw < frameErrorYaw) {
                    frameErrorYaw = errorYaw;
                    gridResolutionYaw = resolution;
                }

                if (frameErrorPitch == null || errorPitch < frameErrorPitch) {
                    frameErrorPitch = errorPitch;
                    gridResolutionPitch = resolution;
                }
            }

            if (gridResolutionYaw != null) {
                /*
                 * Default to pitch resolution since it's generally less prone to error.
                 * Update: Not really you fucking cunt your calculation is still dogshit regardless.
                 */
                samples.add(gridResolutionPitch);

                /*
                 * Take a sample of 20, this means our calculation will be off by up to 1 second from the client setting.
                 * Update: Your calculation is always off because you have the brain of a two-year old and don't know basic math.
                 */
                if (samples.size() == 40) {
                    // Get the sample with the highest frequencies as it's most likely to be accurate.
                    resolution = MathUtil.getMode(samples).floatValue();
                    // Set processed to true so that we can actually run the checks.
                    processed = true;
                    // Clear the samples.
                    samples.clear();
                }
            }

            if (processed) {
                data.getCheckManager().getChecks().stream()
                        .filter(RotationHandler.class::isInstance)
                        .map(RotationHandler.class::cast)
                        .forEach(handler -> handler.handle(to, from));
            }
        }
    }
}
