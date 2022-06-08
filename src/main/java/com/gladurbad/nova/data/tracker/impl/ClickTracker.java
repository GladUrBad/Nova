package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.check.handler.SwingHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketAnimation;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.util.swing.ClickAnalysis;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Deque;

@Getter
public class ClickTracker extends Tracker implements PacketProcessor {

    public ClickTracker(PlayerData data) {
        super(data);
    }

    private final Deque<Integer> samples = Lists.newLinkedList();
    private int ticks;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof CPacketAnimation) {
            samples.add(ticks);
            ticks = 0;

            if (samples.size() == 100) {
                ClickAnalysis analysis = new ClickAnalysis(samples);

                data.getCheckManager().getChecks().stream()
                        .filter(SwingHandler.class::isInstance)
                        .map(SwingHandler.class::cast)
                        .forEach(handler -> handler.handle(analysis));

                samples.clear();
            }
        } else if (packet instanceof CPacketFlying) {
            ++ticks;
        }
    }
}
