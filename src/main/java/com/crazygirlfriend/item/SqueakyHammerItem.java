package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SqueakyHammerItem extends Item {
    public SqueakyHammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SQUEAK, SoundCategory.PLAYERS, 1.2f, 1.8f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.NOTE, target.getX(), target.getY() + 1.2, target.getZ(), 8, 0.3, 0.3, 0.3, 0.1);
            }
            target.takeKnockback(0.8, -attacker.getRotationVec(1.0f).x, -attacker.getRotationVec(1.0f).z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.SQUEAK_HAMMER_VICTIM, 30);
            }
            if (attacker instanceof ServerPlayerEntity playerAttacker) {
                PlayerAnimationTracker.get().startSingleAnimation(playerAttacker, AnimationType.SQUEAK_HAMMER_ATTACKER, 25);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Oversized Squeaky Mallet").formatted(Formatting.YELLOW, Formatting.BOLD));
        tooltip.add(Text.literal("Loud high-pitched squeak with harmless slapstick knockback!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

