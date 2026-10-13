package com.crazygirlfriend.sound;

import com.crazygirlfriend.CrazyGirlfriendMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier ROMANTIC_CHIME_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "romantic_chime");
    public static final SoundEvent ROMANTIC_CHIME = SoundEvent.of(ROMANTIC_CHIME_ID);

    public static final Identifier SLAP_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "slap");
    public static final SoundEvent SLAP = SoundEvent.of(SLAP_ID);

    public static final Identifier KISS_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "kiss");
    public static final SoundEvent KISS = SoundEvent.of(KISS_ID);

    public static final Identifier WEDDING_BELLS_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "wedding_bells");
    public static final SoundEvent WEDDING_BELLS = SoundEvent.of(WEDDING_BELLS_ID);

    public static final Identifier HEARTBREAK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "heartbreak");
    public static final SoundEvent HEARTBREAK = SoundEvent.of(HEARTBREAK_ID);

    public static final Identifier SPANK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "spank");
    public static final SoundEvent SPANK = SoundEvent.of(SPANK_ID);

    public static final Identifier SLIPPER_HIT_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "slipper_hit");
    public static final SoundEvent SLIPPER_HIT = SoundEvent.of(SLIPPER_HIT_ID);

    public static final Identifier GIGGLE_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "giggle");
    public static final SoundEvent GIGGLE = SoundEvent.of(GIGGLE_ID);

    public static final Identifier JEALOUSY_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "jealousy");
    public static final SoundEvent JEALOUSY = SoundEvent.of(JEALOUSY_ID);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, ROMANTIC_CHIME_ID, ROMANTIC_CHIME);
        Registry.register(Registries.SOUND_EVENT, SLAP_ID, SLAP);
        Registry.register(Registries.SOUND_EVENT, KISS_ID, KISS);
        Registry.register(Registries.SOUND_EVENT, WEDDING_BELLS_ID, WEDDING_BELLS);
        Registry.register(Registries.SOUND_EVENT, HEARTBREAK_ID, HEARTBREAK);
        Registry.register(Registries.SOUND_EVENT, SPANK_ID, SPANK);
        Registry.register(Registries.SOUND_EVENT, SLIPPER_HIT_ID, SLIPPER_HIT);
        Registry.register(Registries.SOUND_EVENT, GIGGLE_ID, GIGGLE);
        Registry.register(Registries.SOUND_EVENT, JEALOUSY_ID, JEALOUSY);
    }
}

