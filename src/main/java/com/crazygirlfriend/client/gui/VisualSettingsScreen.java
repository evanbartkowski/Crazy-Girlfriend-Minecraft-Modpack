package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VisualSettingsScreen extends Screen {
    private final Screen parent;

    public VisualSettingsScreen(Screen parent) {
        super(Text.literal("Visual & Atmospheric Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int col1X = centerX - 190;
        int col2X = centerX + 10;
        int startY = 46;
        int buttonWidth = 180;
        int buttonHeight = 20;

        ModConfig cfg = ModConfig.get();

        // ══════════════════════════════════════════════
        // LEFT COLUMN: Seasons, Presets & Movement
        // ══════════════════════════════════════════════

        // 1. Graphics Preset
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Preset: ").append(Text.literal(cfg.visualPreset).formatted(Formatting.GOLD, Formatting.BOLD)),
            btn -> {
                String next = switch (cfg.visualPreset) {
                    case "LOW" -> "MEDIUM";
                    case "MEDIUM" -> "HIGH";
                    case "HIGH" -> "ULTRA";
                    default -> "LOW";
                };
                cfg.applyPreset(next);
                this.clearAndInit();
            }
        ).dimensions(col1X, startY, buttonWidth, buttonHeight).build());

        // 2. Photosensitivity Mode
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Photosafety: ").append(cfg.photosensitivityMode ? Text.literal("ON (Safe)").formatted(Formatting.GREEN) : Text.literal("OFF").formatted(Formatting.GRAY)),
            btn -> {
                cfg.photosensitivityMode = !cfg.photosensitivityMode;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col1X, startY + 26, buttonWidth, buttonHeight).build());

        // 3. Dynamic Four Seasons
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Dynamic Seasons: ").append(cfg.enableSeasons ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableSeasons = !cfg.enableSeasons;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col1X, startY + 52, buttonWidth, buttonHeight).build());

        // 4. Seasonal Foliage & Leaves
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Seasonal Foliage: ").append(cfg.enableSeasonalFoliage ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableSeasonalFoliage = !cfg.enableSeasonalFoliage;
                cfg.enableFallingLeaves = cfg.enableSeasonalFoliage;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col1X, startY + 78, buttonWidth, buttonHeight).build());

        // 5. Advanced Movement Animations
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Movement Animations: ").append(cfg.enableMovementAnimations ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableMovementAnimations = !cfg.enableMovementAnimations;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col1X, startY + 104, buttonWidth, buttonHeight).build());

        // ══════════════════════════════════════════════
        // RIGHT COLUMN: Particles, Skies, Fog & Audio
        // ══════════════════════════════════════════════

        // 6. Particle Density
        int densityPct = (int) (cfg.particleDensity * 100);
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Particles: ").append(Text.literal(densityPct + "%").formatted(Formatting.AQUA)),
            btn -> {
                if (cfg.particleDensity < 0.8f) cfg.particleDensity = 0.8f;
                else if (cfg.particleDensity < 1.2f) cfg.particleDensity = 1.2f;
                else if (cfg.particleDensity < 1.8f) cfg.particleDensity = 1.8f;
                else if (cfg.particleDensity < 2.0f) cfg.particleDensity = 2.0f;
                else cfg.particleDensity = 0.4f;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col2X, startY, buttonWidth, buttonHeight).build());

        // 7. Rare Magical World Events
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("World Events: ").append(cfg.enableWorldEvents ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableWorldEvents = !cfg.enableWorldEvents;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col2X, startY + 26, buttonWidth, buttonHeight).build());

        // 8. Color Grading & Fog Depth
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Color Grading & Fog: ").append(cfg.enableColorGrading ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableColorGrading = !cfg.enableColorGrading;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col2X, startY + 52, buttonWidth, buttonHeight).build());

        // 9. Cave & Dimension Visuals
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Caves & Dimensions: ").append(cfg.enableCaveVisuals ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableCaveVisuals = !cfg.enableCaveVisuals;
                cfg.enableNetherVisuals = cfg.enableCaveVisuals;
                cfg.enableEndVisuals = cfg.enableCaveVisuals;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col2X, startY + 78, buttonWidth, buttonHeight).build());

        // 10. Ambient Nature Sounds Volume
        int volPct = (int) (cfg.ambientAudioVolume * 100);
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Ambient Audio: ").append(Text.literal(volPct + "%").formatted(Formatting.LIGHT_PURPLE)),
            btn -> {
                cfg.ambientAudioVolume += 0.2f;
                if (cfg.ambientAudioVolume > 1.05f) cfg.ambientAudioVolume = 0.2f;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(col2X, startY + 104, buttonWidth, buttonHeight).build());

        // ══════════════════════════════════════════════
        // BOTTOM BUTTONS
        // ══════════════════════════════════════════════

        // Done button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            btn -> {
                ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }
        ).dimensions(centerX - 80, startY + 140, 160, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("🌸 Crazy Girlfriend — Visual & Atmospheric Settings").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), this.width / 2, 16, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Dynamic seasons, fluid movement, rare events, color grading, and dimensions").formatted(Formatting.GRAY), this.width / 2, 28, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
