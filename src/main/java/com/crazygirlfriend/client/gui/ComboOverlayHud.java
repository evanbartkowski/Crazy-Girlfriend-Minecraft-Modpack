package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.client.animation.ClientAnimationManager;
import com.crazygirlfriend.combo.AnimationComboRegistry;
import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ComboOverlayHud {
    private static final ComboOverlayHud INSTANCE = new ComboOverlayHud();
    private int lastHandledKey = -1;

    public static ComboOverlayHud get() {
        return INSTANCE;
    }

    public void tick(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) {
            lastHandledKey = -1;
            return;
        }

        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(client.player.getUuid());
        if (anim == null || anim.partnerUuid == null) {
            lastHandledKey = -1;
            return;
        }

        long handle = client.getWindow().getHandle();
        int pressed = -1;
        for (int key = GLFW.GLFW_KEY_1; key <= GLFW.GLFW_KEY_4; key++) {
            if (InputUtil.isKeyPressed(handle, key)) {
                pressed = key;
                break;
            }
        }

        if (pressed != -1 && pressed != lastHandledKey) {
            handleKeyPress(pressed);
            lastHandledKey = pressed;
        } else if (pressed == -1) {
            lastHandledKey = -1;
        }
    }

    public void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(client.player.getUuid());
        if (anim == null || anim.partnerUuid == null) return;

        List<AnimationComboRegistry.ComboBranch> branches = AnimationComboRegistry.getBranches(anim.type);
        if (branches.isEmpty()) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Calculate box dimensions
        int boxWidth = Math.max(220, branches.size() * 85);
        int boxHeight = 36;
        int boxX = (width - boxWidth) / 2;
        int boxY = height - 90;

        // Draw translucent dark-pink backdrop
        context.fill(boxX - 2, boxY - 2, boxX + boxWidth + 2, boxY + boxHeight + 2, 0x882A082D);
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xD0120516);

        // Header Title
        context.drawCenteredTextWithShadow(
            client.textRenderer,
            Text.literal("✨ Animation Combo Available ✨").formatted(Formatting.GOLD, Formatting.BOLD),
            width / 2,
            boxY + 4,
            0xFFFFD700
        );

        // Render Combo Option Buttons
        int buttonSpacing = boxWidth / branches.size();
        for (int i = 0; i < branches.size(); i++) {
            AnimationComboRegistry.ComboBranch branch = branches.get(i);
            int itemX = boxX + (i * buttonSpacing) + (buttonSpacing / 2);
            String label = "[" + branch.hotkey + "] " + branch.displayName;
            int color = switch (branch.category) {
                case ROMANTIC -> 0xFFFF8DA1;
                case COMEDY -> 0xFFFFD152;
                case DANCE -> 0xFF70E4FF;
                case ARGUMENT -> 0xFFFF5A5A;
            };

            context.drawCenteredTextWithShadow(
                client.textRenderer,
                Text.literal(label),
                itemX,
                boxY + 18,
                color
            );
        }
    }

    public void handleKeyPress(int keyCode) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null) return;

        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(client.player.getUuid());
        if (anim == null || anim.partnerUuid == null) return;

        List<AnimationComboRegistry.ComboBranch> branches = AnimationComboRegistry.getBranches(anim.type);
        if (branches.isEmpty()) return;

        for (AnimationComboRegistry.ComboBranch branch : branches) {
            int branchKey = switch (branch.hotkey) {
                case "1" -> GLFW.GLFW_KEY_1;
                case "2" -> GLFW.GLFW_KEY_2;
                case "3" -> GLFW.GLFW_KEY_3;
                case "4" -> GLFW.GLFW_KEY_4;
                default -> -1;
            };

            if (keyCode == branchKey) {
                triggerCombo(anim.partnerUuid, branch);
                break;
            }
        }
    }

    private void triggerCombo(java.util.UUID partnerUuid, AnimationComboRegistry.ComboBranch branch) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(partnerUuid);
        buf.writeString(branch.to.name());
        ClientPlayNetworking.send(ModPackets.COMBO_TRIGGER_C2S, buf);
    }
}
