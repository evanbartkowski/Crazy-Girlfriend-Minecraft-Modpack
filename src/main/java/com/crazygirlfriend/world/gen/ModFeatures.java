package com.crazygirlfriend.world.gen;

import com.crazygirlfriend.CrazyGirlfriendMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<net.minecraft.world.gen.feature.DefaultFeatureConfig> SCENIC_LOCATION =
            Registry.register(Registries.FEATURE, new Identifier(CrazyGirlfriendMod.MOD_ID, "scenic_location"), ScenicLocationFeature.INSTANCE);

    public static void register() {
        CrazyGirlfriendMod.LOGGER.info("Registered Crazy Girlfriend scenic location features");
    }
}
