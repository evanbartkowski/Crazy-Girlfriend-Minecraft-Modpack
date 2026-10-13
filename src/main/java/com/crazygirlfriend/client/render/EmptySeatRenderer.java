package com.crazygirlfriend.client.render;

import com.crazygirlfriend.entity.SeatEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class EmptySeatRenderer extends EntityRenderer<SeatEntity> {
    public EmptySeatRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(SeatEntity entity) {
        return null;
    }
}
