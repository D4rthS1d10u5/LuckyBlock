package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.DataException;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public class Schematic {
    public Schematic() {
    }

    public static void loadArea(File file, Location loc) {
        loadArea(loc.getWorld(), file, new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public static void loadArea(World world, File file, Vector origin) {
        if (LuckyBlockPlugin.isWorldEditValid()) {
            EditSession es = new EditSession(new BukkitWorld(world), 999999999);
            CuboidClipboard cc = null;

            try {
                cc = CuboidClipboard.loadSchematic(file);
            } catch (IOException | DataException var7) {
                var7.printStackTrace();
            }

            try {
                cc.paste(es, origin, false);
            } catch (MaxChangedBlocksException var6) {
                LuckyBlockPlugin.instance.getLogger().info("Couldn't load schematic " + file.getName() + " !");
            }
        } else {
            LuckyBlockPlugin.instance.getLogger().info("Couldn't load schematic (worldedit is not installed!)");
        }

    }
}

