package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.structures.Structure;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class LBCGenerate extends LBCommand {

    public LBCGenerate() {}

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            send(sender, "command.generate.specify_structure");
            return false;
        } else {
            String s = args[1];
            Player player = (Player) sender;
            boolean b = false;
            if (!Structure.buildStructure(s, player.getLocation())) {
                File file = new File(LuckyBlockPlugin.d() + "Drops/" + s);
                if (!file.exists()) {
                    send(sender, "command.generate.invalid_structure");
                    return false;
                }

                Schematic.loadArea(file, player.getLocation());
                b = true;
            }

            String a = val("command.generate.success", true);
            String[] d1;
            if (b) {
                d1 = s.replace(".", "!").split("!");
                String[] d = d1[0].split("/");
                a = a.replace("%structure%", d[d.length - 1]);
            } else {
                d1 = s.replace(".", "_").split("_");
                a = a.replace("%structure%", d1[d1.length - 1]);
            }

            send_2(sender, a);
            return true;
        }
    }

    public String getCommandName() {
        return "generate";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public boolean requiresPlayer() {
        return true;
    }

    public String getDescription() {
        return val("desc.cmd.generate");
    }
}

