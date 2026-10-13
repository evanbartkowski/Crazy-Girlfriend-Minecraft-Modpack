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
            if (totalDuration <= 0) return (currentTick % 40 + tickDelta) / 40.0f;
            return MathHelper.clamp((currentTick + tickDelta) / (float) totalDuration, 0.0f, 1.0f);
        }

        public float getEaseProgress(float tickDelta) {
            float p = getProgress(tickDelta);
            return p < 0.5f ? 4.0f * p * p * p : 1.0f - (float) Math.pow(-2.0f * p + 2.0f, 3) / 2.0f;
        }

        public float getBlendWeight(float tickDelta) {
            if (totalDuration <= 0) {
                return MathHelper.clamp((currentTick + tickDelta) / 6.0f, 0.0f, 1.0f);
            }
            float cur = currentTick + tickDelta;
            float inWeight = MathHelper.clamp(cur / 6.0f, 0.0f, 1.0f);
            float outWeight = MathHelper.clamp((totalDuration - cur) / 6.0f, 0.0f, 1.0f);
            return Math.min(inWeight, outWeight);
        }

        public float getCycle(float speed, float tickDelta) {
            return (currentTick + tickDelta) * speed;
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
            state.currentTick++;
            if (state.totalDuration > 0 && state.currentTick >= state.totalDuration) {
                return true;
            }
            return false;
        });
    }

    public void applyAnimationToModel(UUID playerUuid, ModelPart head, ModelPart body, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg, float tickDelta) {
        ClientAnimState state = animations.get(playerUuid);
        if (state == null) return;

        float p = state.getProgress(tickDelta);
        float sinP = MathHelper.sin(p * (float) Math.PI);
        float cycle = state.getCycle(0.2f, tickDelta);

        // Idle micro-movements (breathing, soft sway) for sustained poses
        if (state.type == AnimationType.CUDDLE || state.type == AnimationType.CUDDLE_SLEEPING ||
            state.type == AnimationType.LAP_SITTING || state.type == AnimationType.STRADDLE ||
            state.type == AnimationType.HAND_HOLDING || state.type == AnimationType.SIT_TOGETHER ||
            state.type == AnimationType.STARGAZING || state.type == AnimationType.LEASH_SIT ||
            state.type == AnimationType.CAMPFIRE_CUDDLE) {
            float breath = MathHelper.sin(cycle * 0.35f) * 0.025f;
            body.pitch += breath;
            head.pitch -= breath * 0.6f;
        }

        switch (state.type) {
            // === ROMANTIC INTERACTIONS ===
            case HUG -> {
                rightArm.pitch = -1.25f + (sinP * 0.1f);
                rightArm.yaw = -0.55f;
                leftArm.pitch = -1.25f + (sinP * 0.1f);
                leftArm.yaw = 0.55f;
                body.pitch = 0.10f;
                head.pitch = 0.15f;
            }
            case LONG_HUG -> {
                float sway = MathHelper.sin(cycle * 0.5f) * 0.08f;
                rightArm.pitch = -1.35f;
                rightArm.yaw = -0.6f + sway;
                leftArm.pitch = -1.35f;
                leftArm.yaw = 0.6f + sway;
                body.roll = sway;
                body.pitch = 0.08f;
                head.roll = -sway * 1.5f;
            }
            case BACK_HUG -> {
                rightArm.pitch = -1.2f;
                rightArm.yaw = -0.5f;
                leftArm.pitch = -1.2f;
                leftArm.yaw = 0.5f;
                body.pitch = 0.15f;
                head.pitch = 0.2f;
            }
            case KISS, QUICK_KISS -> {
                body.pitch = 0.18f;
                head.pitch = 0.20f;
                head.roll = 0.22f;
                rightArm.pitch = -0.7f;
                rightArm.yaw = -0.2f;
                leftArm.pitch = -0.7f;
                leftArm.yaw = 0.2f;
            }
            case MAKE_OUT -> {
                float pulse = MathHelper.sin(cycle) * 0.05f;
                body.pitch = 0.22f + pulse;
                head.pitch = 0.25f;
                head.roll = 0.28f + (pulse * 2f);
                rightArm.pitch = -1.5f;
                rightArm.yaw = -0.65f;
                leftArm.pitch = -1.5f;
                leftArm.yaw = 0.65f;
            }
            case FOREHEAD_KISS -> {
                body.pitch = 0.12f;
                head.pitch = 0.35f;
                rightArm.pitch = -1.1f;
                rightArm.yaw = -0.3f;
                leftArm.pitch = -1.1f;
                leftArm.yaw = 0.3f;
            }
            case CHEEK_KISS -> {
                body.pitch = 0.10f;
                head.pitch = 0.15f;
                head.yaw = 0.45f;
                rightArm.pitch = -0.6f;
                leftArm.pitch = -0.6f;
            }
            case KISS_ATTACK -> {
                float peck = Math.abs(MathHelper.sin(cycle * 3f)) * 0.25f;
                head.pitch = 0.2f + peck;
                body.pitch = 0.15f + (peck * 0.5f);
                rightArm.pitch = -1.2f;
                rightArm.yaw = -0.4f;
                leftArm.pitch = -1.2f;
                leftArm.yaw = 0.4f;
            }
            case PRINCESS_KISS -> {
                body.pitch = -0.2f;
                body.roll = 0.15f;
                head.pitch = -0.1f;
                head.roll = -0.2f;
                rightArm.pitch = -1.6f;
                leftArm.pitch = -0.8f;
            }
            case NUZZLE -> {
                float nuz = MathHelper.sin(cycle * 1.5f) * 0.2f;
                head.yaw = 0.3f + nuz;
                head.roll = 0.15f + (nuz * 0.5f);
                body.pitch = 0.08f;
                rightArm.pitch = -0.5f;
                leftArm.pitch = -0.5f;
            }
            case CUDDLE, CUDDLE_SLEEPING -> {
                rightArm.pitch = -1.4f;
                rightArm.yaw = -0.6f;
                leftArm.pitch = -1.4f;
                leftArm.yaw = 0.6f;
                body.pitch = 0.10f;
                head.roll = 0.3f;
                head.pitch = 0.15f;
            }
            case SPOONING -> {
                leftLeg.pitch = -0.3f;
                rightLeg.pitch = -0.5f;
                rightArm.pitch = -1.2f;
                leftArm.pitch = -0.8f;
                head.pitch = 0.1f;
            }
            case LAP_SITTING -> {
                leftLeg.pitch = -1.57f;
                rightLeg.pitch = -1.57f;
                leftLeg.yaw = -0.1f;
                rightLeg.yaw = 0.1f;
                rightArm.pitch = -0.8f;
                leftArm.pitch = -0.8f;
                body.pitch = 0.05f;
            }
            case STRADDLE -> {
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
            case HAND_HOLDING -> {
                rightArm.pitch = -0.35f;
                rightArm.yaw = 0.25f;
                rightArm.roll = 0.1f;
            }
            case SLOW_DANCE -> {
                float sway = MathHelper.sin(cycle * 0.8f) * 0.15f;
                body.roll = sway;
                body.yaw = sway * 0.5f;
                rightArm.pitch = -1.3f;
                rightArm.yaw = -0.4f + sway;
                leftArm.pitch = -1.3f;
                leftArm.yaw = 0.4f + sway;
                head.roll = -sway;
            }
            case FAST_DANCE -> {
                float step = MathHelper.sin(cycle * 2.5f);
                rightArm.pitch = step * 0.8f;
                leftArm.pitch = -step * 0.8f;
                rightLeg.pitch = -step * 0.5f;
                leftLeg.pitch = step * 0.5f;
                body.yaw = step * 0.2f;
            }
            case PIGGYBACK -> {
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
            case BRIDAL_CARRY -> {
                rightArm.pitch = -1.4f;
                rightArm.yaw = -0.5f;
                leftArm.pitch = -1.4f;
                leftArm.yaw = 0.5f;
                body.pitch = -0.1f;
            }
            case SHOULDER_CARRY -> {
                rightArm.pitch = -2.2f;
                rightArm.yaw = -0.2f;
                body.pitch = 0.15f;
            }
            case TICKLE_FIGHT -> {
                float wig = MathHelper.sin(cycle * 4f) * 0.35f;
                rightArm.pitch = -1.2f + wig;
                leftArm.pitch = -1.2f - wig;
                body.yaw = wig * 0.3f;
                head.pitch = -0.2f;
            }
            case FLIRTING -> {
                head.roll = 0.25f;
                head.pitch = -0.1f;
                rightArm.pitch = -0.8f;
                rightArm.yaw = -0.4f;
                rightArm.roll = 0.3f; // Hand on hip
                body.yaw = 0.15f;
            }
            case SLEEP_TOGETHER, ROMANTIC_NIGHT, MAKE_LOVE -> {
                rightArm.pitch = -1.35f;
                rightArm.yaw = -0.55f;
                leftArm.pitch = -1.35f;
                leftArm.yaw = 0.55f;
                body.pitch = 0.12f;
                head.pitch = 0.22f;
                head.roll = 0.15f * MathHelper.sin(p * (float) Math.PI * 2);
            }
            case SURPRISE_HUG -> {
                rightArm.pitch = -1.3f;
                rightArm.yaw = -0.4f;
                leftArm.pitch = -1.3f;
                leftArm.yaw = 0.4f;
                body.pitch = 0.18f;
                head.pitch = -0.1f;
            }
            case HEAD_ON_SHOULDER -> {
                head.roll = 0.45f;
                head.pitch = 0.1f;
                rightArm.pitch = -0.5f;
                leftArm.pitch = -0.5f;
            }
            case SIT_TOGETHER -> {
                leftLeg.pitch = -1.57f;
                rightLeg.pitch = -1.57f;
                body.pitch = 0.0f;
                rightArm.pitch = -0.4f;
                leftArm.pitch = -0.4f;
            }
            case COMFORT, RECONCILIATION_HUG -> {
                rightArm.pitch = -1.3f + (MathHelper.sin(cycle * 1.2f) * 0.1f); // Gentle patting
                rightArm.yaw = -0.4f;
                leftArm.pitch = -1.3f;
                leftArm.yaw = 0.4f;
                body.pitch = 0.12f;
                head.pitch = 0.18f;
            }
            case STARGAZING -> {
                head.pitch = -0.8f; // Looking way up at sky
                leftLeg.pitch = -0.4f;
                rightLeg.pitch = -0.4f;
                rightArm.pitch = -0.5f;
                leftArm.pitch = -0.5f;
            }

            // === FUNNY & CHAOTIC ANIMATIONS ===
            case SLAP_ATTACKER -> {
                if (p < 0.35f) {
                    float wind = p / 0.35f;
                    rightArm.yaw = 1.3f * wind;
                    rightArm.pitch = -0.4f * wind;
                } else {
                    float swing = (p - 0.35f) / 0.65f;
                    rightArm.yaw = 1.3f - (2.6f * swing);
                    rightArm.pitch = -0.3f;
                    body.yaw = -0.3f * (1.0f - swing);
                }
            }
            case SLAP_VICTIM -> {
                float decay = 1.0f - p;
                head.yaw = -0.85f * decay;
                head.pitch = -0.35f * decay;
                head.roll = -0.2f * decay;
                body.roll = -0.15f * decay;
            }
            case SLAP_DRAMATIC -> {
                float spin = p * (float) Math.PI * 2f;
                body.yaw = spin;
                head.pitch = -0.3f;
                rightArm.pitch = -1.2f;
                leftArm.pitch = -1.2f;
            }
            case BONK_ATTACKER, FRYING_PAN_SWING -> {
                if (p < 0.4f) {
                    rightArm.pitch = -2.2f * (p / 0.4f); // Raise high
                } else {
                    rightArm.pitch = -2.2f + (2.6f * ((p - 0.4f) / 0.6f)); // Smash down!
                    body.pitch = 0.25f;
                }
            }
            case BONK_VICTIM -> {
                float decay = 1.0f - p;
                head.pitch = 0.6f * decay;
                body.pitch = 0.3f * decay;
                float wob = MathHelper.sin(cycle * 3f) * 0.15f * decay;
                head.roll = wob;
                body.roll = -wob;
            }
            case SQUEAK_HAMMER_ATTACKER -> {
                rightArm.pitch = -1.9f + (sinP * 1.5f);
            }
            case SQUEAK_HAMMER_VICTIM -> {
                head.pitch = 0.4f * (1f - p);
                leftLeg.pitch = MathHelper.sin(cycle * 2f) * 0.2f;
                rightLeg.pitch = -leftLeg.pitch;
            }
            case FOOT_STOMPING -> {
                float stomp = Math.abs(MathHelper.sin(cycle * 3f)) * 0.5f;
                rightLeg.pitch = -stomp;
                body.pitch = 0.15f;
                rightArm.pitch = -0.8f;
                leftArm.pitch = -0.8f;
                head.pitch = 0.2f;
            }
            case ARMS_CROSSED -> {
                rightArm.pitch = -0.85f;
                rightArm.yaw = -0.6f;
                leftArm.pitch = -0.85f;
                leftArm.yaw = 0.6f;
                head.yaw = 0.4f; // Sassy look away
                head.pitch = -0.1f;
            }
            case DRAMATIC_CRYING -> {
                rightArm.pitch = -1.5f;
                rightArm.yaw = -0.4f; // Covering face
                leftArm.pitch = -1.5f;
                leftArm.yaw = 0.4f;
                head.pitch = 0.4f;
                body.pitch = 0.2f + (MathHelper.sin(cycle * 2f) * 0.05f); // Sobbing shakes
            }
            case FALL_DESPAIR -> {
                leftLeg.pitch = -1.57f;
                rightLeg.pitch = -1.57f;
                body.pitch = 0.6f;
                head.pitch = 0.5f;
                rightArm.pitch = 0.3f;
                leftArm.pitch = 0.3f;
            }
            case FAKE_FAINTING -> {
                body.pitch = -0.6f * sinP;
                head.pitch = -0.4f * sinP;
                rightArm.pitch = -1.6f * sinP;
                rightArm.yaw = -0.3f; // Hand on forehead
                leftArm.pitch = 0.2f;
            }
            case SIDE_EYE -> {
                head.yaw = -0.55f;
                head.roll = 0.15f;
                body.yaw = 0.2f;
            }
            case POUTING -> {
                head.pitch = 0.25f;
                head.roll = 0.1f;
                rightArm.pitch = -0.7f;
                rightArm.yaw = -0.5f;
                leftArm.pitch = -0.7f;
                leftArm.yaw = 0.5f;
            }
            case ANGRY_JUMPING -> {
                float jump = Math.abs(MathHelper.sin(cycle * 2.5f)) * 0.4f;
                rightArm.pitch = -1.2f;
                leftArm.pitch = -1.2f;
                rightLeg.pitch = -jump;
                leftLeg.pitch = jump;
                head.pitch = -0.2f;
            }
            case TANTRUM -> {
                float flail = MathHelper.sin(cycle * 3f) * 0.6f;
                rightArm.pitch = -1.0f + flail;
                leftArm.pitch = -1.0f - flail;
                rightLeg.pitch = flail * 0.5f;
                leftLeg.pitch = -flail * 0.5f;
                head.pitch = 0.3f;
            }
            case ROLLING_EYES -> {
                head.pitch = -0.4f;
                head.roll = MathHelper.sin(cycle) * 0.15f;
                body.pitch = -0.05f;
            }
            case LAUGHING_HARD -> {
                float laugh = Math.abs(MathHelper.sin(cycle * 3f)) * 0.25f;
                body.pitch = 0.35f + laugh;
                head.pitch = -0.2f - laugh;
                rightArm.pitch = -0.9f;
                rightArm.yaw = -0.4f; // Clutching belly
                leftArm.pitch = -0.9f;
                leftArm.yaw = 0.4f;
            }
            case BLUSHING -> {
                rightArm.pitch = -1.4f;
                rightArm.yaw = -0.3f; // Shyly covering cheeks
                leftArm.pitch = -1.4f;
                leftArm.yaw = 0.3f;
                head.pitch = 0.25f;
                head.roll = 0.1f;
            }
            case DRAMATIC_REJECTION -> {
                rightArm.pitch = -1.5f; // Talk to the hand
                rightArm.yaw = 0.1f;
                body.pitch = -0.2f;
                head.yaw = 0.6f;
            }
            case BACKING_AWAY -> {
                body.pitch = -0.2f;
                rightArm.pitch = -0.6f;
                leftArm.pitch = -0.6f;
                head.pitch = -0.1f;
            }
            case PILLOW_FIGHT_ATTACKER -> {
                rightArm.pitch = -1.8f + (p * 2.2f);
                leftArm.pitch = -0.5f;
            }
            case PILLOW_FIGHT_VICTIM -> {
                body.pitch = -0.3f * (1f - p);
                head.pitch = -0.2f * (1f - p);
                rightArm.pitch = -1.0f;
                leftArm.pitch = -1.0f;
            }
            case PLAYFUL_WRESTLE -> {
                float wr = MathHelper.sin(cycle * 1.5f) * 0.3f;
                rightArm.pitch = -1.3f + wr;
                leftArm.pitch = -1.3f - wr;
                body.yaw = wr;
            }
            case RING_THROW -> {
                if (p < 0.4f) {
                    rightArm.pitch = -1.5f;
                } else {
                    rightArm.pitch = -0.2f;
                    rightArm.yaw = 0.5f; // Fling arm out
                }
                head.yaw = -0.5f;
            }
            case SURPRISE_REACTION -> {
                rightArm.pitch = -1.8f;
                leftArm.pitch = -1.8f;
                head.pitch = -0.3f;
                body.pitch = -0.15f;
            }
            case JUMP_APART -> {
                float pop = (1.0f - p);
                body.pitch = -0.3f * pop;
                rightArm.pitch = -1.2f * pop;
                leftArm.pitch = -1.2f * pop;
                head.pitch = -0.2f * pop;
            }
            case STRUGGLE_CARRY -> {
                float wobble = MathHelper.sin(cycle * 2.5f) * 0.2f;
                body.pitch = 0.45f;
                body.roll = wobble;
                rightArm.pitch = -1.6f;
                leftArm.pitch = -1.6f;
                head.pitch = 0.4f;
            }
            case DIZZY -> {
                float spin = cycle * 1.5f;
                head.yaw = MathHelper.sin(spin) * 0.4f;
                head.pitch = MathHelper.cos(spin) * 0.2f;
                body.roll = MathHelper.sin(spin * 0.5f) * 0.15f;
                rightArm.pitch = -0.6f;
                leftArm.pitch = -0.6f;
            }
            case SNORING -> {
                float snore = Math.abs(MathHelper.sin(cycle * 0.8f)) * 0.15f;
                body.pitch = -snore;
                head.pitch = -0.3f + snore;
                rightArm.pitch = -0.3f;
                leftArm.pitch = -0.3f;
            }
            case SHIVERING -> {
                float shiv = MathHelper.sin(cycle * 5f) * 0.08f;
                body.roll = shiv;
                rightArm.pitch = -1.0f;
                rightArm.yaw = -0.5f + shiv;
                leftArm.pitch = -1.0f;
                leftArm.yaw = 0.5f - shiv;
                head.pitch = 0.3f;
            }
            case LEASH_SIT -> {
                leftLeg.pitch = -1.57f;
                leftLeg.yaw = -0.12f;
                rightLeg.pitch = -1.57f;
                rightLeg.yaw = 0.12f;
                body.pitch = 0.0f;
            }
            case LEASH_CRAWL -> {
                body.pitch = 1.35f; // Hands and knees horizontal
                head.pitch = -0.8f; // Look forward
                rightArm.pitch = 0.8f;
                leftArm.pitch = 0.8f;
                rightLeg.pitch = 0.8f;
                leftLeg.pitch = 0.8f;
            }
            case DRAGGED_PLAYFUL -> {
                body.pitch = -0.4f;
                leftLeg.pitch = -0.2f;
                rightLeg.pitch = -0.2f;
                rightArm.pitch = -1.8f;
                leftArm.pitch = -1.8f;
            }
            case BREAKUP -> {
                head.pitch = 0.55f;
                head.yaw = -0.45f;
                body.pitch = 0.05f;
                leftArm.pitch = 0.25f;
                rightArm.pitch = 0.25f;
            }
            case DIVORCE -> {
                head.pitch = 0.35f;
                head.yaw = 0.55f;
                body.yaw = 0.25f;
                leftArm.pitch = 0.15f;
                rightArm.pitch = 0.15f;
            }
            case PADDLE_SPANK_ATTACKER -> {
                if (p < 0.38f) {
                    float wind = p / 0.38f;
                    rightArm.pitch = -2.4f * wind;
                    rightArm.yaw = -0.35f * wind;
                } else {
                    float strike = (p - 0.38f) / 0.62f;
                    rightArm.pitch = -2.4f + (3.2f * strike);
                    rightArm.yaw = -0.35f + (0.45f * strike);
                    body.pitch = 0.28f * strike;
                }
            }
            case PADDLE_SPANK_VICTIM -> {
                // Bending over forward at 90 degrees with hands placed on knees
                float bend = MathHelper.sin(p * (float) Math.PI);
                body.pitch = 1.35f * bend;
                head.pitch = -0.55f * bend;
                leftArm.pitch = 0.65f * bend;
                rightArm.pitch = 0.65f * bend;
                leftLeg.pitch = 0.05f * bend;
                rightLeg.pitch = 0.05f * bend;
            }
            case WET_SHAKE -> {
                float shake = MathHelper.sin(cycle * 4f) * 0.4f;
                body.yaw = shake;
                head.yaw = -shake * 1.5f;
                rightArm.pitch = -0.5f;
                leftArm.pitch = -0.5f;
            }
            // === ANIMATION OVERHAUL VARIANTS ===
            case BEAR_HUG -> {
                rightArm.pitch = -1.45f + (sinP * 0.12f);
                rightArm.yaw = -0.72f;
                leftArm.pitch = -1.45f + (sinP * 0.12f);
                leftArm.yaw = 0.72f;
                body.pitch = 0.18f;
                head.pitch = 0.22f;
                head.roll = 0.10f;
            }
            case SPINNING_HUG -> {
                float spin = p * (float) Math.PI * 4.0f; // Two full rotations
                body.yaw = spin;
                rightArm.pitch = -1.30f;
                rightArm.yaw = -0.55f;
                leftArm.pitch = -1.30f;
                leftArm.yaw = 0.55f;
                head.pitch = 0.15f;
                leftLeg.pitch = -0.2f * sinP;
                rightLeg.pitch = 0.2f * sinP;
            }
            case JUMPING_HUG -> {
                float jumpArc = sinP;
                body.pitch = 0.10f - (jumpArc * 0.15f);
                head.pitch = 0.25f;
                rightArm.pitch = -1.65f;
                rightArm.yaw = -0.45f;
                leftArm.pitch = -1.65f;
                leftArm.yaw = 0.45f;
                leftLeg.pitch = -1.35f * jumpArc;
                rightLeg.pitch = -1.35f * jumpArc;
                leftLeg.yaw = -0.2f;
                rightLeg.yaw = 0.2f;
            }
            case ROMANTIC_SWAY_HUG -> {
                float sway = MathHelper.sin(cycle * 0.45f) * 0.12f;
                rightArm.pitch = -1.30f;
                rightArm.yaw = -0.50f + sway;
                leftArm.pitch = -1.30f;
                leftArm.yaw = 0.50f + sway;
                body.roll = sway;
                body.pitch = 0.08f;
                head.roll = -sway * 1.3f;
                head.pitch = 0.12f;
            }
            case ROMANTIC_DIP_KISS -> {
                float dipProgress = sinP;
                body.pitch = 0.52f * dipProgress;
                body.roll = 0.18f * dipProgress;
                head.pitch = -0.35f * dipProgress;
                head.roll = -0.22f * dipProgress;
                rightArm.pitch = -1.55f;
                rightArm.yaw = -0.4f;
                leftArm.pitch = -0.6f;
                leftArm.roll = 0.35f;
                leftLeg.pitch = -0.75f * dipProgress;
                leftLeg.roll = 0.25f * dipProgress;
            }
            case SURPRISE_KISS -> {
                if (p < 0.30f) {
                    float startle = p / 0.30f;
                    body.pitch = -0.25f * startle;
                    head.pitch = -0.15f * startle;
                    rightArm.pitch = -0.9f * startle;
                    leftArm.pitch = -0.9f * startle;
                } else {
                    float melt = (p - 0.30f) / 0.70f;
                    body.pitch = -0.25f + (0.42f * melt);
                    head.pitch = 0.20f * melt;
                    head.roll = 0.25f * melt;
                    rightArm.pitch = -0.9f - (0.4f * melt);
                    leftArm.pitch = -0.9f - (0.4f * melt);
                }
            }
            case HEAD_BUMP_KISS -> {
                if (p < 0.35f) {
                    float lean = p / 0.35f;
                    body.pitch = 0.35f * lean;
                    head.pitch = 0.35f * lean;
                } else if (p < 0.55f) {
                    float recoil = (p - 0.35f) / 0.20f;
                    body.pitch = 0.35f - (0.70f * recoil);
                    head.pitch = 0.35f - (0.90f * recoil);
                } else {
                    float recover = (p - 0.55f) / 0.45f;
                    head.pitch = 0.20f;
                    head.roll = MathHelper.sin(cycle * 3f) * 0.15f * (1.0f - recover);
                    rightArm.pitch = -1.75f;
                    rightArm.yaw = -0.35f;
                    leftArm.pitch = -0.4f;
                }
            }
            case STUMBLE_CARRY -> {
                float stumbleWobble = MathHelper.sin(cycle * 3.5f) * 0.25f;
                body.pitch = 0.48f + (Math.abs(stumbleWobble) * 0.1f);
                body.roll = stumbleWobble;
                rightArm.pitch = -1.65f + (stumbleWobble * 0.3f);
                leftArm.pitch = -1.65f - (stumbleWobble * 0.3f);
                head.pitch = 0.42f;
                leftLeg.pitch = MathHelper.sin(cycle * 2.5f) * 0.4f;
                rightLeg.pitch = -leftLeg.pitch;
            }
            case PASSENGER_WAVING -> {
                leftLeg.pitch = -1.3f;
                leftLeg.yaw = -0.4f;
                rightLeg.pitch = -1.3f;
                rightLeg.yaw = 0.4f;
                leftArm.pitch = -1.5f;
                leftArm.yaw = 0.3f;
                float wave = MathHelper.sin(cycle * 3.0f) * 0.45f;
                rightArm.pitch = -2.25f;
                rightArm.roll = 0.35f + wave;
                head.pitch = -0.2f;
                head.yaw = 0.2f;
            }
            case GENTLE_DISMOUNT -> {
                float decay = 1.0f - p;
                body.pitch = 0.15f * decay;
                rightArm.pitch = -0.45f * decay;
                leftArm.pitch = -0.45f * decay;
                leftLeg.pitch = -0.2f * decay;
                rightLeg.pitch = -0.2f * decay;
            }
            case SPINNING_SLAP -> {
                if (p < 0.45f) {
                    float wind = p / 0.45f;
                    body.yaw = wind * (float) Math.PI * 2.0f;
                    rightArm.pitch = -0.6f;
                    rightArm.yaw = 1.4f;
                } else {
                    float release = (p - 0.45f) / 0.55f;
                    rightArm.pitch = -0.3f;
                    rightArm.yaw = 1.4f - (2.8f * release);
                    body.yaw = -0.45f * (1.0f - release);
                }
            }
            case FISH_SLAP_ATTACKER -> {
                if (p < 0.35f) {
                    float wind = p / 0.35f;
                    rightArm.pitch = -0.6f;
                    rightArm.yaw = 1.5f * wind;
                    leftArm.pitch = -0.5f;
                    leftArm.yaw = 1.2f * wind;
                } else {
                    float slap = (p - 0.35f) / 0.65f;
                    rightArm.pitch = -0.4f;
                    rightArm.yaw = 1.5f - (3.0f * slap);
                    leftArm.pitch = -0.3f;
                    leftArm.yaw = 1.2f - (2.4f * slap);
                    body.yaw = -0.4f * (1.0f - slap);
                }
            }
            case FISH_SLAP_VICTIM -> {
                float decay = 1.0f - p;
                head.yaw = -1.15f * decay;
                head.pitch = -0.35f * decay;
                head.roll = -0.30f * decay;
                body.roll = -0.25f * decay;
                leftArm.pitch = -0.8f * decay;
                rightArm.pitch = 0.4f * decay;
            }
            case FAKE_COLLAPSE -> {
                float swoon = sinP;
                body.pitch = -0.75f * swoon;
                head.pitch = -0.45f * swoon;
                rightArm.pitch = -1.80f * swoon;
                rightArm.yaw = -0.35f;
                leftArm.pitch = 0.35f * swoon;
                leftLeg.pitch = -0.3f * swoon;
            }
            case RAGDOLL_FAINT -> {
                float flop = Math.min(1.0f, p * 1.5f);
                body.pitch = 0.65f * flop;
                head.pitch = 0.55f * flop;
                rightArm.pitch = 0.25f * flop;
                leftArm.pitch = 0.25f * flop;
                leftLeg.pitch = -0.70f * flop;
                rightLeg.pitch = -0.50f * flop;
            }
            case RAIN_TWIRL -> {
                float twirl = p * (float) Math.PI * 3.0f;
                body.yaw = twirl;
                head.pitch = -0.25f;
                rightArm.pitch = -0.90f;
                rightArm.yaw = 0.65f;
                leftArm.pitch = -0.55f;
                leftArm.roll = -0.45f;
            }
            case CAMPFIRE_CUDDLE -> {
                leftLeg.pitch = -1.50f;
                rightLeg.pitch = -1.50f;
                leftLeg.yaw = -0.1f;
                rightLeg.yaw = 0.1f;
                body.pitch = 0.08f;
                head.pitch = 0.15f;
                head.roll = 0.25f;
                rightArm.pitch = -1.25f;
                rightArm.yaw = -0.65f;
                leftArm.pitch = -0.55f;
                leftArm.yaw = 0.25f;
            }
            case UNDERWATER_KISS -> {
                float drift = MathHelper.sin(cycle * 0.8f) * 0.12f;
                body.pitch = 0.12f + drift;
                body.roll = MathHelper.sin(cycle * 0.5f) * 0.10f;
                head.pitch = 0.18f;
                head.roll = 0.20f;
                rightArm.pitch = -1.20f + drift;
                rightArm.yaw = -0.45f;
                leftArm.pitch = -1.20f - drift;
                leftArm.yaw = 0.45f;
                leftLeg.pitch = -0.3f + drift;
                rightLeg.pitch = -0.3f - drift;
            }
            default -> {}
        }
    }
}
