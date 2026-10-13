package com.crazygirlfriend.item;

import com.crazygirlfriend.entity.WaterBalloonEntity;
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

public class WaterBalloonItem extends Item {
    public WaterBalloonItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f);

        if (!world.isClient) {
            WaterBalloonEntity entity = new WaterBalloonEntity(world, user);
            entity.setItem(itemStack);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.3f, 1.0f);
            world.spawnEntity(entity);
        }

        user.getItemCooldownManager().set(this, 10);
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Colorful Water Balloon").formatted(Formatting.AQUA, Formatting.BOLD));
        tooltip.add(Text.literal("Splashes friends with water and causes a wet shivering shake!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
