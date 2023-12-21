package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.outbound.SPacketPosition;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.google.common.collect.Lists;
import org.bukkit.util.Vector;

import java.util.Deque;

public class PositionTracker extends Tracker implements PacketProcessor {

    public PositionTracker(PlayerData data) {
        super(data);
    }

    private final Deque<Vector> teleports = Lists.newLinkedList();
    private PlayerLocation from, to;
    private double lastDistance;
    private int lastOffset, lastTeleport;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            CPacketFlying wrapper = (CPacketFlying) packet;

            // I know what you're going to say, shut the fuck up.
            if (to != null) from = to.copy();
            if (to == null) to = new PlayerLocation(wrapper.getX(), wrapper.getY(), wrapper.getZ(),
                    wrapper.getYaw(), wrapper.getPitch(), wrapper.isOnGround());
            else {
                if (wrapper.isPosition()) {
                    to.setX(wrapper.getX());
                    to.setY(wrapper.getY());
                    to.setZ(wrapper.getZ());
                }

                if (wrapper.isRotation()) {
                    to.setYaw(wrapper.getYaw());
                    to.setPitch(wrapper.getPitch());
                }

                to.setOnGround(wrapper.isOnGround());
                to.setTimestamp(System.currentTimeMillis());
            }

            if (from != null) {
                double distance = to.distanceSquared(from);

                if (lastDistance > 0 && distance == 0 && !wrapper.isPosition()) lastOffset = data.getTick();

                Vector teleport = teleports.peek();

                // Used to confirm if players received teleports. Yes, flags exist, they aren't used in multiplayer.
                if (teleport != null && teleport.distanceSquared(new Vector(to.getX(), to.getY(), to.getZ())) == 0) {
                    teleports.poll();
                    lastTeleport = data.getTick();
                }

                // I hate this as much as you do.
                data.getTracker(CollisionTracker.class).update(to);
                data.getTracker(MouseTracker.class).update(to, from);
                data.getTracker(AimTracker.class).update(to, from);

                // Run movement checks.
                for (PositionHandler check : data.getCheckManager().getPositionChecks())
                    check.handle(to, from);

                lastDistance = distance;
            }
        } else if (packet instanceof SPacketPosition) {
            SPacketPosition wrapper = (SPacketPosition) packet;
            teleports.add(new Vector(wrapper.getX(), wrapper.getY(), wrapper.getZ()));
        }
    }

    // But glad!! You have lombok installed!! Yes, I would love to insert lombok's massive dick deeper into my throat!!
    public PlayerLocation getFrom() {
        return from;
    }

    public PlayerLocation getTo() {
        return to;
    }

    // Basically a shitty fix for the 0.03 condition in the client, just increase the threshold for some checks.
    public boolean isOffsetMotion() {
        return data.getTick() - lastOffset < 4;
    }

    // Different from isOffsetMotion, this is mainly used for reach checks, the former is used for motion checks which use movement deltas which are affected for longer.
    public boolean isOffsetPosition() {
        return data.getTick() == lastOffset;
    }

    public boolean isTeleporting() {
        return data.getTick() - lastTeleport == 0;
    }
}
