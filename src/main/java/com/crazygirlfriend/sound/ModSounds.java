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

    public static final Identifier BONK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "bonk");
    public static final SoundEvent BONK = SoundEvent.of(BONK_ID);

    public static final Identifier SQUEAK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "squeak");
    public static final SoundEvent SQUEAK = SoundEvent.of(SQUEAK_ID);

    public static final Identifier BOOMERANG_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "boomerang");
    public static final SoundEvent BOOMERANG = SoundEvent.of(BOOMERANG_ID);

    public static final Identifier PILLOW_HIT_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "pillow_hit");
    public static final SoundEvent PILLOW_HIT = SoundEvent.of(PILLOW_HIT_ID);

    public static final Identifier RECORD_SCRATCH_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "record_scratch");
    public static final SoundEvent RECORD_SCRATCH = SoundEvent.of(RECORD_SCRATCH_ID);

    public static final Identifier SAD_VIOLIN_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "sad_violin");
    public static final SoundEvent SAD_VIOLIN = SoundEvent.of(SAD_VIOLIN_ID);

    public static final Identifier WATER_SPLASH_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "water_splash");
    public static final SoundEvent WATER_SPLASH = SoundEvent.of(WATER_SPLASH_ID);

    public static final Identifier CAMERA_CLICK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "camera_click");
    public static final SoundEvent CAMERA_CLICK = SoundEvent.of(CAMERA_CLICK_ID);

    public static final Identifier BED_BREAK_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "bed_break");
    public static final SoundEvent BED_BREAK = SoundEvent.of(BED_BREAK_ID);

    public static final Identifier CHEER_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "cheer");
    public static final SoundEvent CHEER = SoundEvent.of(CHEER_ID);

    public static final Identifier FISH_SLAP_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "fish_slap");
    public static final SoundEvent FISH_SLAP = SoundEvent.of(FISH_SLAP_ID);

    public static final Identifier WIND_BREEZE_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "wind_breeze");
    public static final SoundEvent WIND_BREEZE = SoundEvent.of(WIND_BREEZE_ID);

    public static final Identifier BIRDS_MORNING_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "birds_morning");
    public static final SoundEvent BIRDS_MORNING = SoundEvent.of(BIRDS_MORNING_ID);

    public static final Identifier CRICKETS_NIGHT_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "crickets_night");
    public static final SoundEvent CRICKETS_NIGHT = SoundEvent.of(CRICKETS_NIGHT_ID);

    public static final Identifier OCEAN_WAVES_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "ocean_waves");
    public static final SoundEvent OCEAN_WAVES = SoundEvent.of(OCEAN_WAVES_ID);

    public static final Identifier SHOOTING_STAR_ID = new Identifier(CrazyGirlfriendMod.MOD_ID, "shooting_star");
    public static final SoundEvent SHOOTING_STAR = SoundEvent.of(SHOOTING_STAR_ID);

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
        Registry.register(Registries.SOUND_EVENT, BONK_ID, BONK);
        Registry.register(Registries.SOUND_EVENT, SQUEAK_ID, SQUEAK);
        Registry.register(Registries.SOUND_EVENT, BOOMERANG_ID, BOOMERANG);
        Registry.register(Registries.SOUND_EVENT, PILLOW_HIT_ID, PILLOW_HIT);
        Registry.register(Registries.SOUND_EVENT, RECORD_SCRATCH_ID, RECORD_SCRATCH);
        Registry.register(Registries.SOUND_EVENT, SAD_VIOLIN_ID, SAD_VIOLIN);
        Registry.register(Registries.SOUND_EVENT, WATER_SPLASH_ID, WATER_SPLASH);
        Registry.register(Registries.SOUND_EVENT, CAMERA_CLICK_ID, CAMERA_CLICK);
        Registry.register(Registries.SOUND_EVENT, BED_BREAK_ID, BED_BREAK);
        Registry.register(Registries.SOUND_EVENT, CHEER_ID, CHEER);
        Registry.register(Registries.SOUND_EVENT, FISH_SLAP_ID, FISH_SLAP);
        Registry.register(Registries.SOUND_EVENT, WIND_BREEZE_ID, WIND_BREEZE);
        Registry.register(Registries.SOUND_EVENT, BIRDS_MORNING_ID, BIRDS_MORNING);
        Registry.register(Registries.SOUND_EVENT, CRICKETS_NIGHT_ID, CRICKETS_NIGHT);
        Registry.register(Registries.SOUND_EVENT, OCEAN_WAVES_ID, OCEAN_WAVES);
        Registry.register(Registries.SOUND_EVENT, SHOOTING_STAR_ID, SHOOTING_STAR);
    }
}
