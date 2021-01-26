package com.mcgamer199.luckyblock.customentity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;

import java.util.Iterator;

public class EntityTalkingZombie extends CustomEntity {
    String[] texts = new String[]{"Stop hitting me!", "Ouch!", "Stop it!", "Don't kill me!", "Hey stop!", "How dare you!", "Stop"};
    private Zombie z;

    public EntityTalkingZombie() {
    }

    protected Entity spawnFunction(Location loc) {
        Zombie zombie = (Zombie)loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0D);
        zombie.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(6.0D);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25D);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(7.0D);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25.0D);
        zombie.setHealth(25.0D);
        zombie.getEquipment().setHelmetDropChance(1.0F);
        zombie.getEquipment().setChestplateDropChance(1.0F);
        zombie.getEquipment().setLeggingsDropChance(1.0F);
        zombie.getEquipment().setBootsDropChance(1.0F);
        zombie.setCustomName(ChatColor.GREEN + "Talking Zombie");
        zombie.setCustomNameVisible(true);
        this.z = zombie;
        return zombie;
    }

    protected int getTickTime() {
        return 2;
    }

    protected void onTick() {
        this.z.setFireTicks(0);
        if (this.z.getTarget() != null && this.z.getTarget() instanceof Player) {
            Player p = (Player)this.z.getTarget();
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                boolean c = false;
                if ((this.z.getEquipment().getHelmet() == null || this.z.getEquipment().getHelmet().getType() == Material.AIR) && p.getEquipment().getHelmet() != null && p.getEquipment().getHelmet().getType() != Material.AIR) {
                    this.z.getEquipment().setHelmet(p.getEquipment().getHelmet());
                    p.getEquipment().setHelmet((ItemStack)null);
                    c = true;
                }

                if ((this.z.getEquipment().getChestplate() == null || this.z.getEquipment().getChestplate().getType() == Material.AIR) && p.getEquipment().getChestplate() != null && p.getEquipment().getChestplate().getType() != Material.AIR) {
                    this.z.getEquipment().setChestplate(p.getEquipment().getChestplate());
                    p.getEquipment().setChestplate((ItemStack)null);
                    c = true;
                }

                if ((this.z.getEquipment().getLeggings() == null || this.z.getEquipment().getLeggings().getType() == Material.AIR) && p.getEquipment().getLeggings() != null && p.getEquipment().getLeggings().getType() != Material.AIR) {
                    this.z.getEquipment().setLeggings(p.getEquipment().getLeggings());
                    p.getEquipment().setLeggings((ItemStack)null);
                    c = true;
                }

                if ((this.z.getEquipment().getBoots() == null || this.z.getEquipment().getBoots().getType() == Material.AIR) && p.getEquipment().getBoots() != null && p.getEquipment().getBoots().getType() != Material.AIR) {
                    this.z.getEquipment().setBoots(p.getEquipment().getBoots());
                    p.getEquipment().setBoots((ItemStack)null);
                    c = true;
                }

                if (c) {
                    p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "Zombie" + ChatColor.RED + ChatColor.BOLD + "]" + ChatColor.RESET + ": " + ChatColor.RED + "Now give me your armour!");
                }
            }
        }

    }

    protected void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if (this.random.nextInt(100) > 60) {
            this.talk(ChatColor.RED + this.texts[this.random.nextInt(this.texts.length)], 5);
        }

    }

    public double getDefense() {
        return 3.0D;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.FIRE, Immunity.FIRE_TICK};
    }

    protected boolean defaultDrops() {
        return true;
    }

    public int getXp() {
        int total = 30;
        if (this.z.getEquipment().getHelmet() != null) {
            total += 10;
        }

        if (this.z.getEquipment().getChestplate() != null) {
            total += 20;
        }

        if (this.z.getEquipment().getLeggings() != null) {
            total += 15;
        }

        if (this.z.getEquipment().getBoots() != null) {
            total += 5;
        }

        return total;
    }

    public String getSpawnEggEntity() {
        return "spider";
    }

    void talk(String msg, int d) {
        Iterator var4 = Bukkit.getOnlinePlayers().iterator();

        while(var4.hasNext()) {
            Player p = (Player)var4.next();
            if (this.entity.getLocation().distance(p.getLocation()) < (double)(d + 1)) {
                p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "Zombie" + ChatColor.RED + ChatColor.BOLD + "]" + ChatColor.RESET + ": " + msg);
            }
        }

    }

    protected void onLoad(ConfigurationSection c) {
        this.z = (Zombie)this.entity;
    }
}
