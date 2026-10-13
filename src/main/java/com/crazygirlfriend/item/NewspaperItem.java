package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NewspaperItem extends Item {
    public NewspaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SLAP, SoundCategory.PLAYERS, 1.2f, 1.4f);
            target.takeKnockback(0.4, -attacker.getRotationVec(1.0f).x, -attacker.getRotationVec(1.0f).z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.SLAP_VICTIM, 20);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Rolled-Up Daily Newspaper").formatted(Formatting.GRAY, Formatting.BOLD));
        tooltip.add(Text.literal("Playful discipline swatter for unruly partners!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
