package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.relationship.CrazyTier;

public class RelationshipEvent {
    private final String id;
    private final String title;
    private final String prompt;
    private final String optionA;
    private final String optionB;
    private final String outcomeA;
    private final String outcomeB;
    private final int crazyDeltaA;
    private final int crazyDeltaB;
    private final int affectionDeltaA;
    private final int affectionDeltaB;
    private final AnimationType animA;
    private final AnimationType animB;
    private final boolean isCrazyEvent;
    private final CrazyTier minimumTier;

    public RelationshipEvent(String id, String title, String prompt,
                             String optionA, String optionB,
                             String outcomeA, String outcomeB,
                             int crazyDeltaA, int crazyDeltaB,
                             int affectionDeltaA, int affectionDeltaB,
                             AnimationType animA, AnimationType animB,
                             boolean isCrazyEvent, CrazyTier minimumTier) {
        this.id = id;
        this.title = title;
        this.prompt = prompt;
        this.optionA = optionA;
        this.optionB = optionB;
        this.outcomeA = outcomeA;
        this.outcomeB = outcomeB;
        this.crazyDeltaA = crazyDeltaA;
        this.crazyDeltaB = crazyDeltaB;
        this.affectionDeltaA = affectionDeltaA;
        this.affectionDeltaB = affectionDeltaB;
        this.animA = animA;
        this.animB = animB;
        this.isCrazyEvent = isCrazyEvent;
        this.minimumTier = minimumTier;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPrompt() { return prompt; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOutcomeA() { return outcomeA; }
    public String getOutcomeB() { return outcomeB; }
    public int getCrazyDeltaA() { return crazyDeltaA; }
    public int getCrazyDeltaB() { return crazyDeltaB; }
    public int getAffectionDeltaA() { return affectionDeltaA; }
    public int getAffectionDeltaB() { return affectionDeltaB; }
    public AnimationType getAnimA() { return animA; }
    public AnimationType getAnimB() { return animB; }
    public boolean isCrazyEvent() { return isCrazyEvent; }
    public CrazyTier getMinimumTier() { return minimumTier; }
}
