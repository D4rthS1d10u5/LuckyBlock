package com.mcgamer199.luckyblock.api.customdrop.impl;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RandomBlockDrop implements CustomDrop {

    private static final Properties dropOptions = new Properties().putStringArray("Materials", new String[]{"DIAMOND_BLOCK", "DIRT", "TNT", "WOOL", "WOOL 1", "WOOL 2", "WOOL 3", "WOOL 4", "WOOL 5", "WOOL 6", "WOOL 7", "GOLD_BLOCK"});

    public RandomBlockDrop() {}

    public String getName() {
        return "RANDOM_BLOCK";
    }

    public boolean isVisible() {
        return true;
    }

    public Properties getDropOptions() {
        return dropOptions;
    }

    public String getDescription() {
        return null;
    }

    public boolean isEnabledByCommands() {
        return true;
    }

    public void execute(LuckyBlock luckyBlock, Player player) {
        Block block = luckyBlock.getBlock();
        String[] materials = luckyBlock.getDropOptions().getStringArray("Materials");
        if(materials.length > 0) {
            Scheduler.create(() -> {
                String materialName = RandomUtils.getRandomObject(materials).toUpperCase();

                String[] materialData = materialName.split(" ");
                if (materialData.length == 2) {
                    byte data = Byte.parseByte(materialData[1]);
                    block.setType(Material.getMaterial(materialData[0]));
                    block.setData(data);
                } else {
                    block.setType(Material.getMaterial(materialName));
                }
            }).count(RandomUtils.nextInt(5) + 6).timer(5, 5);
        }
    }
}
