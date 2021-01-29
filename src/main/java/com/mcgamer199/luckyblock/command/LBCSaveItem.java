package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class LBCSaveItem extends LBCommand {
    public static File fileF = new File(LuckyBlockPlugin.d() + "saved/items.yml");
    public static FileConfiguration file;

    static {
        file = YamlConfiguration.loadConfiguration(fileF);
    }

    public LBCSaveItem() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        String path = args[1];
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR) {
            file.set(path, item);

            try {
                file.save(fileF);
            } catch (IOException var9) {
                var9.printStackTrace();
            }
        } else {
            send(player, "invalid_item");
        }

        return false;
    }

    public String getCommandName() {
        return "saveItem";
    }

    public int[] getRequiredArgs() {
        return new int[]{2};
    }

    public String getDescription() {
        return "desc.cmd.saveitem";
    }

    public boolean requiresPlayer() {
        return true;
    }
}
