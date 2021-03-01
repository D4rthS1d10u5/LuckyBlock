package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum LBItem {
    LUCKY_TOOL(ItemStackUtils.createItem(Material.CARROT_STICK, 1, 0, ChatColor.YELLOW + "Lucky Block Tool", Arrays.asList("", ChatColor.GRAY + "Right click to use"))),
    A_LUCKY_TOOL(ItemStackUtils.createItem(Material.CARROT_STICK, 1, 0, ChatColor.YELLOW + "Advanced Lucky Block Tool", Arrays.asList("", ChatColor.GRAY + "Right click to use"))),
    THOR_AXE(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.IRON_AXE, 1, 0, ChatColor.AQUA + "Thor's axe"), LuckyBlockPlugin.enchantment_lightning, 10)),
    LB_BOW(ItemStackUtils.createItem(Material.BOW, 1, 0, "" + ChatColor.GOLD + ChatColor.BOLD + "LB Bow", Arrays.asList("", ChatColor.GRAY + "Shoots lucky blocks!"))),
    DETECTOR(ItemStackUtils.createItem(Material.PISTON_BASE, 1, (short) 0, "" + ChatColor.BLUE + ChatColor.BOLD + "Detector", Arrays.asList("", ChatColor.GRAY + "Place it"))),
    LB_REMOVER(ItemStackUtils.createItem(Material.STICK, 1, 0, ChatColor.BLUE + "Lucky Block Remover", Arrays.asList("", ChatColor.GRAY + "Right click to remove lucky block."))),
    KEY_1(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 1", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlockPlugin.enchantment_glow, 1)),
    KEY_2(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 2", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlockPlugin.enchantment_glow, 1)),
    KEY_3(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 3", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlockPlugin.enchantment_glow, 1)),
    KEY_4(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 4", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlockPlugin.enchantment_glow, 1)),
    KEY_5(ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.TRIPWIRE_HOOK, 1, 0, ChatColor.GOLD + "Boss Key 5", Arrays.asList("", ChatColor.GRAY + "Obtain all 5 keys to get into the final dungeon")), LuckyBlockPlugin.enchantment_glow, 1));

    private final ItemStack item;

    LBItem(ItemStack item) {
        this.item = item;
    }

    public static boolean isValid(String name) {
        LBItem[] var4;
        int var3 = (var4 = values()).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            LBItem i = var4[var2];
            if (i.name().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void giveTo(Player player) {
        player.getInventory().addItem(this.item);
    }
}
