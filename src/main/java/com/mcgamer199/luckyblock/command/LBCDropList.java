package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
            LuckyBlockDrop[] var10;
            int var9 = (var10 = LuckyBlockDrop.values()).length;

            int i;
            for (i = 0; i < var9; ++i) {
                LuckyBlockDrop drop = var10[i];
                if (drop.isVisible()) {
                    d.add(drop.name());
                }
            }

            for (CustomDrop c : CustomDropManager.getCustomDrops()) {
                d.add(c.getName());
            }

            send_no(sender, "command.droplist.page", " " + white + page);

            ChatComponent component = new ChatComponent();
            String missingDescription = val("command.droplist.no_desc", false);
            String listDescription = "§6" + val("command.droplist.desc", false);
            for (int x = (page - 1) * 10; x < page * 10; ++x) {
                if (x < d.size()) {
                    String dropName = d.get(x);
                    StringBuilder descriptionBuilder = new StringBuilder();
                    LuckyBlockDrop drop = LuckyBlockDrop.getByName(dropName);
                    if(drop != null) {
                        String optionDescription = IObjects.getString("desc.drop." + drop.name().toLowerCase(), false);
                        if(optionDescription.equalsIgnoreCase("null")) {
                            optionDescription = missingDescription;
                        }

                        descriptionBuilder.append(listDescription).append(":\n").append("§f").append(optionDescription);
                        StringJoiner joiner = new StringJoiner("\n");

                        if(!drop.getDropOptions().isEmpty()) {
                            int limit = 0;
                            for (String option : drop.getDropOptions().keys()) {
                                if(limit++ > 10) {
                                    break;
                                } else {
                                    joiner.add("§e" + option);
                                }
                            }
                        }

                        descriptionBuilder.append(String.format("\n\n§c%s:\n%s", val("command.droplist.drop_options", false), joiner.toString()));
                    }

                    CustomDrop customDrop = CustomDropManager.getByName(d.get(x));
                    if(customDrop != null) {
                        String optionDescription = customDrop.getDescription() != null ? customDrop.getDescription() : missingDescription;
                        descriptionBuilder.append("§cCUSTOM ").append(listDescription).append(":\n").append("§f").append(optionDescription);

                        StringJoiner joiner = new StringJoiner("\n");

                        DropOption[] options = customDrop.getDefaultOptions();
                        if(ArrayUtils.isNotEmpty(options)) {
                            int limit = 0;
                            for (DropOption option : options) {
                                if(limit++ > 10) {
                                    break;
                                } else {
                                    if(option != null) {
                                        joiner.add("§e" + option.getName());
                                    }
                                }
                            }
                        }

                        descriptionBuilder.append("§cCUSTOM ").append(String.format("\n\n§c%s:\n%s", val("command.droplist.drop_options", false), joiner.toString()));
                    }

                    component.addText(String.format("§b[§a%s§b]\n", dropName), Hover.show_text, descriptionBuilder.toString());
                }
            }

            component.send(sender);

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

