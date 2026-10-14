package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.event.world.WorldEventManager;
import com.crazygirlfriend.event.world.WorldEventType;
import com.crazygirlfriend.season.Season;
import com.crazygirlfriend.season.SeasonManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Random;

public class AtmosphericParticleManager {
    private static final AtmosphericParticleManager INSTANCE = new AtmosphericParticleManager();
    public static AtmosphericParticleManager get() { return INSTANCE; }

    private final Random random = new Random();

    public void tick(MinecraftClient client) {
        if (!ModConfig.get().enableAmbientParticles || client.world == null || client.player == null) {
            return;
        }

        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        float density = ModConfig.get().particleDensity;
        if (density <= 0.05f) return;

        BlockPos playerPos = player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = world.getBiome(playerPos);
        boolean isNight = world.getTimeOfDay() % 24000L > 12500L && world.getTimeOfDay() % 24000L < 23500L;
        Vec3d wind = WindManager.get().getWindVector();
        Season season = SeasonManager.get().getClientSeason();
        WorldEventType activeEvent = WorldEventManager.get().getClientEvent();

        // ══════════════════════════════════════════════
        // 1. NETHER DIMENSION OVERHAUL
        // ══════════════════════════════════════════════
        if (world.getRegistryKey() == World.NETHER && ModConfig.get().enableNetherVisuals) {
            int netherAttempts = Math.max(1, (int) (8 * density));
            for (int i = 0; i < netherAttempts; i++) {
                double rx = player.getX() + (random.nextDouble() - 0.5) * 32.0;
                double rz = player.getZ() + (random.nextDouble() - 0.5) * 32.0;
                double ry = player.getY() + (random.nextDouble() - 0.5) * 16.0;
                BlockPos pos = BlockPos.ofFloored(rx, ry, rz);

                if (biomeEntry.matchesKey(BiomeKeys.CRIMSON_FOREST)) {
                    world.addParticle(ParticleTypes.CRIMSON_SPORE, rx, ry, rz, wind.x * 0.05, 0.02, wind.z * 0.05);
                } else if (biomeEntry.matchesKey(BiomeKeys.WARPED_FOREST)) {
                    world.addParticle(ParticleTypes.WARPED_SPORE, rx, ry, rz, wind.x * 0.05, 0.02, wind.z * 0.05);
                } else if (biomeEntry.matchesKey(BiomeKeys.SOUL_SAND_VALLEY)) {
                    if (random.nextFloat() < 0.4f) {
                        world.addParticle(ParticleTypes.SOUL, rx, ry, rz, (random.nextDouble() - 0.5) * 0.02, 0.03, (random.nextDouble() - 0.5) * 0.02);
                    }
                    if (random.nextFloat() < 0.3f) {
                        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, rx, ry, rz, 0, 0.01, 0);
                    }
                } else if (biomeEntry.matchesKey(BiomeKeys.BASALT_DELTAS)) {
                    world.addParticle(ParticleTypes.WHITE_ASH, rx, ry, rz, wind.x * 0.1, -0.04, wind.z * 0.1);
                    if (random.nextFloat() < 0.2f) {
                        world.addParticle(ParticleTypes.SMOKE, rx, ry, rz, 0, 0.02, 0);
                    }
                } else {
                    if (random.nextFloat() < 0.35f) {
                        world.addParticle(ParticleTypes.LAVA, rx, ry, rz, 0, 0.05, 0);
                    }
                }
            }
            return;
        }

        // ══════════════════════════════════════════════
        // 2. THE END DIMENSION OVERHAUL
        // ══════════════════════════════════════════════
        if (world.getRegistryKey() == World.END && ModConfig.get().enableEndVisuals) {
            int endAttempts = Math.max(1, (int) (7 * density));
            for (int i = 0; i < endAttempts; i++) {
                double rx = player.getX() + (random.nextDouble() - 0.5) * 36.0;
                double rz = player.getZ() + (random.nextDouble() - 0.5) * 36.0;
                double ry = player.getY() + (random.nextDouble() - 0.5) * 18.0;

                if (random.nextFloat() < 0.45f) {
                    world.addParticle(ParticleTypes.PORTAL, rx, ry, rz, (random.nextDouble() - 0.5) * 0.2, (random.nextDouble() - 0.5) * 0.2, (random.nextDouble() - 0.5) * 0.2);
                }
                if (random.nextFloat() < 0.25f) {
                    world.addParticle(ParticleTypes.END_ROD, rx, ry, rz, wind.x * 0.02, -0.01, wind.z * 0.02);
                }
            }
            return;
        }

        // ══════════════════════════════════════════════
        // 3. MAGICAL UNDERGROUND CAVE OVERHAUL
        // ══════════════════════════════════════════════
        boolean isUnderground = player.getY() < 60 && !world.isSkyVisible(playerPos);
        if (isUnderground && ModConfig.get().enableCaveVisuals) {
            int caveAttempts = Math.max(1, (int) (6 * density));
            for (int i = 0; i < caveAttempts; i++) {
                double rx = player.getX() + (random.nextDouble() - 0.5) * 24.0;
                double rz = player.getZ() + (random.nextDouble() - 0.5) * 24.0;
                double ry = player.getY() + (random.nextDouble() - 0.5) * 12.0;
                BlockPos pos = BlockPos.ofFloored(rx, ry, rz);
                if (!world.isChunkLoaded(pos)) continue;

                if (biomeEntry.matchesKey(BiomeKeys.LUSH_CAVES)) {
                    if (random.nextFloat() < 0.5f) {
                        world.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, rx, ry, rz, (random.nextDouble() - 0.5) * 0.02, -0.01, (random.nextDouble() - 0.5) * 0.02);
                    }
                    if (random.nextFloat() < 0.25f) {
                        world.addParticle(ParticleTypes.GLOW, rx, ry, rz, 0, 0.01, 0);
                    }
                } else if (biomeEntry.matchesKey(BiomeKeys.DRIPSTONE_CAVES)) {
                    if (random.nextFloat() < 0.35f) {
                        world.addParticle(ParticleTypes.DRIPPING_DRIPSTONE_WATER, rx, ry + 2.0, rz, 0, -0.05, 0);
                    }
                } else if (biomeEntry.matchesKey(BiomeKeys.DEEP_DARK) || player.getY() < 0) {
                    if (random.nextFloat() < 0.3f) {
                        world.addParticle(ParticleTypes.SCULK_SOUL, rx, ry, rz, 0, 0.02, 0);
                    }
                    if (random.nextFloat() < 0.2f) {
                        world.addParticle(ParticleTypes.ELECTRIC_SPARK, rx, ry, rz, (random.nextDouble() - 0.5) * 0.03, 0.02, (random.nextDouble() - 0.5) * 0.03);
                    }
                } else {
                    // Standard caves: subtle ambient dust motes
                    if (random.nextFloat() < 0.15f) {
                        world.addParticle(ParticleTypes.SMOKE, rx, ry, rz, 0, 0.005, 0);
                    }
                }
            }
            return;
        }

        // ══════════════════════════════════════════════
        // 4. OVERWORLD SURFACE & SEASONAL ATMOSPHERES
        // ══════════════════════════════════════════════
        int attempts = Math.max(1, (int) (7 * density));

        for (int i = 0; i < attempts; i++) {
            double rx = player.getX() + (random.nextDouble() - 0.5) * 32.0;
            double rz = player.getZ() + (random.nextDouble() - 0.5) * 32.0;
            double ry = player.getY() + (random.nextDouble() - 0.5) * 14.0 + 2.0;
            BlockPos pos = BlockPos.ofFloored(rx, ry, rz);

            if (!world.isChunkLoaded(pos)) continue;

            // 🍂 A. AUTUMN FALLING LEAVES & FOG
            if (season == Season.AUTUMN && ModConfig.get().enableFallingLeaves) {
                if (random.nextFloat() < 0.45f) {
                    // Orange & red autumn leaves falling and drifting on wind
                    world.addParticle(
                        ParticleTypes.CHERRY_LEAVES,
                        rx, ry + 5.0, rz,
                        wind.x * 0.16 + (random.nextDouble() - 0.5) * 0.05,
                        -0.06 - random.nextDouble() * 0.04,
                        wind.z * 0.16 + (random.nextDouble() - 0.5) * 0.05
                    );
                }
            }

            // ❄️ B. WINTER SNOW FLURRIES ACROSS BIOMES
            if (season == Season.WINTER && ModConfig.get().enableSeasons) {
                if (random.nextFloat() < 0.4f) {
                    world.addParticle(
                        ParticleTypes.SNOWFLAKE,
                        rx, ry + 4.0, rz,
                        wind.x * 0.15,
                        -0.07 - random.nextDouble() * 0.04,
                        wind.z * 0.15
                    );
                }
            }

            // ✨ C. RARE WORLD EVENTS PARTICLES
            if (activeEvent == WorldEventType.FIREFLY_BLOOM && isNight) {
                world.addParticle(ParticleTypes.GLOW, rx, ry, rz, (random.nextDouble() - 0.5) * 0.03, 0.02, (random.nextDouble() - 0.5) * 0.03);
            }
            if (activeEvent == WorldEventType.ENCHANTED_FOREST && isNight) {
                world.addParticle(ParticleTypes.WARPED_SPORE, rx, ry, rz, wind.x * 0.04, 0.02, wind.z * 0.04);
                if (random.nextFloat() < 0.3f) {
                    world.addParticle(ParticleTypes.WAX_ON, rx, ry, rz, 0, 0.02, 0);
                }
            }
            if (activeEvent == WorldEventType.LUMINOUS_FLOWERS && isNight) {
                world.addParticle(ParticleTypes.END_ROD, rx, ry, rz, 0, 0.015, 0);
            }
            if (activeEvent == WorldEventType.MAGICAL_MIST) {
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, rx, pos.getY() + 0.2, rz, wind.x * 0.02, 0.002, wind.z * 0.02);
            }

            // 🌸 D. CHERRY GROVES & FLOWER FORESTS
            if (biomeEntry.matchesKey(BiomeKeys.CHERRY_GROVE) || biomeEntry.matchesKey(BiomeKeys.FLOWER_FOREST) || biomeEntry.matchesKey(BiomeKeys.MEADOW)) {
                if (random.nextFloat() < 0.45f) {
                    world.addParticle(
                        ParticleTypes.CHERRY_LEAVES,
                        rx, ry + 4.0, rz,
                        wind.x * 0.12, -0.05 - random.nextDouble() * 0.03, wind.z * 0.12
                    );
                }
                if (random.nextFloat() < 0.35f) {
                    world.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, rx, ry, rz, wind.x * 0.04, -0.01, wind.z * 0.04);
                }
                if (!isNight && random.nextFloat() < 0.2f) {
                    BlockState below = world.getBlockState(pos.down());
                    if (below.isOf(Blocks.GRASS_BLOCK) || below.isOf(Blocks.MOSS_BLOCK)) {
                        double flapY = Math.sin(world.getTime() * 0.4 + i) * 0.05;
                        world.addParticle(
                            ParticleTypes.WAX_ON,
                            rx, pos.getY() + 0.8, rz,
                            wind.x * 0.08 + (random.nextDouble() - 0.5) * 0.06, flapY, wind.z * 0.08 + (random.nextDouble() - 0.5) * 0.06
                        );
                    }
                }
            }

            // 🌲 E. FORESTS & PLAINS
            if (biomeEntry.matchesKey(BiomeKeys.FOREST) || biomeEntry.matchesKey(BiomeKeys.BIRCH_FOREST) ||
                biomeEntry.matchesKey(BiomeKeys.DARK_FOREST) || biomeEntry.matchesKey(BiomeKeys.PLAINS)) {
                if (isNight) {
                    if (random.nextFloat() < 0.35f) {
                        world.addParticle(ParticleTypes.GLOW, rx, ry, rz, wind.x * 0.03 + (random.nextDouble() - 0.5) * 0.02, 0.015, wind.z * 0.03 + (random.nextDouble() - 0.5) * 0.02);
                    }
                } else {
                    if (random.nextFloat() < 0.2f && world.isSkyVisible(pos)) {
                        world.addParticle(ParticleTypes.END_ROD, rx, ry, rz, wind.x * 0.02, -0.008, wind.z * 0.02);
                    }
                }
            }

            // 🌊 F. OCEANS & RIVERS
            if (biomeEntry.matchesKey(BiomeKeys.OCEAN) || biomeEntry.matchesKey(BiomeKeys.DEEP_OCEAN) ||
                biomeEntry.matchesKey(BiomeKeys.WARM_OCEAN) || biomeEntry.matchesKey(BiomeKeys.RIVER) || biomeEntry.matchesKey(BiomeKeys.BEACH)) {
                BlockState state = world.getBlockState(pos);
                if (state.isOf(Blocks.WATER) && world.getBlockState(pos.up()).isAir() && random.nextFloat() < 0.3f) {
                    world.addParticle(ParticleTypes.SPLASH, rx, pos.getY() + 0.95, rz, (random.nextDouble() - 0.5) * 0.05, 0.08, (random.nextDouble() - 0.5) * 0.05);
                }
            }

            // 🏜️ G. DESERTS & BADLANDS
            if (biomeEntry.matchesKey(BiomeKeys.DESERT) || biomeEntry.matchesKey(BiomeKeys.BADLANDS)) {
                if (random.nextFloat() < 0.4f) {
                    BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, pos);
                    world.addParticle(ParticleTypes.CRIT, surface.getX() + random.nextDouble(), surface.getY() + 0.2, surface.getZ() + random.nextDouble(), wind.x * 0.25, 0.02, wind.z * 0.25);
                }
            }
        }
    }
}
