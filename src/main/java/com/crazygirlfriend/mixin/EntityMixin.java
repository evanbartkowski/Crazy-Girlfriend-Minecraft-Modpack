package com.crazygirlfriend.mixin;

import com.crazygirlfriend.entity.PillorySeatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean isAlive();
    @Shadow public abstract Entity getVehicle();

    @Inject(method = "dismountVehicle", at = @At("HEAD"), cancellable = true)
    private void onPilloryDismount(CallbackInfo ci) {
        if (!this.isAlive()) {
            return;
        }

        if (this.getVehicle() instanceof PillorySeatEntity pillorySeat && pillorySeat.isLocked() && !pillorySeat.isRemoved() && pillorySeat.isAlive()) {
            ci.cancel();
            if ((Object) this instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("🔒 You are locked in the pillory! You cannot escape!").formatted(Formatting.RED), true);
                if (player.age % 15 == 0) {
                    player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, 0.7f, 1.1f);
                }
            }
        }
    }
}

