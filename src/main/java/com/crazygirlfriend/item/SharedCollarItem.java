package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SharedCollarItem extends Item {
    public SharedCollarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity sender) {
            if (com.crazygirlfriend.leash.PlayerLeashManager.get().isCollared(target.getUuid())) {
                user.sendMessage(Text.literal("❌ " + target.getName().getString() + " is already wearing a collar!").formatted(Formatting.YELLOW), true);
                return ActionResult.CONSUME;
            }

            com.crazygirlfriend.leash.PlayerLeashManager.get().attachCollar(sender, target);
            user.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), net.minecraft.sound.SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1.0f, 1.1f);
            if (user.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
            }

            if (!sender.isCreative()) {
                stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        if (entity instanceof TameableEntity pet) {
            World world = user.getWorld();

            if (!pet.isTamed()) {
                if (!world.isClient) {
                    user.sendMessage(Text.literal("❌ You must tame this pet before putting on a Shared Collar!").formatted(Formatting.RED), true);
                }
                return ActionResult.CONSUME;
            }

            if (!pet.isOwner(user)) {
                if (!world.isClient) {
                    user.sendMessage(Text.literal("❌ You must be the owner of this pet to share custody!").formatted(Formatting.RED), true);
                }
                return ActionResult.CONSUME;
            }

            if (!world.isClient && user instanceof ServerPlayerEntity player) {
                ServerWorld serverWorld = (ServerWorld) world;
                Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);
                if (rel == null) {
                    user.sendMessage(Text.literal("❌ You must be in a relationship to share pet custody!").formatted(Formatting.RED), true);
                    return ActionResult.CONSUME;
                }

                UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                String partnerName = rel.getPlayer1Uuid().equals(player.getUuid()) ? rel.getPlayer2Name() : rel.getPlayer1Name();

                // Save shared custody in relationship
                rel.addSharedPet(pet.getUuid());
                RelationshipManager.get().save(serverWorld);

                world.playSound(null, pet.getX(), pet.getY(), pet.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.NEUTRAL, 1.0f, 1.2f);
                serverWorld.spawnParticles(ParticleTypes.HEART, pet.getX(), pet.getY() + 0.5, pet.getZ(), 10, 0.4, 0.4, 0.4, 0.1);

                player.sendMessage(Text.literal("🐾 " + pet.getName().getString() + " is now co-owned by you and " + partnerName + "!").formatted(Formatting.GREEN), false);

                ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);
                if (partner != null) {
                    partner.sendMessage(Text.literal("🐾 " + player.getName().getString() + " gave " + pet.getName().getString() + " a Shared Collar! You now share custody!").formatted(Formatting.LIGHT_PURPLE), false);
                }

                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Use on a tamed pet to share joint custody with your partner.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Symbolizes your shared love and responsibility!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
