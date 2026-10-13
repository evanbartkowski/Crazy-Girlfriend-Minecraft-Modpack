package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SqueakyHammerItem extends Item {
    public SqueakyHammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SQUEAK, SoundCategory.PLAYERS, 1.8f, 1.8f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.NOTE, target.getX(), target.getY() + 1.2, target.getZ(), 20, 0.5, 0.5, 0.5, 0.2);
                serverWorld.spawnParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 0.5, target.getZ(), 15, 0.4, 0.2, 0.4, 0.15);
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + 1.0, target.getZ(), 2, 0.1, 0.1, 0.1, 0.0);
            }

            // KNOCKBACK 200: Catapult them into the stratosphere!
            Vec3d look = attacker.getRotationVec(1.0f);
            Vec3d horiz = new Vec3d(look.x, 0, look.z).normalize();
            Vec3d launch = horiz.multiply(8.5).add(0, 3.2, 0);
            target.setVelocity(launch);
            target.velocityModified = true;
            target.takeKnockback(50.0, -look.x, -look.z);

            if (target instanceof ServerPlayerEntity playerTarget) {
                PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.SQUEAK_HAMMER_VICTIM, 35);
                playerTarget.sendMessage(Text.literal("🚀 SQUEAK! Sent into orbit by " + attacker.getName().getString() + "'s Knockback 200 hammer!").formatted(Formatting.YELLOW, Formatting.BOLD), true);
            }
            if (attacker instanceof ServerPlayerEntity playerAttacker) {
                PlayerAnimationTracker.get().startSingleAnimation(playerAttacker, AnimationType.SQUEAK_HAMMER_ATTACKER, 25);
                playerAttacker.sendMessage(Text.literal("🔨 SQUEAK! KNOCKBACK 200 LAUNCH!").formatted(Formatting.GOLD, Formatting.BOLD), true);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Oversized Slapstick Mallet").formatted(Formatting.YELLOW, Formatting.BOLD));
        tooltip.add(Text.literal("⚡ KNOCKBACK 200: Launches targets into outer space with a squeak!").formatted(Formatting.RED, Formatting.BOLD));
    }
}
