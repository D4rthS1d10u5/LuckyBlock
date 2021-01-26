package com.mcgamer199.luckyblock.event.lb;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.events.LBCraftEvent;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.TitleSender;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.customentity.nametag.EntityFloatingText;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import com.mcgamer199.luckyblock.inventory.event.ItemMaker;
import com.mcgamer199.luckyblock.nbt.ItemNBT;
import com.mcgamer199.luckyblock.nbt.ItemReflection;

import java.util.Arrays;
import java.util.Iterator;

public class CraftLB extends ColorsClass implements Listener {
    public CraftLB() {
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (block != null && LuckyCraftingTable.getByBlock(block) != null) {
                event.setCancelled(true);
                if (!player.isSneaking()) {
                    if (player.hasPermission("lb.viewlct")) {
                        LuckyCraftingTable.getByBlock(block).open(player);
                    } else {
                        TestSend(player, "lct.view.no_permission");
                    }
                }
            }
        }

    }

    @EventHandler
    public void onClickItem(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase(red + val("lct.display_name", false))) {
            int rawSlot = event.getRawSlot();
            if (!event.getWhoClicked().hasPermission("lb.uselct")) {
                send(event.getWhoClicked(), "lct.use.no_permission");
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getWhoClicked() instanceof Player) {
                Player player = (Player)event.getWhoClicked();
                ItemStack item = event.getCurrentItem();
                Inventory inv = event.getInventory();
                if (rawSlot % 9 < 3 && rawSlot < 26 && this.getLTable(inv.getItem(inv.getSize() - 17)) != null) {
                    LuckyCraftingTable cr = this.getLTable(inv.getItem(inv.getSize() - 17));
                    if (cr.isRunning() && rawSlot == cr.getSlot()) {
                        event.setCancelled(true);
                    }
                }

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && event.getRawSlot() < 54) {
                    String name = item.getItemMeta().getDisplayName();
                    if ((item.getType() == Material.STAINED_GLASS_PANE || item.getType() == Material.BARRIER) && name.equalsIgnoreCase(red + val("lct.gui.itemlocked.name", false))) {
                        event.setCancelled(true);
                    }

                    if (item.getType() == Material.COMPASS && name.equalsIgnoreCase(red + val("lct.gui.itemclose.name", false))) {
                        event.setCancelled(true);
                        player.closeInventory();
                    }

                    String a;
                    if (item.getType() == Material.EMERALD && name.equalsIgnoreCase(yellow + val("lct.gui.itemtotal.name", false))) {
                        event.setCancelled(true);
                        ItemMeta itemM = item.getItemMeta();
                        a = this.canCraft(inv);
                        if (a.equalsIgnoreCase("success")) {
                            int total = getTotalLuck(inv);
                            String displayLuck = "";
                            if (total > 0) {
                                displayLuck = "" + ChatColor.GREEN + total;
                            } else if (total == 0) {
                                displayLuck = "" + ChatColor.GOLD + total;
                            } else {
                                displayLuck = "" + ChatColor.RED + total;
                            }

                            String g = val("lct.data.main.luck", false);
                            if (total > -1) {
                                itemM.setLore(Arrays.asList("", aqua + g + ": +" + displayLuck));
                            } else {
                                itemM.setLore(Arrays.asList("", aqua + g + ": " + displayLuck));
                            }

                            item.setItemMeta(itemM);
                        } else {
                            player.closeInventory();
                            if (a.equalsIgnoreCase("invalid_items")) {
                                TestSend(player, "lct.error4");
                            } else if (a.equalsIgnoreCase("no_item_found")) {
                                TestSend(player, "lct.error3");
                            } else if (a.equalsIgnoreCase("more_than_one")) {
                                TestSend(player, "lct.error2");
                            }
                        }
                    }

                    LuckyCraftingTable cr;
                    String s;
                    int p;
                    if (item.getType() == Material.NETHER_STAR && name.equalsIgnoreCase(yellow + val("lct.display_name", false))) {
                        event.setCancelled(true);
                        cr = this.getLTable(inv.getItem(inv.getSize() - 17));
                        if (cr.getPlayer() != null && !player.getName().equalsIgnoreCase(cr.getPlayer())) {
                            return;
                        }

                        com.mcgamer199.luckyblock.tellraw.RawText raw = new com.mcgamer199.luckyblock.tellraw.RawText("[" + val("lct.data.viewers", false) + "]");
                        if (inv.getViewers().size() > 1) {
                            raw.bold = true;
                        }

                        s = val("lct.data.none", false);
                        if (inv.getViewers().size() > 0) {
                            p = 0;
                            Iterator var12 = inv.getViewers().iterator();

                            while(var12.hasNext()) {
                                HumanEntity h = (HumanEntity)var12.next();
                                if (h != player) {
                                    if (p == 0) {
                                        s = gold + h.getName();
                                    } else if (p % 3 == 0) {
                                        s = s + "\n" + gold + h.getName();
                                    } else {
                                        s = s + white + "," + gold + h.getName();
                                    }

                                    ++p;
                                }
                            }
                        }

                        raw.addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, s));
                        raw.sendTo(new Player[]{player});
                    }

                    if (item.getType() == Material.REDSTONE) {
                        if (name.equalsIgnoreCase(green + val("lct.gui.iteminsert.name", false))) {
                            event.setCancelled(true);
                            String craftResult = this.canCraft(inv);
                            if (craftResult.equalsIgnoreCase("success")) {
                                if (getTotalLuck(inv) != 0) {
                                    this.insert(inv);
                                }
                            } else if (craftResult.equalsIgnoreCase("invalid_items")) {
                                player.closeInventory();
                                TestSend(player, "lct.error4");
                            } else if (craftResult.equalsIgnoreCase("no_item_found")) {
                                player.closeInventory();
                                TestSend(player, "lct.error3");
                            }
                        } else if (name.equalsIgnoreCase(green + val("lct.gui.itemextract.name", false))) {
                            event.setCancelled(true);
                            cr = this.getLTable(inv.getItem(inv.getSize() - 17));
                            if (!cr.isRunning()) {
                                if (cr.getFuel() > 0) {
                                    a = this.canCraft1(inv);
                                    s = a.split(" ")[0];
                                    p = Integer.parseInt(a.split(" ")[1]);
                                    if (s.equalsIgnoreCase("success")) {
                                        if (cr.getStoredLuck() != 0) {
                                            cr.run();
                                        } else {
                                            player.closeInventory();
                                            TestSend(player, "lct.error5");
                                        }
                                    } else {
                                        player.closeInventory();
                                        if (p > -1) {
                                            cr.dropItem(p);
                                        }

                                        if (s.equalsIgnoreCase("null")) {
                                            TestSend(player, "lct.error1");
                                        } else if (s.equalsIgnoreCase("more_than_one")) {
                                            TestSend(player, "lct.error2");
                                        } else if (s.equalsIgnoreCase("invalid_lb")) {
                                            TestSend(player, "lct.error6");
                                        } else if (s.equalsIgnoreCase("other")) {
                                            TestSend(player, "lct.error7");
                                        }
                                    }
                                } else {
                                    player.closeInventory();
                                    TestSend(player, "lct.no_fuel");
                                }
                            } else {
                                player.closeInventory();
                                TestSend(player, "lct.running");
                            }
                        }
                    }

                    if (item.getType() == Material.EYE_OF_ENDER && name.equalsIgnoreCase(darkblue + val("lct.gui.itemother.name", false))) {
                        event.setCancelled(true);
                        player.closeInventory();
                        cr = this.getLTable(inv.getItem(inv.getSize() - 17));
                        com.mcgamer199.luckyblock.tellraw.RawText[] texts = new com.mcgamer199.luckyblock.tellraw.RawText[]{new com.mcgamer199.luckyblock.tellraw.RawText(red + "=== [" + bold + val("lct.data.settings", false) + red + "] ===\n"), new com.mcgamer199.luckyblock.tellraw.RawText(aqua + "========= " + darkpurple + bold + "►" + blue + bold + " " + val("lct.data.stop.name", false) + " " + darkpurple + bold + "◄" + aqua + " ========="), null, null};
                        texts[1].addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + val("lct.data.stop.lore", false)));
                        texts[1].addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.CLICK_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " lctstop " + cr.getId()));
                        texts[2] = new com.mcgamer199.luckyblock.tellraw.RawText("\n" + aqua + "========= " + darkpurple + bold + "►" + blue + bold + " " + val("lct.data.exluck.name", false) + " " + darkpurple + bold + "◄" + aqua + " =========");
                        texts[2].addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + val("lct.data.exluck.lore", false)));
                        texts[2].addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.CLICK_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " lctextra " + cr.getId()));
                        texts[3] = new com.mcgamer199.luckyblock.tellraw.RawText("\n" + red + "======================================");
                        com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo(player, texts);
                    }
                }
            }
        }

    }

    String canCraft(Inventory inv) {
        boolean found = false;

        for(int x = 0; x < inv.getSize(); ++x) {
            if (x < 45 && x % 9 > 3) {
                ItemStack item = inv.getItem(x);
                if (item != null) {
                    if (CraftLB.LuckItem.getFromItem(item) == null && !LBType.isLB(item)) {
                        return "INVALID_ITEMS";
                    }

                    if (LBType.isLB(item) && isRandom(item)) {
                        return "INVALID_ITEMS";
                    }

                    found = true;
                }
            }
        }

        if (!found) {
            return "NO_ITEM_FOUND";
        } else {
            return "SUCCESS";
        }
    }

    String canCraft1(Inventory inv) {
        String found = "NULL -1";

        for(int x = 0; x < inv.getSize(); ++x) {
            if (x < 26 && x % 9 < 3 && inv.getItem(x) != null) {
                ItemStack item = inv.getItem(x);
                if (!LBType.isLB(item)) {
                    return "INVALID_LB " + x;
                }

                if (isRandom(item)) {
                    return "OTHER " + x;
                }

                if (item.getAmount() != 1) {
                    return "MORE_THAN_ONE " + x;
                }

                found = "SUCCESS " + x;
            }
        }

        return found;
    }

    public static boolean isRandom(ItemStack item) {
        return ItemReflection.getBoolean(item, "hasRandomLuck");
    }

    public static int getTotalLuck(Inventory inv) {
        int total = 0;

        for(int x = 0; x < inv.getSize(); ++x) {
            if (x < 45 && x % 9 > 3 && inv.getItem(x) != null) {
                ItemStack item = inv.getItem(x);
                if (CraftLB.LuckItem.getFromItem(item) != null && !LBType.isLB(item)) {
                    CraftLB.LuckItem i = CraftLB.LuckItem.getFromItem(item);
                    int luck = i.luck;
                    if (i == CraftLB.LuckItem.POTION || i == CraftLB.LuckItem.SPLASH_POTION || i == CraftLB.LuckItem.LINGERING_POTION) {
                        PotionMeta potion = (PotionMeta)item.getItemMeta();
                        luck += getPotionLuck(potion);
                    }

                    if (i == CraftLB.LuckItem.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta m = (EnchantmentStorageMeta)item.getItemMeta();
                        luck += getBookLuck(m);
                    }

                    luck *= item.getAmount();
                    total += luck;
                }

                if (LBType.isLB(item)) {
                    int luck = getLuck(item);
                    luck *= item.getAmount();
                    total += luck;
                }
            }
        }

        if (total > 9999) {
            total = 9999;
        }

        if (total < -9999) {
            total = -9999;
        }

        return total;
    }

    public static int getLuck(ItemStack item) {
        return LBType.isLB(item) ? LBType.getLuck(item) : 0;
    }

    LuckyCraftingTable getLTable(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(yellow + val("lct.display_name", false)) && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0) != null) {
            String s = (String)item.getItemMeta().getLore().get(0);
            if (stringToBlock(s) != null) {
                return LuckyCraftingTable.getByBlock(stringToBlock(s));
            }
        }

        return null;
    }

    void insert(Inventory inv) {
        LuckyCraftingTable c = this.getLTable(inv.getItem(inv.getSize() - 17));
        int total = getTotalLuck(inv);
        int n = c.getStoredLuck() + total;
        c.setStoredLuck(n, true);
        removeItems(inv);
        c.save(true);
        EntityFloatingText e = new EntityFloatingText();
        e.mode = 1;
        String s = null;
        if (total > 0) {
            s = ChatColor.GREEN + "+" + total;
        } else {
            s = "" + ChatColor.RED + total;
        }

        e.text = s;
        e.spawn(c.getBlock().getLocation().add(0.5D, 1.0D, 0.5D));
        Iterator var8 = inv.getViewers().iterator();

        while(var8.hasNext()) {
            HumanEntity h = (HumanEntity)var8.next();
            if (h instanceof Player) {
                Player p = (Player)h;
                p.playSound(p.getLocation(), getSound("lct_insert"), 0.4F, 2.0F);
                c.open(p);
            }
        }

    }

    public static void removeItems(Inventory inv) {
        int x;
        ItemStack item;
        for(x = 0; x < inv.getSize(); ++x) {
            if (x < 45 && x % 9 > 3 && inv.getItem(x) != null && CraftLB.LuckItem.isValid(inv.getItem(x))) {
                item = inv.getItem(x);
                if (!LBType.isLB(item)) {
                    inv.removeItem(new ItemStack[]{inv.getItem(x)});
                }
            }
        }

        for(x = 0; x < inv.getSize(); ++x) {
            if (x < 45 && x % 9 > 3 && inv.getItem(x) != null && LBType.isLB(inv.getItem(x))) {
                item = inv.getItem(x);
                LBType.changeLuck(LBType.fromItem(item), item, 0);
            }
        }

    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getTitle() != null && inv.getTitle().equalsIgnoreCase(red + val("lct.display_name", false)) && inv.getItem(inv.getSize() - 17) != null && this.getLTable(inv.getItem(inv.getSize() - 17)) != null) {
            LuckyCraftingTable cr = this.getLTable(inv.getItem(inv.getSize() - 17));
            cr.save(true);
        }

    }

    @EventHandler
    public void onCraftLuckyBlock(CraftItemEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && LBType.isLB(event.getCurrentItem())) {
            LBType lbtype = LBType.fromItem(event.getCurrentItem());
            HumanEntity h = event.getWhoClicked();
            if (lbtype.getPermission("Crafting") != null && !h.hasPermission(lbtype.getPermission("Crafting"))) {
                send_no(h, "event.craftlb.error.permission");
                event.setCancelled(true);
            }

            LBCraftEvent e = new LBCraftEvent(event.getWhoClicked(), event.getCurrentItem(), lbtype);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true
    )
    private void CraftLBEvent(LBCraftEvent event) {
        LBType type = event.getLBType();
        Player player = (Player)event.getHuman();
        if (LuckyBlock.isDebugEnabled()) {
            Debug("Lucky block crafted", new DebugData[]{new DebugData("LBType", type.getId() + ", " + ChatColor.stripColor(type.getName())), new DebugData("Item crafted", event.getItem().getType().name()), new DebugData("Crafted By", player.getName()), new DebugData("Luck", String.valueOf(LBType.getLuck(event.getItem())))});
        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand() != null) {
            ItemStack item = event.getItemInHand();
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                Block block = event.getBlock();
                Player player = event.getPlayer();
                int y;
                int z;
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Lucky Crafting Table")) {
                    if (player.hasPermission("lb.placelct")) {
                        for(int x = -1; x < 2; ++x) {
                            for(y = 0; y < 2; ++y) {
                                for(z = -1; z < 2; ++z) {
                                    if ((x != 0 || z != 0 || y != 0) && block.getLocation().add((double)x, (double)y, (double)z).getBlock().getType() != Material.AIR) {
                                        event.setCancelled(true);
                                        TestSend(player, "lct.place.no_place");
                                        return;
                                    }
                                }
                            }
                        }

                        LuckyCraftingTable lc = new LuckyCraftingTable(block, player.getName(), true);
                        lc.save(true);
                        TestSend(player, "lct.place.success");
                    } else {
                        event.setCancelled(true);
                        TestSend(player, "lct.place.no_permission");
                    }
                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Lucky Crafting Table " + ChatColor.GOLD + "[Data]")) {
                    if (player.hasPermission("lb.placelct")) {
                        if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            String p = event.getPlayer().getName();
                            y = ItemReflection.getInt(item, "lct_level");
                            z = ItemReflection.getInt(item, "lct_extra");
                            int fuel = ItemReflection.getInt(item, "lct_fuel");
                            int stored = ItemReflection.getInt(item, "lct_stored");
                            String s = ItemReflection.getString(item, "lct_player");
                            if (s != null) {
                                p = s;
                            }

                            LuckyCraftingTable lc = new LuckyCraftingTable(block, p, true);
                            lc.setLevel((byte)y);
                            lc.setExtraLuck(z);
                            lc.setFuel(fuel);
                            lc.setStoredLuck(stored, false);
                            lc.save(true);
                            TestSend(player, "lct.place.success");
                        } else {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                        TestSend(player, "lct.place.no_permission");
                    }
                }
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true
    )
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (LuckyCraftingTable.getByBlock(block) != null) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            if (!player.hasPermission("lb.breaklct")) {
                TestSend(player, "lct.break.no_permission");
                return;
            }

            block.setType(Material.AIR);
            LuckyCraftingTable cr = LuckyCraftingTable.getByBlock(block);
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                ItemStack item = ItemMaker.createItem(Material.NOTE_BLOCK, 1, 0, ChatColor.YELLOW + "Lucky Crafting Table " + ChatColor.GOLD + "[Data]", Arrays.asList("", ChatColor.GRAY + "Contains stored data"));
                ItemNBT nbt = new ItemNBT(item);
                nbt.set("lct_level", nbt.getNewNBTTagInt(cr.getLevel()));
                nbt.set("lct_extra", nbt.getNewNBTTagInt(cr.getExtraLuck()));
                nbt.set("lct_fuel", nbt.getNewNBTTagInt(cr.getFuel()));
                nbt.set("lct_player", nbt.getNewNBTTagString(cr.getPlayer()));
                nbt.set("lct_stored", nbt.getNewNBTTagInt(cr.getStoredLuck()));
                item = nbt.getItem();
                block.getWorld().dropItem(block.getLocation(), item);
            }

            Item i;
            int x;
            ItemStack item;
            for(x = 0; x < cr.i().getSize(); ++x) {
                if (x < 26 && x % 9 < 3 && cr.i().getItem(x) != null) {
                    item = cr.i().getItem(x);
                    i = cr.getBlock().getWorld().dropItem(cr.getBlock().getLocation().add(0.5D, 1.0D, 0.5D), item);
                    i.setPickupDelay(25);
                }
            }

            for(x = 0; x < cr.i().getSize(); ++x) {
                if (x < 45 && x % 9 > 3 && cr.i().getItem(x) != null) {
                    item = cr.i().getItem(x);
                    i = cr.getBlock().getWorld().dropItem(cr.getBlock().getLocation().add(0.5D, 1.0D, 0.5D), item);
                    i.setPickupDelay(25);
                }
            }

            cr.remove();
            TestSend(player, "lct.break.success");
        }

    }

    @EventHandler
    public void upgradeLCT(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR && event.getAction() == Action.RIGHT_CLICK_BLOCK && LuckyCraftingTable.getByBlock(event.getClickedBlock()) != null) {
            LuckyCraftingTable table = LuckyCraftingTable.getByBlock(event.getClickedBlock());
            if (event.getItem() != null && event.getItem().getType() == Material.EMERALD_BLOCK) {
                Player player = event.getPlayer();
                if (event.getItem().getAmount() > 7 && this.isMainHand(event) && player.isSneaking()) {
                    if (player.hasPermission("lb.upgradelct")) {
                        if (table.getLevel() < 10) {
                            event.setCancelled(true);
                            table.setLevel((byte)(table.getLevel() + 1));
                            table.save(true);
                            table.refresh();
                            playFixedSound(table.getBlock().getLocation(), getSound("lct_upgrade"), 1.0F, 0.0F, 7);
                            EntityFloatingText e = new EntityFloatingText();
                            e.mode = 1;
                            e.text = val("lct.level_up");
                            e.spawn(table.getBlock().getLocation().add(0.5D, 1.0D, 0.5D));
                            TestSend(player, "lct.upgrade.success");
                            if (event.getItem().getAmount() > 8) {
                                event.getItem().setAmount(event.getItem().getAmount() - 8);
                            } else {
                                player.getInventory().removeItem(new ItemStack[]{event.getItem()});
                            }
                        } else {
                            TestSend(player, "lct.upgrade.max_level");
                        }
                    } else {
                        TestSend(player, "lct.upgrade.no_permission");
                    }
                }
            }
        }

    }

    @EventHandler
    public void chargeLCT(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR && LuckyCraftingTable.getByBlock(event.getClickedBlock()) != null) {
            LuckyCraftingTable table = LuckyCraftingTable.getByBlock(event.getClickedBlock());
            if (event.getItem() != null) {
                Player player = event.getPlayer();
                if (player.isSneaking() && this.isMainHand(event)) {
                    ItemStack item = event.getItem();
                    CraftLB.FuelItem i = CraftLB.FuelItem.getByItem(item);
                    if (i != null) {
                        int value = i.value;
                        if (player.hasPermission("lb.chargelct")) {
                            table.setFuel(table.getFuel() + value);
                            if (event.getItem().getAmount() > 1) {
                                event.getItem().setAmount(event.getItem().getAmount() - 1);
                            } else {
                                player.getInventory().removeItem(new ItemStack[]{event.getItem()});
                            }
                        } else {
                            TestSend(player, "lct.charge.no_permission");
                        }
                    }
                }
            }
        }

    }

    private static int getPotionLuck(PotionMeta potion) {
        int luck = 0;
        byte plus;
        if (potion.getBasePotionData() != null) {
            PotionData data = potion.getBasePotionData();
            PotionType type = data.getType();
            plus = 0;
            int multiply = 1;
            if (data.isUpgraded()) {
                multiply = 2;
            }

            if (type == PotionType.FIRE_RESISTANCE) {
                plus = 12;
            } else if (type == PotionType.INSTANT_DAMAGE) {
                plus = -16;
            } else if (type == PotionType.INSTANT_HEAL) {
                plus = 16;
            } else if (type == PotionType.INVISIBILITY) {
                plus = 12;
            } else if (type == PotionType.JUMP) {
                plus = 8;
            } else if (type == PotionType.POISON) {
                plus = -12;
            } else if (type == PotionType.REGEN) {
                plus = 12;
            } else if (type == PotionType.SLOWNESS) {
                plus = -8;
            } else if (type == PotionType.SPEED) {
                plus = 8;
            } else if (type == PotionType.STRENGTH) {
                plus = 12;
            } else if (type == PotionType.WATER_BREATHING) {
                plus = 4;
            } else if (type == PotionType.WEAKNESS) {
                plus = -8;
            } else if (type.name().equalsIgnoreCase("luck")) {
                plus = 12;
            }

            int p = plus * multiply;
            luck += p;
        }

        int plusv20;
        if (potion.hasCustomEffects()) {
            for(Iterator var8 = potion.getCustomEffects().iterator(); var8.hasNext(); luck += plusv20) {
                PotionEffect effect = (PotionEffect)var8.next();
                plusv20 = 0;
                PotionEffectType type = effect.getType();
                String name = type.getName();
                if (name.equalsIgnoreCase("absorption")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("blindness")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("confusion")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("damage_resistance")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("fast_digging")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("fire_resistance")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("harm")) {
                    plusv20 = -2;
                } else if (name.equalsIgnoreCase("heal")) {
                    plusv20 = 2;
                } else if (name.equalsIgnoreCase("health_boost")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("hunger")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("increase_damage")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("invisibility")) {
                    plusv20 = 2;
                } else if (name.equalsIgnoreCase("jump")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("poison")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("regeneration")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("saturation")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("slow")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("slow_digging")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("speed")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("water_breathing")) {
                    plusv20 = 1;
                } else if (name.equalsIgnoreCase("weakness")) {
                    plusv20 = -1;
                } else if (name.equalsIgnoreCase("wither")) {
                    plusv20 = -2;
                }

                plusv20 = plusv20 * (effect.getAmplifier() + 1) * (effect.getDuration() / 100);
            }
        }

        return luck;
    }

    private static int getBookLuck(EnchantmentStorageMeta e) {
        int luck = 0;
        Enchantment c;
        if (e.hasStoredEnchants()) {
            for(Iterator var3 = e.getStoredEnchants().keySet().iterator(); var3.hasNext(); luck += 3 * e.getStoredEnchantLevel(c)) {
                c = (Enchantment)var3.next();
            }
        }

        return luck;
    }

    private static void TestSend(Player player, String path) {
        if (LuckyBlock.action_bar_messages()) {
            TitleSender.send_1(player, path);
        } else {
            send_no(player, path);
        }

    }

    public static class DataTag {
        private Object[] values;
        private CraftLB.DataTagType type;

        public DataTag(CraftLB.DataTagType type, Object[] values) {
            this.type = type;
            this.values = values;
        }

        public Object[] getValues() {
            return this.values;
        }

        public CraftLB.DataTagType getType() {
            return this.type;
        }
    }

    public static enum DataTagType {
        DISPLAY_NAME;

        private DataTagType() {
        }
    }

    public static enum FuelItem {
        COAL(15),
        COAL_BLOCK(130),
        LAVA_BUCKET(175),
        OBSIDIAN(350),
        BLAZE_ROD(75),
        BONE(5),
        BONE_BLOCK(40);

        private int value;

        private FuelItem(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static CraftLB.FuelItem getByItem(ItemStack item) {
            CraftLB.FuelItem[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                CraftLB.FuelItem i = var4[var2];
                if (i.name().equalsIgnoreCase(item.getType().name())) {
                    return i;
                }
            }

            return null;
        }
    }

    public static enum LuckItem {
        DIAMOND(Material.DIAMOND, 10),
        EMERALD(Material.EMERALD, 11),
        GOLD_INGOT(Material.GOLD_INGOT, 9),
        GOLD_NUGGET(Material.GOLD_NUGGET, 1),
        IRON_INGOT(Material.IRON_INGOT, 5),
        QUARTZ(Material.QUARTZ, 3),
        GOLDEN_APPLE(Material.GOLDEN_APPLE, 30, 0, new CraftLB.DataTag[0]),
        ENCHANTED_GOLDEN_APPLE(Material.GOLDEN_APPLE, 100, 1, new CraftLB.DataTag[0]),
        NETHER_STAR(Material.NETHER_STAR, 100),
        IRON_HORSE_ARMOR(Material.IRON_BARDING, 6),
        GOLD_HORSE_ARMOR(Material.GOLD_BARDING, 10),
        DIAMOND_HORSE_ARMOR(Material.DIAMOND_BARDING, 11),
        SPIDER_EYE(Material.SPIDER_EYE, -4),
        RAW_CHICKEN(Material.RAW_CHICKEN, -1),
        ROTTEN_FLESH(Material.ROTTEN_FLESH, -2),
        POISONOUS_POTATO(Material.POISONOUS_POTATO, -3),
        FERMENTED_SPIDER_EYE(Material.FERMENTED_SPIDER_EYE, -3),
        FISH(Material.RAW_FISH, -5, 3, new CraftLB.DataTag[0]),
        NETHER_BRICK(Material.NETHER_BRICK_ITEM, 3),
        BONE(Material.BONE, -2),
        XP_BOTTLE(Material.EXP_BOTTLE, 6),
        GOLDEN_CARROT(Material.GOLDEN_CARROT, 7),
        LAVA_BUCKET(Material.LAVA_BUCKET, -10),
        LAPIS_LAZULI(Material.INK_SACK, 5, 4, new CraftLB.DataTag[0]),
        ENDER_PEARL(Material.ENDER_PEARL, 6),
        EYE_OF_ENDER(Material.EYE_OF_ENDER, 10),
        ENCHANTED_BOOK(Material.ENCHANTED_BOOK, 4),
        IRON_HELMET(Material.IRON_HELMET, 20, 0, new CraftLB.DataTag[0]),
        IRON_CHESTPLATE(Material.IRON_CHESTPLATE, 30, 0, new CraftLB.DataTag[0]),
        IRON_LEGGINGS(Material.IRON_LEGGINGS, 25, 0, new CraftLB.DataTag[0]),
        IRON_BOOTS(Material.IRON_BOOTS, 15, 0, new CraftLB.DataTag[0]),
        GOLD_HELMET(Material.GOLD_HELMET, 25, 0, new CraftLB.DataTag[0]),
        GOLD_CHESTPLATE(Material.GOLD_CHESTPLATE, 35, 0, new CraftLB.DataTag[0]),
        GOLD_LEGGINGS(Material.GOLD_LEGGINGS, 30, 0, new CraftLB.DataTag[0]),
        GOLD_BOOTS(Material.GOLD_BOOTS, 20, 0, new CraftLB.DataTag[0]),
        DIAMOND_HELMET(Material.DIAMOND_HELMET, 30, 0, new CraftLB.DataTag[0]),
        DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, 40, 0, new CraftLB.DataTag[0]),
        DIAMOND_LEGGINGS(Material.DIAMOND_LEGGINGS, 35, 0, new CraftLB.DataTag[0]),
        DIAMOND_BOOTS(Material.DIAMOND_BOOTS, 25, 0, new CraftLB.DataTag[0]),
        REDSTONE(Material.REDSTONE, 2),
        POTION(Material.POTION, 0),
        SPLASH_POTION(Material.getMaterial("SPLASH_POTION"), 0),
        LINGERING_POTION(Material.getMaterial("LINGERING_POTION"), 0),
        CAKE(Material.CAKE, 4),
        PUMPKIN_PIE(Material.PUMPKIN_PIE, 3, 0, new CraftLB.DataTag[0]),
        ENCHANTMENT_TABLE(Material.ENCHANTMENT_TABLE, 15),
        ENDER_CHEST(Material.ENDER_CHEST, 15),
        NETHERRACK(Material.NETHERRACK, -5),
        SKELETON_SKULL(Material.SKULL_ITEM, -7, 0, new CraftLB.DataTag[0]),
        WITHER_SKELETON_SKULL(Material.SKULL_ITEM, -15, 1, new CraftLB.DataTag[0]),
        ZOMBIE_SKULL(Material.SKULL_ITEM, -7, 2, new CraftLB.DataTag[0]),
        CREEPER_SKULL(Material.SKULL_ITEM, -10, 4, new CraftLB.DataTag[0]),
        SPELL_OF_FORTUNE(Material.GHAST_TEAR, 1000, 0, new CraftLB.DataTag[]{new CraftLB.DataTag(CraftLB.DataTagType.DISPLAY_NAME, new String[]{"" + ChatColor.GRAY + ChatColor.BOLD + "Spell of fortune"})}),
        LB_BOSS_HEAD(Material.SKULL_ITEM, 2500, 3, new CraftLB.DataTag[]{new CraftLB.DataTag(CraftLB.DataTagType.DISPLAY_NAME, new String[]{ChatColor.GOLD + "Trophy: " + ChatColor.GREEN + "LBBoss"})}),
        BEACON(Material.BEACON, 65),
        CACTUS(Material.CACTUS, -5),
        DEAD_BUSH(Material.DEAD_BUSH, -3),
        SPONGE(Material.SPONGE, -2),
        SUGAR(Material.SUGAR, 1),
        DIRT(Material.DIRT, -3),
        OBSIDIAN(Material.OBSIDIAN, 15),
        FULL_ANVIL(Material.ANVIL, 45, 0, new CraftLB.DataTag[0]),
        SEMI_BROKEN_ANVIL(Material.ANVIL, 30, 1, new CraftLB.DataTag[0]),
        BROKEN_ANVIL(Material.ANVIL, 15, 2, new CraftLB.DataTag[0]),
        PRISMARINE(Material.PRISMARINE, 7),
        MAGMA_BLOCK(Material.MAGMA, -13),
        GUN_POWDER(Material.SULPHUR, -3),
        DRAGON_HEAD(Material.SKULL_ITEM, 250, 5, new CraftLB.DataTag[0]);

        private int luck;
        private short data;
        private Material material;
        private CraftLB.DataTag[] tags;

        public static CraftLB.LuckItem getFromItem(ItemStack item) {
            CraftLB.LuckItem[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                CraftLB.LuckItem i = var4[var2];
                if (item.getType() == i.material && item.getDurability() == i.data) {
                    if (!i.hasTags()) {
                        return i;
                    }

                    boolean success = true;

                    for(int x = 0; x < i.tags.length; ++x) {
                        CraftLB.DataTag t = i.tags[x];
                        if (t.type == CraftLB.DataTagType.DISPLAY_NAME && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().equalsIgnoreCase(t.values[0].toString()))) {
                            success = false;
                            x = i.tags.length;
                        }
                    }

                    if (success) {
                        return i;
                    }
                }
            }

            return null;
        }

        public static boolean isValid(Material mat) {
            CraftLB.LuckItem[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                CraftLB.LuckItem i = var4[var2];
                if (mat == i.material) {
                    return true;
                }
            }

            return false;
        }

        public static boolean isValid(ItemStack item) {
            CraftLB.LuckItem[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                CraftLB.LuckItem i = var4[var2];
                if (item.getType() == i.material && item.getDurability() == i.data) {
                    return true;
                }
            }

            return false;
        }

        private LuckItem(Material material, int luck) {
            this.material = material;
            this.luck = luck;
            this.data = 0;
        }

        private LuckItem(Material material, int luck, int data, CraftLB.DataTag... tags) {
            this.material = material;
            this.luck = luck;
            this.data = (short)data;
            this.tags = tags;
        }

        public short getData() {
            return this.data;
        }

        public int getLuck() {
            return this.luck;
        }

        public Material getMaterial() {
            return this.material;
        }

        public CraftLB.DataTag[] getTags() {
            return this.tags;
        }

        public boolean hasTags() {
            if (this.tags != null) {
                for(int x = 0; x < this.tags.length; ++x) {
                    if (this.tags[x] != null) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
