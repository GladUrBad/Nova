package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketTransaction;
import com.gladurbad.nova.network.wrapper.outbound.SPacketTransaction;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PingTracker extends Tracker implements PacketProcessor {

    public PingTracker(PlayerData data) {
        super(data);
    }

    private final Map<Short, Long> transactionMap = new HashMap<>();
    private final Map<Short, Runnable> actionMap = new HashMap<>();
    private long ping;
    private short tick;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof CPacketTransaction) {
            CPacketTransaction wrapper = (CPacketTransaction) packet;
            short id = wrapper.getActionNumber();

            if (transactionMap.containsKey(id)) {
                // Real time ping calculation.
                ping = packet.getTime() - transactionMap.remove(id);
                actionMap.remove(id).run();
            }
        } else if (packet instanceof SPacketTransaction) {
            SPacketTransaction wrapper = (SPacketTransaction) packet;
            transactionMap.put(wrapper.getActionNumber(), wrapper.getTime());
        }
    }

    public void confirm(Runnable runnable) {
        // Sending hundreds of transactions per player per tick? Sounds like a fantastic idea.
        data.getPacketManager().sendTransaction(0, tick, false);
        actionMap.put(tick, runnable);
        if (--tick == Short.MIN_VALUE) tick = 0;
    }
}
