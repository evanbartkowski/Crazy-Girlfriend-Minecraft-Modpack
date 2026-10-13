package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PartnerCompassItem extends Item {
    public PartnerCompassItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);
            if (rel != null) {
                UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);
                NbtCompound nbt = stack.getOrCreateNbt();

                if (partner != null && partner.getWorld().equals(world)) {
                    NbtCompound posTag = new NbtCompound();
                    posTag.putInt("X", partner.getBlockX());
                    posTag.putInt("Y", partner.getBlockY());
                    posTag.putInt("Z", partner.getBlockZ());
                    nbt.put("LodestonePos", posTag);
                    nbt.putString("LodestoneDimension", world.getRegistryKey().getValue().toString());
                    nbt.putBoolean("LodestoneTracked", false);
                    nbt.putString("PartnerName", partner.getName().getString());
                    nbt.putDouble("PartnerDistance", player.distanceTo(partner));
                } else {
                    nbt.remove("LodestonePos");
                    nbt.remove("LodestoneDimension");
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);
            if (rel == null) {
                user.sendMessage(Text.literal("💔 You don't have a partner yet! Give someone a Red Rose.").formatted(Formatting.RED), true);
                return TypedActionResult.pass(stack);
            }

            UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
            ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);
            String partnerName = rel.getPlayer1Uuid().equals(player.getUuid()) ? rel.getPlayer2Name() : rel.getPlayer1Name();

            if (partner != null && partner.getWorld().equals(world)) {
                int dist = (int) player.distanceTo(partner);
                String mood = rel.getMood(partnerUuid).getDisplayName();
                int crazy = rel.getCrazyMeter();
                user.sendMessage(Text.literal("🧭 Partner: " + partnerName + " | Distance: " + dist + "m | Mood: " + mood + " | Crazy: " + crazy + "%").formatted(Formatting.AQUA), true);
            } else if (partner != null) {
                user.sendMessage(Text.literal("🌌 " + partnerName + " is in another dimension!").formatted(Formatting.LIGHT_PURPLE), true);
            } else {
                user.sendMessage(Text.literal("💤 " + partnerName + " is currently offline.").formatted(Formatting.GRAY), true);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Tracks your partner's heartbeat and coordinates.").formatted(Formatting.GRAY));
        if (stack.hasNbt() && stack.getNbt().contains("PartnerName")) {
            tooltip.add(Text.literal("Bound to: " + stack.getNbt().getString("PartnerName")).formatted(Formatting.LIGHT_PURPLE));
            if (stack.getNbt().contains("PartnerDistance")) {
                int dist = (int) stack.getNbt().getDouble("PartnerDistance");
                tooltip.add(Text.literal("Last Distance: " + dist + " blocks").formatted(Formatting.YELLOW));
            }
        } else {
            tooltip.add(Text.literal("Binds automatically to your partner.").formatted(Formatting.DARK_GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
