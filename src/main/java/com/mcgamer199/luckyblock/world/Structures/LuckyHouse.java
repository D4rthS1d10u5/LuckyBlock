package com.mcgamer199.luckyblock.world.Structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LuckyHouse extends Structure {
    public LuckyHouse() {
    }

    public int getId() {
        return 5;
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        byte data = (byte)this.random.nextInt(4);

        int z;
        int y;
        for(z = -2; z < 3; ++z) {
            for(y = -2; y < 3; ++y) {
                block.getLocation().add((double)z, -1.0D, (double)y).getBlock().setType(Material.LOG);
                block.getLocation().add((double)z, -1.0D, (double)y).getBlock().setData(data);
            }
        }

        for(z = -2; z < 3; ++z) {
            for(y = 0; y < 8; ++y) {
                block.getLocation().add((double)z, (double)y, 3.0D).getBlock().setType(Material.LOG);
                block.getLocation().add((double)z, (double)y, 3.0D).getBlock().setData(data);
            }
        }

        for(z = -2; z < 3; ++z) {
            for(y = 0; y < 8; ++y) {
                block.getLocation().add((double)z, (double)y, -3.0D).getBlock().setType(Material.LOG);
                block.getLocation().add((double)z, (double)y, -3.0D).getBlock().setData(data);
            }
        }

        for(z = -2; z < 3; ++z) {
            for(y = 0; y < 8; ++y) {
                block.getLocation().add(-3.0D, (double)y, (double)z).getBlock().setType(Material.LOG);
                block.getLocation().add(-3.0D, (double)y, (double)z).getBlock().setData(data);
            }
        }

        for(z = -2; z < 3; ++z) {
            for(y = 0; y < 8; ++y) {
                block.getLocation().add(3.0D, (double)y, (double)z).getBlock().setType(Material.LOG);
                block.getLocation().add(3.0D, (double)y, (double)z).getBlock().setData(data);
            }
        }

        super.build(loc);
    }
}

