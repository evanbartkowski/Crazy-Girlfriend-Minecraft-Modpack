package com.crazygirlfriend.animation;

import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class PlayerAnimationTracker {
    private static final PlayerAnimationTracker INSTANCE = new PlayerAnimationTracker();

    public static class ActiveAnimation {
        public final AnimationType type;
        public final UUID partnerUuid;
        public int remainingTicks;
        public final int totalDuration;

        public ActiveAnimation(AnimationType type, UUID partnerUuid, int duration) {
            this.type = type;
            this.partnerUuid = partnerUuid;
            this.remainingTicks = duration;
            this.totalDuration = duration;
        }
    }

    private final Map<UUID, ActiveAnimation> activeAnimations = new HashMap<>();

    public static PlayerAnimationTracker get() {
        return INSTANCE;
    }

    public void startAnimation(ServerPlayerEntity player1, ServerPlayerEntity player2, AnimationType type1, AnimationType type2, int duration) {
        if (player1 != null) {
            activeAnimations.put(player1.getUuid(), new ActiveAnimation(type1, player2 != null ? player2.getUuid() : null, duration));
            broadcastAnimationPacket(player1.server, player1.getUuid(), player2 != null ? player2.getUuid() : null, type1, duration);
        }
        if (player2 != null && type2 != AnimationType.NONE) {
            activeAnimations.put(player2.getUuid(), new ActiveAnimation(type2, player1.getUuid(), duration));
            broadcastAnimationPacket(player2.server, player2.getUuid(), player1.getUuid(), type2, duration);
        }
    }

    public void startSingleAnimation(ServerPlayerEntity player, AnimationType type, int duration) {
        if (player != null) {
            activeAnimations.put(player.getUuid(), new ActiveAnimation(type, null, duration));
            broadcastAnimationPacket(player.server, player.getUuid(), null, type, duration);
        }
    }

    public void stopAnimation(UUID playerUuid, MinecraftServer server) {
        if (activeAnimations.remove(playerUuid) != null && server != null) {
            broadcastAnimationPacket(server, playerUuid, null, AnimationType.NONE, 0);
        }
    }

    public boolean isPlaying(UUID playerUuid) {
        return activeAnimations.containsKey(playerUuid);
    }

    public void tick(MinecraftServer server) {
        if (activeAnimations.isEmpty()) return;

        List<UUID> finished = new ArrayList<>();
        for (Map.Entry<UUID, ActiveAnimation> entry : activeAnimations.entrySet()) {
            ActiveAnimation anim = entry.getValue();
            if (anim.totalDuration > 0) { // duration of 0 means continuous (like piggyback or leash sit)
                anim.remainingTicks--;
                if (anim.remainingTicks <= 0) {
                    finished.add(entry.getKey());
                }
            }
        }

        for (UUID uuid : finished) {
            stopAnimation(uuid, server);
        }
    }

    private void broadcastAnimationPacket(MinecraftServer server, UUID playerUuid, UUID partnerUuid, AnimationType type, int duration) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerUuid);
        buf.writeBoolean(partnerUuid != null);
        if (partnerUuid != null) {
            buf.writeUuid(partnerUuid);
        }
        buf.writeVarInt(type.ordinal());
        buf.writeVarInt(duration);

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(p, ModPackets.ANIMATION_SYNC_ID, buf);
        }
    }
}

