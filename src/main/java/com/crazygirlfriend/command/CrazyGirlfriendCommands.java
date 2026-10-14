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
import java.util.UUID;

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
            .then(literal("cancel").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                com.crazygirlfriend.animation.PlayerAnimationTracker.get().stopAnimation(player.getUuid(), player.server);
                if (player.hasPassengers()) player.removeAllPassengers();
                if (player.getVehicle() != null) player.stopRiding();
                player.sendMessage(Text.literal("🛑 Interaction / carry cancelled.").formatted(Formatting.YELLOW), false);
                return 1;
            }))
            .then(literal("expression")
                .then(argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String exprName = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");
                        try {
                            com.crazygirlfriend.facial.FacialExpression expr = com.crazygirlfriend.facial.FacialExpression.valueOf(exprName.toUpperCase());
                            com.crazygirlfriend.facial.FacialExpressionManager.get().setAndSyncExpression(player, expr, 100);
                            player.sendMessage(Text.literal("😊 Facial expression set to: " + expr.getDisplayName()).formatted(Formatting.LIGHT_PURPLE), false);
                        } catch (Exception e) {
                            player.sendMessage(Text.literal("❌ Unknown expression. Try: BLUSHING, SMILING, WINKING, ANGRY_EYEBROWS, SHOCKED, POUTING, DIZZY_EYES, LAUGHING...").formatted(Formatting.RED), false);
                        }
                        return 1;
                    })
                )
            )
            .then(literal("anim")
                .then(argument("animation", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(ctx -> {
                        ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                        String animName = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "animation");
                        com.crazygirlfriend.relationship.Relationship rel = RelationshipManager.get().getRelationship(sender.getUuid(), sender.getServerWorld());
                        if (rel == null) {
                            sender.sendMessage(Text.literal("❌ You must have a partner to use this interaction!").formatted(Formatting.RED), false);
                            return 0;
                        }
                        ServerPlayerEntity partner = sender.server.getPlayerManager().getPlayer(rel.getPartner(sender.getUuid()));
                        if (partner == null || partner.distanceTo(sender) > 8.0) {
                            sender.sendMessage(Text.literal("❌ Partner must be within 8 blocks!").formatted(Formatting.RED), false);
                            return 0;
                        }
                        for (com.crazygirlfriend.animation.AnimationType type : com.crazygirlfriend.animation.AnimationType.values()) {
                            if (type.name().equalsIgnoreCase(animName)) {
                                RequestManager.get().sendRequest(sender, partner, RequestType.GENERIC_ANIMATION, type);
                                return 1;
                            }
                        }
                        sender.sendMessage(Text.literal("❌ Unknown animation: " + animName).formatted(Formatting.RED), false);
                        return 0;
                    })
                    .then(argument("target", EntityArgumentType.player()).executes(ctx -> {
                        ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                        String animName = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "animation");
                        for (com.crazygirlfriend.animation.AnimationType type : com.crazygirlfriend.animation.AnimationType.values()) {
                            if (type.name().equalsIgnoreCase(animName)) {
                                RequestManager.get().sendRequest(sender, target, RequestType.GENERIC_ANIMATION, type);
                                return 1;
                            }
                        }
                        sender.sendMessage(Text.literal("❌ Unknown animation: " + animName).formatted(Formatting.RED), false);
                        return 0;
                    }))
                )
            )
            .then(literal("mood").then(argument("mood", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                String moodStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "mood");
                com.crazygirlfriend.relationship.Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), player.getServerWorld());
                if (rel == null) {
                    player.sendMessage(Text.literal("❌ You must be in a relationship to set your mood!").formatted(Formatting.RED), false);
                    return 0;
                }
                for (com.crazygirlfriend.relationship.RelationshipMood m : com.crazygirlfriend.relationship.RelationshipMood.values()) {
                    if (m.name().equalsIgnoreCase(moodStr)) {
                        rel.setMood(player.getUuid(), m);
                        RelationshipManager.get().save(player.getServerWorld());
                        RelationshipManager.get().syncToAll(player.server);
                        player.sendMessage(Text.literal("✨ Mood updated to: " + m.getDisplayName()).formatted(Formatting.GREEN), false);
                        return 1;
                    }
                }
                player.sendMessage(Text.literal("❌ Invalid mood! Available: HAPPY, LOVING, NEEDY, SPICY, POUTY, ANGRY, CHAOTIC, SLEEPY, JEALOUS, PLAYFUL").formatted(Formatting.RED), false);
                return 0;
            })))
            .then(literal("minigame")
                .then(argument("game", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(ctx -> {
                    ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                    String gameId = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "game");
                    com.crazygirlfriend.relationship.Relationship rel = RelationshipManager.get().getRelationship(sender.getUuid(), sender.getServerWorld());
                    if (rel == null) {
                        sender.sendMessage(Text.literal("❌ You must have a partner to start a minigame!").formatted(Formatting.RED), false);
                        return 0;
                    }
                    ServerPlayerEntity partner = sender.server.getPlayerManager().getPlayer(rel.getPartner(sender.getUuid()));
                    if (partner == null || partner.distanceTo(sender) > 12.0) {
                        sender.sendMessage(Text.literal("❌ Partner must be within 12 blocks!").formatted(Formatting.RED), false);
                        return 0;
                    }
                    RequestManager.get().sendRequest(sender, partner, RequestType.MINIGAME_INVITE, gameId);
                    return 1;
                }))
            )
            .then(literal("minigame_ans")
                .then(argument("game", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .then(argument("answer", com.mojang.brigadier.arguments.StringArgumentType.greedyString()).executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String gameId = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "game");
                        String ans = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "answer");
                        com.crazygirlfriend.event.MinigameManager.get().handleAnswer(player, gameId, ans);
                        return 1;
                    }))
                )
            )
            .then(literal("eventchoice")
                .then(argument("eventId", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .then(argument("choice", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String eventId = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "eventId");
                        String choice = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "choice");
                        com.crazygirlfriend.event.RandomEventManager.get().resolveChoice(player, eventId, choice);
                        return 1;
                    }))
                )
            )
            .then(literal("event")
                .then(literal("trigger")
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        com.crazygirlfriend.event.RandomEventManager.get().triggerRandomEventForPlayer(player);
                        return 1;
                    })
                    .then(argument("id", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String eventId = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "id");
                        com.crazygirlfriend.event.RandomEventManager.get().triggerEvent(player, eventId);
                        return 1;
                    }))
                )
            )
            .then(literal("season")
                .executes(ctx -> {
                    com.crazygirlfriend.season.SeasonManager sm = com.crazygirlfriend.season.SeasonManager.get();
                    com.crazygirlfriend.season.Season s = sm.getCurrentSeason();
                    ServerCommandSource src = ctx.getSource();
                    src.sendMessage(Text.literal("════════ Seasonal Calendar ════════").formatted(Formatting.LIGHT_PURPLE));
                    src.sendMessage(Text.literal(" • Current Season: " + s.getEmoji() + " " + s.getDisplayName()).formatted(Formatting.GOLD, Formatting.BOLD));
                    src.sendMessage(Text.literal(" • Atmosphere: " + s.getDescription()).formatted(Formatting.YELLOW));
                    src.sendMessage(Text.literal(" • Cycle Length: " + sm.getSeasonDays() + " in-game days per season").formatted(Formatting.AQUA));
                    src.sendMessage(Text.literal(" • World Status: " + (sm.isLocked() ? "🔒 Locked" : "⏳ Active Seasonal Cycle")).formatted(Formatting.GREEN));
                    src.sendMessage(Text.literal("════════════════════════════════════").formatted(Formatting.LIGHT_PURPLE));
                    return 1;
                })
                .then(literal("set")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("name", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(ctx -> {
                        String name = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");
                        com.crazygirlfriend.season.Season s = com.crazygirlfriend.season.Season.fromString(name);
                        com.crazygirlfriend.season.SeasonManager.get().setSeason(s, ctx.getSource().getServer());
                        ctx.getSource().sendFeedback(() -> Text.literal("🌸 Season manually set to: " + s.getEmoji() + " " + s.getDisplayName()).formatted(Formatting.LIGHT_PURPLE), true);
                        return 1;
                    }))
                )
                .then(literal("lock")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("locked", BoolArgumentType.bool()).executes(ctx -> {
                        boolean locked = BoolArgumentType.getBool(ctx, "locked");
                        com.crazygirlfriend.season.SeasonManager.get().setLocked(locked, null, ctx.getSource().getServer());
                        ctx.getSource().sendFeedback(() -> Text.literal("🔒 Seasonal progression " + (locked ? "LOCKED" : "UNLOCKED")).formatted(Formatting.YELLOW), true);
                        return 1;
                    }))
                )
                .then(literal("days")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("count", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 100)).executes(ctx -> {
                        int count = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "count");
                        com.crazygirlfriend.config.ModConfig.get().seasonDays = count;
                        com.crazygirlfriend.config.ModConfig.save();
                        com.crazygirlfriend.season.SeasonManager.get().broadcastSeasonSync(ctx.getSource().getServer());
                        ctx.getSource().sendFeedback(() -> Text.literal("⏳ Season length set to: " + count + " in-game days").formatted(Formatting.GREEN), true);
                        return 1;
                    }))
                )
            )
            .then(literal("locatescenic").executes(ctx -> {
                com.crazygirlfriend.world.gen.ScenicLocationManager.get().locateForPlayer(ctx.getSource().getPlayerOrThrow());
                return 1;
            }))
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
            source.sendMessage(Text.literal(" • Affection Level: " + rel.getAffectionLevel() + " (" + rel.getAffectionXp() + " XP)").formatted(Formatting.LIGHT_PURPLE));
            com.crazygirlfriend.relationship.CrazyTier tier = rel.getCrazyTier();
            source.sendMessage(Text.literal(" • Crazy Meter: " + rel.getCrazyMeter() + "% [" + tier.getDisplayName() + "]").formatted(Formatting.RED));
            source.sendMessage(Text.literal(" • Your Mood: " + rel.getMood(player.getUuid()).getDisplayName()).formatted(Formatting.AQUA));
            UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
            source.sendMessage(Text.literal(" • Partner Mood: " + rel.getMood(partnerUuid).getDisplayName()).formatted(Formatting.AQUA));
            source.sendMessage(Text.literal(" • Auto-Accept: " + (rel.isAutoAccept() ? "Enabled" : "Disabled")).formatted(Formatting.GREEN));
        }
        source.sendMessage(Text.literal("════════════════════════════════════").formatted(Formatting.LIGHT_PURPLE));
    }

    private static void showHelp(ServerCommandSource source) {
        source.sendMessage(Text.literal("══ Crazy Girlfriend Modpack Commands ══").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        source.sendMessage(Text.literal(" • /cg status [player] - View relationship status, crazy meter & mood").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg anim <name> [player] - Trigger any of the 63 animations").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg minigame <game> - Challenge partner to a couple minigame").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg mood <mood> - Set your relationship mood").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg cancel - Immediately cancel active animation or dismount").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg autoaccept [on|off] - Auto-accept partner requests").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg unleash - Free yourself from a leash").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • /cg wipeface - Wash off makeup").formatted(Formatting.GRAY));
        source.sendMessage(Text.literal(" • Sneak + Right Click any player to open Interaction GUI!").formatted(Formatting.YELLOW, Formatting.ITALIC));
    }
}

