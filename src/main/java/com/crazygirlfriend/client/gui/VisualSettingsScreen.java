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
        int startY = 45;
        int buttonWidth = 200;
        int buttonHeight = 20;

        ModConfig cfg = ModConfig.get();

        // 1. Graphics Preset Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Graphics Preset: ").append(Text.literal(cfg.visualPreset).formatted(Formatting.GOLD, Formatting.BOLD)),
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
        ).dimensions(centerX - 100, startY, buttonWidth, buttonHeight).build());

        // 2. Photosensitivity Mode (Safety)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Photosensitivity Mode: ").append(cfg.photosensitivityMode ? Text.literal("ON (Safe)").formatted(Formatting.GREEN) : Text.literal("OFF").formatted(Formatting.GRAY)),
            btn -> {
                cfg.photosensitivityMode = !cfg.photosensitivityMode;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 26, buttonWidth, buttonHeight).build());

        // 3. Particle Density
        int densityPct = (int) (cfg.particleDensity * 100);
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Particle Density: ").append(Text.literal(densityPct + "%").formatted(Formatting.AQUA)),
            btn -> {
                if (cfg.particleDensity < 0.8f) cfg.particleDensity = 0.8f;
                else if (cfg.particleDensity < 1.2f) cfg.particleDensity = 1.2f;
                else if (cfg.particleDensity < 1.8f) cfg.particleDensity = 1.8f;
                else if (cfg.particleDensity < 2.0f) cfg.particleDensity = 2.0f;
                else cfg.particleDensity = 0.4f;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 52, buttonWidth, buttonHeight).build());

        // 4. Wind & Foliage
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Wind & Foliage Sway: ").append(cfg.enableWindSway ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableWindSway = !cfg.enableWindSway;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 78, buttonWidth, buttonHeight).build());

        // 5. Fireflies & Ambient Particles
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Ambient Particles & Wildlife: ").append(cfg.enableAmbientParticles ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableAmbientParticles = !cfg.enableAmbientParticles;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 104, buttonWidth, buttonHeight).build());

        // 6. Sky & Stars
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Shooting Stars & Auroras: ").append(cfg.enableAtmosphericSky ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableAtmosphericSky = !cfg.enableAtmosphericSky;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 130, buttonWidth, buttonHeight).build());

        // 7. Ambient Audio
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Ambient Nature Sounds: ").append(cfg.enableAmbientAudio ? Text.literal("ENABLED").formatted(Formatting.GREEN) : Text.literal("DISABLED").formatted(Formatting.RED)),
            btn -> {
                cfg.enableAmbientAudio = !cfg.enableAmbientAudio;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 156, buttonWidth, buttonHeight).build());

        // 8. Nature Audio Volume
        int volPct = (int) (cfg.ambientAudioVolume * 100);
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Ambient Volume: ").append(Text.literal(volPct + "%").formatted(Formatting.LIGHT_PURPLE)),
            btn -> {
                cfg.ambientAudioVolume += 0.2f;
                if (cfg.ambientAudioVolume > 1.05f) cfg.ambientAudioVolume = 0.2f;
                ModConfig.save();
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, startY + 182, buttonWidth, buttonHeight).build());

        // Done button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            btn -> {
                ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }
        ).dimensions(centerX - 100, startY + 215, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("🌸 Crazy Girlfriend — Visual & Atmospheric Settings").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), this.width / 2, 18, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Customize enhanced-vanilla graphics, lighting, and ambient effects").formatted(Formatting.GRAY), this.width / 2, 30, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
