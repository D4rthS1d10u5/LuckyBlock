package com.mcgamer199.luckyblock.customentity.boss.main;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import com.mcgamer199.luckyblock.logic.ITask;

public class BossFunctions {
    public BossFunctions() {
    }

    public static void shoot_beam(com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss boss, LivingEntity entity, LivingEntity target, int max, BossFunctions.ParticleHelper helper) {
        shoot_beam(entity.getLocation(), boss, target, max, 0.4D, helper, (String)null);
    }

    public static void shoot_beam(com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss boss, LivingEntity entity, LivingEntity target, int max, double s, BossFunctions.ParticleHelper helper) {
        shoot_beam(entity.getLocation(), boss, target, max, s, helper, (String)null);
    }

    public static void shoot_beam(final Location start, final EntityLBBoss boss, final LivingEntity target, int max, final double s, final BossFunctions.ParticleHelper helper, final String tag) {
        start.setDirection(target.getLocation().toVector().subtract(start.toVector()));
        final Vector increase = start.getDirection().multiply(s);
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            private int m = max;
            private int a = 0;

            public void run() {
                if (this.m < 1) {
                    task.run();
                } else {
                    if (target.isDead()) {
                        task.run();
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

                        task.run();
                    } else if (this.a > 10) {
                        task.run();
                        BossFunctions.shoot_beam(point, boss, target, this.m, s, helper, tag);
                    }
                }

            }
        }, 0L, 1L));
    }

    public static enum LaserType {
        CHASER,
        STRAIGHT;

        private LaserType() {
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

