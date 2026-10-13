package com.crazygirlfriend.test;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.makeup.PlayerMakeupData;
import com.crazygirlfriend.relationship.PendingRequest;
import com.crazygirlfriend.relationship.Relationship;
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
    public void testRelationshipNbtSerialization() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        Relationship original = new Relationship(p1, "PlayerOne", p2, "PlayerTwo", RelationshipType.MARRIED, 123456789L);
        original.setAutoAccept(true);

        NbtCompound tag = new NbtCompound();
        original.writeNbt(tag);

        Relationship restored = Relationship.readNbt(tag);
        assertEquals(original.getPlayer1(), restored.getPlayer1());
        assertEquals(original.getPlayer2(), restored.getPlayer2());
        assertEquals(original.getName1(), restored.getName1());
        assertEquals(original.getName2(), restored.getName2());
        assertEquals(original.getType(), restored.getType());
        assertEquals(original.getFormedTimestamp(), restored.getFormedTimestamp());
        assertEquals(original.isAutoAccept(), restored.isAutoAccept());
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

