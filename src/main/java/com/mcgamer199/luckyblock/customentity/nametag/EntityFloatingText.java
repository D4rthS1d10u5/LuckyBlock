package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityFloatingText extends CustomEntity {
    public int mode = 0;
    public int age = 20;
    public int b = 80;
    public String text = "No Text";

    public EntityFloatingText() {
    }

    protected Entity spawnFunction(Location loc) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setCustomName(this.text);
        as.setCustomNameVisible(true);
        as.setGravity(false);
        as.setMarker(true);
        as.setBasePlate(false);
        this.run(as);
        this.func_age();
        return as;
    }

    private void run(ArmorStand a) {
        if (this.mode == 1) {
            this.run_1(a);
        } else if (this.mode == 2) {
            this.run_2(a);
        }

    }

    private void run_1(final ArmorStand a) {
        Scheduler.timer(new BukkitRunnable() {
            double iY = 1.0D;
            int i = EntityFloatingText.this.b;

            @Override
            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    if (this.i > 0) {
                        this.iY = Math.sin(this.i);
                        this.iY = Math.abs(this.iY);
                        a.teleport(a.getLocation().add(0.0D, this.iY / 30.0D, 0.0D));
                        this.i -= 4;
                    }
                } else {
                    Scheduler.cancelTask(this);
                }
            }
        }, 1,1);
    }

    private void run_2(final ArmorStand a) {
        Scheduler.timer(new BukkitRunnable() {
            double iX = 1.0D;
            int i = 0;

            @Override
            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    this.iX = Math.sin(this.i);
                    a.teleport(a.getLocation().add(this.iX / 20.0D, 0.1D, 0.0D));
                    ++this.i;
                } else {
                    Scheduler.cancelTask(this);
                }
            }
        }, 1, 1);
    }

    private void func_age() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    --EntityFloatingText.this.age;
                } else {
                    EntityFloatingText.this.remove();
                    Scheduler.cancelTask(this);
                }
            }
        }, 1, 1);
    }

    protected void onLoad(ConfigurationSection c) {
        this.remove();
    }
}
