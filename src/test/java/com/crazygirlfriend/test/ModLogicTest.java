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

    @Test
    public void testFacialExpressions() {
        assertEquals(16, com.crazygirlfriend.facial.FacialExpression.values().length);
        for (com.crazygirlfriend.facial.FacialExpression expr : com.crazygirlfriend.facial.FacialExpression.values()) {
            assertNotNull(expr.getDisplayName());
            assertFalse(expr.getDisplayName().isEmpty());
        }
    }

    @Test
    public void testAnimationCombos() {
        // Test combos from HUG
        var branches = com.crazygirlfriend.combo.AnimationComboRegistry.getBranches(AnimationType.HUG);
        assertFalse(branches.isEmpty());
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.HUG, AnimationType.ROMANTIC_DIP_KISS));
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.HUG, AnimationType.SPINNING_HUG));
        assertFalse(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.HUG, AnimationType.DIVORCE));

        // Test combos from KISS
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.KISS, AnimationType.HEAD_BUMP_KISS));
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.KISS, AnimationType.MAKE_OUT));

        // Test combos from SLAP
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.SLAP_ATTACKER, AnimationType.RECONCILIATION_HUG));
        assertTrue(com.crazygirlfriend.combo.AnimationComboRegistry.canCombo(AnimationType.SLAP_VICTIM, AnimationType.FAKE_COLLAPSE));

        // Test branch metadata
        var dipBranch = com.crazygirlfriend.combo.AnimationComboRegistry.getBranch(AnimationType.HUG, AnimationType.ROMANTIC_DIP_KISS);
        assertNotNull(dipBranch);
        assertEquals("1", dipBranch.hotkey);
        assertEquals(com.crazygirlfriend.combo.AnimationComboRegistry.ComboCategory.ROMANTIC, dipBranch.category);
    }

    @Test
    public void testAnimationVariantsAndHelpers() {
        assertTrue(AnimationType.values().length >= 65, "Expected at least 65 animation variants, found: " + AnimationType.values().length);

        // Test variant selectors
        java.util.Random rnd = new java.util.Random(12345);
        AnimationType hugVar = AnimationType.getHugVariant(rnd);
        assertNotNull(hugVar);
        assertTrue(hugVar.name().contains("HUG"));

        AnimationType kissVar = AnimationType.getKissVariant(rnd);
        assertNotNull(kissVar);
        assertTrue(kissVar.name().contains("KISS"));

        AnimationType slapReaction = AnimationType.getSlapReactionVariant(rnd);
        assertNotNull(slapReaction);

        // Test variant durations
        assertTrue(AnimationType.BEAR_HUG.getDefaultDuration() > 0);
        assertTrue(AnimationType.SPINNING_HUG.getDefaultDuration() > 0);
        assertTrue(AnimationType.ROMANTIC_DIP_KISS.getDefaultDuration() > 0);
        assertTrue(AnimationType.FISH_SLAP_ATTACKER.getDefaultDuration() > 0);
    }

    @Test
    public void testFacialExpressionManagerLifecycle() {
        UUID testPlayer = UUID.randomUUID();
        var mgr = com.crazygirlfriend.facial.FacialExpressionManager.get();

        assertEquals(com.crazygirlfriend.facial.FacialExpression.NONE, mgr.getExpression(testPlayer));

        mgr.setExpression(testPlayer, com.crazygirlfriend.facial.FacialExpression.BLUSHING, 3);
        assertEquals(com.crazygirlfriend.facial.FacialExpression.BLUSHING, mgr.getExpression(testPlayer));

        mgr.tick();
        assertEquals(com.crazygirlfriend.facial.FacialExpression.BLUSHING, mgr.getExpression(testPlayer));

        mgr.tick();
        mgr.tick();
        // After 3 ticks, should expire to NONE
        assertEquals(com.crazygirlfriend.facial.FacialExpression.NONE, mgr.getExpression(testPlayer));
    }

    @Test
    public void testCinematicCameraManagerLogic() {
        var cam = com.crazygirlfriend.client.camera.CinematicCameraManager.get();
        assertNotNull(cam);

        cam.triggerScreenShake(5, 1.5f);
        assertTrue(Math.abs(cam.getShakeOffset(0.0f)) >= 0.0f);

        for (int i = 0; i < 6; i++) {
            cam.tick();
        }
        assertEquals(0.0f, cam.getShakeOffset(0.0f));
    }

    @Test
    public void testSeasonProgressionAndColors() {
        assertEquals(com.crazygirlfriend.season.Season.SUMMER, com.crazygirlfriend.season.Season.SPRING.getNext());
        assertEquals(com.crazygirlfriend.season.Season.AUTUMN, com.crazygirlfriend.season.Season.SUMMER.getNext());
        assertEquals(com.crazygirlfriend.season.Season.WINTER, com.crazygirlfriend.season.Season.AUTUMN.getNext());
        assertEquals(com.crazygirlfriend.season.Season.SPRING, com.crazygirlfriend.season.Season.WINTER.getNext());

        assertNotNull(com.crazygirlfriend.season.Season.SPRING.getEmoji());
        assertNotNull(com.crazygirlfriend.season.Season.AUTUMN.getDescription());
        assertTrue(com.crazygirlfriend.season.Season.AUTUMN.getFoliageColorTint() != 0);

        var sm = com.crazygirlfriend.season.SeasonManager.get();
        assertNotNull(sm);
        assertEquals(com.crazygirlfriend.season.Season.SPRING, sm.getClientSeason());
        sm.updateClientState(com.crazygirlfriend.season.Season.AUTUMN, 0.5f, 3, 7, false);
        assertEquals(com.crazygirlfriend.season.Season.AUTUMN, sm.getClientSeason());
        assertEquals(3, sm.getClientDay());
        assertEquals(7, sm.getClientTotalDays());
        assertFalse(sm.isClientLocked());

        // Color blending
        int origColor = 0x55AA55;
        int blendedFoliage = sm.getBlendedFoliageColor(null, null, origColor);
        assertTrue(blendedFoliage != 0);
        int blendedGrass = sm.getBlendedGrassColor(null, null, origColor);
        assertTrue(blendedGrass != 0);
    }

    @Test
    public void testWorldEventsLifecycle() {
        for (var evt : com.crazygirlfriend.event.world.WorldEventType.values()) {
            assertNotNull(evt.getDisplayName());
            assertNotNull(evt.getEmoji());
        }

        var wem = com.crazygirlfriend.event.world.WorldEventManager.get();
        assertNotNull(wem);
        wem.updateClientEvent(com.crazygirlfriend.event.world.WorldEventType.AURORA_NIGHT, 200, 1.0f);
        assertTrue(wem.isEventActive(com.crazygirlfriend.event.world.WorldEventType.AURORA_NIGHT));
        assertEquals(com.crazygirlfriend.event.world.WorldEventType.AURORA_NIGHT, wem.getClientEvent());
        assertEquals(1.0f, wem.getClientIntensity());
    }

    @Test
    public void testScenicLocationManagerLogic() {
        var slm = com.crazygirlfriend.world.gen.ScenicLocationManager.get();
        assertNotNull(slm);

        net.minecraft.util.math.BlockPos p1 = new net.minecraft.util.math.BlockPos(100, 70, 100);
        net.minecraft.util.math.BlockPos p2 = new net.minecraft.util.math.BlockPos(500, 80, 500);
        slm.registerScenicSpot(p1);
        slm.registerScenicSpot(p2);

        net.minecraft.util.math.BlockPos query = new net.minecraft.util.math.BlockPos(110, 70, 105);
        net.minecraft.util.math.BlockPos nearest = slm.getNearestScenicSpot(query);
        assertEquals(p1, nearest);
    }

    @Test
    public void testPhotoModeEnums() {
        var filter = com.crazygirlfriend.client.gui.PhotoModeScreen.FilterType.STANDARD;
        assertNotNull(filter.getNext());
        assertNotEquals(filter, filter.getNext());

        var guide = com.crazygirlfriend.client.gui.PhotoModeScreen.AspectRatioGuide.NONE;
        assertNotNull(guide.getNext());
        assertNotEquals(guide, guide.getNext());

        var pose = com.crazygirlfriend.client.gui.PhotoModeScreen.PhotoPose.DEFAULT;
        assertNotNull(pose.getNext());
        assertNotEquals(pose, pose.getNext());
    }

    @Test
    public void testSleepTransformMatrix() {
        org.joml.Matrix4f mat = new org.joml.Matrix4f();
        // Vanilla LivingEntityRenderer.setupTransforms for sleeping:
        // direction = North (getYaw = 180)
        mat.rotate((float) Math.toRadians(180), 0, 1, 0); // POSITIVE_Y(180)
        mat.rotate((float) Math.toRadians(90), 0, 0, 1);  // POSITIVE_Z(90)
        mat.rotate((float) Math.toRadians(270), 0, 1, 0); // POSITIVE_Y(270)

        // In LivingEntityRenderer, before render, model Y is -1 (upwards) and Z is +1 (forward/facing)
        org.joml.Vector3f up = new org.joml.Vector3f(0, 1, 0); // player's standing "up" (head direction)
        mat.transformDirection(up);
        System.out.println("Head direction in world space: " + up);

        org.joml.Vector3f face = new org.joml.Vector3f(0, 0, 1); // player's facing direction
        mat.transformDirection(face);
        System.out.println("Face direction in world space: " + face);

        // Now test local axes
        org.joml.Vector3f localX = new org.joml.Vector3f(1, 0, 0);
        mat.transformDirection(localX);
        System.out.println("Local X in world space: " + localX);

        org.joml.Vector3f localY = new org.joml.Vector3f(0, 1, 0);
        mat.transformDirection(localY);
        System.out.println("Local Y in world space: " + localY);

        // Test all 4 directions: North (180), South (0), West (90), East (270)
        float[] yaws = {0f, 90f, 180f, 270f};
        String[] dirNames = {"South", "West", "North", "East"};
        for (int i = 0; i < yaws.length; i++) {
            org.joml.Matrix4f m = new org.joml.Matrix4f();
            m.rotate((float) Math.toRadians(yaws[i]), 0, 1, 0);
            m.rotate((float) Math.toRadians(90), 0, 0, 1);
            m.rotate((float) Math.toRadians(270), 0, 1, 0);

            // Local Y axis (head-to-toe)
            org.joml.Vector3f spine = new org.joml.Vector3f(0, 1, 0);
            m.transformDirection(spine);

            // Shift along local X (across bed)
            org.joml.Vector3f across = new org.joml.Vector3f(1, 0, 0);
            m.transformDirection(across);

            // Verify spine is horizontal (Y component is 0)
            assertEquals(0.0f, spine.y, 0.001f, "Spine Y should be 0 for " + dirNames[i]);
            // Verify across is horizontal (Y component is 0)
            assertEquals(0.0f, across.y, 0.001f, "Across Y should be 0 for " + dirNames[i]);
            // Verify spine and across are orthogonal
            assertEquals(0.0f, spine.dot(across), 0.001f, "Spine and across should be orthogonal for " + dirNames[i]);
        }
    }

    @Test
    public void testCoSleepingClientTrackerLogic() {
        var tracker = com.crazygirlfriend.client.bed.CoSleepingClientTracker.get();
        assertNotNull(tracker);

        UUID p1 = UUID.randomUUID();
        assertFalse(tracker.isFlipped(p1));
        tracker.toggleFlip(p1);
        assertTrue(tracker.isFlipped(p1));
        tracker.toggleFlip(p1);
        assertFalse(tracker.isFlipped(p1));

        tracker.setFlipped(p1, true);
        assertTrue(tracker.isFlipped(p1));
        tracker.setFlipped(p1, false);
        assertFalse(tracker.isFlipped(p1));
    }
}


