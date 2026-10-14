package com.crazygirlfriend.block;

import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.entity.PillorySeatEntity;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.leash.PlayerLeashManager;
import com.crazygirlfriend.pillory.PilloryManager;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
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
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PilloryBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;

    protected static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 24.0, 16.0);

    public PilloryBlock() {
        super(AbstractBlock.Settings.create()
                .strength(2.5f)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OCCUPIED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OCCUPIED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(OCCUPIED, false);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        Box seatBox = new Box(pos).expand(0.5);
        List<PillorySeatEntity> existingSeats = serverWorld.getEntitiesByClass(PillorySeatEntity.class, seatBox, Entity::isAlive);

        if (state.get(OCCUPIED)) {
            // Pillory is currently occupied!
            if (!existingSeats.isEmpty()) {
                PillorySeatEntity seat = existingSeats.get(0);
                if (!seat.getPassengerList().isEmpty()) {
                    Entity passenger = seat.getPassengerList().get(0);
                    UUID jailerUuid = PilloryManager.get().getJailer(passenger.getUuid());

                    // Check if player is authorized to release (jailer, partner, holding key/leash/paddle, or sneaking)
                    boolean canRelease = serverPlayer.hasPermissionLevel(2)
                            || (jailerUuid != null && jailerUuid.equals(serverPlayer.getUuid()))
                            || serverPlayer.isSneaking()
                            || serverPlayer.getMainHandStack().isOf(ModItems.PLAYER_LEASH)
                            || serverPlayer.getMainHandStack().isOf(ModItems.SHARED_COLLAR)
                            || serverPlayer.getMainHandStack().isOf(ModItems.PADDLE);

                    Relationship rel = RelationshipManager.get().getRelationship(passenger.getUuid(), serverWorld);
                    if (rel != null && rel.involves(serverPlayer.getUuid())) {
                        canRelease = true;
                    }

                    if (canRelease) {
                        seat.unlock();
                        passenger.dismountVehicle();
                        serverWorld.setBlockState(pos, state.with(OCCUPIED, false), 3);

                        serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 0.9f);
                        serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.1f);
                        serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.05);

                        serverPlayer.sendMessage(Text.literal("🔓 Released " + passenger.getName().getString() + " from the pillory!").formatted(Formatting.GREEN), true);
                        if (passenger instanceof ServerPlayerEntity pPlayer) {
                            pPlayer.sendMessage(Text.literal("🔓 You were released from the pillory!").formatted(Formatting.GREEN, Formatting.BOLD), true);
                        }
                        return ActionResult.SUCCESS;
                    } else {
                        serverPlayer.sendMessage(Text.literal("🔒 " + passenger.getName().getString() + " is locked in the pillory! Only their captor, partner, or someone with a leash/paddle can release them.").formatted(Formatting.YELLOW), true);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            // If marked occupied but no seat/passenger, reset
            serverWorld.setBlockState(pos, state.with(OCCUPIED, false), 3);
        }

        // Pillory is empty: check for collared/leashed player to put inside!
        ServerPlayerEntity targetToLock = null;

        // 1. Is user holding someone on a leash?
        for (ServerPlayerEntity online : serverWorld.getPlayers()) {
            if (PlayerLeashManager.get().isLeashed(online.getUuid()) && online.distanceTo(serverPlayer) < 8.0) {
                UUID holder = PlayerLeashManager.get().getHolder(online.getUuid());
                if (holder != null && holder.equals(serverPlayer.getUuid())) {
                    targetToLock = online;
                    break;
                }
            }
        }

        // 2. Is there a nearby collared/leashed player within 3.5 blocks?
        if (targetToLock == null) {
            for (ServerPlayerEntity nearby : serverWorld.getPlayers()) {
                if (nearby.distanceTo(serverPlayer) <= 3.5 && !nearby.equals(serverPlayer)) {
                    if (PlayerLeashManager.get().isLeashed(nearby.getUuid())
                            || serverPlayer.getMainHandStack().isOf(ModItems.PLAYER_LEASH)
                            || serverPlayer.getMainHandStack().isOf(ModItems.SHARED_COLLAR)) {
                        targetToLock = nearby;
                        break;
                    }
                }
            }
        }

        if (targetToLock != null) {
            // Lock target into pillory!
            Direction facing = state.get(FACING);
            PillorySeatEntity seat = new PillorySeatEntity(ModEntities.PILLORY_SEAT, serverWorld);
            seat.setPilloryPos(pos);
            seat.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, facing.asRotation(), 0);
            serverWorld.spawnEntity(seat);

            targetToLock.teleport(serverWorld, pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, facing.asRotation(), 0);
            targetToLock.startRiding(seat, true);

            // Release leash now that they are locked in stocks
            PlayerLeashManager.get().releaseLeash(targetToLock.getUuid(), serverWorld.getServer());
            PilloryManager.get().lockPlayer(targetToLock, serverPlayer, pos);

            serverWorld.setBlockState(pos, state.with(OCCUPIED, true), 3);

            // Audio & visual effects
            serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.2f, 0.8f);
            serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.7f);
            serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 8, 0.3, 0.3, 0.3, 0.05);

            targetToLock.sendMessage(Text.literal("🔒 CLANG! You were locked into the pillory! You are stuck in until someone unlocks you!").formatted(Formatting.RED, Formatting.BOLD), true);
            serverPlayer.sendMessage(Text.literal("🔒 Successfully locked " + targetToLock.getName().getString() + " into the pillory!").formatted(Formatting.GOLD, Formatting.BOLD), true);

            serverWorld.getServer().getPlayerManager().broadcast(
                    Text.literal("🔒 " + serverPlayer.getName().getString() + " locked " + targetToLock.getName().getString() + " into the pillory! They are stuck in!").formatted(Formatting.YELLOW, Formatting.ITALIC),
                    false
            );
            return ActionResult.SUCCESS;
        }

        serverPlayer.sendMessage(Text.literal("⛓️ The pillory is empty! Lead a collared player on a leash here to lock them in!").formatted(Formatting.AQUA), true);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && !world.isClient && world instanceof ServerWorld serverWorld) {
            Box seatBox = new Box(pos).expand(0.5);
            List<PillorySeatEntity> seats = serverWorld.getEntitiesByClass(PillorySeatEntity.class, seatBox, Entity::isAlive);
            for (PillorySeatEntity seat : seats) {
                seat.unlock();
                seat.removeAllPassengers();
                seat.discard();
            }
            PilloryManager.get().unlockAt(pos, serverWorld.getServer());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
