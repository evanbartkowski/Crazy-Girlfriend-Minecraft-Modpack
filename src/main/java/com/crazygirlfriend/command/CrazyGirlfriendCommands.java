package com.crazygirlfriend.command;

import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.leash.PlayerLeashManager;
import com.crazygirlfriend.makeup.MakeupManager;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.relationship.RequestManager;
import com.crazygirlfriend.relationship.RequestType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CrazyGirlfriendCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("cg")
            .then(literal("accept").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                RequestManager.get().accept(player);
                return 1;
            }))
            .then(literal("decline").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                RequestManager.get().decline(player);
                return 1;
            }))
            .then(literal("status")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    showStatus(ctx.getSource(), player);
                    return 1;
                })
                .then(argument("player", EntityArgumentType.player()).executes(ctx -> {
                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                    showStatus(ctx.getSource(), target);
                    return 1;
                }))
            )
            .then(literal("hug").then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                RequestManager.get().sendRequest(sender, target, RequestType.HUG, null);
                return 1;
            })))
            .then(literal("kiss").then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                RequestManager.get().sendRequest(sender, target, RequestType.KISS, null);
                return 1;
            })))
            .then(literal("slap").then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                RequestManager.get().sendRequest(sender, target, RequestType.SLAP, null);
                return 1;
            })))
            .then(literal("straddle").then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                RequestManager.get().sendRequest(sender, target, RequestType.STRADDLE, null);
                return 1;
            })))
            .then(literal("piggyback").then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                RequestManager.get().sendRequest(sender, target, RequestType.PIGGYBACK, null);
                return 1;
            })))
            .then(literal("makelove").executes(ctx -> {
                ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                Relationship rel = RelationshipManager.get().getRelationship(sender.getUuid(), sender.getServerWorld());
                if (rel == null) {
                    sender.sendMessage(Text.literal("❌ You must be in a relationship to make love!").formatted(Formatting.RED), false);
                    return 0;
                }
                ServerPlayerEntity partner = sender.server.getPlayerManager().getPlayer(rel.getPartner(sender.getUuid()));
                if (partner == null || partner.distanceTo(sender) > 5.0) {
                    sender.sendMessage(Text.literal("❌ Your partner is not close enough!").formatted(Formatting.RED), false);
                    return 0;
                }
                RequestManager.get().sendRequest(sender, partner, RequestType.MAKE_LOVE, null);
                return 1;
            }))
            .then(literal("breakup").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                boolean success = RelationshipManager.get().breakup(player);
                if (!success) {
                    player.sendMessage(Text.literal("❌ You are not currently dating anyone.").formatted(Formatting.RED), false);
                }
                return 1;
            }))
            .then(literal("divorce").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                boolean success = RelationshipManager.get().divorce(player);
                if (!success) {
                    player.sendMessage(Text.literal("❌ You are not currently married.").formatted(Formatting.RED), false);
                }
                return 1;
            }))
            .then(literal("unleash").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                if (PlayerLeashManager.get().isLeashed(player.getUuid())) {
                    PlayerLeashManager.get().releaseLeash(player.getUuid(), player.server);
                } else {
                    player.sendMessage(Text.literal("❌ You are not currently leashed.").formatted(Formatting.GRAY), false);
                }
                return 1;
            }))
            .then(literal("wipeface").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                MakeupManager.get(player.getServerWorld()).clearMakeup(player.getUuid());
                RequestManager.syncMakeup(player.server, player.getUuid(), MakeupManager.get(player.getServerWorld()).getMakeup(player.getUuid()));
                player.sendMessage(Text.literal("🧽 Face makeup washed clean!").formatted(Formatting.AQUA), false);
                return 1;
            }))
            .then(literal("autoaccept")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
                    if (rel != null) {
                        rel.setAutoAccept(!rel.isAutoAccept());
                        player.sendMessage(Text.literal("⚙️ Partner auto-accept is now: " + (rel.isAutoAccept() ? "ON" : "OFF")).formatted(Formatting.LIGHT_PURPLE), false);
                    } else {
                        player.sendMessage(Text.literal("❌ You are not currently in a relationship.").formatted(Formatting.RED), false);
                    }
                    return 1;
                })
                .then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    boolean val = BoolArgumentType.getBool(ctx, "enabled");
                    Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
                    if (rel != null) {
                        rel.setAutoAccept(val);
                        player.sendMessage(Text.literal("⚙️ Partner auto-accept is now: " + (val ? "ON" : "OFF")).formatted(Formatting.LIGHT_PURPLE), false);
                    } else {
                        player.sendMessage(Text.literal("❌ You are not currently in a relationship.").formatted(Formatting.RED), false);
                    }
                    return 1;
                }))
            )
            .then(literal("help").executes(ctx -> {
                showHelp(ctx.getSource());
                return 1;
            }))
        );
    }

    private static void showStatus(ServerCommandSource source, ServerPlayerEntity player) {
        Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
        source.sendMessage(Text.literal("════════════════════════════════════").formatted(Formatting.LIGHT_PURPLE));
        source.sendMessage(Text.literal(" ❤️ Relationship Status: " + player.getName().getString()).formatted(Formatting.GOLD, Formatting.BOLD));
        if (rel == null) {
            source.sendMessage(Text.literal(" • Status: Single").formatted(Formatting.GRAY));
            source.sendMessage(Text.literal(" • Tip: Right-click someone with a Red Rose to ask them out!").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        } else {
            source.sendMessage(Text.literal(" • Status: " + rel.getType().getDisplayName()).formatted(Formatting.LIGHT_PURPLE));
            source.sendMessage(Text.literal(" • Partner: " + rel.getPartnerName(player.getUuid())).formatted(Formatting.AQUA));
            String date = LocalDateTime.ofInstant(Instant.ofEpochMilli(rel.getFormedTimestamp()), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            source.sendMessage(Text.literal(" • Anniversary Since: " + date).formatted(Formatting.YELLOW));
            source.sendMessage(Text.literal(" • Auto-Accept: " + (rel.isAutoAccept() ? "Enabled" : "Disabled")).formatted(Formatting.GREEN));
        }
        source.sendMessage(Text.literal("════════════════════════════════════").formatted(Formatting.LIGHT_PURPLE));
    }

    private static void showHelp(ServerCommandSource source) {
        source.sendMessage(Text.literal("══ Crazy Girlfriend Modpack Commands ══").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        source.sendMessage(Text.literal(" • /cg status [player] - View relationship status").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg hug <player> - Hug a player").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg kiss <player> - Kiss a player").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg slap <player> - Comically slap a player").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg straddle <player> - Straddle interaction").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg piggyback <player> - Ride on player back").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg makelove - Romantic non-explicit embrace").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg breakup - End dating relationship").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg divorce - End marriage").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg unleash - Free yourself from a leash").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg wipeface - Wash off makeup").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg autoaccept [on|off] - Auto-accept partner requests").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • Sneak + Right Click any player to open Interaction GUI!").formatted(Formatting.YELLOW, Formatting.ITALIC));
    }
}

