package com.gladurbad.nova.util.reach;

import com.gladurbad.nova.util.collision.BoundingBox;
import lombok.Getter;

@Getter
public class ReachEntity {

    public BoundingBox box;
    public final int id;

    public int otherPlayerMPPosRotationIncrements;

    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    public double otherPlayerMPX;
    public double otherPlayerMPY;
    public double otherPlayerMPZ;

    public double posX;
    public double posY;
    public double posZ;

    private final float width;
    public final float height;

    public ReachEntity(int id, int serverPosX, int serverPosY, int serverPosZ, float width, float height) {
        /*
         * "Is this even the right way to do it?" - asked Glad.
         * Foo replies, "No, you see here on line thirty-fou-"
         * "Shut the fuck up.", Glad interrupts. "You already know me, I have the buffer class for a reason you twat."
         */
        this.id = id;

        this.serverPosX = serverPosX;
        this.serverPosY = serverPosY;
        this.serverPosZ = serverPosZ;

        this.posX = serverPosX / 32.0D;
        this.posY = serverPosY / 32.0D;
        this.posZ = serverPosZ / 32.0D;

        this.width = width;
        this.height = height;

        float expandX = this.width / 2.0F;

        this.box = new BoundingBox(
                posX - expandX,
                posY,
                posZ - expandX,
                posX + expandX,
                posY + this.height,
                posZ + expandX
        );
    }

    public void setPositionAndRotation2(double x, double y, double z) {
        this.otherPlayerMPX = x;
        this.otherPlayerMPY = y;
        this.otherPlayerMPZ = z;
        this.otherPlayerMPPosRotationIncrements = 3;
    }

    public void onLivingUpdate() {
        if (this.otherPlayerMPPosRotationIncrements > 0) {
            double d0 = this.posX + (this.otherPlayerMPX - this.posX) / (double) this.otherPlayerMPPosRotationIncrements;
            double d1 = this.posY + (this.otherPlayerMPY - this.posY) / (double) this.otherPlayerMPPosRotationIncrements;
            double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double) this.otherPlayerMPPosRotationIncrements;

            --this.otherPlayerMPPosRotationIncrements;

            this.setPosition(d0, d1, d2);
        }
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        this.box = new BoundingBox(x - f, y, z - f, x + f, y + this.height, z + f);
    }
}