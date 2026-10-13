package com.crazygirlfriend.relationship;

public enum CrazyTier {
    SWEETHEART("Sweetheart 💖", 0, 24, "Cute hearts, surprise gifts, sweet hugs"),
    SUSPICIOUS("Suspicious 🤨", 25, 49, "Side-eye looks, suspicious checks, dramatic dialogue"),
    DRAMA_QUEEN("Drama Queen 👑", 50, 74, "Dramatic crying, foot stomping, slipper throwing"),
    FURIOUS("Furious ⚡", 75, 94, "Thunderclouds, comical chases, tantrums"),
    TOTAL_CHAOS("Total Chaos 🌪️", 95, 100, "Unfiltered slapstick, crazy couple challenges!");

    private final String displayName;
    private final int min;
    private final int max;
    private final String description;

    CrazyTier(String displayName, int min, int max, String description) {
        this.displayName = displayName;
        this.min = min;
        this.max = max;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public String getDescription() { return description; }

    public int getColorHex() {
        return switch (this) {
            case SWEETHEART -> 0xFFFF69B4;
            case SUSPICIOUS -> 0xFFFFA500;
            case DRAMA_QUEEN -> 0xFFFF4500;
            case FURIOUS -> 0xFFFF1493;
            case TOTAL_CHAOS -> 0xFFFF0000;
        };
    }

    public static CrazyTier fromMeter(int meter) {
        if (meter < 25) return SWEETHEART;
        if (meter < 50) return SUSPICIOUS;
        if (meter < 75) return DRAMA_QUEEN;
        if (meter < 95) return FURIOUS;
        return TOTAL_CHAOS;
    }
}
