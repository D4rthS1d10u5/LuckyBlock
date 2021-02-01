package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LBType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class LBCTypes extends LBCommand {
    public LBCTypes() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (LBType.getTypes().size() == 0) {
            send(sender, "command.types.no_type");
            return false;
        } else {
            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var13) {
                    send_invalid_number(sender);
                    return false;
                }
            } else if (args.length > 2) {
                send_invalid_args(sender);
                return false;
            }

            if (page >= 1 && page <= 128) {
                sender.sendMessage(aqua + val("command.types.page", false) + " " + white + page);
                List<LBType> types = new ArrayList<>();

                int x;
                LBType t;
                for (x = (page - 1) * 5; x < page * 5; ++x) {
                    if (x < LBType.getTypes().size()) {
                        t = LBType.getTypes().get(x);
                        types.add(t);
                    }
                }

                for (LBType type : types) {
                    short data = type.getData();
                    if (data < 0) {
                        boolean var16 = false;
                    }

                    ChatComponent component = new ChatComponent();
                    String typeName = String.format("%s%s", type.getName(), type.disabled ? "\n§c" + val("command.types.disabled", false) : "");
                    component.addText(String.format("§c%s§a, §r%s", type.getId(), type.getName()), Hover.show_text, typeName, Click.run_command, String.format("/%s lb %s 1 0 %s", lcmd, sender.getName(), type.getId()));
                    component.send(sender);
                }

                return true;
            } else {
                send_invalid_number(sender);
                return false;
            }
        }
    }

    public String getCommandName() {
        return "types";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.types");
    }
}

