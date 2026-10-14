package com.crazygirlfriend.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class RomanticChandelierBlock extends Block {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);

    public RomanticChandelierBlock() {
        super(AbstractBlock.Settings.create()
                .strength(0.5f)
                .sounds(BlockSoundGroup.LANTERN)
                .luminance(s -> 15)
                .nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.65;
        double cz = pos.getZ() + 0.5;

        // 4 candlelight points
        double offset = 0.28;
        world.addParticle(ParticleTypes.SMALL_FLAME, cx + offset, cy, cz, 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.SMALL_FLAME, cx - offset, cy, cz, 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.SMALL_FLAME, cx, cy, cz + offset, 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.SMALL_FLAME, cx, cy, cz - offset, 0.0, 0.0, 0.0);

        if (random.nextFloat() < 0.25f) {
            world.addParticle(ParticleTypes.SMOKE, cx + (random.nextDouble() - 0.5) * 0.4, cy + 0.1, cz + (random.nextDouble() - 0.5) * 0.4, 0.0, 0.01, 0.0);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}
