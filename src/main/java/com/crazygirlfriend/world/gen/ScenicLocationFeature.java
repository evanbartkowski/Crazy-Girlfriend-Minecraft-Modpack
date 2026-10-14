package com.crazygirlfriend.world.gen;

import com.crazygirlfriend.block.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ScenicLocationFeature extends Feature<DefaultFeatureConfig> {
    public static final ScenicLocationFeature INSTANCE = new ScenicLocationFeature(DefaultFeatureConfig.CODEC);

    public ScenicLocationFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        // Find solid surface
        BlockPos.Mutable mut = origin.mutableCopy();
        while (mut.getY() > world.getBottomY() + 5 && world.isAir(mut)) {
            mut.move(Direction.DOWN);
        }

        BlockState groundState = world.getBlockState(mut);
        if (!groundState.isOf(Blocks.GRASS_BLOCK) && !groundState.isOf(Blocks.DIRT) && !groundState.isOf(Blocks.STONE)) {
            return false;
        }

        BlockPos center = mut.up().toImmutable();

        int type = random.nextInt(4);
        if (type == 0) {
            generateGazebo(world, center, random);
        } else if (type == 1) {
            generateLakesidePicnic(world, center, random);
        } else if (type == 2) {
            generateCherryShrine(world, center, random);
        } else {
            generateMountainOverlook(world, center, random);
        }

        ScenicLocationManager.get().registerScenicSpot(center);
        return true;
    }

    private void generateGazebo(StructureWorldAccess world, BlockPos center, Random random) {
        // 5x5 Gazebo base
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos floorPos = center.add(x, 0, z);
                world.setBlockState(floorPos, Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
                world.setBlockState(floorPos.down(), Blocks.COBBLESTONE.getDefaultState(), 2);
            }
        }

        // 4 Corner Pillars
        int[][] corners = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (int[] c : corners) {
            for (int y = 1; y <= 3; y++) {
                world.setBlockState(center.add(c[0], y, c[1]), Blocks.DARK_OAK_LOG.getDefaultState(), 2);
            }
        }

        // Roof
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                world.setBlockState(center.add(x, 4, z), Blocks.DARK_OAK_SLAB.getDefaultState(), 2);
            }
        }
        world.setBlockState(center.add(0, 5, 0), Blocks.DARK_OAK_SLAB.getDefaultState(), 2);

        // Hanging lanterns from roof
        world.setBlockState(center.add(0, 3, 0), Blocks.LANTERN.getDefaultState().with(net.minecraft.state.property.Properties.HANGING, true), 2);

        // Couple Sofa / Bench inside
        world.setBlockState(center.add(0, 1, 0), ModBlocks.COUPLE_SOFA.getDefaultState(), 2);

        // Flower boxes
        world.setBlockState(center.add(-1, 1, -2), Blocks.POTTED_PINK_TULIP.getDefaultState(), 2);
        world.setBlockState(center.add(1, 1, -2), Blocks.POTTED_POPPY.getDefaultState(), 2);
    }

    private void generateLakesidePicnic(StructureWorldAccess world, BlockPos center, Random random) {
        // Picnic Blanket & Flower
        world.setBlockState(center, ModBlocks.COUPLE_PICNIC_BLANKET.getDefaultState(), 2);
        world.setBlockState(center.add(1, 0, 0), Blocks.LANTERN.getDefaultState(), 2);
        world.setBlockState(center.add(-1, 0, 0), Blocks.POTTED_RED_TULIP.getDefaultState(), 2);
        world.setBlockState(center.add(0, 0, 1), Blocks.BARREL.getDefaultState(), 2);
    }

    private void generateCherryShrine(StructureWorldAccess world, BlockPos center, Random random) {
        // Torii Gate / Cherry Arch
        for (int y = 0; y <= 3; y++) {
            world.setBlockState(center.add(-2, y, 0), Blocks.CHERRY_LOG.getDefaultState(), 2);
            world.setBlockState(center.add(2, y, 0), Blocks.CHERRY_LOG.getDefaultState(), 2);
        }
        for (int x = -3; x <= 3; x++) {
            world.setBlockState(center.add(x, 4, 0), Blocks.CHERRY_SLAB.getDefaultState(), 2);
        }
        world.setBlockState(center.add(-1, 3, 0), Blocks.LANTERN.getDefaultState().with(net.minecraft.state.property.Properties.HANGING, true), 2);
        world.setBlockState(center.add(1, 3, 0), Blocks.LANTERN.getDefaultState().with(net.minecraft.state.property.Properties.HANGING, true), 2);

        // Swing or Sofa
        world.setBlockState(center.add(0, 0, 2), ModBlocks.COUPLE_SWING.getDefaultState(), 2);
        world.setBlockState(center.add(0, 0, -2), Blocks.PINK_PETALS.getDefaultState(), 2);
    }

    private void generateMountainOverlook(StructureWorldAccess world, BlockPos center, Random random) {
        // Overlook Platform
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(center.add(x, 0, z), Blocks.STONE_BRICKS.getDefaultState(), 2);
            }
        }
        // Fences on sides
        world.setBlockState(center.add(-1, 1, -1), Blocks.SPRUCE_FENCE.getDefaultState(), 2);
        world.setBlockState(center.add(1, 1, -1), Blocks.SPRUCE_FENCE.getDefaultState(), 2);
        world.setBlockState(center.add(-1, 1, 1), Blocks.SPRUCE_FENCE.getDefaultState(), 2);
        world.setBlockState(center.add(1, 1, 1), Blocks.SPRUCE_FENCE.getDefaultState(), 2);

        // Bench & Lantern
        world.setBlockState(center.add(0, 1, 0), ModBlocks.COUPLE_SOFA.getDefaultState(), 2);
        world.setBlockState(center.add(0, 1, 1), Blocks.LANTERN.getDefaultState(), 2);
    }
}
