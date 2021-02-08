package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.MapEndCastle;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.structures.Structure;
import com.mcgamer199.luckyblock.util.TemporaryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;

public class LBCGenerate extends LBCommand {
    public LBCGenerate() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            send(sender, "command.generate.specify_structure");
            return false;
        } else {
            String s = args[1];
            Player player = (Player) sender;
            boolean b = false;
            if (Structure.isStructure(s)) {
                TemporaryUtils.b(s, player.getLocation());
            } else {
                File file = new File(LuckyBlockPlugin.d() + "Drops/" + s);
                if (!file.exists()) {
                    send(sender, "command.generate.invalid_structure");
                    return false;
                }

                Schematic.loadArea(file, player.getLocation());
                b = true;
            }

            String a = val("command.generate.success", true);
            String[] d1;
            if (b) {
                d1 = s.replace(".", "!").split("!");
                String[] d = d1[0].split("/");
                a = a.replace("%structure%", d[d.length - 1]);
            } else {
                d1 = s.replace(".", "_").split("_");
                a = a.replace("%structure%", d1[d1.length - 1]);
            }

            send_2(sender, a);
            return true;
        }
    }

    void a(Player p) {
        ItemStack[] is = p.getInventory().getContents();
        ItemStack[] var6 = is;
        int var5 = is.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            ItemStack i = var6[var4];
            if (i != null && i.getType() == Material.MAP && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equalsIgnoreCase("Test")) {
                short d = i.getDurability();
                MapView map = Bukkit.getServer().getMap(d);

                for (MapRenderer r : map.getRenderers()) {
                    map.removeRenderer(r);
                }

                map.addRenderer(new MapEndCastle());
            }
        }

    }

    public String getCommandName() {
        return "generate";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public boolean requiresPlayer() {
        return true;
    }

    public String getDescription() {
        return val("desc.cmd.generate");
    }
}

