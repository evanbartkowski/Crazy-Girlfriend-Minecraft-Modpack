package com.crazygirlfriend.mixin;

import com.crazygirlfriend.client.camera.CinematicCameraManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyCinematicFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        float fovMod = CinematicCameraManager.get().getFovModifier(tickDelta);
        if (fovMod != 0.0f) {
            cir.setReturnValue(cir.getReturnValueD() + fovMod);
        }
    }
}

