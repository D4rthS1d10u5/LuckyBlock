package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class LBCSetDrop extends LBCommand {
    public LBCSetDrop() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            send_invalid_sender(sender);
            return true;
        } else {
            if (args.length > 1) {
                Player player = (Player) sender;
                if (player.getTargetBlock(null, 100) != null && player.getTargetBlock(null, 100).getType() != Material.AIR) {
                    Block block = player.getTargetBlock(null, 100);
                    if (LuckyBlock.isLuckyBlock(block)) {
                        LuckyBlock luckyBlock = LuckyBlock.getByBlock(block);
                        boolean x = false;
                        if (!LuckyBlockDrop.isValid(args[1].toUpperCase())) {
                            if (CustomDropManager.getByName(args[1]) == null) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            x = true;
                        } else if (!LuckyBlockDrop.valueOf(args[1].toUpperCase()).isVisible()) {
                            send(sender, "lb.invalid_drop");
                            return false;
                        }

                        if (!x) {
                            luckyBlock.setDrop(LuckyBlockDrop.valueOf(args[1].toUpperCase()), true, true);
                        } else {
                            CustomDrop d = CustomDropManager.getByName(args[1]);
                            if (!d.isEnabledByCommands()) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            luckyBlock.customDrop = d;
                            luckyBlock.refreshCustomDrop();
                            luckyBlock.save(true);
                        }

                        if (args.length == 3) {
                            String s = args[2];
                            if (!s.startsWith("{") || !s.endsWith("}")) {
                                send(sender, "lb.invalid_tag");
                                return false;
                            }

                            s = s.replace("{", "").replace("}", "").replace("'", "");
                            String[] c = s.split(";");

                            for (int v = 0; v < c.length; ++v) {
                                String[] u = c[v].split(":");
                                if (u.length == 2) {
                                    String[] g = u[1].split(",");
                                    String[] op = new String[64];

                                    for (int ii = 0; ii < g.length; ++ii) {
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

                                    if (luckyBlock.getDropOption(u[0]) != null) {
                                        luckyBlock.removeDropOptions(u[0]);
                                    }

                                    if (luckyBlock.hasDropOption(u[0])) {
                                        luckyBlock.removeDropOptions(u[0]);
                                    }

                                    luckyBlock.getOldOptions().add(new DropOption(u[0], op));
                                    luckyBlock.save(true);
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
