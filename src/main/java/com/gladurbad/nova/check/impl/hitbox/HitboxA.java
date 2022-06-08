package com.gladurbad.nova.check.impl.hitbox;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.reach.ReachEntity;
import org.bukkit.util.Vector;

public class HitboxA extends Check implements PacketHandler {

    public HitboxA(PlayerData data) {
        super(data, "Hitbox (A)");
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

            /*
             * Create a direction vector of the attacker's eye based on their yaw value. We aren't really factoring in
             * pitch to the intersection test since it doesn't really matter too much (though you should add it for added detection).
             *
             * Below is a simple ray to axis-aligned rectangle collision test. We create a unit direction vector based
             * on simple trig math from the yaw and pitch rotations. Treating the rectangle as two pairs of parallel lines,
             * we get the point of the ray on each line. We can then use these points to basically check if the path
             * they make crosses through the rectangle with some simple math.
             */
            Vector direction = new Vector(-Math.sin(Math.toRadians(to.getYaw())), 0, Math.cos(Math.toRadians(to.getYaw())));

            double inverseDirectionX = 1.0 / direction.getX();
            double inverseDirectionZ = 1.0 / direction.getZ();

            boolean inverseX = inverseDirectionX < 0.0;
            boolean inverseZ = inverseDirectionZ < 0.0;

            double minX = ((inverseX ? boundingBox.getMaxX() : boundingBox.getMinX()) - from.getX()) * inverseDirectionX;
            double maxX = ((inverseX ? boundingBox.getMinX() : boundingBox.getMaxX()) - from.getX()) * inverseDirectionX;

            double minZ = ((inverseZ ? boundingBox.getMaxZ() : boundingBox.getMinZ()) - from.getZ()) * inverseDirectionZ;
            double maxZ = ((inverseZ ? boundingBox.getMinZ() : boundingBox.getMaxZ()) - from.getZ()) * inverseDirectionZ;

            boolean intersects = maxZ > minX && minZ < maxX;

            if (!intersects) {
                if (buffer.add() > 2) fail();
            } else {
                buffer.reduce(0.01);
            }

            // Set the target to null.
            target = null;
        }
    }
}
