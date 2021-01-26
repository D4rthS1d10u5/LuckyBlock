package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCVersion extends LBCommand {
    public LBCVersion() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(green + val("command.version", false) + " " + gold + "2.2.5");
        } else {
            Player player = (Player)sender;
            com.mcgamer199.luckyblock.tellraw.RawText r = new com.mcgamer199.luckyblock.tellraw.RawText(val("command.version", false) + ": ");
            r.color = ChatColor.GREEN;
            com.mcgamer199.luckyblock.tellraw.RawText r1 = new com.mcgamer199.luckyblock.tellraw.RawText("2.2.5");
            r1.color = ChatColor.GOLD;
            r1.addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + "2.2.5"));
            com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo(player, new com.mcgamer199.luckyblock.tellraw.RawText[]{r, r1});
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