package com.mcgamer199.luckyblock.title;

import org.bukkit.entity.Player;

public interface ITitle {

    void sendTitle(Player var1, String var2, int var3, int var4, int var5);

    void sendSubtitle(Player var1, String var2, int var3, int var4, int var5);

    void sendActionBar(Player var1, String var2, int var3, int var4, int var5);
}
