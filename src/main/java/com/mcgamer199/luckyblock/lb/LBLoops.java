package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

public class LBLoops {
    public LBLoops() {
    }

    static void loop(final LuckyBlock luckyBlock) {
        Scheduler.timer(new BukkitRunnable() {

            @Override
            public void run() {
                if (luckyBlock.isValid()) {
                    if (luckyBlock.getBlock().getRelative(BlockFace.UP).getType() == Material.FIRE) {
                        if (luckyBlock.getType().hasProperty(LBType.BlockProperty.EXPLODE_ON_FIRE)) {
                            luckyBlock.explode();
                        } else if (luckyBlock.getType().hasProperty(LBType.BlockProperty.REMOVE_ON_FIRE)) {
                            luckyBlock.remove();
                        }
                    }
                } else {
                    cancel();
                }
            }
        }, 5, 5);
    }
}
