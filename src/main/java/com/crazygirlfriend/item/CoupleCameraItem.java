package com.crazygirlfriend.item;

import com.crazygirlfriend.facial.FacialExpression;
import com.crazygirlfriend.facial.FacialExpressionManager;
import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class CoupleCameraItem extends Item {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CoupleCameraItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.CAMERA_CLICK, SoundCategory.PLAYERS, 1.2f, 1.0f);

        if (world.isClient) {
            // Camera flash particles
            for (int i = 0; i < 20; i++) {
                double ox = (world.random.nextDouble() - 0.5) * 1.8;
                double oy = (world.random.nextDouble() - 0.5) * 1.8 + 1.0;
                double oz = (world.random.nextDouble() - 0.5) * 1.8;
                world.addParticle(ParticleTypes.END_ROD, user.getX() + ox, user.getY() + oy, user.getZ() + oz, 0, 0.08, 0);
                world.addParticle(ParticleTypes.FLASH, user.getX() + ox * 0.5, user.getY() + 1.2, user.getZ() + oz * 0.5, 0, 0, 0);
            }
        } else if (user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            boolean isSneaking = player.isSneaking();
            String now = LocalDateTime.now().format(DATE_FORMAT);
            String dim = world.getRegistryKey().getValue().getPath();
            String locationStr = "X: " + player.getBlockX() + ", Y: " + player.getBlockY() + ", Z: " + player.getBlockZ() + " (" + dim + ")";

            ItemStack photo = new ItemStack(ModItems.PHOTOGRAPH);
            NbtCompound nbt = photo.getOrCreateNbt();

            if (isSneaking) {
                // Selfie Mode!
                FacialExpressionManager.get().setAndSyncExpression(player, FacialExpression.WINKING, 50);

                nbt.putString("MemoryTitle", "Cute Selfie by " + player.getName().getString());
                nbt.putString("PhotoType", "SELFIE");
                nbt.putString("Photographer", player.getName().getString());
                nbt.putString("Subjects", player.getName().getString());
                nbt.putString("Date", now);
                nbt.putString("Location", locationStr);

                player.sendMessage(Text.literal("🤳 CLICK! You snapped a cute selfie! Photo added to inventory.").formatted(Formatting.AQUA, Formatting.BOLD), true);
            } else {
                // Check for partner nearby
                Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);
                ServerPlayerEntity partner = null;
                if (rel != null) {
                    UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                    partner = player.server.getPlayerManager().getPlayer(partnerUuid);
                }

                if (partner != null && player.distanceTo(partner) <= 12.0) {
                    // Couple Photo
                    FacialExpressionManager.get().setAndSyncExpression(player, FacialExpression.BLUSHING, 50);
                    FacialExpressionManager.get().setAndSyncExpression(partner, FacialExpression.SMILING, 50);

                    rel.addAffection(15);
                    rel.addCrazyMeter(-5);

                    nbt.putString("MemoryTitle", "Date: " + player.getName().getString() + " & " + partner.getName().getString());
                    nbt.putString("PhotoType", "COUPLE");
                    nbt.putString("Photographer", player.getName().getString());
                    nbt.putString("Subjects", player.getName().getString() + " & " + partner.getName().getString());
                    nbt.putString("Date", now);
                    nbt.putString("Location", locationStr);

                    player.sendMessage(Text.literal("📸 SNAP! Captured a romantic couple photo with " + partner.getName().getString() + "! Photo added to inventory.").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), true);
                    partner.sendMessage(Text.literal("📸 " + player.getName().getString() + " took a romantic photo of you two!").formatted(Formatting.LIGHT_PURPLE), true);
                } else {
                    // Scenic snapshot
                    nbt.putString("MemoryTitle", "Scenic Snapshot at " + player.getBlockX() + ", " + player.getBlockZ());
                    nbt.putString("PhotoType", "SCENIC");
                    nbt.putString("Photographer", player.getName().getString());
                    nbt.putString("Subjects", "Landscape");
                    nbt.putString("Date", now);
                    nbt.putString("Location", locationStr);

                    player.sendMessage(Text.literal("📸 SNAP! Captured a scenic photograph! Photo added to inventory.").formatted(Formatting.GREEN, Formatting.BOLD), true);
                }
            }

            // Put the photo directly into player inventory
            if (!player.getInventory().insertStack(photo)) {
                player.dropItem(photo, false);
            }

            user.getItemCooldownManager().set(this, 20);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Instant polaroid couple & selfie camera.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Right-click: Snap scenery or couple photo").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Sneak + Right-click: Snap a cute selfie! 🤳").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Prints an actual inspectable Photograph item into your inventory!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
