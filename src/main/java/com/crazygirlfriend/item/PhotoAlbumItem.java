package com.crazygirlfriend.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhotoAlbumItem extends Item {
    public PhotoAlbumItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (!world.isClient) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains("MemoryTitle")) {
                String title = nbt.getString("MemoryTitle");
                String partner = nbt.getString("Partner");
                String photographer = nbt.getString("Photographer");
                String date = nbt.getString("Date");
                String dim = nbt.getString("Dimension");

                user.sendMessage(Text.literal("═══════ 📖 Romantic Memory ═══════").formatted(Formatting.LIGHT_PURPLE), false);
                user.sendMessage(Text.literal("✨ Memory: ").formatted(Formatting.GOLD).append(Text.literal(title).formatted(Formatting.WHITE)), false);
                user.sendMessage(Text.literal("❤️ Couple: ").formatted(Formatting.RED).append(Text.literal(photographer + " & " + partner).formatted(Formatting.WHITE)), false);
                user.sendMessage(Text.literal("📅 Date: ").formatted(Formatting.AQUA).append(Text.literal(date + " (" + dim + ")").formatted(Formatting.WHITE)), false);
                user.sendMessage(Text.literal("════════════════════════════════").formatted(Formatting.LIGHT_PURPLE), false);
            } else {
                user.sendMessage(Text.literal("📖 This photo album is currently empty. Use the Couple Camera to snap memories!").formatted(Formatting.GRAY), false);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Holds precious photo memories captured by the Couple Camera.").formatted(Formatting.GRAY));
        if (stack.hasNbt() && stack.getNbt().contains("MemoryTitle")) {
            tooltip.add(Text.literal("Memory: " + stack.getNbt().getString("MemoryTitle")).formatted(Formatting.GOLD));
            tooltip.add(Text.literal("Date: " + stack.getNbt().getString("Date")).formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Featuring: " + stack.getNbt().getString("Partner")).formatted(Formatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Text.literal("Empty Album").formatted(Formatting.DARK_GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}

