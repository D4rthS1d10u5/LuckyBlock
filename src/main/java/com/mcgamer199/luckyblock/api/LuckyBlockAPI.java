package com.mcgamer199.luckyblock.api;

import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.enums.BlockProperty;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LuckyBlockAPI implements Listener {
    public static File lbsF;
    public static FileConfiguration lbs;
    public static List<String> lbwblocks;
    public static List<Detector> detectors;
    static List<String> locations;
    static File portalsF;
    static FileConfiguration portals;
    private static boolean loaded;

    static {
        lbsF = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "LuckyBlocks.yml");
        lbs = YamlConfiguration.loadConfiguration(lbsF);
        lbwblocks = new ArrayList();
        detectors = new ArrayList();
        locations = new ArrayList();
        portalsF = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "Data/Portals.yml");
        portals = YamlConfiguration.loadConfiguration(portalsF);
        loaded = false;
    }

    public LuckyBlockAPI() {
    }

    public static void loadLuckyBlocks() {
        if (!loaded) {
            loaded = true;
            LuckyBlockPlugin.instance.getLogger().info(MyTasks.val("log.lb.loading", false));
            int total = 0;

            try {
                for (int x = 0; x < lbs.getStringList("LuckyBlocks").size(); ++x) {
                    String str = lbs.getStringList("LuckyBlocks").get(x);
                    str = str.substring(1, str.length() - 1);
                    String[] d = str.split("/s/");
                    LBType type = null;
                    Block block = null;
                    String placedBy = null;
                    int luck = 0;
                    int var10 = d.length;

                    String[] a;
                    for (int var9 = 0; var9 < var10; ++var9) {
                        String s = d[var9];
                        a = s.split(":=");
                        if (a.length == 2) {
                            if (a[0].equalsIgnoreCase("LBType")) {
                                type = LBType.fromId(Integer.parseInt(a[1]));
                            } else if (a[0].equalsIgnoreCase("Block")) {
                                block = LocationUtils.blockFromString(a[1]);
                            } else if (a[0].equalsIgnoreCase("PlacedBy")) {
                                placedBy = a[1];
                            } else if (a[0].equalsIgnoreCase("Luck")) {
                                luck = Integer.parseInt(a[1]);
                            }
                        }
                    }

                    if (block != null && type != null) {
                        final LuckyBlock luckyBlock = new LuckyBlock(type, block, luck, placedBy, false, false);
                        a = d;
                        int var26 = d.length;

                        for (var10 = 0; var10 < var26; ++var10) {
                            String s = a[var10];
                            String[] a2 = s.split(":=");
                            if (a2.length == 2) {
                                if (a2[0].equalsIgnoreCase("Drop")) {
                                    if (LuckyBlockDrop.isValid(a2[1])) {
                                        luckyBlock.setDrop(LuckyBlockDrop.valueOf(a2[1]), false, false);
                                    }
                                } else if (a2[0].equalsIgnoreCase("Tick_a")) {
                                    luckyBlock.tickDelay = Integer.parseInt(a2[1]);
                                } else if (a2[0].equalsIgnoreCase("CustomDrop")) {
                                    luckyBlock.customDrop = CustomDropManager.getByName(a2[1]);
                                } else if (a2[0].equalsIgnoreCase("Owner")) {
                                    if (!a2[1].equalsIgnoreCase("null")) {
                                        luckyBlock.owner = UUID.fromString(a2[1]);
                                    }
                                } else if (a2[0].equalsIgnoreCase("Facing")) {
                                    luckyBlock.facing = BlockFace.valueOf(a2[1].toUpperCase());
                                } else if (a2[0].equalsIgnoreCase("Freezed")) {
                                    if (a2[1].equalsIgnoreCase("true")) {
                                        luckyBlock.freeze();
                                    }
                                } else if (a2[0].equalsIgnoreCase("Options")) {
                                    //TODO сделать обратную совместимость со старым форматом свойств
                                    String p = a2[1].replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace("'", "");
                                    String[] c = p.split(";");

                                    for (String value : c) {
                                        String[] u = value.split(":");
                                        if (u.length > 1) {
                                            String[] g = u[1].split(",");
                                            String[] op = new String[64];

                                            System.arraycopy(g, 0, op, 0, g.length);
                                            luckyBlock.getDropOptions().putStringArray(u[0], op);
                                        }
                                    }
                                }
                            }
                        }

                        if (block.getType() == Material.AIR) {
                            luckyBlock.remove();
                        } else {
                            ++total;
                        }

                        Scheduler.later(() -> {
                            luckyBlock.playEffects();
                            if(luckyBlock.getType().getProperties().contains(BlockProperty.EXPLOSION_RESISTANCE)) {
                                LuckyBlockPlugin.instance.Loops(luckyBlock);
                            }
                        }, 10L);
                    }
                }

                if (total == 1) {
                    String s = MyTasks.val("log.lb.found", false);
                    s = s.replace("%total%", String.valueOf(total));
                    LuckyBlockPlugin.instance.getLogger().info(s);
                }
            } catch (Exception var21) {
                var21.printStackTrace();
            }

        }
    }

    public static void savePortals() {
        portals.set("Portals", lbwblocks);
        portals.set("Locations", locations);
        savePortalsFile();
    }

    public static Location getLocation(Player player) {
        for (int x = 0; x < locations.size(); ++x) {
            String[] d = locations.get(x).split(",");
            if (d.length == 5) {
                String uuid = d[0];
                if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    String all = d[1] + "," + d[2] + "," + d[3] + "," + d[4];
                    return LocationUtils.locationFromString(all);
                }
            }
        }

        return null;
    }

    public static void addLocation(Player player, Location location) {
        for (int x = 0; x < locations.size(); ++x) {
            String s = locations.get(x);
            String[] d = s.split(",");
            if (d.length == 5) {
                String uuid = d[0];
                if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    locations.remove(s);
                }
            }
        }

        locations.add(player.getUniqueId().toString() + "," + LocationUtils.asString(location));
        savePortals();
    }

    public static void loadPortals() {
        List<String> list = portals.getStringList("Portals");
        if (list.size() > 0) {
            for (int x = 0; x < list.size(); ++x) {
                lbwblocks.add(list.get(x));
            }
        }

        locations = portals.getStringList("Locations");
    }

    public static ItemStack createItemStack(Material type, int amount, short data, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(type);
        if (amount > 0) {
            item.setAmount(amount);
        }

        if (data > 0) {
            item.setDurability(data);
        }

        ItemMeta itemM = item.getItemMeta();
        if (name != null) {
            itemM.setDisplayName(name);
        }

        if (lore != null && lore.size() > 0) {
            itemM.setLore(lore);
        }

        if (enchants != null && enchants.size() > 0) {
            Iterator var9 = enchants.keySet().iterator();

            while (var9.hasNext()) {
                Enchantment e = (Enchantment) var9.next();
                itemM.addEnchant(e, enchants.get(e), true);
            }
        }

        item.setItemMeta(itemM);
        return item;
    }

    public static void removeDrops(World world) {
        Iterator var2 = world.getEntities().iterator();

        while (true) {
            Entity e;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                e = (Entity) var2.next();
            } while (!(e instanceof Item));

            Item item = (Item) e;
            ItemStack i = item.getItemStack();
            LBType type = null;
            Iterator var7 = LBType.getTypes().iterator();

            while (var7.hasNext()) {
                LBType t = (LBType) var7.next();
                if (t.getType() == i.getType() && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equalsIgnoreCase(t.getName())) {
                    type = t;
                }
            }

            if (type != null) {
                item.remove();
            }
        }
    }

    private static void savePortalsFile() {
        try {
            portals.save(portalsF);
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    public static String getDet(int id) {
        String s = null;
        ConfigurationSection cs = LuckyBlockPlugin.instance.detectors.getConfigurationSection("Detectors");

        try {
            for (int x = 0; x < cs.getKeys(false).size(); ++x) {
                if (cs.getInt(cs.getKeys(false).toArray()[x].toString() + ".ID") != 0) {
                    s = "Detectors." + cs.getKeys(false).toArray()[x].toString();
                    x = cs.getKeys(false).size();
                }
            }
        } catch (Exception var4) {
        }

        return s;
    }

    public static void saveLBFile() {
        try {
            lbs.save(lbsF);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void saveDetFile() {
        try {
            LuckyBlockPlugin.instance.detectors.save(LuckyBlockPlugin.instance.detectorsF);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void spawnLuckyBlockItem(LuckyBlock luckyBlock, Location loc) {
        spawnLuckyBlockItem(luckyBlock, luckyBlock.getLuck(), loc);
    }

    public static void spawnLuckyBlockItem(LuckyBlock luckyBlock, int luck, Location loc) {
        ItemStack item = luckyBlock.getType().toItemStack(luck);
        loc.getWorld().dropItem(loc, item);
    }
}

