package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.IObjects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class LBCResourcePack extends LBCommand {
    public LBCResourcePack() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (IObjects.getValue("rp_enabled").equals(true)) {
            String r = IObjects.getValue("rp_url").toString();
            if (r.equalsIgnoreCase("insert your resource pack url here")) {
                send(sender, "command.resourcepack.invalid_resourcepack");
                return false;
            }

            Player p;
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    send_invalid_sender(sender);
                    return false;
                }

                p = (Player) sender;
                p.setResourcePack(r);
                send(p, "command.resourcepack.success");
                return true;
            }

            if (args.length > 1) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("world")) {
                        if (!(sender instanceof Player)) {
                            send_invalid_sender(sender);
                        }

                        p = (Player) sender;
                        p.setResourcePack(r);
                        Iterator var8 = p.getWorld().getPlayers().iterator();

                        while (var8.hasNext()) {
                            Player player = (Player) var8.next();
                            player.setResourcePack(r);
                        }

                        send(p, "command.resourcepack.success");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("server")) {
                        Iterator var7 = Bukkit.getOnlinePlayers().iterator();

                        while (var7.hasNext()) {
                            p = (Player) var7.next();
                            p.setResourcePack(r);
                        }

                        send(sender, "command.resourcepack.success");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("player")) {
                        send_invalid_args(sender);
                    } else {
                        send(sender, "command.resourcepack.invalid_cmd");
                    }
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("player")) {
                        p = Bukkit.getPlayer(args[2]);
                        if (p == null) {
                            send_invalid_player(sender);
                            return false;
                        }

                        p.setResourcePack(r);
                        send(sender, "command.resourcepack.success");
                        return true;
                    }

                    send(sender, "command.resourcepack.invalid_cmd");
                }
            }
        } else {
            send(sender, "command.resourcepack.disabled");
        }

        return false;
    }

    public String getCommandName() {
        return "resourcepack";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2, 3};
    }

    public String getDescription() {
        return val("desc.cmd.resourcepack");
    }
}

