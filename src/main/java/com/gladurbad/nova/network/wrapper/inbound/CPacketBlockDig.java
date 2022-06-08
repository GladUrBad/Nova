package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;

public class CPacketBlockDig extends WrappedPacket {

    public CPacketBlockDig(PacketPlayInBlockDig instance) {
        super(instance, PacketPlayInBlockDig.class);
    }

    public BlockPosition getBlockPosition() {
        return getField("a");
    }

    public EnumDirection getDirection() {
        return getField("b");
    }

    public PacketPlayInBlockDig.EnumPlayerDigType getType() {
        return getField("c");
    }
}
