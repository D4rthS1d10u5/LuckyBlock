package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class FakeTntDrop implements CustomDrop {
    public FakeTntDrop() {
    }

    public String getName() {
        return "FAKE_TNT";
    }

    public boolean isVisible() {
        return false;
    }

    public DropOption[] getDefaultOptions() {
        return null;
    }

    public String getDescription() {
        return "Spawns primed tnt which does no damage";
    }

    public boolean isEnabledByCommands() {
        return false;
    }

    public void function(LB lb, Player player) {
        TNTPrimed tnt = (TNTPrimed)lb.getBlock().getWorld().spawnEntity(lb.getBlock().getLocation().add(0.0D, 1.0D, 0.0D), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(35);
        tnt.setYield(0.0F);
    }
}
