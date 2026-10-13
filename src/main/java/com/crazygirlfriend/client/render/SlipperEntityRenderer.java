package com.crazygirlfriend.client.render;

import com.crazygirlfriend.entity.SlipperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SlipperEntityRenderer extends FlyingItemEntityRenderer<SlipperEntity> {
    public SlipperEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}

