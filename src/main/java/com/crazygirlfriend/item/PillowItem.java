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

import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class PillowItem extends Item {
    public PillowItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            // Nullify any attack damage so pillow hits deal 0 damage
            target.heal(2.0f);

            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PILLOW_HIT, SoundCategory.PLAYERS, 1.2f, 1.0f);
            if (world instanceof ServerWorld serverWorld) {
                // Feathers burst
                serverWorld.spawnParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 0.8, target.getZ(), 25, 0.5, 0.3, 0.5, 0.05);
            }

            // Knock down player / mob: takes a second to get back up
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30, 6, false, false));
            target.takeKnockback(0.3, -attacker.getRotationVec(1.0f).x, -attacker.getRotationVec(1.0f).z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                // Comical ragdoll fall over on the floor for 30 ticks (1.5 seconds)
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.RAGDOLL_FAINT, 30);
                FacialExpressionManager.get().setAndSyncExpression(playerTarget, FacialExpression.DIZZY_EYES, 30);
            } else {
                target.setVelocity(0, -0.2, 0);
                target.velocityModified = true;
            }

            if (attacker instanceof ServerPlayerEntity playerAttacker) {
                PlayerAnimationTracker.get().startSingleAnimation(playerAttacker, AnimationType.PILLOW_FIGHT_ATTACKER, 25);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Fluffy Bed Pillow").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        tooltip.add(Text.literal("Burst of cloud feathers upon hit! Ideal for midnight pillow fights.").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

