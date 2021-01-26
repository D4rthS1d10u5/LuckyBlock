package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.Set;

public class LBCSetDrop extends LBCommand {
    public LBCSetDrop() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            send_invalid_sender(sender);
            return true;
        } else {
            if (args.length > 1) {
                Player player = (Player)sender;
                if (player.getTargetBlock((Set)null, 100) != null && player.getTargetBlock((Set)null, 100).getType() != Material.AIR) {
                    Block block = player.getTargetBlock((Set)null, 100);
                    if (LB.isLuckyBlock(block)) {
                        LB lb = LB.getFromBlock(block);
                        boolean x = false;
                        if (!LBDrop.isValid(args[1].toUpperCase())) {
                            if (CustomDropManager.getByName(args[1]) == null) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            x = true;
                        } else if (!LBDrop.valueOf(args[1].toUpperCase()).isVisible()) {
                            send(sender, "lb.invalid_drop");
                            return false;
                        }

                        if (!x) {
                            lb.setDrop(LBDrop.valueOf(args[1].toUpperCase()), true, true);
                        } else {
                            CustomDrop d = CustomDropManager.getByName(args[1]);
                            if (!d.isEnabledByCommands()) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            lb.customDrop = d;
                            lb.refreshCustomDrop();
                            lb.save(true);
                        }

                        if (args.length == 3) {
                            String s = args[2];
                            if (!s.startsWith("{") || !s.endsWith("}")) {
                                send(sender, "lb.invalid_tag");
                                return false;
                            }

                            s = s.replace("{", "").replace("}", "").replace("'", "");
                            String[] c = s.split(";");

                            for(int v = 0; v < c.length; ++v) {
                                String[] u = c[v].split(":");
                                if (u.length == 2) {
                                    String[] g = u[1].split(",");
                                    String[] op = new String[64];

                                    for(int ii = 0; ii < g.length; ++ii) {
                                        String[] cc = g[ii].split("!");
                                        if (cc.length == 2) {
                                            try {
                                                int i = Integer.parseInt(cc[0]);
                                                int iii = Integer.parseInt(cc[1]);
                                                int t = (new Random()).nextInt(iii - i + 1) + i;
                                                op[ii] = String.valueOf(t);
                                            } catch (NumberFormatException var20) {
                                                op[ii] = g[ii];
                                            }
                                        } else {
                                            op[ii] = g[ii];
                                        }
                                    }

                                    if (lb.getDropOption(u[0]) != null) {
                                        lb.removeDropOptions(u[0]);
                                    }

                                    if (lb.hasDropOption(u[0])) {
                                        lb.removeDropOptions(u[0]);
                                    }

                                    lb.getDropOptions().add(new DropOption(u[0], op));
                                    lb.save(true);
                                }
                            }
                        }

                        send(sender, "command.setdrop.success");
                        return true;
                    }

                    send(sender, "lb.invalid_lb");
                } else {
                    send(sender, "invalid_block");
                }
            } else {
                send_invalid_args(sender);
            }

            return false;
        }
    }

    public String getCommandName() {
        return "setdrop";
    }

    public int[] getRequiredArgs() {
        return new int[]{2, 3};
    }

    public String getDescription() {
        return val("desc.cmd.setdrop");
    }
}
