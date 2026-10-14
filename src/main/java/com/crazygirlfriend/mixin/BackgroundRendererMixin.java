package com.crazygirlfriend.mixin;

import com.crazygirlfriend.client.atmosphere.EnvironmentalColorManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private static void applyCustomAtmosphericColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        EnvironmentalColorManager.get().applyColorGrading(camera, tickDelta, world, viewDistance, skyDarkness);
    }

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyCustomAtmosphericFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        EnvironmentalColorManager.get().applyFogDistance(camera, fogType, viewDistance, thickFog, tickDelta);
    }
}

