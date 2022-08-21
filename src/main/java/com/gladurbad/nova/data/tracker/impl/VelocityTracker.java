package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.data.tracker.handler.PostPacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.outbound.SPacketEntityVelocity;
import com.gladurbad.nova.util.math.MathUtil;
import lombok.Getter;

@Getter
public class VelocityTracker extends Tracker implements PacketProcessor, PostPacketProcessor {

    public VelocityTracker(PlayerData data) {
        super(data);
    }

    private double x, y, z;
    private boolean confirming;


    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity wrapper = (SPacketEntityVelocity) packet;

            // Make sure the ids match since velocity packets are sent for every entity to the client.
            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                PingTracker pingTracker = data.getTracker(PingTracker.class);

                // Using a "transaction sandwich" to ensure that we can "fix" split transaction.
                pingTracker.confirm(() -> confirming = true);

                // This will have disabler problems if someone manages to find a way to get stuck on the confirming status.
                pingTracker.confirm(() -> {
                    x = wrapper.getX();
                    y = wrapper.getY();
                    z = wrapper.getZ();
                });
            }
        }
    }

    @Override
    public void postProcess(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            x = y = z = 0;
        }
    }
}
