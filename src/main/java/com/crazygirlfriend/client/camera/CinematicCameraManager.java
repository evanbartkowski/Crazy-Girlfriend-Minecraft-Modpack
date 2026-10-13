package com.crazygirlfriend.client.camera;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.client.animation.ClientAnimationManager;
import com.crazygirlfriend.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class CinematicCameraManager {
    private static final CinematicCameraManager INSTANCE = new CinematicCameraManager();

    private int shakeTicks = 0;
    private float shakeIntensity = 0.0f;
    private float currentRoll = 0.0f;
    private float targetRoll = 0.0f;

    public static CinematicCameraManager get() {
        return INSTANCE;
    }

    public void triggerScreenShake(int ticks, float intensity) {
        if (!ModConfig.get().enableCinematicCamera) return;
        this.shakeTicks = ticks;
        this.shakeIntensity = intensity;
    }

    public void tick() {
        if (shakeTicks > 0) {
            shakeTicks--;
            if (shakeTicks == 0) {
                shakeIntensity = 0.0f;
            }
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || !ModConfig.get().enableCinematicCamera) {
            targetRoll = 0.0f;
            currentRoll = 0.0f;
            return;
        }

        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(client.player.getUuid());
        if (anim != null) {
            if (anim.type == AnimationType.ROMANTIC_DIP_KISS) {
                targetRoll = 4.5f * MathHelper.sin(anim.getProgress(0.0f) * (float) Math.PI);
            } else if (anim.type == AnimationType.ROMANTIC_SWAY_HUG || anim.type == AnimationType.SLOW_DANCE) {
                targetRoll = 2.0f * MathHelper.sin(anim.getCycle(0.45f, 0.0f));
            } else {
                targetRoll = 0.0f;
            }
        } else {
            targetRoll = 0.0f;
        }

        currentRoll = MathHelper.lerp(0.15f, currentRoll, targetRoll);
    }

    public float getFovModifier(float tickDelta) {
        if (!ModConfig.get().enableCinematicCamera) return 0.0f;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return 0.0f;

        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(client.player.getUuid());
        if (anim != null) {
            AnimationType t = anim.type;
            if (t == AnimationType.KISS || t == AnimationType.MAKE_OUT || t == AnimationType.ROMANTIC_DIP_KISS || t == AnimationType.UNDERWATER_KISS) {
                float p = anim.getProgress(tickDelta);
                return -6.0f * MathHelper.sin(p * (float) Math.PI); // Subtle 6 degree zoom in
            }
        }
        return 0.0f;
    }

    public float getCurrentRoll(float tickDelta) {
        return currentRoll;
    }

    public float getShakeOffset(float tickDelta) {
        if (shakeTicks <= 0 || shakeIntensity <= 0.0f) return 0.0f;
        float progress = (shakeTicks - tickDelta) / 10.0f;
        return (float) (Math.sin(progress * 25.0) * shakeIntensity * 0.08);
    }
}
