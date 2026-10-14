package com.crazygirlfriend.block;

import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.entity.SeatEntity;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CoupleSofaBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public CoupleSofaBlock() {
        super(AbstractBlock.Settings.create()
                .strength(1.0f)
                .sounds(BlockSoundGroup.WOOL)
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
        if (player.getVehicle() instanceof SeatEntity) {
            return ActionResult.PASS;
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            Box box = new Box(pos).expand(0.5);
            List<SeatEntity> existingSeats = serverWorld.getEntitiesByClass(SeatEntity.class, box, seat -> seat.hasPassengers());

            Direction facing = state.get(FACING);
            Direction side = facing.rotateYClockwise();

            double x = pos.getX() + 0.5;
            double z = pos.getZ() + 0.5;

            if (existingSeats.isEmpty()) {
                x += side.getOffsetX() * 0.25;
                z += side.getOffsetZ() * 0.25;
            } else if (existingSeats.size() == 1) {
                x -= side.getOffsetX() * 0.25;
                z -= side.getOffsetZ() * 0.25;
            } else {
                player.sendMessage(Text.literal("🛋️ The sofa is full!").formatted(Formatting.YELLOW), true);
                return ActionResult.CONSUME;
            }

            SeatEntity seat = new SeatEntity(ModEntities.SEAT, serverWorld);
            seat.refreshPositionAndAngles(x, pos.getY() + 0.35, z, facing.asRotation(), 0);
            serverWorld.spawnEntity(seat);
            player.startRiding(seat, true);

            serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            serverWorld.spawnParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 5, 0.3, 0.2, 0.3, 0.02);
            player.sendMessage(Text.literal("🛋️ Sitting comfortably on the couple sofa. Partner can sit right beside you!").formatted(Formatting.LIGHT_PURPLE), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (!world.isClient && world instanceof ServerWorld serverWorld) {
                Box box = new Box(pos).expand(0.5);
                List<SeatEntity> seats = serverWorld.getEntitiesByClass(SeatEntity.class, box, e -> true);
                for (SeatEntity seat : seats) {
                    seat.removeAllPassengers();
                    seat.discard();
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}

