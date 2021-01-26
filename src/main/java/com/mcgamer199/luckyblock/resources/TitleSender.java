package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.entity.Player;

public class TitleSender {

    public TitleSender() {
    }

    public static void send(Player player, String msg) {
        LuckyBlock.getTitle().sendActionBar(player, msg, 20, 50, 20);
    }

    public static void send_1(Player player, String loc) {
        String msg = MyTasks.val(loc, true);
        LuckyBlock.getTitle().sendActionBar(player, msg, 20, 50, 20);
    }
}