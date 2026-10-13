package com.crazygirlfriend.item;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaddleItem extends Item {
    public PaddleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            user.getItemCooldownManager().set(this, 15);

            if (entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity sender) {
                // Attacker swings paddle down, victim bends over forward
                PlayerAnimationTracker.get().startAnimation(sender, target, AnimationType.PADDLE_SPANK_ATTACKER, AnimationType.PADDLE_SPANK_VICTIM, 30);

                ServerWorld serverWorld = sender.getServerWorld();
                serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SPANK, SoundCategory.PLAYERS, 1.2f, 1.1f);
                serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.8, target.getZ(), 20, 0.3, 0.3, 0.3, 0.15);

                Vec3d dir = target.getPos().subtract(sender.getPos()).normalize();
                target.takeKnockback(0.35, -dir.x, -dir.z);

                FacialExpressionManager.get().setAndSyncExpression(sender, FacialExpression.SMILING, 30);
                FacialExpressionManager.get().setAndSyncExpression(target, FacialExpression.SHOCKED, 30);

                sender.sendMessage(Text.literal("🏏 WHACK! You paddled " + target.getName().getString() + "!").formatted(Formatting.RED, Formatting.BOLD), true);
                target.sendMessage(Text.literal("🏏 OUCH! You were paddled by " + sender.getName().getString() + "!").formatted(Formatting.YELLOW, Formatting.BOLD), true);
                return ActionResult.SUCCESS;
            } else if (user instanceof ServerPlayerEntity sender) {
                // Spank mob
                PlayerAnimationTracker.get().startSingleAnimation(sender, AnimationType.PADDLE_SPANK_ATTACKER, 25);
                user.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.SPANK, SoundCategory.PLAYERS, 1.2f, 1.1f);
                if (user.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + 0.8, entity.getZ(), 15, 0.3, 0.3, 0.3, 0.15);
                }
                Vec3d dir = entity.getPos().subtract(user.getPos()).normalize();
                entity.takeKnockback(0.4, -dir.x, -dir.z);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.crazygirlfriend.paddle.desc").formatted(Formatting.RED, Formatting.ITALIC));
    }
}

