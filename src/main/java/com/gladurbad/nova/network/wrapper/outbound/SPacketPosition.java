package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;

import java.util.Set;

public class SPacketPosition extends WrappedPacket {

    public SPacketPosition(PacketPlayOutPosition instance) {
        super(instance, PacketPlayOutPosition.class);
    }

    public double getX() {
        return getField("a");
    }

    public double getY() {
        return getField("b");
    }

    public double getZ() {
        return getField("c");
    }

    public float getYaw() {
        return getField("d");
    }

    public float getPitch() {
        return getField("e");
    }

    public Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> getFlags() {
        return getField("f");
    }
}
