package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class ContextReactionEngine {

    public static float getRandomPitch(ServerWorld world) {
        return 0.92f + (world.random.nextFloat() * 0.16f);
    }

    public static AnimationType resolveContextualAnimation(ServerPlayerEntity actor, ServerPlayerEntity target, AnimationType requested) {
        if (actor == null || target == null) return requested;
        ServerWorld world = actor.getServerWorld();

        // 1. Underwater interactions
        if (actor.isTouchingWater() && target.isTouchingWater()) {
            if (requested == AnimationType.KISS || requested == AnimationType.QUICK_KISS || requested == AnimationType.MAKE_OUT) {
                return AnimationType.UNDERWATER_KISS;
            }
        }

        // 2. Pouring rain twirl / dip
        if (world.isRaining() && world.isSkyVisible(actor.getBlockPos())) {
            if (requested == AnimationType.HUG) {
                return world.random.nextBoolean() ? AnimationType.RAIN_TWIRL : AnimationType.ROMANTIC_SWAY_HUG;
            }
            if (requested == AnimationType.KISS) {
                return world.random.nextBoolean() ? AnimationType.ROMANTIC_DIP_KISS : requested;
            }
        }

        // 3. Campfire cozy cuddle
        if (requested == AnimationType.CUDDLE || requested == AnimationType.SIT_TOGETHER) {
            if (isNearCampfire(actor, 4)) {
                return AnimationType.CAMPFIRE_CUDDLE;
            }
        }

        // 4. Fish slap
        if (requested == AnimationType.SLAP_ATTACKER) {
            ItemStack held = actor.getMainHandStack();
            if (held.isOf(Items.COD) || held.isOf(Items.SALMON)) {
                return AnimationType.FISH_SLAP_ATTACKER;
            }
        }

        // 5. Comedic Kiss outcome (10% chance of head bump bonk)
        if (requested == AnimationType.KISS || requested == AnimationType.QUICK_KISS) {
            if (world.random.nextFloat() < 0.10f) {
                return AnimationType.HEAD_BUMP_KISS;
            }
        }

        return requested;
    }

    private static boolean isNearCampfire(ServerPlayerEntity player, int radius) {
        BlockPos center = player.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -1, -radius), center.add(radius, 2, radius))) {
            if (player.getWorld().getBlockState(pos).isOf(Blocks.CAMPFIRE) || player.getWorld().getBlockState(pos).isOf(Blocks.SOUL_CAMPFIRE)) {
                return true;
            }
        }
        return false;
    }

    public static void checkPostInteractionReactions(ServerPlayerEntity actor, ServerPlayerEntity target, AnimationType type) {
        if (actor == null || target == null) return;
        ServerWorld world = actor.getServerWorld();
        float pitch = getRandomPitch(world);

        // Synchronize Facial Expressions based on action type
        if (type == AnimationType.HEAD_BUMP_KISS) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.BONK, SoundCategory.PLAYERS, 1.2f, 1.3f);
            world.spawnParticles(ParticleTypes.CRIT, (actor.getX() + target.getX()) / 2.0, actor.getY() + 1.5, (actor.getZ() + target.getZ()) / 2.0, 15, 0.2, 0.2, 0.2, 0.1);
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.DIZZY_EYES, 45);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.DIZZY_EYES, 45);
            actor.sendMessage(Text.literal("💥 BONK! You leaned in so fast you bumped heads!").formatted(Formatting.YELLOW), true);
            target.sendMessage(Text.literal("💥 BONK! Head collision!").formatted(Formatting.YELLOW), true);
            return;
        }

        if (type == AnimationType.UNDERWATER_KISS) {
            world.spawnParticles(ParticleTypes.BUBBLE, target.getX(), target.getY() + 1.2, target.getZ(), 25, 0.4, 0.4, 0.4, 0.05);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.KISS, SoundCategory.PLAYERS, 0.9f, pitch);
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.CLOSED_EYES, 40);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.CLOSED_EYES, 40);
            actor.sendMessage(Text.literal("🌊 An enchanting underwater kiss!").formatted(Formatting.AQUA, Formatting.ITALIC), true);
            target.sendMessage(Text.literal("🌊 An enchanting underwater kiss!").formatted(Formatting.AQUA, Formatting.ITALIC), true);
            return;
        }

        if (type == AnimationType.FISH_SLAP_ATTACKER || type == AnimationType.FISH_SLAP_VICTIM) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.FISH_SLAP, SoundCategory.PLAYERS, 1.2f, pitch);
            world.spawnParticles(ParticleTypes.SPLASH, target.getX(), target.getY() + 1.3, target.getZ(), 30, 0.3, 0.3, 0.3, 0.1);
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.SMILING, 30);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.SHOCKED, 35);
            target.sendMessage(Text.literal("🐟 SLAPPED WITH A WET FISH! *FLOP*").formatted(Formatting.AQUA, Formatting.BOLD), true);
            return;
        }

        if (type == AnimationType.CAMPFIRE_CUDDLE) {
            world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, target.getX(), target.getY() + 0.5, target.getZ(), 8, 0.3, 0.3, 0.3, 0.02);
            world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(), 12, 0.4, 0.4, 0.4, 0.04);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, pitch);
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.SMILING, 60);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.SMILING, 60);
            actor.sendMessage(Text.literal("🔥 Cozy warmth by the campfire...").formatted(Formatting.GOLD, Formatting.ITALIC), true);
            target.sendMessage(Text.literal("🔥 Cozy warmth by the campfire...").formatted(Formatting.GOLD, Formatting.ITALIC), true);
            return;
        }

        if (type == AnimationType.RAIN_TWIRL) {
            world.spawnParticles(ParticleTypes.SPLASH, target.getX(), target.getY() + 1.0, target.getZ(), 25, 0.5, 0.5, 0.5, 0.05);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, pitch);
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.LAUGHING, 50);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.LAUGHING, 50);
            actor.sendMessage(Text.literal("🌧️ Twirling gracefully together in the rain!").formatted(Formatting.AQUA), true);
            target.sendMessage(Text.literal("🌧️ Twirling gracefully together in the rain!").formatted(Formatting.AQUA), true);
            return;
        }

        // Standard reactions & expressions
        if (type == AnimationType.KISS || type == AnimationType.MAKE_OUT || type == AnimationType.ROMANTIC_DIP_KISS) {
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.CLOSED_EYES, 35);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.BLUSHING, 35);
        } else if (type == AnimationType.HUG || type == AnimationType.BEAR_HUG || type == AnimationType.SPINNING_HUG) {
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.SMILING, 40);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.SMILING, 40);
        } else if (type == AnimationType.SLAP_ATTACKER || type == AnimationType.SPINNING_SLAP) {
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.ANGRY_EYEBROWS, 30);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.SHOCKED, 35);
        } else if (type == AnimationType.TICKLE_FIGHT) {
            FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.LAUGHING, 45);
            FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.LAUGHING, 45);
        }

        // 1. Villager Onlookers reaction
        if (type == AnimationType.KISS || type == AnimationType.MAKE_OUT || type == AnimationType.HUG || type == AnimationType.ROMANTIC_DIP_KISS) {
            Box searchBox = actor.getBoundingBox().expand(8.0);
            List<VillagerEntity> villagers = world.getEntitiesByClass(VillagerEntity.class, searchBox, v -> true);
            for (VillagerEntity villager : villagers) {
                world.spawnParticles(ParticleTypes.HEART, villager.getX(), villager.getY() + 1.8, villager.getZ(), 3, 0.2, 0.2, 0.2, 0.05);
                villager.playSound(ModSounds.GIGGLE, 0.8f, pitch + 0.2f);
            }
        }

        // 2. Heavy Armor Carry check
        if (type == AnimationType.BRIDAL_CARRY || type == AnimationType.PIGGYBACK || type == AnimationType.SHOULDER_CARRY) {
            ItemStack chest = target.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.isOf(Items.NETHERITE_CHESTPLATE) || chest.isOf(Items.IRON_CHESTPLATE)) {
                actor.sendMessage(Text.literal("⚠️ Oof! That armor is ridiculously heavy!").formatted(Formatting.YELLOW), true);
                PlayerAnimationTracker.get().startSingleAnimation(actor, AnimationType.STUMBLE_CARRY, 40);
                FacialExpressionManager.get().setAndSyncExpression(actor, FacialExpression.EMBARRASSED, 40);
            }
        }
    }
}
