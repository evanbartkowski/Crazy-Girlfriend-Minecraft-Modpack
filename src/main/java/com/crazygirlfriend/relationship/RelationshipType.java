package com.crazygirlfriend.relationship;

public enum RelationshipType {
    NONE,
    DATING,
    MARRIED;

    public String getDisplayName() {
        switch (this) {
            case DATING: return "Dating ❤️";
            case MARRIED: return "Married 💍";
            default: return "Single";
        }
    }
}

