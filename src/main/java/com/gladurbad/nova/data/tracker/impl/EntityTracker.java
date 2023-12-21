package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.Nova;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.data.tracker.handler.PostPacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.outbound.*;
import com.gladurbad.nova.util.reach.ReachEntity;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.HashMap;
import java.util.Map;

public class EntityTracker extends Tracker implements PacketProcessor, PostPacketProcessor {

    public EntityTracker(PlayerData data) {
        super(data);
    }

    private final Map<Integer, ReachEntity> entityMap = new HashMap<>();

    @Override
    public void process(WrappedPacket packet) {
        if (packet instanceof SPacketEntity) {
            SPacketEntity wrapper = (SPacketEntity) packet;
            ReachEntity entity = entityMap.get(wrapper.getEntityId());

            if (entity == null) return;

            // Confirm using transactions for lag-proofing.
            data.getTracker(PingTracker.class).confirm(() -> {
                entity.serverPosX += wrapper.getX();
                entity.serverPosY += wrapper.getY();
                entity.serverPosZ += wrapper.getZ();

                double d0 = (double) entity.serverPosX / 32D;
                double d1 = (double) entity.serverPosY / 32D;
                double d2 = (double) entity.serverPosZ / 32D;

                entity.setPositionAndRotation2(d0, d1, d2);
            });
        } else if (packet instanceof SPacketEntityTeleport) {
            SPacketEntityTeleport wrapper = (SPacketEntityTeleport) packet;
            ReachEntity entity = entityMap.get(wrapper.getEntityId());

            if (entity == null) return;

            data.getTracker(PingTracker.class).confirm(() -> {
                // Code is directly from the client (not really, no).
                entity.serverPosX = wrapper.getX();
                entity.serverPosY = wrapper.getY();
                entity.serverPosZ = wrapper.getZ();

                double d0 = (double) entity.serverPosX / 32.0D;
                double d1 = (double) entity.serverPosY / 32.0D;
                double d2 = (double) entity.serverPosZ / 32.0D;

                if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D) {
                    entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ);
                } else {
                    entity.setPositionAndRotation2(d0, d1, d2);
                }
            });
        } else if (packet instanceof SPacketSpawnPlayer) {
            SPacketSpawnPlayer wrapper = (SPacketSpawnPlayer) packet;

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) return;

            CraftWorld craftWorld = (CraftWorld) data.getPlayer().getWorld();
            net.minecraft.server.v1_8_R3.World nmsWorld = craftWorld.getHandle();
            net.minecraft.server.v1_8_R3.Entity nmsEntity = nmsWorld.a(wrapper.getEntityId());

            if (!entityMap.containsKey(wrapper.getEntityId())) {
                // Runs later cause server is fucked i don't know whatever.
                Bukkit.getScheduler().runTaskLater(Nova.getPlugin(), () -> {
                    ReachEntity reachEntity = new ReachEntity(
                            wrapper.getEntityId(),
                            wrapper.getX(),
                            wrapper.getY(),
                            wrapper.getZ(),
                            0.6F,
                            1.8F);

                    entityMap.put(wrapper.getEntityId(), reachEntity);
                }, 3);
            }
        } else if (packet instanceof SPacketSpawnLivingEntity) {
            SPacketSpawnLivingEntity wrapper = (SPacketSpawnLivingEntity) packet;

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) return;

            CraftWorld craftWorld = (CraftWorld) data.getPlayer().getWorld();
            net.minecraft.server.v1_8_R3.World nmsWorld = craftWorld.getHandle();

            if (!entityMap.containsKey(wrapper.getEntityId())) {
                // Runs later cause server is fucked i don't know whatever.
                Bukkit.getScheduler().runTaskLater(Nova.getPlugin(), () -> {
                    net.minecraft.server.v1_8_R3.Entity nmsEntity = nmsWorld.a(wrapper.getEntityId());
                    ReachEntity reachEntity = new ReachEntity(
                            wrapper.getEntityId(),
                            wrapper.getX(),
                            wrapper.getY(),
                            wrapper.getZ(),
                            nmsEntity.width,
                            nmsEntity.length);

                    entityMap.put(wrapper.getEntityId(), reachEntity);
                }, 3);
            }
        } else if (packet instanceof SPacketEntityDestroy) {
            SPacketEntityDestroy wrapper = (SPacketEntityDestroy) packet;

            for (int id : wrapper.getEntities()) entityMap.remove(id);
        }
    }

    @Override
    public void postProcess(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            // Interpolation, super important for the reach check.
            entityMap.values().forEach(ReachEntity::onLivingUpdate);
        }
    }

    public ReachEntity get(int id) {
        return entityMap.get(id);
    }
}
