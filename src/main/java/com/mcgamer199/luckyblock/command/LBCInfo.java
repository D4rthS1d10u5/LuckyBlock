package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCInfo extends LBCommand {
    public LBCInfo() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            com.mcgamer199.luckyblock.tellraw.RawText text1 = new com.mcgamer199.luckyblock.tellraw.RawText(ChatColor.GREEN + "LuckyBlock" + " ");
            com.mcgamer199.luckyblock.tellraw.RawText text2 = new com.mcgamer199.luckyblock.tellraw.RawText(ChatColor.YELLOW + "2.2.5" + "\n");
            text2.addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, ChatColor.RED + "Internal version: " + ChatColor.YELLOW + 225));
            com.mcgamer199.luckyblock.tellraw.RawText text3 = new com.mcgamer199.luckyblock.tellraw.RawText(ChatColor.GREEN + "Created by ");
            com.mcgamer199.luckyblock.tellraw.RawText text4 = new com.mcgamer199.luckyblock.tellraw.RawText("" + ChatColor.BLUE + ChatColor.BOLD + "MCGamer199");
            text4.addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, ChatColor.YELLOW + "Click here to open website"));
            text4.addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.CLICK_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.OPEN_URL, "https://dev.bukkit.org/profiles/MCGamer199/"));
            com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo((Player) sender, text1, text2, text3, text4);
        } else {
            sender.sendMessage(ChatColor.GREEN + "LuckyBlock" + " " + ChatColor.YELLOW + "2.2.5" + ChatColor.GREEN + " Created by " + ChatColor.BLUE + ChatColor.BOLD + "MCGamer199");
        }

        return true;
    }

    public String getCommandName() {
        return "info";
    }

    public int[] getRequiredArgs() {
        return new int[]{1};
    }

    public String getDescription() {
        return "information about plugin's version,author (Me) and url to my profile on bukkit site.";
    }
}
