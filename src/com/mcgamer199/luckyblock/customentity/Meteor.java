package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;
import com.mcgamer199.luckyblock.logic.SchedulerTask;

import java.util.Random;

public class Meteor {
    Random random = new Random();

    public Meteor() {
    }

    public void spawn(Location loc) {
        Fireball ball = (Fireball)loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        this.particles(ball);
        ball.setBounce(true);
        ball.setYield(15.0F);
        ball.setDirection(new Vector(this.randomDir(), -1.0D, this.randomDir()));
    }

    private double randomDir() {
        int h = this.random.nextInt(24) - 12;
        double g = (double)h / 10.0D;
        return g;
    }

    private void particles(final Fireball ball) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (ball.isValid()) {
                    ball.getWorld().spawnParticle(Particle.FLAME, ball.getLocation(), 120, 1.0D, 1.0D, 1.0D, 0.0D);
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }
}

