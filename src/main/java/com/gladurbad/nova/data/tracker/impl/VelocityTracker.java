package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.data.tracker.handler.PostPacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.outbound.SPacketEntityVelocity;
import com.gladurbad.nova.util.math.MathUtil;
import org.bukkit.util.Vector;

public class VelocityTracker extends Tracker implements PacketProcessor, PostPacketProcessor {

    public VelocityTracker(PlayerData data) {
        super(data);
    }

    private double horizontalVelocity, verticalVelocity;
    private int exemptedTicks;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity wrapper = (SPacketEntityVelocity) packet;

            // Make sure the ids match since velocity packets are sent for every entity to the client.
            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                horizontalVelocity = MathUtil.hypot(wrapper.getX(), wrapper.getZ());
                verticalVelocity = wrapper.getY();

                /*
                 * This is a simple way to exempt velocity from certain movement checks you might make
                 * that are so terrible that you'd have to use an exempt system like this. An example of a check that
                 * might use this is a Verus-type speed check where it's basically already terrible, and then you exempt it
                 * like this making it even more terrible. Then you sell it for hundreds of dollars anyways.
                 */
                double velocity = Math.abs(horizontalVelocity);

                while (velocity > 0.005 && exemptedTicks < 50) {
                    velocity *= 0.65;
                    ++exemptedTicks;
                }
            }
        } else if (packet instanceof CPacketFlying) {
            exemptedTicks = Math.max(0, exemptedTicks - 1);
        }
    }

    @Override
    public void postProcess(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            horizontalVelocity = 0;
            verticalVelocity = 0;
        }
    }

    public double getHorizontalVelocity() {
        return horizontalVelocity;
    }

    public double getVerticalVelocity() {
        return verticalVelocity;
    }

    public boolean isExempted() {
        return exemptedTicks > 0;
    }
}
