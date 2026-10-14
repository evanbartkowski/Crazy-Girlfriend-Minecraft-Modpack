package com.crazygirlfriend.event.world;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Random;

public class WorldEventManager {
    private static final WorldEventManager INSTANCE = new WorldEventManager();
    public static WorldEventManager get() { return INSTANCE; }

    private final Random random = new Random();

    // Server state
    private WorldEventType activeEvent = WorldEventType.NONE;
    private int eventTicksRemaining = 0;
    private float eventIntensity = 1.0f;
    private int checkCooldown = 0;
    private boolean wasRainingLastTick = false;

    // Client state
    private WorldEventType clientEvent = WorldEventType.NONE;
    private int clientTicks = 0;
    private float clientIntensity = 1.0f;

    public void tick(MinecraftServer server) {
        if (!ModConfig.get().enableWorldEvents) return;

        ServerWorld overworld = server.getOverworld();
        if (overworld == null) return;

        long time = overworld.getTimeOfDay() % 24000L;
        boolean isRaining = overworld.isRaining();

        // 1. Tick active event
        if (activeEvent != WorldEventType.NONE) {
            eventTicksRemaining--;
            if (eventTicksRemaining <= 0) {
                activeEvent = WorldEventType.NONE;
                broadcastEventSync(server);
            }
            return;
        }

        // 2. Check for Rainbow after Rain
        if (wasRainingLastTick && !isRaining && time < 12000L) {
            wasRainingLastTick = false;
            if (random.nextInt(3) == 0) {
                triggerEvent(server, WorldEventType.RAINBOW_AFTER_RAIN, 3600, 1.0f);
                return;
            }
        }
        wasRainingLastTick = isRaining;

        // 3. Periodic rare check (every 10 seconds)
        checkCooldown++;
        if (checkCooldown >= 200) {
            checkCooldown = 0;
            checkAndTriggerRareEvents(server, overworld, time, isRaining);
        }
    }

    private void checkAndTriggerRareEvents(MinecraftServer server, ServerWorld world, long time, boolean isRaining) {
        if (isRaining) return;

        // Pink Sunset (late afternoon / dusk)
        if (time >= 11500L && time <= 12800L) {
            if (random.nextInt(8) == 0) {
                triggerEvent(server, WorldEventType.PINK_SUNSET, 2400, 1.0f);
                return;
            }
        }

        // Night Events
        if (time >= 13500L && time <= 22500L) {
            int roll = random.nextInt(100);
            if (roll < 8) {
                triggerEvent(server, WorldEventType.METEOR_SHOWER, 4800, 1.0f);
            } else if (roll < 16) {
                triggerEvent(server, WorldEventType.AURORA_NIGHT, 6000, 1.0f);
            } else if (roll < 24) {
                triggerEvent(server, WorldEventType.ENCHANTED_FOREST, 5000, 1.0f);
            } else if (roll < 32) {
                triggerEvent(server, WorldEventType.FIREFLY_BLOOM, 4000, 1.0f);
            } else if (roll < 40) {
                triggerEvent(server, WorldEventType.LUMINOUS_FLOWERS, 4500, 1.0f);
            }
            return;
        }

        // Early Morning Mist
        if (time >= 23500L || time <= 1500L) {
            if (random.nextInt(8) == 0) {
                triggerEvent(server, WorldEventType.MAGICAL_MIST, 3600, 1.0f);
            }
        }
    }

    public void triggerEvent(MinecraftServer server, WorldEventType event, int durationTicks, float intensity) {
        this.activeEvent = event;
        this.eventTicksRemaining = durationTicks > 0 ? durationTicks : event.getDefaultDurationTicks();
        this.eventIntensity = intensity;

        Text announce = Text.literal("§d[World Event] §fA celestial §e" +
                event.getEmoji() + " " + event.getDisplayName() +
                "§f has begun!");
        server.getPlayerManager().broadcast(announce, false);

        broadcastEventSync(server);
    }

    public void broadcastEventSync(MinecraftServer server) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(activeEvent.name());
        buf.writeInt(eventTicksRemaining);
        buf.writeFloat(eventIntensity);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, ModPackets.WORLD_EVENT_SYNC_ID, buf);
        }
    }

    public void syncToPlayer(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(activeEvent.name());
        buf.writeInt(eventTicksRemaining);
        buf.writeFloat(eventIntensity);

        ServerPlayNetworking.send(player, ModPackets.WORLD_EVENT_SYNC_ID, buf);
    }

    // Client handling
    public void updateClientEvent(WorldEventType event, int ticks, float intensity) {
        this.clientEvent = event;
        this.clientTicks = ticks;
        this.clientIntensity = intensity;
    }

    public WorldEventType getActiveEvent() {
        return activeEvent;
    }

    public WorldEventType getClientEvent() {
        return clientEvent;
    }

    public boolean isEventActive(WorldEventType type) {
        return clientEvent == type;
    }

    public float getClientIntensity() {
        return clientIntensity;
    }
}

