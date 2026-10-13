package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class WeddingCakeItem extends Item {
    public static final FoodComponent FOOD = new FoodComponent.Builder()
        .hunger(8)
        .saturationModifier(0.9f)
        .alwaysEdible()
        .build();

    public WeddingCakeItem(Settings settings) {
        super(settings.food(FOOD));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);

            if (rel != null) {
                UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);

                if (partner != null && player.distanceTo(partner) <= 8.0) {
                    player.sendMessage(Text.literal("🍰 You and " + partner.getName().getString() + " shared a slice of wedding cake!").formatted(Formatting.LIGHT_PURPLE), true);
                    partner.sendMessage(Text.literal("🍰 " + player.getName().getString() + " shared delicious wedding cake with you!").formatted(Formatting.LIGHT_PURPLE), true);

                    partner.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
                    partner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
                    partner.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 0));

                    serverWorld.spawnParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.5, 0.5, 0.5, 0.05);
                    serverWorld.spawnParticles(ParticleTypes.HEART, partner.getX(), partner.getY() + 1.0, partner.getZ(), 10, 0.5, 0.5, 0.5, 0.05);

                    rel.addAffection(30);
                    rel.addCrazyMeter(-15);
                    RelationshipManager.get().save(serverWorld);
                }
            }

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 0));
        }
        return result;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = player.getServerWorld();
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);

            if (rel != null && rel.involves(target.getUuid())) {
                serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                target.getHungerManager().add(6, 0.8f);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
                serverWorld.spawnParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.0, target.getZ(), 12, 0.4, 0.4, 0.4, 0.05);

                player.sendMessage(Text.literal("🍰 You sweetly fed cake to " + target.getName().getString() + "!").formatted(Formatting.LIGHT_PURPLE), true);
                target.sendMessage(Text.literal("🍰 " + player.getName().getString() + " fed you a delicious piece of wedding cake!").formatted(Formatting.LIGHT_PURPLE), true);

                rel.addAffection(25);
                rel.addCrazyMeter(-10);
                RelationshipManager.get().save(serverWorld);

                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A sweet celebratory cake to share with your partner.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Grants powerful buffs and reduces Crazy Meter when eaten together!").formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

