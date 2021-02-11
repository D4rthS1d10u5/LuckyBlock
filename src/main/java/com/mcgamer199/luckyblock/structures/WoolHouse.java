package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class WoolHouse extends Structure {
    static int[] Tall = new int[]{8, 17};

    public WoolHouse() {
    }

    public int getId() {
        return 6;
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        int r = RandomUtils.nextInt(Tall[0]) + (Tall[1] - Tall[0]);
        int s = 0;

        int g;
        for (g = 0; g < r; ++g) {
            block.getLocation().add(0.0D, g, 0.0D).getBlock().setType(Material.WOOL);
            block.getLocation().add(0.0D, g, 0.0D).getBlock().setData((byte) 7);
            ++s;
        }

        g = RandomUtils.nextInt(7) + 3;
        if (g % 2 == 0) {
            ++g;
        }

        byte b = (byte) RandomUtils.nextInt(16);
        int k = RandomUtils.nextInt(2) + 1;

        for (int h = g; h > 0; --h) {
            for (int x = g * -1; x < g - 1; ++x) {
                for (int z = g * -1; z < g - 1; ++z) {
                    block.getLocation().add(x + 1, s, z + 1).getBlock().setType(Material.WOOL);
                    block.getLocation().add(x + 1, s, z + 1).getBlock().setData(b);
                }
            }

            if (k == 1) {
                --g;
            } else if (k == 2) {
                g -= 2;
            }

            ++s;
        }

        super.build(loc);
    }
}
