package com.crazygirlfriend.event.world;

public enum WorldEventType {
    NONE("None", "✨", 0),
    AURORA_NIGHT("Aurora Night", "🌌", 6000),
    METEOR_SHOWER("Meteor Shower", "🌠", 4800),
    ENCHANTED_FOREST("Enchanted Forest Night", "✨", 5000),
    PINK_SUNSET("Pink Sunset", "🌅", 2400),
    MAGICAL_MIST("Magical Valley Mist", "🌫️", 3600),
    FIREFLY_BLOOM("Firefly Bloom", "✨", 4000),
    LUMINOUS_FLOWERS("Luminous Flower Bloom", "🌸", 4500),
    RAINBOW_AFTER_RAIN("Rainbow After Rain", "🌈", 3000);

    private final String displayName;
    private final String emoji;
    private final int defaultDurationTicks;

    WorldEventType(String displayName, String emoji, int defaultDurationTicks) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.defaultDurationTicks = defaultDurationTicks;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getDefaultDurationTicks() {
        return defaultDurationTicks;
    }

    public static WorldEventType fromString(String str) {
        if (str == null) return NONE;
        try {
            return WorldEventType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
