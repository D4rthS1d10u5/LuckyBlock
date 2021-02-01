package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class LBCInfo extends LBCommand {
    public LBCInfo() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        PluginDescriptionFile description = LuckyBlockPlugin.instance.getDescription();

        ChatComponent component = new ChatComponent(String.format("§a%s %s\n", description.getName(), description.getVersion()));
        component.addText(String.format("§aCreated by §9§b%s", description.getAuthors()), Hover.show_text, "§eClick here to open website", Click.open_url, "https://dev.bukkit.org/profiles/MCGamer199/");
        component.send(sender);

        return true;
    }

    public String getCommandName() {
        return "info";
    }

    public int[] getRequiredArgs() {
        return new int[]{1};
    }

    public String getDescription() {
        return "information about plugin's version,author (Me) and url to my profile on bukkit site.";
    }
}
