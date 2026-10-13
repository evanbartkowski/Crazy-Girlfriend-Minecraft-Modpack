package com.crazygirlfriend.facial;

import com.crazygirlfriend.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FacialExpressionManager {
    private static final FacialExpressionManager INSTANCE = new FacialExpressionManager();

    public static class ExpressionState {
        public FacialExpression expression;
        public int remainingTicks;

        public ExpressionState(FacialExpression expression, int remainingTicks) {
            this.expression = expression;
            this.remainingTicks = remainingTicks;
        }
    }

    private final Map<UUID, ExpressionState> activeExpressions = new HashMap<>();

    public static FacialExpressionManager get() {
        return INSTANCE;
    }

    public void setExpression(UUID playerUuid, FacialExpression expression, int durationTicks) {
        if (expression == null || expression == FacialExpression.NONE || durationTicks <= 0) {
            activeExpressions.remove(playerUuid);
        } else {
            activeExpressions.put(playerUuid, new ExpressionState(expression, durationTicks));
        }
    }

    public void setAndSyncExpression(ServerPlayerEntity player, FacialExpression expression, int durationTicks) {
        setExpression(player.getUuid(), expression, durationTicks);
        syncExpressionToAll(player.server, player.getUuid(), expression, durationTicks);
    }

    public FacialExpression getExpression(UUID playerUuid) {
        ExpressionState state = activeExpressions.get(playerUuid);
        if (state == null || state.remainingTicks <= 0) {
            return FacialExpression.NONE;
        }
        return state.expression;
    }

    public void tick() {
        if (activeExpressions.isEmpty()) return;
        activeExpressions.entrySet().removeIf(entry -> {
            entry.getValue().remainingTicks--;
            return entry.getValue().remainingTicks <= 0;
        });
    }

    public void syncExpressionToAll(MinecraftServer server, UUID playerUuid, FacialExpression expression, int durationTicks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerUuid);
        buf.writeVarInt(expression.ordinal());
        buf.writeVarInt(durationTicks);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, ModPackets.FACIAL_EXPRESSION_SYNC_ID, buf);
        }
    }
}
