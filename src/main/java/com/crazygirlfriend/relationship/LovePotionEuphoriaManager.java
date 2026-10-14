package com.crazygirlfriend.relationship;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class LovePotionEuphoriaManager {
    private static final LovePotionEuphoriaManager INSTANCE = new LovePotionEuphoriaManager();

    private final Map<UUID, Integer> euphoricPlayers = new java.util.concurrent.ConcurrentHashMap<>();

    public static LovePotionEuphoriaManager get() {
        return INSTANCE;
    }

    public void applyEuphoria(ServerPlayerEntity player, int ticks) {
        euphoricPlayers.put(player.getUuid(), ticks);

        // Sync pink screen overlay packet to client
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ticks);
        ServerPlayNetworking.send(player, ModPackets.LOVE_EUPHORIA_SYNC_ID, buf);
    }

    public boolean isEuphoric(UUID playerUuid) {
        return euphoricPlayers.containsKey(playerUuid);
    }

    public void tick(MinecraftServer server) {
        if (euphoricPlayers.isEmpty()) return;

        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : euphoricPlayers.entrySet()) {
            UUID playerUuid = entry.getKey();
            int remaining = entry.getValue() - 1;

            if (remaining <= 0) {
                toRemove.add(playerUuid);
                continue;
            }
            entry.setValue(remaining);

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            if (player == null || !player.isAlive()) {
                toRemove.add(playerUuid);
                continue;
            }

            ServerWorld world = player.getServerWorld();

            // Spawn floating heart particles around player while in euphoria
            if (remaining % 5 == 0) {
                world.spawnParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 3, 0.4, 0.4, 0.4, 0.05);
            }

            // Check if player gets close to any other player within 2.5 blocks to pounce and make love
            for (ServerPlayerEntity other : world.getPlayers()) {
                if (other.equals(player) || !other.isAlive()) continue;

                if (player.distanceTo(other) <= 2.8) {
                    // Pounce leap towards the partner!
                    Vec3d pounceDir = other.getPos().subtract(player.getPos()).normalize().multiply(0.7).add(0, 0.35, 0);
                    player.setVelocity(pounceDir);
                    player.velocityModified = true;

                    // Immediately trigger MAKE_LOVE interaction!
                    PlayerAnimationTracker.get().startAnimation(player, other, AnimationType.MAKE_LOVE, AnimationType.MAKE_LOVE, 80);
                    RequestManager.get().sendFadeVignette(player, 80);
                    RequestManager.get().sendFadeVignette(other, 80);

                    // Sounds and particle explosions
                    world.spawnParticles(ParticleTypes.HEART, other.getX(), other.getY() + 1.2, other.getZ(), 45, 0.6, 0.6, 0.6, 0.1);
                    world.playSound(null, other.getX(), other.getY(), other.getZ(), ModSounds.GIGGLE, SoundCategory.PLAYERS, 1.2f, 1.0f);
                    world.playSound(null, other.getX(), other.getY(), other.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.3f);

                    // Announce the pounce in chat
                    player.sendMessage(Text.literal("💘 Overwhelmed by the love potion, you pounced on " + other.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);
                    other.sendMessage(Text.literal("💘 " + player.getName().getString() + " pounced on you in an overwhelming surge of romantic euphoria!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);

                    // Consume the euphoria
                    toRemove.add(playerUuid);
                    break;
                }
            }
        }

        for (UUID uuid : toRemove) {
            euphoricPlayers.remove(uuid);
        }
    }
}

