package com.crazygirlfriend.facial;

public enum FacialExpression {
    NONE("Neutral"),
    BLUSHING("Blushing"),
    SMILING("Smiling"),
    CLOSED_EYES("Closed Eyes"),
    WINKING("Winking"),
    ANGRY_EYEBROWS("Angry"),
    SHOCKED("Shocked"),
    EMBARRASSED("Embarrassed"),
    CARTOON_TEARS("Cartoon Tears"),
    SAD("Sad"),
    POUTING("Pouting"),
    SUSPICIOUS_SIDE_EYE("Side-Eye"),
    DIZZY_EYES("Dizzy Eyes"),
    SLEEPY("Sleepy"),
    LAUGHING("Laughing"),
    EXAGGERATED_CRYING("Drama Crying");

    private final String displayName;

    FacialExpression(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

