package com.crazygirlfriend.block;

import com.crazygirlfriend.CrazyGirlfriendMod;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block COUPLE_PICNIC_BLANKET = registerBlock("couple_picnic_blanket", new PicnicBlanketBlock());
    public static final Block COUPLE_SOFA = registerBlock("couple_sofa", new CoupleSofaBlock());
    public static final Block COUPLE_DOUBLE_BED = registerBlock("couple_double_bed", new CoupleDoubleBedBlock());
    public static final Block COUPLE_SWING = registerBlock("couple_swing", new CoupleSwingBlock());
    public static final Block TEDDY_BEAR = registerBlock("teddy_bear", new TeddyBearBlock());
    public static final Block COZY_FIREPLACE = registerBlock("cozy_fireplace", new CozyFireplaceBlock());
    public static final Block ROMANTIC_CHANDELIER = registerBlock("romantic_chandelier", new RomanticChandelierBlock());
    public static final Block ROMANTIC_DINING_TABLE = registerBlock("romantic_dining_table", new RomanticDiningTableBlock());

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(CrazyGirlfriendMod.MOD_ID, name), block);
    }

    public static void register() {
        CrazyGirlfriendMod.LOGGER.info("Registered Crazy Girlfriend couple furniture & teddy blocks");
    }
}
