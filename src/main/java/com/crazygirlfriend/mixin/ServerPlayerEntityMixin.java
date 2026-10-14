package com.crazygirlfriend.mixin;

import com.crazygirlfriend.relationship.MarriedLifeBondManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onMarriedPlayerTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MarriedLifeBondManager.get().tick(player);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onMarriedPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MarriedLifeBondManager.get().onSpouseDied(player, damageSource);
    }
}
