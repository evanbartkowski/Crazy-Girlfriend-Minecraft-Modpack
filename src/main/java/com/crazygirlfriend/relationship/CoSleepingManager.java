package com.crazygirlfriend.relationship;

import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages co-sleeping mechanics, allowing two players to sleep in the same bed
 * simultaneously while sleeping on their sides facing each other.
 */
public class CoSleepingManager {
    private static final CoSleepingManager INSTANCE = new CoSleepingManager();

    // Map of BedPos -> List of Player UUIDs currently sleeping there
    private final Map<BlockPos, Set<UUID>> bedSleepers = new ConcurrentHashMap<>();

    // Map of Player UUID -> Partner UUID they are co-sleeping with
    private final Map<UUID, UUID> coSleepingPairs = new ConcurrentHashMap<>();

    public static CoSleepingManager get() {
        return INSTANCE;
    }

    /**
     * Finds all players currently sleeping in the specified bed (head pos).
     */
    public List<ServerPlayerEntity> getSleepersAt(World world, BlockPos pos) {
        List<ServerPlayerEntity> sleepers = new ArrayList<>();
        if (!(world instanceof ServerWorld serverWorld)) return sleepers;

        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (player.isSleeping()) {
                Optional<BlockPos> sleepPosOpt = player.getSleepingPosition();
                if (sleepPosOpt.isPresent()) {
                    BlockPos p = sleepPosOpt.get();
                    if (p.equals(pos) || p.isWithinDistance(pos, 1.8)) {
                        sleepers.add(player);
                    }
                }
            }
        }
        return sleepers;
    }

    /**
     * Checks if another player can join this bed (up to 2 players max).
     */
    public boolean canSleepInBed(World world, BlockPos pos, PlayerEntity player) {
        List<ServerPlayerEntity> sleepers = getSleepersAt(world, pos);
        return sleepers.size() < 2 || sleepers.contains(player);
    }

    /**
     * Called when a second player gets into bed with an existing sleeper.
     */
    public void onCoSleepStart(ServerPlayerEntity player, ServerPlayerEntity partner, BlockPos bedPos) {
        ServerWorld world = player.getServerWorld();

        // Track pair
        coSleepingPairs.put(player.getUuid(), partner.getUuid());
        coSleepingPairs.put(partner.getUuid(), player.getUuid());

        Set<UUID> sleepers = bedSleepers.computeIfAbsent(bedPos, k -> ConcurrentHashMap.newKeySet());
        sleepers.add(player.getUuid());
        sleepers.add(partner.getUuid());

        // Spawn romantic cozy particles
        double midX = (player.getX() + partner.getX()) / 2.0;
        double midY = Math.max(player.getY(), partner.getY()) + 0.6;
        double midZ = (player.getZ() + partner.getZ()) / 2.0;

        world.spawnParticles(ParticleTypes.HEART, midX, midY, midZ, 15, 0.4, 0.3, 0.4, 0.05);
        world.playSound(null, midX, midY, midZ, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 0.9f);
        world.playSound(null, midX, midY, midZ, ModSounds.GIGGLE, SoundCategory.PLAYERS, 0.8f, 1.1f);

        // Check relationship status
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), world);
        boolean isPartner = rel != null && rel.involves(partner.getUuid());

        if (isPartner) {
            rel.addAffectionXp(25);
            rel.addCrazyMeter(-15);

            player.sendMessage(Text.literal("🛏️ You snuggled into bed next to " + partner.getName().getString() + "! Sleeping on your side together...").formatted(Formatting.LIGHT_PURPLE), true);
            partner.sendMessage(Text.literal("🛏️ " + player.getName().getString() + " snuggled into bed beside you! Sleeping on your side together...").formatted(Formatting.LIGHT_PURPLE), true);
        } else {
            player.sendMessage(Text.literal("🛏️ Sharing the bed with " + partner.getName().getString() + "! Sleeping on your side...").formatted(Formatting.AQUA), true);
            partner.sendMessage(Text.literal("🛏️ " + player.getName().getString() + " hopped into bed with you! Sleeping on your side...").formatted(Formatting.AQUA), true);
        }
    }

    /**
     * Called when the night is skipped and all sleeping players wake up in the morning.
     */
    public void handleMorningWakeUp(ServerWorld world, List<ServerPlayerEntity> wakingPlayers) {
        Set<UUID> processed = new HashSet<>();

        for (ServerPlayerEntity player : wakingPlayers) {
            UUID partnerUuid = coSleepingPairs.remove(player.getUuid());
            if (partnerUuid != null && !processed.contains(player.getUuid())) {
                ServerPlayerEntity partner = world.getServer().getPlayerManager().getPlayer(partnerUuid);
                if (partner != null && wakingPlayers.contains(partner)) {
                    processed.add(player.getUuid());
                    processed.add(partner.getUuid());
                    coSleepingPairs.remove(partner.getUuid());

                    // Resolve morning-after interaction
                    Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), world);
                    if (rel != null && rel.involves(partner.getUuid())) {
                        BedroomManager.resolveMorningAfter(player, partner);
                    } else {
                        player.sendMessage(Text.literal("☀️ Good morning! You survived sharing a bed with " + partner.getName().getString() + "!").formatted(Formatting.YELLOW), false);
                        partner.sendMessage(Text.literal("☀️ Good morning! You survived sharing a bed with " + player.getName().getString() + "!").formatted(Formatting.YELLOW), false);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.2f);
                    }
                }
            }
        }
    }

    /**
     * Called when a player wakes up early (e.g. leaves bed before night skips).
     */
    public void onPlayerWakeUp(ServerPlayerEntity player) {
        UUID partnerUuid = coSleepingPairs.remove(player.getUuid());
        if (partnerUuid != null) {
            coSleepingPairs.remove(partnerUuid);
        }

        // Clean up bedSleepers map
        bedSleepers.entrySet().removeIf(entry -> {
            entry.getValue().remove(player.getUuid());
            if (entry.getValue().isEmpty()) {
                return true;
            } else {
                // If another player is still in the bed, ensure bed remains marked occupied in blockstate
                BlockPos pos = entry.getKey();
                BlockState state = player.getServerWorld().getBlockState(pos);
                if (state.getBlock() instanceof BedBlock && !state.get(BedBlock.OCCUPIED)) {
                    player.getServerWorld().setBlockState(pos, state.with(BedBlock.OCCUPIED, true), 3);
                }
                return false;
            }
        });
    }
}
