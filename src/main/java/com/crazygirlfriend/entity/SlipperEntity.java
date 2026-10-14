package com.crazygirlfriend.entity;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlipperEntity extends ThrownItemEntity {
    public SlipperEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlipperEntity(World world, LivingEntity owner) {
        super(ModEntities.SLIPPER, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THROWABLE_SLIPPER;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.age > 200) {
            this.dropStack(new ItemStack(ModItems.THROWABLE_SLIPPER));
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            if (entityHitResult.getEntity() instanceof LivingEntity target) {
                float damage = ModConfig.get().slipperDamage;
                DamageSource source = this.getDamageSources().thrown(this, this.getOwner());
                target.damage(source, damage);

                // Comical knockback
                Vec3d vel = this.getVelocity().normalize();
                double kb = ModConfig.get().slipperKnockback;
                target.takeKnockback(kb, -vel.x, -vel.z);

                // Particles & sound
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0, target.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
                    serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SLIPPER_HIT, SoundCategory.PLAYERS, 1.2f, 1.2f);
                }

                // If hit player or mob, spin in a circle
                target.setYaw((target.getYaw() + 180.0f) % 360.0f);
                target.setBodyYaw((target.getBodyYaw() + 180.0f) % 360.0f);
                target.setHeadYaw((target.getHeadYaw() + 180.0f) % 360.0f);
                double spinX = -vel.z * 0.5;
                double spinZ = vel.x * 0.5;
                target.addVelocity(spinX, 0.15, spinZ);
                target.velocityModified = true;

                if (target instanceof ServerPlayerEntity playerTarget) {
                    // Full 360-degree dramatic spin animation
                    PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.SLAP_DRAMATIC, 35);
                    com.crazygirlfriend.facial.FacialExpressionManager.get().setAndSyncExpression(playerTarget, com.crazygirlfriend.facial.FacialExpression.DIZZY_EYES, 35);
                }

                // Drop item so it can be retrieved
                this.dropStack(new ItemStack(ModItems.THROWABLE_SLIPPER));
                this.discard();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.SLIPPER_HIT, SoundCategory.PLAYERS, 0.8f, 1.4f);
            this.dropStack(new ItemStack(ModItems.THROWABLE_SLIPPER));
            this.discard();
        }
    }
}

