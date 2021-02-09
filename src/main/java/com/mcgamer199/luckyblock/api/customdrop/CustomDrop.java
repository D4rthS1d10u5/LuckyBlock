package com.mcgamer199.luckyblock.api.customdrop;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface CustomDrop {

    String getName();

    boolean isVisible();

    @Nullable
    Properties getDropOptions();

    String getDescription();

    boolean isEnabledByCommands();

    void execute(LuckyBlock var1, Player var2);
}
