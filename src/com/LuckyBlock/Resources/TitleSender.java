package com.LuckyBlock.Resources;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.logic.MyTasks;
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
