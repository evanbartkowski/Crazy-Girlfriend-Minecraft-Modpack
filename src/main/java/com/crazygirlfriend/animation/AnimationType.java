package com.crazygirlfriend.animation;

public enum AnimationType {
    NONE(0),
    // Romantic Interactions
    HUG(40),
    LONG_HUG(80),
    BACK_HUG(60),
    KISS(30),
    QUICK_KISS(20),
    MAKE_OUT(70),
    FOREHEAD_KISS(35),
    CHEEK_KISS(30),
    KISS_ATTACK(50),
    PRINCESS_KISS(60),
    NUZZLE(45),
    CUDDLE(80),
    SPOONING(0),
    LAP_SITTING(0),
    STRADDLE(60),
    HAND_HOLDING(0),
    SLOW_DANCE(80),
    FAST_DANCE(60),
    PIGGYBACK(0),
    BRIDAL_CARRY(0),
    SHOULDER_CARRY(0),
    TICKLE_FIGHT(50),
    FLIRTING(40),
    SLEEP_TOGETHER(0),
    ROMANTIC_NIGHT(70),
    MAKE_LOVE(70),
    CUDDLE_SLEEPING(0),
    SURPRISE_HUG(45),
    HEAD_ON_SHOULDER(60),
    SIT_TOGETHER(0),
    COMFORT(50),
    STARGAZING(80),

    // Funny & Chaotic Animations
    SLAP_ATTACKER(25),
    SLAP_VICTIM(25),
    SLAP_DRAMATIC(30),
    PADDLE_SPANK_ATTACKER(25),
    PADDLE_SPANK_VICTIM(25),
    BONK_ATTACKER(25),
    FRYING_PAN_SWING(25),
    BONK_VICTIM(35),
    GIGGLE(35),
    SQUEAK_HAMMER_ATTACKER(25),
    SQUEAK_HAMMER_VICTIM(30),
    FOOT_STOMPING(35),
    ARMS_CROSSED(45),
    DRAMATIC_CRYING(50),
    FALL_DESPAIR(50),
    FAKE_FAINTING(60),
    SIDE_EYE(35),
    POUTING(40),
    ANGRY_JUMPING(40),
    TANTRUM(50),
    ROLLING_EYES(30),
    LAUGHING_HARD(45),
    BLUSHING(40),
    DRAMATIC_REJECTION(45),
    BACKING_AWAY(35),
    PILLOW_FIGHT_ATTACKER(25),
    PILLOW_FIGHT_VICTIM(25),
    PLAYFUL_WRESTLE(60),
    RING_THROW(40),
    SURPRISE_REACTION(35),
    JUMP_APART(30),
    STRUGGLE_CARRY(40),
    DIZZY(50),
    SNORING(60),
    SHIVERING(50),
    LEASH_SIT(0),
    LEASH_CRAWL(0),
    DRAGGED_PLAYFUL(40),
    RECONCILIATION_HUG(60),
    BREAKUP(40),
    DIVORCE(50),
    WET_SHAKE(35);

    private final int defaultDuration;

    AnimationType(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public boolean isContinuous() {
        return defaultDuration == 0;
    }
}
