package com.crazygirlfriend.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhotographItem extends Item {
    public PhotographItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.2f);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HEART, user.getX(), user.getY() + 1.2, user.getZ(), 4, 0.2, 0.2, 0.2, 0.02);

            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains("MemoryTitle")) {
                String title = nbt.getString("MemoryTitle");
                String photoType = nbt.getString("PhotoType");
                String photographer = nbt.getString("Photographer");
                String subjects = nbt.getString("Subjects");
                String date = nbt.getString("Date");
                String location = nbt.getString("Location");

                boolean isSelfie = "SELFIE".equalsIgnoreCase(photoType);

                user.sendMessage(Text.literal("╔═══════════════════════════════════════╗").formatted(Formatting.LIGHT_PURPLE), false);
                user.sendMessage(Text.literal("║ 📸 POLAROID PHOTO SNAPSHOT           ║").formatted(Formatting.GOLD, Formatting.BOLD), false);
                user.sendMessage(Text.literal("╟───────────────────────────────────────╢").formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("║ ✨ ").append(Text.literal(title).formatted(Formatting.YELLOW, Formatting.BOLD)), false);
                user.sendMessage(Text.literal("║ " + (isSelfie ? "🤳 Type: Solo Selfie" : "❤️ Type: Couple Date")).formatted(isSelfie ? Formatting.AQUA : Formatting.RED), false);
                user.sendMessage(Text.literal("║ 👤 Photographer: " + photographer).formatted(Formatting.WHITE), false);
                if (!subjects.isEmpty()) {
                    user.sendMessage(Text.literal("║ 👥 Featuring: " + subjects).formatted(Formatting.LIGHT_PURPLE), false);
                }
                user.sendMessage(Text.literal("║ 📍 Location: " + location).formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("║ 📅 Date: " + date).formatted(Formatting.AQUA), false);
                user.sendMessage(Text.literal("╚═══════════════════════════════════════╝").formatted(Formatting.LIGHT_PURPLE), false);
            } else {
                user.sendMessage(Text.literal("📸 A blank photograph. Use the Couple Camera to snap a picture!").formatted(Formatting.GRAY), false);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A glossy printed polaroid photograph.").formatted(Formatting.GRAY, Formatting.ITALIC));
        if (stack.hasNbt() && stack.getNbt().contains("MemoryTitle")) {
            NbtCompound nbt = stack.getNbt();
            String title = nbt.getString("MemoryTitle");
            boolean isSelfie = "SELFIE".equalsIgnoreCase(nbt.getString("PhotoType"));
            tooltip.add(Text.literal((isSelfie ? "🤳 " : "❤️ ") + title).formatted(Formatting.GOLD));
            tooltip.add(Text.literal("Photographer: " + nbt.getString("Photographer")).formatted(Formatting.YELLOW));
            tooltip.add(Text.literal("Date: " + nbt.getString("Date")).formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Right-click to inspect photograph!").formatted(Formatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Text.literal("Right-click with Couple Camera to capture a photo!").formatted(Formatting.DARK_GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
