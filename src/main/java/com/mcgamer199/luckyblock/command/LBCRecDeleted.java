package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
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
            List<LuckyBlock> luckyBlocks = new ArrayList();

            int x;
            for (x = LuckyBlock.lastDeleted.size() - 1 - 10 * (page - 1); x > LuckyBlock.lastDeleted.size() - page * 10 - 1; --x) {
                if (x > -1) {
                    luckyBlocks.add(LuckyBlock.lastDeleted.get(x));
                }
            }

            if (luckyBlocks.size() < 1) {
                send(sender, "command.lbs.no_lb");
                return false;
            } else {
                sender.sendMessage(white + val("command.lbs.page", false) + " " + page);

                for (x = 0; x < luckyBlocks.size(); ++x) {
                    LuckyBlock luckyBlock = luckyBlocks.get(x);
                    LBCLbs.sendLB(sender, luckyBlock);
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

