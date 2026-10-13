package com.crazygirlfriend.item;

import com.crazygirlfriend.CrazyGirlfriendMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
    public static final Item TEDDY_BEAR = registerItem("teddy_bear", new TeddyBearItem(new Item.Settings().maxCount(1)));
    public static final Item LOVE_LETTER = registerItem("love_letter", new LoveLetterItem(new Item.Settings().maxCount(16)));

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(CrazyGirlfriendMod.MOD_ID, "general"));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CrazyGirlfriendMod.MOD_ID, name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
            .icon(() -> new ItemStack(RED_ROSE))
            .displayName(Text.translatable("itemGroup.crazygirlfriend.general"))
            .entries((context, entries) -> {
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
            })
            .build()
        );
    }
}

