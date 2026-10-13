package com.crazygirlfriend.relationship;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.leash.PlayerLeashManager;
import com.crazygirlfriend.makeup.MakeupManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestManager {
    private static final RequestManager INSTANCE = new RequestManager();

    // targetUuid -> PendingRequest
    private final Map<UUID, PendingRequest> pendingRequests = new HashMap<>();

    public static RequestManager get() {
        return INSTANCE;
    }

    public boolean hasPendingRequest(UUID target) {
        return pendingRequests.containsKey(target);
    }

    public PendingRequest getRequest(UUID target) {
        return pendingRequests.get(target);
    }

    public void sendRequest(ServerPlayerEntity sender, ServerPlayerEntity target, RequestType type, Object extraData) {
        if (sender == null || target == null || sender.equals(target)) {
            return;
        }

        // Check relationship auto-accept
        Relationship rel = RelationshipManager.get().getRelationship(target.getUuid(), target.getServerWorld());
        boolean autoAccept = rel != null && rel.involves(sender.getUuid()) && (rel.isAutoAccept() || ModConfig.get().autoAcceptForPartners);

        long expiry = target.server.getTicks() + (ModConfig.get().interactionTimeoutSeconds * 20L);
        PendingRequest req = new PendingRequest(
            sender.getUuid(), sender.getName().getString(),
            target.getUuid(), target.getName().getString(),
            type, expiry, extraData
        );

        if (autoAccept) {
            pendingRequests.put(target.getUuid(), req);
            accept(target);
            return;
        }

        pendingRequests.put(target.getUuid(), req);

        // Notify sender
        sender.sendMessage(Text.literal("✉️ Request sent to " + target.getName().getString() + " for: " + type.getDescription()).formatted(Formatting.LIGHT_PURPLE), false);

        // Clickable message for target
        MutableText prompt;
        if (type == RequestType.PROPOSAL_DATING) {
            prompt = Text.literal("❤️ " + sender.getName().getString() + " is asking you to be their girlfriend! Type YES to accept or NO to decline.").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
        } else if (type == RequestType.PROPOSAL_MARRIAGE) {
            prompt = Text.literal("💍 " + sender.getName().getString() + " is asking you to marry them! Type YES to accept or NO to decline.").formatted(Formatting.GOLD, Formatting.BOLD);
        } else {
            prompt = Text.literal("✨ " + sender.getName().getString() + " wants to " + type.getDescription() + " with you!").formatted(Formatting.AQUA);
        }

        MutableText acceptButton = Text.literal(" [ACCEPT] ")
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg accept"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to accept request"))));

        MutableText declineButton = Text.literal(" [DECLINE] ")
            .formatted(Formatting.RED, Formatting.BOLD)
            .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg decline"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to decline request"))));

        target.sendMessage(prompt.append(" ").append(acceptButton).append(" ").append(declineButton), false);
        target.playSound(ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.2f);
    }

    public boolean accept(ServerPlayerEntity target) {
        PendingRequest req = pendingRequests.remove(target.getUuid());
        if (req == null) {
            target.sendMessage(Text.literal("❌ No pending request found.").formatted(Formatting.RED), false);
            return false;
        }

        ServerPlayerEntity sender = target.server.getPlayerManager().getPlayer(req.getSender());
        if (sender == null || !sender.isAlive()) {
            target.sendMessage(Text.literal("❌ The requesting player is no longer online.").formatted(Formatting.RED), false);
            return false;
        }

        ServerWorld world = target.getServerWorld();

        switch (req.getType()) {
            case PROPOSAL_DATING -> {
                // Consume rose if held
                if (sender.getMainHandStack().isOf(ModItems.RED_ROSE)) {
                    sender.getMainHandStack().decrement(1);
                }
                RelationshipManager.get().createDating(sender, target);
            }
            case PROPOSAL_MARRIAGE -> {
                // Consume ring if held
                if (sender.getMainHandStack().isOf(ModItems.DIAMOND_RING)) {
                    sender.getMainHandStack().decrement(1);
                }
                RelationshipManager.get().upgradeToMarriage(sender, target);
            }
            case HUG -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.HUG, AnimationType.HUG, 40);
                world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(), 15, 0.4, 0.4, 0.4, 0.05);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.9f, 1.2f);
                checkJealousy(sender, target, world);
            }
            case KISS -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.KISS, AnimationType.KISS, 35);
                world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.5, target.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.KISS, SoundCategory.PLAYERS, 1.0f, 1.0f);
                checkJealousy(sender, target, world);
            }
            case SLAP -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.SLAP_ATTACKER, AnimationType.SLAP_VICTIM, 25);
                world.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.2, target.getZ(), 25, 0.3, 0.3, 0.3, 0.2);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SLAP, SoundCategory.PLAYERS, 1.0f, 1.1f);
                Vec3d dir = target.getPos().subtract(sender.getPos()).normalize();
                double kb = ModConfig.get().slapKnockback;
                target.takeKnockback(kb, -dir.x, -dir.z);
            }
            case STRADDLE -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.STRADDLE, AnimationType.STRADDLE, 60);
                world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.0, target.getZ(), 10, 0.4, 0.4, 0.4, 0.05);
            }
            case PIGGYBACK -> {
                target.startRiding(sender, true);
                PlayerAnimationTracker.get().startAnimation(target, sender, AnimationType.PIGGYBACK, AnimationType.PIGGYBACK, 0);
                target.sendMessage(Text.literal("🐴 You hopped onto " + sender.getName().getString() + "'s back! Sneak to dismount.").formatted(Formatting.YELLOW), true);
            }
            case MAKE_LOVE -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.MAKE_LOVE, AnimationType.MAKE_LOVE, 70);
                world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(), 35, 0.5, 0.5, 0.5, 0.08);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.GIGGLE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                // Send fade-to-black vignette packet to both players
                sendFadeVignette(sender, 70);
                sendFadeVignette(target, 70);
            }
            case GIFT_BOUQUET -> {
                if (req.getExtraData() instanceof ItemStack bouquetStack) {
                    sender.getInventory().removeOne(bouquetStack);
                    target.getInventory().offerOrDrop(bouquetStack.copy());
                }
                world.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(), 20, 0.5, 0.5, 0.5, 0.05);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.3f);
                target.sendMessage(Text.literal("💐 You received a beautiful flower bouquet from " + sender.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), false);
            }
            case PADDLE_SPANK -> {
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.PADDLE_SPANK_ATTACKER, AnimationType.PADDLE_SPANK_VICTIM, 25);
                world.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.8, target.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SPANK, SoundCategory.PLAYERS, 1.0f, 1.2f);
                Vec3d dir = target.getPos().subtract(sender.getPos()).normalize();
                target.takeKnockback(0.3, -dir.x, -dir.z);
            }
            case LEASH -> {
                PlayerLeashManager.get().attachLeash(sender, target);
            }
            case APPLY_MAKEUP -> {
                if (req.getExtraData() instanceof PlayerMakeupData makeupData) {
                    MakeupManager.get(world).setMakeup(target.getUuid(), makeupData);
                    syncMakeup(target.server, target.getUuid(), makeupData);
                    world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.5f);
                    target.sendMessage(Text.literal("💄 Makeup has been applied by " + sender.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), false);
                }
            }
        }

        return true;
    }

    public boolean decline(ServerPlayerEntity target) {
        PendingRequest req = pendingRequests.remove(target.getUuid());
        if (req == null) return false;

        target.sendMessage(Text.literal("❌ Request declined.").formatted(Formatting.GRAY), false);
        ServerPlayerEntity sender = target.server.getPlayerManager().getPlayer(req.getSender());
        if (sender != null) {
            sender.sendMessage(Text.literal("💔 " + target.getName().getString() + " declined your request.").formatted(Formatting.RED), false);
        }
        return true;
    }

    private void checkJealousy(ServerPlayerEntity actor1, ServerPlayerEntity actor2, ServerWorld world) {
        if (!ModConfig.get().enableJealousyEffects) return;

        // Check if actor1 or actor2 is dating/married to someone else nearby
        Relationship r1 = RelationshipManager.get().getRelationship(actor1.getUuid(), world);
        if (r1 != null && !r1.involves(actor2.getUuid())) {
            UUID p = r1.getPartner(actor1.getUuid());
            ServerPlayerEntity partner = world.getServer().getPlayerManager().getPlayer(p);
            if (partner != null && partner.distanceTo(actor1) <= 10.0) {
                triggerJealousyEffect(partner, world);
            }
        }

        Relationship r2 = RelationshipManager.get().getRelationship(actor2.getUuid(), world);
        if (r2 != null && !r2.involves(actor1.getUuid())) {
            UUID p = r2.getPartner(actor2.getUuid());
            ServerPlayerEntity partner = world.getServer().getPlayerManager().getPlayer(p);
            if (partner != null && partner.distanceTo(actor2) <= 10.0) {
                triggerJealousyEffect(partner, world);
            }
        }
    }

    private void triggerJealousyEffect(ServerPlayerEntity partner, ServerWorld world) {
        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, partner.getX(), partner.getY() + 1.8, partner.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        world.playSound(null, partner.getX(), partner.getY(), partner.getZ(), ModSounds.JEALOUSY, SoundCategory.PLAYERS, 1.0f, 1.0f);
        partner.sendMessage(Text.literal("👀 Hey! What's going on over there?!").formatted(Formatting.RED, Formatting.ITALIC), true);
    }

    private void sendFadeVignette(ServerPlayerEntity player, int ticks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ticks);
        ServerPlayNetworking.send(player, ModPackets.FADE_VIGNETTE_ID, buf);
    }

    public static void syncMakeup(MinecraftServer server, UUID playerUuid, PlayerMakeupData makeupData) {
        PacketByteBuf buf = PacketByteBufs.create();
        makeupData.writeToBuf(buf);
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(p, ModPackets.MAKEUP_SYNC_ID, buf);
        }
    }

    public void tick(MinecraftServer server) {
        if (pendingRequests.isEmpty()) return;

        long currentTick = server.getTicks();
        pendingRequests.entrySet().removeIf(entry -> {
            PendingRequest req = entry.getValue();
            if (req.isExpired(currentTick)) {
                ServerPlayerEntity sender = server.getPlayerManager().getPlayer(req.getSender());
                if (sender != null) {
                    sender.sendMessage(Text.literal("⏳ Request to " + req.getTargetName() + " expired.").formatted(Formatting.GRAY), false);
                }
                return true;
            }
            return false;
        });
    }
}

