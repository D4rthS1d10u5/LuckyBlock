package com.LuckyBlock.LB;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.core.logic.ITask;

public class LBLoops {
    public LBLoops() {
    }

    static void loop(final LB lb) {
        final ITask task = new ITask();
        LuckyBlock var10001 = LuckyBlock.instance;
        Runnable var10002 = new Runnable() {
            public void run() {
                if (lb.isValid()) {
                    if (lb.getBlock().getRelative(BlockFace.UP).getType() == Material.FIRE) {
                        if (lb.getType().hasProperty(LBType.BlockProperty.EXPLODE_ON_FIRE)) {
                            lb.explode();
                        } else if (lb.getType().hasProperty(LBType.BlockProperty.REMOVE_ON_FIRE)) {
                            lb.remove();
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
