package com.crazygirlfriend.client.atmosphere;

import com.crazygirlfriend.config.ModConfig;
import com.crazygirlfriend.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SkyOverhaulRenderer {
    private static final SkyOverhaulRenderer INSTANCE = new SkyOverhaulRenderer();
    public static SkyOverhaulRenderer get() { return INSTANCE; }

    private final Random random = new Random();
    private final List<ShootingStar> activeStars = new ArrayList<>();
    private boolean isMeteorShower = false;
    private int meteorShowerTicks = 0;

    public static class ShootingStar {
        public double x, y, z;
        public double vx, vy, vz;
        public int age;
        public int maxAge;

        public ShootingStar(double x, double y, double z, double vx, double vy, double vz, int maxAge) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.age = 0;
            this.maxAge = maxAge;
        }
    }

    public void tick(MinecraftClient client) {
        if (!ModConfig.get().enableAtmosphericSky || client.world == null || client.player == null) {
            activeStars.clear();
            return;
        }

        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        long timeOfDay = world.getTimeOfDay() % 24000L;
        boolean isNight = timeOfDay > 13000L && timeOfDay < 23000L;
        boolean isClear = !world.isRaining() && !world.isThundering();

        // Meteor Shower event check (rare night event)
        if (isNight && isClear) {
            if (!isMeteorShower && random.nextInt(1200) == 0) {
                isMeteorShower = true;
                meteorShowerTicks = 400 + random.nextInt(400); // 20-40 seconds of meteor shower
            }
        } else {
            isMeteorShower = false;
            meteorShowerTicks = 0;
        }

        if (isMeteorShower) {
            meteorShowerTicks--;
            if (meteorShowerTicks <= 0) {
                isMeteorShower = false;
            }
        }

        // Spawn shooting stars at night
        if (isNight && isClear) {
            int spawnChance = isMeteorShower ? 8 : 120;
            if (random.nextInt(spawnChance) == 0) {
                double dist = 40.0 + random.nextDouble() * 30.0;
                double angle = random.nextDouble() * Math.PI * 2.0;
                double sx = player.getX() + Math.cos(angle) * dist;
                double sz = player.getZ() + Math.sin(angle) * dist;
                double sy = player.getY() + 35.0 + random.nextDouble() * 25.0;

                double speed = 1.2 + random.nextDouble() * 0.8;
                double trajAngle = angle + Math.PI * 0.75 + (random.nextDouble() - 0.5) * 0.5;
                double vx = Math.cos(trajAngle) * speed;
                double vy = -0.3 - random.nextDouble() * 0.3;
                double vz = Math.sin(trajAngle) * speed;

                activeStars.add(new ShootingStar(sx, sy, sz, vx, vy, vz, 25 + random.nextInt(15)));

                if (random.nextInt(3) == 0) {
                    world.playSound(player.getX(), player.getY(), player.getZ(), ModSounds.SHOOTING_STAR, SoundCategory.AMBIENT, 0.4f * ModConfig.get().ambientAudioVolume, 1.8f, false);
                }
            }
        }

        // Tick active shooting stars and spawn streak particles
        Iterator<ShootingStar> it = activeStars.iterator();
        while (it.hasNext()) {
            ShootingStar star = it.next();
            star.age++;
            star.x += star.vx;
            star.y += star.vy;
            star.z += star.vz;

            // Spawn bright streak particles along star path
            world.addParticle(ParticleTypes.END_ROD, star.x, star.y, star.z, -star.vx * 0.1, -star.vy * 0.1, -star.vz * 0.1);
            world.addParticle(ParticleTypes.FIREWORK, star.x, star.y, star.z, 0, 0, 0);

            if (star.age >= star.maxAge) {
                it.remove();
            }
        }
    }

    public boolean isMeteorShower() {
        return isMeteorShower;
    }
}
