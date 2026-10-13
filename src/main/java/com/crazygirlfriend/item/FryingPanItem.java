package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FryingPanItem extends SwordItem {
    public FryingPanItem(Settings settings) {
        super(ToolMaterials.IRON, 3, -2.4f, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.BONK, SoundCategory.PLAYERS, 1.4f, 1.5f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.2, target.getZ(), 20, 0.4, 0.4, 0.4, 0.2);
            }
            target.takeKnockback(0.7, -attacker.getRotationVec(1.0f).x, -attacker.getRotationVec(1.0f).z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.BONK_VICTIM, 35);
            }
            if (attacker instanceof ServerPlayerEntity playerAttacker) {
                PlayerAnimationTracker.get().startSingleAnimation(playerAttacker, AnimationType.BONK_ATTACKER, 25);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Legendary Cast-Iron Frying Pan").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(Text.literal("Produces an ear-splitting BONK upon impact!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

