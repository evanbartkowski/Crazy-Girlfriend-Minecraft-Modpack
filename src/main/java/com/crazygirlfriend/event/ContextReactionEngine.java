package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
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
import net.minecraft.util.math.Box;

import java.util.List;

public class ContextReactionEngine {

    public static void checkPostInteractionReactions(ServerPlayerEntity actor, ServerPlayerEntity target, AnimationType type) {
        if (actor == null || target == null) return;
        ServerWorld world = actor.getServerWorld();

        // 1. Villager Onlookers reaction
        if (type == AnimationType.KISS || type == AnimationType.MAKE_OUT || type == AnimationType.HUG) {
            Box searchBox = actor.getBoundingBox().expand(8.0);
            List<VillagerEntity> villagers = world.getEntitiesByClass(VillagerEntity.class, searchBox, v -> true);
            for (VillagerEntity villager : villagers) {
                world.spawnParticles(ParticleTypes.HEART, villager.getX(), villager.getY() + 1.8, villager.getZ(), 3, 0.2, 0.2, 0.2, 0.05);
                villager.playSound(ModSounds.GIGGLE, 0.8f, 1.4f);
            }
        }

        // 2. Heavy Armor Carry check
        if (type == AnimationType.BRIDAL_CARRY || type == AnimationType.PIGGYBACK || type == AnimationType.SHOULDER_CARRY) {
            ItemStack chest = target.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.isOf(Items.NETHERITE_CHESTPLATE) || chest.isOf(Items.IRON_CHESTPLATE)) {
                actor.sendMessage(Text.literal("⚠️ Oof! That armor is ridiculously heavy!").formatted(Formatting.YELLOW), true);
                PlayerAnimationTracker.get().startSingleAnimation(actor, AnimationType.STRUGGLE_CARRY, 40);
            }
        }

        // 3. Kissing in the Rain
        if ((type == AnimationType.KISS || type == AnimationType.MAKE_OUT) && world.isRaining()) {
            world.spawnParticles(ParticleTypes.FIREWORK, target.getX(), target.getY() + 1.5, target.getZ(), 20, 0.4, 0.4, 0.4, 0.1);
            actor.sendMessage(Text.literal("🌧️ Kissing in the pouring rain... magical!").formatted(Formatting.AQUA, Formatting.ITALIC), true);
            target.sendMessage(Text.literal("🌧️ Kissing in the pouring rain... magical!").formatted(Formatting.AQUA, Formatting.ITALIC), true);
        }

        // 4. Thunderstorm Cuddle Startle
        if (type == AnimationType.CUDDLE && world.isThundering()) {
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.2, target.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
            actor.playSound(ModSounds.GIGGLE, SoundCategory.PLAYERS, 0.9f, 1.3f);
        }

        // 5. Raw Fish Slap Check
        if (type == AnimationType.SLAP_ATTACKER && (actor.getMainHandStack().isOf(Items.COD) || actor.getMainHandStack().isOf(Items.SALMON))) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.FISH_SLAP, SoundCategory.PLAYERS, 1.2f, 1.1f);
            target.sendMessage(Text.literal("🐟 SLAPPED WITH A WET FISH!").formatted(Formatting.AQUA, Formatting.BOLD), true);
        }
    }
}

