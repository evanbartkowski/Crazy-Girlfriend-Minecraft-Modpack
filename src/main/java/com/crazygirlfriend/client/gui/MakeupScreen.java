package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MakeupScreen extends Screen {
    private final PlayerEntity targetPlayer;
    private final PlayerMakeupData makeupData;

    private static final String[] LIPSTICK_NAMES = {"None", "Ruby Red 💄", "Soft Pink 🌸", "Midnight Black 🖤", "Berry Purple 🍇"};
    private static final String[] BLUSH_NAMES = {"None", "Rosy Cheek 🌸", "Anime Peach 🍑", "Doll Red 🍎"};
    private static final String[] EYELINER_NAMES = {"None", "Winged Cat Eye 👁️", "Smoky Eye ✨"};
    private static final String[] MARKING_NAMES = {"None", "Cat Whiskers 🐱", "Cheek Hearts 💕", "Clown Nose 🤡", "Cute Star ⭐", "French Mustache 🥸"};

    private ButtonWidget lipstickBtn;
    private ButtonWidget blushBtn;
    private ButtonWidget eyelinerBtn;
    private ButtonWidget markingsBtn;

    public MakeupScreen(PlayerEntity targetPlayer) {
        super(Text.translatable("gui.crazygirlfriend.makeup.title", targetPlayer.getName().getString()));
        this.targetPlayer = targetPlayer;
        // Copy current makeup
        PlayerMakeupData current = ClientRelationshipManager.get().getMakeup(targetPlayer.getUuid());
        this.makeupData = new PlayerMakeupData(targetPlayer.getUuid());
        this.makeupData.setLipstickStyle(current.getLipstickStyle());
        this.makeupData.setBlushStyle(current.getBlushStyle());
        this.makeupData.setEyelinerStyle(current.getEyelinerStyle());
        this.makeupData.setFaceMarking(current.getFaceMarking());
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        int btnWidth = 180;
        int btnHeight = 20;

        // Lipstick
        lipstickBtn = ButtonWidget.builder(getLipstickText(), b -> {
            makeupData.setLipstickStyle((makeupData.getLipstickStyle() + 1) % LIPSTICK_NAMES.length);
            b.setMessage(getLipstickText());
        }).dimensions(centerX - btnWidth / 2, startY, btnWidth, btnHeight).build();
        this.addDrawableChild(lipstickBtn);

        // Blush
        blushBtn = ButtonWidget.builder(getBlushText(), b -> {
            makeupData.setBlushStyle((makeupData.getBlushStyle() + 1) % BLUSH_NAMES.length);
            b.setMessage(getBlushText());
        }).dimensions(centerX - btnWidth / 2, startY + 24, btnWidth, btnHeight).build();
        this.addDrawableChild(blushBtn);

        // Eyeliner
        eyelinerBtn = ButtonWidget.builder(getEyelinerText(), b -> {
            makeupData.setEyelinerStyle((makeupData.getEyelinerStyle() + 1) % EYELINER_NAMES.length);
            b.setMessage(getEyelinerText());
        }).dimensions(centerX - btnWidth / 2, startY + 48, btnWidth, btnHeight).build();
        this.addDrawableChild(eyelinerBtn);

        // Face Markings
        markingsBtn = ButtonWidget.builder(getMarkingText(), b -> {
            makeupData.setFaceMarking((makeupData.getFaceMarking() + 1) % MARKING_NAMES.length);
            b.setMessage(getMarkingText());
        }).dimensions(centerX - btnWidth / 2, startY + 72, btnWidth, btnHeight).build();
        this.addDrawableChild(markingsBtn);

        // Apply Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✨ Apply Makeup ✨").formatted(Formatting.GREEN, Formatting.BOLD), b -> {
            applyAndSend();
        }).dimensions(centerX - btnWidth / 2, startY + 104, btnWidth, btnHeight).build());

        // Clear Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🧽 Clear All").formatted(Formatting.RED), b -> {
            makeupData.clear();
            applyAndSend();
        }).dimensions(centerX - btnWidth / 2, startY + 128, btnWidth, btnHeight).build());
    }

    private Text getLipstickText() {
        return Text.literal("Lipstick: " + LIPSTICK_NAMES[makeupData.getLipstickStyle()]);
    }

    private Text getBlushText() {
        return Text.literal("Blush: " + BLUSH_NAMES[makeupData.getBlushStyle()]);
    }

    private Text getEyelinerText() {
        return Text.literal("Eyeliner: " + EYELINER_NAMES[makeupData.getEyelinerStyle()]);
    }

    private Text getMarkingText() {
        return Text.literal("Marking: " + MARKING_NAMES[makeupData.getFaceMarking()]);
    }

    private void applyAndSend() {
        PacketByteBuf buf = PacketByteBufs.create();
        makeupData.writeToBuf(buf);
        ClientPlayNetworking.send(ModPackets.APPLY_MAKEUP_C2S, buf);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 90;

        // Container
        context.fill(centerX - 110, startY - 10, centerX + 110, startY + 250, 0xDD111111);
        context.fill(centerX - 108, startY - 8, centerX + 108, startY + 248, 0x88222222);

        // Header Title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("💄 Makeup Studio 💄").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), centerX, startY + 2, 0xFFFFAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Styling: " + targetPlayer.getName().getString()).formatted(Formatting.AQUA), centerX, startY + 16, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

