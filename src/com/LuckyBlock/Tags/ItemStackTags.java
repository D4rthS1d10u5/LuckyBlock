package com.LuckyBlock.Tags;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class ItemStackTags extends HTag {
    public ItemStackTags() {
    }

    public static ItemStack[] getRandomItems(ConfigurationSection c) {
        return getItems(c.getConfigurationSection(getRandomLoc(c)));
    }

    public static void fillChest(Chest chest, FileConfiguration file, String loc, String loc1) {
        file.getConfigurationSection(loc);
    }

    public static ItemStack[] getItems(ConfigurationSection c) {
        ItemStack[] items = new ItemStack[128];

        try {
            int x = 0;
            if (c.getConfigurationSection("Items") == null && c.getString("Loc") == null && c.getKeys(false).size() > 0) {
                return getItems(c.getConfigurationSection(getRandomLoc(c)));
            } else {
                Iterator var4 = c.getKeys(false).iterator();

                while(var4.hasNext()) {
                    String s = (String)var4.next();
                    int timesA = 1;
                    if (c.getString("Loc") != null) {
                        return getItems(getSection(c.getString("Loc")));
                    }

                    if (c.getString("Times") != null) {
                        String[] d = c.getString("Times").split("-");
                        timesA = getRandomNumber(d);
                    }

                    if (s.equalsIgnoreCase("Items") && c.getConfigurationSection(s) != null) {
                        for(int i = 0; i < timesA; ++i) {
                            Iterator var8 = c.getConfigurationSection(s).getKeys(false).iterator();

                            while(var8.hasNext()) {
                                String t = (String)var8.next();
                                ConfigurationSection f = c.getConfigurationSection(s).getConfigurationSection(t);
                                int times = 1;
                                if (f.getString("Times") != null) {
                                    String[] d = f.getString("Times").split("-");
                                    times = getRandomNumber(d);
                                }

                                for(int e = times; e > 0; --e) {
                                    ItemStack item = ItemStackGetter.getItemStack(f);
                                    int chance = 100;
                                    if (f.getInt("Chance") > 0) {
                                        chance = f.getInt("Chance");
                                    }

                                    if (random.nextInt(100) + 1 <= chance && item != null) {
                                        items[x] = item;
                                        ++x;
                                    }
                                }
                            }
                        }
                    }
                }

                return items;
            }
        } catch (Exception var14) {
            if (c != null) {
                LuckyBlock.instance.getLogger().info("Error [Section:" + c.getName() + "]");
            } else {
                LuckyBlock.instance.getLogger().info("Error [Invalid Path!]");
            }

            throw var14;
        }
    }
}
