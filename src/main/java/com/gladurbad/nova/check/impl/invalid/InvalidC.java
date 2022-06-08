package com.gladurbad.nova.check.impl.invalid;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.inbound.CPacketInput;
import com.gladurbad.nova.network.wrapper.outbound.SPacketPosition;
import com.gladurbad.nova.util.location.PlayerLocation;

public class InvalidC extends Check implements PacketHandler {

    public InvalidC(PlayerData data) {
        super(data, "Invalid (C)");
    }

    private int exempts;

    @Override
    public void handle(WrappedPacket packet) {
        // Make sure the player is rotating and that the position tracker had enough time to process.
        if (packet instanceof CPacketFlying && ((CPacketFlying) packet).isRotation() && data.getTick() > 4) {
            PlayerLocation to = positionTracker.getTo();
            PlayerLocation from = positionTracker.getFrom();

            // Player cannot send a rotation packet with the same values.
            if (to.getYaw() == from.getYaw() && to.getPitch() == from.getPitch()) {
                if (exempts == 0) fail();

                exempts = Math.max(0, exempts - 1);
            }
        } else if (packet instanceof SPacketPosition || packet instanceof CPacketInput) {
            // Simple way of lag-proof exempting.
            ++exempts;
        }
    }
}
