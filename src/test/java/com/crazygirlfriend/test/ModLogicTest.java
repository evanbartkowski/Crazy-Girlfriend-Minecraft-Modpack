package com.crazygirlfriend.test;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.event.RelationshipEventRegistry;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.event.MinigameManager;
import com.crazygirlfriend.relationship.CrazyTier;
import com.crazygirlfriend.relationship.PendingRequest;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipMood;
import com.crazygirlfriend.relationship.RelationshipType;
import com.crazygirlfriend.relationship.RequestType;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ModLogicTest {

    @Test
    public void testRelationshipCreationAndPartners() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        String name1 = "Alice";
        String name2 = "Bob";
        long timestamp = 1700000000000L;

        Relationship rel = new Relationship(p1, name1, p2, name2, RelationshipType.DATING, timestamp);

        assertTrue(rel.involves(p1));
        assertTrue(rel.involves(p2));
        assertFalse(rel.involves(UUID.randomUUID()));

        assertEquals(p2, rel.getPartner(p1));
        assertEquals(p1, rel.getPartner(p2));
        assertNull(rel.getPartner(UUID.randomUUID()));

        assertEquals("Bob", rel.getPartnerName(p1));
        assertEquals("Alice", rel.getPartnerName(p2));

        assertEquals(RelationshipType.DATING, rel.getType());
        assertFalse(rel.isAutoAccept());

        rel.setType(RelationshipType.MARRIED);
        assertEquals(RelationshipType.MARRIED, rel.getType());

        rel.setAutoAccept(true);
        assertTrue(rel.isAutoAccept());
    }

    @Test
    public void testRelationshipExpansionFields() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        Relationship rel = new Relationship(p1, "Alice", p2, "Bob", RelationshipType.DATING, 1700000000000L);

        // Crazy meter
        assertEquals(0, rel.getCrazyMeter());
        rel.addCrazyMeter(50);
        assertEquals(50, rel.getCrazyMeter());
        rel.addCrazyMeter(100);
        assertEquals(100, rel.getCrazyMeter()); // Clamped to 100
        rel.addCrazyMeter(-150);
        assertEquals(0, rel.getCrazyMeter()); // Clamped to 0

        // Affection
        assertEquals(0, rel.getAffectionXp());
        assertEquals(1, rel.getAffectionLevel());
        rel.addAffection(250);
        assertEquals(250, rel.getAffectionXp());
        assertEquals(3, rel.getAffectionLevel());

        // Moods
        assertEquals(RelationshipMood.HAPPY, rel.getMood(p1));
        rel.setMood(p1, RelationshipMood.LOVING);
        rel.setMood(p2, RelationshipMood.FURIOUS);
        assertEquals(RelationshipMood.LOVING, rel.getMood(p1));
        assertEquals(RelationshipMood.FURIOUS, rel.getMood(p2));

        // Custom Title
        rel.setCustomTitle("Soulmates for Eternity");
        assertEquals("Soulmates for Eternity", rel.getCustomTitle());

        // NBT serialization
        NbtCompound tag = new NbtCompound();
        rel.writeNbt(tag);

        Relationship restored = Relationship.readNbt(tag);
        assertEquals(rel.getCrazyMeter(), restored.getCrazyMeter());
        assertEquals(rel.getAffectionXp(), restored.getAffectionXp());
        assertEquals(rel.getMood(p1), restored.getMood(p1));
        assertEquals(rel.getMood(p2), restored.getMood(p2));
        assertEquals(rel.getCustomTitle(), restored.getCustomTitle());
    }

    @Test
    public void testCrazyTiers() {
        assertEquals(CrazyTier.SWEETHEART, CrazyTier.fromMeter(0));
        assertEquals(CrazyTier.SWEETHEART, CrazyTier.fromMeter(24));
        assertEquals(CrazyTier.SUSPICIOUS, CrazyTier.fromMeter(25));
        assertEquals(CrazyTier.SUSPICIOUS, CrazyTier.fromMeter(49));
        assertEquals(CrazyTier.DRAMA_QUEEN, CrazyTier.fromMeter(50));
        assertEquals(CrazyTier.DRAMA_QUEEN, CrazyTier.fromMeter(74));
        assertEquals(CrazyTier.FURIOUS, CrazyTier.fromMeter(75));
        assertEquals(CrazyTier.FURIOUS, CrazyTier.fromMeter(94));
        assertEquals(CrazyTier.TOTAL_CHAOS, CrazyTier.fromMeter(95));
        assertEquals(CrazyTier.TOTAL_CHAOS, CrazyTier.fromMeter(100));
    }

    @Test
    public void testRelationshipMoods() {
        assertEquals(10, RelationshipMood.values().length);
        for (RelationshipMood mood : RelationshipMood.values()) {
            assertNotNull(mood.getDisplayName());
            assertTrue(mood.getColor() > 0);
            assertFalse(mood.getDisplayName().isEmpty());
        }
    }

    @Test
    public void testAnimationCount() {
        // Must implement at least 50 unique animations
        assertTrue(AnimationType.values().length >= 50, "Expected at least 50 animations, found: " + AnimationType.values().length);
    }

    @Test
    public void testMinigamesAndEvents() {
        // At least 10 minigames
        assertTrue(MinigameManager.get().getGames().length >= 10, "Expected at least 10 minigames");

        // At least 75 random events
        assertTrue(RelationshipEventRegistry.getEvents().size() >= 75, "Expected at least 75 events");
    }

    @Test
    public void testPlayerMakeupData() {
        UUID player = UUID.randomUUID();
        PlayerMakeupData makeup = new PlayerMakeupData(player);

        assertTrue(makeup.isEmpty());

        makeup.setLipstickStyle(2);
        makeup.setBlushStyle(1);
        makeup.setEyelinerStyle(1);
        makeup.setFaceMarking(3);

        assertFalse(makeup.isEmpty());
        assertEquals(2, makeup.getLipstickStyle());
        assertEquals(1, makeup.getBlushStyle());
        assertEquals(1, makeup.getEyelinerStyle());
        assertEquals(3, makeup.getFaceMarking());

        NbtCompound tag = new NbtCompound();
        makeup.writeNbt(tag);

        PlayerMakeupData restored = PlayerMakeupData.readNbt(tag);
        assertEquals(makeup.getPlayerUuid(), restored.getPlayerUuid());
        assertEquals(makeup.getLipstickStyle(), restored.getLipstickStyle());
        assertEquals(makeup.getBlushStyle(), restored.getBlushStyle());
        assertEquals(makeup.getEyelinerStyle(), restored.getEyelinerStyle());
        assertEquals(makeup.getFaceMarking(), restored.getFaceMarking());

        makeup.clear();
        assertTrue(makeup.isEmpty());
    }

    @Test
    public void testPendingRequestExpiration() {
        UUID sender = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        PendingRequest req = new PendingRequest(sender, "Sender", target, "Target", RequestType.HUG, 100L, null);

        assertFalse(req.isExpired(99L));
        assertTrue(req.isExpired(100L));
        assertTrue(req.isExpired(105L));
    }

    @Test
    public void testConfigDefaults() {
        ModConfig config = ModConfig.get();
        assertNotNull(config);
        assertTrue(config.allowLeashRestrictions);
        assertTrue(config.leashMaxDistance > 5.0);
        assertTrue(config.interactionTimeoutSeconds > 0);
        assertTrue(config.slipperDamage > 0);
    }

    @Test
    public void testAnimationDurations() {
        assertTrue(AnimationType.HUG.getDefaultDuration() > 0);
        assertTrue(AnimationType.KISS.getDefaultDuration() > 0);
        assertTrue(AnimationType.SLAP_ATTACKER.getDefaultDuration() > 0);
        assertTrue(AnimationType.MAKE_LOVE.getDefaultDuration() > 0);
        assertEquals(0, AnimationType.PIGGYBACK.getDefaultDuration());
        assertEquals(0, AnimationType.LEASH_SIT.getDefaultDuration());
    }
}
