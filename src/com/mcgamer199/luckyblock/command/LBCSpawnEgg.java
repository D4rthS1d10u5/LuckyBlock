package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.customentity.boss.EntityLBBoss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.entity.CustomEntity;

public class LBCSpawnEgg extends LBCommand {
    public LBCSpawnEgg() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if (CustomEntity.isClassValid(args[1])) {
            String s = args[1];
            CustomEntity e = CustomEntity.getClassByName(s);
            if (e == null) {
                send(sender, "command.spawnegg.invalid_entity");
                return false;
            } else if (e instanceof EntityLBBoss && !player.getName().equalsIgnoreCase(pn)) {
                send(sender, "command.spawnegg.not_allowed");
                return false;
            } else {
                ItemStack item = e.getSpawnEgg();
                player.getInventory().addItem(new ItemStack[]{item});
                send(sender, "command.spawnegg.success");
                return true;
            }
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
