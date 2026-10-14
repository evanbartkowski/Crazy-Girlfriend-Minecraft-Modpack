package com.crazygirlfriend.mixin;

import com.crazygirlfriend.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    private static final int PETAL_COUNT = 30;
    private static final float[] PETAL_X = new float[PETAL_COUNT];
    private static final float[] PETAL_Y = new float[PETAL_COUNT];
    private static final float[] PETAL_SPEED = new float[PETAL_COUNT];
    private static boolean initialized = false;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderRomanticTitleOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModConfig.get().enableCustomUI) return;

        TitleScreen screen = (TitleScreen) (Object) this;
        int width = screen.width;
        int height = screen.height;

        if (!initialized) {
            for (int i = 0; i < PETAL_COUNT; i++) {
                PETAL_X[i] = (float) (Math.random() * width);
                PETAL_Y[i] = (float) (Math.random() * height);
                PETAL_SPEED[i] = 0.4f + (float) (Math.random() * 0.7f);
            }
            initialized = true;
        }

        // Animate floating sakura blossom petals across main menu
        for (int i = 0; i < PETAL_COUNT; i++) {
            PETAL_Y[i] += PETAL_SPEED[i];
            PETAL_X[i] += (float) Math.sin((PETAL_Y[i] + i * 20) * 0.02) * 0.35f;

            if (PETAL_Y[i] > height) {
                PETAL_Y[i] = -6;
                PETAL_X[i] = (float) (Math.random() * width);
            }

            int px = (int) PETAL_X[i];
            int py = (int) PETAL_Y[i];
            context.fill(px, py, px + 2, py + 2, 0xCCFFB6C1);
            context.fill(px + 1, py + 1, px + 3, py + 3, 0x99FF69B4);
        }

        // Romantic fantasy edition banner
        long time = System.currentTimeMillis();
        float pulse = 0.75f + 0.25f * MathHelper.sin(time * 0.003f);
        int alpha = (int) (255 * pulse);
        int titleColor = (alpha << 24) | 0xFFB6C1;

        context.drawCenteredTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            Text.literal("✨ CRAZY GIRLFRIEND MODPACK — ROMANTIC FANTASY EDITION ✨").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
            width / 2,
            height / 4 - 28,
            titleColor
        );
    }
}

