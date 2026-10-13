package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.relationship.RelationshipType;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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

public class WeddingAltarItem extends Item {
    public WeddingAltarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = player.getServerWorld();
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);

            if (rel == null || !rel.involves(target.getUuid())) {
                player.sendMessage(Text.literal("❌ You must be in a relationship with this player to use the Wedding Altar!").formatted(Formatting.RED), true);
                return ActionResult.CONSUME;
            }

            if (rel.getType() == RelationshipType.MARRIED) {
                // Anniversary Celebration!
                performAnniversary(serverWorld, player, target, rel);
                return ActionResult.SUCCESS;
            }

            // Perform Wedding Ceremony!
            performWedding(serverWorld, player, target, rel, stack);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void performWedding(ServerWorld world, ServerPlayerEntity player, ServerPlayerEntity target, Relationship rel, ItemStack stack) {
        rel.setType(RelationshipType.MARRIED);
        rel.addAffection(50);
        rel.setCrazyMeter(0);
        RelationshipManager.get().save(world);
        RelationshipManager.get().syncToAll(world.getServer());

        // Play sounds
        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.CHEER, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.ROMANTIC_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);

        // Spawn Fireworks
        spawnFireworks(world, player.getX(), player.getY() + 2, player.getZ());
        spawnFireworks(world, target.getX(), target.getY() + 2, target.getZ());

        // Particles
        world.spawnParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 30, 0.8, 0.8, 0.8, 0.1);
        world.spawnParticles(ParticleTypes.FIREWORK, target.getX(), target.getY() + 1.2, target.getZ(), 40, 0.8, 0.8, 0.8, 0.1);

        // Server-wide announcement
        Text announcement = Text.literal("🔔 THE WEDDING BELLS RING! ").formatted(Formatting.GOLD, Formatting.BOLD)
            .append(Text.literal(player.getName().getString() + " and " + target.getName().getString()).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD))
            .append(Text.literal(" are now officially HUSBAND & WIFE! 💍").formatted(Formatting.GOLD, Formatting.BOLD));
        world.getServer().getPlayerManager().broadcast(announcement, false);

        // Give Wedding Cake
        ItemStack cake = new ItemStack(ModItems.WEDDING_CAKE);
        if (!player.getInventory().insertStack(cake)) {
            player.dropItem(cake, false);
        }

        // Buffs
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 1));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 1));
    }

    private void performAnniversary(ServerWorld world, ServerPlayerEntity player, ServerPlayerEntity target, Relationship rel) {
        rel.addAffection(25);
        rel.addCrazyMeter(-10);
        RelationshipManager.get().save(world);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.CHEER, SoundCategory.PLAYERS, 1.0f, 1.1f);
        spawnFireworks(world, player.getX(), player.getY() + 2, player.getZ());

        player.sendMessage(Text.literal("🎉 Happy Anniversary! Your bond grows ever stronger!").formatted(Formatting.GOLD), true);
        target.sendMessage(Text.literal("🎉 Happy Anniversary! Your bond grows ever stronger!").formatted(Formatting.GOLD), true);

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 0));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 0));
    }

    private void spawnFireworks(ServerWorld world, double x, double y, double z) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        NbtCompound fireworksNbt = new NbtCompound();
        NbtList explosionsList = new NbtList();
        NbtCompound explosionNbt = new NbtCompound();
        explosionNbt.putByte("Type", (byte) 1); // Large ball
        explosionNbt.putIntArray("Colors", new int[]{16711680, 16738740, 16761035}); // Red, Pink, Magenta
        explosionsList.add(explosionNbt);
        fireworksNbt.put("Explosions", explosionsList);
        fireworksNbt.putByte("Flight", (byte) 1);
        fireworkStack.getOrCreateSubNbt("Fireworks").copyFrom(fireworksNbt);

        FireworkRocketEntity firework = new FireworkRocketEntity(world, x, y, z, fireworkStack);
        world.spawnEntity(firework);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Right-click your partner to conduct an unforgettable wedding ceremony!").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Also celebrates anniversaries if already married.").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
