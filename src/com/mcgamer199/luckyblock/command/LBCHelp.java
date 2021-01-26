package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LBCHelp extends LBCommand {
    public LBCHelp() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            this.send1(sender, lcmd, 1);
        } else if (args.length == 2) {
            boolean var5 = false;

            byte page;
            try {
                page = Byte.parseByte(args[1]);
            } catch (NumberFormatException var7) {
                send_invalid_number(sender);
                return false;
            }

            if (page < 1) {
                send_invalid_number(sender);
                return false;
            }

            this.send1(sender, lcmd, page);
            return true;
        }

        return false;
    }

    public String getCommandName() {
        return "help";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.help");
    }
}
