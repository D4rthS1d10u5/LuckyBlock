package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LBCVersion extends LBCommand {
    public LBCVersion() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        ChatComponent component = new ChatComponent();
        component.addText(String.format("§a%s: %s", val("command.version", false), LuckyBlockPlugin.instance.getDescription().getVersion()), Hover.show_text, String.format("§e%s", LuckyBlockPlugin.instance.getDescription().getVersion()));
        component.send(sender);

        return true;
    }

    public String[] getCommandNames() {
        return new String[]{"version", "v"};
    }

    public int[] getRequiredArgs() {
        return new int[]{1};
    }

    public String getDescription() {
        return "Deprecated";
    }

    public boolean isDeprecated() {
        return true;
    }
}