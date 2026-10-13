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
            if (flightTicks > 15) {
                returning = true;
            }

            if (returning && this.getOwner() != null) {
                LivingEntity owner = (LivingEntity) this.getOwner();
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
                target.takeKnockback(0.6, -this.getVelocity().x, -this.getVelocity().z);

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

