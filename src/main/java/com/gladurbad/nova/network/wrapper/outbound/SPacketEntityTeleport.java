package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;

public class SPacketEntityTeleport extends WrappedPacket {

    public SPacketEntityTeleport(PacketPlayOutEntityTeleport instance) {
        super(instance, PacketPlayOutEntityTeleport.class);
    }

    public int getEntityId() {
        return getField("a");
    }

    public int getX() {
        return getField("b");
    }

    public int getY() {
        return getField("c");
    }

    public int getZ() {
        return getField("d");
    }

    public byte getYaw() {
        return getField("e");
    }

    public byte getPitch() {
        return getField("f");
    }

    public boolean isOnGround() {
        return getField("g");
    }
}
