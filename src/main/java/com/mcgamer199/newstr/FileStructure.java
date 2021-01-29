package com.mcgamer199.newstr;

import com.mcgamer199.luckyblock.customentity.boss.EntityBossWitch;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.tags.BlockTags;
import com.mcgamer199.luckyblock.tags.ChestFiller;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileStructure {
    public FileStructure() {
    }

    public static void generate(String fileS, String l, Location loc) {
        if (fileS != null && l != null) {
            File fileF = new File(LuckyBlockPlugin.d() + "data/plugin/str/" + fileS + ".yml");
            FileConfiguration file = YamlConfiguration.loadConfiguration(fileF);
            String[] d = l.split(",");
            BlockTags.buildStructure(file.getConfigurationSection(d[0]).getConfigurationSection(d[1]), loc);
        }

    }

    static void _generateLBBossDungeon(Location loc) {
    }

    public static void generateWitchDungeon(Location loc) {
        Location l = new Location(loc.getWorld(), loc.getX() - 22.0D, loc.getY(), loc.getZ() - 22.0D);
        Schematic.loadArea(IObjects.getStoredFile("witch_structure"), l);
        FileConfiguration c = YamlConfiguration.loadConfiguration(IObjects.getStoredFile("witch_chests"));
        Location l1 = new Location(loc.getWorld(), loc.getX() + 4.0D, loc.getY() + 22.0D, loc.getZ() - 4.0D);
        Location l2 = new Location(loc.getWorld(), loc.getX() - 4.0D, loc.getY() + 22.0D, loc.getZ() + 4.0D);
        Location l3 = new Location(loc.getWorld(), loc.getX() + 4.0D, loc.getY() + 22.0D, loc.getZ() + 4.0D);
        Location l4 = new Location(loc.getWorld(), loc.getX() - 4.0D, loc.getY() + 22.0D, loc.getZ() - 4.0D);
        Block block1 = l1.getBlock();
        Block block2 = l2.getBlock();
        Block block3 = l3.getBlock();
        Block block4 = l4.getBlock();
        Chest chest1 = (Chest) block1.getState();
        Chest chest2 = (Chest) block2.getState();
        Chest chest3 = (Chest) block3.getState();
        Chest chest4 = (Chest) block4.getState();
        ChestFiller filler = new ChestFiller(c.getConfigurationSection("Chests"), chest1);
        filler.loc1 = "FoodsChest";
        filler.fill();
        filler.chest = chest2;
        filler.loc1 = "ArmorChest";
        filler.fill();
        filler.chest = chest3;
        filler.loc1 = "ToolsWeaponsChest";
        filler.fill();
        filler.chest = chest4;
        filler.loc1 = "ItemsChest";
        filler.fill();
        EntityBossWitch w = new EntityBossWitch();
        w.spawn(loc.add(0.0D, 8.0D, 0.0D));
    }
}


