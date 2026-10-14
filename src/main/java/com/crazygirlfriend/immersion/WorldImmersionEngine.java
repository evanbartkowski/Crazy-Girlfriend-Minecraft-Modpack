package com.crazygirlfriend.immersion;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.event.world.WorldEventManager;
import com.crazygirlfriend.event.world.WorldEventType;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.season.Season;
import com.crazygirlfriend.season.SeasonManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WorldImmersionEngine {
    private static final WorldImmersionEngine INSTANCE = new WorldImmersionEngine();
    public static WorldImmersionEngine get() { return INSTANCE; }

    /**
     * Checks and applies contextual environmental immersion effects during player interactions.
     */
    public void onCoupleInteraction(ServerPlayerEntity actor, ServerPlayerEntity partner, AnimationType animType, Relationship rel) {
        if (!ModConfig.get().enableWorldEvents) return;

        ServerWorld world = actor.getServerWorld();
        long time = world.getTimeOfDay() % 24000L;
        Season season = SeasonManager.get().getCurrentSeason();
        WorldEventType activeEvent = WorldEventManager.get().getActiveEvent();

        // 1. Kissing during Sunset
        if ((animType == AnimationType.KISS || animType == AnimationType.QUICK_KISS || animType == AnimationType.MAKE_OUT || animType == AnimationType.ROMANTIC_DIP_KISS) &&
                (time >= 11500L && time <= 13000L)) {
            world.spawnParticles(ParticleTypes.HEART, actor.getX(), actor.getY() + 1.2, actor.getZ(), 10, 0.5, 0.4, 0.5, 0.05);
            world.spawnParticles(ParticleTypes.CHERRY_LEAVES, actor.getX(), actor.getY() + 1.5, actor.getZ(), 12, 0.6, 0.5, 0.6, 0.08);
            world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.0f);
            actor.sendMessage(Text.literal("🌅 Sunset Kiss: The golden sunset casts a magical glow around you both!").formatted(Formatting.GOLD), true);
            partner.sendMessage(Text.literal("🌅 Sunset Kiss: The golden sunset casts a magical glow around you both!").formatted(Formatting.GOLD), true);
            rel.addAffectionXp(25);
        }

        // 2. Cuddling during Thunderstorm
        if ((animType == AnimationType.CUDDLE || animType == AnimationType.CAMPFIRE_CUDDLE || animType == AnimationType.CUDDLE_SLEEPING) &&
                world.isThundering()) {
            world.spawnParticles(ParticleTypes.HEART, actor.getX(), actor.getY() + 1.0, actor.getZ(), 8, 0.4, 0.3, 0.4, 0.02);
            actor.sendMessage(Text.literal("⛈️ Thunderstorm Snuggle: Safe, warm, and cozy in each other's arms.").formatted(Formatting.AQUA), true);
            partner.sendMessage(Text.literal("⛈️ Thunderstorm Snuggle: Safe, warm, and cozy in each other's arms.").formatted(Formatting.AQUA), true);
            rel.addCrazyMeter(-10);
            rel.addAffectionXp(20);
        }

        // 3. Proposing / Dancing during an Aurora Night
        if ((animType == AnimationType.SLOW_DANCE || animType == AnimationType.FAST_DANCE) && (activeEvent == WorldEventType.AURORA_NIGHT || world.getBiome(actor.getBlockPos()).value().isCold(actor.getBlockPos()))) {
            world.spawnParticles(ParticleTypes.END_ROD, actor.getX(), actor.getY() + 1.0, actor.getZ(), 20, 0.8, 0.8, 0.8, 0.1);
            world.spawnParticles(ParticleTypes.GLOW, actor.getX(), actor.getY() + 1.2, actor.getZ(), 15, 0.6, 0.6, 0.6, 0.05);
            actor.sendMessage(Text.literal("🌌 Aurora Romance: Dancing beneath the shimmering Northern Lights!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);
            partner.sendMessage(Text.literal("🌌 Aurora Romance: Dancing beneath the shimmering Northern Lights!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);
            rel.addAffectionXp(50);
        }

        // 4. Stargazing during Meteor Shower
        if (animType == AnimationType.STARGAZING && (activeEvent == WorldEventType.METEOR_SHOWER || (time >= 13500L && time <= 22500L))) {
            world.spawnParticles(ParticleTypes.FIREWORK, actor.getX(), actor.getY() + 2.0, actor.getZ(), 12, 0.5, 0.5, 0.5, 0.08);
            actor.sendMessage(Text.literal("🌠 Stargazing: Shooting stars streak across the cosmic sky above you.").formatted(Formatting.YELLOW), true);
            partner.sendMessage(Text.literal("🌠 Stargazing: Shooting stars streak across the cosmic sky above you.").formatted(Formatting.YELLOW), true);
            rel.addAffectionXp(30);
            rel.addCrazyMeter(-15);
        }

        // 5. Walking & Holding Hands in Autumn
        if (animType == AnimationType.HAND_HOLDING && season == Season.AUTUMN) {
            world.spawnParticles(ParticleTypes.CHERRY_LEAVES, actor.getX(), actor.getY() + 0.8, actor.getZ(), 8, 0.6, 0.3, 0.6, 0.04);
            actor.sendMessage(Text.literal("🍂 Autumn Stroll: Golden leaves dance along the path as you walk together.").formatted(Formatting.GOLD), true);
            partner.sendMessage(Text.literal("🍂 Autumn Stroll: Golden leaves dance along the path as you walk together.").formatted(Formatting.GOLD), true);
            rel.addAffectionXp(15);
        }
    }
}
