package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public class SPacketSpawnLivingEntity extends WrappedPacket {
    public SPacketSpawnLivingEntity(PacketPlayOutSpawnEntityLiving instance) {
        super(instance, PacketPlayOutSpawnEntityLiving.class);
    }

    public int getEntityId() {
        return getField("a");
    }

    public int getX() {
        return getField("c");
    }

    public int getY() {
        return getField("d");
    }

    public int getZ() {
        return getField("e");
    }
}
