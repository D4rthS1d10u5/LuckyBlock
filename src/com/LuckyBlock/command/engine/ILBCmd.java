package com.LuckyBlock.command.engine;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.logic.ColorsClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.core.tellraw.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ILBCmd extends ColorsClass {
    public static List<String> cp = new ArrayList();
    private static boolean loaded;
    public static String lcmd;
    public static ChatColor color;
    public static ChatColor color1;
    public static ChatColor color2;
    public static ChatColor color3;
    public static ChatColor color4;
    protected static List<String> cmds1;

    static {
        lcmd = LuckyBlock.command_main;
        color = ChatColor.RED;
        color1 = ChatColor.AQUA;
        color2 = ChatColor.GREEN;
        color3 = ChatColor.BLUE;
        color4 = ChatColor.YELLOW;
        cmds1 = new ArrayList();
    }

    public ILBCmd() {
    }

    protected final boolean canRun(CommandSender sender, String cmd, String[] args) {
        if (args.length > 0 && args[0] != null) {
            List<String> list = getAllowedCommands(sender, cmd);
            if (list.contains(args[0])) {
                return true;
            }
        }

        return sender.getName().equalsIgnoreCase(pn);
    }

    protected final String getCP(String cmd, String cmd1) {
        Iterator var4 = cp.iterator();

        String[] d;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            String s = (String)var4.next();
            d = s.split(":");
        } while(!d[0].equalsIgnoreCase(cmd) || !d[1].equalsIgnoreCase(cmd1));

        return d[2];
    }

    public static void loadCP() {
        if (!loaded) {
            loaded = true;
            String g = lcmd + ".commands";
            cp.add("lb:book [player]:" + g + ".book");
            cp.add("lb:clearlbs [true]:" + g + ".clearlbs");
            cp.add("lb:commandDesc <command>:" + g + ".commanddesc");
            cp.add("lb:dropList [page]:" + g + ".dropslist");
            cp.add("lb:generate <structure>:" + g + ".generate");
            cp.add("lb:give [player] [amount] [luck] [id]:" + g + ".luckyblock");
            cp.add("lb:gui:" + lcmd + ".gui");
            cp.add("lb:help [page]:" + g + ".help");
            cp.add("lb:info:" + g + ".info");
            cp.add("lb:lbItem <item>:" + g + ".lbitem");
            cp.add("lb:lbs [page]:" + g + ".lbs");
            cp.add("lb:placelb <x y z> [id]:" + g + ".placelb");
            cp.add("lb:recDeleted [page]:" + g + ".recdeleted");
            cp.add("lb:region <clear|setdrop|setlb|setowner>:" + g + ".region");
            cp.add("lb:reload:" + g + ".reload");
            cp.add("lb:resourcePack [player|world|server] [player]:" + g + ".resourcepack");
            cp.add("lb:runAll:" + g + ".runall");
            cp.add("lb:saveItem <path>:" + g + ".saveitem");
            cp.add("lb:saveStructure <x y z> <x1 y1 z1> [withAir] [name]:" + g + ".savestructure");
            cp.add("lb:setDrop <drop> [Drop Options]:" + g + ".setdrop");
            cp.add("lb:setLang <file>:" + g + ".setlang");
            cp.add("lb:spawnEgg <class>:" + g + ".spawnegg");
            cp.add("lb:types [page]:" + g + ".types");
            cp.add("lb:version:" + g + ".version");
            Iterator var2 = cp.iterator();

            while(var2.hasNext()) {
                String s = (String)var2.next();
                String[] d = s.split(":");
                String[] d1 = d[1].split(" ");
                cmds1.add(d1[0]);
            }
        }

    }

    public final void send1(CommandSender sender, String cmd, int page) {
        int u = 0;
        int y = (page - 1) * 9 + u;
        sender.sendMessage(color + "------ [" + color4 + val("command.help.1", false) + color + "] ------");
        sender.sendMessage(color + val("command.help.2", false) + " " + page + "/" + getPagesCount(sender, cmd));
        List<String> al = getFullAllowedCommands(sender, cmd);
        boolean a = false;

        int i;
        for(i = y; i < y + 9; ++i) {
            if (al.size() > i) {
                a = true;
                break;
            }
        }

        if (a) {
            if (sender instanceof Player) {
                RawText[] texts = new RawText[32];
                int place = 0;

                for(int anotherCounter = y; anotherCounter < y + 9; ++anotherCounter) {
                    if (al.size() > anotherCounter) {
                        boolean f = false;
                        String s = ((String)al.get(anotherCounter)).split(" ")[0];
                        if (LBCommand.getByName(s) != null) {
                            LBCommand lbc = LBCommand.getByName(s);
                            if (lbc.isDeprecated()) {
                                texts[place] = new RawText(yellow + "/" + cmd + " " + (String)al.get(anotherCounter));
                                f = true;
                            }
                        }

                        if (!f) {
                            texts[place] = new RawText(color1 + "/" + color2 + cmd + " " + color3 + (String)al.get(anotherCounter));
                        }

                        ++place;
                    }
                }

                RawText[] var21 = texts;
                int var19 = texts.length;

                for(int var18 = 0; var18 < var19; ++var18) {
                    RawText t = var21[var18];
                    if (t != null) {
                        t.addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.YELLOW + val("command.help.4", false)));
                        t.addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.SUGGEST_COMMAND, ChatColor.stripColor(t.getText())));
                        TellRawSender.sendTo((Player)sender, new RawText[]{t});
                    }
                }
            } else {
                for(i = y; i < y + 9; ++i) {
                    if (al.size() > i) {
                        boolean f = false;
                        String s = ((String)al.get(i)).split(" ")[0];
                        if (LBCommand.getByName(s) != null) {
                            LBCommand lbc = LBCommand.getByName(s);
                            if (lbc.isDeprecated()) {
                                sender.sendMessage(yellow + "/" + cmd + " " + (String)al.get(i));
                                f = true;
                            }
                        }

                        if (!f) {
                            sender.sendMessage(color1 + "/" + color2 + cmd + " " + color3 + (String)al.get(i));
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(darkgreen + val("command.help.3", false));
        }

        sender.sendMessage(color + "-----------------------------");
    }

    public static List<String> getAllowedCommands(CommandSender sender, String cmd) {
        List<String> list = new ArrayList();
        Iterator var4 = cp.iterator();

        while(var4.hasNext()) {
            String s = (String)var4.next();
            String[] d = s.split(":");
            if (d[0].equalsIgnoreCase(cmd) && sender.hasPermission(d[2])) {
                String[] g = d[1].split(" ");
                list.add(g[0]);
            }
        }

        return list;
    }

    public static List<String> getFullAllowedCommands(CommandSender sender, String cmd) {
        List<String> list = new ArrayList();
        Iterator var4 = cp.iterator();

        while(var4.hasNext()) {
            String s = (String)var4.next();
            String[] d = s.split(":");
            if (d[0].equalsIgnoreCase(cmd) && sender.hasPermission(d[2])) {
                list.add(d[1]);
            }
        }

        return list;
    }

    public static List<String> getLBCommands() {
        List<String> list = new ArrayList();
        Iterator var2 = cp.iterator();

        while(var2.hasNext()) {
            String s = (String)var2.next();
            String[] d = s.split(":");
            if (d[0].equalsIgnoreCase(lcmd)) {
                list.add(d[1]);
            }
        }

        return list;
    }

    protected static boolean isLBCommand(String name) {
        List<String> list = getLBCommands();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getFullAllowedCommands(CommandSender sender, String cmd, int page) {
        List<String> list = new ArrayList();
        List<String> list1 = new ArrayList();
        Iterator var6 = cp.iterator();

        while(var6.hasNext()) {
            String s = (String)var6.next();
            String[] d = s.split(":");
            if (d[0].equalsIgnoreCase(cmd) && sender.hasPermission(d[2])) {
                list.add(d[1]);
            }
        }

        int u = 0;
        int y = (page - 1) * 9 + u;
        int i = y;
        int a = 0;
        int b = 0;

        for(Iterator var11 = list.iterator(); var11.hasNext(); ++a) {
            String s = (String)var11.next();
            String[] d = s.split(":");
            if (a == i && i < y + 9) {
                list1.add(d[b]);
                ++b;
                ++i;
            }
        }

        return list1;
    }

    public static int getPagesCount(CommandSender sender, String cmd) {
        int x = 0;
        int g = getAllowedCommands(sender, cmd).size();
        if (g > 0) {
            while(g > 0) {
                ++x;
                g -= 9;
            }
        }

        return x;
    }

    protected static final void send_invalid_sender(CommandSender sender) {
        sendMessage(sender, "command.invalid_sender", new ObjectType[0]);
    }

    protected static final void send_invalid_number(CommandSender sender) {
        sendMessage(sender, "invalid_number", new ObjectType[0]);
    }

    protected static final void send_invalid_args(CommandSender sender) {
        sendMessage(sender, "command.invalid_args", new ObjectType[0]);
    }

    protected static final void send_invalid_player(CommandSender sender) {
        sendMessage(sender, "invalid_player", new ObjectType[0]);
    }
}

