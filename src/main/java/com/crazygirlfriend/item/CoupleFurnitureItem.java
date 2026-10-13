package com.crazygirlfriend.item;

import com.crazygirlfriend.entity.ModEntities;
import com.crazygirlfriend.entity.SeatEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoupleFurnitureItem extends Item {
    public enum FurnitureType {
        SOFA("Couple Sofa", "A cozy loveseat designed for two lovers to sit close.", 0.25),
        DOUBLE_BED("Couple Double Bed", "A luxury double bed for romantic cuddling and spooning.", 0.15),
        SWING("Garden Swing", "A romantic garden swing for stargazing and swaying together.", 0.35),
        PICNIC_BLANKET("Picnic Blanket", "A checkered picnic blanket for outdoor romance.", 0.05);

        private final String displayName;
        private final String description;
        private final double heightOffset;

        FurnitureType(String displayName, String description, double heightOffset) {
            this.displayName = displayName;
            this.description = description;
            this.heightOffset = heightOffset;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public double getHeightOffset() {
            return heightOffset;
        }
    }

    private final FurnitureType furnitureType;

    public CoupleFurnitureItem(FurnitureType furnitureType, Settings settings) {
        super(settings);
        this.furnitureType = furnitureType;
    }

    public FurnitureType getFurnitureType() {
        return furnitureType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (player == null) return ActionResult.PASS;

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0 + furnitureType.getHeightOffset();
            double z = pos.getZ() + 0.5;

            SeatEntity seat = new SeatEntity(ModEntities.SEAT, serverWorld);
            seat.refreshPositionAndAngles(x, y, z, player.getYaw(), 0);
            serverWorld.spawnEntity(seat);
            player.startRiding(seat, true);

            serverWorld.playSound(null, x, y, z, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            serverWorld.spawnParticles(ParticleTypes.HEART, x, y + 0.5, z, 3, 0.2, 0.2, 0.2, 0.02);

            player.sendMessage(Text.literal("🛋️ Relaxing on the " + furnitureType.getDisplayName() + ". Partner can join beside you!").formatted(Formatting.GREEN), true);
            return ActionResult.SUCCESS;
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(furnitureType.getDescription()).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Right-click ground to sit and relax with your partner!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
