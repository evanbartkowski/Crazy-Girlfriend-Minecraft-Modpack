package com.crazygirlfriend.mixin;

import com.crazygirlfriend.client.render.FacialExpressionFeatureRenderer;
import com.crazygirlfriend.client.render.LeashFeatureRenderer;
import com.crazygirlfriend.client.render.MakeupFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCrazyGirlfriendFeatures(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.addFeature(new MakeupFeatureRenderer(this));
        this.addFeature(new LeashFeatureRenderer(this));
        this.addFeature(new FacialExpressionFeatureRenderer(this));
    }

    @Inject(method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V", at = @At("TAIL"))
    private void applySideSleepingTransforms(AbstractClientPlayerEntity player, net.minecraft.client.util.math.MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        if (player.isInPose(net.minecraft.entity.EntityPose.SLEEPING)) {
            float lateralOffset = com.crazygirlfriend.client.bed.CoSleepingClientTracker.getLateralOffset(player);
            if (lateralOffset != 0.0f) {
                matrices.translate(lateralOffset, 0.0f, 0.0f);
            }
            float rollAngle = com.crazygirlfriend.client.bed.CoSleepingClientTracker.getRollAngle(player);
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(rollAngle));
            // Subtle adjustment so shoulder and body rest naturally on the mattress
            matrices.translate(0.0f, 0.04f, 0.0f);
        }
    }
}

