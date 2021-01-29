package com.mcgamer199.luckyblock.customentity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class EntitySuperSlime extends CustomEntity {
    int size = 2;

    public EntitySuperSlime() {
    }

    public Entity spawnFunction(Location loc) {
        Slime slime = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);
        slime.setSize(this.size);
        slime.setMaxHealth(20.0D);
        slime.setHealth(20.0D);
        slime.setCustomName("Super Slime");
        slime.setCustomNameVisible(true);
        return slime;
    }

    protected void onTick() {
        Iterator var2 = this.entity.getNearbyEntities(6.0D, 6.0D, 6.0D).iterator();

        while (var2.hasNext()) {
            Entity e = (Entity) var2.next();
            if (e instanceof LivingEntity) {
                LivingEntity l = (LivingEntity) e;
                if (!(l instanceof Player) && l.getHealth() < l.getMaxHealth() && l.getHealth() > 0.0D) {
                    try {
                        l.setHealth(l.getHealth() + 3.0D);
                    } catch (Exception var6) {
                        l.setHealth(l.getMaxHealth());
                    }

                    this.entity.getWorld().spawnParticle(Particle.HEART, l.getLocation(), 30, 0.7D, 0.7D, 0.7D, 0.0D);
                }
            }
        }

        LivingEntity l = (LivingEntity) this.entity;
        if (l.getHealth() < l.getMaxHealth() && l.getHealth() > 0.0D) {
            try {
                l.setHealth(l.getHealth() + 3.0D);
            } catch (Exception var5) {
                l.setHealth(l.getMaxHealth());
            }

            this.entity.getWorld().spawnParticle(Particle.HEART, l.getLocation(), 28, 0.7D, 0.7D, 0.7D, 0.0D);
        }

    }

    protected int getNamesDelay() {
        return 25;
    }

    protected List<String> getNames() {
        return Arrays.asList(ChatColor.GREEN + "Super Slime", ChatColor.DARK_GREEN + "Super Slime");
    }

    protected boolean isAnimated() {
        return true;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getDefense() {
        return 1.5D;
    }

    public int getAttackDamage() {
        return 15;
    }

    public int getXp() {
        return 150;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.POISON};
    }

    protected int getTickTime() {
        return 85;
    }

    protected void onSlimeSplit(SlimeSplitEvent event) {
        event.setCancelled(true);
        if (this.size > 1 && this.random.nextInt(100) + 1 > 30) {
            int i = this.random.nextInt(2) + 2;

            for (int x = i; x > 0; --x) {
                EntitySuperSlime superSlime = new EntitySuperSlime();
                superSlime.size = this.size - 1;
                superSlime.spawn(this.entity.getLocation());
            }
        }

    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{new ItemStack(Material.EXP_BOTTLE, this.random.nextInt(3) + 2)};
    }

    protected int[] getPercents() {
        return new int[]{65};
    }

    public String getSpawnEggEntity() {
        return "Slime";
    }
}
