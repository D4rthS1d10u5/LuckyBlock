package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.listeners.BreakLuckyBlock;
import com.mcgamer199.luckyblock.listeners.LBGui;
import com.mcgamer199.luckyblock.tellraw.EnumTextAction;
import com.mcgamer199.luckyblock.tellraw.EnumTextEvent;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCOther extends LBCommand {
    public LBCOther() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args[0].equalsIgnoreCase("gui")) {
            if (sender instanceof Player) {
                if (args.length != 1) {
                    send_invalid_args(sender);
                    return false;
                }

                target = (Player) sender;
                LBGui.open(target);
                send(sender, "command.gui.success");
                return true;
            }

            send_invalid_sender(sender);
        } else {
            LuckyCraftingTable t;
            int x;
            boolean var10;
            int id;
            if (args[0].equalsIgnoreCase("lctextra")) {
                if (!sender.isOp()) {
                    return false;
                }

                if (sender instanceof Player && args.length == 2) {
                    var10 = false;

                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException var9) {
                        return false;
                    }

                    if (LuckyCraftingTable.getById(id) != null) {
                        t = LuckyCraftingTable.getById(id);
                        if (t.getExtraLuck() != 0) {
                            if (t.getExtraLuck() > 0) {
                                if (t.getStoredLuck() < t.getMaxLuck()) {
                                    x = t.getMaxLuck() - t.getStoredLuck();
                                    t.setStoredLuck(t.getStoredLuck() + x, false);
                                    t.setExtraLuck(t.getExtraLuck() - x);
                                    t.save(true);
                                    send(sender, "command.lctextra.success");
                                    return true;
                                }

                                send(sender, "command.lctextra.error1");
                            } else {
                                if (t.getStoredLuck() > t.getMaxLuck() * -1) {
                                    x = t.getMaxLuck() - t.getStoredLuck();
                                    t.setStoredLuck(t.getStoredLuck() - x, false);
                                    t.setExtraLuck(t.getExtraLuck() + x);
                                    t.save(true);
                                    send(sender, "command.lctextra.success");
                                    return true;
                                }

                                send(sender, "command.lctextra.error1");
                            }
                        } else {
                            send(sender, "command.lctextra.error2");
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("lctstop")) {
                if (!sender.isOp()) {
                    return false;
                }

                if (sender instanceof Player && args.length == 2) {
                    var10 = false;

                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException var8) {
                        return false;
                    }

                    if (LuckyCraftingTable.getById(id) != null) {
                        t = LuckyCraftingTable.getById(id);
                        if (t.isRunning()) {
                            t.stop();
                            send(sender, "command.lctstop.success");
                            return true;
                        }

                        send(sender, "command.lctstop.error");
                    }
                }
            } else if (args[0].equalsIgnoreCase("lct")) {
                if (sender instanceof Player && sender.isOp()) {
                    ((Player) sender).getInventory().addItem(ItemMaker.createItem(Material.NOTE_BLOCK, 1, 0, yellow + "Lucky Crafting Table"));
                    return true;
                }
            } else {
                if (args[0].equalsIgnoreCase("runall")) {
                    if (LuckyBlock.luckyBlocks.size() <= 0) {
                        send(sender, "command.runall.no_lb");
                        return false;
                    }

                    for (byte blocks = 0; blocks < LuckyBlock.luckyBlocks.size() && blocks < 65; blocks = 0) {
                        LuckyBlock luckyBlock = LuckyBlock.luckyBlocks.get(blocks);
                        BreakLuckyBlock.openLB(luckyBlock, null);
                    }

                    send(sender, "command.runall.success");
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    LuckyBlockPlugin.instance.reloadConfig();
                    LuckyBlockPlugin.instance.config = LuckyBlockPlugin.instance.getConfig();
                    send(sender, "command.reload.success");
                    return true;
                }

                if (args[0].equalsIgnoreCase("illuminati")) {
                    if (sender instanceof Player) {
                        target = (Player) sender;
                        target.damage(500.0D);

                        for (int count = 0; count < 100; ++count) {
                            target.sendMessage("");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("say")) {
                    if (args.length > 2) {
                        target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            String s = "";

                            for (x = 2; x < args.length; ++x) {
                                s = s + args[x];
                                if (args.length > x + 1) {
                                    s = s + " ";
                                }
                            }

                            target.chat(ChatColor.translateAlternateColorCodes('&', s));
                        }
                    }
                } else {
                    sendMessage(sender, "invalid_command", new TextAction[]{new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "/" + lcmd + " help"), new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + lcmd + " help")});
                }
            }
        }

        return false;
    }
}

