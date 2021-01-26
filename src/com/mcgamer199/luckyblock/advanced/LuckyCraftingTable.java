package com.mcgamer199.luckyblock.advanced;

import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.event.lb.CraftLB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.customentity.lct.EntityLCTItem;
import com.mcgamer199.luckyblock.customentity.lct.EntityLCTNameTag;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.mcgamer199.luckyblock.inventory.event.ItemMaker;
import com.mcgamer199.luckyblock.logic.ITask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LuckyCraftingTable extends ColorsClass {
    static File fileF;
    static FileConfiguration file;
    public static List<LuckyCraftingTable> tables;
    private Block block;
    private int storedLuck;
    Inventory inv;
    private int fuel = 360;
    private int id;
    private int maxLuck = 5000;
    private boolean running;
    private byte level = 1;
    private String player;
    public int slot = -1;
    private int extraLuck;
    private int[] u_luck = new int[]{4, 1};
    private int[] u_fuel = new int[]{7, 2};

    static {
        fileF = new File(LuckyBlock.instance.getDataFolder() + File.separator + "data/LuckyTables.yml");
        file = YamlConfiguration.loadConfiguration(fileF);
        tables = new ArrayList();
    }

    public static LuckyCraftingTable getByBlock(Block block) {
        for(int x = 0; x < tables.size(); ++x) {
            LuckyCraftingTable c = (LuckyCraftingTable)tables.get(x);
            Block b = c.block;
            String s = blockToString(b);
            if (s.equalsIgnoreCase(blockToString(block))) {
                return c;
            }
        }

        return null;
    }

    public boolean isValid() {
        return getByBlock(this.block) != null;
    }

    public void stop() {
        if (this.running) {
            this.running = false;
            playFixedSound(this.block.getLocation(), getSound("lct_stop"), 1.0F, 0.0F, 8);
        }

    }

    public static LuckyCraftingTable getById(int id) {
        for(int x = 0; x < tables.size(); ++x) {
            LuckyCraftingTable c = (LuckyCraftingTable)tables.get(x);
            if (c.id == id) {
                return c;
            }
        }

        return null;
    }

    public int getMaxLuck() {
        return this.maxLuck;
    }

    public Inventory i() {
        return this.inv;
    }

    public void refresh() {
        Iterator var2 = this.inv.getViewers().iterator();

        while(var2.hasNext()) {
            HumanEntity h = (HumanEntity)var2.next();
            if (h instanceof Player) {
                this.open((Player)h);
            } else {
                h.closeInventory();
            }
        }

    }

    public LuckyCraftingTable(Block block, String player, boolean first) {
        this.id = random.nextInt(100000) + 1;
        this.block = block;
        this.player = player;
        if (this.inv == null) {
            this.inv = Bukkit.createInventory((InventoryHolder)null, 54, red + val("lct.display_name", false));
        }

        ItemStack glass = ItemMaker.createItem(Material.STAINED_GLASS_PANE, 1, 15, red + val("lct.gui.itemlocked.name", false));
        int s = this.inv.getSize();
        int[] glasses = new int[]{1, 2, 3, 4, 5, 6, 15, 24, 25, 26, 27, 33, 42, 51};
        int[] var10 = glasses;
        int var9 = glasses.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            int i = var10[var8];
            this.inv.setItem(s - i, glass);
        }

        this.inv.setItem(s - 9, ItemMaker.createItem(Material.COMPASS, 1, 0, red + val("lct.gui.itemclose.name", false), Arrays.asList("", gray + val("lct.gui.itemclose.lore", false))));
        this.inv.setItem(this.inv.getSize() - 8, ItemMaker.createItem(Material.EMERALD, 1, 0, yellow + val("lct.gui.itemresult.name", false), Arrays.asList("", gray + val("lct.gui.itemresult.lore", false))));
        this.inv.setItem(this.inv.getSize() - 7, ItemMaker.createItem(Material.EYE_OF_ENDER, 1, 0, darkblue + val("lct.gui.itemother.name", false), Arrays.asList("", gray + val("lct.gui.itemother.lore", false))));
        this.inv.setItem(this.inv.getSize() - 17, ItemMaker.createItem(Material.NETHER_STAR, 1, 0, yellow + val("lct.display_name", false), Arrays.asList(gray + blockToString(block))));
        this.inv.setItem(this.inv.getSize() - 16, ItemMaker.createItem(Material.REDSTONE, 1, 0, green + val("lct.gui.iteminsert.name", false), Arrays.asList("", gray + val("lct.gui.iteminsert.lore", false))));
        this.inv.setItem(this.inv.getSize() - 18, ItemMaker.createItem(Material.REDSTONE, 1, 0, green + val("lct.gui.itemextract.name", false), Arrays.asList("", gray + val("lct.gui.itemextract.lore"))));
        if (first && IObjects.getValue("lct_nameVisible").toString().equalsIgnoreCase("true")) {
            EntityLCTNameTag e = new EntityLCTNameTag();
            e.spawn(this);
            this.func_loop();
            this.spawn_items();
        }

    }

    private void spawn_items() {
        EntityLCTItem e1 = new EntityLCTItem(this);
        e1.spawn(this.block.getLocation().add(0.5D, -0.9D, 0.5D));
    }

    public void dropItem(int slot) {
        if (this.inv.getItem(slot) != null) {
            ItemStack item = this.inv.getItem(slot);
            Item i = this.block.getWorld().dropItem(this.block.getLocation().add(0.5D, 1.0D, 0.5D), item);
            i.setPickupDelay(25);
            this.inv.clear(slot);
            this.save(true);
        }

    }

    public Block getBlock() {
        return this.block;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
        if (fuel > 9999) {
            this.explode();
        } else {
            this.save(true);
            this.refresh();
        }

    }

    private void func_play_run() {
        Iterator var2 = this.inv.getViewers().iterator();

        while(var2.hasNext()) {
            HumanEntity h = (HumanEntity)var2.next();
            if (h instanceof Player) {
                Player player = (Player)h;
                player.playSound(player.getLocation(), getSound("lct_run"), 1.0F, 2.0F);
            }
        }

    }

    public void run() {
        if (!this.running) {
            this.running = true;
            CraftLB.removeItems(this.inv);
            this.func_play_run();
            this.slot = 0;
            this.run1();
            final String t = val("lct.data.main.stored_luck", false);
            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
                int place = 0;
                int working = 0;
                boolean changed = false;

                public void run() {
                    if (LuckyCraftingTable.this.running) {
                        if (!LuckyCraftingTable.this.isValid()) {
                            task.run();
                            return;
                        }

                        if (this.working == 0) {
                            if (this.place < 21) {
                                if (LuckyCraftingTable.this.inv.getItem(this.place) != null) {
                                    ItemStack item = LuckyCraftingTable.this.inv.getItem(this.place);
                                    if (LBType.isLB(item)) {
                                        LBType type = LBType.fromItem(item);
                                        int luck = LBType.getLuck(item);
                                        int added = LuckyCraftingTable.this.getLuckAdded(luck, type.getMaxLuck(), type.getMinLuck());
                                        if (added != 0) {
                                            luck += added;
                                            LuckyCraftingTable var10000 = LuckyCraftingTable.this;
                                            var10000.storedLuck = var10000.storedLuck + added * -1;
                                            this.changed = true;
                                        } else {
                                            int ix = LuckyCraftingTable.this.place_(this.place);
                                            if (ix != 0) {
                                                this.place = ix;
                                                LuckyCraftingTable.this.slot = ix;
                                            } else {
                                                this.working = 1;
                                            }
                                        }

                                        LBType.changeLuck(type, item, luck);
                                        ItemStack st = LuckyCraftingTable.this.inv.getItem(LuckyCraftingTable.this.inv.getSize() - 17);
                                        LuckyCraftingTable.this._itemS(st, t);
                                    } else {
                                        this.working = 2;
                                    }
                                } else {
                                    int i = LuckyCraftingTable.this.place_(this.place);
                                    if (i != 0) {
                                        this.place = i;
                                        LuckyCraftingTable.this.slot = i;
                                    } else {
                                        this.working = 2;
                                    }
                                }
                            }
                        } else if (this.working == 1) {
                            LuckyCraftingTable.playFixedSound(LuckyCraftingTable.this.block.getLocation(), LuckyCraftingTable.getSound("lct_finish"), 1.0F, 2.0F, 10);
                            task.run();
                            LuckyCraftingTable.this.running = false;
                            LuckyCraftingTable.this.save(true);
                        } else {
                            if (this.changed) {
                                LuckyCraftingTable.playFixedSound(LuckyCraftingTable.this.block.getLocation(), LuckyCraftingTable.getSound("lct_finish"), 1.0F, 2.0F, 10);
                            }

                            task.run();
                            LuckyCraftingTable.this.running = false;
                        }
                    }

                }
            }, (long)this.u_luck[0], (long)this.u_luck[0]));
        }

    }

    private ItemStack _itemS(ItemStack item, String t) {
        ItemMeta stM = item.getItemMeta();
        List<String> lest = stM.getLore();
        String l = "" + ChatColor.GOLD + this.storedLuck;
        if (this.storedLuck > 0) {
            l = "" + ChatColor.GREEN + this.storedLuck;
        } else if (this.storedLuck < 0) {
            l = "" + ChatColor.RED + this.storedLuck;
        }

        lest.set(1, blue + t + ": " + l);
        stM.setLore(lest);
        item.setItemMeta(stM);
        return item;
    }

    private int getLuckAdded(int luck, int maxLuck, int minLuck) {
        int a = 0;
        if (this.storedLuck > 0) {
            if (luck + this.u_luck[1] - 1 < maxLuck) {
                if (this.storedLuck >= this.u_luck[1]) {
                    a = this.u_luck[1];
                } else {
                    a = this.storedLuck;
                }
            } else if (luck < maxLuck) {
                a = maxLuck - luck;
            }
        } else if (this.storedLuck < 0) {
            if (luck - this.u_luck[1] + 1 > minLuck) {
                if (this.storedLuck <= this.u_luck[1]) {
                    a = this.u_luck[1] * -1;
                } else {
                    a = this.storedLuck;
                }
            } else if (luck > minLuck) {
                a = minLuck - luck;
            }
        }

        return a;
    }

    private int place_(int place) {
        if (place != 0 && place != 1 && place != 9 && place != 10 && place != 18 && place != 19) {
            if (place == 2) {
                return 9;
            } else {
                return place == 11 ? 18 : 0;
            }
        } else {
            return place + 1;
        }
    }

    private void run1() {
        final String s = val("lct.data.main.fuel", false);
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (LuckyCraftingTable.this.running) {
                    ItemStack st;
                    ItemMeta stM;
                    List lest;
                    if (LuckyCraftingTable.this.fuel > LuckyCraftingTable.this.u_fuel[1] - 1) {
                        LuckyCraftingTable var10000 = LuckyCraftingTable.this;
                        var10000.fuel = var10000.fuel - LuckyCraftingTable.this.u_fuel[1];
                        st = LuckyCraftingTable.this.inv.getItem(LuckyCraftingTable.this.inv.getSize() - 17);
                        stM = st.getItemMeta();
                        lest = stM.getLore();
                        lest.set(5, LuckyCraftingTable.blue + s + ": " + LuckyCraftingTable.white + LuckyCraftingTable.this.fuel);
                        stM.setLore(lest);
                        st.setItemMeta(stM);
                    } else if (LuckyCraftingTable.this.fuel > 0 && LuckyCraftingTable.this.fuel < LuckyCraftingTable.this.u_fuel[1]) {
                        LuckyCraftingTable.this.fuel = 0;
                        st = LuckyCraftingTable.this.inv.getItem(LuckyCraftingTable.this.inv.getSize() - 17);
                        stM = st.getItemMeta();
                        lest = stM.getLore();
                        lest.set(5, LuckyCraftingTable.blue + s + LuckyCraftingTable.white + LuckyCraftingTable.this.fuel);
                        stM.setLore(lest);
                        st.setItemMeta(stM);
                    } else {
                        LuckyCraftingTable.this.running = false;
                        LuckyCraftingTable.playFixedSound(LuckyCraftingTable.this.block.getLocation(), LuckyCraftingTable.getSound("lct_finish"), 1.0F, 2.0F, 10);
                    }
                } else {
                    task.run();
                }

            }
        }, (long)this.u_fuel[0], (long)this.u_fuel[0]));
    }

    public int getId() {
        return this.id;
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setLevel(byte level) {
        if (level > 0) {
            this.level = level;
            this.maxLuck = 5000;
            this.u_luck[0] = 3;
            this.u_fuel[0] = 10;
            if (level > 1) {
                this.maxLuck = 6000;
                this.u_luck[0] = 2;
                this.u_fuel[0] = 15;
            }

            if (level > 2) {
                this.maxLuck = 7000;
                this.u_fuel[0] = 20;
            }

            if (level > 3) {
                this.maxLuck = 9000;
                this.u_fuel[0] = 30;
                this.u_fuel[1] = 1;
            }

            if (level > 4) {
                this.maxLuck = 11000;
                this.u_luck[0] = 1;
            }

            if (level > 5) {
                this.maxLuck = 14000;
                this.u_fuel[0] = 60;
            }

            if (level > 6) {
                this.maxLuck = 18000;
                this.u_luck[1] = 2;
            }

            if (level > 7) {
                this.maxLuck = 23000;
                this.u_fuel[0] = 100;
                this.u_luck[1] = 3;
            }

            if (level > 8) {
                this.maxLuck = 25000;
                this.u_luck[1] = 4;
            }

            if (level > 9) {
                this.maxLuck = 30000;
                this.u_luck[1] = 5;
            }

            if (level > 10) {
                this.level = 10;
            }
        }

    }

    public int getStoredLuck() {
        return this.storedLuck;
    }

    public byte getLevel() {
        return this.level;
    }

    public String getPlayer() {
        return this.player;
    }

    public void explode() {
        this.remove();
        this.block.setType(Material.AIR);
        this.block.getWorld().createExplosion(this.block.getLocation(), 5.0F);
    }

    public void setStoredLuck(int storedLuck, boolean e) {
        int extra;
        if (storedLuck > 0) {
            if (storedLuck <= this.maxLuck) {
                this.storedLuck = storedLuck;
            } else {
                extra = storedLuck - this.maxLuck;
                this.storedLuck = this.maxLuck;
                if (e) {
                    this.setExtraLuck(this.extraLuck + extra);
                }
            }
        } else if (storedLuck < 0) {
            if (storedLuck >= this.maxLuck * -1) {
                this.storedLuck = storedLuck;
            } else {
                extra = storedLuck + this.maxLuck;
                this.storedLuck = this.maxLuck * -1;
                if (e) {
                    this.setExtraLuck(this.extraLuck + extra);
                }
            }
        } else {
            this.storedLuck = 0;
        }

    }

    public int getExtraLuck() {
        return this.extraLuck;
    }

    public void setExtraLuck(int extraLuck) {
        if (extraLuck > 1000000) {
            extraLuck = 1000000;
        }

        if (extraLuck < -1000000) {
            extraLuck = -1000000;
        }

        this.extraLuck = extraLuck;
    }

    public void open(Player player) {
        String l = "" + ChatColor.GOLD + this.storedLuck;
        if (this.storedLuck > 0) {
            l = "" + ChatColor.GREEN + this.storedLuck;
        } else if (this.storedLuck < 0) {
            l = "" + ChatColor.RED + this.storedLuck;
        }

        this.inv.setItem(this.inv.getSize() - 17, ItemMaker.createItem(Material.NETHER_STAR, 1, 0, yellow + val("lct.display_name", false), Arrays.asList(gray + blockToString(this.block), blue + val("lct.data.main.stored_luck", false) + ": " + l, blue + val("lct.data.main.level", false) + ": " + white + this.level, blue + val("lct.data.main.max_luck", false) + ": " + white + this.maxLuck, blue + val("lct.data.main.player", false) + ": " + white + this.player, blue + val("lct.data.main.fuel", false) + ": " + white + this.fuel, blue + val("lct.data.main.extra_luck", false) + ": " + white + this.extraLuck)));
        this.inv.setItem(this.inv.getSize() - 8, ItemMaker.createItem(Material.EMERALD, 1, 0, yellow + val("lct.gui.itemtotal.name", false), Arrays.asList("", gray + val("lct.gui.itemtotal.lore", false))));
        player.openInventory(this.inv);
    }

    private void func_loop() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (LuckyCraftingTable.this.isValid()) {
                    if (LuckyCraftingTable.this.inv.getViewers().size() > 0) {
                        MyTasks.playEffects(Particle.ENCHANTMENT_TABLE, LuckyCraftingTable.this.block.getLocation().add(0.5D, 0.5D, 0.5D), 45, new double[]{0.3D, 0.3D, 0.3D}, 1.0F);
                    }
                } else {
                    task.run();
                }

            }
        }, 20L, 20L));
    }

    public void remove() {
        tables.remove(this);
        FileConfiguration file = YamlConfiguration.loadConfiguration(fileF);
        if (file.getConfigurationSection("Tables") != null) {
            Iterator var3 = file.getConfigurationSection("Tables").getKeys(false).iterator();

            while(var3.hasNext()) {
                String s = (String)var3.next();
                ConfigurationSection f = file.getConfigurationSection("Tables").getConfigurationSection(s);
                if (f != null) {
                    int id = f.getInt("ID");
                    if (this.id == id) {
                        file.set("Tables." + s, (Object)null);
                    }
                }
            }
        }

        try {
            file.save(fileF);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void save(boolean saveToFile) {
        for(int x = 0; x < tables.size(); ++x) {
            LuckyCraftingTable c = (LuckyCraftingTable)tables.get(x);
            if (c.id == this.id) {
                tables.remove(c);
            }
        }

        tables.add(this);
        if (saveToFile) {
            this.saveToFile();
        }

    }

    private void saveToFile() {
        String path = "Tables.Table" + this.id;
        file.set(path + ".ID", this.id);
        file.set(path + ".Block", blockToString(this.block));
        file.set(path + ".Fuel", this.fuel);
        file.set(path + ".StoredLuck", this.storedLuck);
        file.set(path + ".Level", this.level);
        file.set(path + ".Player", this.player);
        file.set(path + ".Extra", this.extraLuck);
        file.set(path + ".uLuck", this.u_luck[0] + "," + this.u_luck[1]);
        file.set(path + ".uFuel", this.u_fuel[0] + "," + this.u_fuel[1]);
        this.saveItems();
    }

    private void saveItems() {
        String path = "Tables.Table" + this.id;
        file.set(path + ".LBItem.1", this.inv.getItem(this.inv.getSize() - 54));
        file.set(path + ".LBItem.2", this.inv.getItem(this.inv.getSize() - 53));
        file.set(path + ".LBItem.3", this.inv.getItem(this.inv.getSize() - 52));
        file.set(path + ".LBItem.4", this.inv.getItem(this.inv.getSize() - 45));
        file.set(path + ".LBItem.5", this.inv.getItem(this.inv.getSize() - 44));
        file.set(path + ".LBItem.6", this.inv.getItem(this.inv.getSize() - 43));
        file.set(path + ".LBItem.7", this.inv.getItem(this.inv.getSize() - 36));
        file.set(path + ".LBItem.8", this.inv.getItem(this.inv.getSize() - 35));
        file.set(path + ".LBItem.9", this.inv.getItem(this.inv.getSize() - 34));
        saveFile();
    }

    private static void saveFile() {
        try {
            file.save(fileF);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void load() {
        ConfigurationSection c = file.getConfigurationSection("Tables");
        if (c != null) {
            Iterator var2 = c.getKeys(false).iterator();

            while(true) {
                while(true) {
                    String s;
                    ConfigurationSection f;
                    do {
                        if (!var2.hasNext()) {
                            return;
                        }

                        s = (String)var2.next();
                        f = c.getConfigurationSection(s);
                    } while(f == null);

                    int id = f.getInt("ID");
                    String player = f.getString("Player");
                    Block block = stringToBlock(f.getString("Block"));
                    if (block != null && block.getType() == Material.NOTE_BLOCK) {
                        LuckyCraftingTable cr = new LuckyCraftingTable(block, player, false);
                        cr.id = id;
                        cr.fuel = f.getInt("Fuel");
                        cr.extraLuck = f.getInt("Extra");
                        if (f.getInt("Level") > 0 && f.getInt("Level") < 128) {
                            cr.setLevel((byte)f.getInt("Level"));
                        }

                        cr.storedLuck = f.getInt("StoredLuck");
                        if (f.getConfigurationSection("LBItem") != null) {
                            if (f.getItemStack("LBItem.1") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 54, f.getItemStack("LBItem.1"));
                            }

                            if (f.getItemStack("LBItem.2") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 53, f.getItemStack("LBItem.2"));
                            }

                            if (f.getItemStack("LBItem.3") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 52, f.getItemStack("LBItem.3"));
                            }

                            if (f.getItemStack("LBItem.4") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 45, f.getItemStack("LBItem.4"));
                            }

                            if (f.getItemStack("LBItem.5") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 44, f.getItemStack("LBItem.5"));
                            }

                            if (f.getItemStack("LBItem.6") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 43, f.getItemStack("LBItem.6"));
                            }

                            if (f.getItemStack("LBItem.7") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 36, f.getItemStack("LBItem.7"));
                            }

                            if (f.getItemStack("LBItem.8") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 35, f.getItemStack("LBItem.8"));
                            }

                            if (f.getItemStack("LBItem.9") != null) {
                                cr.inv.setItem(cr.inv.getSize() - 34, f.getItemStack("LBItem.9"));
                            }
                        }

                        cr.save(false);
                        cr.func_loop();
                    } else {
                        c.set(s, (Object)null);
                        saveFile();
                    }
                }
            }
        }
    }

    public String toString() {
        return "LuckyCraftingTable:[ID:" + this.id + ",Level:" + this.level + ",Player:" + this.player + ",StoredLuck:" + this.storedLuck + ",Fuel:" + this.fuel + ",Extra:" + this.extraLuck + "]";
    }
}

