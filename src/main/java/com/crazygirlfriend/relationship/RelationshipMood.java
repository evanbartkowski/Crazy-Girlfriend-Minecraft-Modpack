package com.crazygirlfriend.relationship;

public enum RelationshipMood {
    HAPPY("Happy 😊", 0xFFE066),
    LOVING("Loving 🥰", 0xFF69B4),
    EXCITED("Excited 🤩", 0xFF9933),
    SHY("Shy 😳", 0xFFB6C1),
    ANNOYED("Annoyed 😒", 0xCC9966),
    JEALOUS("Jealous 😠", 0x33CC33),
    FURIOUS("Furious 🤬", 0xFF3333),
    EMBARRASSED("Embarrassed 🫣", 0xFF9999),
    SLEEPY("Sleepy 🥱", 0x9999FF),
    SAD("Sad 🥺", 0x6699FF);

    private final String displayName;
    private final int color;

    RelationshipMood(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }
}

