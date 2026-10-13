package com.crazygirlfriend.client.render;

import com.crazygirlfriend.CrazyGirlfriendMod;
import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class MakeupFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier BLANK_TEXTURE = new Identifier(CrazyGirlfriendMod.MOD_ID, "textures/entity/makeup_blank.png");
    private static final float FACE_Z = -4.06f;

    public MakeupFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (player.isInvisible()) return;

        PlayerMakeupData data = ClientRelationshipManager.get().getMakeup(player.getUuid());
        if (data == null || data.isEmpty()) return;

        matrices.push();
        this.getContextModel().getHead().rotate(matrices);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BLANK_TEXTURE));
        MatrixStack.Entry entry = matrices.peek();

        // 1. Lipstick
        if (data.getLipstickStyle() > 0) {
            float r = 0.83f, g = 0.12f, b = 0.18f, a = 0.95f; // Ruby red
            if (data.getLipstickStyle() == 2) { r = 1.0f; g = 0.45f; b = 0.65f; } // Soft pink
            else if (data.getLipstickStyle() == 3) { r = 0.12f; g = 0.12f; b = 0.12f; } // Black
            else if (data.getLipstickStyle() == 4) { r = 0.48f; g = 0.12f; b = 0.64f; } // Berry purple

            drawQuad(entry, buffer, -1.0f, -1.5f, 1.0f, -0.5f, FACE_Z, r, g, b, a, light);
        }

        // 2. Blush
        if (data.getBlushStyle() > 0) {
            float r = 1.0f, g = 0.55f, b = 0.65f, a = 0.65f; // Rosy
            if (data.getBlushStyle() == 2) { r = 1.0f; g = 0.68f; b = 0.50f; a = 0.60f; } // Anime peach
            else if (data.getBlushStyle() == 3) { r = 1.0f; g = 0.20f; b = 0.30f; a = 0.75f; } // Doll red

            // Left & Right cheeks
            drawQuad(entry, buffer, -3.5f, -3.2f, -1.5f, -1.8f, FACE_Z, r, g, b, a, light);
            drawQuad(entry, buffer, 1.5f, -3.2f, 3.5f, -1.8f, FACE_Z, r, g, b, a, light);
        }

        // 3. Eyeliner
        if (data.getEyelinerStyle() > 0) {
            float r = 0.08f, g = 0.08f, b = 0.08f, a = 0.95f;
            // Eyeliner upper line & winged tips
            drawQuad(entry, buffer, -3.5f, -4.5f, -1.0f, -4.1f, FACE_Z, r, g, b, a, light);
            drawQuad(entry, buffer, 1.0f, -4.5f, 3.5f, -4.1f, FACE_Z, r, g, b, a, light);
            if (data.getEyelinerStyle() == 1) { // Winged cat eye tips
                drawQuad(entry, buffer, -4.0f, -4.8f, -3.5f, -4.3f, FACE_Z, r, g, b, a, light);
                drawQuad(entry, buffer, 3.5f, -4.8f, 4.0f, -4.3f, FACE_Z, r, g, b, a, light);
            }
        }

        // 4. Face Markings
        if (data.getFaceMarking() > 0) {
            switch (data.getFaceMarking()) {
                case 1 -> { // Cat Whiskers
                    float r = 0.1f, g = 0.1f, b = 0.1f, a = 0.9f;
                    drawQuad(entry, buffer, -3.8f, -2.8f, -1.8f, -2.6f, FACE_Z, r, g, b, a, light);
                    drawQuad(entry, buffer, -3.8f, -2.2f, -1.8f, -2.0f, FACE_Z, r, g, b, a, light);
                    drawQuad(entry, buffer, 1.8f, -2.8f, 3.8f, -2.6f, FACE_Z, r, g, b, a, light);
                    drawQuad(entry, buffer, 1.8f, -2.2f, 3.8f, -2.0f, FACE_Z, r, g, b, a, light);
                }
                case 2 -> { // Cheek Hearts
                    drawQuad(entry, buffer, -3.0f, -2.8f, -2.0f, -1.8f, FACE_Z, 1.0f, 0.2f, 0.4f, 0.9f, light);
                    drawQuad(entry, buffer, 2.0f, -2.8f, 3.0f, -1.8f, FACE_Z, 1.0f, 0.2f, 0.4f, 0.9f, light);
                }
                case 3 -> { // Clown Nose
                    drawQuad(entry, buffer, -0.8f, -2.8f, 0.8f, -1.4f, FACE_Z, 0.95f, 0.1f, 0.1f, 0.95f, light);
                }
                case 4 -> { // Cute Star
                    drawQuad(entry, buffer, 1.8f, -3.0f, 3.2f, -1.6f, FACE_Z, 1.0f, 0.85f, 0.1f, 0.95f, light);
                }
                case 5 -> { // French Mustache
                    drawQuad(entry, buffer, -2.5f, -1.8f, 2.5f, -1.4f, FACE_Z, 0.2f, 0.12f, 0.05f, 0.95f, light);
                }
            }
        }

        matrices.pop();
    }

    private static void drawQuad(MatrixStack.Entry entry, VertexConsumer buffer, float minX, float minY, float maxX, float maxY, float z, float r, float g, float b, float a, int light) {
        Matrix4f pos = entry.getPositionMatrix();
        buffer.vertex(pos, minX, minY, z).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry.getNormalMatrix(), 0, 0, -1).next();
        buffer.vertex(pos, maxX, minY, z).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry.getNormalMatrix(), 0, 0, -1).next();
        buffer.vertex(pos, maxX, maxY, z).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry.getNormalMatrix(), 0, 0, -1).next();
        buffer.vertex(pos, minX, maxY, z).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry.getNormalMatrix(), 0, 0, -1).next();
    }
}

