package com.crazygirlfriend.relationship;

import net.minecraft.nbt.NbtCompound;
import java.util.UUID;

public class Relationship {
    private final UUID player1;
    private final UUID player2;
    private String name1;
    private String name2;
    private RelationshipType type;
    private long formedTimestamp;
    private boolean autoAccept;

    public Relationship(UUID player1, String name1, UUID player2, String name2, RelationshipType type, long formedTimestamp) {
        this.player1 = player1;
        this.name1 = name1;
        this.player2 = player2;
        this.name2 = name2;
        this.type = type;
        this.formedTimestamp = formedTimestamp;
        this.autoAccept = false;
    }

    public UUID getPlayer1() { return player1; }
    public UUID getPlayer2() { return player2; }
    public String getName1() { return name1; }
    public String getName2() { return name2; }
    public void setName1(String name1) { this.name1 = name1; }
    public void setName2(String name2) { this.name2 = name2; }
    public RelationshipType getType() { return type; }
    public void setType(RelationshipType type) { this.type = type; }
    public long getFormedTimestamp() { return formedTimestamp; }
    public boolean isAutoAccept() { return autoAccept; }
    public void setAutoAccept(boolean autoAccept) { this.autoAccept = autoAccept; }

    public UUID getPartner(UUID player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        return null;
    }

    public String getPartnerName(UUID player) {
        if (player.equals(player1)) return name2;
        if (player.equals(player2)) return name1;
        return "";
    }

    public boolean involves(UUID player) {
        return player.equals(player1) || player.equals(player2);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putUuid("Player1", player1);
        nbt.putUuid("Player2", player2);
        nbt.putString("Name1", name1);
        nbt.putString("Name2", name2);
        nbt.putString("Type", type.name());
        nbt.putLong("FormedTimestamp", formedTimestamp);
        nbt.putBoolean("AutoAccept", autoAccept);
        return nbt;
    }

    public static Relationship readNbt(NbtCompound nbt) {
        UUID p1 = nbt.getUuid("Player1");
        UUID p2 = nbt.getUuid("Player2");
        String n1 = nbt.getString("Name1");
        String n2 = nbt.getString("Name2");
        RelationshipType t = RelationshipType.valueOf(nbt.getString("Type"));
        long time = nbt.getLong("FormedTimestamp");
        Relationship rel = new Relationship(p1, n1, p2, n2, t, time);
        if (nbt.contains("AutoAccept")) {
            rel.setAutoAccept(nbt.getBoolean("AutoAccept"));
        }
        return rel;
    }
}

