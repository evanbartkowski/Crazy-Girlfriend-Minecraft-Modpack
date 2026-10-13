package com.crazygirlfriend.client.render;

import com.crazygirlfriend.client.ClientRelationshipManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.UUID;

public class LeashFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public LeashFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity leashedPlayer, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        UUID holderUuid = ClientRelationshipManager.get().getLeashHolder(leashedPlayer.getUuid());
        if (holderUuid == null) return;

        PlayerEntity holder = leashedPlayer.getWorld().getPlayerByUuid(holderUuid);
        if (holder == null || holder.isInvisible()) return;

        Vec3d leashedPos = leashedPlayer.getLerpedPos(tickDelta).add(0, 0.9, 0);
        Vec3d holderPos = holder.getLerpedPos(tickDelta).add(0, 0.9, 0);

        matrices.push();
        // Move matrix relative to leashed player position
        double dx = holderPos.x - leashedPos.x;
        double dy = holderPos.y - leashedPos.y;
        double dz = holderPos.z - leashedPos.z;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        int segments = 24;
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            double px = dx * t;
            // Add natural gravity droop curve to the leash
            double py = dy * t + Math.sin(t * Math.PI) * -0.35;
            double pz = dz * t;

            float r = 0.92f, g = 0.20f, b = 0.45f; // Luxurious Pink/Ruby silk rope
            buffer.vertex(posMatrix, (float) px, (float) py, (float) pz).color(r, g, b, 1.0f).light(light).next();
            buffer.vertex(posMatrix, (float) px + 0.025f, (float) py + 0.025f, (float) pz).color(1.0f, 0.85f, 0.2f, 1.0f).light(light).next(); // Gold specks
        }

        matrices.pop();
    }
}

