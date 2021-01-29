package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Gui extends ColorsClass implements Listener {

    public Gui() {
    }

    public static void openLBGui(Player player, int page) {
        if (player.hasPermission("lb.gui")) {
            if (page >= 1) {
                int size = 9;
                boolean show = false;
                List<LBType> tps = LBType.getTypes();
                if (tps.size() > 9 && tps.size() < 19) {
                    size = 18;
                } else if (tps.size() > 18 && tps.size() < 28) {
                    size = 27;
                } else if (tps.size() > 27 && tps.size() < 37) {
                    size = 36;
                } else if (tps.size() > 36) {
                    size = 45;
                }

                Inventory inv = Bukkit.createInventory(player, size + 9, yellow + "[Lucky Blocks]");

                for (int x = (page - 1) * size; x < page * size + 1; ++x) {
                    if (tps.size() > x) {
                        LBType type = tps.get(x);
                        inv.addItem(type.toItemStack(0));
                        if (x == page * size && tps.size() > x + 1) {
                            show = true;
                        }
                    }
                }

                inv.setItem(inv.getSize() - 1, ItemMaker.createItem(Material.COMPASS, 1, 0, red + "Back", Arrays.asList(gray + "Click to return")));
                if (show) {
                    inv.setItem(inv.getSize() - 2, ItemMaker.createItem(Material.ARROW, 1, 0, green + "Next Page", Arrays.asList(gray + "Click to open next page")));
                }

                if (page > 1) {
                    inv.setItem(inv.getSize() - 8, ItemMaker.createItem(Material.ARROW, 1, 0, green + "Previous Page", Arrays.asList(gray + "Click to open previous page")));
                }

                inv.setItem(inv.getSize() - 5, ItemMaker.createItem(Material.ARROW, 1, 0, green + "Current Page: " + page));
                player.openInventory(inv);
            }
        }
    }

    public static int getCurrentPage(ItemStack item) {
        int page = 0;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(ChatColor.GREEN + "Current Page:")) {
            String[] d = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split("Current Page: ");
            if (d.length == 2) {
                page = Integer.parseInt(d[1]);
            }
        }

        return page;
    }

    public static void fill_glass(Inventory inv) {
        ItemStack item = ItemMaker.createItem(Material.STAINED_GLASS_PANE, 1, 11, ChatColor.RED + "Lucky Block");

        for (int x = 0; x < inv.getSize(); ++x) {
            if (x >= 9 && x <= inv.getSize() - 9) {
                if (x % 9 == 0 || x % 9 == 8) {
                    inv.setItem(x, item);
                }
            } else {
                inv.setItem(x, item);
            }
        }

    }

    public static ItemStack getIItem(String name) {
        ItemStack item = null;
        if (name.equalsIgnoreCase("back")) {
            item = ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Back", Arrays.asList("", ChatColor.GRAY + "Click to back"));
        } else if (name.equalsIgnoreCase("close")) {
            item = ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Close", Arrays.asList("", ChatColor.GRAY + "Click to close"));
        }

        return item;
    }

    public static boolean isIItem(ItemStack item, String name) {
        if (item == null) {
            return false;
        } else {
            if (name.equalsIgnoreCase("back")) {
                return item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back");
            } else return name.equalsIgnoreCase("close") && item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close");
        }
    }

    @EventHandler
    private void onClickLBGui(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase(yellow + "[Lucky Blocks]")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getWhoClicked() instanceof Player) {
                ItemStack item = event.getCurrentItem();
                Player player = (Player) event.getWhoClicked();
                Inventory inv = event.getInventory();
                if (LBType.isLB(item)) {
                    player.getInventory().addItem(item);
                    player.playSound(player.getLocation(), SoundManager.getSound("lb_gui_getitem"), 1.0F, 0.0F);
                } else if (item.getType() == Material.COMPASS) {
                    LBGui.open(player);
                } else if (item.getType() == Material.ARROW && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page")) {
                        openLBGui(player, getCurrentPage(inv.getItem(inv.getSize() - 5)) + 1);
                    } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Previous Page")) {
                        int p = getCurrentPage(inv.getItem(inv.getSize() - 5));
                        if (p > 1) {
                            openLBGui(player, p - 1);
                        }
                    }
                }
            }
        }

    }
}

