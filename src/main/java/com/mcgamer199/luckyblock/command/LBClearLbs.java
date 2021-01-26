package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LBClearLbs extends LBCommand {
    public LBClearLbs() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 2 && !args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
            send(sender, "command.tf");
            return false;
        } else if (LB.lbs.size() <= 0) {
            send(sender, "command.clearlbs.no_lb");
            return false;
        } else {
            int amount = LB.lbs.size();
            boolean a = false;
            if (args.length == 2 && args[1].equalsIgnoreCase("true")) {
                a = true;
            }

            for(byte x = 0; x < LB.lbs.size(); x = 0) {
                LB lb = (LB)LB.lbs.get(x);
                lb.remove(a);
            }

            LuckyBlockAPI.lbs.set("LuckyBlocks", (Object)null);
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