package com.mcgamer199.luckyblock.Enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Lightning extends Enchantment {

    public Lightning(int id) {
        super(id);
    }

    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    public int getMaxLevel() {
        return 10;
    }

    public String getName() {
        return "Lightning";
    }

    public int getId() {
        return 25;
    }

    public int getStartLevel() {
        return 1;
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }
}
