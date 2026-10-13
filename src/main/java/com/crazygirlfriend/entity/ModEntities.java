package com.crazygirlfriend.entity;

import com.crazygirlfriend.CrazyGirlfriendMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<SlipperEntity> SLIPPER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CrazyGirlfriendMod.MOD_ID, "slipper"),
        FabricEntityTypeBuilder.<SlipperEntity>create(SpawnGroup.MISC, SlipperEntity::new)
            .dimensions(EntityDimensions.fixed(0.35f, 0.35f))
            .trackRangeBlocks(64)
            .trackedUpdateRate(10)
            .build()
    );

    public static void register() {
        // Classloaded
    }
}

