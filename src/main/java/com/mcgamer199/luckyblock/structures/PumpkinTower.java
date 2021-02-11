package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PumpkinTower extends Structure {
    public PumpkinTower() {
    }

    public int getId() {
        return 3;
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        int r = RandomUtils.nextInt(5) + 3;
        int s = 0;

        for (int y = 0; y < r; ++y) {
            block.getLocation().add(0.0D, y, 0.0D).getBlock().setType(Material.GOLD_BLOCK);
            ++s;
        }

        block.getLocation().add(0.0D, s, 0.0D).getBlock().setType(Material.PUMPKIN);
        super.build(loc);
    }
}
