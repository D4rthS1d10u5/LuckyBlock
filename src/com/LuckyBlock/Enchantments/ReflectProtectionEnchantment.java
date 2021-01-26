package com.LuckyBlock.Enchantments;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class ReflectProtectionEnchantment extends Enchantment {

    public ReflectProtectionEnchantment(int id) {
        super(id);
    }

    public boolean canEnchantItem(ItemStack item) {
        return item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.IRON_CHESTPLATE || item.getType() == Material.GOLD_CHESTPLATE || item.getType() == Material.CHAINMAIL_CHESTPLATE || item.getType() == Material.DIAMOND_CHESTPLATE;
    }

    public boolean conflictsWith(Enchantment ench) {
        return false;
    }

    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_TORSO;
    }

    public int getMaxLevel() {
        return 3;
    }

    public String getName() {
        return "Reflect Protection";
    }

    public int getStartLevel() {
        return 1;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean isTreasure() {
        return false;
    }
}
