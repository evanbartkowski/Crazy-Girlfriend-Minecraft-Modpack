package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Random;

public class AmbientSoundManager {
    private static final AmbientSoundManager INSTANCE = new AmbientSoundManager();
    public static AmbientSoundManager get() { return INSTANCE; }

    private final Random random = new Random();
    private int soundCooldown = 80;

    public void tick(MinecraftClient client) {
        if (!ModConfig.get().enableAmbientAudio || client.world == null || client.player == null) {
            return;
        }

        soundCooldown--;
        if (soundCooldown > 0) return;
        soundCooldown = 80 + random.nextInt(120); // Play ambient sound every 4-10 seconds

        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        float volume = ModConfig.get().ambientAudioVolume;
        if (volume <= 0.05f) return;

        BlockPos pos = player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        long timeOfDay = world.getTimeOfDay() % 24000L;
        boolean isDay = timeOfDay >= 1000L && timeOfDay <= 12000L;
        boolean isNight = timeOfDay > 13000L && timeOfDay < 23000L;

        // 1. Forest & Plains Daytime Birds
        if (isDay && !world.isRaining() && (biomeEntry.matchesKey(BiomeKeys.FOREST) || biomeEntry.matchesKey(BiomeKeys.BIRCH_FOREST) ||
            biomeEntry.matchesKey(BiomeKeys.CHERRY_GROVE) || biomeEntry.matchesKey(BiomeKeys.MEADOW) || biomeEntry.matchesKey(BiomeKeys.PLAINS))) {
            world.playSound(player.getX(), player.getY(), player.getZ(), ModSounds.BIRDS_MORNING, SoundCategory.AMBIENT, volume * 0.35f, 0.95f + random.nextFloat() * 0.15f, false);
            return;
        }

        // 2. Nighttime Crickets in Plains, Forests, Swamps
        if (isNight && !world.isRaining() && (biomeEntry.matchesKey(BiomeKeys.PLAINS) || biomeEntry.matchesKey(BiomeKeys.FOREST) ||
            biomeEntry.matchesKey(BiomeKeys.SWAMP) || biomeEntry.matchesKey(BiomeKeys.MANGROVE_SWAMP) || biomeEntry.matchesKey(BiomeKeys.MEADOW))) {
            world.playSound(player.getX(), player.getY(), player.getZ(), ModSounds.CRICKETS_NIGHT, SoundCategory.AMBIENT, volume * 0.3f, 0.9f + random.nextFloat() * 0.2f, false);
            return;
        }

        // 3. Ocean & Beach Waves
        if (biomeEntry.matchesKey(BiomeKeys.BEACH) || biomeEntry.matchesKey(BiomeKeys.STONY_SHORE) ||
            biomeEntry.matchesKey(BiomeKeys.OCEAN) || biomeEntry.matchesKey(BiomeKeys.DEEP_OCEAN) || biomeEntry.matchesKey(BiomeKeys.WARM_OCEAN)) {
            world.playSound(player.getX(), player.getY(), player.getZ(), ModSounds.OCEAN_WAVES, SoundCategory.AMBIENT, volume * 0.45f, 0.9f + random.nextFloat() * 0.15f, false);
            return;
        }

        // 4. Breezy Wind
        if (WindManager.get().getWindSpeed() > 0.6f || world.isRaining()) {
            world.playSound(player.getX(), player.getY(), player.getZ(), ModSounds.WIND_BREEZE, SoundCategory.AMBIENT, volume * 0.3f, 0.85f + random.nextFloat() * 0.2f, false);
        }
    }
}
