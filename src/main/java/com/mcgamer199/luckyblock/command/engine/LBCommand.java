package com.mcgamer199.luckyblock.command.engine;

import com.mcgamer199.luckyblock.command.LBCOther;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.tellraw.EnumTextAction;
import com.mcgamer199.luckyblock.tellraw.EnumTextEvent;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LBCommand extends ILBCmd implements CommandExecutor {
    private final boolean strict;

    public LBCommand() {
        this.strict = LuckyBlockPlugin.instance.config.getBoolean("strict_commands");
    }

    public static LBCommand getByName(String name) {
        List<LBCommand> c = IObjects.getCommands();

        for (int x = 0; x < c.size(); ++x) {
            LBCommand l = c.get(x);
            if (l.getCommandNames() != null) {
                String[] var7;
                int var6 = (var7 = l.getCommandNames()).length;

                for (int var5 = 0; var5 < var6; ++var5) {
                    String s = var7[var5];
                    if (s.equalsIgnoreCase(name)) {
                        return l;
                    }
                }
            }

            if (l.getCommandName() != null && l.getCommandName().equalsIgnoreCase(name)) {
                return l;
            }
        }

        return null;
    }

    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && this.strict && !canRun(((Player) sender).getUniqueId())) {
            return false;
        } else if (args.length > 0 && args[0] != null && cmds1.contains(args[0]) && !this.canRun(sender, "lb", args)) {
            send(sender, "command.no_perm");
            return false;
        } else if (!sender.hasPermission(lcmd + ".commands")) {
            send(sender, "command.no_perm");
            return false;
        } else {
            if (args.length < 1) {
                sendMessage(sender, "invalid_command", new TextAction[]{new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "/" + lcmd + " help"), new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + lcmd + " help")});
            } else {
                String a = args[0];
                if (getByName(a) != null) {
                    this.send(getByName(a), sender, cmd, label, args);
                } else {
                    this.send(new LBCOther(), sender, cmd, label, args);
                }
            }

            return false;
        }
    }

    public final boolean send(LBCommand clss, CommandSender sender, Command cmd, String label, String[] args) {
        return this.pre_receive(clss, sender, cmd, label, args);
    }

    private boolean pre_receive(LBCommand clss, CommandSender sender, Command cmd, String label, String[] args) {
        if (clss.requiresPlayer() && !(sender instanceof Player)) {
            send(sender, "command.invalid_sender");
            return false;
        } else {
            if (clss.getRequiredArgs() != null) {
                int a = args.length;
                boolean f = false;

                for (int x = 0; x < clss.getRequiredArgs().length; ++x) {
                    if (clss.getRequiredArgs()[x] == a) {
                        f = true;
                    }
                }

                if (!f) {
                    send(sender, "command.invalid_args");
                    return false;
                }
            }

            return clss.receive(sender, cmd, label, args);
        }
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }

    public String getCommandName() {
        return null;
    }

    public String[] getCommandNames() {
        return null;
    }

    public int[] getRequiredArgs() {
        return null;
    }

    public boolean requiresPlayer() {
        return false;
    }

    public String getPermission() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public boolean isDeprecated() {
        return false;
    }

    public final String toString() {
        return "LBCommand:" + this.getCommandName();
    }
}
