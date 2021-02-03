package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    public void function(LuckyBlock luckyBlock, Player player) {
        if (luckyBlock.hasDropOption("Materials")) {
            final String[] materials = (String[]) luckyBlock.getDropOption("Materials").getValues();
            final Block block = luckyBlock.getBlock();
            Scheduler.timer(new BukkitRunnable() {
                private int times = RandomUtils.nextInt(5) + 6;

                @Override
                public void run() {
                    if (this.times > 0) {
                        String materialName = RandomUtils.getRandomObject(materials).toUpperCase();

                        String[] materialData = materialName.split(" ");
                        if (materialData.length == 2) {
                            byte data = Byte.parseByte(materialData[1]);
                            block.setType(Material.getMaterial(materialData[0]));
                            block.setData(data);
                        } else {
                            block.setType(Material.getMaterial(materialName));
                        }

                        --this.times;
                    } else {
                        cancel();
                    }
                }
            }, 5, 5);
        }
    }
}
