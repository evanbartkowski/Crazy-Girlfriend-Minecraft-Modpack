package com.crazygirlfriend.relationship;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class CrazyGirlfriendSaveData extends PersistentState {
    public static final String IDENTIFIER = "crazygirlfriend_relationships";
    private final List<Relationship> relationships = new ArrayList<>();

    public CrazyGirlfriendSaveData() {
        super();
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public static CrazyGirlfriendSaveData fromNbt(NbtCompound nbt) {
        CrazyGirlfriendSaveData data = new CrazyGirlfriendSaveData();
        NbtList list = nbt.getList("Relationships", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound comp = list.getCompound(i);
            try {
                data.relationships.add(Relationship.readNbt(comp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (Relationship rel : relationships) {
            NbtCompound comp = new NbtCompound();
            rel.writeNbt(comp);
            list.add(comp);
        }
        nbt.put("Relationships", list);
        return nbt;
    }

    public static CrazyGirlfriendSaveData get(ServerWorld world) {
        return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(
            CrazyGirlfriendSaveData::fromNbt,
            CrazyGirlfriendSaveData::new,
            IDENTIFIER
        );
    }
}

