package com.crazygirlfriend.item;

import com.crazygirlfriend.entity.BoomerangSlipperEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoomerangSlipperItem extends Item {
    public BoomerangSlipperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.6f, 0.6f);

        if (!world.isClient) {
            BoomerangSlipperEntity entity = new BoomerangSlipperEntity(world, user);
            entity.setItem(itemStack);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.4f, 1.0f);
            world.spawnEntity(entity);
        }

        user.getItemCooldownManager().set(this, 25);
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Boomerang Slipper").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        tooltip.add(Text.literal("Hurls towards friends and curves right back to you!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
