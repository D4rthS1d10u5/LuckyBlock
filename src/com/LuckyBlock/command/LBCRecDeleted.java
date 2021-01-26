package com.LuckyBlock.command;

import com.LuckyBlock.LB.LB;
import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class LBCRecDeleted extends LBCommand {
    public LBCRecDeleted() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        byte page = 1;
        if (args.length == 2) {
            try {
                page = Byte.parseByte(args[1]);
            } catch (NumberFormatException var9) {
                send_invalid_number(sender);
                return false;
            }
        } else if (args.length > 2) {
            send_invalid_args(sender);
            return false;
        }

        if (page < 1) {
            send_invalid_args(sender);
            return false;
        } else {
            List<LB> lbs = new ArrayList();

            int x;
            for(x = LB.lastDeleted.size() - 1 - 10 * (page - 1); x > LB.lastDeleted.size() - page * 10 - 1; --x) {
                if (x > -1) {
                    lbs.add((LB)LB.lastDeleted.get(x));
                }
            }

            if (lbs.size() < 1) {
                send(sender, "command.lbs.no_lb");
                return false;
            } else {
                sender.sendMessage(white + val("command.lbs.page", false) + " " + page);

                for(x = 0; x < lbs.size(); ++x) {
                    LB lb = (LB)lbs.get(x);
                    LBCLbs.sendLB(sender, lb);
                }

                sender.sendMessage(white + "--------------");
                return true;
            }
        }
    }

    public String getCommandName() {
        return "recDeleted";
    }

    public String getDescription() {
        return val("desc.cmd.recdeleted", false);
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }
}

