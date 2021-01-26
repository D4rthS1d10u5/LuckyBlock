package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import org.bukkit.entity.Player;

public interface CustomDrop {
    String getName();

    boolean isVisible();

    DropOption[] getDefaultOptions();

    String getDescription();

    boolean isEnabledByCommands();

    void function(LB var1, Player var2);
}
