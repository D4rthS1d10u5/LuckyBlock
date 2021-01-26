package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LBCPlaceLB extends LBCommand {
    public LBCPlaceLB() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Location loc = null;
        int id = LBType.getDefaultType().getId();
        Object b = null;
        if (sender instanceof Player) {
            Player player = (Player)sender;
            loc = player.getLocation();
            b = player;
        } else if (sender instanceof ProxiedCommandSender) {
            ProxiedCommandSender p = (ProxiedCommandSender)sender;
            if (p.getCallee() instanceof Entity) {
                Entity entity = (Entity)p.getCallee();
                loc = entity.getLocation();
                b = entity;
            }
        } else {
            if (!(sender instanceof BlockCommandSender)) {
                send_invalid_sender(sender);
                return false;
            }

            BlockCommandSender bcs = (BlockCommandSender)sender;
            loc = bcs.getBlock().getLocation();
            b = bcs.getBlock();
        }

        if (args.length > 3) {
            int x;
            int y;
            int z;
            try {
                String a1 = args[1];
                String a2 = args[2];
                String a3 = args[3];
                x = loc.getBlockX();
                y = loc.getBlockY();
                z = loc.getBlockZ();
                String[] d;
                if (a1.startsWith("~")) {
                    d = a1.split("~");
                    if (d.length == 2) {
                        x += Integer.parseInt(d[1]);
                    }
                }

                if (a2.startsWith("~")) {
                    d = a2.split("~");
                    if (d.length == 2) {
                        y += Integer.parseInt(d[1]);
                    }
                }

                if (a3.startsWith("~")) {
                    d = a3.split("~");
                    if (d.length == 2) {
                        z += Integer.parseInt(d[1]);
                    }
                }

                if (args.length > 4) {
                    try {
                        id = Integer.parseInt(args[4]);
                    } catch (NumberFormatException var15) {
                        send_invalid_number(sender);
                    }
                }
            } catch (Exception var16) {
                sendMessage(sender, "invalid_number", new ObjectType[0]);
                return false;
            }

            if (loc != null) {
                Location l = new Location(loc.getWorld(), (double)x, (double)y, (double)z);
                LBType type = LBType.fromId(id);
                if (type == null) {
                    send(sender, "lb.invalid_type");
                    return false;
                }

                if (type.disabled) {
                    send(sender, "lb.type_disabled");
                    return false;
                }

                if (LB.getFromBlock(l.getBlock()) == null) {
                    LB.placeLB(l, type, (ItemStack)null, b);
                    sendMessage(sender, "command.placelb.success", new ObjectType[0]);
                    return true;
                }

                sendMessage(sender, "command.placelb.error", new ObjectType[0]);
            }

            return false;
        } else {
            send_invalid_args(sender);
            return false;
        }
    }

    public String getCommandName() {
        return "placelb";
    }

    public int[] getRequiredArgs() {
        return new int[]{4, 5};
    }

    public String getDescription() {
        return val("desc.cmd.placelb");
    }
}
