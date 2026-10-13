package com.crazygirlfriend.relationship;

import java.util.UUID;

public class PendingRequest {
    private final UUID sender;
    private final String senderName;
    private final UUID target;
    private final String targetName;
    private final RequestType type;
    private final long expiryTick;
    private final Object extraData;

    public PendingRequest(UUID sender, String senderName, UUID target, String targetName, RequestType type, long expiryTick, Object extraData) {
        this.sender = sender;
        this.senderName = senderName;
        this.target = target;
        this.targetName = targetName;
        this.type = type;
        this.expiryTick = expiryTick;
        this.extraData = extraData;
    }

    public UUID getSender() { return sender; }
    public String getSenderName() { return senderName; }
    public UUID getTarget() { return target; }
    public String getTargetName() { return targetName; }
    public RequestType getType() { return type; }
    public long getExpiryTick() { return expiryTick; }
    public Object getExtraData() { return extraData; }

    public boolean isExpired(long currentTick) {
        return currentTick >= expiryTick;
    }
}

