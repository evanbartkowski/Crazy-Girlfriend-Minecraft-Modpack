package com.crazygirlfriend.client;

import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientRelationshipManager {
    private static final ClientRelationshipManager INSTANCE = new ClientRelationshipManager();

    private final Map<UUID, Relationship> relationships = new HashMap<>();
    private final Map<UUID, UUID> activeLeashes = new HashMap<>();
    private final Map<UUID, Boolean> leashSitting = new HashMap<>();
    private final Map<UUID, PlayerMakeupData> playerMakeups = new HashMap<>();

    public int fadeVignetteRemaining = 0;
    public int fadeVignetteTotal = 0;

    public static ClientRelationshipManager get() {
        return INSTANCE;
    }

    public void clearRelationships() {
        relationships.clear();
    }

    public void addRelationship(Relationship rel) {
        relationships.put(rel.getPlayer1(), rel);
        relationships.put(rel.getPlayer2(), rel);
    }

    public Relationship getRelationship(UUID playerUuid) {
        return relationships.get(playerUuid);
    }

    public boolean areInRelationship(UUID p1, UUID p2) {
        Relationship rel = getRelationship(p1);
        return rel != null && rel.involves(p2);
    }

    public void clearLeashes() {
        activeLeashes.clear();
        leashSitting.clear();
    }

    public void setLeash(UUID leashed, UUID holder, boolean sitting) {
        activeLeashes.put(leashed, holder);
        leashSitting.put(leashed, sitting);
    }

    public boolean isLeashed(UUID playerUuid) {
        return activeLeashes.containsKey(playerUuid);
    }

    public UUID getLeashHolder(UUID leashedUuid) {
        return activeLeashes.get(leashedUuid);
    }

    public boolean isSitting(UUID playerUuid) {
        return leashSitting.getOrDefault(playerUuid, false);
    }

    public void setMakeup(UUID playerUuid, PlayerMakeupData makeupData) {
        playerMakeups.put(playerUuid, makeupData);
    }

    public PlayerMakeupData getMakeup(UUID playerUuid) {
        return playerMakeups.computeIfAbsent(playerUuid, PlayerMakeupData::new);
    }

    public void triggerFadeVignette(int duration) {
        this.fadeVignetteRemaining = duration;
        this.fadeVignetteTotal = duration;
    }

    public void tick() {
        if (fadeVignetteRemaining > 0) {
            fadeVignetteRemaining--;
        }
    }
}

