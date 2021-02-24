package com.mcgamer199.luckyblock.api.customdrop.impl;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class FakeTntDrop implements CustomDrop {

    public FakeTntDrop() {}

    public String getName() {
        return "FAKE_TNT";
    }

    public boolean isVisible() {
        return false;
    }

    public Properties getDropOptions() {
        return null;
    }

    public String getDescription() {
        return "Spawns primed tnt which does no damage";
    }

    public boolean isEnabledByCommands() {
        return false;
    }

    public void execute(LuckyBlock luckyBlock, Player player) {
        TNTPrimed tnt = (TNTPrimed) luckyBlock.getBlock().getWorld().spawnEntity(luckyBlock.getLocation().add(0.0D, 1.0D, 0.0D), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(35);
        tnt.setYield(0.0F);
    }
}
