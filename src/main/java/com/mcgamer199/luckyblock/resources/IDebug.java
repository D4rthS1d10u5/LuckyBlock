package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class IDebug {
    public IDebug() {
    }

    public static void sendError(Player player, String[] msgs) {
        String errorMessage = "unknown";

        if(!ArrayUtils.isEmpty(msgs)) {
            StringBuilder builder = new StringBuilder();
            for (String msg : msgs) {
                builder.append(msg).append('\n');
            }
            errorMessage = builder.toString();
        }

        ChatComponent component = new ChatComponent();
        component.addText("§5§l[§e§lLBDebug§5§l]", Hover.show_text, "§eErrors detector\n");
        component.addText("§c An error has occurred! For more information hover §ehere.", Hover.show_text, "§9§l" + errorMessage);

        component.send(player);
    }

    public static void sendDebug(String name, DebugData... datas) {
        if (LuckyBlockPlugin.isDebugEnabled()) {
            ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
            c.sendMessage(ChatColor.RED + "[Lucky Block Debug]");
            Calendar cal = Calendar.getInstance();
            c.sendMessage(ChatColor.AQUA + "Time: " + ChatColor.YELLOW + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
            c.sendMessage(ChatColor.AQUA + "Event: " + ChatColor.YELLOW + name);

            for (DebugData data : datas) {
                c.sendMessage(ChatColor.AQUA + data.dataName + ": " + ChatColor.YELLOW + data.dataValue);
            }

            c.sendMessage(ChatColor.RED + "-------------------");
        }

    }
}
