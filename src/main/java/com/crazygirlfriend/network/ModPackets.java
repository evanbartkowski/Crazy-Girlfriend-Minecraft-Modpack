package com.crazygirlfriend.network;

import com.crazygirlfriend.CrazyGirlfriendMod;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.leash.PlayerLeashManager;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.relationship.RequestManager;
import com.crazygirlfriend.relationship.RequestType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModPackets {
    public static final Identifier RELATIONSHIP_SYNC_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "relationship_sync");
    public static final Identifier ANIMATION_SYNC_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "animation_sync");
    public static final Identifier LEASH_SYNC_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "leash_sync");
    public static final Identifier MAKEUP_SYNC_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "makeup_sync");
    public static final Identifier FADE_VIGNETTE_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "fade_vignette");

    public static final Identifier INTERACTION_REQUEST_C2S = new Identifier(CrazyGirlfriendMod.MOD_ID, "interaction_request");
    public static final Identifier APPLY_MAKEUP_C2S = new Identifier(CrazyGirlfriendMod.MOD_ID, "apply_makeup");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(INTERACTION_REQUEST_C2S, (server, player, handler, buf, responseSender) -> {
            UUID targetUuid = buf.readUuid();
            String actionName = buf.readString();

            server.execute(() -> {
                ServerPlayerEntity target = server.getPlayerManager().getPlayer(targetUuid);
                if (target == null || target.isRemoved() || target.equals(player)) {
                    player.sendMessage(Text.literal("❌ Target player is not available.").formatted(Formatting.RED), false);
                    return;
                }

                if (player.distanceTo(target) > 8.0) {
                    player.sendMessage(Text.literal("❌ Target player is too far away.").formatted(Formatting.RED), false);
                    return;
                }

                handleInteraction(player, target, actionName);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(APPLY_MAKEUP_C2S, (server, player, handler, buf, responseSender) -> {
            PlayerMakeupData makeupData = PlayerMakeupData.readFromBuf(buf);

            server.execute(() -> {
                ServerPlayerEntity target = server.getPlayerManager().getPlayer(makeupData.getPlayerUuid());
                if (target == null || target.isRemoved()) return;

                if (player.distanceTo(target) > 6.0) {
                    player.sendMessage(Text.literal("❌ Target is too far away to apply makeup.").formatted(Formatting.RED), false);
                    return;
                }

                // Send request with makeup payload
                RequestManager.get().sendRequest(player, target, RequestType.APPLY_MAKEUP, makeupData);
            });
        });
    }

    private static void handleInteraction(ServerPlayerEntity sender, ServerPlayerEntity target, String actionName) {
        switch (actionName.toLowerCase()) {
            case "rose" -> {
                if (sender.getInventory().contains(new ItemStack(ModItems.RED_ROSE))) {
                    RequestManager.get().sendRequest(sender, target, RequestType.PROPOSAL_DATING, null);
                } else {
                    sender.sendMessage(Text.literal("❌ You must have a Red Romance Rose!").formatted(Formatting.RED), false);
                }
            }
            case "marry" -> {
                if (sender.getInventory().contains(new ItemStack(ModItems.DIAMOND_RING))) {
                    RequestManager.get().sendRequest(sender, target, RequestType.PROPOSAL_MARRIAGE, null);
                } else {
                    sender.sendMessage(Text.literal("❌ You must have a Diamond Engagement Ring!").formatted(Formatting.RED), false);
                }
            }
            case "hug" -> RequestManager.get().sendRequest(sender, target, RequestType.HUG, null);
            case "kiss" -> RequestManager.get().sendRequest(sender, target, RequestType.KISS, null);
            case "slap" -> RequestManager.get().sendRequest(sender, target, RequestType.SLAP, null);
            case "straddle" -> RequestManager.get().sendRequest(sender, target, RequestType.STRADDLE, null);
            case "piggyback" -> RequestManager.get().sendRequest(sender, target, RequestType.PIGGYBACK, null);
            case "makelove" -> {
                Relationship rel = RelationshipManager.get().getRelationship(sender.getUuid(), sender.getServerWorld());
                if (rel != null && rel.involves(target.getUuid())) {
                    RequestManager.get().sendRequest(sender, target, RequestType.MAKE_LOVE, null);
                } else {
                    sender.sendMessage(Text.literal("❌ You must be in a romantic relationship to do that!").formatted(Formatting.RED), false);
                }
            }
            case "bouquet" -> {
                ItemStack bouquet = findBouquet(sender);
                if (!bouquet.isEmpty()) {
                    RequestManager.get().sendRequest(sender, target, RequestType.GIFT_BOUQUET, bouquet);
                } else {
                    sender.sendMessage(Text.literal("❌ You need a Flower Bouquet to gift!").formatted(Formatting.RED), false);
                }
            }
            case "paddle" -> {
                if (sender.getInventory().contains(new ItemStack(ModItems.PADDLE))) {
                    RequestManager.get().sendRequest(sender, target, RequestType.PADDLE_SPANK, null);
                } else {
                    sender.sendMessage(Text.literal("❌ You need a Disciplinary Paddle!").formatted(Formatting.RED), false);
                }
            }
            case "leash" -> {
                if (PlayerLeashManager.get().isLeashed(target.getUuid())) {
                    PlayerLeashManager.get().toggleSit(sender, target);
                } else if (sender.getInventory().contains(new ItemStack(ModItems.PLAYER_LEASH))) {
                    RequestManager.get().sendRequest(sender, target, RequestType.LEASH, null);
                } else {
                    sender.sendMessage(Text.literal("❌ You need a Player Leash!").formatted(Formatting.RED), false);
                }
            }
            case "breakup" -> RelationshipManager.get().breakup(sender);
            case "divorce" -> RelationshipManager.get().divorce(sender);
            default -> {
                if (actionName.startsWith("minigame:")) {
                    String gameId = actionName.substring("minigame:".length());
                    RequestManager.get().sendRequest(sender, target, RequestType.MINIGAME_INVITE, gameId);
                    return;
                }
                for (com.crazygirlfriend.animation.AnimationType type : com.crazygirlfriend.animation.AnimationType.values()) {
                    if (type.name().equalsIgnoreCase(actionName)) {
                        RequestManager.get().sendRequest(sender, target, RequestType.GENERIC_ANIMATION, type);
                        return;
                    }
                }
                sender.sendMessage(Text.literal("❌ Unknown interaction: " + actionName).formatted(Formatting.RED), false);
            }
        }
    }

    private static ItemStack findBouquet(ServerPlayerEntity player) {
        if (player.getMainHandStack().isOf(ModItems.FLOWER_BOUQUET) || player.getMainHandStack().isOf(ModItems.PASTEL_BOUQUET) || player.getMainHandStack().isOf(ModItems.SUN_BOUQUET)) {
            return player.getMainHandStack();
        }
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.FLOWER_BOUQUET) || stack.isOf(ModItems.PASTEL_BOUQUET) || stack.isOf(ModItems.SUN_BOUQUET)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}

