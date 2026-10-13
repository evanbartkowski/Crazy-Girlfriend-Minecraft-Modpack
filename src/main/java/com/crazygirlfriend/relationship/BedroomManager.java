package com.crazygirlfriend.relationship;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.makeup.MakeupManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BedroomManager {
    private static final Random RANDOM = new Random();

    public static void executeRomanticNight(ServerPlayerEntity p1, ServerPlayerEntity p2) {
        if (p1 == null || p2 == null) return;
        ServerWorld world = p1.getServerWorld();

        // 1. Initial romantic embrace
        PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.ROMANTIC_NIGHT, AnimationType.ROMANTIC_NIGHT, 70);
        world.spawnParticles(ParticleTypes.HEART, (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2 + 1.2, (p1.getZ() + p2.getZ()) / 2, 35, 0.5, 0.5, 0.5, 0.08);
        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.GIGGLE, SoundCategory.PLAYERS, 1.0f, 1.0f);



        // Resolve morning outcome after a delay
        resolveMorningAfter(p1, p2);
    }

    public static void resolveMorningAfter(ServerPlayerEntity p1, ServerPlayerEntity p2) {
        ServerWorld world = p1.getServerWorld();
        Relationship rel = RelationshipManager.get().getRelationship(p1.getUuid(), world);
        if (rel != null) {
            rel.addAffectionXp(35);
            rel.addCrazyMeter(-20);
        }

        int outcome = RANDOM.nextInt(6);
        switch (outcome) {
            case 0 -> { // Waking up blushing
                PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.BLUSHING, AnimationType.BLUSHING, 40);
                p1.sendMessage(Text.literal("☀️ Good morning! Both of you wake up smiling and blushing warmly.").formatted(Formatting.LIGHT_PURPLE), false);
                p2.sendMessage(Text.literal("☀️ Good morning! Both of you wake up smiling and blushing warmly.").formatted(Formatting.LIGHT_PURPLE), false);
            }
            case 1 -> { // Messy hair & bed cosmetics
                PlayerMakeupData m1 = MakeupManager.get(world).getMakeup(p1.getUuid());
                m1.setBlushStyle(1);
                RequestManager.syncMakeup(p1.server, p1.getUuid(), m1);

                PlayerMakeupData m2 = MakeupManager.get(world).getMakeup(p2.getUuid());
                m2.setBlushStyle(1);
                RequestManager.syncMakeup(p2.server, p2.getUuid(), m2);

                p1.sendMessage(Text.literal("☀️ You wake up with adorable bed-head cheeks!").formatted(Formatting.YELLOW), false);
                p2.sendMessage(Text.literal("☀️ You wake up with adorable bed-head cheeks!").formatted(Formatting.YELLOW), false);
            }
            case 2 -> { // Comical bed break sound!
                world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.BED_BREAK, SoundCategory.PLAYERS, 1.2f, 0.8f);
                world.spawnParticles(ParticleTypes.CRIT, p1.getX(), p1.getY() + 0.5, p1.getZ(), 25, 0.5, 0.5, 0.5, 0.15);
                p1.sendMessage(Text.literal("💥 CRASH! The bed frame couldn't handle all the love!").formatted(Formatting.GOLD, Formatting.BOLD), false);
                p2.sendMessage(Text.literal("💥 CRASH! The bed frame couldn't handle all the love!").formatted(Formatting.GOLD, Formatting.BOLD), false);
            }
            case 3 -> { // Awkward villager noise outside
                world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.JEALOUSY, SoundCategory.PLAYERS, 1.0f, 1.2f);
                p1.sendMessage(Text.literal("👀 A villager sneezed right outside your bedroom window!").formatted(Formatting.GRAY, Formatting.ITALIC), false);
                p2.sendMessage(Text.literal("👀 A villager sneezed right outside your bedroom window!").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            }
            case 4 -> { // One snores, one annoyed
                PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.SNORING, AnimationType.ARMS_CROSSED, 50);
                p1.sendMessage(Text.literal("😴 Loud morning snoring fills the bedroom!").formatted(Formatting.AQUA), false);
                p2.sendMessage(Text.literal("😴 Loud morning snoring fills the bedroom!").formatted(Formatting.AQUA), false);
            }
            case 5 -> { // Massive heart explosion
                world.spawnParticles(ParticleTypes.HEART, (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2 + 1.2, (p1.getZ() + p2.getZ()) / 2, 50, 0.8, 0.8, 0.8, 0.1);
                world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.2f, 1.0f);
                p1.sendMessage(Text.literal("💖 You both wake up surrounded by sweet romantic hearts!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
                p2.sendMessage(Text.literal("💖 You both wake up surrounded by sweet romantic hearts!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
            }
        }
    }

    public static void checkCaughtIn4K(ServerPlayerEntity p1, ServerPlayerEntity p2) {
        ServerWorld world = p1.getServerWorld();
        for (ServerPlayerEntity thirdPlayer : world.getPlayers()) {
            if (thirdPlayer.equals(p1) || thirdPlayer.equals(p2)) continue;

            double dist = thirdPlayer.distanceTo(p1);
            if (dist <= 6.0) {
                // Caught in 4K!
                world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.RECORD_SCRATCH, SoundCategory.PLAYERS, 1.2f, 1.0f);
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, (p1.getX() + p2.getX()) / 2, p1.getY() + 1.8, (p1.getZ() + p2.getZ()) / 2, 10, 0.3, 0.3, 0.3, 0.05);

                PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.JUMP_APART, AnimationType.JUMP_APART, 30);

                Text caughtText = Text.literal("📸 CAUGHT IN 4K! " + thirdPlayer.getName().getString() + " walked in on your private moment!").formatted(Formatting.RED, Formatting.BOLD);
                p1.sendMessage(caughtText, true);
                p2.sendMessage(caughtText, true);
                thirdPlayer.sendMessage(Text.literal("👀 You just walked in on " + p1.getName().getString() + " & " + p2.getName().getString() + "! Awkward!").formatted(Formatting.GOLD), true);
                return;
            }
        }
    }
}
