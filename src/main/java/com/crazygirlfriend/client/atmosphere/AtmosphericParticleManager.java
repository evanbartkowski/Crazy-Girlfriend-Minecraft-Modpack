package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

        int attempts = Math.max(1, (int) (6 * density));

        for (int i = 0; i < attempts; i++) {
            double rx = player.getX() + (random.nextDouble() - 0.5) * 32.0;
            double rz = player.getZ() + (random.nextDouble() - 0.5) * 32.0;
            double ry = player.getY() + (random.nextDouble() - 0.5) * 14.0 + 2.0;
            BlockPos pos = BlockPos.ofFloored(rx, ry, rz);

            if (!world.isChunkLoaded(pos)) continue;

            // 1. Cherry Groves & Flower Forests (Falling Petals & Spores)
            if (biomeEntry.matchesKey(BiomeKeys.CHERRY_GROVE) || biomeEntry.matchesKey(BiomeKeys.FLOWER_FOREST) || biomeEntry.matchesKey(BiomeKeys.MEADOW)) {
                if (random.nextFloat() < 0.45f) {
                    world.addParticle(
                        ParticleTypes.CHERRY_LEAVES,
                        rx, ry + 4.0, rz,
                        wind.x * 0.12, -0.05 - random.nextDouble() * 0.03, wind.z * 0.12
                    );
                }
                if (random.nextFloat() < 0.35f) {
                    world.addParticle(
                        ParticleTypes.SPORE_BLOSSOM_AIR,
                        rx, ry, rz,
                        wind.x * 0.04, -0.01, wind.z * 0.04
                    );
                }
                // Ambient fluttering butterflies near flowers/ground
                if (!isNight && random.nextFloat() < 0.2f) {
                    BlockState below = world.getBlockState(pos.down());
                    if (below.isOf(Blocks.GRASS_BLOCK) || below.isOf(Blocks.MOSS_BLOCK)) {
                        double flapY = Math.sin(world.getTime() * 0.4 + i) * 0.05;
                        world.addParticle(
                            ParticleTypes.WAX_ON,
                            rx, pos.getY() + 0.8, rz,
                            wind.x * 0.08 + (random.nextDouble() - 0.5) * 0.06,
                            flapY,
                            wind.z * 0.08 + (random.nextDouble() - 0.5) * 0.06
                        );
                    }
                }
            }

            // 2. Forests & Plains (Fireflies at Evening/Night, Pollen by Day)
            if (biomeEntry.matchesKey(BiomeKeys.FOREST) || biomeEntry.matchesKey(BiomeKeys.BIRCH_FOREST) ||
                biomeEntry.matchesKey(BiomeKeys.DARK_FOREST) || biomeEntry.matchesKey(BiomeKeys.OLD_GROWTH_BIRCH_FOREST) ||
                biomeEntry.matchesKey(BiomeKeys.PLAINS) || biomeEntry.matchesKey(BiomeKeys.SUNFLOWER_PLAINS)) {

                if (isNight) {
                    // Fireflies! Glowing, blinking, hovering
                    if (random.nextFloat() < 0.35f) {
                        BlockState state = world.getBlockState(pos);
                        if (state.isAir()) {
                            world.addParticle(
                                ParticleTypes.GLOW,
                                rx, ry, rz,
                                wind.x * 0.03 + (random.nextDouble() - 0.5) * 0.02,
                                0.015 + random.nextDouble() * 0.02,
                                wind.z * 0.03 + (random.nextDouble() - 0.5) * 0.02
                            );
                        }
                    }
                } else {
                    // Floating golden dust motes in daylight
                    if (random.nextFloat() < 0.2f && world.isSkyVisible(pos)) {
                        world.addParticle(
                            ParticleTypes.END_ROD,
                            rx, ry, rz,
                            wind.x * 0.02, -0.008, wind.z * 0.02
                        );
                    }
                }
            }

            // 3. Swamps & Mangroves (Atmospheric Ground Fog Mist & Green Spores)
            if (biomeEntry.matchesKey(BiomeKeys.SWAMP) || biomeEntry.matchesKey(BiomeKeys.MANGROVE_SWAMP)) {
                if (random.nextFloat() < 0.3f) {
                    BlockPos surfacePos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, pos);
                    world.addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        surfacePos.getX() + random.nextDouble(),
                        surfacePos.getY() + 0.15,
                        surfacePos.getZ() + random.nextDouble(),
                        wind.x * 0.02, 0.002, wind.z * 0.02
                    );
                }
                if (isNight && random.nextFloat() < 0.35f) {
                    world.addParticle(
                        ParticleTypes.WARPED_SPORE,
                        rx, ry, rz,
                        (random.nextDouble() - 0.5) * 0.02, 0.01, (random.nextDouble() - 0.5) * 0.02
                    );
                }
            }

            // 4. Oceans, Rivers & Coastlines (Shoreline Foam, Water Ripples, Underwater Bubbles)
            if (biomeEntry.matchesKey(BiomeKeys.OCEAN) || biomeEntry.matchesKey(BiomeKeys.DEEP_OCEAN) ||
                biomeEntry.matchesKey(BiomeKeys.WARM_OCEAN) || biomeEntry.matchesKey(BiomeKeys.LUKEWARM_OCEAN) ||
                biomeEntry.matchesKey(BiomeKeys.RIVER) || biomeEntry.matchesKey(BiomeKeys.BEACH)) {

                BlockState state = world.getBlockState(pos);
                if (state.isOf(Blocks.WATER)) {
                    if (world.getBlockState(pos.up()).isAir() && random.nextFloat() < 0.3f) {
                        // Shoreline splash / foam ripple
                        world.addParticle(
                            ParticleTypes.SPLASH,
                            rx, pos.getY() + 0.95, rz,
                            (random.nextDouble() - 0.5) * 0.05, 0.08, (random.nextDouble() - 0.5) * 0.05
                        );
                    }
                }
            }

            // 5. Deserts & Badlands (Wind-Blown Sand Dust Whisps)
            if (biomeEntry.matchesKey(BiomeKeys.DESERT) || biomeEntry.matchesKey(BiomeKeys.BADLANDS) || biomeEntry.matchesKey(BiomeKeys.WOODED_BADLANDS)) {
                if (random.nextFloat() < 0.4f) {
                    BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, pos);
                    world.addParticle(
                        ParticleTypes.CRIT,
                        surface.getX() + random.nextDouble(),
                        surface.getY() + 0.1 + random.nextDouble() * 0.5,
                        surface.getZ() + random.nextDouble(),
                        wind.x * 0.25, 0.02, wind.z * 0.25
                    );
                }
            }

            // 6. Mountains, Peaks & Snowy Tundras (Blowing Blizzard Snow)
            if (biomeEntry.matchesKey(BiomeKeys.SNOWY_PLAINS) || biomeEntry.matchesKey(BiomeKeys.SNOWY_SLOPES) ||
                biomeEntry.matchesKey(BiomeKeys.JAGGED_PEAKS) || biomeEntry.matchesKey(BiomeKeys.FROZEN_PEAKS) ||
                biomeEntry.matchesKey(BiomeKeys.SNOWY_TAIGA)) {

                if (random.nextFloat() < 0.45f) {
                    world.addParticle(
                        ParticleTypes.SNOWFLAKE,
                        rx, ry, rz,
                        wind.x * 0.18, -0.06 - random.nextDouble() * 0.04, wind.z * 0.18
                    );
                }
            }
        }
    }
}
