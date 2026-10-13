package com.crazygirlfriend.leash;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class PlayerLeashManager {
    private static final PlayerLeashManager INSTANCE = new PlayerLeashManager();

    // leashedPlayerUuid -> holderPlayerUuid
    private final Map<UUID, UUID> activeLeashes = new HashMap<>();
    // leashedPlayerUuid -> isSitting
    private final Map<UUID, Boolean> sittingStatus = new HashMap<>();

    public static PlayerLeashManager get() {
        return INSTANCE;
    }

    public boolean isLeashed(UUID playerUuid) {
        return activeLeashes.containsKey(playerUuid);
    }

    public boolean isSitting(UUID playerUuid) {
        return sittingStatus.getOrDefault(playerUuid, false);
    }

    public UUID getHolder(UUID leashedPlayerUuid) {
        return activeLeashes.get(leashedPlayerUuid);
    }

    public void attachLeash(ServerPlayerEntity holder, ServerPlayerEntity leashed) {
        activeLeashes.put(leashed.getUuid(), holder.getUuid());
        sittingStatus.put(leashed.getUuid(), false);

        holder.sendMessage(Text.translatable("message.crazygirlfriend.leashed", leashed.getName().getString()), false);
        leashed.sendMessage(Text.translatable("message.crazygirlfriend.leashed", holder.getName().getString()), false);

        broadcastLeashState(holder.server);
    }

    public void releaseLeash(UUID leashedPlayerUuid, MinecraftServer server) {
        if (activeLeashes.containsKey(leashedPlayerUuid)) {
            UUID holderUuid = activeLeashes.remove(leashedPlayerUuid);
            sittingStatus.remove(leashedPlayerUuid);

            if (server != null) {
                ServerPlayerEntity leashed = server.getPlayerManager().getPlayer(leashedPlayerUuid);
                if (leashed != null) {
                    leashed.sendMessage(Text.translatable("message.crazygirlfriend.unleashed"), false);
                }
                if (holderUuid != null) {
                    ServerPlayerEntity holder = server.getPlayerManager().getPlayer(holderUuid);
                    if (holder != null) {
                        holder.sendMessage(Text.translatable("message.crazygirlfriend.unleashed"), false);
                    }
                }
                broadcastLeashState(server);
            }
        }
    }

    public void toggleSit(ServerPlayerEntity holder, ServerPlayerEntity leashed) {
        boolean nowSitting = !isSitting(leashed.getUuid());
        sittingStatus.put(leashed.getUuid(), nowSitting);

        String statusMsg = nowSitting ? "is now sitting." : "is now standing.";
        holder.sendMessage(Text.literal("🪢 " + leashed.getName().getString() + " " + statusMsg), true);
        leashed.sendMessage(Text.literal("🪢 You are now " + (nowSitting ? "sitting." : "standing.")), true);

        broadcastLeashState(holder.server);
    }

    public void tick(MinecraftServer server) {
        if (activeLeashes.isEmpty()) return;

        List<UUID> toRemove = new ArrayList<>();
        double maxDist = ModConfig.get().leashMaxDistance;

        for (Map.Entry<UUID, UUID> entry : activeLeashes.entrySet()) {
            UUID leashedUuid = entry.getKey();
            UUID holderUuid = entry.getValue();

            ServerPlayerEntity leashed = server.getPlayerManager().getPlayer(leashedUuid);
            ServerPlayerEntity holder = server.getPlayerManager().getPlayer(holderUuid);

            if (leashed == null || holder == null || leashed.isRemoved() || holder.isRemoved() || !leashed.isAlive() || !holder.isAlive()) {
                toRemove.add(leashedUuid);
                continue;
            }

            if (leashed.getWorld() != holder.getWorld()) {
                toRemove.add(leashedUuid);
                continue;
            }

            double dist = leashed.distanceTo(holder);
            if (dist > maxDist) {
                toRemove.add(leashedUuid);
                continue;
            }

            boolean sitting = isSitting(leashedUuid);
            if (dist > 3.2 && !sitting) {
                Vec3d dir = holder.getPos().subtract(leashed.getPos()).normalize();
                double speed = Math.min(0.45, (dist - 3.0) * 0.2);
                leashed.setVelocity(dir.x * speed, Math.max(leashed.getVelocity().y, 0.08), dir.z * speed);
                leashed.velocityModified = true;
            } else if (dist > 5.0 && sitting) {
                Vec3d dir = holder.getPos().subtract(leashed.getPos()).normalize();
                leashed.setVelocity(dir.x * 0.35, 0.05, dir.z * 0.35);
                leashed.velocityModified = true;
            }
        }

        for (UUID uuid : toRemove) {
            releaseLeash(uuid, server);
        }
    }

    public void broadcastLeashState(MinecraftServer server) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(activeLeashes.size());
        for (Map.Entry<UUID, UUID> entry : activeLeashes.entrySet()) {
            buf.writeUuid(entry.getKey());
            buf.writeUuid(entry.getValue());
            buf.writeBoolean(isSitting(entry.getKey()));
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, ModPackets.LEASH_SYNC_ID, buf);
        }
    }

    public static void registerRestrictions() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && ModConfig.get().allowLeashRestrictions && INSTANCE.isLeashed(player.getUuid())) {
                player.sendMessage(Text.translatable("message.crazygirlfriend.leash_restricted"), true);
                return false;
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient && ModConfig.get().allowLeashRestrictions && INSTANCE.isLeashed(player.getUuid())) {
                player.sendMessage(Text.translatable("message.crazygirlfriend.leash_restricted"), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && ModConfig.get().allowLeashRestrictions && INSTANCE.isLeashed(player.getUuid())) {
                player.sendMessage(Text.translatable("message.crazygirlfriend.leash_restricted"), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}

