package com.mcgamer199.luckyblock.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.api.item.ItemMaker;

public class BossPalace extends Structure {
    static final ItemStack[] items;
    static final int[] chances;

    static {
        items = new ItemStack[]{ItemMaker.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{10}, new Enchantment[]{Enchantment.DAMAGE_ALL})};
        chances = new int[]{100};
    }

    public BossPalace() {
    }

    public void build(Location loc) {
        super.build(loc);
    }

    public int getId() {
        return 18;
    }
}
