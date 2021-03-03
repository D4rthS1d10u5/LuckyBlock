package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityTrophyNameTag;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trophy {

    private static final Map<Location, Trophy> trophies = new HashMap<>();
    private static final File fileF = new File(LuckyBlockPlugin.d() + "data/trophies.yml");
    private static final FileConfiguration file = YamlConfiguration.loadConfiguration(fileF);
    private static boolean loaded = false;

    static  {
        Scheduler.timer(Trophy::purgeInvalidBlocks, 20, 20);
    }

    private UUID uuid = UUID.randomUUID();
    @Getter
    private final Block block;
    @Getter
    private final ItemStack itemToDrop;

    private Trophy(Block block, ItemStack itemToDrop) {
        this.block = block;
        if (itemToDrop != null) {
            itemToDrop.setAmount(1);
        }

        this.itemToDrop = itemToDrop;
    }

    public static void place(Block block, ItemStack itemToDrop) {
        Trophy trophy = new Trophy(block, itemToDrop);
        trophy.save(true);
        CustomEntityTrophyNameTag trophyNameTag = new CustomEntityTrophyNameTag();
        trophyNameTag.spawn(trophy);
    }

    public static Trophy getByBlock(Block block) {
        return trophies.get(block.getLocation());
    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            if (file.getConfigurationSection("Trophies") != null) {
                for (String s : file.getConfigurationSection("Trophies").getKeys(false)) {
                    ConfigurationSection c = file.getConfigurationSection("Trophies").getConfigurationSection(s);
                    if (c != null) {
                        String b = c.getString("Block");
                        ItemStack i = c.getItemStack("Item");
                        UUID u = UUID.fromString(c.getString("UUID"));
                        Trophy t = new Trophy(LocationUtils.blockFromString(b), i);
                        t.uuid = u;
                        t.save(false);
                    }
                }
            }
        }
    }

    private void save(boolean saveToFile) {
        trophies.put(this.block.getLocation(), this);

        if (saveToFile) {
            this.saveFile();
        }
    }

    public void remove() {
        file.set("Trophies.Trophy" + this.uuid.toString(), null);
        try {
            file.save(fileF);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    private void saveFile() {
        file.set("Trophies.Trophy" + this.uuid.toString() + ".UUID", this.uuid.toString());
        file.set("Trophies.Trophy" + this.uuid.toString() + ".Block", LocationUtils.asString(this.block.getLocation()));
        file.set("Trophies.Trophy" + this.uuid.toString() + ".Item", this.itemToDrop);

        try {
            file.save(fileF);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public boolean isValid() {
        return trophies.containsKey(block.getLocation());
    }

    private static void purgeInvalidBlocks() {
        trophies.values().removeIf(trophy -> {
           if(!trophy.getBlock().getType().equals(Material.SKULL)) {
               trophy.remove();
               return true;
           }

           return false;
        });
    }
}
