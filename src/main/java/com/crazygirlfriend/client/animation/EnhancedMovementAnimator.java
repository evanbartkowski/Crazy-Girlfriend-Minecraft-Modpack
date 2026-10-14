package com.crazygirlfriend.client.animation;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.relationship.Relationship;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EnhancedMovementAnimator {
    private static final EnhancedMovementAnimator INSTANCE = new EnhancedMovementAnimator();
    public static EnhancedMovementAnimator get() { return INSTANCE; }

    public void applyMovementAnimation(
            AbstractClientPlayerEntity player,
            ModelPart head,
            ModelPart body,
            ModelPart rightArm,
            ModelPart leftArm,
            ModelPart rightLeg,
            ModelPart leftLeg,
            float limbAngle,
            float limbDistance,
            float animationProgress,
            float tickDelta
    ) {
        // If movement animations disabled or active relationship animation playing, respect existing system
        if (!ModConfig.get().enableMovementAnimations) return;
        if (ClientAnimationManager.get().hasAnimation(player.getUuid())) return;
        if (ClientRelationshipManager.get().isSitting(player.getUuid())) return;
        if (player.hasVehicle()) return;

        float age = player.age + tickDelta;

        // 1. Idle Breathing & Subtle Natural Posture (when standing or barely moving)
        if (limbDistance < 0.1f) {
            float breath = MathHelper.sin(age * 0.07f) * 0.02f;
            body.pitch += breath;
            head.pitch -= breath * 0.7f;
            rightArm.roll += MathHelper.cos(age * 0.07f) * 0.015f;
            leftArm.roll -= MathHelper.cos(age * 0.07f) * 0.015f;

            // Relaxed stance near romantic partner
            Relationship rel = ClientRelationshipManager.get().getRelationship(player.getUuid());
            if (rel != null) {
                net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                if (client.world != null) {
                    net.minecraft.entity.player.PlayerEntity partner = client.world.getPlayerByUuid(rel.getOtherPlayerUuid(player.getUuid()));
                    if (partner != null && partner.squaredDistanceTo(player) < 16.0) {
                        // Gentle romantic lean towards partner
                        body.roll += 0.035f;
                        head.yaw += 0.05f;
                    }
                }
            }
        }

        // 2. Sprinting Forward Lean & Athletic Running Form
        if (player.isSprinting() && limbDistance > 0.1f) {
            float speedFactor = Math.min(limbDistance, 1.0f);
            body.pitch += 0.22f * speedFactor;
            head.pitch -= 0.16f * speedFactor;

            // Natural arm pumping
            rightArm.roll += 0.06f;
            leftArm.roll -= 0.06f;

            // Subtle torso twist while running
            body.yaw += MathHelper.sin(limbAngle) * 0.08f * speedFactor;
        }

        // 3. Fluid Swimming Strokes
        if (player.isInSwimmingPose() || (player.isTouchingWater() && !player.isOnGround())) {
            float swimCycle = age * 0.25f;
            float kick = MathHelper.sin(swimCycle) * 0.35f;
            rightLeg.pitch = kick;
            leftLeg.pitch = -kick;

            float armStroke = MathHelper.cos(swimCycle) * 0.45f;
            rightArm.pitch = -1.4f + armStroke;
            leftArm.pitch = -1.4f - armStroke;
            rightArm.roll = 0.3f;
            leftArm.roll = -0.3f;
            body.pitch += 0.1f;
        }

        // 4. Climbing Ladders / Vines
        if (player.isClimbing()) {
            float climbCycle = age * 0.2f;
            rightArm.pitch = -1.8f + MathHelper.sin(climbCycle) * 0.4f;
            leftArm.pitch = -1.8f - MathHelper.sin(climbCycle) * 0.4f;
            rightLeg.pitch = MathHelper.cos(climbCycle) * 0.35f;
            leftLeg.pitch = -MathHelper.cos(climbCycle) * 0.35f;
        }

        // 5. Airborne / Falling / Jumping Balance
        if (!player.isOnGround() && !player.isInSwimmingPose() && !player.isClimbing() && !player.isFallFlying()) {
            Vec3d vel = player.getVelocity();
            float fallFactor = MathHelper.clamp((float) -vel.y * 1.5f, 0.0f, 1.0f);

            // Legs trail slightly
            rightLeg.pitch += 0.15f + fallFactor * 0.2f;
            leftLeg.pitch += 0.22f + fallFactor * 0.25f;

            // Arms outward for natural balance
            rightArm.roll += 0.25f + fallFactor * 0.2f;
            leftArm.roll -= 0.25f + fallFactor * 0.2f;
            rightArm.pitch *= 0.5f;
            leftArm.pitch *= 0.5f;
        }

        // 6. Context-Sensitive Shivering (Cold Biomes, Snowstorms, or Winter)
        BlockPos pos = player.getBlockPos();
        boolean isFreezingBiome = player.getWorld().getBiome(pos).value().isCold(pos);
        boolean isWinter = com.crazygirlfriend.season.SeasonManager.get().getClientSeason() == com.crazygirlfriend.season.Season.WINTER;
        if ((isFreezingBiome || isWinter) && player.getWorld().isSkyVisible(pos)) {
            float shiver = MathHelper.sin(age * 1.6f) * 0.025f;
            body.yaw += shiver;
            head.yaw -= shiver * 0.6f;
            rightArm.roll += Math.abs(shiver);
            leftArm.roll -= Math.abs(shiver);
        }
    }
}

