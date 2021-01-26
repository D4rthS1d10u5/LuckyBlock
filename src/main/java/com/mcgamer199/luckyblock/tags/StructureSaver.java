package com.mcgamer199.luckyblock.tags;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StructureSaver {
    public StructureSaver() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int saveStructure(File fileF, Location loc1, Location loc2, boolean withAir) {
        FileConfiguration file = YamlConfiguration.loadConfiguration(fileF);
        file.set("Structures.Structure1.Chance", 1);
        file.set("Structures.Structure1.LocationType", "BLOCK");
        int i = 0;

        for (int x = 0; x < loc2.getBlockX() - loc1.getBlockX() + 1; ++x) {
            for (int y = 0; y < loc2.getBlockY() - loc1.getBlockY() + 1; ++y) {
                for (int z = 0; z < loc2.getBlockZ() - loc1.getBlockZ() + 1; ++z) {
                    Location loc = new Location(loc1.getWorld(), loc1.getBlockX() + x, loc1.getBlockY() + y, loc1.getBlockZ() + z);
                    Block block = loc.getBlock();
                    if (block.getType() == Material.AIR) {
                        if (withAir) {
                            ++i;
                            saveBlock(file, block, new int[]{x, y, z}, i);
                        }
                    } else {
                        ++i;
                        saveBlock(file, block, new int[]{x, y, z}, i);
                    }
                }
            }
        }

        try {
            file.save(fileF);
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return i;
    }

    public static int saveStructureFixed(File fileF, Location loc1, Location loc2, boolean withAir) {
        FileConfiguration file = YamlConfiguration.loadConfiguration(fileF);
        file.set("Structures.Structure1.Chance", 1);
        file.set("Structures.Structure1.LocationType", "BLOCK");
        int i = 0;

        int x;
        int y;
        int z;
        Location loc;
        Block block;
        for (x = 0; x < loc2.getBlockX() - loc1.getBlockX() + 1; ++x) {
            for (y = 0; y < loc2.getBlockY() - loc1.getBlockY() + 1; ++y) {
                for (z = 0; z < loc2.getBlockZ() - loc1.getBlockZ() + 1; ++z) {
                    loc = new Location(loc1.getWorld(), loc1.getBlockX() + x, loc1.getBlockY() + y, loc1.getBlockZ() + z);
                    block = loc.getBlock();
                    if (block.getType() == Material.AIR) {
                        if (withAir) {
                            ++i;
                            saveBlock(file, block, new int[]{x, y, z}, i);
                        }
                    } else if (block.getType().isOccluding()) {
                        ++i;
                        saveBlock(file, block, new int[]{x, y, z}, i);
                    }
                }
            }
        }

        for (x = 0; x < loc2.getBlockX() - loc1.getBlockX() + 1; ++x) {
            for (y = 0; y < loc2.getBlockY() - loc1.getBlockY() + 1; ++y) {
                for (z = 0; z < loc2.getBlockZ() - loc1.getBlockZ() + 1; ++z) {
                    loc = new Location(loc1.getWorld(), loc1.getBlockX() + x, loc1.getBlockY() + y, loc1.getBlockZ() + z);
                    block = loc.getBlock();
                    if (block.getType() != Material.AIR && !block.getType().isOccluding()) {
                        ++i;
                        saveBlock(file, block, new int[]{x, y, z}, i);
                    }
                }
            }
        }

        try {
            file.save(fileF);
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return i;
    }

    private static void saveBlock(FileConfiguration file, Block block, int[] l, int i) {
        String p = "Structures.Structure1.Blocks.Block" + i + ".";
        file.set(p + "Location.X", l[0]);
        file.set(p + "Location.Y", l[1]);
        file.set(p + "Location.Z", l[2]);
        file.set(p + "Type", block.getType().name());
        if (block.getData() != 0) {
            file.set(p + "Data", block.getData());
        }

    }
}
