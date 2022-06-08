package com.gladurbad.nova.check.impl.reach;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;
import com.gladurbad.nova.util.reach.ReachEntity;
import org.bukkit.util.Vector;

public class ReachA extends Check implements PacketHandler {

    public ReachA(PlayerData data) {
        super(data, "Reach (A)");
    }

    private final Buffer buffer = new Buffer(5);
    private ReachEntity target;

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketUseEntity) {
            // Get the tracked entity from the entity tracker.
            CPacketUseEntity wrapper = (CPacketUseEntity) packet;
            target = entityTracker.get(wrapper.getEntityId());
        }

        // Wait for the next flying packet since the attack is processed before position is updated in the client.
        else if (packet instanceof CPacketFlying && target != null) {
            // Get the attacker's current and last location.
            PlayerLocation from = positionTracker.getFrom();
            PlayerLocation to = positionTracker.getTo();

            // Get the targets bounding box. This is accurate (mostly) because we replicated client tracking using interpolation and transactions.
            BoundingBox boundingBox = target.getBox().copy();

            // The client expands the hit-box by 0.1 (Only 1.8 and below, but this anti-cheat is only 1.8 anyways).
            boundingBox.expand(0.1F, 0.1F, 0.1F);

            // The player server and client positions are offset, expand by the maximum offset.
            if (positionTracker.isOffsetPosition()) boundingBox.expand(0.03, 0.03, 0.03);

            // Get the minimum distance on the X-axis.
            double distanceX = Math.min(Math.abs(from.getX() - boundingBox.getMinX()),
                    Math.abs(from.getX() - boundingBox.getMaxX()));

            // Get the minimum distance on the Z-axis.
            double distanceZ = Math.min(Math.abs(from.getZ() - boundingBox.getMinZ()),
                    Math.abs(from.getZ() - boundingBox.getMaxZ()));

            // This gets the minimum horizontal distance from a point to a rectangle in two dimensions.
            double distance = MathUtil.hypot(distanceX, distanceZ);

            /*
             * Technically this isn't the right way to do reach calculation. It's not aligned with what the client uses,
             * but it still works fine, I just figured I would leave it out of Nova so that you guys can figure out this
             * on your own. :) Plus, if I gave out the raytrace too, then I can't imagine the number of 10$ MCMarket
             * anti-cheats I would see claiming to have 3.01 reach detection right after I make this public.
             *
             * This simply factors in the pitch to the distance calculation.
             * Thanks, Rowin.
             */
            if (Math.abs(to.getPitch()) != 90F) distance /= Math.cos(Math.toRadians(to.getPitch()));

            if (distance > 3.05) {
                /*
                 * Glad!! If perfect reach check, why buffer. Because I didn't spoon-feed everything to you, you
                 * shameless skidding whore. There's still a lot about this check that needs to be fixed to make it
                 * production ready.
                 *
                 * Things to be fixed:
                 * - Transaction spam, the tracker sends a transaction per entity movement packet, per player. That's a lot
                 * of used bandwidth.
                 * - Distance calculation. It's not perfect at all. Use the raytrace from the client.
                 * - Split transactions. The tracker currently does not handle this, but it doesn't matter that much
                 * as the time between the relative movements and each transaction is insanely small.
                 */
                if (buffer.add() > 1) fail();
            } else {
                buffer.reduce(0.025);
            }

            // Reset the target to null.
            target = null;
        }
    }
}
