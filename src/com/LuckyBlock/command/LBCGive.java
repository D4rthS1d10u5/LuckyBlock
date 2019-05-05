//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.LuckyBlock.command;

import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.LB.LBType.LBOption;
import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LBCGive extends LBCommand {
    public LBCGive() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player target = null;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                send_invalid_sender(sender);
                return false;
            }

            target = (Player)sender;
        }

        if (target == null) {
            target = Bukkit.getPlayer(args[1]);
        }

        if (target == null) {
            send_invalid_player(sender);
            return false;
        } else {
            int amount = 1;
            int id = LBType.getDefaultType().getId();
            int luck = 0;
            if (args.length > 2) {
                try {
                    amount = Math.min(Integer.parseInt(args[2]), 64);
                } catch (NumberFormatException var19) {
                    send_invalid_number(sender);
                    return false;
                }
            }

            if (amount < 1) {
                send_invalid_number(sender);
                return false;
            } else {
                if (args.length > 3 && !args[3].equalsIgnoreCase("random")) {
                    try {
                        luck = Integer.parseInt(args[3]);
                    } catch (NumberFormatException var18) {
                        send_invalid_number(sender);
                        return false;
                    }
                }

                if (args.length > 4) {
                    try {
                        id = Integer.parseInt(args[4]);
                    } catch (NumberFormatException var17) {
                        send_invalid_number(sender);
                        return false;
                    }
                }

                LBType type = LBType.fromId(id);
                if (type == null) {
                    sendMessage(sender, "lb.invalid_type", new ObjectType[0]);
                    return false;
                } else if (luck <= type.getMaxLuck() && luck >= type.getMinLuck()) {
                    if (type.disabled) {
                        sendMessage(sender, "lb.type_disabled", new ObjectType[0]);
                        return false;
                    } else {
                        LBOption[] options = new LBOption[16];
                        if (args.length > 5) {
                            String a = args[5];
                            String[] d = a.split(",");
                            int i = 0;

                            for(int x = 0; x < d.length; ++x) {
                                if (d[x] != null) {
                                    try {
                                        options[i] = LBOption.valueOf(d[x].toUpperCase());
                                        ++i;
                                    } catch (Exception var16) {
                                        send(sender, "command.givelb.invalid_options");
                                        return false;
                                    }
                                }
                            }
                        }

                        boolean b = false;
                        if (args.length < 4 || args[3].equalsIgnoreCase("random")) {
                            b = true;
                        }

                        ItemStack item = type.toItemStack(luck, options, (String)null, b, amount);
                        item.setAmount(amount);
                        target.getInventory().addItem(new ItemStack[]{item});
                        String a = val("command.givelb.success", false);
                        a = a.replace("%amount%", String.valueOf(amount));
                        if (!b) {
                            a = a.replace("%luck-color%", "" + type.getPercentColor(luck));
                            a = a.replace("%luck%", "" + luck);
                        } else {
                            a = a.replace("%luck-color%", "");
                            a = a.replace("%luck%", ChatColor.BLUE + "[Random]");
                            a = a.replace("%", "");
                        }

                        send_2(sender, a);
                        return true;
                    }
                } else {
                    sendMessage(sender, "command.givelb.invalid_luck", new ObjectType[0]);
                    return false;
                }
            }
        }
    }

    public String[] getCommandNames() {
        return new String[]{"give", "lb", "luckyblock"};
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2, 3, 4, 5, 6};
    }

    public String getDescription() {
        return val("desc.cmd.givelb");
    }
}
