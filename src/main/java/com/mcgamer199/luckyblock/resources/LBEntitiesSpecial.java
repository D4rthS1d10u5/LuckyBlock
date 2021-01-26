package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.customentity.EntityElementalCreeper;
import com.mcgamer199.luckyblock.customentity.EntityLuckyVillager;
import com.mcgamer199.luckyblock.customentity.EntitySuperSlime;
import com.mcgamer199.luckyblock.tags.EntityTags;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class LBEntitiesSpecial {
    public LBEntitiesSpecial() {
    }

    public static void spawnBob(Location loc, boolean noAI) {
        Zombie zombie = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        zombie.setCanPickupItems(true);
        zombie.setCustomName("" + ChatColor.YELLOW + ChatColor.BOLD + "Bob");
        zombie.setCustomNameVisible(true);
        ItemStack item = ItemMaker.createItem(Material.DIAMOND_SWORD, 1, 0);
        item = ItemMaker.addEnchants(item, new int[]{5, 2}, Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT);
        zombie.getEquipment().setItemInMainHand(item);
        ItemStack item1 = ItemMaker.createItem(Material.DIAMOND_HELMET);
        item1 = ItemMaker.addEnchants(item1, new int[]{4, 4, 4, 4, 4}, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE);
        if ((new Random()).nextInt(100) + 1 >= 65) {
            item1 = ItemMaker.addEnchant(item1, Enchantment.THORNS, 3);
        }

        zombie.getEquipment().setHelmet(item1);
        item1.setType(Material.DIAMOND_CHESTPLATE);
        zombie.getEquipment().setChestplate(item1);
        item1.setType(Material.DIAMOND_LEGGINGS);
        zombie.getEquipment().setLeggings(item1);
        item1.setType(Material.DIAMOND_BOOTS);
        zombie.getEquipment().setBoots(item1);
        zombie.getEquipment().setItemInMainHandDropChance(0.0F);
        zombie.getEquipment().setBootsDropChance(0.3F);
        zombie.getEquipment().setLeggingsDropChance(0.2F);
        zombie.getEquipment().setChestplateDropChance(0.15F);
        zombie.getEquipment().setHelmetDropChance(0.25F);
        zombie.setMaxHealth(65.0D);
        zombie.setHealth(65.0D);
        zombie.setBaby(false);
        if (noAI) {
            zombie.setAI(false);
        }

    }

    public static void spawnPeter(Location loc, boolean noAI) {
        Skeleton skeleton = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
        skeleton.setCanPickupItems(true);
        skeleton.setCustomName("" + ChatColor.GRAY + ChatColor.BOLD + "Peter");
        skeleton.setCustomNameVisible(true);
        ItemStack item = ItemMaker.createItem(Material.BOW, 1, 0);
        item = ItemMaker.addEnchants(item, new int[]{5, 1}, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE);
        skeleton.getEquipment().setItemInMainHand(item);
        ItemStack item1 = ItemMaker.createItem(Material.DIAMOND_HELMET);
        item1 = ItemMaker.addEnchants(item1, new int[]{4, 4, 4, 4, 4}, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE);
        skeleton.getEquipment().setHelmet(item1);
        item1.setType(Material.DIAMOND_CHESTPLATE);
        skeleton.getEquipment().setChestplate(item1);
        item1.setType(Material.DIAMOND_LEGGINGS);
        skeleton.getEquipment().setLeggings(item1);
        item1.setType(Material.DIAMOND_BOOTS);
        skeleton.getEquipment().setBoots(item1);
        skeleton.getEquipment().setItemInMainHandDropChance(0.0F);
        skeleton.getEquipment().setBootsDropChance(0.2F);
        skeleton.getEquipment().setLeggingsDropChance(0.1F);
        skeleton.getEquipment().setChestplateDropChance(0.05F);
        skeleton.getEquipment().setHelmetDropChance(0.15F);
        skeleton.setMaxHealth(50.0D);
        skeleton.setHealth(50.0D);
        if (noAI) {
            skeleton.setAI(false);
        }

    }

    public static void spawnKarl(Player player, Location loc, boolean noAI) {
        Enderman karl = (Enderman) loc.getWorld().spawnEntity(loc, EntityType.ENDERMAN);
        karl.setCarriedMaterial(new MaterialData(Material.TNT));
        karl.setCustomName("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Karl");
        karl.setCustomNameVisible(true);
        karl.setMaxHealth(45.0D);
        karl.setHealth(45.0D);
        if (player != null) {
            karl.setTarget(player);
        }

        if (noAI) {
            karl.setAI(false);
        }

    }

    public static void spawnHellHound(Player player, Location loc, boolean noAI) {
        Wolf hellHund = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
        hellHund.setAngry(true);
        hellHund.setAgeLock(true);
        hellHund.setCollarColor(DyeColor.RED);
        hellHund.setCustomName(ChatColor.GOLD + "Hell Hound");
        hellHund.setCustomNameVisible(true);
        hellHund.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 0));
        hellHund.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, 0));
        hellHund.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000, 0));
        hellHund.setMaxHealth(60.0D);
        hellHund.setHealth(60.0D);
        hellHund.setRemoveWhenFarAway(false);
        hellHund.setFireTicks(100000);
        EntityTags.addRandomDrops(hellHund.getUniqueId(), new double[]{100.0D}, new ItemStack(Material.LAVA_BUCKET));
        if (player != null) {
            hellHund.setTarget(player);
        }

        if (noAI) {
            hellHund.setAI(false);
        }

    }

    public static void spawnElementalCreeper(Location loc, boolean noAI) {
        EntityElementalCreeper e = new EntityElementalCreeper();
        e.spawn(loc);
        if (noAI) {
            ((Creeper) e.getEntity()).setAI(false);
        }

    }

    public static void spawnLuckyVillager(Location loc, boolean noAI) {
        EntityLuckyVillager v = new EntityLuckyVillager();
        v.spawn(loc);
        if (noAI) {
            ((Villager) v.getEntity()).setAI(false);
        }

    }

    public static void spawnSuperSlime(Location loc, boolean noAI) {
        EntitySuperSlime s = new EntitySuperSlime();
        s.spawn(loc);
        if (noAI) {
            ((Slime) s.getEntity()).setAI(false);
        }

    }
}
