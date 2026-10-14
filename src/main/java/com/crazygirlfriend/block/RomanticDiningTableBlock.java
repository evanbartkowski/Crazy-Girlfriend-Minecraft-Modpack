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

public class RomanticDiningTableBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public RomanticDiningTableBlock() {
        super(AbstractBlock.Settings.create()
                .strength(1.5f)
                .sounds(BlockSoundGroup.WOOD)
                .luminance(s -> 9)
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
        Direction sideDir = facing.rotateYClockwise();

        // Check seat A and seat B
        double seat1X = pos.getX() + 0.5 + sideDir.getOffsetX() * 0.7;
        double seat1Z = pos.getZ() + 0.5 + sideDir.getOffsetZ() * 0.7;

        double seat2X = pos.getX() + 0.5 - sideDir.getOffsetX() * 0.7;
        double seat2Z = pos.getZ() + 0.5 - sideDir.getOffsetZ() * 0.7;

        double seatY = pos.getY() + 0.1;

        SeatEntity seat1 = getOrCreateSeat(world, seat1X, seatY, seat1Z);
        SeatEntity seat2 = getOrCreateSeat(world, seat2X, seatY, seat2Z);

        SeatEntity targetSeat = null;
        if (!seat1.hasPassengers()) {
            targetSeat = seat1;
        } else if (!seat2.hasPassengers()) {
            targetSeat = seat2;
        }

        if (targetSeat != null) {
            player.startRiding(targetSeat, true);
            world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 1.0f, 1.0f);

            // Check if partner is seated on the other side
            SeatEntity otherSeat = targetSeat == seat1 ? seat2 : seat1;
            if (otherSeat.hasPassengers() && otherSeat.getFirstPassenger() instanceof ServerPlayerEntity partner && player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
                Relationship rel = RelationshipManager.get().getRelationship(serverPlayer.getUuid(), serverWorld);
                if (rel != null && rel.involves(partner.getUuid())) {
                    rel.addAffectionXp(20);
                    rel.addCrazyMeter(-15);
                    serverWorld.spawnParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 6, 0.4, 0.3, 0.4, 0.05);
                    serverPlayer.sendMessage(Text.literal("🍷 Candlelit Dinner: Sharing a romantic dinner date with " + partner.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), true);
                    partner.sendMessage(Text.literal("🍷 Candlelit Dinner: Sharing a romantic dinner date with " + serverPlayer.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), true);
                }
            } else {
                player.sendMessage(Text.literal("🍷 Seated at the dining table. Awaiting your date...").formatted(Formatting.YELLOW), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private SeatEntity getOrCreateSeat(World world, double x, double y, double z) {
        Box box = new Box(x - 0.3, y - 0.2, z - 0.3, x + 0.3, y + 0.6, z + 0.3);
        List<SeatEntity> existing = world.getEntitiesByClass(SeatEntity.class, box, e -> true);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        SeatEntity seat = new SeatEntity(ModEntities.SEAT, world);
        seat.setPosition(x, y, z);
        world.spawnEntity(seat);
        return seat;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.05;
        double cz = pos.getZ() + 0.5;

        // Center dinner candle
        world.addParticle(ParticleTypes.SMALL_FLAME, cx, cy, cz, 0.0, 0.0, 0.0);
        if (random.nextFloat() < 0.2f) {
            world.addParticle(ParticleTypes.SMOKE, cx, cy + 0.05, cz, 0.0, 0.01, 0.0);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}
