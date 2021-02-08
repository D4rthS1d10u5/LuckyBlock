package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class LBCLbs extends LBCommand {
    public LBCLbs() {
    }

    static void sendLB(CommandSender sender, LuckyBlock luckyBlock) {
        ChatComponent component = new ChatComponent();
        String luckyBlockTitle = "&cnull";
        if(luckyBlock.hasDropOption("Title")) {
            luckyBlockTitle = luckyBlock.getDropOption("Title").getValues()[0].toString();
        }

        component.addText(String.format("§6§lLB §9(%s§9)", val("command.lbs.data.hover", false)), Hover.show_text, String.format("§a%s\n§b%s: §d%s\n§b%s: §9%s\n§b%s: §6%s\n§b%s: %s",
                luckyBlock.blockToString(),
                val("command.lbs.data.drop", false), luckyBlockTitle,
                val("command.lbs.data.placedby", false), luckyBlock.getPlacedByClass(),
                val("command.lbs.data.luck", false), luckyBlock.getType().getLuckString(luckyBlock.getLuck()),
                val("command.lbs.data.type", false), luckyBlock.getType().getName()));
        component.send(sender);
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
            List<LuckyBlock> luckyBlocks = LuckyBlock.getStorage().values().stream().skip(page * 10).limit(10).collect(Collectors.toList());

            if (luckyBlocks.size() < 1) {
                send(sender, "command.lbs.no_lb");
                return false;
            } else {
                sender.sendMessage(white + val("command.lbs.page", false) + " " + page);
                sender.sendMessage("Total: " + LuckyBlock.getStorage().size());

                luckyBlocks.forEach(luckyBlock -> sendLB(sender, luckyBlock));

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
