package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RelationshipInteractionScreen extends Screen {
    private final PlayerEntity targetPlayer;

    public RelationshipInteractionScreen(PlayerEntity targetPlayer) {
        super(Text.translatable("gui.crazygirlfriend.interaction_title", targetPlayer.getName().getString()));
        this.targetPlayer = targetPlayer;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        int btnWidth = 140;
        int btnHeight = 20;

        Relationship clientRel = ClientRelationshipManager.get().getRelationship(client.player.getUuid());
        boolean isDatingThisTarget = clientRel != null && clientRel.involves(targetPlayer.getUuid()) && clientRel.getType() == RelationshipType.DATING;
        boolean isMarriedThisTarget = clientRel != null && clientRel.involves(targetPlayer.getUuid()) && clientRel.getType() == RelationshipType.MARRIED;
        boolean hasPartner = clientRel != null;

        // Left Column: Romantic & Proposals
        // 1. Give Red Rose
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🌹 Give Red Rose"), b -> sendAction("rose"))
            .dimensions(centerX - btnWidth - 10, startY, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Ask to be girlfriend/boyfriend!")))
            .build());

        // 2. Propose Marriage
        this.addDrawableChild(ButtonWidget.builder(Text.literal("💍 Propose Marriage"), b -> sendAction("marry"))
            .dimensions(centerX - btnWidth - 10, startY + 24, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Propose marriage with Diamond Ring!")))
            .build());

        // 3. Make Love
        this.addDrawableChild(ButtonWidget.builder(Text.literal("❤️ Make Love"), b -> sendAction("makelove"))
            .dimensions(centerX - btnWidth - 10, startY + 48, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Romantic embrace & fade-to-black")))
            .build());

        // 4. Gift Bouquet
        this.addDrawableChild(ButtonWidget.builder(Text.literal("💐 Gift Bouquet"), b -> sendAction("bouquet"))
            .dimensions(centerX - btnWidth - 10, startY + 72, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Gift a colorful flower bouquet!")))
            .build());

        // 5. Apply Makeup
        this.addDrawableChild(ButtonWidget.builder(Text.literal("💄 Apply Makeup"), b -> {
                this.close();
                client.setScreen(new MakeupScreen(targetPlayer));
            })
            .dimensions(centerX - btnWidth - 10, startY + 96, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Open Makeup Studio for this player!")))
            .build());

        // 6. Breakup / Divorce
        if (isMarriedThisTarget) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("💔 Divorce"), b -> sendAction("divorce"))
                .dimensions(centerX - btnWidth - 10, startY + 120, btnWidth, btnHeight)
                .build());
        } else if (isDatingThisTarget) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("💔 Break Up"), b -> sendAction("breakup"))
                .dimensions(centerX - btnWidth - 10, startY + 120, btnWidth, btnHeight)
                .build());
        }

        // Right Column: Fun Actions & Physical Interactions
        // 7. Hug
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🤗 Hug"), b -> sendAction("hug"))
            .dimensions(centerX + 10, startY, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Warm animated hug with hearts")))
            .build());

        // 8. Kiss
        this.addDrawableChild(ButtonWidget.builder(Text.literal("💋 Kiss"), b -> sendAction("kiss"))
            .dimensions(centerX + 10, startY + 24, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Sweet animated kiss")))
            .build());

        // 9. Slap
        this.addDrawableChild(ButtonWidget.builder(Text.literal("👋 Slap"), b -> sendAction("slap"))
            .dimensions(centerX + 10, startY + 48, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Dramatic comedic slap with stars")))
            .build());

        // 10. Straddle
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🧸 Straddle"), b -> sendAction("straddle"))
            .dimensions(centerX + 10, startY + 72, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Playful lap sitting animation")))
            .build());

        // 11. Piggyback Ride
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🐴 Piggyback Ride"), b -> sendAction("piggyback"))
            .dimensions(centerX + 10, startY + 96, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Hop on their back for a ride!")))
            .build());

        // 12. Paddle Spank
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🏏 Paddle Spank"), b -> sendAction("paddle"))
            .dimensions(centerX + 10, startY + 120, btnWidth, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Playful spank with disciplinary paddle")))
            .build());

        // 13. Leash (Toggle / Attach)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🪢 Player Leash"), b -> sendAction("leash"))
            .dimensions(centerX - 70, startY + 148, 140, btnHeight)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Attach leash or toggle sit/stand!")))
            .build());
    }

    private void sendAction(String action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetPlayer.getUuid());
        buf.writeString(action);
        ClientPlayNetworking.send(ModPackets.INTERACTION_REQUEST_C2S, buf);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 105;

        // Dark container background
        context.fill(centerX - 165, startY - 15, centerX + 165, startY + 280, 0xCC111111);
        context.fill(centerX - 163, startY - 13, centerX + 163, startY + 278, 0x99222222);

        // Header Title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("❤️ Crazy Girlfriend Interaction Menu ❤️").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), centerX, startY - 5, 0xFFFFAA);

        // Target Name & Status Badge
        Relationship rel = ClientRelationshipManager.get().getRelationship(targetPlayer.getUuid());
        String relBadge = "Status: Single";
        int badgeColor = 0xAAAAAA;
        if (rel != null) {
            if (rel.getType() == RelationshipType.MARRIED) {
                relBadge = "Status: Married to " + rel.getPartnerName(targetPlayer.getUuid()) + " 💍";
                badgeColor = 0xFFD700;
            } else if (rel.getType() == RelationshipType.DATING) {
                relBadge = "Status: Dating " + rel.getPartnerName(targetPlayer.getUuid()) + " ❤️";
                badgeColor = 0xFF69B4;
            }
        }

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Interacting with: ").append(targetPlayer.getName()).formatted(Formatting.AQUA), centerX, startY + 10, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(relBadge), centerX, startY + 22, badgeColor);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

