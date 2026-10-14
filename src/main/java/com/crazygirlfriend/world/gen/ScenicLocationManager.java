package com.crazygirlfriend.world.gen;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScenicLocationManager {
    private static final ScenicLocationManager INSTANCE = new ScenicLocationManager();
    public static ScenicLocationManager get() { return INSTANCE; }

    private final List<BlockPos> scenicSpots = new CopyOnWriteArrayList<>();

    public void registerScenicSpot(BlockPos pos) {
        if (!scenicSpots.contains(pos)) {
            scenicSpots.add(pos);
        }
    }

    public BlockPos getNearestScenicSpot(BlockPos pos) {
        if (scenicSpots.isEmpty()) return null;

        BlockPos nearest = null;
        double minDistSq = Double.MAX_VALUE;

        for (BlockPos spot : scenicSpots) {
            double distSq = spot.getSquaredDistance(pos);
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = spot;
            }
        }
        return nearest;
    }

    public void locateForPlayer(ServerPlayerEntity player) {
        BlockPos currentPos = player.getBlockPos();
        BlockPos nearest = getNearestScenicSpot(currentPos);

        if (nearest == null) {
            player.sendMessage(Text.literal("🌸 Exploring undiscovered lands... Keep adventuring to uncover romantic gazebos and scenic overlooks!").formatted(Formatting.LIGHT_PURPLE), false);
            return;
        }

        int dist = (int) Math.sqrt(nearest.getSquaredDistance(currentPos));
        player.sendMessage(Text.literal("════════ Romantic Scenic Spot ════════").formatted(Formatting.LIGHT_PURPLE));
        player.sendMessage(Text.literal(" • Nearest Location: [" + nearest.getX() + ", " + nearest.getY() + ", " + nearest.getZ() + "]").formatted(Formatting.GOLD));
        player.sendMessage(Text.literal(" • Distance: " + dist + " blocks away").formatted(Formatting.AQUA));
        player.sendMessage(Text.literal(" • Tip: Take your partner here for photos, dates, and stargazing!").formatted(Formatting.YELLOW));
        player.sendMessage(Text.literal("═══════════════════════════════════════").formatted(Formatting.LIGHT_PURPLE));
    }
}

