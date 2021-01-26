package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.event.lb.block.PlaceLuckyBlock.LBOption;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.logic.ITask;

import java.util.UUID;

public class LBCRegion extends LBCommand {
    public LBCRegion() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            if (!LuckyBlock.isWorldEditValid()) {
                send(sender, "world_edit_not_found");
                return false;
            }

            Player player = (Player)sender;
            WorldEditPlugin w = LuckyBlock.instance.getWorldEdit();
            Selection s = w.getSelection(player);
            if (s != null) {
                if (args.length > 1) {
                    int total = s.getArea();
                    String a;
                    int x;
                    if (args[1].equalsIgnoreCase("setlb")) {
                        if (args.length < 4) {
                            LBType type = LBType.getDefaultType();
                            if (args.length == 3) {
                                try {
                                    x = Integer.parseInt(args[2]);
                                    if (LBType.fromId(x) == null) {
                                        send(player, "lb.invalid_type");
                                        return false;
                                    }

                                    type = LBType.fromId(x);
                                } catch (NumberFormatException var13) {
                                    send_invalid_number(player);
                                    return false;
                                }
                            }

                            if (s.getArea() > 1048) {
                                send(sender, "command.region.action1.error");
                                return false;
                            }

                            a = val("command.region.progress", false);
                            a = a.replace("%total%", String.valueOf(total));
                            send_2(sender, a);
                            this.action1(s, player, type);
                            return true;
                        }

                        send_invalid_args(player);
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("setdrop")) {
                        if (args.length == 3) {
                            boolean i = false;
                            if (!LBDrop.isValid(args[2].toUpperCase())) {
                                if (CustomDropManager.getByName(args[2]) == null) {
                                    send(sender, "lb.invalid_drop");
                                    return false;
                                }

                                i = true;
                            } else if (!LBDrop.valueOf(args[2].toUpperCase()).isVisible()) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            a = val("command.region.progress", false);
                            a = a.replace("%total%", String.valueOf(total));
                            send_2(sender, a);
                            if (!i) {
                                LBDrop drop = LBDrop.valueOf(args[2].toUpperCase());
                                this.action2(s, player, drop, (CustomDrop)null);
                            } else {
                                CustomDrop d = CustomDropManager.getByName(args[2]);
                                if (!d.isEnabledByCommands()) {
                                    send(sender, "lb.invalid_drop");
                                    return false;
                                }

                                this.action2(s, player, (LBDrop)null, d);
                            }
                        } else {
                            send_invalid_args(sender);
                        }
                    } else {
                        String action3;
                        int y;//TODO это что-то с чем-то -_-
                        if (args[1].equalsIgnoreCase("clear")) {
                            for(int minX = s.getMinimumPoint().getBlockX(); minX < s.getMaximumPoint().getBlockX() + 1; ++minX) {
                                for(minX = s.getMinimumPoint().getBlockY(); minX < s.getMaximumPoint().getBlockY() + 1; ++minX) {
                                    for(y = s.getMinimumPoint().getBlockZ(); y < s.getMaximumPoint().getBlockZ() + 1; ++y) {
                                        if (LB.isLuckyBlock(s.getWorld().getBlockAt(minX, minX, y))) {
                                            LB.getFromBlock(s.getWorld().getBlockAt(minX, minX, y)).remove();
                                            ++total;
                                        }
                                    }
                                }
                            }

                            action3 = val("command.region.action3", false);
                            action3 = action3.replace("%total%", String.valueOf(total));
                            send_2(sender, action3);
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("setowner")) {
                            if (args.length != 3) {
                                send_invalid_args(sender);
                                return false;
                            }

                            UUID uuid = null;

                            try {
                                uuid = UUID.fromString(args[2]);
                            } catch (Exception var14) {
                                send(sender, "invalid_uid");
                                return false;
                            }

                            for(x = s.getMinimumPoint().getBlockX(); x < s.getMaximumPoint().getBlockX() + 1; ++x) {
                                for(y = s.getMinimumPoint().getBlockY(); y < s.getMaximumPoint().getBlockY() + 1; ++y) {
                                    for(int z = s.getMinimumPoint().getBlockZ(); z < s.getMaximumPoint().getBlockZ() + 1; ++z) {
                                        if (LB.isLuckyBlock(s.getWorld().getBlockAt(x, y, z))) {
                                            LB.getFromBlock(s.getWorld().getBlockAt(x, y, z)).setOwner(uuid);
                                            ++total;
                                        }
                                    }
                                }
                            }

                            action3 = val("command.region.action4", false);
                            action3 = action3.replace("%total%", String.valueOf(total));
                            send_2(sender, action3);
                            return true;
                        }

                        send(sender, "command.region.invalid_action");
                    }
                } else {
                    send_invalid_args(sender);
                }
            } else {
                send(sender, "command.region.no_selection");
            }
        } else {
            send_invalid_args(sender);
        }

        return false;
    }

    public String getCommandName() {
        return "region";
    }

    public boolean requiresPlayer() {
        return true;
    }

    public String getDescription() {
        return val("desc.cmd.region");
    }

    void action1(final Selection s, final Player player, final LBType type) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            int x = s.getMinimumPoint().getBlockX();
            int y = s.getMinimumPoint().getBlockY();
            int z = s.getMinimumPoint().getBlockZ();
            int x1 = s.getMinimumPoint().getBlockX();
            int z1 = s.getMinimumPoint().getBlockZ();
            int x2 = s.getMaximumPoint().getBlockX();
            int y2 = s.getMaximumPoint().getBlockY();
            int z2 = s.getMaximumPoint().getBlockZ();
            int total = 0;
            boolean finish = false;

            public void run() {
                boolean changeX = true;
                boolean changeZ = true;
                if (!LB.canSaveMore()) {
                    task.run();
                } else {
                    if (!LB.isLuckyBlock(s.getWorld().getBlockAt(this.x, this.y, this.z))) {
                        LB.placeLB(s.getWorld().getBlockAt(this.x, this.y, this.z).getLocation(), type, (ItemStack)null, (Object)null, (String)null, 0, new LBOption[]{LBOption.NO_SOUNDS});
                        ++this.total;
                    }

                    if (this.y == this.y2 && this.x == this.x2 && this.z == this.z2) {
                        this.finish = true;
                    }

                    if (this.z == this.z2 && this.x == this.x2) {
                        ++this.y;
                        this.z = this.z1;
                        changeZ = false;
                    }

                    if (this.x == this.x2) {
                        if (changeZ) {
                            ++this.z;
                        }

                        this.x = this.x1;
                        changeX = false;
                    }

                    if (changeX) {
                        ++this.x;
                    }

                    if (this.finish) {
                        String a = LBCRegion.val("command.region.action1.success", false);
                        a = a.replace("%total%", String.valueOf(this.total));
                        LBCRegion.send_2(player, a);
                        task.run();
                    }

                }
            }
        }, 5L, 6L));
    }

    void action2(final Selection s, final Player player, final LBDrop drop, final CustomDrop cd) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            int x = s.getMinimumPoint().getBlockX();
            int y = s.getMinimumPoint().getBlockY();
            int z = s.getMinimumPoint().getBlockZ();
            int x1 = s.getMinimumPoint().getBlockX();
            int z1 = s.getMinimumPoint().getBlockZ();
            int x2 = s.getMaximumPoint().getBlockX();
            int y2 = s.getMaximumPoint().getBlockY();
            int z2 = s.getMaximumPoint().getBlockZ();
            int total = 0;
            boolean finish = false;

            public void run() {
                boolean changeX = true;
                boolean changeZ = true;
                Block b = s.getWorld().getBlockAt(this.x, this.y, this.z);
                if (LB.isLuckyBlock(b)) {
                    LB lb = LB.getFromBlock(b);
                    if (cd != null) {
                        lb.customDrop = cd;
                        lb.refreshCustomDrop();
                        lb.changed();
                        ++this.total;
                    } else if (drop != null) {
                        lb.setDrop(drop, false, true);
                        lb.changed();
                        ++this.total;
                    }
                }

                if (this.y == this.y2 && this.x == this.x2 && this.z == this.z2) {
                    this.finish = true;
                }

                if (this.z == this.z2 && this.x == this.x2) {
                    ++this.y;
                    this.z = this.z1;
                    changeZ = false;
                }

                if (this.x == this.x2) {
                    if (changeZ) {
                        ++this.z;
                    }

                    this.x = this.x1;
                    changeX = false;
                }

                if (changeX) {
                    ++this.x;
                }

                if (this.finish) {
                    if (this.total > 0) {
                        LB.saveAll();
                        String a = LBCRegion.val("command.region.action2", false);
                        a = a.replace("%total%", String.valueOf(this.total));
                        LBCRegion.send_2(player, a);
                    } else {
                        LBCRegion.send(player, "command.region.no_changes");
                    }

                    task.run();
                }

            }
        }, 5L, 3L));
    }
}

