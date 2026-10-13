package com.crazygirlfriend.item;

import com.crazygirlfriend.CrazyGirlfriendMod;
import com.crazygirlfriend.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    // Original Items
    public static final Item RED_ROSE = registerItem("red_rose", new RedRoseItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final Item DIAMOND_RING = registerItem("diamond_ring", new DiamondRingItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item FLOWER_BOUQUET = registerItem("flower_bouquet", new FlowerBouquetItem(new Item.Settings().maxCount(16)));
    public static final Item PASTEL_BOUQUET = registerItem("pastel_bouquet", new FlowerBouquetItem(new Item.Settings().maxCount(16)));
    public static final Item SUN_BOUQUET = registerItem("sun_bouquet", new FlowerBouquetItem(new Item.Settings().maxCount(16)));
    public static final Item THROWABLE_SLIPPER = registerItem("throwable_slipper", new SlipperItem(new Item.Settings().maxCount(16)));
    public static final Item MAKEUP_KIT = registerItem("makeup_kit", new MakeupKitItem(new Item.Settings().maxCount(1)));
    public static final Item MAKEUP_REMOVER = registerItem("makeup_remover", new MakeupRemoverItem(new Item.Settings().maxCount(16)));
    public static final Item PADDLE = registerItem("paddle", new PaddleItem(new Item.Settings().maxCount(1)));
    public static final Item PLAYER_LEASH = registerItem("player_leash", new PlayerLeashItem(new Item.Settings().maxCount(1)));
    public static final Item CHOCOLATE_BOX = registerItem("chocolate_box", new ChocolateBoxItem(new Item.Settings().maxCount(64)));
    public static final Item TEDDY_BEAR = registerItem("teddy_bear", new TeddyBearItem(ModBlocks.TEDDY_BEAR, new Item.Settings().maxCount(1)));
    public static final Item LOVE_LETTER = registerItem("love_letter", new LoveLetterItem(new Item.Settings().maxCount(16)));

    // Expansion: Comedy Weapons & Prank Items
    public static final Item FRYING_PAN = registerItem("frying_pan", new FryingPanItem(new Item.Settings().maxCount(1)));
    public static final Item BOOMERANG_SLIPPER = registerItem("boomerang_slipper", new BoomerangSlipperItem(new Item.Settings().maxCount(1)));
    public static final Item SQUEAKY_HAMMER = registerItem("squeaky_hammer", new SqueakyHammerItem(new Item.Settings().maxCount(1)));
    public static final Item WATER_BALLOON = registerItem("water_balloon", new WaterBalloonItem(new Item.Settings().maxCount(16)));
    public static final Item PILLOW = registerItem("pillow", new PillowItem(new Item.Settings().maxCount(1)));
    public static final Item NEWSPAPER = registerItem("newspaper", new NewspaperItem(new Item.Settings().maxCount(1)));
    public static final Item LOVE_POTION = registerItem("love_potion", new LovePotionItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));

    // Expansion: Romance, Wedding, Custody & Lifestyle
    public static final Item PARTNER_COMPASS = registerItem("partner_compass", new PartnerCompassItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item COUPLE_CAMERA = registerItem("couple_camera", new CoupleCameraItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item PHOTOGRAPH = registerItem("photograph", new PhotographItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final Item PHOTO_ALBUM = registerItem("photo_album", new PhotoAlbumItem(new Item.Settings().maxCount(16)));
    public static final Item SHARED_COLLAR = registerItem("shared_collar", new SharedCollarItem(new Item.Settings().maxCount(16)));
    public static final Item WEDDING_ALTAR = registerItem("wedding_altar", new WeddingAltarItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item WEDDING_CAKE = registerItem("wedding_cake", new WeddingCakeItem(new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)));
    public static final Item DIVORCE_PAPERS = registerItem("divorce_papers", new DivorcePapersItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item COUPLE_HOODIE = registerItem("couple_hoodie", new CoupleClothingItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    // Expansion: Couple Furniture
    public static final Item COUPLE_SOFA = registerItem("couple_sofa", new CoupleFurnitureItem(ModBlocks.COUPLE_SOFA, CoupleFurnitureItem.FurnitureType.SOFA, new Item.Settings().maxCount(16)));
    public static final Item COUPLE_DOUBLE_BED = registerItem("couple_double_bed", new CoupleFurnitureItem(ModBlocks.COUPLE_DOUBLE_BED, CoupleFurnitureItem.FurnitureType.DOUBLE_BED, new Item.Settings().maxCount(16)));
    public static final Item COUPLE_SWING = registerItem("couple_swing", new CoupleFurnitureItem(ModBlocks.COUPLE_SWING, CoupleFurnitureItem.FurnitureType.SWING, new Item.Settings().maxCount(16)));
    public static final Item COUPLE_PICNIC_BLANKET = registerItem("couple_picnic_blanket", new CoupleFurnitureItem(ModBlocks.COUPLE_PICNIC_BLANKET, CoupleFurnitureItem.FurnitureType.PICNIC_BLANKET, new Item.Settings().maxCount(16)));

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(CrazyGirlfriendMod.MOD_ID, "general"));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CrazyGirlfriendMod.MOD_ID, name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
            .icon(() -> new ItemStack(RED_ROSE))
            .displayName(Text.translatable("itemGroup.crazygirlfriend.general"))
            .entries((context, entries) -> {
                // Original
                entries.add(RED_ROSE);
                entries.add(DIAMOND_RING);
                entries.add(FLOWER_BOUQUET);
                entries.add(PASTEL_BOUQUET);
                entries.add(SUN_BOUQUET);
                entries.add(THROWABLE_SLIPPER);
                entries.add(MAKEUP_KIT);
                entries.add(MAKEUP_REMOVER);
                entries.add(PADDLE);
                entries.add(PLAYER_LEASH);
                entries.add(CHOCOLATE_BOX);
                entries.add(TEDDY_BEAR);
                entries.add(LOVE_LETTER);

                // Prank & Weapons
                entries.add(FRYING_PAN);
                entries.add(BOOMERANG_SLIPPER);
                entries.add(SQUEAKY_HAMMER);
                entries.add(WATER_BALLOON);
                entries.add(PILLOW);
                entries.add(NEWSPAPER);
                entries.add(LOVE_POTION);

                // Romance & Lifestyle
                entries.add(PARTNER_COMPASS);
                entries.add(COUPLE_CAMERA);
                entries.add(PHOTOGRAPH);
                entries.add(PHOTO_ALBUM);
                entries.add(SHARED_COLLAR);
                entries.add(WEDDING_ALTAR);
                entries.add(WEDDING_CAKE);
                entries.add(DIVORCE_PAPERS);
                entries.add(COUPLE_HOODIE);

                // Furniture
                entries.add(COUPLE_SOFA);
                entries.add(COUPLE_DOUBLE_BED);
                entries.add(COUPLE_SWING);
                entries.add(COUPLE_PICNIC_BLANKET);
            })
            .build()
        );
    }
}
