package com.crazygirlfriend.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptySeatRenderer<T extends Entity> extends EntityRenderer<T> {
    public EmptySeatRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}

