package com.crazygirlfriend.mixin;

import com.crazygirlfriend.relationship.CoSleepingManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "wakeSleepingPlayers", at = @At("HEAD"))
    private void onWakeSleepingPlayers(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        List<ServerPlayerEntity> sleeping = world.getPlayers().stream()
                .filter(LivingEntity::isSleeping)
                .collect(Collectors.toList());
        CoSleepingManager.get().handleMorningWakeUp(world, sleeping);
    }
}

