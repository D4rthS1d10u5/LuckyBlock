package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class LuckyWell extends Structure {
    public static List<String> blocks = new ArrayList();

    public LuckyWell() {
    }

    public static boolean isValid(Block block) {
        String b = LocationUtils.asString(block.getLocation());

        for (int x = 0; x < blocks.size(); ++x) {
            if (b.equalsIgnoreCase(blocks.get(x))) {
                return true;
            }
        }

        return false;
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        if (!block.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getType().isSolid()) {
            block.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().setType(Material.COBBLESTONE);
        }

        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                block.getLocation().add(x, -1.0D, z).getBlock().setType(Material.COBBLESTONE);
            }
        }

        block.setType(Material.WATER);
        block.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().setType(Material.IRON_PLATE);
        blocks.add(LocationUtils.asString(block.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getLocation()));
        block.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 0.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 0.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 0.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 0.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 3.0D, 0.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 3.0D, 0.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 3.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 3.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 3.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(0.0D, 3.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(0.0D, 3.0D, -1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(-1.0D, 3.0D, 1.0D).getBlock().setType(Material.COBBLESTONE);
        block.getLocation().add(1.0D, 1.0D, 1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(-1.0D, 1.0D, -1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(-1.0D, 1.0D, 1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(1.0D, 1.0D, -1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(1.0D, 2.0D, 1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(-1.0D, 2.0D, -1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(-1.0D, 2.0D, 1.0D).getBlock().setType(Material.FENCE);
        block.getLocation().add(1.0D, 2.0D, -1.0D).getBlock().setType(Material.FENCE);
    }
}
