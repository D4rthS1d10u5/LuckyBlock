package com.LuckyBlock.Inventory.Event;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.Resources.LBEntitiesSpecial;
import com.LuckyBlock.logic.MyTasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.core.inventory.ItemMaker;

import java.util.Arrays;

public class EntitiesGui implements Listener {
    public EntitiesGui() {
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.BLUE + "Lucky Block: Entities");
        Gui.fill_glass(inv);
        inv.addItem(new ItemStack[]{ItemMaker.addEnchant(ItemMaker.createItem(Material.LAVA_BUCKET, 1, 0, ChatColor.GOLD + "Hell Hound", Arrays.asList("", ChatColor.GRAY + "Click to spawn")), LuckyBlock.enchantment_glow, 1)});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.ENDER_PEARL, 1, 0, ChatColor.DARK_PURPLE + "Karl", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.ROTTEN_FLESH, 1, 0, ChatColor.YELLOW + "Bob", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.BONE, 1, 0, ChatColor.BLUE + "Peter", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.SULPHUR, 1, 0, ChatColor.RED + "Elemental Creeper", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.EMERALD, 1, 0, ChatColor.YELLOW + "Lucky Villager", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.addItem(new ItemStack[]{ItemMaker.createItem(Material.SLIME_BALL, 1, 0, ChatColor.GREEN + "Super Slime", Arrays.asList("", ChatColor.GRAY + "Click to spawn"))});
        inv.setItem(inv.getSize() - 11, Gui.getIItem("back"));
        player.openInventory(inv);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null) {
            Inventory inv = event.getInventory();
            if (inv.getTitle().equalsIgnoreCase(ChatColor.BLUE + "Lucky Block: Entities") && event.getWhoClicked() instanceof Player) {
                Player player = (Player)event.getWhoClicked();
                event.setCancelled(true);
                int slot = event.getRawSlot();
                if (slot < 44) {
                    ItemStack item = event.getCurrentItem();
                    player.playSound(player.getLocation(), MyTasks.getSound("lb_gui_getitem"), 1.0F, 0.0F);
                    if (item != null) {
                        if (Gui.isIItem(item, "back")) {
                            LBGui.open(player);
                        } else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                            String name = item.getItemMeta().getDisplayName();
                            if (name.equalsIgnoreCase(ChatColor.GOLD + "Hell Hound")) {
                                LBEntitiesSpecial.spawnHellHound(player, player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.DARK_PURPLE + "Karl")) {
                                LBEntitiesSpecial.spawnKarl(player, player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Bob")) {
                                LBEntitiesSpecial.spawnBob(player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.BLUE + "Peter")) {
                                LBEntitiesSpecial.spawnPeter(player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.RED + "Elemental Creeper")) {
                                LBEntitiesSpecial.spawnElementalCreeper(player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Lucky Villager")) {
                                LBEntitiesSpecial.spawnLuckyVillager(player.getLocation(), true);
                            } else if (name.equalsIgnoreCase(ChatColor.GREEN + "Super Slime")) {
                                LBEntitiesSpecial.spawnSuperSlime(player.getLocation(), true);
                            }
                        }
                    }
                }
            }
        }

    }
}
