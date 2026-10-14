package com.crazygirlfriend.mixin;

import com.crazygirlfriend.CrazyGirlfriendMod;
import com.crazygirlfriend.item.CoupleClothingItem;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
    private static final Identifier HOODIE_LAYER_1 = new Identifier(CrazyGirlfriendMod.MOD_ID, "textures/models/armor/couple_hoodie_layer_1.png");
    private static final Identifier HOODIE_LAYER_2 = new Identifier(CrazyGirlfriendMod.MOD_ID, "textures/models/armor/couple_hoodie_layer_2.png");

    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void injectCoupleHoodieTexture(ArmorItem item, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir) {
        if (item instanceof CoupleClothingItem || "couple_hoodie".equals(item.getMaterial().getName())) {
            cir.setReturnValue(legs ? HOODIE_LAYER_2 : HOODIE_LAYER_1);
        }
    }
}

