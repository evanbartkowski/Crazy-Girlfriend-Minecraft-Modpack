package com.crazygirlfriend.combo;

import com.crazygirlfriend.animation.AnimationType;

import java.util.*;

public class AnimationComboRegistry {
    public enum ComboCategory {
        ROMANTIC,
        COMEDY,
        ARGUMENT,
        DANCE
    }

    public static class ComboBranch {
        public final AnimationType from;
        public final AnimationType to;
        public final String displayName;
        public final String hotkey;
        public final ComboCategory category;
        public final int affectionBonus;
        public final int crazyDelta;

        public ComboBranch(AnimationType from, AnimationType to, String displayName, String hotkey, ComboCategory category, int affectionBonus, int crazyDelta) {
            this.from = from;
            this.to = to;
            this.displayName = displayName;
            this.hotkey = hotkey;
            this.category = category;
            this.affectionBonus = affectionBonus;
            this.crazyDelta = crazyDelta;
        }
    }

    private static final Map<AnimationType, List<ComboBranch>> COMBO_MAP = new EnumMap<>(AnimationType.class);

    static {
        // === FROM HUG ===
        register(new ComboBranch(AnimationType.HUG, AnimationType.ROMANTIC_DIP_KISS, "Romantic Dip", "1", ComboCategory.ROMANTIC, 15, -2));
        register(new ComboBranch(AnimationType.HUG, AnimationType.SPINNING_HUG, "Spin Around", "2", ComboCategory.ROMANTIC, 10, 0));
        register(new ComboBranch(AnimationType.HUG, AnimationType.BEAR_HUG, "Bear Hug", "3", ComboCategory.ROMANTIC, 8, -1));
        register(new ComboBranch(AnimationType.HUG, AnimationType.HAND_HOLDING, "Hold Hands", "4", ComboCategory.ROMANTIC, 5, 0));

        // === FROM LONG HUG / ROMANTIC SWAY HUG ===
        register(new ComboBranch(AnimationType.LONG_HUG, AnimationType.KISS, "Romantic Kiss", "1", ComboCategory.ROMANTIC, 12, -2));
        register(new ComboBranch(AnimationType.LONG_HUG, AnimationType.SLOW_DANCE, "Slow Dance", "2", ComboCategory.DANCE, 15, -3));
        register(new ComboBranch(AnimationType.LONG_HUG, AnimationType.CUDDLE, "Gentle Cuddle", "3", ComboCategory.ROMANTIC, 10, -2));
        register(new ComboBranch(AnimationType.ROMANTIC_SWAY_HUG, AnimationType.ROMANTIC_DIP_KISS, "Dip Kiss", "1", ComboCategory.ROMANTIC, 15, -2));
        register(new ComboBranch(AnimationType.ROMANTIC_SWAY_HUG, AnimationType.SLOW_DANCE, "Slow Dance", "2", ComboCategory.DANCE, 12, -2));

        // === FROM KISS / QUICK KISS ===
        register(new ComboBranch(AnimationType.KISS, AnimationType.MAKE_OUT, "Passionate Kiss", "1", ComboCategory.ROMANTIC, 20, -5));
        register(new ComboBranch(AnimationType.KISS, AnimationType.FOREHEAD_KISS, "Forehead Kiss", "2", ComboCategory.ROMANTIC, 10, -2));
        register(new ComboBranch(AnimationType.KISS, AnimationType.HEAD_BUMP_KISS, "Head Bonk!", "3", ComboCategory.COMEDY, 5, 3));
        register(new ComboBranch(AnimationType.KISS, AnimationType.BLUSHING, "Blush Shyly", "4", ComboCategory.ROMANTIC, 8, 0));
        register(new ComboBranch(AnimationType.QUICK_KISS, AnimationType.KISS, "Deeper Kiss", "1", ComboCategory.ROMANTIC, 10, -1));
        register(new ComboBranch(AnimationType.QUICK_KISS, AnimationType.CHEEK_KISS, "Cheek Kiss", "2", ComboCategory.ROMANTIC, 6, 0));

        // === FROM HAND HOLDING ===
        register(new ComboBranch(AnimationType.HAND_HOLDING, AnimationType.RAIN_TWIRL, "Rain Twirl", "1", ComboCategory.DANCE, 15, -2));
        register(new ComboBranch(AnimationType.HAND_HOLDING, AnimationType.SLOW_DANCE, "Slow Dance", "2", ComboCategory.DANCE, 12, -2));
        register(new ComboBranch(AnimationType.HAND_HOLDING, AnimationType.FAST_DANCE, "Fast Dance", "3", ComboCategory.DANCE, 10, 0));
        register(new ComboBranch(AnimationType.HAND_HOLDING, AnimationType.HUG, "Embrace", "4", ComboCategory.ROMANTIC, 8, -1));

        // === FROM FLIRTING ===
        register(new ComboBranch(AnimationType.FLIRTING, AnimationType.CHEEK_KISS, "Cheek Kiss", "1", ComboCategory.ROMANTIC, 10, -1));
        register(new ComboBranch(AnimationType.FLIRTING, AnimationType.TICKLE_FIGHT, "Tickle Fight", "2", ComboCategory.COMEDY, 12, 1));
        register(new ComboBranch(AnimationType.FLIRTING, AnimationType.POUTING, "Playful Pout", "3", ComboCategory.ROMANTIC, 6, 2));

        // === FROM SLAP ===
        register(new ComboBranch(AnimationType.SLAP_ATTACKER, AnimationType.SPINNING_SLAP, "Spinning Slap!", "1", ComboCategory.COMEDY, 5, 5));
        register(new ComboBranch(AnimationType.SLAP_ATTACKER, AnimationType.RECONCILIATION_HUG, "Forgive & Hug", "2", ComboCategory.ROMANTIC, 25, -15));
        register(new ComboBranch(AnimationType.SLAP_VICTIM, AnimationType.FAKE_COLLAPSE, "Dramatic Faint", "1", ComboCategory.COMEDY, 8, 2));
        register(new ComboBranch(AnimationType.SLAP_VICTIM, AnimationType.RAGDOLL_FAINT, "Ragdoll Flop", "2", ComboCategory.COMEDY, 8, 2));

        // === FROM PIGGYBACK ===
        register(new ComboBranch(AnimationType.PIGGYBACK, AnimationType.PASSENGER_WAVING, "Wave Hands", "1", ComboCategory.ROMANTIC, 8, 0));
        register(new ComboBranch(AnimationType.PIGGYBACK, AnimationType.STUMBLE_CARRY, "Stumble Wobble", "2", ComboCategory.COMEDY, 6, 2));
        register(new ComboBranch(AnimationType.PIGGYBACK, AnimationType.GENTLE_DISMOUNT, "Step Down", "3", ComboCategory.ROMANTIC, 4, 0));

        // === FROM CUDDLE ===
        register(new ComboBranch(AnimationType.CUDDLE, AnimationType.NUZZLE, "Gentle Nuzzle", "1", ComboCategory.ROMANTIC, 12, -2));
        register(new ComboBranch(AnimationType.CUDDLE, AnimationType.HEAD_ON_SHOULDER, "Rest Head", "2", ComboCategory.ROMANTIC, 10, -2));
        register(new ComboBranch(AnimationType.CUDDLE, AnimationType.SLEEP_TOGETHER, "Sleep Together", "3", ComboCategory.ROMANTIC, 18, -4));
        register(new ComboBranch(AnimationType.CAMPFIRE_CUDDLE, AnimationType.CUDDLE, "Closer Cuddle", "1", ComboCategory.ROMANTIC, 14, -3));
        register(new ComboBranch(AnimationType.CAMPFIRE_CUDDLE, AnimationType.STARGAZING, "Stargazing", "2", ComboCategory.ROMANTIC, 16, -4));
    }

    private static void register(ComboBranch branch) {
        COMBO_MAP.computeIfAbsent(branch.from, k -> new ArrayList<>()).add(branch);
    }

    public static List<ComboBranch> getBranches(AnimationType from) {
        return COMBO_MAP.getOrDefault(from, Collections.emptyList());
    }

    public static boolean canCombo(AnimationType from, AnimationType to) {
        List<ComboBranch> branches = COMBO_MAP.get(from);
        if (branches == null) return false;
        for (ComboBranch branch : branches) {
            if (branch.to == to) return true;
        }
        return false;
    }

    public static ComboBranch getBranch(AnimationType from, AnimationType to) {
        List<ComboBranch> branches = COMBO_MAP.get(from);
        if (branches == null) return null;
        for (ComboBranch branch : branches) {
            if (branch.to == to) return branch;
        }
        return null;
    }
}

