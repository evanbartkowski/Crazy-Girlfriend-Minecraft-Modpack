package com.crazygirlfriend.client;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.client.animation.ClientAnimationManager;
import com.crazygirlfriend.client.gui.MakeupScreen;
import com.crazygirlfriend.client.gui.RelationshipInteractionScreen;
import com.crazygirlfriend.client.render.SlipperEntityRenderer;
import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class CrazyGirlfriendClient implements ClientModInitializer {
    public static KeyBinding INTERACTION_MENU_KEY;

    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRendererRegistry.register(ModEntities.SLIPPER, SlipperEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOOMERANG_SLIPPER, net.minecraft.client.render.entity.FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.WATER_BALLOON, net.minecraft.client.render.entity.FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SEAT, com.crazygirlfriend.client.render.EmptySeatRenderer::new);

        // Register block render layers
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlocks(
            net.minecraft.client.render.RenderLayer.getCutout(),
            com.crazygirlfriend.block.ModBlocks.COUPLE_PICNIC_BLANKET,
            com.crazygirlfriend.block.ModBlocks.COUPLE_SOFA,
            com.crazygirlfriend.block.ModBlocks.COUPLE_DOUBLE_BED,
            com.crazygirlfriend.block.ModBlocks.COUPLE_SWING,
            com.crazygirlfriend.block.ModBlocks.TEDDY_BEAR,
            com.crazygirlfriend.block.ModBlocks.COZY_FIREPLACE,
            com.crazygirlfriend.block.ModBlocks.ROMANTIC_CHANDELIER,
            com.crazygirlfriend.block.ModBlocks.ROMANTIC_DINING_TABLE
        );

        // Register keybindings
        INTERACTION_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.crazygirlfriend.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.crazygirlfriend"
        ));

        // Register S2C packet receivers
        registerPacketReceivers();

        // Register client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientAnimationManager.get().tick();
            ClientRelationshipManager.get().tick();
            com.crazygirlfriend.facial.FacialExpressionManager.get().tick();
            com.crazygirlfriend.client.gui.ComboOverlayHud.get().tick(client);
            com.crazygirlfriend.client.camera.CinematicCameraManager.get().tick();

            // Atmospheric & Environmental Systems
            if (client.world != null) {
                com.crazygirlfriend.client.atmosphere.WindManager.get().tick(client.world);
                com.crazygirlfriend.client.atmosphere.AtmosphericParticleManager.get().tick(client);
                com.crazygirlfriend.client.atmosphere.SkyOverhaulRenderer.get().tick(client);
                com.crazygirlfriend.client.atmosphere.AmbientSoundManager.get().tick(client);
            }

            while (INTERACTION_MENU_KEY.wasPressed()) {
                if (client.targetedEntity instanceof PlayerEntity targetPlayer) {
                    client.setScreen(new RelationshipInteractionScreen(targetPlayer));
                }
            }
        });

        // Sneak + Right click to open interaction GUI, or holding makeup kit
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && hand == Hand.MAIN_HAND && entity instanceof PlayerEntity targetPlayer) {
                if (player.getMainHandStack().isOf(ModItems.MAKEUP_KIT)) {
                    MinecraftClient.getInstance().setScreen(new MakeupScreen(targetPlayer));
                    return ActionResult.SUCCESS;
                }
                if (player.isSneaking()) {
                    MinecraftClient.getInstance().setScreen(new RelationshipInteractionScreen(targetPlayer));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    private void registerPacketReceivers() {
        // Relationship Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.RELATIONSHIP_SYNC_ID, (client, handler, buf, responseSender) -> {
            int count = buf.readInt();
            ClientRelationshipManager.get().clearRelationships();
            for (int i = 0; i < count; i++) {
                UUID p1 = buf.readUuid();
                String n1 = buf.readString();
                UUID p2 = buf.readUuid();
                String n2 = buf.readString();
                RelationshipType type = RelationshipType.valueOf(buf.readString());
                long timestamp = buf.readLong();
                boolean autoAccept = buf.readBoolean();
                int crazyMeter = buf.readInt();
                int affectionXp = buf.readInt();
                String m1 = buf.readString();
                String m2 = buf.readString();
                String title = buf.readString();

                Relationship rel = new Relationship(p1, n1, p2, n2, type, timestamp);
                rel.setAutoAccept(autoAccept);
                rel.setCrazyMeter(crazyMeter);
                rel.setAffectionXp(affectionXp);
                try { rel.setMood(p1, com.crazygirlfriend.relationship.RelationshipMood.valueOf(m1)); } catch (Exception ignored) {}
                try { rel.setMood(p2, com.crazygirlfriend.relationship.RelationshipMood.valueOf(m2)); } catch (Exception ignored) {}
                rel.setCustomTitle(title);
                ClientRelationshipManager.get().addRelationship(rel);
            }
        });

        // Animation Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.ANIMATION_SYNC_ID, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            boolean hasPartner = buf.readBoolean();
            UUID partnerUuid = hasPartner ? buf.readUuid() : null;
            int typeOrdinal = buf.readVarInt();
            int duration = buf.readVarInt();

            AnimationType type = AnimationType.values()[typeOrdinal];
            client.execute(() -> {
                ClientAnimationManager.get().setAnimation(playerUuid, partnerUuid, type, duration);
            });
        });

        // Leash Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LEASH_SYNC_ID, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            client.execute(() -> {
                ClientRelationshipManager.get().clearLeashes();
                for (int i = 0; i < size; i++) {
                    UUID leashed = buf.readUuid();
                    UUID holder = buf.readUuid();
                    boolean sitting = buf.readBoolean();
                    ClientRelationshipManager.get().setLeash(leashed, holder, sitting);
                }
            });
        });

        // Makeup Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.MAKEUP_SYNC_ID, (client, handler, buf, responseSender) -> {
            PlayerMakeupData makeupData = PlayerMakeupData.readFromBuf(buf);
            client.execute(() -> {
                ClientRelationshipManager.get().setMakeup(makeupData.getPlayerUuid(), makeupData);
            });
        });

        // Fade Vignette
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.FADE_VIGNETTE_ID, (client, handler, buf, responseSender) -> {
            int duration = buf.readInt();
            client.execute(() -> {
                ClientRelationshipManager.get().triggerFadeVignette(duration);
            });
        });

        // Facial Expression Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.FACIAL_EXPRESSION_SYNC_ID, (client, handler, buf, responseSender) -> {
            UUID targetUuid = buf.readUuid();
            int exprOrdinal = buf.readVarInt();
            int duration = buf.readVarInt();
            com.crazygirlfriend.facial.FacialExpression expr = com.crazygirlfriend.facial.FacialExpression.values()[exprOrdinal];
            client.execute(() -> {
                com.crazygirlfriend.facial.FacialExpressionManager.get().setExpression(targetUuid, expr, duration);
            });
        });

        // Love Euphoria Pink Screen Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LOVE_EUPHORIA_SYNC_ID, (client, handler, buf, responseSender) -> {
            int duration = buf.readInt();
            client.execute(() -> {
                ClientRelationshipManager.get().triggerLoveEuphoria(duration);
            });
        });

        // Season Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SEASON_SYNC_ID, (client, handler, buf, responseSender) -> {
            String seasonName = buf.readString();
            float progress = buf.readFloat();
            int day = buf.readInt();
            int totalDays = buf.readInt();
            boolean locked = buf.readBoolean();
            com.crazygirlfriend.season.Season season = com.crazygirlfriend.season.Season.fromString(seasonName);
            client.execute(() -> {
                com.crazygirlfriend.season.SeasonManager.get().updateClientState(season, progress, day, totalDays, locked);
            });
        });

        // World Event Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.WORLD_EVENT_SYNC_ID, (client, handler, buf, responseSender) -> {
            String eventName = buf.readString();
            int ticks = buf.readInt();
            float intensity = buf.readFloat();
            com.crazygirlfriend.event.world.WorldEventType event = com.crazygirlfriend.event.world.WorldEventType.fromString(eventName);
            client.execute(() -> {
                com.crazygirlfriend.event.world.WorldEventManager.get().updateClientEvent(event, ticks, intensity);
            });
        });

        // Sleep Side Flip Sync
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SLEEP_SIDE_FLIP_ID, (client, handler, buf, responseSender) -> {
            UUID targetUuid = buf.readUuid();
            client.execute(() -> {
                com.crazygirlfriend.client.bed.CoSleepingClientTracker.get().toggleFlip(targetUuid);
            });
        });
    }
}

