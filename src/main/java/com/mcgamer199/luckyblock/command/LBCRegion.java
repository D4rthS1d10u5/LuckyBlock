package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LBCRegion extends LBCommand {
    public LBCRegion() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            if (!LuckyBlockPlugin.isWorldEditValid()) {
                send(sender, "world_edit_not_found");
                return false;
            }

            Player player = (Player) sender;
            WorldEditPlugin w = LuckyBlockPlugin.instance.getWorldEdit();
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
                            if (!LuckyBlockDrop.isValid(args[2].toUpperCase())) {
                                if (CustomDropManager.getByName(args[2]) == null) {
                                    send(sender, "lb.invalid_drop");
                                    return false;
                                }

                                i = true;
                            } else if (!LuckyBlockDrop.valueOf(args[2].toUpperCase()).isVisible()) {
                                send(sender, "lb.invalid_drop");
                                return false;
                            }

                            a = val("command.region.progress", false);
                            a = a.replace("%total%", String.valueOf(total));
                            send_2(sender, a);
                            if (!i) {
                                LuckyBlockDrop drop = LuckyBlockDrop.valueOf(args[2].toUpperCase());
                                this.action2(s, player, drop, null);
                            } else {
                                CustomDrop d = CustomDropManager.getByName(args[2]);
                                if (!d.isEnabledByCommands()) {
                                    send(sender, "lb.invalid_drop");
                                    return false;
                                }

                                this.action2(s, player, null, d);
                            }
                        } else {
                            send_invalid_args(sender);
                        }
                    } else {
                        String action3;
                        int y;//TODO это что-то с чем-то -_-
                        if (args[1].equalsIgnoreCase("clear")) {
                            for (int minX = s.getMinimumPoint().getBlockX(); minX < s.getMaximumPoint().getBlockX() + 1; ++minX) {
                                for (minX = s.getMinimumPoint().getBlockY(); minX < s.getMaximumPoint().getBlockY() + 1; ++minX) {
                                    for (y = s.getMinimumPoint().getBlockZ(); y < s.getMaximumPoint().getBlockZ() + 1; ++y) {
                                        if (LuckyBlock.isLuckyBlock(s.getWorld().getBlockAt(minX, minX, y))) {
                                            LuckyBlock.getByBlock(s.getWorld().getBlockAt(minX, minX, y)).remove();
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

                            for (x = s.getMinimumPoint().getBlockX(); x < s.getMaximumPoint().getBlockX() + 1; ++x) {
                                for (y = s.getMinimumPoint().getBlockY(); y < s.getMaximumPoint().getBlockY() + 1; ++y) {
                                    for (int z = s.getMinimumPoint().getBlockZ(); z < s.getMaximumPoint().getBlockZ() + 1; ++z) {
                                        if (LuckyBlock.isLuckyBlock(s.getWorld().getBlockAt(x, y, z))) {
                                            LuckyBlock.getByBlock(s.getWorld().getBlockAt(x, y, z)).setOwner(uuid);
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
        Scheduler.timer(new BukkitRunnable() {
                int x = s.getMinimumPoint().getBlockX();
                int y = s.getMinimumPoint().getBlockY();
                int z = s.getMinimumPoint().getBlockZ();
                final int x1 = s.getMinimumPoint().getBlockX();
                final int z1 = s.getMinimumPoint().getBlockZ();
                final int x2 = s.getMaximumPoint().getBlockX();
                final int y2 = s.getMaximumPoint().getBlockY();
                final int z2 = s.getMaximumPoint().getBlockZ();
                int total = 0;
                boolean finish = false;

                @Override
                public void run() {
                    boolean changeX = true;
                    boolean changeZ = true;
                    if (!LuckyBlock.canSaveMore()) {
                        cancel();
                    } else {
                        if (!LuckyBlock.isLuckyBlock(s.getWorld().getBlockAt(this.x, this.y, this.z))) {
                            LuckyBlock.placeLB(s.getWorld().getBlockAt(this.x, this.y, this.z).getLocation(), type, null, null, null, 0, LBOption.NO_SOUNDS);
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
                            cancel();
                        }

                    }
            }
        }, 5, 6);
    }

    void action2(final Selection s, final Player player, final LuckyBlockDrop drop, final CustomDrop cd) {
        Scheduler.timer(new BukkitRunnable() {
            int x = s.getMinimumPoint().getBlockX();
            int y = s.getMinimumPoint().getBlockY();
            int z = s.getMinimumPoint().getBlockZ();
            final int x1 = s.getMinimumPoint().getBlockX();
            final int z1 = s.getMinimumPoint().getBlockZ();
            final int x2 = s.getMaximumPoint().getBlockX();
            final int y2 = s.getMaximumPoint().getBlockY();
            final int z2 = s.getMaximumPoint().getBlockZ();
            int total = 0;
            boolean finish = false;

            public void run() {
                boolean changeX = true;
                boolean changeZ = true;
                Block b = s.getWorld().getBlockAt(this.x, this.y, this.z);
                if (LuckyBlock.isLuckyBlock(b)) {
                    LuckyBlock luckyBlock = LuckyBlock.getByBlock(b);
                    if (cd != null) {
                        luckyBlock.customDrop = cd;
                        luckyBlock.refreshCustomDrop();
                        luckyBlock.changed();
                        ++this.total;
                    } else if (drop != null) {
                        luckyBlock.setDrop(drop, false, true);
                        luckyBlock.changed();
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
                        LuckyBlock.saveAll();
                        String a = LBCRegion.val("command.region.action2", false);
                        a = a.replace("%total%", String.valueOf(this.total));
                        LBCRegion.send_2(player, a);
                    } else {
                        LBCRegion.send(player, "command.region.no_changes");
                    }

                    cancel();
                }

            }
        }, 5, 3);
    }
}

