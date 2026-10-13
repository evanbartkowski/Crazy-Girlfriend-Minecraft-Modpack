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

public class FlowerBouquetItem extends Item {
    public FlowerBouquetItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity sender) {
            RequestManager.get().sendRequest(sender, target, RequestType.GIFT_BOUQUET, stack.copy());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Right-click a player to gift them this bouquet!").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
    }
}

