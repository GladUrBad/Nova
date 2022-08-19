package com.gladurbad.nova.util.collision;

import lombok.experimental.UtilityClass;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@UtilityClass
public class BlockUtil {

    private static final Set<Material> SOLIDS = new HashSet<>();
    private static final Set<Material> LIQUIDS = new HashSet<>();
    private static final Set<Material> LADDERS = new HashSet<>();
    private static final Set<Material> WALLS = new HashSet<>();
    private static final Set<Material> STAIRS = new HashSet<>();
    private static final Set<Material> WEBS = new HashSet<>();
    private static final Set<Material> STEPS = new HashSet<>();
    private static final Set<Material> PISTONS = new HashSet<>();
    private static final Set<Material> SLIMES = new HashSet<>();
    private static final Set<Material> ICES = new HashSet<>();
    private static final Set<Material> PASSABLES = new HashSet<>();
    private static final Set<Material> SOUL_SANDS = new HashSet<>();
    private static final Set<Material> ABNORMALS =  new HashSet<>();

    public static final Predicate<Block> SOLID = block -> SOLIDS.contains(block.getType()),
            LIQUID = block -> LIQUIDS.contains(block.getType()),
            LADDER = block -> LADDERS.contains(block.getType()),
            WALL = block -> WALLS.contains(block.getType()),
            STAIR = block -> STAIRS.contains(block.getType()),
            WEB = block -> WEBS.contains(block.getType()),
            STEP = block -> STEPS.contains(block.getType()),
            PISTON = block -> PISTONS.contains(block.getType()),
            SLIME = block -> SLIMES.contains(block.getType()),
            ICE = block -> ICES.contains(block.getType()),
            PASSABLE = block -> PASSABLES.contains(block.getType()),
            SOUL_SAND = block -> SOUL_SANDS.contains(block.getType()),
            ABNORMAL = block -> ABNORMALS.contains(block.getType());

    static {
        for (Material material : Material.values()) {
            String name = material.name().toUpperCase();

            if (material.isSolid()) {
                SOLIDS.add(material);
            }

            if (name.contains("STAIR")) {
                STAIRS.add(material);
                ABNORMALS.add(material);
            }

            if (name.contains("WEB")) {
                WEBS.add(material);
            }

            if (name.contains("STEP")) {
                STEPS.add(material);
                ABNORMALS.add(material);
            }

            if (name.contains("PISTON")) {
                PISTONS.add(material);
                ABNORMALS.add(material);
            }

            if (name.contains("ICE")) {
                ICES.add(material);
            }

            if (name.contains("DOOR")) {
                ABNORMALS.add(material);
            }

            if (name.contains("FENCE") || name.contains("WALL")) {
                WALLS.add(material);
            }
        }

        // fix some types where Block#isSolid returns the wrong value
        SOLIDS.addAll(Arrays.asList(Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF, Material.CARPET,
                Material.SNOW, Material.ANVIL, Material.SLIME_BLOCK, Material.WATER_LILY, Material.TRAP_DOOR,
                Material.IRON_TRAPDOOR));

        // passable materials.
        PASSABLES.addAll(Arrays.asList(Material.AIR, Material.GRASS, Material.SAPLING, Material.POWERED_RAIL,
                Material.DETECTOR_RAIL, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER,
                Material.RED_ROSE, Material.TORCH, Material.FIRE, Material.CROPS, Material.SIGN_POST,
                Material.RAILS, Material.WALL_SIGN, Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE,
                Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.PORTAL,
                Material.TRIPWIRE, Material.CARROT, Material.POTATO, Material.WOOD_BUTTON, Material.GOLD_PLATE,
                Material.IRON_PLATE, Material.ACTIVATOR_RAIL, Material.STANDING_BANNER, Material.WALL_BANNER));

        // abnormal collision box materials.
        ABNORMALS.addAll(Arrays.asList(Material.BED_BLOCK, Material.ANVIL, Material.WATER_LILY,
                Material.BREWING_STAND, Material.CAULDRON, Material.FLOWER_POT, Material.SKULL));

        // soul sand
        SOUL_SANDS.add(Material.SOUL_SAND);

        // liquid materials.
        LIQUIDS.addAll(Arrays.asList(Material.WATER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER, Material.LAVA));

        // ladder materials.
        LADDERS.add(Material.VINE);
        LADDERS.add(Material.LADDER);

        // odd-blocks
        STEPS.add(Material.SKULL);
        SLIMES.add(Material.SLIME_BLOCK);
    }

    public boolean check(Block block, Predicate<Block> condition) {
        return condition.test(block);
    }
}

