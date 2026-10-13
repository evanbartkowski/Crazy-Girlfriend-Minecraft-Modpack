package com.crazygirlfriend.item;

import com.crazygirlfriend.makeup.MakeupManager;
import com.crazygirlfriend.relationship.RequestManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MakeupRemoverItem extends Item {
    public MakeupRemoverItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity instanceof ServerPlayerEntity target) {
            cleanMakeup(target);
            user.sendMessage(Text.literal("🧽 You cleaned all makeup off " + target.getName().getString() + "!").formatted(Formatting.AQUA), true);
            target.sendMessage(Text.literal("🧽 Your face was cleaned with a makeup sponge!").formatted(Formatting.AQUA), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            cleanMakeup(serverPlayer);
            user.sendMessage(Text.literal("🧽 You cleaned your face!").formatted(Formatting.AQUA), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    private void cleanMakeup(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        MakeupManager.get(world).clearMakeup(player.getUuid());
        RequestManager.syncMakeup(player.server, player.getUuid(), MakeupManager.get(world).getMakeup(player.getUuid()));

        world.spawnParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 1.5, player.getZ(), 20, 0.3, 0.3, 0.3, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.8f, 1.3f);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.crazygirlfriend.makeup_remover.desc").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}

