package com.crazygirlfriend.mixin;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.client.animation.ClientAnimationManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {
    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void applyCrazyGirlfriendAnimation(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            float tickDelta = MinecraftClient.getInstance().getTickDelta();
            com.crazygirlfriend.client.animation.EnhancedMovementAnimator.get().applyMovementAnimation(
                player,
                this.head,
                this.body,
                this.rightArm,
                this.leftArm,
                this.rightLeg,
                this.leftLeg,
                limbAngle,
                limbDistance,
                animationProgress,
                tickDelta
            );

            ClientAnimationManager.get().applyAnimationToModel(
                player.getUuid(),
                this.head,
                this.body,
                this.rightArm,
                this.leftArm,
                this.rightLeg,
                this.leftLeg,
                tickDelta
            );

            if (ClientRelationshipManager.get().isSitting(player.getUuid())) {
                this.leftLeg.pitch = -1.57f;
                this.leftLeg.yaw = -0.1f;
                this.rightLeg.pitch = -1.57f;
                this.rightLeg.yaw = 0.1f;
            }

            if (player.isInPose(net.minecraft.entity.EntityPose.SLEEPING)) {
                float rollAngle = com.crazygirlfriend.client.bed.CoSleepingClientTracker.getRollAngle(player);
                float breath = (float) Math.sin((player.age + tickDelta) * 0.08f) * 0.03f;

                if (rollAngle > 0) {
                    // Rolled onto right side (right is bottom, left is top)
                    this.rightLeg.pitch = 0.35f;
                    this.rightLeg.yaw = -0.05f;
                    this.rightLeg.roll = -0.05f;

                    this.leftLeg.pitch = 0.55f;
                    this.leftLeg.yaw = 0.08f;
                    this.leftLeg.roll = 0.08f;

                    this.rightArm.pitch = -0.7f;
                    this.rightArm.yaw = -0.2f;
                    this.rightArm.roll = -0.2f;

                    this.leftArm.pitch = -0.85f;
                    this.leftArm.yaw = 0.35f;
                    this.leftArm.roll = 0.25f + breath;

                    this.head.pitch = 0.1f + breath;
                    this.head.roll = 0.15f;
                } else {
                    // Rolled onto left side (left is bottom, right is top)
                    this.leftLeg.pitch = 0.35f;
                    this.leftLeg.yaw = 0.05f;
                    this.leftLeg.roll = 0.05f;

                    this.rightLeg.pitch = 0.55f;
                    this.rightLeg.yaw = -0.08f;
                    this.rightLeg.roll = -0.08f;

                    this.leftArm.pitch = -0.7f;
                    this.leftArm.yaw = 0.2f;
                    this.leftArm.roll = 0.2f;

                    this.rightArm.pitch = -0.85f;
                    this.rightArm.yaw = -0.35f;
                    this.rightArm.roll = -0.25f - breath;

                    this.head.pitch = 0.1f + breath;
                    this.head.roll = -0.15f;
                }
            }
        }
    }
}

