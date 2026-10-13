package com.crazygirlfriend.item;

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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CoupleCameraItem extends Item {
    public CoupleCameraItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.CAMERA_CLICK, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (world.isClient) {
            for (int i = 0; i < 15; i++) {
                double ox = (world.random.nextDouble() - 0.5) * 1.5;
                double oy = world.random.nextDouble() * 1.5;
                double oz = (world.random.nextDouble() - 0.5) * 1.5;
                world.addParticle(ParticleTypes.END_ROD, user.getX() + ox, user.getY() + oy, user.getZ() + oz, 0, 0.05, 0);
            }
        } else if (user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);

            if (rel != null) {
                UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);

                if (partner != null && player.distanceTo(partner) <= 12.0) {
                    player.sendMessage(Text.literal("📸 Captured a romantic photo with " + partner.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), true);
                    partner.sendMessage(Text.literal("📸 " + player.getName().getString() + " snapped a cute photo of you two!").formatted(Formatting.LIGHT_PURPLE), true);

                    rel.addAffection(15);
                    rel.addCrazyMeter(-5);

                    // Create a photo album memory
                    ItemStack album = new ItemStack(ModItems.PHOTO_ALBUM);
                    NbtCompound nbt = album.getOrCreateNbt();
                    nbt.putString("MemoryTitle", "Date at " + player.getBlockX() + ", " + player.getBlockZ());
                    nbt.putString("Partner", partner.getName().getString());
                    nbt.putString("Photographer", player.getName().getString());
                    nbt.putString("Date", LocalDate.now().toString());
                    nbt.putString("Dimension", world.getRegistryKey().getValue().getPath());

                    if (!player.getInventory().insertStack(album)) {
                        player.dropItem(album, false);
                    }
                } else {
                    player.sendMessage(Text.literal("📸 Took a solo selfie! Bring your partner closer for couple photos.").formatted(Formatting.YELLOW), true);
                }
            } else {
                player.sendMessage(Text.literal("📸 Took a cute solo picture!").formatted(Formatting.YELLOW), true);
            }

            user.getItemCooldownManager().set(this, 30);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Capture romantic moments and funny dates with your partner.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Snapping couple photos grants Photo Album memories & Affection!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

