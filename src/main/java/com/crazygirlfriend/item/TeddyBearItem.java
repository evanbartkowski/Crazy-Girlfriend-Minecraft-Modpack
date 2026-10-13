package com.crazygirlfriend.item;

import com.crazygirlfriend.block.ModBlocks;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeddyBearItem extends BlockItem {
    public TeddyBearItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HEART, user.getX(), user.getY() + 1.2, user.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
            serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.4f);
            user.sendMessage(Text.literal("🧸 You squeeze the teddy bear! So warm and cuddly.").formatted(Formatting.LIGHT_PURPLE), true);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A huggable plushie to show someone you care.").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("Can be placed on the ground or furniture as a cute block!").formatted(Formatting.LIGHT_PURPLE));
    }
}
