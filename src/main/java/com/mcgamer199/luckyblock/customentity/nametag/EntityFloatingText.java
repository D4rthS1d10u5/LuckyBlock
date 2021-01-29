package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

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
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            double iY = 1.0D;
            int i;

            {
                this.i = EntityFloatingText.this.b;
            }

            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    if (this.i > 0) {
                        this.iY = Math.sin(this.i);
                        this.iY = Math.abs(this.iY);
                        a.teleport(a.getLocation().add(0.0D, this.iY / 30.0D, 0.0D));
                        this.i -= 4;
                    }
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    private void run_2(final ArmorStand a) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            double iX = 1.0D;
            int i = 0;

            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    this.iX = Math.sin(this.i);
                    a.teleport(a.getLocation().add(this.iX / 20.0D, 0.1D, 0.0D));
                    ++this.i;
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    private void func_age() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityFloatingText.this.age > 0) {
                    --EntityFloatingText.this.age;
                } else {
                    EntityFloatingText.this.remove();
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    protected void onLoad(ConfigurationSection c) {
        this.remove();
    }
}
