package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LBGui implements Listener {
    public LBGui() {
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Lucky Block");
        ItemStack item = ItemMaker.createItem(Material.STAINED_GLASS_PANE, 1, 11, ChatColor.RED + "Lucky Block");
        LBType type = (LBType)LBType.getTypes().get(0);

        for(int x = 0; x < inv.getSize(); ++x) {
            if (x >= 9 && x <= 45) {
                if (x % 9 == 0 || x % 9 == 8) {
                    inv.setItem(x, item);
                }
            } else {
                inv.setItem(x, item);
            }
        }

        inv.setItem(10, ItemMaker.createItem(type.getType(), 1, type.getData(), ChatColor.GOLD + "Lucky Blocks", Arrays.asList("", ChatColor.GRAY + "Click to open")));
        inv.setItem(43, ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Close", Arrays.asList("", ChatColor.GRAY + "Click to close")));
        inv.setItem(11, ItemMaker.createItem(Material.SLIME_BALL, 1, 0, ChatColor.GREEN + "Drops", Arrays.asList("", ChatColor.GRAY + "Click to open")));
        inv.setItem(12, ItemMaker.createItem(Material.SKULL_ITEM, 1, 4, ChatColor.DARK_PURPLE + "Entities", Arrays.asList("", ChatColor.GRAY + "Click to open")));
        inv.setItem(13, ItemMaker.createItem(Material.WORKBENCH, 1, 0, "" + ChatColor.AQUA + ChatColor.BOLD + "Recipes", Arrays.asList("", ChatColor.GRAY + "Click to open")));
        player.openInventory(inv);
    }

    public static void open_drops(Player player, int page) {
        if (page < 1) {
            throw new Error("You can't open that page");
        } else {
            Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Lucky Block: Drops");
            Gui.fill_glass(inv);
            boolean next = false;
            boolean finished = false;
            List<LBDrop> dr = new ArrayList();
            LBDrop[] var9;
            int var8 = (var9 = LBDrop.values()).length;

            int x;
            for(x = 0; x < var8; ++x) {
                LBDrop d = var9[x];
                if (d.isVisible()) {
                    dr.add(d);
                }
            }

            for(int i = (page - 1) * 21; i < page * 21; ++i) {
                if (dr.size() > i) {
                    LBDrop drop = (LBDrop)dr.get(i);
                    ItemStack item = ItemMaker.createItem(Material.BRICK, 1, 0, ChatColor.GREEN + drop.name(), Arrays.asList(""));
                    String a = IObjects.getString("desc.drop." + drop.name().toLowerCase(), false);
                    if (!a.equalsIgnoreCase("null")) {
                        item = ItemMaker.addLore(item, ChatColor.GOLD + a);
                    } else {
                        item = ItemMaker.addLore(item, ChatColor.RED + "no description");
                    }

                    inv.addItem(new ItemStack[]{item});
                    if (LBDrop.values().length > i + 1) {
                        next = true;
                    }
                } else {
                    finished = true;
                }
            }

            if (finished) {
                List<CustomDrop> lst = CustomDropManager.getCustomDrops();

                for(x = 0; x < 10; ++x) {
                    if (lst.size() > x) {
                        CustomDrop cd = (CustomDrop)lst.get(x);
                        if (cd.isVisible()) {
                            ItemStack item = ItemMaker.createItem(Material.STAINED_CLAY, 1, 0, ChatColor.GREEN + cd.getName(), Arrays.asList(""));
                            if (cd.getDescription() != null) {
                                item = ItemMaker.addLore(item, ChatColor.GOLD + cd.getDescription());
                            } else {
                                item = ItemMaker.addLore(item, ChatColor.RED + "no description");
                            }

                            inv.addItem(new ItemStack[]{item});
                        }
                    } else {
                        next = false;
                    }
                }
            }

            if (next) {
                inv.setItem(42, ItemMaker.createItem(Material.ARROW, 1, 0, ChatColor.GREEN + "Next Page"));
            }

            inv.setItem(40, ItemMaker.createItem(Material.ARROW, 1, 0, ChatColor.GREEN + "Current Page: " + page));
            if (page > 1) {
                inv.setItem(37, ItemMaker.createItem(Material.ARROW, 1, 0, ChatColor.GREEN + "Previous Page"));
            }

            inv.setItem(43, ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Back", Arrays.asList("", ChatColor.GRAY + "Click to return")));
            player.openInventory(inv);
        }
    }

    @EventHandler
    public void onClickItem(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getTitle() != null && event.getWhoClicked() instanceof Player) {
            Player player = (Player)event.getWhoClicked();
            String title = inv.getTitle();
            ItemStack item = event.getCurrentItem();
            if (title.equalsIgnoreCase(ChatColor.BLUE + "Lucky Block")) {
                event.setCancelled(true);
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String name = item.getItemMeta().getDisplayName();
                    if (name.equalsIgnoreCase(ChatColor.GOLD + "Lucky Blocks")) {
                        Gui.openLBGui(player, 1);
                    }

                    if (item.getType() == Material.COMPASS && name.equalsIgnoreCase(ChatColor.RED + "Close")) {
                        player.closeInventory();
                    }

                    if (item.getType() == Material.SLIME_BALL && name.equalsIgnoreCase(ChatColor.GREEN + "Drops")) {
                        open_drops(player, 1);
                    }

                    if (item.getType() == Material.SKULL_ITEM && name.equalsIgnoreCase(ChatColor.DARK_PURPLE + "Entities")) {
                        EntitiesGui.open(player);
                    }

                    if (item.getType() == Material.WORKBENCH && name.equalsIgnoreCase("" + ChatColor.AQUA + ChatColor.BOLD + "Recipes")) {
                        openRecipesGui(player);
                    }

                    if (item.getType() == Material.CHEST) {
                        player.getInventory().addItem(new ItemStack[]{ItemMaker.createItem(Material.CHEST, 1, 0, "" + ChatColor.GREEN + ChatColor.BOLD + ChatColor.ITALIC + "Random Chest")});
                    }
                }
            } else if (title.equalsIgnoreCase(ChatColor.BLUE + "Lucky Block: Drops")) {
                event.setCancelled(true);
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back")) {
                        open(player);
                    }

                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page")) {
                        open_drops(player, Gui.getCurrentPage(inv.getItem(40)) + 1);
                    }

                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Previous Page")) {
                        open_drops(player, Gui.getCurrentPage(inv.getItem(40)) - 1);
                    }
                }
            } else if (title.equalsIgnoreCase(ChatColor.BLUE + "Lucky Block: Recipes") && event.getRawSlot() < 54) {
                event.setCancelled(true);
                if (item != null) {
                    if (item.getType() != Material.COMPASS) {
                        LBType type = LBType.fromItem(item);
                        RecipeLB.open(player, item, type.getId(), 1);
                    } else {
                        open(player);
                    }
                }
            }
        }

    }

    public static void openRecipesGui(Player player) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.BLUE + "Lucky Block: Recipes");
        Iterator var3 = LBType.getTypes().iterator();

        while(var3.hasNext()) {
            LBType types = (LBType)var3.next();
            if (!types.disabled) {
                ItemStack item = types.toItemStack();
                ItemMeta itemM = item.getItemMeta();
                itemM.setLore(Arrays.asList("", ChatColor.GRAY + "Click to open"));
                item.setItemMeta(itemM);
                inv.addItem(new ItemStack[]{item});
            }
        }

        inv.setItem(inv.getSize() - 1, ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Back", Arrays.asList("", ChatColor.GRAY + "Click to open the main gui")));
        player.openInventory(inv);
    }
}
