package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.customentity.EntityGuardian;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BossDungeon extends LuckyDungeon {

    public static List<String> baseBlocks = new ArrayList<>();
    private static final Material[] blocks = new Material[] {Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK};

    public BossDungeon() {}

    public void build(Location loc) {
        EntityGuardian b = new EntityGuardian();
        b.type = LBType.getRandomType();
        Block block = loc.getBlock();
        int a = 6;

        int r;
        int x;
        int z;
        for (r = -5; r < 30; ++r) {
            for (x = -5; x < 6; ++x) {
                for (z = -5; z < 6; ++z) {
                    block.getLocation().add(x, r, z).getBlock().setType(Material.BRICK);
                }
            }
        }

        for (r = -3; r < 4; ++r) {
            for (x = 1; x < 29; ++x) {
                for (z = -3; z < 4; ++z) {
                    block.getLocation().add(r, x, z).getBlock().setType(Material.AIR);
                }
            }
        }

        for (r = 0; r < 6; ++r) {
            for (x = a * -1; x < a + 1; ++x) {
                for (z = a * -1; z < a + 1; ++z) {
                    block.getLocation().add(x, r + 30, z).getBlock().setType(Material.BRICK);
                }
            }

            --a;
        }

        block.getLocation().add(0.0D, 35.0D, 0.0D).getBlock().setType(RandomUtils.getRandomObject(blocks));
        baseBlocks.add(LocationUtils.asString(block.getLocation().add(0.0D, 35.0D, 0.0D).getBlock().getLocation()));
        super.build(loc);
        b.spawn(loc);
    }

    public int getId() {
        return 10;
    }
}
