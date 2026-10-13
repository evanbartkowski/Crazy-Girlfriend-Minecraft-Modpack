package com.crazygirlfriend.item;

import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoveLetterItem extends Item {
    public LoveLetterItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.7f, 1.3f);
            user.sendMessage(Text.literal("💌 \"Dearest one, every sunrise in this cubic world shines brighter with you by my side. Forever yours.\"").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A sealed parchment with sweet words of affection.").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

