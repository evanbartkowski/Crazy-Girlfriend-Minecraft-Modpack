package com.crazygirlfriend.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoupleFurnitureItem extends BlockItem {
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

    public CoupleFurnitureItem(Block block, FurnitureType furnitureType, Settings settings) {
        super(block, settings);
        this.furnitureType = furnitureType;
    }

    public FurnitureType getFurnitureType() {
        return furnitureType;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(furnitureType.getDescription()).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Place on the ground and right-click to interact & sit with your partner!").formatted(Formatting.LIGHT_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
