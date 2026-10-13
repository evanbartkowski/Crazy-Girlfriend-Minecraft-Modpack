package com.crazygirlfriend.animation;

public enum AnimationType {
    NONE(0),
    HUG(40),
    KISS(35),
    SLAP_ATTACKER(25),
    SLAP_VICTIM(25),
    STRADDLE(60),
    PIGGYBACK(0),
    MAKE_LOVE(70),
    BREAKUP(40),
    DIVORCE(50),
    PADDLE_SPANK_ATTACKER(25),
    PADDLE_SPANK_VICTIM(25),
    LEASH_SIT(0);

    private final int defaultDuration;

    AnimationType(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }
}

