package com.LuckyBlock.command;

import com.LuckyBlock.Engine.IObjects;
import com.LuckyBlock.LB.DropOption;
import com.LuckyBlock.LB.LBDrop;
import com.LuckyBlock.command.engine.LBCommand;
import com.LuckyBlock.customdrop.CustomDrop;
import com.LuckyBlock.customdrop.CustomDropManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LBCDropList extends LBCommand {
    public LBCDropList() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException var18) {
                send_invalid_number(sender);
                return false;
            }

            if (page < 1 || page > 127) {
                send_invalid_number(sender);
                return false;
            }
        }

        if (args.length > 2) {
            send_invalid_args(sender);
            return false;
        } else {
            List<String> d = new ArrayList();
            LBDrop[] var10;
            int var9 = (var10 = LBDrop.values()).length;

            int i;
            for(i = 0; i < var9; ++i) {
                LBDrop drop = var10[i];
                if (drop.isVisible()) {
                    d.add(drop.name());
                }
            }

            Iterator var22 = CustomDropManager.getCustomDrops().iterator();

            while(var22.hasNext()) {
                CustomDrop c = (CustomDrop)var22.next();
                d.add(c.getName());
            }

            send_no(sender, "command.droplist.page", " " + white + page);
            if (sender instanceof Player) {
                RawText[] texts = new RawText[16];
                i = 0;
                String no_desc = val("command.droplist.no_desc", false);
                String desc_ = ChatColor.GOLD + val("command.droplist.desc", false);

                for(int x = (page - 1) * 10; x < page * 10; ++x) {
                    if (x < d.size()) {
                        texts[i] = new RawText(aqua + "[" + green + (String)d.get(x) + aqua + "]");
                        String text;
                        String desc;
                        int z;
                        String all;
                        if (LBDrop.isValid((String)d.get(x))) {
                            LBDrop dr = LBDrop.getByName((String)d.get(x));
                            DropOption[] s = dr.getDefaultOptions();
                            text = null;
                            String description = no_desc;
                            description = IObjects.getString("desc.drop." + dr.name().toLowerCase(), false);
                            if (!description.equalsIgnoreCase("null")) {
                                description = desc_ + ":\n" + white + description;
                            }

                            for(z = 0; z < s.length; ++z) {
                                if (s[z] != null && s[z].getName() != null && z < 11) {
                                    if (z == 0) {
                                        text = "\n" + yellow + s[z].getName();
                                    } else {
                                        text = text + "\n" + yellow + s[z].getName();
                                    }
                                }
                            }

                            all = description;
                            if (text != null) {
                                all = description + "\n\n" + red + val("command.droplist.drop_options", false) + ":" + white + text;
                            }

                            texts[i].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, all));
                        }

                        if (CustomDropManager.getByName((String)d.get(x)) != null) {
                            CustomDrop cd = CustomDropManager.getByName((String)d.get(x));
                            String text2blyat = null;
                            text2blyat = cd.getDescription();
                            DropOption[] dr = cd.getDefaultOptions();
                            desc = no_desc;
                            if (text2blyat != null) {
                                desc = desc_ + ":\n" + white + text2blyat;
                            }

                            if (dr != null) {
                                for(z = 0; z < dr.length; ++z) {
                                    if (dr[z] != null && dr[z].getName() != null && z < 11) {
                                        if (z == 0) {
                                            text2blyat = "\n" + yellow + dr[z].getName();
                                        } else {
                                            text2blyat = text2blyat + "\n" + yellow + dr[z].getName();
                                        }
                                    }
                                }
                            }

                            all = desc;
                            if (text2blyat != null) {
                                all = desc + "\n\n" + red + val("command.droplist.drop_options", false) + ":" + white + text2blyat;
                            }

                            texts[i].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, all));
                        }

                        ++i;
                    }
                }

                RawText[] var30 = texts;
                int var29 = texts.length;

                for(int var27 = 0; var27 < var29; ++var27) {
                    RawText r = var30[var27];
                    if (r != null) {
                        TellRawSender.sendTo((Player)sender, new RawText[]{r});
                    }
                }
            } else {
                for(int x = (page - 1) * 10; x < page * 10; ++x) {
                    if (x < d.size()) {
                        sender.sendMessage(aqua + "[" + green + (String)d.get(x) + aqua + "]");
                    }
                }
            }

            sender.sendMessage(aqua + "---------------------");
            return true;
        }
    }

    public String getCommandName() {
        return "droplist";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.droplist");
    }
}

