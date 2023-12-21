package com.gladurbad.nova.network.manager;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.*;
import com.gladurbad.nova.network.wrapper.outbound.*;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class PacketManager {
    protected final PlayerData data;
    protected final EntityPlayer nmsPlayer;
    protected final ExecutorService executor;
    protected final List<Consumer<WrappedPacket>> listeners = new ArrayList<>();

    public PacketManager(PlayerData data) {
        this.data = data;
        this.nmsPlayer = ((CraftPlayer) data.getPlayer()).getHandle();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executor.execute(() -> {
            new ModifiedPlayerConnection(nmsPlayer.server,
                    nmsPlayer.playerConnection.networkManager,
                    nmsPlayer,
                    this);
        });
    }

    public void sendTransaction(int windowId, short actionNumber, boolean accepted) {
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutTransaction(windowId, actionNumber, accepted));
    }
    
    public void addListener(Consumer<WrappedPacket> listener) {
        listeners.add(listener);
    }
    
    private void handle(WrappedPacket packet) {
        listeners.forEach(listener -> listener.accept(packet));
    }

    private static class ModifiedPlayerConnection extends PlayerConnection {

        private final PacketManager packetManager;

        public ModifiedPlayerConnection(MinecraftServer server,
                                        NetworkManager networkManager,
                                        EntityPlayer nmsPlayer,
                                        PacketManager packetManager) {

            super(server, networkManager, nmsPlayer);
            this.packetManager = packetManager;
        }

        @Override
        public void a(PacketPlayInSteerVehicle packet) {
            super.a(packet);
            packetManager.handle(new CPacketInput(packet));
        }

        @Override
        public void a(PacketPlayInBlockDig packet) {
            super.a(packet);
            packetManager.handle(new CPacketBlockDig(packet));
        }

        @Override
        public void a(PacketPlayInBlockPlace packet) {
            super.a(packet);
            packetManager.handle(new CPacketBlockPlace(packet));
        }

        @Override
        public void a(PacketPlayInHeldItemSlot packet) {
            super.a(packet);
            packetManager.handle(new CPacketHeldItemSlot(packet));
        }

        @Override
        public void a(PacketPlayInEntityAction packet) {
            super.a(packet);
            packetManager.handle(new CPacketEntityAction(packet));
        }

        @Override
        public void a(PacketPlayInUseEntity packet) {
            super.a(packet);
            packetManager.handle(new CPacketUseEntity(packet));
        }

        @Override
        public void a(PacketPlayInTransaction packet) {
            super.a(packet);
            packetManager.handle(new CPacketTransaction(packet));
        }

        @Override
        public void a(PacketPlayInKeepAlive packet) {
            super.a(packet);
            packetManager.handle(new CPacketKeepAlive(packet));
        }

        @Override
        public void a(PacketPlayInAbilities packet) {
            super.a(packet);
            packetManager.handle(new CPacketAbilities(packet));
        }

        @Override
        public void a(PacketPlayInChat packet) {
            super.a(packet);
            packetManager.handle(new CPacketChat(packet));
        }

        @Override
        public void a(PacketPlayInFlying packet) {
            super.a(packet);
            packetManager.handle(new CPacketFlying(packet));
        }

        @Override
        public void a(PacketPlayInArmAnimation packet) {
            super.a(packet);
            packetManager.handle(new CPacketAnimation(packet));
        }

        @Override
        public void sendPacket(Packet packet) {
            super.sendPacket(packet);

            /*
             * Object oriented programming? Polymorphism? Inheritance? OOP principles? What's that?
             * I learned how to program by the one and only YandereDev, the best if-else chainer to graze this Earth.
             */
            if (packet instanceof PacketPlayOutTransaction) {
                packetManager.handle(new SPacketTransaction((PacketPlayOutTransaction) packet));
            } else if (packet instanceof PacketPlayOutEntity) {
                packetManager.handle(new SPacketEntity((PacketPlayOutEntity) packet));
            } else if (packet instanceof PacketPlayOutEntityTeleport) {
                packetManager.handle(new SPacketEntityTeleport((PacketPlayOutEntityTeleport) packet));
            } else if (packet instanceof PacketPlayOutNamedEntitySpawn) {
                packetManager.handle(new SPacketSpawnPlayer((PacketPlayOutNamedEntitySpawn) packet));
            } else if (packet instanceof PacketPlayOutPosition) {
                packetManager.handle(new SPacketPosition((PacketPlayOutPosition) packet));
            } else if (packet instanceof PacketPlayOutEntityVelocity) {
                packetManager.handle(new SPacketEntityVelocity((PacketPlayOutEntityVelocity) packet));
            } else if (packet instanceof PacketPlayOutAbilities) {
                packetManager.handle(new SPacketAbilities((PacketPlayOutAbilities) packet));
            } else if (packet instanceof PacketPlayOutEntityDestroy) {
                packetManager.handle(new SPacketEntityDestroy((PacketPlayOutEntityDestroy) packet));
            } else if (packet instanceof PacketPlayOutEntityEffect) {
                packetManager.handle(new SPacketEntityEffect((PacketPlayOutEntityEffect) packet));
            } else if (packet instanceof PacketPlayOutHeldItemSlot) {
                packetManager.handle(new SPacketHeldItemSlot((PacketPlayOutHeldItemSlot) packet));
            } else if (packet instanceof PacketPlayOutKeepAlive) {
                packetManager.handle(new SPacketKeepAlive((PacketPlayOutKeepAlive) packet));
            } else if (packet instanceof PacketPlayOutRemoveEntityEffect) {
                packetManager.handle(new SPacketRemoveEntityEffect((PacketPlayOutRemoveEntityEffect) packet));
            } else if (packet instanceof PacketPlayOutSpawnEntityLiving) {
                packetManager.handle(new SPacketSpawnLivingEntity((PacketPlayOutSpawnEntityLiving) packet));
            }
        }
    }
}
