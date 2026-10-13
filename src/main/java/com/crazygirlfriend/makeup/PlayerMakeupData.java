package com.crazygirlfriend.makeup;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class PlayerMakeupData {
    private final UUID playerUuid;
    private int lipstickStyle = 0;
    private int blushStyle = 0;
    private int eyelinerStyle = 0;
    private int faceMarking = 0;

    public PlayerMakeupData(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public int getLipstickStyle() { return lipstickStyle; }
    public void setLipstickStyle(int lipstickStyle) { this.lipstickStyle = lipstickStyle; }
    public int getBlushStyle() { return blushStyle; }
    public void setBlushStyle(int blushStyle) { this.blushStyle = blushStyle; }
    public int getEyelinerStyle() { return eyelinerStyle; }
    public void setEyelinerStyle(int eyelinerStyle) { this.eyelinerStyle = eyelinerStyle; }
    public int getFaceMarking() { return faceMarking; }
    public void setFaceMarking(int faceMarking) { this.faceMarking = faceMarking; }

    public boolean isEmpty() {
        return lipstickStyle == 0 && blushStyle == 0 && eyelinerStyle == 0 && faceMarking == 0;
    }

    public void clear() {
        lipstickStyle = 0;
        blushStyle = 0;
        eyelinerStyle = 0;
        faceMarking = 0;
    }

    public void writeToBuf(PacketByteBuf buf) {
        buf.writeUuid(playerUuid);
        buf.writeByte(lipstickStyle);
        buf.writeByte(blushStyle);
        buf.writeByte(eyelinerStyle);
        buf.writeByte(faceMarking);
    }

    public static PlayerMakeupData readFromBuf(PacketByteBuf buf) {
        UUID uuid = buf.readUuid();
        PlayerMakeupData data = new PlayerMakeupData(uuid);
        data.lipstickStyle = buf.readByte();
        data.blushStyle = buf.readByte();
        data.eyelinerStyle = buf.readByte();
        data.faceMarking = buf.readByte();
        return data;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putUuid("PlayerUuid", playerUuid);
        nbt.putInt("Lipstick", lipstickStyle);
        nbt.putInt("Blush", blushStyle);
        nbt.putInt("Eyeliner", eyelinerStyle);
        nbt.putInt("FaceMarking", faceMarking);
        return nbt;
    }

    public static PlayerMakeupData readNbt(NbtCompound nbt) {
        UUID uuid = nbt.getUuid("PlayerUuid");
        PlayerMakeupData data = new PlayerMakeupData(uuid);
        data.lipstickStyle = nbt.getInt("Lipstick");
        data.blushStyle = nbt.getInt("Blush");
        data.eyelinerStyle = nbt.getInt("Eyeliner");
        data.faceMarking = nbt.getInt("FaceMarking");
        return data;
    }
}

