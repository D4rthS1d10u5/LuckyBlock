package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LBCLbs extends LBCommand {
    public LBCLbs() {
    }

    static void sendLB(CommandSender sender, LuckyBlock luckyBlock) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ChatColor color = ChatColor.GOLD;
            String dr = ChatColor.RED + "null";
            if (luckyBlock.hasDropOption("Title")) {
                dr = c(luckyBlock.getDropOption("Title").getValues()[0].toString());
            }

            com.mcgamer199.luckyblock.tellraw.RawText text = new com.mcgamer199.luckyblock.tellraw.RawText("" + color + bold + "LB " + ChatColor.BLUE + "(" + val("command.lbs.data.hover", false) + ")");
            text.addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, green + luckyBlock.blockToString() + "\n" + aqua + val("command.lbs.data.drop", false) + ": " + lightpurple + dr + "\n" + aqua + val("command.lbs.data.placedby", false) + ": " + blue + luckyBlock.getPlacedByClass() + "\n" + aqua + val("command.lbs.data.luck", false) + ": " + gold + luckyBlock.getType().getLuckString(luckyBlock.getLuck()) + "\n" + aqua + val("command.lbs.data.type", false) + ": " + luckyBlock.getType().getName()));
            com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo(player, text);
        } else {
            ChatColor color = ChatColor.GOLD;
            String dr = ChatColor.RED + "null";
            if (luckyBlock.hasDropOption("Title")) {
                dr = c(luckyBlock.getDropOption("Title").getValues()[0].toString());
            }

            sender.sendMessage("" + color + bold + "LB" + aqua + "," + green + luckyBlock.blockToString() + aqua + "," + val("command.lbs.data.drop", false) + ":" + lightpurple + dr + aqua + "," + val("command.lbs.data.placedby", false) + ":" + blue + luckyBlock.getPlacedByClass() + aqua + "," + val("command.lbs.data.luck", false) + ":" + luckyBlock.getType().getLuckString(luckyBlock.getLuck()) + aqua + "," + val("command.lbs.data.type", false) + ":" + luckyBlock.getType().getName());
        }

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
            for (x = (page - 1) * 10; x < page * 10; ++x) {
                if (x < LuckyBlock.luckyBlocks.size()) {
                    luckyBlocks.add(LuckyBlock.luckyBlocks.get(x));
                }
            }

            if (luckyBlocks.size() < 1) {
                send(sender, "command.lbs.no_lb");
                return false;
            } else {
                sender.sendMessage(white + val("command.lbs.page", false) + " " + page);
                sender.sendMessage("Total: " + LuckyBlock.luckyBlocks.size());

                for (x = 0; x < luckyBlocks.size(); ++x) {
                    LuckyBlock luckyBlock = luckyBlocks.get(x);
                    sendLB(sender, luckyBlock);
                }

                sender.sendMessage(white + "--------------");
                return true;
            }
        }
    }

    public String getCommandName() {
        return "lbs";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.lbs");
    }
}
