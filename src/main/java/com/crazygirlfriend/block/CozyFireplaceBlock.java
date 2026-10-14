package com.crazygirlfriend.block;

import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.entity.SeatEntity;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CozyFireplaceBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public CozyFireplaceBlock() {
        super(AbstractBlock.Settings.create()
                .strength(2.0f)
                .sounds(BlockSoundGroup.STONE)
                .luminance(s -> 15)
                .nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        Direction facing = state.get(FACING);
        double seatX = pos.getX() + 0.5 + facing.getOffsetX() * 0.8;
        double seatZ = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.8;
        double seatY = pos.getY() + 0.1;

        Box searchBox = new Box(seatX - 0.4, seatY - 0.2, seatZ - 0.4, seatX + 0.4, seatY + 0.6, seatZ + 0.4);
        List<SeatEntity> existingSeats = world.getEntitiesByClass(SeatEntity.class, searchBox, e -> true);

        SeatEntity seat;
        if (!existingSeats.isEmpty()) {
            seat = existingSeats.get(0);
        } else {
            seat = new SeatEntity(ModEntities.SEAT, world);
            seat.setPosition(seatX, seatY, seatZ);
            world.spawnEntity(seat);
        }

        if (!seat.hasPassengers()) {
            player.startRiding(seat, true);
            world.playSound(null, pos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f);

            // Check if partner is also near hearth
            if (player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
                Relationship rel = RelationshipManager.get().getRelationship(serverPlayer.getUuid(), serverWorld);
                if (rel != null) {
                    ServerPlayerEntity partner = serverPlayer.server.getPlayerManager().getPlayer(rel.getPartner(serverPlayer.getUuid()));
                    if (partner != null && partner.squaredDistanceTo(serverPlayer) < 9.0) {
                        rel.addCrazyMeter(-10);
                        rel.addAffectionXp(15);
                        serverWorld.spawnParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 5, 0.4, 0.3, 0.4, 0.05);
                        serverPlayer.sendMessage(Text.literal("🔥 Cozy Hearth: Cuddling warmly by the fire with " + partner.getName().getString() + "!").formatted(Formatting.GOLD), true);
                        partner.sendMessage(Text.literal("🔥 Cozy Hearth: Cuddling warmly by the fire with " + serverPlayer.getName().getString() + "!").formatted(Formatting.GOLD), true);
                    } else {
                        serverPlayer.sendMessage(Text.literal("🔥 Warming up comfortably by the fireplace...").formatted(Formatting.YELLOW), true);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(4) == 0) {
            world.playSound(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
                0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false
            );
        }

        // Fireplace chimney smoke & flame sparks
        Direction facing = state.get(FACING);
        double fx = pos.getX() + 0.5 + facing.getOffsetX() * 0.2;
        double fz = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.2;
        double fy = pos.getY() + 0.4;

        world.addParticle(ParticleTypes.FLAME, fx + (random.nextDouble() - 0.5) * 0.3, fy, fz + (random.nextDouble() - 0.5) * 0.3, 0.0, 0.015, 0.0);
        if (random.nextFloat() < 0.4f) {
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 0.0, 0.04, 0.0);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}
