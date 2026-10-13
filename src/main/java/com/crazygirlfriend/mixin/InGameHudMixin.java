package com.crazygirlfriend.mixin;

import com.crazygirlfriend.client.ClientRelationshipManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderRomanticFadeVignette(DrawContext context, float tickDelta, CallbackInfo ci) {
        int rem = ClientRelationshipManager.get().fadeVignetteRemaining;
        int tot = ClientRelationshipManager.get().fadeVignetteTotal;
        if (rem > 0 && tot > 0) {
            float progress = (float) rem / tot;
            float alpha = MathHelper.sin(progress * (float) Math.PI);
            int alphaInt = (int) (alpha * 245.0f);
            if (alphaInt > 0) {
                int color = (alphaInt << 24) | 0x0D0611;
                context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), color);

                if (alphaInt > 80) {
                    context.drawCenteredTextWithShadow(
                        this.client.textRenderer,
                        Text.literal("❤️ Making Love ❤️").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                        context.getScaledWindowWidth() / 2,
                        context.getScaledWindowHeight() / 2,
                        (alphaInt << 24) | 0xFFB6C1
                    );
                }
            }
        }

        // Render Pink Euphoria Screen (Aphrodisiac love potion effect)
        int euRem = ClientRelationshipManager.get().loveEuphoriaRemaining;
        if (euRem > 0) {
            float pulse = 0.65f + 0.35f * MathHelper.sin(ClientRelationshipManager.get().clientTicks * 0.12f);
            int alpha = (int) (65.0f * pulse);
            int pinkOverlay = (alpha << 24) | 0xFF3399; // Dreamy romantic pink wash
            context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), pinkOverlay);

            if (euRem % 40 < 25) {
                context.drawCenteredTextWithShadow(
                    this.client.textRenderer,
                    Text.literal("💖 LOVE EUPHORIA (Get close to someone to pounce!) 💖").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                    context.getScaledWindowWidth() / 2,
                    15,
                    0xFFFF69B4
                );
            }
        }

        // Render Combo HUD prompt if an interactive animation combo is available
        com.crazygirlfriend.client.gui.ComboOverlayHud.get().render(context, tickDelta);
    }
}

