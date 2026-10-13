package com.crazygirlfriend.item;

import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

public class TeddyBearItem extends Item {
    public TeddyBearItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HEART, user.getX(), user.getY() + 1.2, user.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
            serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 0.8f, 1.4f);
            user.sendMessage(Text.literal("🧸 You squeeze the teddy bear! So warm and cuddly.").formatted(Formatting.LIGHT_PURPLE), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A huggable plushie to show someone you care.").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

