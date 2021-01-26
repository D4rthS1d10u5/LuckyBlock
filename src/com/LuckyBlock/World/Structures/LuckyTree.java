package com.LuckyBlock.World.Structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class LuckyTree extends Structure {

    private List<Location> locs = new ArrayList();
    static int[] Tall = new int[]{4, 9};

    public LuckyTree() {
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.IRON_BLOCK);
        int y = 1;
        int g = this.random.nextInt(Tall[0]) + (Tall[1] - Tall[0]);

        int x;
        for(x = 1; x < g; ++x) {
            block.getLocation().add(0.0D, (double)x, 0.0D).getBlock().setType(Material.IRON_BLOCK);
            ++y;
        }

        int z;
        for(x = -3; x < 4; ++x) {
            for(z = -3; z < 4; ++z) {
                block.getLocation().add((double)x, (double)y, (double)z).getBlock().setType(Material.SPONGE);
                if (this.random.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add((double)x, (double)y, (double)z));
                }
            }
        }

        block.getLocation().add(-3.0D, (double)y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, (double)y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, (double)y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, (double)y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-3.0D, (double)y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, (double)y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, (double)y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, (double)y, 3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, (double)y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, (double)y, -3.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(3.0D, (double)y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, (double)y, -3.0D).getBlock().setType(Material.AIR);
        ++y;

        for(x = -2; x < 3; ++x) {
            for(z = -2; z < 3; ++z) {
                block.getLocation().add((double)x, (double)y, (double)z).getBlock().setType(Material.SPONGE);
                if (this.random.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add((double)x, (double)y, (double)z));
                }
            }
        }

        block.getLocation().add(2.0D, (double)y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, (double)y, -2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(-2.0D, (double)y, 2.0D).getBlock().setType(Material.AIR);
        block.getLocation().add(2.0D, (double)y, 2.0D).getBlock().setType(Material.AIR);
        ++y;

        for(x = -1; x < 2; ++x) {
            for(z = -1; z < 2; ++z) {
                block.getLocation().add((double)x, (double)y, (double)z).getBlock().setType(Material.SPONGE);
                if (this.random.nextInt(100) > 90) {
                    this.locs.add(block.getLocation().add((double)x, (double)y, (double)z));
                }
            }
        }

        ++y;
        block.getLocation().add(0.0D, (double)y, 0.0D).getBlock().setType(Material.SPONGE);
        if (this.random.nextInt(100) > 90) {
            this.locs.add(block.getLocation().add(0.0D, (double)y, 0.0D));
        }

        super.build(loc);
    }

    public int getId() {
        return 1;
    }
}