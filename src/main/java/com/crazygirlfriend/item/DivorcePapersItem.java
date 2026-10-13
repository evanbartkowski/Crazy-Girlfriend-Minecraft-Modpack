package com.crazygirlfriend.item;

import com.crazygirlfriend.relationship.Relationship;
import com.crazygirlfriend.relationship.RelationshipManager;
import com.crazygirlfriend.relationship.RelationshipType;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DivorcePapersItem extends Item {
    public DivorcePapersItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof ServerPlayerEntity target && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = player.getServerWorld();
            Relationship rel = RelationshipManager.get().getRelationship(player.getUuid(), serverWorld);

            if (rel == null || !rel.involves(target.getUuid())) {
                player.sendMessage(Text.literal("❌ You can only serve divorce papers to your spouse!").formatted(Formatting.RED), true);
                return ActionResult.CONSUME;
            }

            if (rel.getType() != RelationshipType.MARRIED) {
                player.sendMessage(Text.literal("❌ You aren't married! Use '/cg breakup' for dating breakups.").formatted(Formatting.RED), true);
                return ActionResult.CONSUME;
            }

            serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RECORD_SCRATCH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SAD_VIOLIN, SoundCategory.PLAYERS, 1.0f, 1.0f);

            if (player.isSneaking()) {
                // Instant finalize
                RelationshipManager.get().divorce(player);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }

            // Notice to Target
            MutableText msg = Text.literal("⚖️ ").formatted(Formatting.RED, Formatting.BOLD)
                .append(Text.literal(player.getName().getString()).formatted(Formatting.YELLOW, Formatting.BOLD))
                .append(Text.literal(" has served you with OFFICIAL DIVORCE PAPERS!\n").formatted(Formatting.RED))
                .append(Text.literal("Cause: ").formatted(Formatting.GRAY))
                .append(Text.literal("Irreconcilable differences & blanket theft.\n").formatted(Formatting.ITALIC, Formatting.WHITE));

            MutableText signBtn = Text.literal("[ ⚖️ SIGN DIVORCE SETTLEMENT ] ")
                .formatted(Formatting.RED, Formatting.BOLD)
                .styled(style -> style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg divorce"))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Finalize the divorce"))));

            target.sendMessage(msg.append(signBtn), false);
            player.sendMessage(Text.literal("⚖️ Served divorce papers to " + target.getName().getString() + "! (Sneak + click to force finalize).").formatted(Formatting.YELLOW), true);

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Comical legal documents for dramatic marital dissolution.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Right-click your spouse to serve them in Divorce Court!").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
