package com.crazygirlfriend.relationship;

public enum RequestType {
    PROPOSAL_DATING("be their girlfriend"),
    PROPOSAL_MARRIAGE("marry them"),
    HUG("hug"),
    KISS("kiss"),
    SLAP("slap"),
    STRADDLE("straddle"),
    PIGGYBACK("piggyback ride"),
    MAKE_LOVE("make love"),
    GIFT_BOUQUET("gift a bouquet"),
    PADDLE_SPANK("paddle spank"),
    LEASH("put a leash on you"),
    APPLY_MAKEUP("apply makeup on you");

    private final String description;

    RequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

