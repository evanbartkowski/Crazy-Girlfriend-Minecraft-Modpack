package com.crazygirlfriend.client.animation;

import com.crazygirlfriend.animation.AnimationType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientAnimationManager {
    private static final ClientAnimationManager INSTANCE = new ClientAnimationManager();

    public static class ClientAnimState {
        public final AnimationType type;
        public final UUID partnerUuid;
        public int currentTick;
        public final int totalDuration;

        public ClientAnimState(AnimationType type, UUID partnerUuid, int totalDuration) {
            this.type = type;
            this.partnerUuid = partnerUuid;
            this.currentTick = 0;
            this.totalDuration = totalDuration;
        }

        public float getProgress(float tickDelta) {
            if (totalDuration <= 0) return 0.5f;
            return MathHelper.clamp((currentTick + tickDelta) / (float) totalDuration, 0.0f, 1.0f);
        }
    }

    private final Map<UUID, ClientAnimState> animations = new HashMap<>();

    public static ClientAnimationManager get() {
        return INSTANCE;
    }

    public void setAnimation(UUID playerUuid, UUID partnerUuid, AnimationType type, int duration) {
        if (type == AnimationType.NONE) {
            animations.remove(playerUuid);
        } else {
            animations.put(playerUuid, new ClientAnimState(type, partnerUuid, duration));
        }
    }

    public ClientAnimState getAnimation(UUID playerUuid) {
        return animations.get(playerUuid);
    }

    public boolean hasAnimation(UUID playerUuid) {
        return animations.containsKey(playerUuid);
    }

    public void tick() {
        if (animations.isEmpty()) return;

        animations.entrySet().removeIf(entry -> {
            ClientAnimState state = entry.getValue();
            if (state.totalDuration > 0) {
                state.currentTick++;
                return state.currentTick >= state.totalDuration;
            }
            return false;
        });
    }

    public void applyAnimationToModel(UUID playerUuid, ModelPart head, ModelPart body, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg, float tickDelta) {
        ClientAnimState state = animations.get(playerUuid);
        if (state == null) return;

        float p = state.getProgress(tickDelta);
        float sinP = MathHelper.sin(p * (float) Math.PI);

        switch (state.type) {
            case HUG -> {
                // Both arms wrap around partner
                rightArm.pitch = -1.25f + (sinP * 0.1f);
                rightArm.yaw = -0.55f;
                rightArm.roll = 0.0f;

                leftArm.pitch = -1.25f + (sinP * 0.1f);
                leftArm.yaw = 0.55f;
                leftArm.roll = 0.0f;

                body.pitch = 0.10f;
                head.pitch = 0.15f;
            }
            case KISS -> {
                // Lean forward, romantic tilt
                body.pitch = 0.18f;
                head.pitch = 0.20f;
                head.roll = 0.22f;

                rightArm.pitch = -0.7f;
                rightArm.yaw = -0.2f;
                leftArm.pitch = -0.7f;
                leftArm.yaw = 0.2f;
            }
            case SLAP_ATTACKER -> {
                if (p < 0.35f) {
                    // Wind up
                    float wind = p / 0.35f;
                    rightArm.yaw = 1.3f * wind;
                    rightArm.pitch = -0.4f * wind;
                    rightArm.roll = 0.3f * wind;
                } else {
                    // Swing across
                    float swing = (p - 0.35f) / 0.65f;
                    rightArm.yaw = 1.3f - (2.6f * swing);
                    rightArm.pitch = -0.3f;
                    body.yaw = -0.3f * (1.0f - swing);
                }
            }
            case SLAP_VICTIM -> {
                // Head snaps back from slap impact
                float decay = 1.0f - p;
                head.yaw = -0.85f * decay;
                head.pitch = -0.35f * decay;
                head.roll = -0.2f * decay;
                body.roll = -0.15f * decay;
            }
            case STRADDLE -> {
                // Seated playfully on lap
                leftLeg.pitch = -1.45f;
                leftLeg.yaw = -0.35f;
                rightLeg.pitch = -1.45f;
                rightLeg.yaw = 0.35f;

                leftArm.pitch = -0.9f;
                leftArm.yaw = 0.3f;
                rightArm.pitch = -0.9f;
                rightArm.yaw = -0.3f;
                body.pitch = 0.05f;
            }
            case PIGGYBACK -> {
                // Mounted riding on back
                leftLeg.pitch = -1.3f;
                leftLeg.yaw = -0.4f;
                rightLeg.pitch = -1.3f;
                rightLeg.yaw = 0.4f;

                leftArm.pitch = -1.6f;
                leftArm.yaw = 0.35f;
                rightArm.pitch = -1.6f;
                rightArm.yaw = -0.35f;
                head.pitch = -0.1f;
            }
            case MAKE_LOVE -> {
                // Warm gentle embrace
                rightArm.pitch = -1.35f;
                rightArm.yaw = -0.55f;
                leftArm.pitch = -1.35f;
                leftArm.yaw = 0.55f;

                body.pitch = 0.12f;
                head.pitch = 0.22f;
                head.roll = 0.15f * MathHelper.sin(p * (float) Math.PI * 2);
            }
            case BREAKUP -> {
                // Turned away, head down
                head.pitch = 0.55f;
                head.yaw = -0.45f;
                body.pitch = 0.05f;
                leftArm.pitch = 0.25f;
                rightArm.pitch = 0.25f;
            }
            case DIVORCE -> {
                // Completely turned away, cold stance
                head.pitch = 0.35f;
                head.yaw = 0.55f;
                body.yaw = 0.25f;
                leftArm.pitch = 0.15f;
                rightArm.pitch = 0.15f;
            }
            case PADDLE_SPANK_ATTACKER -> {
                // Swing arm downwards
                rightArm.pitch = -2.0f + (p * 2.5f);
                rightArm.yaw = -0.2f;
            }
            case PADDLE_SPANK_VICTIM -> {
                // Flinch forward
                float flinch = (1.0f - p);
                body.pitch = 0.5f * flinch;
                head.pitch = 0.35f * flinch;
                leftArm.pitch = -0.4f * flinch;
                rightArm.pitch = -0.4f * flinch;
            }
            case LEASH_SIT -> {
                // Seated on ground
                leftLeg.pitch = -1.57f;
                leftLeg.yaw = -0.12f;
                rightLeg.pitch = -1.57f;
                rightLeg.yaw = 0.12f;
                body.pitch = 0.0f;
            }
            default -> {}
        }
    }
}

