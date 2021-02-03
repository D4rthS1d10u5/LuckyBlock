package com.mcgamer199.luckyblock.customentity.boss.main;

import com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BossFunctions {
    public BossFunctions() {
    }

    public static void shoot_beam(com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss boss, LivingEntity entity, LivingEntity target, int max, BossFunctions.ParticleHelper helper) {
        shoot_beam(entity.getLocation(), boss, target, max, 0.4D, helper, null);
    }

    public static void shoot_beam(com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss boss, LivingEntity entity, LivingEntity target, int max, double s, BossFunctions.ParticleHelper helper) {
        shoot_beam(entity.getLocation(), boss, target, max, s, helper, null);
    }

    public static void shoot_beam(final Location start, final EntityLBBoss boss, final LivingEntity target, int max, final double s, final BossFunctions.ParticleHelper helper, final String tag) {
        start.setDirection(target.getLocation().toVector().subtract(start.toVector()));
        final Vector increase = start.getDirection().multiply(s);
        Scheduler.timer(new BukkitRunnable() {
            private int m = max;
            private int a = 0;

            @Override
            public void run() {
                if (this.m < 1) {
                    cancel();
                } else {
                    if (target.isDead()) {
                        cancel();
                        return;
                    }

                    ++this.a;
                    Location point = start.add(increase);
                    MyTasks.playEffects(helper.particle, point, helper.amount, helper.doubles, helper.speed);
                    --this.m;
                    if (point.distance(target.getLocation()) < 1.0D) {
                        if (boss != null) {
                            boss.onHitEntityWithBeam(target, tag);
                        }

                        cancel();
                    } else if (this.a > 10) {
                        cancel();
                        BossFunctions.shoot_beam(point, boss, target, this.m, s, helper, tag);
                    }
                }
            }
        }, 0, 1);
    }

    public enum LaserType {
        CHASER,
        STRAIGHT;

        LaserType() {
        }
    }

    public static class ParticleHelper {
        Particle particle;
        double[] doubles;
        int amount;
        float speed;

        public ParticleHelper(Particle particle, int amount, double[] doubles, float speed) {
            this.particle = particle;
            this.amount = amount;
            this.doubles = doubles;
            this.speed = speed;
        }

        public int getAmount() {
            return this.amount;
        }

        public Particle getParticle() {
            return this.particle;
        }

        public float getSpeed() {
            return this.speed;
        }

        public double[] getDoubles() {
            return this.doubles;
        }
    }
}

