package com.crazygirlfriend.client.bed;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks client-side sleeping positions and side-sleeping orientations.
 * Allows players to lie side-by-side on their sides facing each other when sharing a bed.
 */
public class CoSleepingClientTracker {
    private static final CoSleepingClientTracker INSTANCE = new CoSleepingClientTracker();

    // Set of player UUIDs who have flipped their side-sleeping direction
    private final Set<UUID> flippedPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static CoSleepingClientTracker get() {
        return INSTANCE;
    }

    /**
     * Determines whether the player is sleeping alone (0), on the left side of the bed (-1),
     * or on the right side of the bed (+1).
     */
    public static int getBedSlot(AbstractClientPlayerEntity player) {
        if (!player.isInPose(EntityPose.SLEEPING)) {
            return 0;
        }

        Optional<BlockPos> sleepPosOpt = player.getSleepingPosition();
        if (sleepPosOpt.isEmpty()) {
            return 0;
        }

        BlockPos sleepPos = sleepPosOpt.get();
        if (!(player.getWorld() instanceof ClientWorld world)) {
            return 0;
        }

        List<AbstractClientPlayerEntity> coSleepers = new ArrayList<>();
        for (AbstractClientPlayerEntity other : world.getPlayers()) {
            if (other.isInPose(EntityPose.SLEEPING)) {
                Optional<BlockPos> otherPosOpt = other.getSleepingPosition();
                if (otherPosOpt.isPresent()) {
                    BlockPos otherPos = otherPosOpt.get();
                    if (otherPos.equals(sleepPos) || otherPos.isWithinDistance(sleepPos, 1.8)) {
                        coSleepers.add(other);
                    }
                }
            }
        }

        if (coSleepers.size() <= 1) {
            return 0; // Sleeping alone
        }

        // Deterministically sort co-sleepers by UUID so both clients agree
        coSleepers.sort(Comparator.comparing(Entity::getUuid));

        if (coSleepers.get(0).equals(player)) {
            return -1; // Left side of the bed
        } else {
            return 1;  // Right side of the bed
        }
    }

    /**
     * Lateral shift across bed width (perpendicular to head-to-toe bed axis).
     */
    public static float getLateralOffset(AbstractClientPlayerEntity player) {
        int slot = getBedSlot(player);
        if (slot == -1) {
            return -0.28f;
        } else if (slot == 1) {
            return 0.28f;
        }
        return 0.0f;
    }

    /**
     * Roll angle around the player's spine (local Y axis in setupTransforms).
     * Rolls the player 80 degrees onto their side.
     */
    public static float getRollAngle(AbstractClientPlayerEntity player) {
        int slot = getBedSlot(player);
        float baseRoll;
        if (slot == 1) {
            baseRoll = -80.0f; // Right sleeper rolls onto left side, facing center
        } else {
            baseRoll = 80.0f;  // Left or single sleeper rolls onto right side
        }

        if (INSTANCE.flippedPlayers.contains(player.getUuid())) {
            baseRoll = -baseRoll;
        }
        return baseRoll;
    }

    public boolean isFlipped(UUID uuid) {
        return flippedPlayers.contains(uuid);
    }

    public void toggleFlip(UUID uuid) {
        if (flippedPlayers.contains(uuid)) {
            flippedPlayers.remove(uuid);
        } else {
            flippedPlayers.add(uuid);
        }
    }

    public void setFlipped(UUID uuid, boolean flipped) {
        if (flipped) {
            flippedPlayers.add(uuid);
        } else {
            flippedPlayers.remove(uuid);
        }
    }
}
