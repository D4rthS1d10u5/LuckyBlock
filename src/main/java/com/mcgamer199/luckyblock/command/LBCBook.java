package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.logic.LBBook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCBook extends LBCommand {
    public LBCBook() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                LBBook.giveBook((Player) sender);
                send(sender, "command.book.1");
            } else {
                send_invalid_sender(sender);
            }
        } else if (args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p != null) {
                LBBook.giveBook(p);
                String a = val("command.book.2", false);
                a = a.replace("%player%", p.getName());
                send_2(sender, a);
            } else {
                send_invalid_player(sender);
            }
        }

        return true;
    }

    public String getCommandName() {
        return "book";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.book");
    }
}
