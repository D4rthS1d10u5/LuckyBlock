package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.api.item.ItemMaker;

import java.util.Arrays;

public class LBCDetector extends LBCommand {
    public LBCDetector() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        ItemStack item;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                send_invalid_sender(sender);
                return true;
            }

            target = (Player)sender;
            item = LBItem.DETECTOR.getItem();
            target.getInventory().addItem(new ItemStack[]{item});
            send(sender, "command.detector.success");
        } else if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                send_invalid_player(sender);
                return true;
            }

            item = ItemMaker.createItem(Material.PISTON_BASE, 1, (short)0, "" + blue + bold + "Detector", Arrays.asList(gray + "Place it"));
            target.getInventory().addItem(new ItemStack[]{item});
            send(sender, "command.detector.success");
            return true;
        }

        return false;
    }

    public String getCommandName() {
        return "detector";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.detector");
    }
}
