package com.LuckyBlock.customdrop;

import com.LuckyBlock.LB.DropOption;
import com.LuckyBlock.LB.LB;
import org.bukkit.entity.Player;

public interface CustomDrop {
    String getName();

    boolean isVisible();

    DropOption[] getDefaultOptions();

    String getDescription();

    boolean isEnabledByCommands();

    void function(LB var1, Player var2);
}
