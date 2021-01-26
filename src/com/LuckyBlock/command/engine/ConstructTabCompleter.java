package com.LuckyBlock.command.engine;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.Event.LB.Block.PlaceLuckyBlock;
import com.LuckyBlock.Event.LB.Block.PlaceLuckyBlock.LBOption;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.LB.LBDrop;
import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.Resources.LBItem;
import com.LuckyBlock.customdrop.CustomDrop;
import com.LuckyBlock.customdrop.CustomDropManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.core.entity.CustomEntity;

import java.io.File;
import java.util.*;

public class ConstructTabCompleter extends ILBCmd implements TabCompleter {
    public ConstructTabCompleter() {
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList();
        String s;
        Iterator var7;
        if (args.length == 1) {
            if (args[0] == null) {
                list = ILBCmd.getAllowedCommands(sender, cmd.getName());
            } else {
                var7 = ILBCmd.getAllowedCommands(sender, cmd.getName()).iterator();

                while(var7.hasNext()) {
                    s = (String)var7.next();
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        ((List)list).add(s);
                    }
                }
            }
        } else {
            if (!this.canRun(sender, "lb", args)) {
                return (List)list;
            }

            if (args[0].equalsIgnoreCase("clearlbs")) {
                if (args[1] == null) {
                    ((List)list).add("true");
                } else if ("true".startsWith(args[1])) {
                    ((List)list).add("true");
                }
            } else {
                int var8;
                Player player;
                LBType type;
                int var16;
                if (!args[0].equalsIgnoreCase("luckyblock") && !args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("lb")) {
                    if (args[0].equalsIgnoreCase("detector")) {
                        if (args.length == 2) {
                            var7 = Bukkit.getOnlinePlayers().iterator();

                            while(var7.hasNext()) {
                                player = (Player)var7.next();
                                ((List)list).add(player.getName());
                            }
                        }
                    } else {
                        int x;
                        if (args[0].equalsIgnoreCase("help")) {
                            if (args.length == 2) {
                                for(x = 1; x < ILBCmd.getPagesCount(sender, ILBCmd.lcmd) + 1; ++x) {
                                    ((List)list).add("" + x);
                                }
                            }
                        } else {
                            LBDrop drop;
                            LBDrop[] var19;
                            CustomDrop customDrop;
                            if (args[0].equalsIgnoreCase("setdrop")) {
                                if (args.length == 2 && sender instanceof Player && LB.isLuckyBlock(((Player)sender).getTargetBlock((Set)null, 100))) {
                                    if (args[1] == null) {
                                        var8 = (var19 = LBDrop.values()).length;

                                        for(var16 = 0; var16 < var8; ++var16) {
                                            drop = var19[var16];
                                            if (drop.isVisible()) {
                                                ((List)list).add(drop.name());
                                            }
                                        }

                                        for(x = 0; x < CustomDropManager.getCustomDrops().size(); ++x) {
                                            customDrop = (CustomDrop)CustomDropManager.getCustomDrops().get(x);
                                            if (customDrop.isVisible()) {
                                                ((List)list).add(customDrop.getName());
                                            }
                                        }
                                    } else {
                                        var8 = (var19 = LBDrop.values()).length;

                                        for(var16 = 0; var16 < var8; ++var16) {
                                            drop = var19[var16];
                                            if (drop.name().startsWith(args[1].toUpperCase()) && drop.isVisible()) {
                                                ((List)list).add(drop.name());
                                            }
                                        }

                                        for(x = 0; x < CustomDropManager.getCustomDrops().size(); ++x) {
                                            customDrop = (CustomDrop)CustomDropManager.getCustomDrops().get(x);
                                            if (customDrop.isVisible() && customDrop.getName().startsWith(args[1].toUpperCase())) {
                                                ((List)list).add(customDrop.getName());
                                            }
                                        }
                                    }
                                }
                            } else {
                                Iterator var18;
                                String stringProsto;
                                if (args[0].equalsIgnoreCase("region")) {
                                    if (!LuckyBlock.isWorldEditValid()) {
                                        return null;
                                    }

                                    if (args.length == 2) {
                                        List<String> l = Arrays.asList("clear", "setdrop", "setlb", "setowner");
                                        if (args[1] == null) {
                                            list = l;
                                        } else {
                                            var18 = l.iterator();

                                            while(var18.hasNext()) {
                                                stringProsto = (String)var18.next();
                                                if (stringProsto.startsWith(args[1].toLowerCase())) {
                                                    ((List)list).add(stringProsto);
                                                }
                                            }
                                        }
                                    } else if (args.length == 3) {
                                        if (args[1].equalsIgnoreCase("setdrop")) {
                                            if (args[2] == null) {
                                                var8 = (var19 = LBDrop.values()).length;

                                                for(var16 = 0; var16 < var8; ++var16) {
                                                    drop = var19[var16];
                                                    if (drop.isVisible()) {
                                                        ((List)list).add(drop.name());
                                                    }
                                                }

                                                for(x = 0; x < CustomDropManager.getCustomDrops().size(); ++x) {
                                                    customDrop = (CustomDrop)CustomDropManager.getCustomDrops().get(x);
                                                    if (customDrop.isVisible()) {
                                                        ((List)list).add(customDrop.getName());
                                                    }
                                                }
                                            } else {
                                                var8 = (var19 = LBDrop.values()).length;

                                                for(var16 = 0; var16 < var8; ++var16) {
                                                    drop = var19[var16];
                                                    if (drop.name().startsWith(args[2].toUpperCase()) && drop.isVisible()) {
                                                        ((List)list).add(drop.name());
                                                    }
                                                }

                                                for(x = 0; x < CustomDropManager.getCustomDrops().size(); ++x) {
                                                    customDrop = (CustomDrop)CustomDropManager.getCustomDrops().get(x);
                                                    if (customDrop.isVisible() && customDrop.getName().startsWith(args[2].toUpperCase())) {
                                                        ((List)list).add(customDrop.getName());
                                                    }
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("setowner") && sender instanceof Player) {
                                            ((List)list).add(((Player)sender).getUniqueId().toString());
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("lbitem")) {
                                    if (args.length == 2) {
                                        LBItem[] var22;
                                        LBItem i;
                                        if (args[1] == null) {
                                            var8 = (var22 = LBItem.values()).length;

                                            for(var16 = 0; var16 < var8; ++var16) {
                                                i = var22[var16];
                                                ((List)list).add(i.name());
                                            }
                                        } else {
                                            var8 = (var22 = LBItem.values()).length;

                                            for(var16 = 0; var16 < var8; ++var16) {
                                                i = var22[var16];
                                                if (i.name().startsWith(args[1].toUpperCase())) {
                                                    ((List)list).add(i.name());
                                                }
                                            }
                                        }
                                    } else if (args.length == 3) {
                                        if (args[1] == null) {
                                            var7 = Bukkit.getOnlinePlayers().iterator();

                                            while(var7.hasNext()) {
                                                player = (Player)var7.next();
                                                ((List)list).add(player.getName());
                                            }
                                        } else {
                                            var7 = Bukkit.getOnlinePlayers().iterator();

                                            while(var7.hasNext()) {
                                                player = (Player)var7.next();
                                                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                                    ((List)list).add(player.getName());
                                                }
                                            }
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("spawnegg")) {
                                    if (args.length == 2) {
                                        List<String> ents = new ArrayList();
                                        var18 = CustomEntity.classes.iterator();

                                        while(var18.hasNext()) {
                                            CustomEntity e = (CustomEntity)var18.next();
                                            ents.add(e.getClass().getName().replace("com.LuckyBlock.customentity.", "LB_").replace(".", "-"));
                                        }

                                        if (args[1] == null) {
                                            list = ents;
                                        } else {
                                            var18 = ents.iterator();

                                            while(var18.hasNext()) {
                                                stringProsto = (String)var18.next();
                                                if (stringProsto.toUpperCase().startsWith(args[1].toUpperCase())) {
                                                    ((List)list).add(stringProsto);
                                                }
                                            }
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("placelb")) {
                                    if (args.length == 2) {
                                        ((List)list).add("~0");
                                    } else if (args.length == 3) {
                                        ((List)list).add("~0");
                                    } else if (args.length == 4) {
                                        ((List)list).add("~0");
                                    } else if (args.length == 5) {
                                        var7 = LBType.getTypes().iterator();

                                        while(var7.hasNext()) {
                                            type = (LBType)var7.next();
                                            if (!type.disabled) {
                                                ((List)list).add(String.valueOf(type.getId()));
                                            }
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("setlang")) {
                                    if (args.length == 2) {
                                        File folder = new File(LuckyBlock.d() + "Data/messages");
                                        if (folder.listFiles() != null) {
                                            File[] var10;
                                            int var24 = (var10 = folder.listFiles()).length;

                                            for(var8 = 0; var8 < var24; ++var8) {
                                                File f = var10[var8];
                                                if (f != null && f.getName().endsWith(".yml")) {
                                                    String[] d = f.getName().split(".yml");
                                                    ((List)list).add(d[0]);
                                                }
                                            }
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("generate")) {
                                    if (args.length == 2) {
                                        ((List)list).add("LB_");
                                    }
                                } else if (args[0].equalsIgnoreCase("savestructure")) {
                                    if (sender instanceof Player) {
                                        player = (Player)sender;
                                        if (args.length == 2) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockX()));
                                        } else if (args.length == 3) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockY()));
                                        } else if (args.length == 4) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockZ()));
                                        } else if (args.length == 5) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockX()));
                                        } else if (args.length == 6) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockY()));
                                        } else if (args.length == 7) {
                                            ((List)list).add(String.valueOf(player.getLocation().getBlockZ()));
                                        } else if (args.length == 8) {
                                            ((List)list).add("false");
                                            ((List)list).add("true");
                                        }
                                    }
                                } else if (args[0].equalsIgnoreCase("commanddesc") && args.length == 2) {
                                    if (args[1] == null) {
                                        list = ILBCmd.getAllowedCommands(sender, cmd.getName());
                                    } else {
                                        var7 = ILBCmd.getAllowedCommands(sender, cmd.getName()).iterator();

                                        while(var7.hasNext()) {
                                            stringProsto = (String)var7.next();
                                            if (stringProsto.toLowerCase().startsWith(args[1].toLowerCase())) {
                                                ((List)list).add(stringProsto);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (args.length == 2) {
                    var7 = Bukkit.getOnlinePlayers().iterator();

                    while(var7.hasNext()) {
                        player = (Player)var7.next();
                        ((List)list).add(player.getName());
                    }
                } else if (args.length == 3) {
                    ((List)list).add("1");
                } else if (args.length == 4) {
                    ((List)list).add("random");
                } else if (args.length == 5) {
                    var7 = LBType.getTypes().iterator();

                    while(var7.hasNext()) {
                        type = (LBType)var7.next();
                        if (!type.disabled) {
                            ((List)list).add(String.valueOf(type.getId()));
                        }
                    }
                } else if (args.length > 5) {
                    LBOption[] var9;
                    var8 = (var9 = LBOption.values()).length;

                    for(var16 = 0; var16 < var8; ++var16) {
                        LBOption option = var9[var16];
                        ((List)list).add(option.name().toLowerCase());
                    }
                }
            }
        }

        return (List)list;
    }
}
