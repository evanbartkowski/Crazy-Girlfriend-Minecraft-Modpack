package com.crazygirlfriend.relationship;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the life bond between married couples:
 * - Shared Health Bar: taking damage or healing synchronizes both spouses.
 * - Shared Food / Hunger: eating or hunger drain synchronizes both spouses.
 * - Till Death Do Us Part: if one spouse dies, the other spouse also dies.
 */
public class MarriedLifeBondManager {
    private static final MarriedLifeBondManager INSTANCE = new MarriedLifeBondManager();

    // Guard against recursive health/damage syncing
    private static final ThreadLocal<Boolean> IS_SYNCING_HEALTH = ThreadLocal.withInitial(() -> false);
    // Guard against recursive death loops
    private static final Set<UUID> DYING_SPOUSES = ConcurrentHashMap.newKeySet();

    // Track previous food & saturation to detect changes
    private final Map<UUID, Integer> lastFoodLevels = new ConcurrentHashMap<>();
    private final Map<UUID, Float> lastSaturationLevels = new ConcurrentHashMap<>();

    public static MarriedLifeBondManager get() {
        return INSTANCE;
    }

    /**
     * Retrieves the online spouse of the given player if married.
     */
    public ServerPlayerEntity getSpouse(ServerPlayerEntity player) {
        if (player == null || player.isRemoved() || player.server == null) return null;
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        if (rel != null && rel.getType() == RelationshipType.MARRIED) {
            UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
            ServerPlayerEntity spouse = player.server.getPlayerManager().getPlayer(partnerUuid);
            if (spouse != null && spouse.isAlive() && !spouse.isDisconnected()) {
                return spouse;
            }
        }
        return null;
    }

    public boolean isBondActive(ServerPlayerEntity player) {
        if (!ModConfig.get().enableMarriedLifeBond) return false;
        if (player.isCreative() || player.isSpectator()) return false;
        ServerPlayerEntity spouse = getSpouse(player);
        return spouse != null && !spouse.isCreative() && !spouse.isSpectator();
    }

    /**
     * Called whenever a player's health is modified directly (damage or setHealth).
     */
    public void onHealthChanged(ServerPlayerEntity player, float newHealth) {
        if (IS_SYNCING_HEALTH.get()) return;
        if (!isBondActive(player)) return;

        ServerPlayerEntity spouse = getSpouse(player);
        if (spouse != null && spouse.isAlive()) {
            IS_SYNCING_HEALTH.set(true);
            try {
                float targetHealth = Math.min(newHealth, spouse.getMaxHealth());
                spouse.setHealth(targetHealth);
                spouse.setAbsorptionAmount(player.getAbsorptionAmount());

                // Visual/audio feedback if significant damage occurred
                if (newHealth < player.getHealth() - 1.0f) {
                    ServerWorld world = spouse.getServerWorld();
                    world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, spouse.getX(), spouse.getY() + 1.0, spouse.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
                    spouse.sendMessage(Text.literal("💔 You felt your spouse's pain! Health synchronized.").formatted(Formatting.RED), true);
                }
            } finally {
                IS_SYNCING_HEALTH.set(false);
            }
        }
    }

    /**
     * Called on each server tick for a player to reconcile health and synchronize food.
     */
    public void tick(ServerPlayerEntity player) {
        if (!isBondActive(player)) {
            lastFoodLevels.remove(player.getUuid());
            lastSaturationLevels.remove(player.getUuid());
            return;
        }

        ServerPlayerEntity spouse = getSpouse(player);
        if (spouse == null || !spouse.isAlive()) return;

        // 1. Health Bar Reconciliation
        if (!IS_SYNCING_HEALTH.get()) {
            float h1 = player.getHealth();
            float h2 = spouse.getHealth();
            if (Math.abs(h1 - h2) > 0.2f) {
                float target = Math.min(h1, h2);
                IS_SYNCING_HEALTH.set(true);
                try {
                    player.setHealth(target);
                    spouse.setHealth(target);
                } finally {
                    IS_SYNCING_HEALTH.set(false);
                }
            }

            // Sync absorption hearts
            float abs1 = player.getAbsorptionAmount();
            float abs2 = spouse.getAbsorptionAmount();
            if (Math.abs(abs1 - abs2) > 0.5f) {
                float maxAbs = Math.max(abs1, abs2);
                player.setAbsorptionAmount(maxAbs);
                spouse.setAbsorptionAmount(maxAbs);
            }
        }

        // 2. Food Bar (Hunger & Saturation) Synchronization
        if (ModConfig.get().enableMarriedFoodBond) {
            HungerManager hm1 = player.getHungerManager();
            HungerManager hm2 = spouse.getHungerManager();

            int food1 = hm1.getFoodLevel();
            float sat1 = hm1.getSaturationLevel();
            int prevFood = lastFoodLevels.getOrDefault(player.getUuid(), food1);
            float prevSat = lastSaturationLevels.getOrDefault(player.getUuid(), sat1);

            // If player's hunger or saturation changed, push to spouse
            if (food1 != prevFood || Math.abs(sat1 - prevSat) > 0.1f) {
                hm2.setFoodLevel(food1);
                hm2.setSaturationLevel(sat1);
                lastFoodLevels.put(spouse.getUuid(), food1);
                lastSaturationLevels.put(spouse.getUuid(), sat1);
            }

            lastFoodLevels.put(player.getUuid(), food1);
            lastSaturationLevels.put(player.getUuid(), sat1);
        }
    }

    /**
     * Called when a player dies. If married, their spouse dies of a broken heart.
     */
    public void onSpouseDied(ServerPlayerEntity deadPlayer, DamageSource source) {
        if (!ModConfig.get().enableMarriedDeathBond) return;
        if (DYING_SPOUSES.contains(deadPlayer.getUuid())) return;

        ServerPlayerEntity spouse = getSpouse(deadPlayer);
        if (spouse != null && spouse.isAlive() && !DYING_SPOUSES.contains(spouse.getUuid())) {
            DYING_SPOUSES.add(deadPlayer.getUuid());
            DYING_SPOUSES.add(spouse.getUuid());

            try {
                MinecraftServer server = deadPlayer.server;
                // Broadcast dramatic marriage death message
                Text deathMsg = Text.literal("💔 " + spouse.getName().getString() + " died of a broken heart because their spouse " + deadPlayer.getName().getString() + " perished! Till death do us part...")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC);
                server.getPlayerManager().broadcast(deathMsg, false);

                // Heartbreak particles and sound on surviving spouse
                ServerWorld spouseWorld = spouse.getServerWorld();
                spouseWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, spouse.getX(), spouse.getY() + 1.0, spouse.getZ(), 25, 0.5, 0.5, 0.5, 0.1);
                spouseWorld.spawnParticles(ParticleTypes.SOUL, spouse.getX(), spouse.getY() + 1.0, spouse.getZ(), 20, 0.4, 0.4, 0.4, 0.05);
                spouseWorld.playSound(null, spouse.getX(), spouse.getY(), spouse.getZ(), ModSounds.RECORD_SCRATCH, SoundCategory.PLAYERS, 1.2f, 0.9f);

                // Send immediate actionbar message
                spouse.sendMessage(Text.literal("💔 Till death do us part! You followed your spouse into the afterlife...").formatted(Formatting.RED, Formatting.BOLD), true);

                // Inflict fatal damage on spouse
                spouse.damage(spouse.getDamageSources().genericKill(), Float.MAX_VALUE);
            } finally {
                // Clean up after slight delay
                serverTickCleanup(deadPlayer.server, deadPlayer.getUuid(), spouse.getUuid());
            }
        }
    }

    private void serverTickCleanup(MinecraftServer server, UUID id1, UUID id2) {
        if (server == null) return;
        // Clean up dying set on the next tick so both deaths are finalized without recursion
        server.execute(() -> {
            DYING_SPOUSES.remove(id1);
            DYING_SPOUSES.remove(id2);
        });
    }
}
