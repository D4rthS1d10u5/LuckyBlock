package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Meteor {

    public void spawn(Location loc) {
        Fireball ball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        this.particles(ball);
        ball.setBounce(true);
        ball.setYield(15.0F);
        ball.setDirection(new Vector(this.randomDir(), -1.0D, this.randomDir()));
    }

    private double randomDir() {
        return (double) RandomUtils.nextInt(24) - 12 / 10.0D;
    }

    private void particles(final Fireball ball) {
        Scheduler.timerAsync(new BukkitRunnable() {
            @Override
            public void run() {
                if (ball.isValid()) {
                    ball.getWorld().spawnParticle(Particle.FLAME, ball.getLocation(), 120, 1.0D, 1.0D, 1.0D, 0.0D);
                } else {
                    Scheduler.cancelTask(this);
                }
            }
        }, 1, 1);
    }
}

