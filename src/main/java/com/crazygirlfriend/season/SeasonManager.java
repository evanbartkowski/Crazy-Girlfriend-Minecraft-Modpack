package com.crazygirlfriend.season;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;

public class SeasonManager {
    private static final SeasonManager INSTANCE = new SeasonManager();
    public static SeasonManager get() { return INSTANCE; }

    // Server-side state
    private Season currentSeason = Season.SPRING;
    private long seasonTicks = 0L;
    private boolean isLocked = false;
    private Season lockedSeason = Season.SPRING;
    private int syncCounter = 0;

    // Client-side state
    private Season clientSeason = Season.SPRING;
    private float clientProgress = 0.0f;
    private int clientDay = 1;
    private int clientTotalDays = 7;
    private boolean clientLocked = false;

    public static final long TICKS_PER_DAY = 24000L;

    public void tick(MinecraftServer server) {
        if (!ModConfig.get().enableSeasons) return;

        int daysPerSeason = Math.max(1, ModConfig.get().seasonDays);
        long totalSeasonTicks = daysPerSeason * TICKS_PER_DAY;

        if (isLocked) {
            currentSeason = lockedSeason;
        } else {
            seasonTicks++;
            if (seasonTicks >= totalSeasonTicks) {
                seasonTicks = 0;
                currentSeason = currentSeason.getNext();

                // Broadcast season change announcement
                Text msg = Text.literal("§d[Season] §fThe world has blossomed into §e" +
                        currentSeason.getEmoji() + " " + currentSeason.getDisplayName() +
                        "§f! §7(" + currentSeason.getDescription() + ")");
                server.getPlayerManager().broadcast(msg, false);
            }
        }

        syncCounter++;
        if (syncCounter >= 100) { // Every 5 seconds
            syncCounter = 0;
            broadcastSeasonSync(server);
        }
    }

    public void broadcastSeasonSync(MinecraftServer server) {
        int daysPerSeason = Math.max(1, ModConfig.get().seasonDays);
        long totalSeasonTicks = daysPerSeason * TICKS_PER_DAY;
        float progress = MathHelper.clamp((float) seasonTicks / (float) totalSeasonTicks, 0.0f, 1.0f);
        int currentDay = (int) (seasonTicks / TICKS_PER_DAY) + 1;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(currentSeason.name());
        buf.writeFloat(progress);
        buf.writeInt(currentDay);
        buf.writeInt(daysPerSeason);
        buf.writeBoolean(isLocked);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, ModPackets.SEASON_SYNC_ID, buf);
        }
    }

    public void syncToPlayer(ServerPlayerEntity player) {
        int daysPerSeason = Math.max(1, ModConfig.get().seasonDays);
        long totalSeasonTicks = daysPerSeason * TICKS_PER_DAY;
        float progress = MathHelper.clamp((float) seasonTicks / (float) totalSeasonTicks, 0.0f, 1.0f);
        int currentDay = (int) (seasonTicks / TICKS_PER_DAY) + 1;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(currentSeason.name());
        buf.writeFloat(progress);
        buf.writeInt(currentDay);
        buf.writeInt(daysPerSeason);
        buf.writeBoolean(isLocked);

        ServerPlayNetworking.send(player, ModPackets.SEASON_SYNC_ID, buf);
    }

    public void setSeason(Season season, MinecraftServer server) {
        this.currentSeason = season;
        this.seasonTicks = 0;
        broadcastSeasonSync(server);
    }

    public void setLocked(boolean locked, Season season, MinecraftServer server) {
        this.isLocked = locked;
        if (season != null) {
            this.lockedSeason = season;
            this.currentSeason = season;
        }
        broadcastSeasonSync(server);
    }

    // Client-side handlers
    public void updateClientState(Season season, float progress, int day, int totalDays, boolean locked) {
        this.clientSeason = season;
        this.clientProgress = progress;
        this.clientDay = day;
        this.clientTotalDays = totalDays;
        this.clientLocked = locked;
    }

    public Season getClientSeason() {
        return clientSeason;
    }

    public float getClientProgress() {
        return clientProgress;
    }

    public int getClientDay() {
        return clientDay;
    }

    public int getClientTotalDays() {
        return clientTotalDays;
    }

    public boolean isClientLocked() {
        return clientLocked;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public int getSeasonDays() {
        return ModConfig.get().seasonDays;
    }

    /**
     * Blends vanilla foliage color with the active season's palette.
     */
    public int getBlendedFoliageColor(BlockRenderView world, BlockPos pos, int vanillaColor) {
        if (!ModConfig.get().enableSeasons || !ModConfig.get().enableSeasonalFoliage) {
            return vanillaColor;
        }

        int vr = (vanillaColor >> 16) & 0xFF;
        int vg = (vanillaColor >> 8) & 0xFF;
        int vb = vanillaColor & 0xFF;

        float weight = 0.55f; // Blend weight with season

        int targetTint = clientSeason.getFoliageColorTint();

        // In Autumn, add deterministic natural variation so some trees are orange, red, or golden amber
        if (clientSeason == Season.AUTUMN && pos != null) {
            long hash = (long) pos.getX() * 3129871L ^ (long) pos.getZ() * 116129781L ^ (long) (pos.getY() / 4);
            int variant = (int) Math.abs(hash % 3);
            if (variant == 0) {
                targetTint = 0xD84018; // Fiery Crimson Red
            } else if (variant == 1) {
                targetTint = 0xE68A20; // Warm Pumpkin Orange
            } else {
                targetTint = 0xD8B228; // Golden Amber Yellow
            }
        }

        // If near end of season, smoothly transition to next season
        if (clientProgress > 0.8f) {
            float transProgress = (clientProgress - 0.8f) / 0.2f;
            int nextTint = clientSeason.getNext().getFoliageColorTint();
            targetTint = lerpColor(targetTint, nextTint, transProgress);
        }

        int tr = (targetTint >> 16) & 0xFF;
        int tg = (targetTint >> 8) & 0xFF;
        int tb = targetTint & 0xFF;

        int finalR = MathHelper.clamp((int) (vr * (1.0f - weight) + tr * weight), 0, 255);
        int finalG = MathHelper.clamp((int) (vg * (1.0f - weight) + tg * weight), 0, 255);
        int finalB = MathHelper.clamp((int) (vb * (1.0f - weight) + tb * weight), 0, 255);

        return (finalR << 16) | (finalG << 8) | finalB;
    }

    /**
     * Blends vanilla grass color with the active season's palette.
     */
    public int getBlendedGrassColor(BlockRenderView world, BlockPos pos, int vanillaColor) {
        if (!ModConfig.get().enableSeasons || !ModConfig.get().enableSeasonalFoliage) {
            return vanillaColor;
        }

        int vr = (vanillaColor >> 16) & 0xFF;
        int vg = (vanillaColor >> 8) & 0xFF;
        int vb = vanillaColor & 0xFF;

        float weight = 0.45f;
        int targetTint = clientSeason.getGrassColorTint();

        if (clientProgress > 0.8f) {
            float transProgress = (clientProgress - 0.8f) / 0.2f;
            int nextTint = clientSeason.getNext().getGrassColorTint();
            targetTint = lerpColor(targetTint, nextTint, transProgress);
        }

        int tr = (targetTint >> 16) & 0xFF;
        int tg = (targetTint >> 8) & 0xFF;
        int tb = targetTint & 0xFF;

        int finalR = MathHelper.clamp((int) (vr * (1.0f - weight) + tr * weight), 0, 255);
        int finalG = MathHelper.clamp((int) (vg * (1.0f - weight) + tg * weight), 0, 255);
        int finalB = MathHelper.clamp((int) (vb * (1.0f - weight) + tb * weight), 0, 255);

        return (finalR << 16) | (finalG << 8) | finalB;
    }

    private int lerpColor(int colorA, int colorB, float delta) {
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int r = (int) (rA + (rB - rA) * delta);
        int g = (int) (gA + (gB - gA) * delta);
        int b = (int) (bA + (bB - bA) * delta);

        return (r << 16) | (g << 8) | b;
    }
}

