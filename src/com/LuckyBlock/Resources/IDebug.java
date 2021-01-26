package com.LuckyBlock.Resources;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.*;

import java.util.Calendar;

public class IDebug {
    public IDebug() {
    }

    public static void sendError(Player player, String[] msgs) {
        RawText text1 = new RawText("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "[" + ChatColor.YELLOW + ChatColor.BOLD + "LBDebug" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "]");
        text1.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.YELLOW + "Errors detector"));
        RawText text2 = new RawText(ChatColor.RED + " An error has occured! for more information hover ");
        RawText text3 = new RawText(ChatColor.YELLOW + "Here");
        text3.bold = true;
        String a = "";
        if (msgs != null && msgs.length > 0) {
            for(int x = 0; x < msgs.length; ++x) {
                if (x > 0) {
                    a = a + "\n";
                }

                a = a + msgs[x];
            }
        }

        if (a.equalsIgnoreCase("")) {
            a = "unknown";
        }

        a = ChatColor.stripColor(a);
        text3.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.BLUE + a));
        TellRawSender.sendTo(player, new RawText[]{text1, text2, text3});
    }

    public static void sendDebug(String name, DebugData... datas) {
        if (LuckyBlock.isDebugEnabled()) {
            ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
            c.sendMessage(ChatColor.RED + "[Lucky Block Debug]");
            Calendar cal = Calendar.getInstance();
            c.sendMessage(ChatColor.AQUA + "Time: " + ChatColor.YELLOW + cal.get(11) + ":" + cal.get(12) + ":" + cal.get(13));
            c.sendMessage(ChatColor.AQUA + "Event: " + ChatColor.YELLOW + name);
            DebugData[] var7 = datas;
            int var6 = datas.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                DebugData d = var7[var5];
                c.sendMessage(ChatColor.AQUA + d.dataName + ": " + ChatColor.YELLOW + d.dataValue);
            }

            c.sendMessage(ChatColor.RED + "-------------------");
        }

    }
}
