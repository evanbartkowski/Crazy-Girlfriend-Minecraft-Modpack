package com.crazygirlfriend.entity;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaterBalloonEntity extends ThrownItemEntity {
    public WaterBalloonEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public WaterBalloonEntity(World world, LivingEntity owner) {
        super(ModEntities.WATER_BALLOON, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.WATER_BALLOON;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.age > 200) {
            popWaterSplash();
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            popWaterSplash();
            if (entityHitResult.getEntity() instanceof LivingEntity living) {
                // Apply Slowness II (100 ticks = 5 seconds)
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));

                // Apply Knockback 2
                Vec3d vel = this.getVelocity().normalize();
                living.takeKnockback(1.0, -vel.x, -vel.z);
                living.velocityModified = true;

                if (living instanceof ServerPlayerEntity target) {
                    PlayerAnimationTracker.get().startSingleAnimation(target, AnimationType.WET_SHAKE, 40);
                }
            }
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            popWaterSplash();
            this.discard();
        }
    }

    private void popWaterSplash() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 40, 0.4, 0.4, 0.4, 0.15);
            serverWorld.spawnParticles(ParticleTypes.DRIPPING_WATER, this.getX(), this.getY(), this.getZ(), 20, 0.3, 0.3, 0.3, 0.1);
            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.WATER_SPLASH, SoundCategory.PLAYERS, 1.0f, 1.2f);
        }
    }
}

