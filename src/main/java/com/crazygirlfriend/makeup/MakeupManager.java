package com.crazygirlfriend.makeup;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MakeupManager extends PersistentState {
    public static final String IDENTIFIER = "crazygirlfriend_makeup";
    private final Map<UUID, PlayerMakeupData> playerMakeup = new HashMap<>();

    public PlayerMakeupData getMakeup(UUID playerUuid) {
        return playerMakeup.computeIfAbsent(playerUuid, PlayerMakeupData::new);
    }

    public void setMakeup(UUID playerUuid, PlayerMakeupData data) {
        playerMakeup.put(playerUuid, data);
        markDirty();
    }

    public void clearMakeup(UUID playerUuid) {
        if (playerMakeup.containsKey(playerUuid)) {
            playerMakeup.get(playerUuid).clear();
            markDirty();
        }
    }

    public Map<UUID, PlayerMakeupData> getAllMakeup() {
        return playerMakeup;
    }

    public static MakeupManager fromNbt(NbtCompound nbt) {
        MakeupManager manager = new MakeupManager();
        NbtList list = nbt.getList("MakeupList", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound comp = list.getCompound(i);
            try {
                PlayerMakeupData data = PlayerMakeupData.readNbt(comp);
                manager.playerMakeup.put(data.getPlayerUuid(), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (PlayerMakeupData data : playerMakeup.values()) {
            if (!data.isEmpty()) {
                NbtCompound comp = new NbtCompound();
                data.writeNbt(comp);
                list.add(comp);
            }
        }
        nbt.put("MakeupList", list);
        return nbt;
    }

    public static MakeupManager get(ServerWorld world) {
        return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(
            MakeupManager::fromNbt,
            MakeupManager::new,
            IDENTIFIER
        );
    }
}

