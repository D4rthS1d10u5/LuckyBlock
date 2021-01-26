package com.LuckyBlock.Resources;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.core.inventory.ItemMaker;

import java.util.Arrays;

public enum LBItem {
    LUCKY_TOOL(ItemMaker.createItem(Material.CARROT_STICK, 1, 0, ChatColor.YELLOW + "Lucky Block Tool", Arrays.asList("", ChatColor.GRAY + "Right click to use"))),
    A_LUCKY_TOOL(ItemMaker.createItem(Material.CARROT_STICK, 1, 0, ChatColor.YELLOW + "Advanced Lucky Block Tool", Arrays.asList("", ChatColor.GRAY + "Right click to use"))),
    THOR_AXE(ItemMaker.addEnchant(ItemMaker.createItem(Material.IRON_AXE, 1, 0, ChatColor.AQUA + "Thor's axe"), LuckyBlock.enchantment_lightning, 10)),
    LB_BOW(ItemMaker.createItem(Material.BOW, 1, 0, "" + ChatColor.GOLD + ChatColor.BOLD + "LB Bow", Arrays.asList("", ChatColor.GRAY + "Shoots lucky blocks!"))),
    DETECTOR(ItemMaker.createItem(Material.PISTON_BASE, 1, (short)0, "" + ChatColor.BLUE + ChatColor.BOLD + "Detector", Arrays.asList("", ChatColor.GRAY + "Place it"))),
    LB_REMOVER(ItemMaker.createItem(Material.STICK, 1, 0, ChatColor.BLUE + "Lucky Block Remover", Arrays.asList("", ChatColor.GRAY + "Right click to remove lucky block."))),
    KEY_1(ItemMaker.addEnchant(ItemMaker.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 1", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlock.enchantment_glow, 1)),
    KEY_2(ItemMaker.addEnchant(ItemMaker.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 2", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlock.enchantment_glow, 1)),
    KEY_3(ItemMaker.addEnchant(ItemMaker.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 3", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlock.enchantment_glow, 1)),
    KEY_4(ItemMaker.addEnchant(ItemMaker.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 4", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlock.enchantment_glow, 1)),
    KEY_5(ItemMaker.addEnchant(ItemMaker.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 5", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlock.enchantment_glow, 1));

    private ItemStack item;

    private LBItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void giveTo(Player player) {
        player.getInventory().addItem(new ItemStack[]{this.item});
    }

    public static boolean isValid(String name) {
        LBItem[] var4;
        int var3 = (var4 = values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            LBItem i = var4[var2];
            if (i.name().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
