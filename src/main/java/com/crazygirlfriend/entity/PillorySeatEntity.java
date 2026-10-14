package com.crazygirlfriend.entity;

import com.crazygirlfriend.block.PilloryBlock;
import com.crazygirlfriend.pillory.PilloryManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PillorySeatEntity extends Entity {
    private BlockPos pilloryPos;
    private boolean locked = true;

    public PillorySeatEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setInvisible(true);
    }

    public void setPilloryPos(BlockPos pos) {
        this.pilloryPos = pos;
    }

    public BlockPos getPilloryPos() {
        return this.pilloryPos;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void unlock() {
        this.locked = false;
        if (!this.getWorld().isClient && !this.getPassengerList().isEmpty()) {
            Entity passenger = this.getPassengerList().get(0);
            PilloryManager.get().unlockPlayer(passenger.getUuid(), this.getServer());
        }
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("PilloryX")) {
            this.pilloryPos = new BlockPos(nbt.getInt("PilloryX"), nbt.getInt("PilloryY"), nbt.getInt("PilloryZ"));
        }
        this.locked = nbt.getBoolean("Locked");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.pilloryPos != null) {
            nbt.putInt("PilloryX", this.pilloryPos.getX());
            nbt.putInt("PilloryY", this.pilloryPos.getY());
            nbt.putInt("PilloryZ", this.pilloryPos.getZ());
        }
        nbt.putBoolean("Locked", this.locked);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.pilloryPos != null) {
                if (!(this.getWorld().getBlockState(this.pilloryPos).getBlock() instanceof PilloryBlock)) {
                    this.unlock();
                    this.removeAllPassengers();
                    this.discard();
                    return;
                }
            }
            if (this.getPassengerList().isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.1D;
    }
}
