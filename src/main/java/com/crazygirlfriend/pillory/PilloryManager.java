package com.crazygirlfriend.pillory;

import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages pillory locking state across server and client.
 */
public class PilloryManager {
    private static final PilloryManager INSTANCE = new PilloryManager();

    // Server-side: playerUuid -> pillory BlockPos
    private final Map<UUID, BlockPos> lockedPlayers = new ConcurrentHashMap<>();
    // Server-side: playerUuid -> jailerUuid
    private final Map<UUID, UUID> jailers = new ConcurrentHashMap<>();

    // Client-side: set of player UUIDs locked in pillories
    private final Set<UUID> clientLockedPlayers = ConcurrentHashMap.newKeySet();

    public static PilloryManager get() {
        return INSTANCE;
    }

    public boolean isPilloryLocked(UUID playerUuid) {
        return lockedPlayers.containsKey(playerUuid) || clientLockedPlayers.contains(playerUuid);
    }

    public BlockPos getPilloryPos(UUID playerUuid) {
        return lockedPlayers.get(playerUuid);
    }

    public UUID getJailer(UUID playerUuid) {
        return jailers.get(playerUuid);
    }

    public void lockPlayer(ServerPlayerEntity target, ServerPlayerEntity jailer, BlockPos pos) {
        lockedPlayers.put(target.getUuid(), pos);
        if (jailer != null) {
            jailers.put(target.getUuid(), jailer.getUuid());
        }

        // Apply embarrassed facial expression
        FacialExpressionManager.get().setExpression(target.getUuid(), FacialExpression.EMBARRASSED, 1200);

        broadcastLockState(target.server, target.getUuid(), true);
    }

    public void unlockPlayer(UUID playerUuid, MinecraftServer server) {
        lockedPlayers.remove(playerUuid);
        jailers.remove(playerUuid);

        if (server != null) {
            broadcastLockState(server, playerUuid, false);
        }
    }

    public void unlockAt(BlockPos pos, MinecraftServer server) {
        for (Map.Entry<UUID, BlockPos> entry : lockedPlayers.entrySet()) {
            if (entry.getValue().equals(pos)) {
                unlockPlayer(entry.getKey(), server);
            }
        }
    }

    public void broadcastLockState(MinecraftServer server, UUID targetUuid, boolean locked) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetUuid);
        buf.writeBoolean(locked);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, ModPackets.PILLORY_SYNC_ID, buf);
        }
    }

    // Client-side methods
    public void updateClientState(UUID targetUuid, boolean locked) {
        if (locked) {
            clientLockedPlayers.add(targetUuid);
        } else {
            clientLockedPlayers.remove(targetUuid);
        }
    }
}

