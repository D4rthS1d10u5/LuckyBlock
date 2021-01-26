package com.LuckyBlock.command;

import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.*;

public class LBCVersion extends LBCommand {
    public LBCVersion() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(green + val("command.version", false) + " " + gold + "2.2.5");
        } else {
            Player player = (Player)sender;
            RawText r = new RawText(val("command.version", false) + ": ");
            r.color = ChatColor.GREEN;
            RawText r1 = new RawText("2.2.5");
            r1.color = ChatColor.GOLD;
            r1.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "2.2.5"));
            TellRawSender.sendTo(player, new RawText[]{r, r1});
        }

        return true;
    }

    public String[] getCommandNames() {
        return new String[]{"version", "v"};
    }

    public int[] getRequiredArgs() {
        return new int[]{1};
    }

    public String getDescription() {
        return "Deprecated";
    }

    public boolean isDeprecated() {
        return true;
    }
}