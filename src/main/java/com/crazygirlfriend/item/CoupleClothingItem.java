package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CoupleClothingItem extends ArmorItem {

    public static final ArmorMaterial COUPLE_HOODIE_MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurability(Type type) {
            return 350;
        }

        @Override
        public int getProtection(Type type) {
            return 5;
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.PINK_WOOL, Items.RED_WOOL);
        }

        @Override
        public String getName() {
            return "couple_hoodie";
        }

        @Override
        public float getToughness() {
            return 1.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0f;
        }
    };

    public CoupleClothingItem(Settings settings) {
        super(COUPLE_HOODIE_MATERIAL, Type.CHESTPLATE, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            if (player.getEquippedStack(EquipmentSlot.CHEST) == stack) {
                // Check every 20 ticks (1 second)
                if (player.age % 20 == 0) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);
                    if (rel != null) {
                        UUID partnerUuid = rel.getOtherPlayerUuid(player.getUuid());
                        ServerPlayerEntity partner = player.server.getPlayerManager().getPlayer(partnerUuid);

                        if (partner != null && player.distanceTo(partner) <= 16.0) {
                            // Soulmate Warmth buff!
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0, true, false, true));
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0, true, false, true));

                            if (player.age % 80 == 0) {
                                serverWorld.spawnParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Cozy matching couple hoodie.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Soulmate Warmth: Grants Speed & Resistance when near your partner!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
