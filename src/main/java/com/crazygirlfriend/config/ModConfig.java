package com.crazygirlfriend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File getConfigFile() {
        try {
            if (FabricLoader.getInstance() != null && FabricLoader.getInstance().getConfigDir() != null) {
                return new File(FabricLoader.getInstance().getConfigDir().toFile(), "crazygirlfriend.json");
            }
        } catch (Throwable ignored) {}
        return new File("config", "crazygirlfriend.json");
    }

    public boolean allowLeashRestrictions = true;
    public double leashMaxDistance = 16.0;
    public int interactionTimeoutSeconds = 30;
    public double slapKnockback = 0.4;
    public float slipperDamage = 1.0f;
    public double slipperKnockback = 0.5;
    public boolean enableRelationshipHearts = true;
    public boolean enableJealousyEffects = true;
    public boolean autoAcceptForPartners = false;
    public boolean enableCrazyGirlfriendMode = true;
    public int eventIntervalSeconds = 120;
    public boolean enableMinigames = true;
    public boolean enableCinematicCamera = true;

    // Visual & Atmospheric Settings
    public String visualPreset = "HIGH"; // LOW, MEDIUM, HIGH, ULTRA
    public float particleDensity = 1.2f;
    public boolean enableWindSway = true;
    public boolean enableAmbientParticles = true;
    public boolean enableAtmosphericSky = true;
    public boolean enableCustomWeather = true;
    public boolean enableAmbientAudio = true;
    public float ambientAudioVolume = 0.7f;
    public boolean photosensitivityMode = false;
    public boolean enableCustomUI = true;

    // Seasons & World Immersion Settings
    public boolean enableSeasons = true;
    public int seasonDays = 7;
    public boolean enableSeasonalFoliage = true;
    public boolean enableFallingLeaves = true;
    public boolean enableMovementAnimations = true;
    public boolean enableWorldEvents = true;
    public boolean enableColorGrading = true;
    public boolean enableCaveVisuals = true;
    public boolean enableNetherVisuals = true;
    public boolean enableEndVisuals = true;
    public boolean enablePhotoMode = true;

    public void applyPreset(String preset) {
        if (preset == null) return;
        this.visualPreset = preset.toUpperCase();
        switch (this.visualPreset) {
            case "LOW":
                this.particleDensity = 0.4f;
                this.enableWindSway = false;
                this.enableAmbientParticles = false;
                this.enableAtmosphericSky = false;
                this.enableCustomWeather = false;
                this.enableAmbientAudio = false;
                this.enableSeasonalFoliage = false;
                this.enableFallingLeaves = false;
                this.enableMovementAnimations = false;
                this.enableWorldEvents = false;
                this.enableColorGrading = false;
                this.enableCaveVisuals = false;
                this.enableNetherVisuals = false;
                this.enableEndVisuals = false;
                break;
            case "MEDIUM":
                this.particleDensity = 0.8f;
                this.enableWindSway = true;
                this.enableAmbientParticles = true;
                this.enableAtmosphericSky = true;
                this.enableCustomWeather = true;
                this.enableAmbientAudio = true;
                this.ambientAudioVolume = 0.5f;
                this.enableSeasonalFoliage = true;
                this.enableFallingLeaves = true;
                this.enableMovementAnimations = true;
                this.enableWorldEvents = true;
                this.enableColorGrading = true;
                this.enableCaveVisuals = true;
                this.enableNetherVisuals = true;
                this.enableEndVisuals = true;
                break;
            case "ULTRA":
                this.particleDensity = 1.8f;
                this.enableWindSway = true;
                this.enableAmbientParticles = true;
                this.enableAtmosphericSky = true;
                this.enableCustomWeather = true;
                this.enableAmbientAudio = true;
                this.ambientAudioVolume = 0.85f;
                this.enableSeasonalFoliage = true;
                this.enableFallingLeaves = true;
                this.enableMovementAnimations = true;
                this.enableWorldEvents = true;
                this.enableColorGrading = true;
                this.enableCaveVisuals = true;
                this.enableNetherVisuals = true;
                this.enableEndVisuals = true;
                break;
            case "HIGH":
            default:
                this.visualPreset = "HIGH";
                this.particleDensity = 1.2f;
                this.enableWindSway = true;
                this.enableAmbientParticles = true;
                this.enableAtmosphericSky = true;
                this.enableCustomWeather = true;
                this.enableAmbientAudio = true;
                this.ambientAudioVolume = 0.7f;
                this.enableSeasonalFoliage = true;
                this.enableFallingLeaves = true;
                this.enableMovementAnimations = true;
                this.enableWorldEvents = true;
                this.enableColorGrading = true;
                this.enableCaveVisuals = true;
                this.enableNetherVisuals = true;
                this.enableEndVisuals = true;
                break;
        }
        save();
    }

    private static ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
                if (INSTANCE == null) {
                    INSTANCE = new ModConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            File file = getConfigFile();
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

