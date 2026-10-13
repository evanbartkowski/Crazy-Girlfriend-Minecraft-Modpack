package com.crazygirlfriend.relationship;

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

import java.util.List;
import java.util.UUID;

public class RelationshipManager {
    private static final RelationshipManager INSTANCE = new RelationshipManager();

    public static RelationshipManager get() {
        return INSTANCE;
    }

    public void save(ServerWorld world) {
        CrazyGirlfriendSaveData.get(world).markDirty();
    }

    public Relationship getRelationship(UUID player, ServerWorld world) {
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);
        for (Relationship rel : data.getRelationships()) {
            if (rel.involves(player)) {
                return rel;
            }
        }
        return null;
    }

    public boolean areInRelationship(UUID p1, UUID p2, ServerWorld world) {
        Relationship rel = getRelationship(p1, world);
        return rel != null && rel.involves(p2);
    }

    public boolean areDating(UUID p1, UUID p2, ServerWorld world) {
        Relationship rel = getRelationship(p1, world);
        return rel != null && rel.involves(p2) && rel.getType() == RelationshipType.DATING;
    }

    public boolean areMarried(UUID p1, UUID p2, ServerWorld world) {
        Relationship rel = getRelationship(p1, world);
        return rel != null && rel.involves(p2) && rel.getType() == RelationshipType.MARRIED;
    }

    public boolean createDating(ServerPlayerEntity p1, ServerPlayerEntity p2) {
        ServerWorld world = p1.getServerWorld();
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);

        // Check if either already in a relationship
        if (getRelationship(p1.getUuid(), world) != null || getRelationship(p2.getUuid(), world) != null) {
            return false;
        }

        Relationship rel = new Relationship(
            p1.getUuid(), p1.getName().getString(),
            p2.getUuid(), p2.getName().getString(),
            RelationshipType.DATING,
            System.currentTimeMillis()
        );
        data.getRelationships().add(rel);
        data.markDirty();

        // Celebration effects
        celebrateDating(p1, p2, world);

        // Announcement
        Text msg = Text.translatable("message.crazygirlfriend.dating_success", p1.getName().getString(), p2.getName().getString());
        p1.server.getPlayerManager().broadcast(msg, false);

        syncToAll(p1.server);
        return true;
    }

    public boolean upgradeToMarriage(ServerPlayerEntity p1, ServerPlayerEntity p2) {
        ServerWorld world = p1.getServerWorld();
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);

        Relationship rel = getRelationship(p1.getUuid(), world);
        if (rel == null || !rel.involves(p2.getUuid())) {
            // Can also marry directly
            rel = new Relationship(
                p1.getUuid(), p1.getName().getString(),
                p2.getUuid(), p2.getName().getString(),
                RelationshipType.MARRIED,
                System.currentTimeMillis()
            );
            data.getRelationships().add(rel);
        } else {
            rel.setType(RelationshipType.MARRIED);
        }
        data.markDirty();

        // Celebration effects
        celebrateMarriage(p1, p2, world);

        // Announcement
        Text msg = Text.translatable("message.crazygirlfriend.marriage_success", p1.getName().getString(), p2.getName().getString());
        p1.server.getPlayerManager().broadcast(msg, false);

        syncToAll(p1.server);
        return true;
    }

    public boolean breakup(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);

        Relationship rel = getRelationship(player.getUuid(), world);
        if (rel == null || rel.getType() != RelationshipType.DATING) {
            return false;
        }

        data.getRelationships().remove(rel);
        data.markDirty();

        // Heartbreak effects
        world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY() + 1.2, player.getZ(), 20, 0.4, 0.4, 0.4, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HEARTBREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);

        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));
        if (partner != null) {
            partner.getServerWorld().spawnParticles(ParticleTypes.DAMAGE_INDICATOR, partner.getX(), partner.getY() + 1.2, partner.getZ(), 20, 0.4, 0.4, 0.4, 0.1);
            partner.getServerWorld().playSound(null, partner.getX(), partner.getY(), partner.getZ(), ModSounds.HEARTBREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

        Text msg = Text.translatable("message.crazygirlfriend.breakup_success", rel.getName1(), rel.getName2());
        player.server.getPlayerManager().broadcast(msg, false);

        syncToAll(player.server);
        return true;
    }

    public boolean divorce(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);

        Relationship rel = getRelationship(player.getUuid(), world);
        if (rel == null || rel.getType() != RelationshipType.MARRIED) {
            return false;
        }

        data.getRelationships().remove(rel);
        data.markDirty();

        // Heartbreak effects
        world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY() + 1.2, player.getZ(), 35, 0.5, 0.5, 0.5, 0.15);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HEARTBREAK, SoundCategory.PLAYERS, 1.0f, 0.9f);

        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(rel.getPartner(player.getUuid()));
        if (partner != null) {
            partner.getServerWorld().spawnParticles(ParticleTypes.DAMAGE_INDICATOR, partner.getX(), partner.getY() + 1.2, partner.getZ(), 35, 0.5, 0.5, 0.5, 0.15);
            partner.getServerWorld().playSound(null, partner.getX(), partner.getY(), partner.getZ(), ModSounds.HEARTBREAK, SoundCategory.PLAYERS, 1.0f, 0.9f);
        }

        Text msg = Text.translatable("message.crazygirlfriend.divorce_success", rel.getName1(), rel.getName2());
        player.server.getPlayerManager().broadcast(msg, false);

        syncToAll(player.server);
        return true;
    }

    private void celebrateDating(ServerPlayerEntity p1, ServerPlayerEntity p2, ServerWorld world) {
        world.spawnParticles(ParticleTypes.HEART, p1.getX(), p1.getY() + 1.2, p1.getZ(), 30, 0.6, 0.6, 0.6, 0.1);
        world.spawnParticles(ParticleTypes.HEART, p2.getX(), p2.getY() + 1.2, p2.getZ(), 30, 0.6, 0.6, 0.6, 0.1);
        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, p2.getX(), p2.getY(), p2.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void celebrateMarriage(ServerPlayerEntity p1, ServerPlayerEntity p2, ServerWorld world) {
        world.spawnParticles(ParticleTypes.HEART, p1.getX(), p1.getY() + 1.5, p1.getZ(), 40, 0.8, 0.8, 0.8, 0.1);
        world.spawnParticles(ParticleTypes.FIREWORK, p1.getX(), p1.getY() + 2.0, p1.getZ(), 30, 0.5, 0.5, 0.5, 0.15);
        world.spawnParticles(ParticleTypes.HEART, p2.getX(), p2.getY() + 1.5, p2.getZ(), 40, 0.8, 0.8, 0.8, 0.1);
        world.spawnParticles(ParticleTypes.FIREWORK, p2.getX(), p2.getY() + 2.0, p2.getZ(), 30, 0.5, 0.5, 0.5, 0.15);

        world.playSound(null, p1.getX(), p1.getY(), p1.getZ(), ModSounds.WEDDING_BELLS, SoundCategory.PLAYERS, 1.2f, 1.0f);
        world.playSound(null, p2.getX(), p2.getY(), p2.getZ(), ModSounds.WEDDING_BELLS, SoundCategory.PLAYERS, 1.2f, 1.0f);
    }

    public void syncToAll(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            syncToPlayer(player);
        }
    }

    public void syncToPlayer(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        CrazyGirlfriendSaveData data = CrazyGirlfriendSaveData.get(world);
        List<Relationship> list = data.getRelationships();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(list.size());
        for (Relationship rel : list) {
            buf.writeUuid(rel.getPlayer1());
            buf.writeString(rel.getName1());
            buf.writeUuid(rel.getPlayer2());
            buf.writeString(rel.getName2());
            buf.writeString(rel.getType().name());
            buf.writeLong(rel.getFormedTimestamp());
            buf.writeBoolean(rel.isAutoAccept());
            buf.writeInt(rel.getCrazyMeter());
            buf.writeInt(rel.getAffectionXp());
            buf.writeString(rel.getMood(rel.getPlayer1()).name());
            buf.writeString(rel.getMood(rel.getPlayer2()).name());
            buf.writeString(rel.getCustomTitle() != null ? rel.getCustomTitle() : "Sweethearts");
        }

        ServerPlayNetworking.send(player, ModPackets.RELATIONSHIP_SYNC_ID, buf);
    }
}

