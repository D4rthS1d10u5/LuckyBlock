package com.mcgamer199.luckyblock.world.Structures;

import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.customentity.EntityGuardian;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BossDungeon extends LuckyDungeon {
    public static List<String> baseBlocks = new ArrayList();

    public BossDungeon() {
    }

    public void build(Location loc) {
        EntityGuardian b = new EntityGuardian();
        b.type = LBType.getRandomType();
        Block block = loc.getBlock();
        int a = 6;

        int r;
        int x;
        int z;
        for(r = -5; r < 30; ++r) {
            for(x = -5; x < 6; ++x) {
                for(z = -5; z < 6; ++z) {
                    block.getLocation().add((double)x, (double)r, (double)z).getBlock().setType(Material.BRICK);
                }
            }
        }

        for(r = -3; r < 4; ++r) {
            for(x = 1; x < 29; ++x) {
                for(z = -3; z < 4; ++z) {
                    block.getLocation().add((double)r, (double)x, (double)z).getBlock().setType(Material.AIR);
                }
            }
        }

        for(r = 0; r < 6; ++r) {
            for(x = a * -1; x < a + 1; ++x) {
                for(z = a * -1; z < a + 1; ++z) {
                    block.getLocation().add((double)x, (double)(r + 30), (double)z).getBlock().setType(Material.BRICK);
                }
            }

            --a;
        }

        r = this.random.nextInt(4) + 1;
        Material mat = Material.IRON_BLOCK;
        if (r == 2) {
            mat = Material.GOLD_BLOCK;
        } else if (r == 3) {
            mat = Material.DIAMOND_BLOCK;
        } else if (r == 4) {
            mat = Material.EMERALD_BLOCK;
        }

        block.getLocation().add(0.0D, 35.0D, 0.0D).getBlock().setType(mat);
        baseBlocks.add(LB.blockToString(block.getLocation().add(0.0D, 35.0D, 0.0D).getBlock()));
        super.build(loc);
        b.spawn(loc);
    }

    public int getId() {
        return 10;
    }
}
