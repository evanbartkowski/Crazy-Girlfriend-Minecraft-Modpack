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

    // Expansion fields
    private int crazyMeter = 0; // 0 to 100
    private int affectionXp = 0; // 0+
    private RelationshipMood mood1 = RelationshipMood.HAPPY;
    private RelationshipMood mood2 = RelationshipMood.HAPPY;
    private String customTitle = "Sweethearts";

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

    public int getCrazyMeter() { return crazyMeter; }
    public void setCrazyMeter(int crazyMeter) {
        this.crazyMeter = Math.max(0, Math.min(100, crazyMeter));
    }
    public void addCrazyMeter(int delta) {
        setCrazyMeter(this.crazyMeter + delta);
    }
    public CrazyTier getCrazyTier() {
        return CrazyTier.fromMeter(crazyMeter);
    }

    public int getAffectionXp() { return affectionXp; }
    public void setAffectionXp(int affectionXp) { this.affectionXp = Math.max(0, affectionXp); }
    public void addAffectionXp(int delta) { setAffectionXp(this.affectionXp + delta); }
    public int getAffectionLevel() { return (affectionXp / 100) + 1; }

    public RelationshipMood getMood(UUID player) {
        if (player.equals(player1)) return mood1;
        if (player.equals(player2)) return mood2;
        return RelationshipMood.HAPPY;
    }
    public void setMood(UUID player, RelationshipMood mood) {
        if (player.equals(player1)) this.mood1 = mood;
        if (player.equals(player2)) this.mood2 = mood;
    }

    public String getCustomTitle() { return customTitle; }
    public void setCustomTitle(String customTitle) { this.customTitle = customTitle; }

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

    public UUID getOtherPlayerUuid(UUID player) {
        return getPartner(player);
    }

    public UUID getPlayer1Uuid() {
        return player1;
    }

    public UUID getPlayer2Uuid() {
        return player2;
    }

    public String getPlayer1Name() {
        return name1;
    }

    public String getPlayer2Name() {
        return name2;
    }

    public void addAffection(int delta) {
        addAffectionXp(delta);
    }

    private final java.util.Set<UUID> sharedPets = new java.util.HashSet<>();

    public void addSharedPet(UUID petUuid) {
        sharedPets.add(petUuid);
    }

    public boolean isPetShared(UUID petUuid) {
        return sharedPets.contains(petUuid);
    }

    public java.util.Set<UUID> getSharedPets() {
        return sharedPets;
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
        nbt.putInt("CrazyMeter", crazyMeter);
        nbt.putInt("AffectionXp", affectionXp);
        nbt.putString("Mood1", mood1.name());
        nbt.putString("Mood2", mood2.name());
        nbt.putString("CustomTitle", customTitle != null ? customTitle : "Sweethearts");
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
        if (nbt.contains("CrazyMeter")) {
            rel.setCrazyMeter(nbt.getInt("CrazyMeter"));
        }
        if (nbt.contains("AffectionXp")) {
            rel.setAffectionXp(nbt.getInt("AffectionXp"));
        }
        if (nbt.contains("Mood1")) {
            try { rel.mood1 = RelationshipMood.valueOf(nbt.getString("Mood1")); } catch (Exception ignored) {}
        }
        if (nbt.contains("Mood2")) {
            try { rel.mood2 = RelationshipMood.valueOf(nbt.getString("Mood2")); } catch (Exception ignored) {}
        }
        if (nbt.contains("CustomTitle")) {
            rel.setCustomTitle(nbt.getString("CustomTitle"));
        }
        return rel;
    }
}
