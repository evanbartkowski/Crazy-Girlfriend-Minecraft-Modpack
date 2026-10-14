package com.crazygirlfriend.mixin;

import com.crazygirlfriend.relationship.MarriedLifeBondManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "setHealth", at = @At("TAIL"))
    private void onMarriedSetHealth(float health, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            MarriedLifeBondManager.get().onHealthChanged(player, health);
        }
    }
}

