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

public class PillowItem extends Item {
    public PillowItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PILLOW_HIT, SoundCategory.PLAYERS, 1.2f, 1.0f);
            if (world instanceof ServerWorld serverWorld) {
                // Feathers burst
                serverWorld.spawnParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 1.0, target.getZ(), 15, 0.4, 0.4, 0.4, 0.05);
            }
            target.takeKnockback(0.4, -attacker.getRotationVec(1.0f).x, -attacker.getRotationVec(1.0f).z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.PILLOW_FIGHT_VICTIM, 25);
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
