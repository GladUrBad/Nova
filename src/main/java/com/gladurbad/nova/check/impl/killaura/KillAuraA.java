package com.gladurbad.nova.check.impl.killaura;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class KillAuraA extends Check implements PacketHandler, PositionHandler {

    public KillAuraA(PlayerData data) {
        super(data, "KillAura (A)");
    }

    private final Buffer buffer = new Buffer(10);
    private Double lastDistance;
    private int lastSlowdown;

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketUseEntity) {
            CPacketUseEntity wrapper = (CPacketUseEntity) packet;

            // Get the Entity using the NMS map.
            CraftWorld craftWorld = (CraftWorld) data.getPlayer().getWorld();
            net.minecraft.server.v1_8_R3.World nmsWorld = craftWorld.getHandle();
            net.minecraft.server.v1_8_R3.Entity nmsEntity = nmsWorld.a(wrapper.getEntityId());
            Entity entity = nmsEntity.getBukkitEntity();

            // Motion reduction applies when the player is sprinting or holding a knockback enchanted item and hitting a player.
            boolean applies = actionTracker.isSprinting()
                    || data.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 0;

            if (entity instanceof Player && applies) {
                lastSlowdown = data.getTick();
            }
        }
    }

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the horizontal distance.
        double distance = to.horizontalDistance(from);

        // Check if the slowdown applies.
        boolean applies = data.getTick() - lastSlowdown < 3;

        /*
         * With this check we are checking if the player's motion reduces properly when the reduction applies.
         * This is called a keep-sprint check. It's not a perfect way of doing it, but it will detect most of the
         * keep-sprint modules in clients.
         *
         * This check does false when players click twice in one tick.
         */
        if (applies && lastDistance != null) {
            double acceleration = Math.abs(distance - lastDistance);

            if (acceleration < 0.001) {
                if (buffer.add() > 6) fail();
            } else {
                buffer.reduce();
            }
        }

        // Set the last horizontal distance.
        lastDistance = distance;
    }
}
