package com.crazygirlfriend.mixin;

import com.crazygirlfriend.season.SeasonManager;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {

    @Inject(method = "getGrassColor", at = @At("RETURN"), cancellable = true)
    private static void applySeasonalGrassColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValueI();
        int seasonal = SeasonManager.get().getBlendedGrassColor(world, pos, original);
        if (seasonal != original) {
            cir.setReturnValue(seasonal);
        }
    }

    @Inject(method = "getFoliageColor", at = @At("RETURN"), cancellable = true)
    private static void applySeasonalFoliageColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValueI();
        int seasonal = SeasonManager.get().getBlendedFoliageColor(world, pos, original);
        if (seasonal != original) {
            cir.setReturnValue(seasonal);
        }
    }
}

