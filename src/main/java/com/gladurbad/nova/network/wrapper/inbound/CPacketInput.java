package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;

public class CPacketInput extends WrappedPacket {

    public CPacketInput(PacketPlayInSteerVehicle instance) {
        super(instance, PacketPlayInSteerVehicle.class);
    }

    public float getMoveStrafing() {
        return getField("a");
    }

    public float getMoveForward() {
        return getField("b");
    }

    public boolean getJumping() {
        return getField("c");
    }

    public boolean getSneaking() {
        return getField("d");
    }
}
