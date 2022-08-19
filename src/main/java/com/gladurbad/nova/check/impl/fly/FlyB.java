package com.gladurbad.nova.check.impl.fly;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;

public class FlyB extends Check implements PositionHandler {

    public FlyB(PlayerData data) {
        super(data, "Fly (B)");
    }

    private final Buffer buffer = new Buffer(5);
    private Double lastDeltaY;
    private Boolean lastMotionExempt;

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the vertical motion.
        double deltaY = to.getY() - from.getY();

        /*
         * These cases in particular modify vertical motion while still being off the ground.
         * Obviously we did not calculate that with our check, so we just exempt this tick and the last tick as well
         * since we need two ticks of information to estimate the next motion.
         *
         * The last condition is a simple way to exempt chunk motion, this happens when the chunk is not loaded
         * clientside.
         */
        boolean motionExempt = collisionTracker.isUnderBlock()
                || collisionTracker.isLiquid()
                || collisionTracker.isLadder()
                || collisionTracker.isSlime()
                || collisionTracker.isWeb()
                || positionTracker.isTeleporting()
                || Math.abs(deltaY + 0.098) < 0.001
                || data.getTick() < 120;


        // Make sure the last statuses are not null to run the check.
        if (lastDeltaY != null && lastMotionExempt != null) {
            // Create a rudimentary estimation of the current vertical motion based on the last.
            double estimation = lastDeltaY;

            estimation -= 0.08D;
            estimation *= 0.98F;

            if (Math.abs(estimation) < 0.005D) estimation = 0.0;

            // Create the offset from the actual motion versus the predicted motion.
            double offset = Math.abs(deltaY - estimation);

            // When the player's position is offset we can increase the offset threshold to prevent false positives.
            double threshold = positionTracker.isOffsetPosition() ? 0.05 : 1e-10;

            // Exempt cases where the motion was modified while being off the ground or if the player has not loaded in yet.
            boolean exempt = motionExempt || lastMotionExempt || data.getTick() < 40;

            /*
             * The player's estimated vertical motion did not match with their actual motion, flag.
             * Keep in mind this does not factor in collisions, so we are exempting particular collision cases such as
             * ladders, liquids, slimes, and being under blocks. This check is similar to what a lot of public anti-cheat
             * systems will have, and it works quite well given that your exempts are not exploitable. In the case of this
             * check, they are.
             *
             * Another thing to keep in mind is that we are using the client ground status to prevent issues related
             * to ghost blocks. Fly A is a ground-spoof check that prevents some, but not all bypasses related to
             * this check caused by ground-spoofing.
             */
            if (offset > threshold && !to.isOnGround() && !from.isOnGround() && !exempt) {
                // Using a buffer since this check is not perfect.
                if (buffer.add() > 2) fail();
            } else {
                buffer.reduce(0.05);
            }
        }

        lastDeltaY = deltaY;
        lastMotionExempt = motionExempt;
    }
}
