package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCSpawnEgg extends LBCommand {

    public LBCSpawnEgg() {}

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        CustomEntity customEntity = CustomEntityManager.createCustomEntity(args[1]);
        if(customEntity != null) {
            player.getInventory().addItem(CustomEntityManager.createSpawnEgg(customEntity));
            send(sender, "command.spawnegg.success");
            return true;
        } else {
            send(sender, "command.spawnegg.invalid_entity");
            return false;
        }
    }

    public String getCommandName() {
        return "spawnegg";
    }

    public boolean requiresPlayer() {
        return true;
    }

    public int[] getRequiredArgs() {
        return new int[]{2};
    }

    public String getDescription() {
        return val("desc.cmd.spawnegg");
    }
}
