package com.gladurbad.nova.check.impl.aim;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.RotationHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;

public class AimA extends Check implements RotationHandler {

    public AimA(PlayerData data) {
        super(data, "Aim (A)");
    }

    private final Buffer buffer = new Buffer(10);
    private Float lastDeltaPitch;
    private Float lastDeltaYaw;

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the rotation deltas.
        float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        // Make sure the last rotation deltas are not null, else the calculations can be wrong.
        if (lastDeltaPitch != null && lastDeltaYaw != null) {
            // Calculate the acceleration yaw and pitch.
            float accelerationYaw = Math.abs(deltaYaw - lastDeltaYaw);
            float accelerationPitch = Math.abs(deltaPitch - lastDeltaPitch);

            // Acceleration was low, player could have been using cinematic camera.
            if (accelerationYaw < 0.1 || accelerationPitch < 0.1) return;

            // Player is moving their mouse too slowly or quickly, leading to possible cinematic cam use or compounded arithmetic error.
            if (deltaYaw < 0.35 || deltaPitch < 0.35 || deltaYaw > 12.5 || deltaPitch > 12.5) return;

            /*
             * The concept of the resolution check (fancy way of saying GCD check) is that the client rotates on a fixed grid.
             * The client polls the mouse deltas, represented as integers, and creates the rotation yaw and pitch based on
             * factoring in the in-game sensitivity through a simple formula. Because of this, we can make a check for the GCD.
             *
             * Some kill-aura/aim-bot modules do not follow a grid system, so their GCD is going to be below the threshold of
             * 0.007, which appears to be the minimum GCD on yawn sensitivity.
             *
             * Of course, a buffer is used, firstly because this anti-cheat was developed by yours truly, and secondly
             * because of the potential for error in this calculation. Not only is the GCD method we are using not fully
             * accurate, but the client compounds arithmetic errors per frame as rotations are updated and sent to the
             * server only every tick. Because of this, players with higher frame-rates have a higher potential of false
             * positives.
             */
            double resolution = MathUtil.gcd(deltaPitch, lastDeltaPitch);

            if (resolution < 0.007) {
                if (buffer.add() > 8) fail();
            } else {
                buffer.reduce(0.25);
            }
        }

        // Set the last rotation deltas.
        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;
    }
}
