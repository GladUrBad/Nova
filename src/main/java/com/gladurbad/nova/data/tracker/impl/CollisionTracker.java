package com.gladurbad.nova.data.tracker.impl;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.util.collision.BlockUtil;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import lombok.Getter;
import lombok.var;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CollisionTracker extends Tracker {

    public CollisionTracker(PlayerData data) {
        super(data);
    }

    private boolean ice, slime, web, soulSand, ladder, abnormal, onGround,
            liquid, collidedHorizontally, underBlock, touchingEntity;


    public void update(PlayerLocation to) {
        // Reset all collision assessments to false.
        ice = slime = web = soulSand = ladder = abnormal = touchingEntity
                = onGround = liquid = collidedHorizontally = underBlock = false;

        World world = data.getPlayer().getWorld();
        BoundingBox collisionBox = new BoundingBox(to.getX(), to.getY(), to.getZ(), 0.6F, 1.8F);

        // Map of blocks and their respective boxes, some blocks like stairs have multiple.
        Map<Block, List<BoundingBox>> blockMap = new HashMap<>();

        /*
         * Bounds to get blocks around the player.
         * I don't know why, but I literally cannot get it to work without subtracting 0.001.
         */
        int x = MathHelper.floor(collisionBox.getMinX() - 0.001);
        int y = MathHelper.floor(collisionBox.getMinY() - 0.001);
        int z = MathHelper.floor(collisionBox.getMinZ() - 0.001);
        int x1 = MathHelper.f(collisionBox.getMaxX() + 0.001);
        int y1 = MathHelper.f(collisionBox.getMaxY() + 0.001);
        int z1 = MathHelper.f(collisionBox.getMaxZ() + 0.001);

        // The NMS method we are using is going to be weird and it requires this to work.
        AxisAlignedBB aabb = new AxisAlignedBB(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
        Entity nmsEntity = ((CraftEntity) data.getPlayer()).getHandle();

        // Will this crash the server? Probably. Do I care? No, my anticheats have never seen any server outside of localhost.
        for (int lx = x; lx < x1; lx++) {
            for (int lz = z; lz < z1; lz++) {
                for (int ly = y; ly < y1; ly++) {
                    List<AxisAlignedBB> boxes = new ArrayList<>();
                    Block block = world.getBlockAt(lx, ly, lz);
                    Chunk nmsChunk = nmsWorld.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
                    BlockPosition nmsBlockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                    IBlockData iBlockData = nmsChunk.getBlockData(nmsBlockPosition);

                    if (iBlockData != null) {
                        iBlockData.getBlock().a(nmsWorld, nmsBlockPosition, iBlockData, aabb, boxes, nmsEntity);
                    }

                    blockMap.put(block, boxes.stream()
                            .map(bb -> bb == null ? null : new BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f))
                            .collect(Collectors.toList()));
                }
            }
        }

        // Var because fuck you ain't no way in hell I'm typing Map.Entry<?,?> like that shit is so fucking long.
        for (var entry : blockMap.entrySet()) {
            Block block = entry.getKey();
            List<BoundingBox> boxes = entry.getValue();

            for (BoundingBox box : boxes) {
                if (collisionBox.collidesAbove(box)) {
                    // Pipe operator, essentially the equivalent of doing bool = bool || condition.
                    ice |= BlockUtil.check(block, BlockUtil.ICE);
                    slime |= BlockUtil.check(block, BlockUtil.SLIME);
                    soulSand |= BlockUtil.check(block, BlockUtil.SOUL_SAND);
                    onGround |= !BlockUtil.check(block, BlockUtil.PASSABLE);
                }

                // Okay so some blocks have weird collision boxes so instead of properly accounting we're exempting Verus style.
                if (collisionBox.collides(box)) {
                    abnormal |= BlockUtil.check(block, BlockUtil.ABNORMAL);
                }

                collidedHorizontally |= collisionBox.collidesHorizontally(box) && !BlockUtil.check(block, BlockUtil.PASSABLE);
                underBlock |= collisionBox.collidesUnder(box) && !BlockUtil.check(block, BlockUtil.PASSABLE);
            }

            // These blocks don't have any collision boxes since they are passable.
            ladder |= BlockUtil.check(block, BlockUtil.LADDER);
            liquid |= BlockUtil.check(block, BlockUtil.LIQUID);
            web |= BlockUtil.check(block, BlockUtil.WEB);
        }
    }
}
