package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;

public class SPacketEntity extends WrappedPacket {

    public SPacketEntity(PacketPlayOutEntity instance) {
        super(instance, PacketPlayOutEntity.class);
    }

    public int getEntityId() {
        return getField("a");
    }

    public byte getX() {
        return getField("b");
    }

    public byte getY() {
        return getField("c");
    }

    public byte getZ() {
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

    public boolean isRotation() {
        return getField("h");
    }
}
