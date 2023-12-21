package com.gladurbad.nova.util.reach;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.impl.PositionTracker;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.util.Vector;

@UtilityClass
public class ReachUtil {
    public boolean lineOfSight(float yaw, PlayerLocation from, BoundingBox target) {
        Vector direction = new Vector(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));

        double inverseDirectionX = 1.0 / direction.getX();
        double inverseDirectionZ = 1.0 / direction.getZ();

        boolean inverseX = inverseDirectionX < 0.0;
        boolean inverseZ = inverseDirectionZ < 0.0;

        double minX = ((inverseX ? target.getMaxX() : target.getMinX()) - from.getX()) * inverseDirectionX;
        double maxX = ((inverseX ? target.getMinX() : target.getMaxX()) - from.getX()) * inverseDirectionX;

        double minZ = ((inverseZ ? target.getMaxZ() : target.getMinZ()) - from.getZ()) * inverseDirectionZ;
        double maxZ = ((inverseZ ? target.getMinZ() : target.getMaxZ()) - from.getZ()) * inverseDirectionZ;

        boolean intersects = maxZ > minX && minZ < maxX;

        return intersects;
    }
    public float[] getRotations(double[] cameraPosition, double[] vertex) {
        double dx = vertex[0] - cameraPosition[0];
        double dy = vertex[1] - cameraPosition[1];
        double dz = vertex[2] - cameraPosition[2];

        double dist = Math.hypot(dx, dz);

        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(dy, dist) * 180.0 / Math.PI));

        yaw = (yaw % 360f + 360f) % 360f;

        return new float[]{yaw, pitch};
    }

    public double fixYawRange(double yaw) {
        yaw %= 360.0F;
        if (yaw >= 180.0F) {
            yaw -= 360.0F;
        }

        if (yaw < -180.0F) {
            yaw += 360.0F;
        }

        return yaw;
    }


    public double[][] obtainCameraPositions(PlayerData data) {
        PositionTracker positionTracker = data.getTracker(PositionTracker.class);

        return new double[][]{
                {positionTracker.getTo().getX(), positionTracker.getTo().getY() + 1.62, positionTracker.getTo().getZ()},
                {positionTracker.getTo().getX(), positionTracker.getTo().getY() + 1.54, positionTracker.getTo().getZ()}
        };
    }
}
