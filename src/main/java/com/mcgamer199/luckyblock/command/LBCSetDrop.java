package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LBCSetDrop extends LBCommand {
    public LBCSetDrop() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            send_invalid_sender(sender);
            return true;
        } else {
            if (args.length > 1) {
                Player player = (Player) sender;
                if (player.getTargetBlock(null, 100) != null && player.getTargetBlock(null, 100).getType() != Material.AIR) {
                    Block block = player.getTargetBlock(null, 100);
                    if (LuckyBlock.isLuckyBlock(block)) {
                        LuckyBlock luckyBlock = LuckyBlock.getByBlock(block);
                        boolean x = false;
                        if (!LuckyBlockDrop.isValid(args[1].toUpperCase())) {
                            if (CustomDropManager.getByName(args[1]) == null) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            x = true;
                        } else if (!LuckyBlockDrop.valueOf(args[1].toUpperCase()).isVisible()) {
                            send(sender, "lb.invalid_drop");
                            return false;
                        }

                        if (!x) {
                            luckyBlock.setDrop(LuckyBlockDrop.valueOf(args[1].toUpperCase()), true, true);
                        } else {
                            CustomDrop d = CustomDropManager.getByName(args[1]);
                            if (!d.isEnabledByCommands()) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            luckyBlock.customDrop = d;
                            luckyBlock.refreshCustomDrop();
                            luckyBlock.save();
                        }

                        if (args.length == 3) {
                            String options = args[2];
                            if (!options.startsWith("{") || !options.endsWith("}")) {
                                send(sender, "lb.invalid_tag");
                                return false;
                            }

                            Properties properties = new Properties(options);
                            luckyBlock.getDropOptions().merge(properties, true);
                        }

                        send(sender, "command.setdrop.success");
                        return true;
                    }

                    send(sender, "lb.invalid_lb");
                } else {
                    send(sender, "invalid_block");
                }
            } else {
                send_invalid_args(sender);
            }

            return false;
        }
    }

    public String getCommandName() {
        return "setdrop";
    }

    public int[] getRequiredArgs() {
        return new int[]{2, 3};
    }

    public String getDescription() {
        return val("desc.cmd.setdrop");
    }
}
