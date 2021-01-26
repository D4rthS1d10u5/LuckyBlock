package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Random;

public class RandomBlockDrop implements CustomDrop {

    public RandomBlockDrop() {
    }

    public String getName() {
        return "RANDOM_BLOCK";
    }

    public boolean isVisible() {
        return true;
    }

    public DropOption[] getDefaultOptions() {
        return new DropOption[]{new DropOption("Materials", new String[]{"DIAMOND_BLOCK", "DIRT", "TNT", "WOOL", "WOOL 1", "WOOL 2", "WOOL 3", "WOOL 4", "WOOL 5", "WOOL 6", "WOOL 7", "GOLD_BLOCK"})};
    }

    public String getDescription() {
        return null;
    }

    public boolean isEnabledByCommands() {
        return true;
    }

    public void function(LB lb, Player player) {
        if (lb.hasDropOption("Materials")) {
            final String[] mats = (String[]) lb.getDropOption("Materials").getValues();
            final ITask task = new ITask();
            final Block b = lb.getBlock();
            final Random random = new Random();
            task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
                int x = random.nextInt(5) + 6;

                public void run() {
                    if (this.x > 0) {
                        String s = mats[random.nextInt(mats.length)].toUpperCase();
                        String[] d = s.split(" ");
                        if (d.length == 2) {
                            byte data = Byte.parseByte(d[1]);
                            b.setType(Material.getMaterial(d[0]));
                            b.setData(data);
                        } else {
                            b.setType(Material.getMaterial(s));
                        }

                        --this.x;
                    } else {
                        task.run();
                    }

                }
            }, 5L, 5L));
        }

    }
}
