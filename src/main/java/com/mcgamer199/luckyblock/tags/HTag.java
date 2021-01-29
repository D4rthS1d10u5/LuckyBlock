package com.mcgamer199.luckyblock.tags;

import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Iterator;
import java.util.Random;

public class HTag extends ColorsClass {
    public HTag() {
    }

    public static ConfigurationSection getSection(String s) {
        String[] d = s.split(",");
        FileConfiguration f = getFileByLoc(d[0]);
        return f.getConfigurationSection(d[1]);
    }

    protected static FileConfiguration getFileByLoc(String s) {
        File ff = new File(LuckyBlockPlugin.d() + "Drops/" + s);
        FileConfiguration f = YamlConfiguration.loadConfiguration(ff);
        return f;
    }

    public static ConfigurationSection getSectionMain(String s) {
        String[] d = s.split(",");
        File ff = new File(LuckyBlockPlugin.d() + d[0]);
        FileConfiguration f = YamlConfiguration.loadConfiguration(ff);
        return f.getConfigurationSection(d[1]);
    }

    protected static String getRandomLoc(FileConfiguration file, String loc) {
        return getRandomLoc(file.getConfigurationSection(loc));
    }

    protected static String getRandomLoc(ConfigurationSection c) {
        if (c != null) {
            int x = 0;
            int tot = getTotal(c);
            int random;
            if (tot <= 0) {
                random = (new Random()).nextInt(c.getKeys(false).size());
                return c.getKeys(false).toArray()[random].toString();
            } else {
                random = (new Random()).nextInt(tot) + 1;
                Iterator var5 = c.getKeys(false).iterator();

                while (true) {
                    String s;
                    do {
                        if (!var5.hasNext()) {
                            return null;
                        }

                        s = (String) var5.next();
                    } while (c.getConfigurationSection(s) == null);

                    ConfigurationSection f = c.getConfigurationSection(s);
                    Iterator var8 = f.getKeys(false).iterator();

                    while (var8.hasNext()) {
                        String t = (String) var8.next();
                        if (t.equalsIgnoreCase("Chance")) {
                            if (f.getInt(t) + x >= random) {
                                return s;
                            }

                            x += f.getInt(t);
                        }
                    }
                }
            }
        } else {
            return null;
        }
    }

    protected static int getTotal(FileConfiguration file, String loc) {
        return getTotal(file.getConfigurationSection(loc));
    }

    protected static int getTotal(ConfigurationSection cs) {
        int total = 0;
        if (cs != null) {
            Iterator var3 = cs.getKeys(false).iterator();

            while (true) {
                String s;
                do {
                    if (!var3.hasNext()) {
                        return total;
                    }

                    s = (String) var3.next();
                } while (cs.getConfigurationSection(s) == null);

                ConfigurationSection c = cs.getConfigurationSection(s);
                Iterator var6 = c.getKeys(false).iterator();

                while (var6.hasNext()) {
                    String t = (String) var6.next();
                    if (t.equalsIgnoreCase("Chance")) {
                        total += c.getInt(t);
                    }
                }
            }
        } else {
            return total;
        }
    }

    protected static int getRandomNumber(String[] d) {
        int a1 = Integer.parseInt(d[0]);
        int num;
        if (d.length < 2) {
            num = a1;
        } else {
            int a2 = Integer.parseInt(d[1]);
            num = random.nextInt(a2 - a1 + 1) + a1;
        }

        return num;
    }

    protected static String translateString(String a, HTag.IDataType... data) {
        a = a.replace("%s%", "'");
        String[] d = a.split("%TRANSLATE%");

        for (int x = 1; x < d.length; ++x) {
            String[] d1 = d[x].split("!");
            String pth = d1[0];
            String z = ChatColor.translateAlternateColorCodes('&', IObjects.fLang.getString(pth));
            d[x] = z;
        }

        a = "";
        String[] var14 = d;
        int var13 = d.length;

        int var12;
        for (var12 = 0; var12 < var13; ++var12) {
            String b = var14[var12];
            a = a + b;
        }

        if (data != null && data.length > 0) {
            HTag.IDataType[] var15 = data;
            var13 = data.length;

            for (var12 = 0; var12 < var13; ++var12) {
                HTag.IDataType dat = var15[var12];
                Object o = dat.object;
                if (o != null) {
                    if (o instanceof Entity) {
                        Entity entity = (Entity) o;
                        if (entity.getCustomName() != null) {
                            a = a.replace("%ENTITY_CUSTOMNAME%", entity.getCustomName());
                        }

                        a = a.replace("%ENTITY_FIRETICKS%", String.valueOf(entity.getFireTicks()));
                        a = a.replace("%ENTITY_LOCATION_WORLD%", entity.getLocation().getWorld().getName());
                        a = a.replace("%ENTITY_LOCATION_X%", String.valueOf(entity.getLocation().getX()));
                        a = a.replace("%ENTITY_LOCATION_Y%", String.valueOf(entity.getLocation().getY()));
                        a = a.replace("%ENTITY_LOCATION_Z%", String.valueOf(entity.getLocation().getZ()));
                        a = a.replace("%ENTITY_LIFE_TICKS%", String.valueOf(entity.getTicksLived()));
                        a = a.replace("%ENTITY_UUID%", "" + entity.getUniqueId());
                        a = a.replace("%ENTITY_TYPE%", entity.getType().name());
                        if (o instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) o;
                            a = a.replace("%ENTITY_HEALTH%", String.valueOf(living.getHealth()));
                            a = a.replace("%ENTITY_MAXHEALTH%", String.valueOf(living.getMaxHealth()));
                        }

                        if (o instanceof Player) {
                            Player player = (Player) o;
                            a = a.replace("%PLAYER%", player.getName());
                            a = a.replace("%PLAYER_XP%", String.valueOf(player.getExp()));
                            a = a.replace("%PLAYER_DISPLAY_NAME%", player.getDisplayName());
                            a = a.replace("%PLAYER_GAMEMODE%", player.getGameMode().name());
                        }
                    } else if (o instanceof Block) {
                        Block block = (Block) o;
                        a = a.replace("%BLOCK_BIOME%", block.getBiome().name());
                    } else if (o instanceof ItemStack) {
                        ItemStack item = (ItemStack) o;
                        a = a.replace("%ITEM_TYPE%", item.getType().name());
                    }
                }
            }
        }

        a = ChatColor.translateAlternateColorCodes('&', a);
        return a;
    }

    public enum TDataType {
        PLAYER,
        ENTITY,
        BLOCK,
        LIVING,
        ITEM;

        TDataType() {
        }
    }

    public static class IDataType {
        private final HTag.TDataType dataType;
        private final Object object;

        public IDataType(HTag.TDataType dataType, Object object) {
            this.dataType = dataType;
            this.object = object;
        }

        public HTag.TDataType getDataType() {
            return this.dataType;
        }

        public Object getObject() {
            return this.object;
        }
    }
}
