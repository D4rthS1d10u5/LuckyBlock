package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class LBLoops {
    public LBLoops() {
    }

    static void loop(final LuckyBlock luckyBlock) {
        final ITask task = new ITask();
        LuckyBlockPlugin var10001 = LuckyBlockPlugin.instance;
        Runnable var10002 = new Runnable() {
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
                    task.run();
                }

            }
        };
        task.setId(ITask.getNewRepeating(var10001, var10002, 5L, 5L));
    }
}
