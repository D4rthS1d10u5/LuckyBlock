package com.LuckyBlock.command;

import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.*;

public class LBCInfo extends LBCommand {
    public LBCInfo() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            RawText text1 = new RawText(ChatColor.GREEN + "LuckyBlock" + " ");
            RawText text2 = new RawText(ChatColor.YELLOW + "2.2.5" + "\n");
            text2.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.RED + "Internal version: " + ChatColor.YELLOW + 225));
            RawText text3 = new RawText(ChatColor.GREEN + "Created by ");
            RawText text4 = new RawText("" + ChatColor.BLUE + ChatColor.BOLD + "MCGamer199");
            text4.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.YELLOW + "Click here to open website"));
            text4.addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.OPEN_URL, "https://dev.bukkit.org/profiles/MCGamer199/"));
            TellRawSender.sendTo((Player)sender, new RawText[]{text1, text2, text3, text4});
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
