package com.crazygirlfriend;

import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.block.ModBlocks;
import com.crazygirlfriend.command.CrazyGirlfriendCommands;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.leash.PlayerLeashManager;
import com.crazygirlfriend.makeup.MakeupManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.relationship.RequestManager;
import com.crazygirlfriend.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrazyGirlfriendMod implements ModInitializer {
    public static final String MOD_ID = "crazygirlfriend";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private int periodicHeartTicks = 0;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Crazy Girlfriend Modpack...");

        // Load configuration
        ModConfig.load();

        // Register registries
        ModSounds.register();
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
        ModPackets.registerC2SPackets();
        CrazyGirlfriendCommands.register();

        // Leash restrictions
        PlayerLeashManager.registerRestrictions();

        // Server tick events
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        // Player connection events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            // Sync relationships
            RelationshipManager.get().syncToPlayer(player);
            // Sync leashes
            PlayerLeashManager.get().broadcastLeashState(server);
            // Sync makeups
            MakeupManager makeupMgr = MakeupManager.get(player.getServerWorld());
            for (Map.Entry<UUID, PlayerMakeupData> entry : makeupMgr.getAllMakeup().entrySet()) {
                RequestManager.syncMakeup(server, entry.getKey(), entry.getValue());
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.getPlayer().getUuid();
            PlayerLeashManager.get().releaseLeash(uuid, server);
            PlayerAnimationTracker.get().stopAnimation(uuid, server);
        });

        LOGGER.info("Crazy Girlfriend Mod initialized successfully!");
    }

    private void onServerTick(MinecraftServer server) {
        RequestManager.get().tick(server);
        PlayerAnimationTracker.get().tick(server);
        PlayerLeashManager.get().tick(server);
        com.crazygirlfriend.facial.FacialExpressionManager.get().tick();
        com.crazygirlfriend.relationship.LovePotionEuphoriaManager.get().tick(server);

        periodicHeartTicks++;
        if (periodicHeartTicks >= 60) { // Every 3 seconds
            periodicHeartTicks = 0;
            checkRomanticPartnerProximity(server);
        }
    }

    private void checkRomanticPartnerProximity(MinecraftServer server) {
        if (!ModConfig.get().enableRelationshipHearts) return;

        for (ServerWorld world : server.getWorlds()) {
            List<ServerPlayerEntity> players = world.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                ServerPlayerEntity p1 = players.get(i);
                for (int j = i + 1; j < players.size(); j++) {
                    ServerPlayerEntity p2 = players.get(j);

                    if (p1.distanceTo(p2) <= 3.5) {
                        Relationship rel = RelationshipManager.get().getRelationship(p1.getUuid(), world);
                        if (rel != null && rel.involves(p2.getUuid())) {
                            // Spawn couple hearts between them
                            double midX = (p1.getX() + p2.getX()) / 2.0;
                            double midY = ((p1.getY() + p2.getY()) / 2.0) + 1.2;
                            double midZ = (p1.getZ() + p2.getZ()) / 2.0;

                            world.spawnParticles(ParticleTypes.HEART, midX, midY, midZ, 2, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                }
            }
        }
    }
}

