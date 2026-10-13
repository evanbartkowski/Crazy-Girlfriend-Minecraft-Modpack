package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class RandomEventManager {
    private static final RandomEventManager INSTANCE = new RandomEventManager();
    private int tickCounter = 0;

    // targetUuid -> activeEventId
    private final Map<UUID, String> activeEvents = new HashMap<>();

    public static RandomEventManager get() {
        return INSTANCE;
    }

    public void triggerEvent(ServerPlayerEntity p1, ServerPlayerEntity p2, RelationshipEvent event) {
        if (p1 == null || p2 == null || event == null) return;

        activeEvents.put(p1.getUuid(), event.getId());
        activeEvents.put(p2.getUuid(), event.getId());

        MutableText header = Text.literal("═════ 🎭 " + event.getTitle() + " ═════").formatted(Formatting.GOLD, Formatting.BOLD);
        MutableText desc = Text.literal("\n" + event.getPrompt() + "\n").formatted(Formatting.YELLOW);

        MutableText btnA = Text.literal("\n [1] " + event.getOptionA())
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg eventchoice " + event.getId() + " A"))
                          .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to choose Option 1"))));

        MutableText btnB = Text.literal("\n [2] " + event.getOptionB())
            .formatted(Formatting.AQUA, Formatting.BOLD)
            .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg eventchoice " + event.getId() + " B"))
                          .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to choose Option 2"))));

        MutableText fullMsg = header.append(desc).append(btnA).append(btnB).append(Text.literal("\n════════════════════════════════════").formatted(Formatting.GOLD));

        p1.sendMessage(fullMsg, false);
        p2.sendMessage(fullMsg, false);

        p1.playSound(ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.3f);
        p2.playSound(ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.3f);
    }

    public void triggerRandomEventForPlayer(ServerPlayerEntity player) {
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        if (rel == null) {
            player.sendMessage(Text.literal("❌ You must be in a relationship to trigger relationship events!").formatted(Formatting.RED), false);
            return;
        }
        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));
        if (partner == null) {
            player.sendMessage(Text.literal("❌ Partner must be online to trigger relationship events!").formatted(Formatting.RED), false);
            return;
        }
        List<RelationshipEvent> events = RelationshipEventRegistry.getEvents();
        if (!events.isEmpty()) {
            RelationshipEvent event = events.get(new Random().nextInt(events.size()));
            triggerEvent(player, partner, event);
        }
    }

    public void triggerEvent(ServerPlayerEntity player, String eventId) {
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        if (rel == null) return;
        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));
        if (partner == null) return;
        for (RelationshipEvent e : RelationshipEventRegistry.getEvents()) {
            if (e.getId().equalsIgnoreCase(eventId)) {
                triggerEvent(player, partner, e);
                return;
            }
        }
    }

    public void resolveChoice(ServerPlayerEntity player, String eventId, String choice) {
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        if (rel == null) return;

        RelationshipEvent event = null;
        for (RelationshipEvent e : RelationshipEventRegistry.getEvents()) {
            if (e.getId().equalsIgnoreCase(eventId)) {
                event = e;
                break;
            }
        }
        if (event == null) return;

        activeEvents.remove(player.getUuid());
        activeEvents.remove(rel.getPartner(player.getUuid()));

        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));

        boolean isA = "A".equalsIgnoreCase(choice);
        String outcome = isA ? event.getOutcomeA() : event.getOutcomeB();
        int crazyDelta = isA ? event.getCrazyDeltaA() : event.getCrazyDeltaB();
        int affDelta = isA ? event.getAffectionDeltaA() : event.getAffectionDeltaB();
        AnimationType anim = isA ? event.getAnimA() : event.getAnimB();

        rel.addCrazyMeter(crazyDelta);
        rel.addAffectionXp(affDelta);

        // Broadcast outcome to couple
        MutableText outcomeMsg = Text.literal("✨ [Outcome] " + outcome).formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC);
        player.sendMessage(outcomeMsg, false);
        if (partner != null) partner.sendMessage(outcomeMsg, false);

        // Play animation
        if (anim != null && anim != AnimationType.NONE) {
            PlayerAnimationTracker.get().startAnimation(player, partner, anim, anim, anim.getDefaultDuration());
        }

        // Spawn particles
        ServerWorld world = player.getServerWorld();
        world.spawnParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 15, 0.4, 0.4, 0.4, 0.05);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.2f);

        RelationshipManager.get().syncToAll(player.server);
    }

    public void tick(MinecraftServer server) {
        tickCounter++;
        int interval = Math.max(600, ModConfig.get().eventIntervalSeconds * 20); // default ~120s
        if (tickCounter < interval) return;
        tickCounter = 0;

        if (!ModConfig.get().enableCrazyGirlfriendMode) return;

        for (ServerWorld world : server.getWorlds()) {
            List<ServerPlayerEntity> players = world.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                ServerPlayerEntity p1 = players.get(i);
                for (int j = i + 1; j < players.size(); j++) {
                    ServerPlayerEntity p2 = players.get(j);

                    if (p1.distanceTo(p2) <= 12.0) {
                        Relationship rel = RelationshipManager.get().getRelationship(p1.getUuid(), world);
                        if (rel != null && rel.involves(p2.getUuid())) {
                            if (!activeEvents.containsKey(p1.getUuid()) && !activeEvents.containsKey(p2.getUuid())) {
                                RelationshipEvent event = RelationshipEventRegistry.getRandomEvent(rel.getCrazyTier(), true);
                                triggerEvent(p1, p2, event);
                                return; // trigger one per tick cycle
                            }
                        }
                    }
                }
            }
        }
    }
}
