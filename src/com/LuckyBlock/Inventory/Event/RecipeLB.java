package com.LuckyBlock.Inventory.Event;

import com.LuckyBlock.LB.LBType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.core.inventory.ItemMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeLB implements Listener {

    public RecipeLB() {
    }

    public static void open(Player player, ItemStack item, int id, int page) {
        Inventory inv = Bukkit.createInventory(player, 45, ChatColor.BLUE + "LB Recipes#ID:" + id + "#Page:" + page);
        ItemStack itemN = ItemMaker.createItem(Material.STAINED_GLASS_PANE, 1, 15, ChatColor.RED + "Locked");

        for(int x = 0; x < inv.getSize(); ++x) {
            inv.setItem(x, itemN);
        }

        inv.setItem(12, (ItemStack)null);
        inv.setItem(13, (ItemStack)null);
        inv.setItem(14, (ItemStack)null);
        inv.setItem(21, (ItemStack)null);
        inv.setItem(22, (ItemStack)null);
        inv.setItem(23, (ItemStack)null);
        inv.setItem(30, (ItemStack)null);
        inv.setItem(31, (ItemStack)null);
        inv.setItem(32, (ItemStack)null);
        inv.setItem(25, (ItemStack)null);
        List<ShapedRecipe> recipes = new ArrayList();

        for(int x = 0; x < Bukkit.getRecipesFor(item).size(); ++x) {
            Recipe recipe = (Recipe)Bukkit.getRecipesFor(item).get(x);
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe r = (ShapedRecipe)recipe;
                recipes.add(r);
            }
        }

        inv.setItem(0, ItemMaker.createItem(Material.COMPASS, 1, 0, ChatColor.RED + "Back", Arrays.asList("", ChatColor.GRAY + "Click to open recipes gui")));
        if (page > 1) {
            inv.setItem(inv.getSize() - 9, ItemMaker.createItem(Material.ARROW, 1, 0, ChatColor.GREEN + "Prev page", Arrays.asList("", ChatColor.GRAY + "Click to open previous page")));
        }

        if (recipes.size() > page) {
            inv.setItem(inv.getSize() - 1, ItemMaker.createItem(Material.ARROW, 1, 0, ChatColor.GREEN + "Next page", Arrays.asList("", ChatColor.GRAY + "Click to open next page")));
        }

        if (recipes.size() > 0) {
            Recipe r = (Recipe)recipes.get(page - 1);
            inv.setItem(25, r.getResult());
            if (r instanceof ShapedRecipe) {
                ShapedRecipe s = (ShapedRecipe)r;
                inv.setItem(12, (ItemStack)s.getIngredientMap().get(s.getShape()[0].charAt(0)));
                inv.setItem(13, (ItemStack)s.getIngredientMap().get(s.getShape()[0].charAt(1)));
                inv.setItem(14, (ItemStack)s.getIngredientMap().get(s.getShape()[0].charAt(2)));
                inv.setItem(21, (ItemStack)s.getIngredientMap().get(s.getShape()[1].charAt(0)));
                inv.setItem(22, (ItemStack)s.getIngredientMap().get(s.getShape()[1].charAt(1)));
                inv.setItem(23, (ItemStack)s.getIngredientMap().get(s.getShape()[1].charAt(2)));
                inv.setItem(30, (ItemStack)s.getIngredientMap().get(s.getShape()[2].charAt(0)));
                inv.setItem(31, (ItemStack)s.getIngredientMap().get(s.getShape()[2].charAt(1)));
                inv.setItem(32, (ItemStack)s.getIngredientMap().get(s.getShape()[2].charAt(2)));
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    private void onClickItem(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getTitle() != null) {
            String title = inv.getTitle();
            if (title.startsWith(ChatColor.BLUE + "LB Recipes")) {
                String[] d = ChatColor.stripColor(title).split("#");
                String[] d1 = d[1].split("ID:");
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player) {
                    Player player = (Player)event.getWhoClicked();
                    ItemStack item = event.getCurrentItem();
                    if (item != null && item.getType() != Material.AIR) {
                        if (item.getType() == Material.ARROW) {
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                int s = Integer.parseInt(d1[1]);
                                ItemStack i = null;
                                i = LBType.fromId(s).toItemStack();
                                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next page")) {
                                    open(player, i, s, getPage(title) + 1);
                                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Prev page")) {
                                    open(player, i, s, getPage(title) - 1);
                                }
                            }
                        } else if (item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back")) {
                            LBGui.openRecipesGui(player);
                        }
                    }
                }
            }
        }

    }

    static int getPage(String title) {
        title = ChatColor.stripColor(title);
        String[] d = title.split("#");
        if (d.length == 3) {
            String[] s = d[2].split("Page:");
            return Integer.parseInt(s[1]);
        } else {
            return 0;
        }
    }
}

