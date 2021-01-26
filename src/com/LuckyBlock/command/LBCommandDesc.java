package com.LuckyBlock.command;

import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LBCommandDesc extends LBCommand {
    public LBCommandDesc() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        String c = args[1];
        if (LBCommand.getByName(c) == null) {
            if (isLBCommand(c)) {
                send(sender, "command.commanddesc.no_description");
            } else {
                send(sender, "command.commanddesc.invalid_command");
            }

            return false;
        } else {
            LBCommand lbc = LBCommand.getByName(c);
            if (lbc.getDescription() == null) {
                send(sender, "command.commanddesc.no_description");
                return false;
            } else {
                String d = val("command.commanddesc.description", false);
                sender.sendMessage(gold + d + ": " + aqua + lbc.getDescription());
                return true;
            }
        }
    }

    public String getCommandName() {
        return "commanddesc";
    }

    public String getDescription() {
        return val("desc.cmd.commanddesc");
    }

    public int[] getRequiredArgs() {
        return new int[]{2};
    }
}
