package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.tags.ItemStackGetter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class CItem {
    public static HashMap<String, ItemStack> customItems = new HashMap();

    public CItem() {
    }

    public static void loadItemsFromFolder(String folderName) {
        String a = folderName + "/customItems/";
        File folder = new File(LuckyBlockPlugin.d() + "Drops/" + a);
        if (folder.listFiles() != null) {
            File[] var6;
            int var5 = (var6 = folder.listFiles()).length;

            for (int var4 = 0; var4 < var5; ++var4) {
                File file = var6[var4];
                if (file.exists() && file.getName().endsWith(".yml")) {
                    FileConfiguration f = YamlConfiguration.loadConfiguration(file);
                    addItem(a + file.getName(), getItem(f));
                }
            }
        }

    }

    static ItemStack getItem(FileConfiguration file) {
        ItemStack item = null;
        ConfigurationSection c = file.getConfigurationSection("Item");
        if (c != null) {
            item = ItemStackGetter.getItemStack(c);
        }

        return item;
    }

    static void addItem(String a, ItemStack item) {
        if (a != null && item != null && !customItems.containsKey(a)) {
            customItems.put(a, item);
        }

    }

    public static ItemStack getItem(String name) {
        return customItems.containsKey(name) ? customItems.get(name) : null;
    }
}
