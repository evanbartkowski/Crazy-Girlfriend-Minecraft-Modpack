package com.crazygirlfriend.block;

import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.entity.SeatEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class PicnicBlanketBlock extends Block {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public PicnicBlanketBlock() {
        super(AbstractBlock.Settings.create()
                .strength(0.2f)
                .sounds(BlockSoundGroup.WOOL)
                .nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            // Check existing seats at this location
            Box box = new Box(pos).expand(0.5);
            List<SeatEntity> existingSeats = serverWorld.getEntitiesByClass(SeatEntity.class, box, seat -> seat.hasPassengers());

            double xOffset = 0.5;
            double zOffset = 0.5;

            if (existingSeats.isEmpty()) {
                xOffset = 0.35;
            } else if (existingSeats.size() == 1) {
                xOffset = 0.65;
            } else {
                player.sendMessage(Text.literal("🧺 The picnic blanket is full!").formatted(Formatting.YELLOW), true);
                return ActionResult.CONSUME;
            }

            SeatEntity seat = new SeatEntity(ModEntities.SEAT, serverWorld);
            seat.refreshPositionAndAngles(pos.getX() + xOffset, pos.getY() + 0.1, pos.getZ() + zOffset, player.getYaw(), 0);
            serverWorld.spawnEntity(seat);
            player.startRiding(seat, true);

            serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            serverWorld.spawnParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 5, 0.3, 0.2, 0.3, 0.02);
            player.sendMessage(Text.literal("🧺 Relaxing on the picnic blanket! Partner can cuddle up right next to you.").formatted(Formatting.GREEN), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
