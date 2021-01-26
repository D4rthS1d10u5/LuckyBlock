package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

public class LBCLang extends LBCommand {
    public LBCLang() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        File file = new File(LuckyBlock.d() + "Data/messages/" + args[1] + ".yml");
        if (!file.exists()) {
            send(sender, "invalid_file");
            return false;
        } else {
            LuckyBlock.lang = args[1];
            if (LuckyBlock.reload_lang()) {
                send(sender, "command.setlang.success");
            } else {
                String a = "";
                List<String> l = IObjects.listM();

                for(int x = 0; x < l.size(); ++x) {
                    if (x == 0) {
                        a = (String)l.get(x);
                    } else {
                        a = a + ", " + (String)l.get(x);
                    }
                }

                LuckyBlock.instance.getLogger().info("Missing Strings: [" + a + "]");
                sender.sendMessage(ChatColor.RED + "Some strings are missing! (see the console)");
            }

            return true;
        }
    }

    public String getCommandName() {
        return "setlang";
    }

    public int[] getRequiredArgs() {
        return new int[]{2};
    }

    public String getDescription() {
        return val("desc.cmd.lang");
    }
}
