package com.crazygirlfriend.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChocolateBoxItem extends Item {
    public static final FoodComponent FOOD = new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(0.8f)
        .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1), 1.0f)
        .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 0), 1.0f)
        .alwaysEdible()
        .build();

    public ChocolateBoxItem(Settings settings) {
        super(settings.food(FOOD));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Delicious artisanal heart chocolates.").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
        tooltip.add(Text.literal("Grants Regeneration II and Speed I!").formatted(Formatting.YELLOW));
    }
}

