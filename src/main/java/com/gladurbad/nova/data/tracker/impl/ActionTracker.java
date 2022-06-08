package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.EventProcessor;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketEntityAction;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public class ActionTracker extends Tracker implements PacketProcessor, EventProcessor {

    public ActionTracker(PlayerData data) {
        super(data);
    }

    private boolean sprinting, sneaking, digging;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof CPacketEntityAction) {
            CPacketEntityAction wrapper = (CPacketEntityAction) packet;

            // I think this isn't fully correct, oh well.
            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    sprinting = true;
                    break;
                case STOP_SPRINTING:
                    sprinting = false;
                    break;
                case START_SNEAKING:
                    sneaking = true;
                    break;
                case STOP_SNEAKING:
                    sneaking = false;
            }
        } else if (packet instanceof CPacketFlying) digging = false;
    }

    @Override
    public void process(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent interact = (PlayerInteractEvent) event;

            // Using bukkit's event since it does the raycast for us, and we're running on main thread anyways.
            if (interact.getAction() == Action.LEFT_CLICK_BLOCK) {
                digging = true;
            }
        }
    }
}
