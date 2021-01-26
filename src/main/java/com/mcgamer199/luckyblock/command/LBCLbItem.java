package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCLbItem extends LBCommand {
    public LBCLbItem() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player target = null;
        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                send_invalid_sender(sender);
                return false;
            }

            target = (Player)sender;
        } else {
            if (args.length != 3) {
                send_invalid_args(sender);
                return false;
            }

            if (Bukkit.getPlayer(args[2]) == null) {
                send_invalid_player(sender);
                return false;
            }

            target = Bukkit.getPlayer(args[2]);
        }

        if (LBItem.isValid(args[1])) {
            LBItem.valueOf(args[1].toUpperCase()).giveTo(target);
            send(sender, "command.lbitem.success");
            return true;
        } else {
            send(sender, "command.lbitem.invalid_item");
            return false;
        }
    }

    public String getCommandName() {
        return "lbitem";
    }

    public int[] getRequiredArgs() {
        return new int[]{2};
    }

    public String getDescription() {
        return val("desc.cmd.lbitem");
    }
}
