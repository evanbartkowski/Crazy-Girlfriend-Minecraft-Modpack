package com.crazygirlfriend.entity;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.animation.PlayerAnimationTracker;
import com.crazygirlfriend.item.ModItems;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class BoomerangSlipperEntity extends ThrownItemEntity {
    private boolean returning = false;
    private int flightTicks = 0;

    public BoomerangSlipperEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BoomerangSlipperEntity(World world, LivingEntity owner) {
        super(ModEntities.BOOMERANG_SLIPPER, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BOOMERANG_SLIPPER;
    }

    @Override
    public void tick() {
        super.tick();
        flightTicks++;

        if (!this.getWorld().isClient) {
            // Lifespan safety: prevent infinite flight if target lost or stuck
            if (flightTicks > 100) {
                if (this.getOwner() instanceof PlayerEntity player && player.isAlive() && player.getWorld() == this.getWorld()) {
                    player.getInventory().offerOrDrop(new ItemStack(ModItems.BOOMERANG_SLIPPER));
                } else {
                    this.dropStack(new ItemStack(ModItems.BOOMERANG_SLIPPER));
                }
                this.discard();
                return;
            }

            if (flightTicks > 15) {
                returning = true;
            }

            if (returning) {
                if (this.getOwner() == null || !(this.getOwner() instanceof LivingEntity owner) || !owner.isAlive() || owner.getWorld() != this.getWorld()) {
                    this.dropStack(new ItemStack(ModItems.BOOMERANG_SLIPPER));
                    this.discard();
                    return;
                }

                Vec3d target = owner.getPos().add(0, 1.0, 0);
                Vec3d diff = target.subtract(this.getPos());
                double dist = diff.length();

                if (dist < 1.5) {
                    if (owner instanceof PlayerEntity player) {
                        player.getInventory().offerOrDrop(new ItemStack(ModItems.BOOMERANG_SLIPPER));
                        this.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.BOOMERANG, SoundCategory.PLAYERS, 0.8f, 1.4f);
                    }
                    this.discard();
                    return;
                }

                Vec3d dir = diff.normalize().multiply(1.2);
                this.setVelocity(dir);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            if (entityHitResult.getEntity() instanceof LivingEntity target) {
                if (target.equals(this.getOwner()) && returning) return;

                target.damage(this.getDamageSources().thrown(this, this.getOwner()), 1.5f);
                Vec3d kb = this.getVelocity().normalize();
                target.takeKnockback(2.5, -kb.x, -kb.z); // Knockback 5 power
                target.addVelocity(0, 0.45, 0); // Comedic launch into the air
                target.velocityModified = true;

                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0, target.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
                    serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SLIPPER_HIT, SoundCategory.PLAYERS, 1.2f, 1.3f);
                }

                if (target instanceof ServerPlayerEntity playerTarget) {
                    PlayerAnimationTracker.get().startSingleAnimation(playerTarget, AnimationType.SLAP_VICTIM, 20);
                }
                returning = true;
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.SLIPPER_HIT, SoundCategory.PLAYERS, 0.8f, 1.2f);
            returning = true;
        }
    }
}

