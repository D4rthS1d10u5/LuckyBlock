package com.mcgamer199.luckyblock.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LuckyTrap extends Structure {

    public LuckyTrap() {
    }

    public int getId() {
        return 4;
    }

    public void build(Location loc) {
        Block block = loc.getBlock();

        int x;
        int z;
        for (x = -2; x < 3; ++x) {
            for (z = -2; z < 3; ++z) {
                block.getLocation().add(x, 0.0D, z).getBlock().setType(Material.COBBLESTONE);
            }
        }

        for (x = -1; x < 2; ++x) {
            for (z = -1; z < 2; ++z) {
                block.getLocation().add(x, 0.0D, z).getBlock().setType(Material.AIR);
            }
        }

        for (x = -2; x < 3; ++x) {
            for (z = -2; z < 3; ++z) {
                block.getLocation().add(x, -1.0D, z).getBlock().setType(Material.COBBLESTONE);
            }
        }

        block.getLocation().add(0.0D, 0.0D, -2.0D).getBlock().setType(Material.AIR);

        for (x = -1; x < 2; ++x) {
            for (z = -1; z < 2; ++z) {
                block.getLocation().add(x, -2.0D, z).getBlock().setType(Material.TNT);
            }
        }

        block.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().setType(Material.STONE_PLATE);
        super.build(loc);
    }
}
