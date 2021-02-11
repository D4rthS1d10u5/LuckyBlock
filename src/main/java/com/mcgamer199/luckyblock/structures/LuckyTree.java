package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class LuckyTree extends Structure {

    static int[] Tall = new int[]{4, 9};
    private final List<Location> locs = new ArrayList();

    public LuckyTree() {
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.IRON_BLOCK);
        int y = 1;
        int g = RandomUtils.nextInt(Tall[0]) + (Tall[1] - Tall[0]);

        int x;
        for (x = 1; x < g; ++x) {
            block.getLocation().add(0.0D, x, 0.0D).getBlock().setType(Material.IRON_BLOCK);
            ++y;
        }

        int z;
        for (x = -3; x < 4; ++x) {
            for (z = -3; z < 4; ++z) {
                block.getLocation().add(x, y, z).getBlock().setType(Material.SPONGE);
                if (RandomUtils.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add(x, y, z));
                }
            }
        }

        block.getLocation().add(-3.0D, y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, y, -3.0D).getBlock().setType(Material.AIR);
        ++y;

        for (x = -2; x < 3; ++x) {
            for (z = -2; z < 3; ++z) {
                block.getLocation().add(x, y, z).getBlock().setType(Material.SPONGE);
                if (RandomUtils.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add(x, y, z));
                }
            }
        }

        block.getLocation().add(2.0D, y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, y, 2.0D).getBlock().setType(Material.AIR);
        ++y;

        for (x = -1; x < 2; ++x) {
            for (z = -1; z < 2; ++z) {
                block.getLocation().add(x, y, z).getBlock().setType(Material.SPONGE);
                if (RandomUtils.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add(x, y, z));
                }
            }
        }

        ++y;
        block.getLocation().add(0.0D, y, 0.0D).getBlock().setType(Material.SPONGE);
        if (RandomUtils.nextInt(100) > 90) {
            this.locs.add(block.getLocation().add(0.0D, y, 0.0D));
        }

        super.build(loc);
    }

    public int getId() {
        return 1;
    }
}