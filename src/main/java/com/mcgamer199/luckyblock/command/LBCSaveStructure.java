package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.tags.StructureSaver;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class LBCSaveStructure extends LBCommand {
    public LBCSaveStructure() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        int x1;
        int x2;
        int y1;
        int y2;
        int z1;
        int z2;
        int t;
        try {
            int i1 = Integer.parseInt(args[1]);
            int i2 = Integer.parseInt(args[2]);
            t = Integer.parseInt(args[3]);
            int i4 = Integer.parseInt(args[4]);
            int i5 = Integer.parseInt(args[5]);
            int i6 = Integer.parseInt(args[6]);
            if (i1 < i4) {
                x1 = i1;
                x2 = i4;
            } else {
                x1 = i4;
                x2 = i1;
            }

            if (i2 < i5) {
                y1 = i2;
                y2 = i5;
            } else {
                y1 = i5;
                y2 = i2;
            }

            if (t < i6) {
                z1 = t;
                z2 = i6;
            } else {
                z1 = i6;
                z2 = t;
            }
        } catch (Exception var20) {
            send_invalid_number(sender);
            return false;
        }

        boolean withAir = Boolean.parseBoolean(args[7]);
        String fileName = args[8];
        if (y1 > -1 && y2 < 256) {
            if (x2 - x1 + 1 <= 1000 && z2 - z1 + 1 <= 1000) {
                t = (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1);
                if (t > 9600) {
                    String a = val("command.savestructure.max_count_error", true);
                    a = a.replace("%count%", "" + t);
                    send_2(sender, a);
                    return false;
                } else {
                    File file = new File(LuckyBlock.d() + "saved/structures/" + fileName + ".yml");
                    if (file.exists()) {
                        send(sender, "command.savestructure.file_exists");
                        return false;
                    } else {
                        Location loc1 = new Location(player.getWorld(), x1, y1, z1);
                        Location loc2 = new Location(player.getWorld(), x2, y2, z2);
                        int total = StructureSaver.saveStructureFixed(file, loc1, loc2, withAir);
                        String a = val("command.savestructure.success", true);
                        a = a.replace("%count%", "" + total);
                        send_2(sender, a);
                        return true;
                    }
                }
            } else {
                send(sender, "command.savestructure.area_error");
                return false;
            }
        } else {
            send(sender, "command.savestructure.y_error");
            return false;
        }
    }

    public String getCommandName() {
        return "savestructure";
    }

    public int[] getRequiredArgs() {
        return new int[]{9};
    }

    public boolean requiresPlayer() {
        return true;
    }

    public String getDescription() {
        return val("desc.cmd.savestructure");
    }
}
