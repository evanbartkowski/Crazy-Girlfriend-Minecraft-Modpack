package com.crazygirlfriend.mixin;

import com.crazygirlfriend.block.CoupleDoubleBedBlock;
import com.crazygirlfriend.relationship.CoSleepingManager;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onCoSleepingBedUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) return;

        BlockPos headPos = pos;
        if (state.get(BedBlock.PART) != BedPart.HEAD) {
            headPos = pos.offset(state.get(BedBlock.FACING));
            state = world.getBlockState(headPos);
            if (!(state.getBlock() instanceof BedBlock)) {
                return;
            }
        }

        if (!BedBlock.isBedWorking(world)) {
            return;
        }

        final BlockPos finalHeadPos = headPos;
        if (state.get(BedBlock.OCCUPIED)) {
            List<ServerPlayerEntity> sleepers = CoSleepingManager.get().getSleepersAt(world, finalHeadPos);
            if (sleepers.size() == 1 && !sleepers.contains(player)) {
                ServerPlayerEntity partner = sleepers.get(0);
                Either<PlayerEntity.SleepFailureReason, Unit> either = player.trySleep(finalHeadPos);
                either.ifLeft(reason -> {
                    if (reason.getMessage() != null) {
                        player.sendMessage(reason.getMessage(), true);
                    }
                });
                either.ifRight(unit -> {
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        CoSleepingManager.get().onCoSleepStart(serverPlayer, partner, finalHeadPos);
                    }
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (sleepers.size() >= 2) {
                player.sendMessage(Text.literal("🛏️ This bed is already full (2/2)!").formatted(Formatting.YELLOW), true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "getDirection", at = @At("HEAD"), cancellable = true)
    private static void getCustomBedDirection(BlockView world, BlockPos pos, CallbackInfoReturnable<Direction> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CoupleDoubleBedBlock) {
            cir.setReturnValue(state.get(CoupleDoubleBedBlock.FACING));
        }
    }
}
