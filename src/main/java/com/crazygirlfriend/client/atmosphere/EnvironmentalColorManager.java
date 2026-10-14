package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.event.world.WorldEventManager;
import com.crazygirlfriend.event.world.WorldEventType;
import com.crazygirlfriend.season.Season;
import com.crazygirlfriend.season.SeasonManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EnvironmentalColorManager {
    private static final EnvironmentalColorManager INSTANCE = new EnvironmentalColorManager();
    public static EnvironmentalColorManager get() { return INSTANCE; }

    public void applyColorGrading(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
        if (!ModConfig.get().enableColorGrading || world == null) return;

        float[] curFog = RenderSystem.getShaderFogColor();
        if (curFog == null || curFog.length < 3) return;

        float r = curFog[0];
        float g = curFog[1];
        float b = curFog[2];

        long time = world.getTimeOfDay() % 24000L;
        Season season = SeasonManager.get().getClientSeason();
        WorldEventType activeEvent = WorldEventManager.get().getClientEvent();

        float intensityMultiplier = ModConfig.get().photosensitivityMode ? 0.35f : 1.0f;

        // 1. Rare Pink Sunset Event
        if (activeEvent == WorldEventType.PINK_SUNSET) {
            r = MathHelper.clamp(r + 0.18f * intensityMultiplier, 0.0f, 1.0f);
            g = MathHelper.clamp(g + 0.05f * intensityMultiplier, 0.0f, 1.0f);
            b = MathHelper.clamp(b + 0.22f * intensityMultiplier, 0.0f, 1.0f);
        }
        // 2. Sunset / Golden Hour (Normal)
        else if (time >= 11500L && time <= 13000L) {
            float sunsetFactor = 1.0f - Math.abs(time - 12250L) / 750.0f;
            r = MathHelper.clamp(r + 0.14f * sunsetFactor * intensityMultiplier, 0.0f, 1.0f);
            g = MathHelper.clamp(g + 0.06f * sunsetFactor * intensityMultiplier, 0.0f, 1.0f);
            b = MathHelper.clamp(b + 0.08f * sunsetFactor * intensityMultiplier, 0.0f, 1.0f);
        }
        // 3. Morning / Dawn Peach Radiance
        else if (time >= 22500L || time <= 1500L) {
            float dawnFactor = time >= 22500L ? (time - 22500L) / 1500.0f : (1500L - time) / 1500.0f;
            r = MathHelper.clamp(r + 0.10f * dawnFactor * intensityMultiplier, 0.0f, 1.0f);
            g = MathHelper.clamp(g + 0.05f * dawnFactor * intensityMultiplier, 0.0f, 1.0f);
            b = MathHelper.clamp(b + 0.02f * dawnFactor * intensityMultiplier, 0.0f, 1.0f);
        }

        // 4. Seasonal Atmospheric Tints
        if (season == Season.WINTER) {
            // Cool silvery-blue winter air
            b = MathHelper.clamp(b + 0.06f * intensityMultiplier, 0.0f, 1.0f);
            r = MathHelper.clamp(r - 0.03f * intensityMultiplier, 0.0f, 1.0f);
        } else if (season == Season.AUTUMN) {
            // Warm golden autumn glow
            r = MathHelper.clamp(r + 0.05f * intensityMultiplier, 0.0f, 1.0f);
            g = MathHelper.clamp(g + 0.02f * intensityMultiplier, 0.0f, 1.0f);
        }

        RenderSystem.setShaderFogColor(r, g, b, 1.0f);
        RenderSystem.clearColor(r, g, b, 0.0f);
    }

    public void applyFogDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
        if (!ModConfig.get().enableColorGrading || fogType != BackgroundRenderer.FogType.FOG_TERRAIN) return;

        WorldEventType activeEvent = WorldEventManager.get().getClientEvent();
        Season season = SeasonManager.get().getClientSeason();

        // If Magical Mist or Winter Blizzard, bring fog closer for cinematic rolling mist
        if (activeEvent == WorldEventType.MAGICAL_MIST) {
            float start = viewDistance * 0.1f;
            float end = viewDistance * 0.65f;
            RenderSystem.setShaderFogStart(start);
            RenderSystem.setShaderFogEnd(end);
        } else if (season == Season.WINTER && thickFog) {
            float start = viewDistance * 0.15f;
            float end = viewDistance * 0.75f;
            RenderSystem.setShaderFogStart(start);
            RenderSystem.setShaderFogEnd(end);
        }
    }
}

