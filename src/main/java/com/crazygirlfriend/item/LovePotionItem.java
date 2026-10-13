package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LovePotionItem extends Item {
    public LovePotionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300, 0));

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.HEART, user.getX(), user.getY() + 1.2, user.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
                serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.5f);
            }

            if (user instanceof ServerPlayerEntity serverPlayer) {
                PlayerAnimationTracker.get().startSingleAnimation(serverPlayer, AnimationType.DIZZY, 60);
                serverPlayer.sendMessage(Text.literal("💖 Love Potion imbibed! Overwhelming romantic euphoria!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);
            }
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Mystical Love Potion").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        tooltip.add(Text.literal("Sparkles with effervescent romantic aura and grants sweet buffs.").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

