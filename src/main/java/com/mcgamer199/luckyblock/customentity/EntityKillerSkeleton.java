package com.mcgamer199.luckyblock.customentity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.entity.Immunity;
import com.mcgamer199.luckyblock.inventory.event.ItemMaker;

public class EntityKillerSkeleton extends EntityKiller {
    public EntityKillerSkeleton() {
    }

    public Entity spawnFunction(Location loc) {
        WitherSkeleton skeleton = (WitherSkeleton)loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
        skeleton.setMaxHealth(120.0D);
        skeleton.setHealth(120.0D);
        skeleton.setCustomName(ChatColor.RED + "Killer Skeleton");
        skeleton.setCustomNameVisible(true);
        skeleton.getEquipment().setItemInMainHand(ItemMaker.addEnchants(new ItemStack(Material.BOW), new int[]{3}, new Enchantment[]{Enchantment.ARROW_DAMAGE}));
        skeleton.getEquipment().setHelmet(new ItemStack(Material.GLASS));
        skeleton.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        skeleton.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        skeleton.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
        return skeleton;
    }

    public double getDefense() {
        return 2.0D;
    }

    public Particle getDeathParticles() {
        return Particle.CRIT_MAGIC;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.PROJECTILE};
    }

    public int getXp() {
        return this.random.nextInt(350) + 200;
    }
}

