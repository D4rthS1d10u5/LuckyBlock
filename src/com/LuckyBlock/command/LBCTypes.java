package com.LuckyBlock.command;

import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.EnumTextAction;
import org.core.tellraw.EnumTextEvent;
import org.core.tellraw.RawText;
import org.core.tellraw.TextAction;

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
                List<LBType> types = new ArrayList();

                int x;
                LBType t;
                for(x = (page - 1) * 5; x < page * 5; ++x) {
                    if (x < LBType.getTypes().size()) {
                        t = (LBType)LBType.getTypes().get(x);
                        types.add(t);
                    }
                }

                if (sender instanceof Player) {
                    Player player = (Player)sender;

                    for(int counter = 0; counter < types.size(); ++counter) {
                        LBType type = (LBType)types.get(counter);
                        short data = type.getData();
                        if (data < 0) {
                            boolean var16 = false;
                        }

                        RawText r = new RawText("" + red + type.getId() + green + ", " + reset + type.getName());
                        String i = type.getName();
                        if (type.disabled) {
                            i = i + "\n" + red + val("command.types.disabled", false);
                        }

                        r.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, i));
                        r.addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + lcmd + " lb " + player.getName() + " 1 0 " + type.getId()));
                        r.sendTo(new Player[]{player});
                    }

                    return true;
                } else {
                    for(x = 0; x < types.size(); ++x) {
                        t = (LBType)types.get(x);
                        sender.sendMessage("" + red + t.getId() + green + ", " + t.getName());
                    }

                    return true;
                }
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

