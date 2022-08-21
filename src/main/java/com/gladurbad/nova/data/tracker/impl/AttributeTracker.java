package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.outbound.SPacketAbilities;
import com.gladurbad.nova.network.wrapper.outbound.SPacketEntityEffect;
import com.gladurbad.nova.network.wrapper.outbound.SPacketRemoveEntityEffect;
import lombok.Getter;

@Getter
public class AttributeTracker extends Tracker implements PacketProcessor {

    public AttributeTracker(PlayerData data) {
        super(data);
    }

    private int jumpModifier, speedModifier, lastAbilityChange;
    private boolean flying;
    private float flySpeed = 0.05F, walkSpeed = 0.1F;

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof SPacketEntityEffect) {
            SPacketEntityEffect wrapper = (SPacketEntityEffect) packet;

            // Make sure ids match since this packet is send for every entity.
            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                int effectId = wrapper.getEffectId();

                // Confirm changes using transactions to prevent falses from lag.
                data.getTracker(PingTracker.class).confirm(() -> {
                    if (effectId == 1) {
                        speedModifier = wrapper.getAmplifier() + 1;
                    } else if (effectId == 8) {
                        jumpModifier = wrapper.getAmplifier()  + 1;
                    }
                });
            }
        } else if (packet instanceof SPacketRemoveEntityEffect) {
            SPacketRemoveEntityEffect wrapper = (SPacketRemoveEntityEffect) packet;

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                int effectId = wrapper.getEffectId();

                data.getTracker(PingTracker.class).confirm(() -> {
                    if (effectId == 1) speedModifier = 0;
                    else if (effectId == 8) jumpModifier = 0;
                });
            }
        } else if (packet instanceof SPacketAbilities) {
            SPacketAbilities wrapper = (SPacketAbilities) packet;

            data.getTracker(PingTracker.class).confirm(() -> {
                if (flySpeed != wrapper.getFlySpeed()) {
                    lastAbilityChange = data.getTick();
                }

                flySpeed = wrapper.getFlySpeed();

                if (walkSpeed != wrapper.getWalkSpeed()) {
                    lastAbilityChange = data.getTick();
                }

                walkSpeed = wrapper.getWalkSpeed();

                if (flying != wrapper.isFlying() || flying != wrapper.allowsFlying()) {
                    lastAbilityChange = data.getTick();
                }

                flying = wrapper.isFlying() || wrapper.allowsFlying();
            });
        }
    }

    public float getAttributeSpeed() {
        float attributeSpeed = walkSpeed * 1.3F;

        final int speedAmplifier = speedModifier;

        if (speedAmplifier > 0) {
            attributeSpeed *= 1.F + (speedAmplifier * 0.2F);
        }

        return attributeSpeed;
    }
}
