package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.sound.ModSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class PhotoModeScreen extends Screen {

    public enum FilterType {
        STANDARD("Standard", 0x000000, 0.0f),
        WARM_SUNSET("Warm Sunset", 0xE68A20, 0.25f),
        DREAMY_PASTEL("Dreamy Pastel", 0xE066B8, 0.22f),
        ROMANTIC_ROSE("Romantic Rose", 0xD8366A, 0.25f),
        VINTAGE_SEPIA("Vintage Sepia", 0x8C6838, 0.35f),
        NOIR_MONOCHROME("Noir Monochrome", 0x222222, 0.40f),
        VIBRANT_FANTASY("Vibrant Fantasy", 0x33DDAA, 0.20f);

        private final String displayName;
        private final int tintColor;
        private final float alpha;

        FilterType(String displayName, int tintColor, float alpha) {
            this.displayName = displayName;
            this.tintColor = tintColor;
            this.alpha = alpha;
        }

        public String getDisplayName() { return displayName; }
        public int getTintColor() { return tintColor; }
        public float getAlpha() { return alpha; }
        public FilterType getNext() { return values()[(ordinal() + 1) % values().length]; }
    }

    public enum AspectRatioGuide {
        NONE("Full View"),
        CINEMA_16_9("16:9 Widescreen"),
        CINEMA_239("2.39:1 Cinemascope"),
        POLAROID("Polaroid Square");

        private final String label;
        AspectRatioGuide(String label) { this.label = label; }
        public String getLabel() { return label; }
        public AspectRatioGuide getNext() { return values()[(ordinal() + 1) % values().length]; }
    }

    public enum PhotoPose {
        DEFAULT("Default Pose"),
        WAVE("Friendly Wave"),
        PEACE("Peace Sign"),
        CUTE_WINK("Cute Wink"),
        ROMANTIC_LEAN("Romantic Lean"),
        COUPLE_DIP("Dramatic Dip");

        private final String label;
        PhotoPose(String label) { this.label = label; }
        public String getLabel() { return label; }
        public PhotoPose getNext() { return values()[(ordinal() + 1) % values().length]; }
    }

    private FilterType currentFilter = FilterType.STANDARD;
    private AspectRatioGuide currentGuide = AspectRatioGuide.NONE;
    private PhotoPose currentPose = PhotoPose.DEFAULT;
    private boolean hideControls = false;
    private float cameraFov = 70.0f;
    private float flashAlpha = 0.0f;

    public PhotoModeScreen() {
        super(Text.literal("📷 Cinematic Photography Mode"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 140;
        int buttonHeight = 20;
        int startX = 20;
        int startY = this.height - 110;

        // Filter Toggle Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("🎨 Filter: " + currentFilter.getDisplayName()),
                btn -> {
                    currentFilter = currentFilter.getNext();
                    btn.setMessage(Text.literal("🎨 Filter: " + currentFilter.getDisplayName()));
                }
        ).dimensions(startX, startY, buttonWidth, buttonHeight).build());

        // Framing Guide Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("📐 Guide: " + currentGuide.getLabel()),
                btn -> {
                    currentGuide = currentGuide.getNext();
                    btn.setMessage(Text.literal("📐 Guide: " + currentGuide.getLabel()));
                }
        ).dimensions(startX, startY + 24, buttonWidth, buttonHeight).build());

        // Pose Selection Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("🧍 Pose: " + currentPose.getLabel()),
                btn -> {
                    currentPose = currentPose.getNext();
                    btn.setMessage(Text.literal("🧍 Pose: " + currentPose.getLabel()));
                }
        ).dimensions(startX, startY + 48, buttonWidth, buttonHeight).build());

        // Snap Photo Button
        int snapWidth = 160;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("📸 SNAP PHOTO").formatted(Formatting.GOLD, Formatting.BOLD),
                btn -> takePhoto()
        ).dimensions(this.width / 2 - snapWidth / 2, this.height - 40, snapWidth, 26).build());

        // Hide Controls Toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("👁 Hide HUD (H)"),
                btn -> hideControls = !hideControls
        ).dimensions(this.width - 130, 15, 110, 20).build());

        // Exit Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("✕ Exit"),
                btn -> this.close()
        ).dimensions(this.width - 80, this.height - 35, 65, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 72) { // 'H' key
            hideControls = !hideControls;
            return true;
        }
        if (keyCode == 32 || keyCode == 257) { // Space or Enter
            takePhoto();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void takePhoto() {
        if (this.client == null || this.client.player == null) return;

        // Camera shutter sound & flash
        this.client.world.playSound(
                this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(),
                ModSounds.CAMERA_CLICK, SoundCategory.PLAYERS, 1.0f, 1.0f, false
        );

        flashAlpha = 1.0f;

        // Save screenshot
        ScreenshotRecorder.saveScreenshot(
                this.client.runDirectory,
                this.client.getFramebuffer(),
                text -> this.client.player.sendMessage(text, true)
        );

        // Send photo metadata to server to grant Polaroid item
        String partnerName = "";
        Relationship rel = ClientRelationshipManager.get().getRelationship(this.client.player.getUuid());
        if (rel != null) {
            partnerName = rel.getPartnerName(this.client.player.getUuid());
        }

        String biomeName = this.client.world.getBiome(this.client.player.getBlockPos()).getKey()
                .map(k -> k.getValue().getPath().replace('_', ' ')).orElse("Wilderness");

        String title = partnerName.isEmpty() ? "Scenic Photo in " + biomeName : "Date with " + partnerName + " in " + biomeName;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(title);
        buf.writeString(partnerName);
        buf.writeString(biomeName);
        buf.writeString(currentFilter.getDisplayName());
        buf.writeString(currentPose.getLabel());

        ClientPlayNetworking.send(ModPackets.PHOTO_SAVE_C2S, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Render Active Color Filter Overlay
        if (currentFilter != FilterType.STANDARD && currentFilter.getAlpha() > 0.01f) {
            int tr = (currentFilter.getTintColor() >> 16) & 0xFF;
            int tg = (currentFilter.getTintColor() >> 8) & 0xFF;
            int tb = currentFilter.getTintColor() & 0xFF;
            int alpha = (int) (currentFilter.getAlpha() * 255.0f);
            int color = (alpha << 24) | (tr << 16) | (tg << 8) | tb;
            context.fill(0, 0, this.width, this.height, color);
        }

        // 2. Render Framing Guides (Cinema black bars or Polaroid frame)
        if (currentGuide == AspectRatioGuide.CINEMA_16_9) {
            int barHeight = (int) (this.height * 0.12f);
            context.fill(0, 0, this.width, barHeight, 0xFF000000);
            context.fill(0, this.height - barHeight, this.width, this.height, 0xFF000000);
        } else if (currentGuide == AspectRatioGuide.CINEMA_239) {
            int barHeight = (int) (this.height * 0.18f);
            context.fill(0, 0, this.width, barHeight, 0xFF000000);
            context.fill(0, this.height - barHeight, this.width, this.height, 0xFF000000);
        } else if (currentGuide == AspectRatioGuide.POLAROID) {
            int border = 30;
            int bottomBorder = 70;
            context.fill(0, 0, this.width, border, 0xEEFFFFFF);
            context.fill(0, 0, border, this.height, 0xEEFFFFFF);
            context.fill(this.width - border, 0, this.width, this.height, 0xEEFFFFFF);
            context.fill(0, this.height - bottomBorder, this.width, this.height, 0xEEFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, "♥ Crazy Girlfriend Memories ♥", this.width / 2, this.height - 35, 0xFF444444);
        }

        // 3. Render Flash Animation
        if (flashAlpha > 0.05f) {
            int alpha = (int) (flashAlpha * 255.0f);
            context.fill(0, 0, this.width, this.height, (alpha << 24) | 0x00FFFFFF);
            flashAlpha = MathHelper.clamp(flashAlpha - delta * 0.15f, 0.0f, 1.0f);
        }

        // 4. Render UI Elements if not hidden
        if (!hideControls) {
            super.render(context, mouseX, mouseY, delta);

            // Title Header & Hotkey guide
            context.drawTextWithShadow(this.textRenderer, "📷 Photography Mode", 20, 18, 0xFFFFAA);
            context.drawTextWithShadow(this.textRenderer, "§7[Space] Take Snapshot  §7[H] Toggle HUD", 20, 32, 0xAAAAAA);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
