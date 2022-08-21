package com.gladurbad.nova.check.impl.velocity;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;

public class VelocityA extends Check implements PositionHandler {

    public VelocityA(PlayerData data) {
        super(data, "Velocity (A)");
    }

    private final Buffer buffer = new Buffer(5);

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the vertical velocity.
        double verticalVelocity = velocityTracker.getY();

        // Check if the player is taking velocity.
        if (verticalVelocity > 0) {
            // Get the vertical motion delta.
            double deltaY = to.getY() - from.getY();

            // Calculate the expected jumping motion.
            double jumpMotion = 0.42F + (double) ((float) attributeTracker.getJumpModifier() * 0.1F);

            /*
             * Exempt cases for this check. In these instances the velocity motion gets over-ridden, instead of
             * handling them we can just exempt them since they are pretty niche bypasses. The player can jump
             * to override their velocity as well, so we exempt that, too.
             */
            boolean exempt = positionTracker.isOffsetMotion()
                    || attributeTracker.isFlying()
                    || collisionTracker.isUnderBlock()
                    || collisionTracker.isSlime()
                    || collisionTracker.isLiquid()
                    || collisionTracker.isLadder()
                    || positionTracker.isTeleporting()
                    || velocityTracker.isConfirming()
                    || Math.abs(deltaY - jumpMotion) < 0.001;

            if (exempt) return;

            // Player vertical velocity was incorrect.
            if (Math.abs(deltaY - verticalVelocity) > 1e-4) {
                // Use a buffer since this check is not perfect.
                if (buffer.add() > 3) fail();
            } else {
                buffer.reduce(0.05);
            }
        }
    }
}
