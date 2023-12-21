package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.ai.AimClassifierNeuralNetwork;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketChat;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.Vertex;
import com.gladurbad.nova.util.mouse.MouseSnapshot;
import com.gladurbad.nova.util.reach.ReachEntity;
import com.gladurbad.nova.util.reach.ReachUtil;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;

public class AimTracker extends Tracker implements PacketProcessor {

    private ReachEntity target = null;
    private MouseSnapshot snapshot;

    private final AimClassifierNeuralNetwork neuralNetwork;

    private Integer classifier;
    private boolean predicting = false;
    private double lastDeltaYaw, lastDeltaPitch;

    public AimTracker(PlayerData data) {
        super(data);
        this.neuralNetwork = new AimClassifierNeuralNetwork();
    }

    public void update(PlayerLocation to, PlayerLocation from) {
        snapshot = null;

        double deltaYaw = to.getYaw() - from.getYaw();
        double deltaPitch = to.getPitch() - from.getPitch();

        double accelerationYaw = deltaYaw - lastDeltaYaw;
        double accelerationPitch = deltaPitch - lastDeltaPitch;

        snapshot: {
            if (target == null) break snapshot;

            double offsetAngle = handleOffsetAngle(from, target.getBox(), to.getYaw());
            double[] intercepts = handleIntercepts(to, from);

            this.snapshot = new MouseSnapshot.Builder()
                    .setDeltaYaw(deltaYaw)
                    .setDeltaPitch(deltaPitch)
                    .setAccelerationYaw(accelerationYaw)
                    .setAccelerationPitch(accelerationPitch)
                    .setInterceptX(intercepts[0])
                    .setInterceptY(intercepts[1])
                    .setOffsetFromCenter(offsetAngle)
                    .build();
        }

        if (classifier != null) {
            neuralNetwork.trainSnapshot(snapshot, classifier);
        }

        if (predicting && classifier == null) {
            neuralNetwork.predictSnapshot(snapshot);
        }

        if (snapshot != null) {
            data.getCheckManager().getRotationContextChecks().forEach(check -> check.handle(snapshot));
        }

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
    }

    private double handleOffsetAngle(PlayerLocation from, BoundingBox boundingBox, double yaw) {
        double distanceX = boundingBox.posX() - from.getX();
        double distanceZ = boundingBox.posZ() - from.getZ();

        // Generate the yaw based on the distance using simple trig math. Wrap to 180F.
        float generatedYaw =  MathHelper.g((float) (Math.toDegrees(Math.atan2(distanceZ, distanceX)) - 90F));

        // Add the offset from the actual yaw and generated yaw to the sample.
        return Math.abs(yaw - generatedYaw);
    }

    public double[] handleIntercepts(PlayerLocation to, PlayerLocation from) {
        Vertex[] vertices = getVertices();

        float[] yaws = new float[]{Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY};
        float[] pitches = new float[]{Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY};

        for (Vertex vt : vertices) {
            data.getPlayer().getWorld().playEffect(new Location(data.getPlayer().getWorld(), vt.getX(), vt.getY(), vt.getZ()), Effect.COLOURED_DUST, 5);
            double[][] camera = ReachUtil.obtainCameraPositions(data);
            double[] vertex = vt.toArray();

            for (double[] campos : camera) {
                // TODO: Calculations are a little fucked.
                float[] rotations = ReachUtil.getRotations(campos, vertex);

                float yaw = rotations[0];
                float pitch = rotations[1];

                yaws[0] = Math.min(yaws[0], yaw);
                yaws[1] = Math.max(yaws[1], yaw);
                pitches[0] = Math.min(pitches[0], pitch);
                pitches[1] = Math.max(pitches[1], pitch);
            }
        }

        float playerYaw = to.getYaw();
        float playerPitch = to.getPitch();

        playerYaw = fixYaw(playerYaw);

        float rangeYaw = interiorAngle(yaws[1], yaws[0]);
        float rangePitch = interiorAngle(pitches[1], pitches[0]);

        // this shows where on the target box the player hit, on interceptX 0.0 is the left edge, 1.0 the right, same concept for pitch.
        double interceptX = (playerYaw - fixYaw(yaws[0])) / rangeYaw;
        double interceptY = (playerPitch - pitches[0]) / rangePitch;

        return new double[]{
                interceptX,
                interceptY
        };
    }

    private float interiorAngle(float a, float b) {
        float delta = Math.abs(a - b);

        if (delta > 180) {
            delta = 360 - delta;
        }

        return delta;
    }
    private float fixYaw(double v) {
        return (float) (v % 360f + 360f) % 360f;
    }

    private Vertex[] getVertices() {
        BoundingBox box = target.getBox().copy().expand(0.25F, 0.25F, 0.25F);

        return new Vertex[]{
                new Vertex(box.getMinX(), box.getMinY(), box.getMinZ()),
                new Vertex(box.getMinX(), box.getMinY(), box.getMaxZ()),
                new Vertex(box.getMaxX(), box.getMinY(), box.getMinZ()),
                new Vertex(box.getMaxX(), box.getMinY(), box.getMaxZ()),
                new Vertex(box.getMinX(), box.getMaxY(), box.getMinZ()),
                new Vertex(box.getMinX(), box.getMaxY(), box.getMaxZ()),
                new Vertex(box.getMaxX(), box.getMaxY(), box.getMinZ()),
                new Vertex(box.getMaxX(), box.getMaxY(), box.getMaxZ())
        };
    }

    @Override
    public void process(WrappedPacket packet) {
        EntityTracker entityTracker = data.getTracker(EntityTracker.class);

        if (packet instanceof CPacketUseEntity) {
            CPacketUseEntity wrapper = (CPacketUseEntity) packet;
            target = entityTracker.get(wrapper.getEntityId());
        } else if (packet instanceof CPacketChat) {
            String message = ((CPacketChat) packet).getMessage();

            switch (message) {
                case "NovaTrain Cheats":
                    Bukkit.broadcastMessage("Nova > Training neural network for aim cheats");
                    classifier = 1;
                    break;
                case "NovaTrain Legit":
                    Bukkit.broadcastMessage("Nova > Training neural network for human behaviour");
                    classifier = 0;
                    break;
                case "NovaTrain Stop":
                    Bukkit.broadcastMessage("Nova > Stopping training");
                    classifier = null;
                    break;
                case "NovaTrain Predict":
                    if (classifier != null) {
                        Bukkit.broadcastMessage("Nova > Cannot predict while training");
                        break;
                    }
                    predicting = !predicting;
                    Bukkit.broadcastMessage("Nova > Neural network predicting " + (predicting ? "on" : "off"));
                    break;
            }
        }
    }
}
