package com.crazygirlfriend.season;

public enum Season {
    SPRING("Spring", "🌸", 0x78D936, 0x60D048, "Blooms, cherry petals, fresh breezes, and gentle showers"),
    SUMMER("Summer", "☀️", 0x4FB828, 0x389E20, "Golden sun, active butterflies, warm twilights, and fireflies"),
    AUTUMN("Autumn", "🍂", 0xBFA046, 0xD86618, "Golden amber forests, falling leaves, crisp air, and misty mornings"),
    WINTER("Winter", "❄️", 0x8CA09A, 0x7A948C, "Snow flurries, frost-covered trees, clear starry nights, and auroras");

    private final String displayName;
    private final String emoji;
    private final int grassColorTint;
    private final int foliageColorTint;
    private final String description;

    Season(String displayName, String emoji, int grassColorTint, int foliageColorTint, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.grassColorTint = grassColorTint;
        this.foliageColorTint = foliageColorTint;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getGrassColorTint() {
        return grassColorTint;
    }

    public int getFoliageColorTint() {
        return foliageColorTint;
    }

    public String getDescription() {
        return description;
    }

    public Season getNext() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public static Season fromString(String str) {
        if (str == null) return SPRING;
        try {
            return Season.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SPRING;
        }
    }
}

