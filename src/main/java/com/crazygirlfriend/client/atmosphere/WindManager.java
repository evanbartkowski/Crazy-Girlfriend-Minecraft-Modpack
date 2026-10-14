package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public class WindManager {
    private static final WindManager INSTANCE = new WindManager();
    public static WindManager get() { return INSTANCE; }

    private float windAngle = 0.0f;
    private float windSpeed = 0.5f;
    private Vec3d windVector = new Vec3d(0.5, 0, 0.2);

    public void tick(ClientWorld world) {
        if (!ModConfig.get().enableWindSway || world == null) {
            windSpeed = 0.0f;
            windVector = Vec3d.ZERO;
            return;
        }

        long time = world.getTime();
        // Slow rotating wind angle
        windAngle = (float) Math.sin(time * 0.001) * 3.14159f;

        // Base speed 0.35 - 0.75 modulated by natural turbulence
        float baseSpeed = 0.45f + (float) Math.sin(time * 0.003) * 0.25f;

        if (world.isThundering()) {
            baseSpeed *= 2.2f;
        } else if (world.isRaining()) {
            baseSpeed *= 1.5f;
        }

        windSpeed = baseSpeed;
        double vx = Math.cos(windAngle) * windSpeed;
        double vz = Math.sin(windAngle) * windSpeed;
        windVector = new Vec3d(vx, 0, vz);
    }

    public float getWindAngle() { return windAngle; }
    public float getWindSpeed() { return windSpeed; }
    public Vec3d getWindVector() { return windVector; }
}
