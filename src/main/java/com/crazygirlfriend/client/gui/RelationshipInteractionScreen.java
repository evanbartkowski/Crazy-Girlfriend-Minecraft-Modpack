package com.crazygirlfriend.client.gui;

import com.crazygirlfriend.client.ClientRelationshipManager;
import com.crazygirlfriend.network.ModPackets;
import com.crazygirlfriend.relationship.CrazyTier;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class RelationshipInteractionScreen extends Screen {
    private final PlayerEntity targetPlayer;
    private int currentTab = 0; // 0 = Romantic, 1 = Chaos, 2 = Minigames, 3 = Actions & Props
    private int page = 0;
    private static final int ITEMS_PER_PAGE = 8;

    private record ActionEntry(String label, String actionId, String tooltip) {}

    private final List<ActionEntry> romanticActions = new ArrayList<>();
    private final List<ActionEntry> chaosActions = new ArrayList<>();
    private final List<ActionEntry> minigameActions = new ArrayList<>();
    private final List<ActionEntry> actionsProps = new ArrayList<>();

    public RelationshipInteractionScreen(PlayerEntity targetPlayer) {
        super(Text.translatable("gui.crazygirlfriend.interaction_title", targetPlayer.getName().getString()));
        this.targetPlayer = targetPlayer;
        initActionLists();
    }

    private void initActionLists() {
        // Romantic
        romanticActions.add(new ActionEntry("🌹 Give Red Rose", "rose", "Ask them to be your girlfriend/partner"));
        romanticActions.add(new ActionEntry("💍 Propose Marriage", "marry", "Propose marriage with Diamond Ring"));
        romanticActions.add(new ActionEntry("🤗 Warm Hug", "hug", "Gentle romantic embrace with hearts"));
        romanticActions.add(new ActionEntry("🫂 Long Hug", "LONG_HUG", "Deep sustained embrace with swaying"));
        romanticActions.add(new ActionEntry("💖 Back Hug", "BACK_HUG", "Sweet surprise hug from behind"));
        romanticActions.add(new ActionEntry("💋 Sweet Kiss", "kiss", "Quick sweet kiss with heart particles"));
        romanticActions.add(new ActionEntry("🔥 Passionate Make Out", "MAKE_OUT", "Intimate romantic embrace & blushing"));
        romanticActions.add(new ActionEntry("✨ Forehead Kiss", "FOREHEAD_KISS", "Gentle affectionate forehead kiss"));
        romanticActions.add(new ActionEntry("👑 Princess Carry Kiss", "PRINCESS_KISS", "Dramatically sweep them up and kiss"));
        romanticActions.add(new ActionEntry("🥰 Face Nuzzle", "NUZZLE", "Gently nuzzle cheeks together"));
        romanticActions.add(new ActionEntry("🧸 Cuddle", "CUDDLE", "Affectionate cuddle together"));
        romanticActions.add(new ActionEntry("🤝 Hold Hands", "HAND_HOLDING", "Hold hands while walking together"));
        romanticActions.add(new ActionEntry("💃 Slow Dance", "SLOW_DANCING", "Romantic synchronized slow dance"));
        romanticActions.add(new ActionEntry("✨ Stargazing", "ROMANTIC_STARGAZING", "Lie down side-by-side looking at the sky"));
        romanticActions.add(new ActionEntry("❤️ Make Love", "makelove", "Romantic non-explicit embrace & fade-to-black"));
        romanticActions.add(new ActionEntry("💐 Gift Bouquet", "bouquet", "Gift a beautiful flower bouquet"));
        romanticActions.add(new ActionEntry("💃 Romantic Dip Kiss", "ROMANTIC_DIP_KISS", "Dramatic cinematic dip kiss"));
        romanticActions.add(new ActionEntry("🔄 Spinning Hug", "SPINNING_HUG", "Lift and twirl around in circle"));
        romanticActions.add(new ActionEntry("🐻 Bear Hug", "BEAR_HUG", "Tight passionate bear hug"));
        romanticActions.add(new ActionEntry("🌧️ Rain Twirl", "RAIN_TWIRL", "Graceful musical twirl in the rain"));
        romanticActions.add(new ActionEntry("🔥 Campfire Cuddle", "CAMPFIRE_CUDDLE", "Warm snuggle beside the campfire"));

        // Chaos & Comedy
        chaosActions.add(new ActionEntry("👋 Dramatic Slap", "slap", "Dramatic comedic slap with stars & knockback"));
        chaosActions.add(new ActionEntry("🍳 Frying Pan BONK", "BONK_REACTION", "Bonk them over the head with comic sound"));
        chaosActions.add(new ActionEntry("🏏 Paddle Spank", "paddle", "Playful disciplinary paddle spank"));
        chaosActions.add(new ActionEntry("🩴 Slipper Impact", "SLIPPER_IMPACT", "Dramatic motherly slipper reaction"));
        chaosActions.add(new ActionEntry("🔨 Squeak Hammer", "squeak", "Hit with squeaky squeaking hammer"));
        chaosActions.add(new ActionEntry("🐟 Raw Fish Slap", "FISH_SLAP_ATTACKER", "Slap with a wet slimy fish!"));
        chaosActions.add(new ActionEntry("💫 360 Spinning Slap", "SPINNING_SLAP", "Dramatic full spin windup slap!"));
        chaosActions.add(new ActionEntry("💥 Head Bonk Kiss", "HEAD_BUMP_KISS", "Comedic accidental head collision!"));
        chaosActions.add(new ActionEntry("🎭 Ragdoll Flop", "RAGDOLL_FAINT", "Comedic limp ragdoll faint"));
        chaosActions.add(new ActionEntry("😾 Arms Crossed", "ARMS_CROSSED_ANGRY", "Sulking angry arms-crossed stance"));
        chaosActions.add(new ActionEntry("😭 Dramatic Crying", "DRAMATIC_CRYING", "Exaggerated sobbing and tears"));
        chaosActions.add(new ActionEntry("🧎 Fall in Despair", "FALL_TO_KNEES_DESPAIR", "Fall to knees like a telenovela scene"));
        chaosActions.add(new ActionEntry("💀 Fake Fainting", "FAKE_FAINTING", "Comically faint backwards"));
        chaosActions.add(new ActionEntry("👀 Exaggerated Side-Eye", "EXAGGERATED_SIDE_EYE", "Bombastic side-eye look"));
        chaosActions.add(new ActionEntry("😤 Cartoon Tantrum", "CARTOON_TANTRUM", "Stamp feet and jump angrily"));
        chaosActions.add(new ActionEntry("🤣 Can't Stop Laughing", "LAUGHING_UNCONTROLLABLY", "Roll around laughing hysterically"));
        chaosActions.add(new ActionEntry("🤼 Playful Wrestling", "PLAYFUL_WRESTLING", "Chaotic comedy wrestle on the ground"));
        chaosActions.add(new ActionEntry("🪶 Pillow Fight", "PILLOW_FIGHTING", "Dramatic feather-flying pillow battle"));
        chaosActions.add(new ActionEntry("💍 Throw Ring Away", "THROWING_RING_DRAMATIC", "Dramatically fling engagement ring away"));

        // Minigames & Dates
        minigameActions.add(new ActionEntry("🧠 Couple Trivia", "minigame:trivia", "Answer funny relationship trivia together!"));
        minigameActions.add(new ActionEntry("✂️ Rock Paper Scissors", "minigame:rps", "Best 2 out of 3 comedy duel!"));
        minigameActions.add(new ActionEntry("🪶 Pillow War Arena", "minigame:pillow_fight", "30-second pillow whacking challenge!"));
        minigameActions.add(new ActionEntry("🤼 Wrestling Match", "minigame:wrestling", "Humorous living room wrestling match!"));
        minigameActions.add(new ActionEntry("🎰 Love Roulette", "minigame:love_roulette", "Spin the wheel for random funny buff or prank!"));
        minigameActions.add(new ActionEntry("🗺️ Scavenger Hunt", "minigame:scavenger", "Find a romantic item in 60 seconds!"));
        minigameActions.add(new ActionEntry("🎁 Secret Gift Challenge", "minigame:gift_challenge", "Exchange mystery presents for rewards!"));
        minigameActions.add(new ActionEntry("📷 Kiss Cam Moment", "minigame:kiss_cam", "10 seconds to find each other and kiss!"));
        minigameActions.add(new ActionEntry("🕺 Couple Dance-Off", "minigame:dance_off", "Synchronized dancing rhythmic competition!"));
        minigameActions.add(new ActionEntry("🥺 The Apology Challenge", "minigame:apology", "Apologize dramatically to clear Crazy Meter!"));

        // Actions & Props
        actionsProps.add(new ActionEntry("🐴 Piggyback Ride", "piggyback", "Carry them on your back"));
        actionsProps.add(new ActionEntry("👰 Bridal Carry", "BRIDAL_CARRY", "Carry partner romantically in arms"));
        actionsProps.add(new ActionEntry("🏋️ Shoulder Carry", "SHOULDER_CARRY", "Comically carry them over your shoulder"));
        actionsProps.add(new ActionEntry("👋 Wave While Riding", "PASSENGER_WAVING", "Wave enthusiastically from piggyback"));
        actionsProps.add(new ActionEntry("🏋️ Stumble Carry", "STUMBLE_CARRY", "Comically struggle to carry"));
        actionsProps.add(new ActionEntry("⬇️ Gentle Dismount", "GENTLE_DISMOUNT", "Gracefully step down to the ground"));
        actionsProps.add(new ActionEntry("🧸 Straddle", "straddle", "Playful lap sitting interaction"));
        actionsProps.add(new ActionEntry("💄 Makeup Studio", "makeup_screen", "Open custom facial makeup artist studio"));
        actionsProps.add(new ActionEntry("🪢 Player Leash", "leash", "Attach leash or toggle sit / heel"));
        actionsProps.add(new ActionEntry("🛋️ Sit Together", "SIT_TOGETHER", "Sit beside each other on the ground/sofa"));
        actionsProps.add(new ActionEntry("🤝 Reconcile Hug", "RECONCILIATION_HUG", "Emotional reunion hug after argument"));
        actionsProps.add(new ActionEntry("💔 Break Up / Divorce", "breakup_or_divorce", "End dating or marriage legally"));
    }

    @Override
    protected void init() {
        super.init();
        buildGui();
    }

    private void buildGui() {
        this.clearChildren();

        int centerX = this.width / 2;
        int topY = this.height / 2 - 100;
        int tabWidth = 72;
        int tabHeight = 20;

        // 4 Category Tabs
        String[] tabNames = {"❤️ Romance", "⚡ Chaos", "🎮 Dates", "🛠️ Actions"};
        for (int i = 0; i < 4; i++) {
            final int tIndex = i;
            ButtonWidget tabBtn = ButtonWidget.builder(Text.literal(tabNames[i]), b -> {
                currentTab = tIndex;
                page = 0;
                buildGui();
            }).dimensions(centerX - 150 + (i * 75), topY + 45, tabWidth, tabHeight).build();
            if (currentTab == i) {
                tabBtn.active = false;
            }
            this.addDrawableChild(tabBtn);
        }

        // Active Tab Action Buttons (Grid 2 columns x 4 rows = 8 items)
        List<ActionEntry> list = getCurrentList();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, list.size());

        int btnW = 145;
        int btnH = 20;
        int startActionY = topY + 72;

        for (int i = startIndex; i < endIndex; i++) {
            int relIndex = i - startIndex;
            int col = relIndex % 2;
            int row = relIndex / 2;
            int x = (col == 0) ? (centerX - btnW - 5) : (centerX + 5);
            int y = startActionY + (row * 24);

            ActionEntry entry = list.get(i);
            this.addDrawableChild(ButtonWidget.builder(Text.literal(entry.label()), b -> handleActionClick(entry.actionId()))
                .dimensions(x, y, btnW, btnH)
                .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal(entry.tooltip())))
                .build());
        }

        // Pagination buttons
        if (page > 0) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                page--;
                buildGui();
            }).dimensions(centerX - 110, startActionY + 102, 50, 18).build());
        }

        if (endIndex < list.size()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                page++;
                buildGui();
            }).dimensions(centerX + 60, startActionY + 102, 50, 18).build());
        }

        // Visuals & Settings Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🌸 Visuals"), b -> {
            if (this.client != null) {
                this.client.setScreen(new VisualSettingsScreen(this));
            }
        }).dimensions(centerX - 80, startActionY + 125, 75, 18).build());

        // Close Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✖ Close"), b -> this.close())
            .dimensions(centerX + 5, startActionY + 125, 75, 18)
            .build());
    }

    private List<ActionEntry> getCurrentList() {
        return switch (currentTab) {
            case 0 -> romanticActions;
            case 1 -> chaosActions;
            case 2 -> minigameActions;
            default -> actionsProps;
        };
    }

    private void handleActionClick(String actionId) {
        if ("makeup_screen".equals(actionId)) {
            this.close();
            if (client != null) {
                client.setScreen(new MakeupScreen(targetPlayer));
            }
            return;
        }

        if ("breakup_or_divorce".equals(actionId)) {
            Relationship clientRel = ClientRelationshipManager.get().getRelationship(client.player.getUuid());
            if (clientRel != null && clientRel.getType() == RelationshipType.MARRIED) {
                sendAction("divorce");
            } else {
                sendAction("breakup");
            }
            return;
        }

        sendAction(actionId);
    }

    private void sendAction(String action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetPlayer.getUuid());
        buf.writeString(action);
        ClientPlayNetworking.send(ModPackets.INTERACTION_REQUEST_C2S, buf);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int centerX = this.width / 2;
        int topY = this.height / 2 - 100;

        // Container Panel
        context.fill(centerX - 160, topY - 10, centerX + 160, topY + 225, 0xEE141419);
        context.fill(centerX - 158, topY - 8, centerX + 158, topY + 223, 0x8823232D);

        // Header
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("💕 CRAZY GIRLFRIEND HUB 💕").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), centerX, topY - 2, 0xFFFFAA);

        // Target Name & Status Badge
        Relationship rel = ClientRelationshipManager.get().getRelationship(targetPlayer.getUuid());
        String relBadge = "Status: Single";
        int badgeColor = 0xAAAAAA;
        int crazyMeter = 0;
        int affectionXp = 0;
        String mood = "HAPPY";

        if (rel != null) {
            crazyMeter = rel.getCrazyMeter();
            affectionXp = rel.getAffectionXp();
            mood = rel.getMood(targetPlayer.getUuid()).getDisplayName();
            if (rel.getType() == RelationshipType.MARRIED) {
                relBadge = "💍 Married to " + rel.getPartnerName(targetPlayer.getUuid());
                badgeColor = 0xFFD700;
            } else if (rel.getType() == RelationshipType.DATING) {
                relBadge = "❤️ Dating " + rel.getPartnerName(targetPlayer.getUuid());
                badgeColor = 0xFF69B4;
            }
        }

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Partner: ").append(targetPlayer.getName()).append(" | ").append(relBadge), centerX, topY + 12, badgeColor);

        // Status Dashboard (Crazy Meter Bar & Mood)
        int barX = centerX - 120;
        int barY = topY + 26;
        int barWidth = 240;
        int barHeight = 8;

        // Background Bar
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        // Filled Crazy Bar
        CrazyTier tier = CrazyTier.fromMeter(crazyMeter);
        int fillW = (int) ((crazyMeter / 100.0) * barWidth);
        context.fill(barX, barY, barX + fillW, barY + barHeight, tier.getColorHex());

        // Text Info below bar
        String statusLine = "Crazy: " + crazyMeter + "% [" + tier.getDisplayName() + "] | Affection: " + (affectionXp / 100 + 1) + "★ | Mood: " + mood;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(statusLine).formatted(Formatting.GOLD), centerX, barY + 10, 0xFFE080);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
