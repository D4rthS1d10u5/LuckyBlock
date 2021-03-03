package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.enums.LBOption;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LBCRegion extends LBCommand {
    public LBCRegion() {}

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
                        int y;
                        if (args[1].equalsIgnoreCase("clear")) {
                            for (int minX = s.getMinimumPoint().getBlockX(); minX < s.getMaximumPoint().getBlockX() + 1; ++minX) {
                                for (minX = s.getMinimumPoint().getBlockY(); minX < s.getMaximumPoint().getBlockY() + 1; ++minX) {
                                    for (y = s.getMinimumPoint().getBlockZ(); y < s.getMaximumPoint().getBlockZ() + 1; ++y) {
                                        if (LuckyBlock.isLuckyBlock(s.getWorld().getBlockAt(minX, minX, y))) {
                                            LuckyBlock.getByBlock(s.getWorld().getBlockAt(minX, minX, y)).remove(false, false);
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

    void action1(final Selection selection, final Player player, final LBType type) {
        AtomicBoolean working = new AtomicBoolean(true);
        AtomicInteger totalChanges = new AtomicInteger(0);

        Scheduler.create(new Runnable() {

            private int x = selection.getMinimumPoint().getBlockX();
            private int y = selection.getMinimumPoint().getBlockY();
            private int z = selection.getMinimumPoint().getBlockZ();

            private final int x1 = selection.getMinimumPoint().getBlockX();
            private final int z1 = selection.getMinimumPoint().getBlockZ();

            private final int x2 = selection.getMaximumPoint().getBlockX();
            private final int y2 = selection.getMaximumPoint().getBlockY();
            private final int z2 = selection.getMaximumPoint().getBlockZ();

            @Override
            public void run() {
                boolean changeX = true;
                boolean changeZ = true;

                if(LuckyBlock.canSaveMore()) {
                    Block block = selection.getWorld().getBlockAt(this.x, this.y, this.z);
                    if(!LuckyBlock.isLuckyBlock(block)) {
                        LuckyBlock.placeLB(block.getLocation(), type, null, null, null, 0, LBOption.NO_SOUNDS);
                        totalChanges.incrementAndGet();
                    }

                    if(x == x2 && y == y2 && z == z2) {
                        working.set(false);
                    }

                    if(x == x2 && z == z2) {
                        y++;
                        z = z1;
                        changeZ = false;
                    }

                    if(x == x2) {
                        if(changeZ) {
                            z++;
                        }
                        x = x1;
                        changeX = false;
                    }

                    if(changeX) {
                        x++;
                    }
                }
            }
        }).predicate(working::get).onCancel(() -> {
            String a = LBCRegion.val("command.region.action1.success", false);
            a = a.replace("%total%", String.valueOf(totalChanges.get()));
            LBCRegion.send_2(player, a);
        }).timer(5, 6);
    }

    void action2(final Selection selection, final Player player, final LuckyBlockDrop drop, final CustomDrop customDrop) {
        AtomicBoolean working = new AtomicBoolean(true);
        AtomicInteger totalChanges = new AtomicInteger(0);

        Scheduler.create(new Runnable() {

            private int x = selection.getMinimumPoint().getBlockX();
            private int y = selection.getMinimumPoint().getBlockY();
            private int z = selection.getMinimumPoint().getBlockZ();
            private final int x1 = selection.getMinimumPoint().getBlockX();
            private final int z1 = selection.getMinimumPoint().getBlockZ();
            private final int x2 = selection.getMaximumPoint().getBlockX();
            private final int y2 = selection.getMaximumPoint().getBlockY();
            private final int z2 = selection.getMaximumPoint().getBlockZ();

            @Override
            public void run() {
                boolean changeX = true;
                boolean changeZ = true;
                Block b = selection.getWorld().getBlockAt(this.x, this.y, this.z);

                LuckyBlock luckyBlock = LuckyBlock.getByBlock(b);
                if (luckyBlock != null) {
                    if (customDrop != null) {
                        luckyBlock.customDrop = customDrop;
                        luckyBlock.refreshCustomDrop();
                        luckyBlock.changed();
                        totalChanges.incrementAndGet();
                    } else if (drop != null) {
                        luckyBlock.setDrop(drop, true);
                        luckyBlock.changed();
                        totalChanges.incrementAndGet();
                    }
                }

                if (this.y == this.y2 && this.x == this.x2 && this.z == this.z2) {
                    working.set(false);
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
            }
        }).predicate(working::get).onCancel(() -> {
            int total = totalChanges.get();
            if (total > 0) {
                LuckyBlock.saveAll();
                String a = LBCRegion.val("command.region.action2", false);
                a = a.replace("%total%", String.valueOf(total));
                LBCRegion.send_2(player, a);
            } else {
                LBCRegion.send(player, "command.region.no_changes");
            }
        }).timer(5, 3);
    }
}

