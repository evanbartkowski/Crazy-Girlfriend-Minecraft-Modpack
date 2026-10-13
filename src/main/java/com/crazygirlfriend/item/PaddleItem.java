package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.RequestManager;
import com.crazygirlfriend.relationship.RequestType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaddleItem extends Item {
    public PaddleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity sender) {
            user.getItemCooldownManager().set(this, 20);
            RequestManager.get().sendRequest(sender, target, RequestType.PADDLE_SPANK, null);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.crazygirlfriend.paddle.desc").formatted(Formatting.RED, Formatting.ITALIC));
    }
}

