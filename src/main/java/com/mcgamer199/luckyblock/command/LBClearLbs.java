package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LBClearLbs extends LBCommand {
    public LBClearLbs() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 2 && !args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
            send(sender, "command.tf");
            return false;
        } else if (LuckyBlock.luckyBlocks.size() <= 0) {
            send(sender, "command.clearlbs.no_lb");
            return false;
        } else {
            int amount = LuckyBlock.luckyBlocks.size();
            boolean a = false;
            if (args.length == 2 && args[1].equalsIgnoreCase("true")) {
                a = true;
            }

            for (byte x = 0; x < LuckyBlock.luckyBlocks.size(); x = 0) {
                LuckyBlock luckyBlock = LuckyBlock.luckyBlocks.get(x);
                luckyBlock.remove(a);
            }

            LuckyBlockAPI.lbs.set("LuckyBlocks", null);
            LuckyBlockAPI.saveLBFile();
            String b = val("command.clearlbs.success", false);
            b = b.replace("%total%", "" + amount);
            send_2(sender, b);
            return true;
        }
    }

    public String getCommandName() {
        return "clearlbs";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.clearlbs");
    }
}