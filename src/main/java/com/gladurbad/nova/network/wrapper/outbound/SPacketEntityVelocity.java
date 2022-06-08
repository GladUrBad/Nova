package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;

public class SPacketEntityVelocity extends WrappedPacket {

    public SPacketEntityVelocity(PacketPlayOutEntityVelocity instance) {
        super(instance, PacketPlayOutEntityVelocity.class);
    }

    public int getEntityId() {
        return getField("a");
    }

    public double getX() {
        return ((int) getField("b")) / 8000.0;
    }

    public double getY() {
        return ((int) getField("c")) / 8000.0;
    }

    public double getZ() {
        return ((int) getField("d")) / 8000.0;
    }
}
