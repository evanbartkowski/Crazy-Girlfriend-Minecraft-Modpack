package com.crazygirlfriend.client.render;

import com.crazygirlfriend.CrazyGirlfriendMod;
import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.client.animation.ClientAnimationManager;
import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipMood;
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

public class FacialExpressionFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier BLANK_TEXTURE = new Identifier(CrazyGirlfriendMod.MOD_ID, "textures/entity/makeup_blank.png");
    private static final float FACE_Z = -4.07f;

    public FacialExpressionFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (player.isInvisible()) return;

        FacialExpression expr = FacialExpressionManager.get().getExpression(player.getUuid());

        // Contextual fallback expression based on ongoing animations or moods
        if (expr == FacialExpression.NONE) {
            expr = resolveContextualExpression(player);
        }

        if (expr == FacialExpression.NONE) return;

        matrices.push();
        this.getContextModel().getHead().rotate(matrices);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BLANK_TEXTURE));
        MatrixStack.Entry entry = matrices.peek();

        renderExpression(entry, buffer, expr, light);

        matrices.pop();
    }

    private FacialExpression resolveContextualExpression(AbstractClientPlayerEntity player) {
        ClientAnimationManager.ClientAnimState anim = ClientAnimationManager.get().getAnimation(player.getUuid());
        if (anim != null) {
            AnimationType t = anim.type;
            if (t == AnimationType.KISS || t == AnimationType.MAKE_OUT || t == AnimationType.ROMANTIC_DIP_KISS || t == AnimationType.UNDERWATER_KISS) {
                return FacialExpression.CLOSED_EYES;
            }
            if (t == AnimationType.HUG || t == AnimationType.BEAR_HUG || t == AnimationType.SPINNING_HUG || t == AnimationType.JUMPING_HUG || t == AnimationType.ROMANTIC_SWAY_HUG) {
                return FacialExpression.SMILING;
            }
            if (t == AnimationType.SLAP_VICTIM || t == AnimationType.SLAP_DRAMATIC || t == AnimationType.SPINNING_SLAP || t == AnimationType.FISH_SLAP_VICTIM) {
                return FacialExpression.SHOCKED;
            }
            if (t == AnimationType.DIZZY || t == AnimationType.BONK_VICTIM || t == AnimationType.SQUEAK_HAMMER_VICTIM) {
                return FacialExpression.DIZZY_EYES;
            }
            if (t == AnimationType.DRAMATIC_CRYING || t == AnimationType.FALL_DESPAIR) {
                return FacialExpression.EXAGGERATED_CRYING;
            }
            if (t == AnimationType.TANTRUM || t == AnimationType.FOOT_STOMPING || t == AnimationType.ANGRY_JUMPING) {
                return FacialExpression.ANGRY_EYEBROWS;
            }
            if (t == AnimationType.LAUGHING_HARD || t == AnimationType.GIGGLE) {
                return FacialExpression.LAUGHING;
            }
            if (t == AnimationType.SIDE_EYE) {
                return FacialExpression.SUSPICIOUS_SIDE_EYE;
            }
            if (t == AnimationType.POUTING) {
                return FacialExpression.POUTING;
            }
            if (t == AnimationType.BLUSHING || t == AnimationType.SURPRISE_KISS) {
                return FacialExpression.BLUSHING;
            }
            if (t == AnimationType.CUDDLE_SLEEPING || t == AnimationType.SLEEP_TOGETHER || t == AnimationType.SNORING) {
                return FacialExpression.SLEEPY;
            }
        }

        Relationship rel = ClientRelationshipManager.get().getRelationship(player.getUuid());
        if (rel != null) {
            RelationshipMood mood = rel.getMood(player.getUuid());
            if (mood == RelationshipMood.FURIOUS && rel.getCrazyMeter() >= 70) {
                return FacialExpression.ANGRY_EYEBROWS;
            }
            if (mood == RelationshipMood.JEALOUS || mood == RelationshipMood.ANNOYED) {
                return FacialExpression.SUSPICIOUS_SIDE_EYE;
            }
            if (mood == RelationshipMood.LOVING) {
                return FacialExpression.BLUSHING;
            }
            if (mood == RelationshipMood.EMBARRASSED) {
                return FacialExpression.EMBARRASSED;
            }
            if (mood == RelationshipMood.SLEEPY) {
                return FacialExpression.SLEEPY;
            }
        }

        return FacialExpression.NONE;
    }

    private void renderExpression(MatrixStack.Entry entry, VertexConsumer buffer, FacialExpression expr, int light) {
        switch (expr) {
            case BLUSHING -> {
                // Rosy blush patches on both cheeks with cute hatch marks
                drawQuad(entry, buffer, -3.5f, -3.2f, -1.2f, -1.8f, FACE_Z, 1.0f, 0.45f, 0.60f, 0.70f, light);
                drawQuad(entry, buffer, 1.2f, -3.2f, 3.5f, -1.8f, FACE_Z, 1.0f, 0.45f, 0.60f, 0.70f, light);
                // Subtle smile
                drawQuad(entry, buffer, -1.0f, -1.5f, 1.0f, -0.8f, FACE_Z, 0.8f, 0.2f, 0.3f, 0.85f, light);
            }
            case SMILING -> {
                // Cheerful open smile
                drawQuad(entry, buffer, -1.5f, -1.6f, 1.5f, -0.7f, FACE_Z, 0.9f, 0.2f, 0.3f, 0.90f, light);
                drawQuad(entry, buffer, -1.0f, -1.3f, 1.0f, -0.9f, FACE_Z, 1.0f, 1.0f, 1.0f, 0.95f, light); // teeth
                // Soft cheek pink
                drawQuad(entry, buffer, -3.2f, -3.0f, -1.8f, -2.0f, FACE_Z, 1.0f, 0.55f, 0.65f, 0.45f, light);
                drawQuad(entry, buffer, 1.8f, -3.0f, 3.2f, -2.0f, FACE_Z, 1.0f, 0.55f, 0.65f, 0.45f, light);
            }
            case CLOSED_EYES -> {
                // Curved closed eyelashes: ⌒ ⌒
                drawQuad(entry, buffer, -3.2f, -4.5f, -1.5f, -4.0f, FACE_Z, 0.15f, 0.15f, 0.15f, 0.95f, light);
                drawQuad(entry, buffer, 1.5f, -4.5f, 3.2f, -4.0f, FACE_Z, 0.15f, 0.15f, 0.15f, 0.95f, light);
                // Rosy sweet cheeks
                drawQuad(entry, buffer, -3.2f, -3.0f, -1.5f, -2.0f, FACE_Z, 1.0f, 0.4f, 0.55f, 0.65f, light);
                drawQuad(entry, buffer, 1.5f, -3.0f, 3.2f, -2.0f, FACE_Z, 1.0f, 0.4f, 0.55f, 0.65f, light);
            }
            case WINKING -> {
                // Right eye closed winking
                drawQuad(entry, buffer, -3.2f, -4.5f, -1.5f, -4.0f, FACE_Z, 0.15f, 0.15f, 0.15f, 0.95f, light);
                // Cheeky smile
                drawQuad(entry, buffer, -0.8f, -1.5f, 1.5f, -0.8f, FACE_Z, 0.85f, 0.2f, 0.35f, 0.90f, light);
                // Blush
                drawQuad(entry, buffer, -3.0f, -3.0f, -1.8f, -2.0f, FACE_Z, 1.0f, 0.5f, 0.6f, 0.55f, light);
            }
            case ANGRY_EYEBROWS -> {
                // Slanted furrowed eyebrows: \ /
                drawQuad(entry, buffer, -3.5f, -5.8f, -1.2f, -5.1f, FACE_Z, 0.2f, 0.05f, 0.05f, 0.95f, light);
                drawQuad(entry, buffer, 1.2f, -5.8f, 3.5f, -5.1f, FACE_Z, 0.2f, 0.05f, 0.05f, 0.95f, light);
                // Gritted teeth
                drawQuad(entry, buffer, -1.5f, -1.5f, 1.5f, -0.8f, FACE_Z, 0.95f, 0.95f, 0.95f, 0.90f, light);
                drawQuad(entry, buffer, -1.5f, -1.2f, 1.5f, -1.1f, FACE_Z, 0.2f, 0.2f, 0.2f, 0.90f, light); // teeth line
            }
            case SHOCKED -> {
                // Wide shocked pupils
                drawQuad(entry, buffer, -3.0f, -4.8f, -1.8f, -3.5f, FACE_Z, 0.1f, 0.1f, 0.1f, 0.95f, light);
                drawQuad(entry, buffer, 1.8f, -4.8f, 3.0f, -3.5f, FACE_Z, 0.1f, 0.1f, 0.1f, 0.95f, light);
                // Small round 'O' mouth
                drawQuad(entry, buffer, -0.7f, -1.8f, 0.7f, -0.6f, FACE_Z, 0.2f, 0.1f, 0.1f, 0.95f, light);
            }
            case EMBARRASSED -> {
                // Deep red blush across bridge of nose and cheeks
                drawQuad(entry, buffer, -3.5f, -3.5f, 3.5f, -1.8f, FACE_Z, 1.0f, 0.25f, 0.40f, 0.75f, light);
                // Small squiggly mouth
                drawQuad(entry, buffer, -1.0f, -1.3f, 1.0f, -0.8f, FACE_Z, 0.4f, 0.1f, 0.15f, 0.85f, light);
            }
            case CARTOON_TEARS -> {
                // Anime waterfall tears streaming down cheeks
                drawQuad(entry, buffer, -3.0f, -4.0f, -1.8f, -0.5f, FACE_Z, 0.35f, 0.75f, 1.0f, 0.85f, light);
                drawQuad(entry, buffer, 1.8f, -4.0f, 3.0f, -0.5f, FACE_Z, 0.35f, 0.75f, 1.0f, 0.85f, light);
                // Trembling sad mouth
                drawQuad(entry, buffer, -1.2f, -1.2f, 1.2f, -0.6f, FACE_Z, 0.4f, 0.1f, 0.15f, 0.85f, light);
            }
            case SAD -> {
                // Downturned sad eyebrows
                drawQuad(entry, buffer, -3.2f, -5.2f, -1.5f, -5.7f, FACE_Z, 0.25f, 0.2f, 0.2f, 0.85f, light);
                drawQuad(entry, buffer, 1.5f, -5.7f, 3.2f, -5.2f, FACE_Z, 0.25f, 0.2f, 0.2f, 0.85f, light);
                // Pouty downturned mouth
                drawQuad(entry, buffer, -1.2f, -1.1f, 1.2f, -0.7f, FACE_Z, 0.45f, 0.15f, 0.2f, 0.85f, light);
            }
            case POUTING -> {
                // Puffed cheeks
                drawQuad(entry, buffer, -3.2f, -2.8f, -1.8f, -1.6f, FACE_Z, 1.0f, 0.5f, 0.65f, 0.65f, light);
                drawQuad(entry, buffer, 1.8f, -2.8f, 3.2f, -1.6f, FACE_Z, 1.0f, 0.5f, 0.65f, 0.65f, light);
                // Small pouty mouth
                drawQuad(entry, buffer, -0.6f, -1.4f, 0.6f, -0.7f, FACE_Z, 0.75f, 0.2f, 0.35f, 0.90f, light);
            }
            case SUSPICIOUS_SIDE_EYE -> {
                // Pupils shifted far to the left
                drawQuad(entry, buffer, -3.2f, -4.5f, -2.2f, -3.5f, FACE_Z, 0.15f, 0.15f, 0.15f, 0.95f, light);
                drawQuad(entry, buffer, 1.5f, -4.5f, 2.5f, -3.5f, FACE_Z, 0.15f, 0.15f, 0.15f, 0.95f, light);
                // One raised skeptical eyebrow
                drawQuad(entry, buffer, 1.2f, -6.0f, 3.2f, -5.4f, FACE_Z, 0.2f, 0.2f, 0.2f, 0.90f, light);
                // Straight suspicious mouth line
                drawQuad(entry, buffer, -1.2f, -1.3f, 1.2f, -1.0f, FACE_Z, 0.3f, 0.2f, 0.2f, 0.85f, light);
            }
            case DIZZY_EYES -> {
                // Cartoon dizzy spiral / '@' eyes
                drawQuad(entry, buffer, -3.2f, -4.6f, -1.8f, -3.4f, FACE_Z, 0.9f, 0.85f, 0.2f, 0.90f, light);
                drawQuad(entry, buffer, 1.8f, -4.6f, 3.2f, -3.4f, FACE_Z, 0.9f, 0.85f, 0.2f, 0.90f, light);
                drawQuad(entry, buffer, -2.8f, -4.3f, -2.2f, -3.7f, FACE_Z, 0.2f, 0.15f, 0.1f, 0.95f, light);
                drawQuad(entry, buffer, 2.2f, -4.3f, 2.8f, -3.7f, FACE_Z, 0.2f, 0.15f, 0.1f, 0.95f, light);
                // Wavy mouth
                drawQuad(entry, buffer, -1.2f, -1.5f, 1.2f, -0.9f, FACE_Z, 0.4f, 0.2f, 0.2f, 0.85f, light);
            }
            case SLEEPY -> {
                // Drooping sleepy eyes
                drawQuad(entry, buffer, -3.2f, -4.2f, -1.8f, -3.8f, FACE_Z, 0.25f, 0.25f, 0.30f, 0.85f, light);
                drawQuad(entry, buffer, 1.8f, -4.2f, 3.2f, -3.8f, FACE_Z, 0.25f, 0.25f, 0.30f, 0.85f, light);
                // Small relaxed mouth
                drawQuad(entry, buffer, -0.8f, -1.2f, 0.8f, -0.8f, FACE_Z, 0.5f, 0.3f, 0.35f, 0.80f, light);
            }
            case LAUGHING -> {
                // Squeezed shut happy eyes: > <
                drawQuad(entry, buffer, -3.2f, -4.5f, -1.8f, -4.0f, FACE_Z, 0.2f, 0.2f, 0.2f, 0.90f, light);
                drawQuad(entry, buffer, 1.8f, -4.5f, 3.2f, -4.0f, FACE_Z, 0.2f, 0.2f, 0.2f, 0.90f, light);
                // Wide open joyful laugh mouth
                drawQuad(entry, buffer, -1.8f, -1.8f, 1.8f, -0.5f, FACE_Z, 0.85f, 0.15f, 0.25f, 0.95f, light);
                drawQuad(entry, buffer, -1.2f, -1.5f, 1.2f, -1.1f, FACE_Z, 1.0f, 1.0f, 1.0f, 0.95f, light); // teeth
            }
            case EXAGGERATED_CRYING -> {
                // Massive fountain tears streaming outward
                drawQuad(entry, buffer, -3.8f, -4.5f, -1.5f, 0.5f, FACE_Z, 0.3f, 0.7f, 1.0f, 0.90f, light);
                drawQuad(entry, buffer, 1.5f, -4.5f, 3.8f, 0.5f, FACE_Z, 0.3f, 0.7f, 1.0f, 0.90f, light);
                // Wide wailing mouth
                drawQuad(entry, buffer, -2.0f, -2.2f, 2.0f, -0.4f, FACE_Z, 0.2f, 0.05f, 0.05f, 0.95f, light);
            }
            default -> {}
        }
    }

    private void drawQuad(MatrixStack.Entry entry, VertexConsumer buffer, float x1, float y1, float x2, float y2, float z, float r, float g, float b, float a, int light) {
        Matrix4f pos = entry.getPositionMatrix();
        buffer.vertex(pos, x1, y2, z).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, -1).next();
        buffer.vertex(pos, x2, y2, z).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, -1).next();
        buffer.vertex(pos, x2, y1, z).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, -1).next();
        buffer.vertex(pos, x1, y1, z).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, -1).next();
    }
}
