package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;

public class CPacketFlying extends WrappedPacket {

    public CPacketFlying(PacketPlayInFlying instance) {
        super(instance, PacketPlayInFlying.class);
    }

    public double getX() {
        return getField("x");
    }

    public double getY() {
        return getField("y");
    }

    public double getZ() {
        return getField("z");
    }

    public float getYaw() {
        return getField("yaw");
    }

    public float getPitch() {
        return getField("pitch");
    }

    public boolean isOnGround() {
        return getField("f");
    }

    public boolean isPosition() {
        return getField("hasPos");
    }

    public boolean isRotation() {
        return getField("hasLook");
    }
}
