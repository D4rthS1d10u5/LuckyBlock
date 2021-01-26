package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.world.Engine.LuckyBlockWorld;
import com.mcgamer199.luckyblock.world.Engine.WorldOptions;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LBCWorld extends LBCommand {
    public LBCWorld() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        int id = ((LBType)LBType.getTypes().get(0)).getId();
        List<WorldOptions> options = new ArrayList();
        if (args.length == 2) {
            String[] d = args[1].toUpperCase().split("-");
            String[] var11 = d;
            int var10 = d.length;

            for(int var9 = 0; var9 < var10; ++var9) {
                String s = var11[var9];
                if (s.startsWith("ID:")) {
                    String[] a = s.split("ID:");
                    id = Integer.parseInt(a[1]);
                } else if (!s.equalsIgnoreCase("END") && !s.equalsIgnoreCase("NETHER")) {
                    boolean c = false;
                    WorldOptions[] var16;
                    int var15 = (var16 = WorldOptions.values()).length;

                    for(int var14 = 0; var14 < var15; ++var14) {
                        WorldOptions o = var16[var14];
                        if (o.name().equalsIgnoreCase(s)) {
                            options.add(WorldOptions.valueOf(s));
                            c = true;
                        }
                    }

                    if (!c) {
                        sender.sendMessage(red + "Invalid option!");
                        return true;
                    }
                }
            }

            WorldOptions w = WorldOptions.ID;
            w.setId(id);
            if (LBType.fromId(id) == null) {
                sender.sendMessage(red + "Invalid ID!");
                return true;
            }

            options.add(w);
        }

        if (sender instanceof Player) {
            World world = LuckyBlockWorld.getWorld(options);
            ((Player)sender).teleport(new Location(world, 0.0D, (double)world.getHighestBlockYAt(0, 0), 0.0D));
            return true;
        } else {
            send_invalid_sender(sender);
            return false;
        }
    }

    public String getCommandName() {
        return "world";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return "WIP";
    }
}
