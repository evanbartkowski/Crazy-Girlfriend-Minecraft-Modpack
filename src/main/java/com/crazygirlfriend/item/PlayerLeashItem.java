package com.crazygirlfriend.item;

import com.crazygirlfriend.leash.PlayerLeashManager;
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

public class PlayerLeashItem extends Item {
    public PlayerLeashItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity sender) {
            if (PlayerLeashManager.get().isLeashed(target.getUuid())) {
                PlayerLeashManager.get().toggleSit(sender, target);
            } else {
                RequestManager.get().sendRequest(sender, target, RequestType.LEASH, null);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.crazygirlfriend.player_leash.desc").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
    }
}

