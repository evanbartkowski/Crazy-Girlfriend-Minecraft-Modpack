package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Random;

public class MinigameManager {
    private static final MinigameManager INSTANCE = new MinigameManager();
    private static final Random RANDOM = new Random();

    public static final String[] GAMES = {"trivia", "rps", "pillow_fight", "wrestling", "roulette", "scavenger", "gift_challenge", "kiss_cam", "dance_challenge", "apology_challenge"};

    public static MinigameManager get() {
        return INSTANCE;
    }

    public String[] getGames() {
        return GAMES;
    }

    public void startMinigame(ServerPlayerEntity p1, ServerPlayerEntity p2, String gameType) {
        if (p1 == null || p2 == null) return;
        Relationship rel = RelationshipManager.get().getRelationship(p1.getUuid(), p1.getServerWorld());
        if (rel == null) return;

        switch (gameType.toLowerCase()) {
            case "trivia" -> startTrivia(p1, p2, rel);
            case "rps" -> startRockPaperScissors(p1, p2, rel);
            case "pillow_fight" -> startPillowFight(p1, p2, rel);
            case "wrestling" -> startWrestling(p1, p2, rel);
            case "roulette" -> startLoveRoulette(p1, p2, rel);
            case "scavenger" -> startScavengerHunt(p1, p2, rel);
            case "gift_challenge" -> startGiftChallenge(p1, p2, rel);
            case "kiss_cam" -> startKissCam(p1, p2, rel);
            case "dance_challenge" -> startDanceChallenge(p1, p2, rel);
            case "apology_challenge" -> startApologyChallenge(p1, p2, rel);
        }
    }

    private void startTrivia(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        MutableText header = Text.literal("🎮 [Minigame: Couple Trivia] 🎮\n").formatted(Formatting.AQUA, Formatting.BOLD);
        MutableText q = Text.literal("Question: What makes the ultimate Minecraft date?\n").formatted(Formatting.YELLOW);
        MutableText optA = Text.literal(" [A] Mining for diamonds side-by-side! ")
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans trivia A")));
        MutableText optB = Text.literal(" [B] Watching the sunset from a cliff! ")
            .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans trivia B")));

        p1.sendMessage(header.copy().append(q).append(optA).append(optB), false);
        p2.sendMessage(header.copy().append(q).append(optA).append(optB), false);
    }

    private void startRockPaperScissors(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        MutableText header = Text.literal("✌️ [Minigame: Rock-Paper-Scissors Argument Settler] ✋\n").formatted(Formatting.GOLD, Formatting.BOLD);
        MutableText prompt = Text.literal("Choose your move to settle who was right:\n").formatted(Formatting.YELLOW);
        MutableText rock = Text.literal(" [ROCK 🪨] ").formatted(Formatting.RED, Formatting.BOLD).styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans rps rock")));
        MutableText paper = Text.literal(" [PAPER 📄] ").formatted(Formatting.WHITE, Formatting.BOLD).styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans rps paper")));
        MutableText scissors = Text.literal(" [SCISSORS ✂️] ").formatted(Formatting.AQUA, Formatting.BOLD).styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans rps scissors")));

        p1.sendMessage(header.copy().append(prompt).append(rock).append(paper).append(scissors), false);
        p2.sendMessage(header.copy().append(prompt).append(rock).append(paper).append(scissors), false);
    }

    private void startPillowFight(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        p1.getInventory().offerOrDrop(new ItemStack(ModItems.PILLOW));
        p2.getInventory().offerOrDrop(new ItemStack(ModItems.PILLOW));

        p1.sendMessage(Text.literal("⚔️ [Minigame: Pillow War!] Soft pillows equipped! First to land 3 hits wins!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        p2.sendMessage(Text.literal("⚔️ [Minigame: Pillow War!] Soft pillows equipped! First to land 3 hits wins!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);

        PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.PILLOW_FIGHT_ATTACKER, AnimationType.PILLOW_FIGHT_VICTIM, 25);
    }

    private void startWrestling(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.PLAYFUL_WRESTLE, AnimationType.PLAYFUL_WRESTLE, 60);
        rel.addAffectionXp(20);
        rel.addCrazyMeter(-10);

        MutableText msg = Text.literal("🤼 [Minigame: Playful Wrestling] You both tumble on the carpet laughing! +20 Affection XP").formatted(Formatting.GOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startLoveRoulette(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        String[] perks = {"Speed & Hearts Aura", "Regeneration & Sweet Warmth", "Glowing Soulmate Bond"};
        String perk = perks[RANDOM.nextInt(perks.length)];

        p1.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 1));
        p1.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
        p2.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 1));
        p2.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));

        ServerWorld world = p1.getServerWorld();
        world.spawnParticles(ParticleTypes.HEART, p1.getX(), p1.getY() + 1.2, p1.getZ(), 25, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticles(ParticleTypes.HEART, p2.getX(), p2.getY() + 1.2, p2.getZ(), 25, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.4f);

        rel.addAffectionXp(25);
        rel.addCrazyMeter(-15);

        MutableText msg = Text.literal("🎰 [Love Roulette] Jackpot! Blessed with " + perk + "! Crazy Meter -15!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startScavengerHunt(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        MutableText msg = Text.literal("🔍 [Couple Scavenger Hunt] Find a Pink Tulip or an Apple within 2 minutes to earn +30 Affection XP!").formatted(Formatting.YELLOW, Formatting.BOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startGiftChallenge(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        MutableText msg = Text.literal("🎁 [Secret Gift Challenge] Sneak-gift any item to your partner right now to unlock mutual love buffs!").formatted(Formatting.GOLD, Formatting.BOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startKissCam(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        ServerWorld world = p1.getServerWorld();
        world.spawnParticles(ParticleTypes.HEART, (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2 + 1.5, (p1.getZ() + p2.getZ()) / 2, 40, 0.8, 0.8, 0.8, 0.1);
        world.spawnParticles(ParticleTypes.FIREWORK, (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2 + 2.0, (p1.getZ() + p2.getZ()) / 2, 20, 0.5, 0.5, 0.5, 0.15);
        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.CHEER, SoundCategory.PLAYERS, 1.0f, 1.1f);

        PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.MAKE_OUT, AnimationType.MAKE_OUT, 70);
        rel.addAffectionXp(30);

        MutableText msg = Text.literal("📹 [KISS CAM ACTIVATED!] The crowd cheers as you share a passionate kiss!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startDanceChallenge(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        PlayerAnimationTracker.get().startAnimation(p1, p2, AnimationType.FAST_DANCE, AnimationType.FAST_DANCE, 60);
        ServerWorld world = p1.getServerWorld();
        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.GIGGLE, SoundCategory.PLAYERS, 1.0f, 1.2f);
        rel.addAffectionXp(20);

        MutableText msg = Text.literal("💃 [Dance-Off Challenge!] You both rock the rhythm together with stylish moves!").formatted(Formatting.AQUA, Formatting.BOLD);
        p1.sendMessage(msg, false);
        p2.sendMessage(msg, false);
    }

    private void startApologyChallenge(ServerPlayerEntity p1, ServerPlayerEntity p2, Relationship rel) {
        MutableText header = Text.literal("🕊️ [Apology Challenge] 🕊️\n").formatted(Formatting.GREEN, Formatting.BOLD);
        MutableText prompt = Text.literal("Choose the best apology to defuse partner tension:\n").formatted(Formatting.YELLOW);
        MutableText opt1 = Text.literal(" [1] 'You were completely right, and I am the biggest doofus.'\n")
            .formatted(Formatting.AQUA)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans apology 1")));
        MutableText opt2 = Text.literal(" [2] 'I brought you chocolates, flowers, and unconditional love.'\n")
            .formatted(Formatting.LIGHT_PURPLE)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg minigame_ans apology 2")));

        p1.sendMessage(header.copy().append(prompt).append(opt1).append(opt2), false);
        p2.sendMessage(header.copy().append(prompt).append(opt1).append(opt2), false);
    }

    public void handleAnswer(ServerPlayerEntity player, String game, String answer) {
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        if (rel == null) return;
        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));

        if ("apology".equalsIgnoreCase(game)) {
            rel.addCrazyMeter(-25);
            rel.addAffectionXp(30);
            player.sendMessage(Text.literal("❤️ Apology wholeheartedly accepted! Crazy Meter reduced by 25!").formatted(Formatting.GREEN, Formatting.BOLD), false);
            if (partner != null) partner.sendMessage(Text.literal("❤️ Partner delivered a sweet heartfelt apology!").formatted(Formatting.GREEN, Formatting.BOLD), false);
            PlayerAnimationTracker.get().startAnimation(player, partner, AnimationType.RECONCILIATION_HUG, AnimationType.RECONCILIATION_HUG, 60);
        } else if ("trivia".equalsIgnoreCase(game)) {
            rel.addAffectionXp(25);
            player.sendMessage(Text.literal("🎉 Correct! Soulmate connection confirmed! +25 Affection XP!").formatted(Formatting.GREEN, Formatting.BOLD), false);
            if (partner != null) partner.sendMessage(Text.literal("🎉 Trivia answered! Perfect match! +25 Affection XP!").formatted(Formatting.GREEN, Formatting.BOLD), false);
        } else if ("rps".equalsIgnoreCase(game)) {
            String[] outcomes = {"You tied! Celebrate with a hug!", "Victory! Your point is proven!", "Defeat! But cuddles make up for it!"};
            String res = outcomes[RANDOM.nextInt(outcomes.length)];
            player.sendMessage(Text.literal("🎲 " + res).formatted(Formatting.GOLD), false);
            if (partner != null) partner.sendMessage(Text.literal("🎲 " + res).formatted(Formatting.GOLD), false);
        }

        RelationshipManager.get().syncToAll(player.server);
    }
}
