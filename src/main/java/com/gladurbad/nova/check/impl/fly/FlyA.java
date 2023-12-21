package com.gladurbad.nova.check.impl.fly;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;

public class FlyA extends Check implements PositionHandler {

    public FlyA(PlayerData data) {
        super(data, "Fly (A)");
    }

    private final Buffer buffer = new Buffer(5);

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the vertical motion delta.
        double deltaY = to.getY() - from.getY();

        // Client claimed ground status (can be spoofed).
        boolean clientGround = to.isOnGround();

        // Ground status as calculated by the anti-cheat.
        boolean serverGround = collisionTracker.isOnGround();
        
        // Exempt cases where the ground status is modified or when players can step.
        if (collisionTracker.isSlime() || collisionTracker.isAbnormal() || data.getTick() < 120) return;

        /*
         * We use the motion to make this check a tad more accurate.
         * It's still not perfect, but it is better than just directly comparing the statuses alone.
         * "Spoof" means the client claimed to be on the ground though they were not actually on the ground.
         * "Anti" means the client claimed to not be on the ground though they were on the ground.
         */

        if (clientGround != serverGround) {
            // Use a buffer, the GladUrBad classic.
            if (buffer.add() > 3) fail();
        } else {
            buffer.reduce(0.025);
        }
    }
}
