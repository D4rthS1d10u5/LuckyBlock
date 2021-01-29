package com.mcgamer199.luckyblock.logic;

import com.mcgamer199.luckyblock.api.item.ItemFactory;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.IDebug;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class ColorsClass {
    protected static ChatColor red;
    protected static ChatColor blue;
    protected static ChatColor aqua;
    protected static ChatColor black;
    protected static ChatColor bold;
    protected static ChatColor darkaqua;
    protected static ChatColor darkblue;
    protected static ChatColor darkgray;
    protected static ChatColor darkgreen;
    protected static ChatColor darkpurple;
    protected static ChatColor darkred;
    protected static ChatColor gold;
    protected static ChatColor gray;
    protected static ChatColor green;
    protected static ChatColor italic;
    protected static ChatColor lightpurple;
    protected static ChatColor magic;
    protected static ChatColor reset;
    protected static ChatColor strikethrough;
    protected static ChatColor underline;
    protected static ChatColor white;
    protected static ChatColor yellow;
    protected static String pn;
    protected static Random random;
    protected static List<Material> swords;
    private static final List<UUID> wait;
    private static final long delay;

    static {
        red = ChatColor.RED;
        blue = ChatColor.BLUE;
        aqua = ChatColor.AQUA;
        black = ChatColor.BLACK;
        bold = ChatColor.BOLD;
        darkaqua = ChatColor.DARK_AQUA;
        darkblue = ChatColor.DARK_BLUE;
        darkgray = ChatColor.DARK_GRAY;
        darkgreen = ChatColor.DARK_GREEN;
        darkpurple = ChatColor.DARK_PURPLE;
        darkred = ChatColor.DARK_RED;
        gold = ChatColor.GOLD;
        gray = ChatColor.GRAY;
        green = ChatColor.GREEN;
        italic = ChatColor.ITALIC;
        lightpurple = ChatColor.LIGHT_PURPLE;
        magic = ChatColor.MAGIC;
        reset = ChatColor.RESET;
        strikethrough = ChatColor.STRIKETHROUGH;
        underline = ChatColor.UNDERLINE;
        white = ChatColor.WHITE;
        yellow = ChatColor.YELLOW;
        pn = "DarkKing_199";
        random = new Random();
        swords = Arrays.asList(Material.DIAMOND_SWORD, Material.GOLD_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD);
        wait = new ArrayList();
        delay = LuckyBlockPlugin.instance.config.getLong("delay");
    }

    public ColorsClass() {
    }

    protected static final String blockToString(Block block) {
        String world = block.getWorld().getName();
        return world + "," + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    protected static final String locToString(Location loc) {
        String world = loc.getWorld().getName();
        return world + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    protected static final Location stringToLoc(String s) {
        String[] d = s.split(",");
        World world = Bukkit.getWorld(d[0]);
        double x = Double.parseDouble(d[1]);
        double y = Double.parseDouble(d[2]);
        double z = Double.parseDouble(d[3]);
        return new Location(world, x, y, z);
    }

    protected static final Block stringToBlock(String s) {
        String a = ChatColor.stripColor(s);
        String[] d = a.split(",");
        Block block = null;
        World world = Bukkit.getWorld(d[0]);
        int x = Integer.parseInt(d[1]);
        int y = Integer.parseInt(d[2]);
        int z = Integer.parseInt(d[3]);
        if (world != null && world.getBlockAt(x, y, z) != null) {
            block = world.getBlockAt(x, y, z);
        }

        return block;
    }

    private static String getMessage(String loc, ColorsClass.ObjectType... objs) {
        return getMessage1(loc, true, objs);
    }

    private static String getMessage1(String loc, boolean colors, ColorsClass.ObjectType... objs) {
        String s = "null";
        String a = IObjects.getString(loc);
        if (a == null) {
            return s;
        } else {
            s = a;
            if (colors) {
                s = ChatColor.translateAlternateColorCodes('&', a);
            }

            try {
                s = s.replace("<lbcmd>", ILBCmd.lcmd);
            } catch (Exception var10) {
            }

            if (objs.length > 0) {
                ColorsClass.ObjectType[] var8 = objs;
                int var7 = objs.length;

                for (int var6 = 0; var6 < var7; ++var6) {
                    ColorsClass.ObjectType obj = var8[var6];
                    if (obj != null) {
                        if (obj == ColorsClass.ObjectType.ITEM && obj.getObj() != null && obj.getObj() instanceof ItemStack) {
                            ItemStack item = (ItemStack) obj.getObj();
                            s = s.replace("{itemType}", item.getType().name());
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                s = s.replace("{itemName}", item.getItemMeta().getDisplayName());
                            }

                            s = s.replace("{itemAmount}", String.valueOf(item.getAmount()));
                            s = s.replace("{itemData}", String.valueOf(item.getDurability()));
                        }

                        if (obj == ColorsClass.ObjectType.BLOCK && obj.getObj() != null && obj.getObj() instanceof Block) {
                            Block block = (Block) obj.getObj();
                            s = s.replace("{blockType}", block.getType().name());
                            s = s.replace("{blockLightLevel}", String.valueOf(block.getLightLevel()));
                        }
                    }
                }
            }

            return s;
        }
    }

    protected static final void addToList(final UUID uuid) {
        if (!wait.contains(uuid)) {
            wait.add(uuid);
            SchedulerTask task = new SchedulerTask();
            task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
                public void run() {
                    ColorsClass.wait.remove(uuid);
                }
            }, delay));
        }

    }

    protected static final boolean canRun(UUID uuid) {
        return !wait.contains(uuid);
    }

    protected static final Sound getSound(String name) {
        Sound s = null;
        String n = IObjects.getSound(name);
        if (n != null) {
            try {
                s = Sound.valueOf(n);
            } catch (Exception var4) {
            }
        }

        return s;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected static final Sound _deprecated_getSound(String name) {
        Sound s = null;
        File folder = new File(LuckyBlockPlugin.d() + "data/sounds/" + LuckyBlockPlugin.sounds_file);
        if (folder.exists() && folder.getName().endsWith(".yml")) {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(folder);
            if (fc.getString(name) != null && isSoundValid(fc.getString(name))) {
                s = getSoundByName(fc.getString(name));
                return s;
            }
        }

        folder = new File(LuckyBlockPlugin.d() + "data/sounds");
        if (folder.listFiles() != null) {
            File[] var6;
            int var5 = (var6 = folder.listFiles()).length;

            for (int var4 = 0; var4 < var5; ++var4) {
                File file = var6[var4];
                if (file.getName().endsWith(".yml")) {
                    FileConfiguration c = YamlConfiguration.loadConfiguration(file);
                    if (c.getString(name) != null) {
                        try {
                            s = Sound.valueOf(c.getString(name));
                        } catch (Exception var9) {
                        }
                    }
                }
            }
        }

        return s;
    }

    static boolean isSoundValid(String name) {
        try {
            Sound s = Sound.valueOf(name.toUpperCase());
            return s != null;
        } catch (Exception var2) {
            return false;
        }
    }

    static Sound getSoundByName(String name) {
        return isSoundValid(name) ? Sound.valueOf(name.toUpperCase()) : null;
    }

    protected static final String val(String loc) {
        return getMessage(loc);
    }

    protected static final String val(String loc, boolean colors) {
        return getMessage1(loc, colors);
    }

    protected static final void send(CommandSender sender, String loc) {
        sendMessage(sender, loc);
    }

    protected static final void send(CommandSender sender, String loc, String apnd) {
        msg_a(sender, loc, apnd);
    }

    protected static final void send_no(CommandSender sender, String loc) {
        send_no(sender, loc, null);
    }

    protected static final void send_no(CommandSender sender, String loc, String ap) {
        String s = getMessage(loc);
        if (ap != null) {
            s = s + ap;
        }

        if (s != null) {
            sender.sendMessage(s);
        }

    }

    protected static final void sendMessage(CommandSender sender, String loc, ColorsClass.ObjectType... objectTypes) {
        msg_a(sender, loc, null, objectTypes);
    }

    protected static final void sendMessage(CommandSender sender, String loc, com.mcgamer199.luckyblock.tellraw.TextAction[] actions, ColorsClass.ObjectType... objectTypes) {
        String s = getMessage(loc, objectTypes);
        if (s != null && !s.equalsIgnoreCase("")) {
            if (sender instanceof Player) {
                com.mcgamer199.luckyblock.tellraw.RawText _a = new com.mcgamer199.luckyblock.tellraw.RawText("");
                com.mcgamer199.luckyblock.tellraw.RawText pl = new com.mcgamer199.luckyblock.tellraw.RawText(green + "[" + yellow + bold + "LB" + green + "]");
                pl.addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + "Lucky Block"));
                com.mcgamer199.luckyblock.tellraw.RawText text1 = new com.mcgamer199.luckyblock.tellraw.RawText(": ");
                com.mcgamer199.luckyblock.tellraw.RawText text2 = new com.mcgamer199.luckyblock.tellraw.RawText(s);
                com.mcgamer199.luckyblock.tellraw.TextAction[] var12 = actions;
                int var11 = actions.length;

                for (int var10 = 0; var10 < var11; ++var10) {
                    com.mcgamer199.luckyblock.tellraw.TextAction a = var12[var10];
                    if (a != null) {
                        text2.addAction(a);
                    }
                }

                com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo((Player) sender, _a, pl, text1, text2);
            } else {
                sender.sendMessage(green + "[" + yellow + "LB" + green + "]" + reset + ": " + s);
            }
        }

    }

    private static void msg_a(CommandSender sender, String loc, String ap, ColorsClass.ObjectType... objectTypes) {
        String s = getMessage(loc, objectTypes);
        if (s != null && !s.equalsIgnoreCase("")) {
            if (ap != null) {
                s = s + ap;
            }

            if (sender instanceof Player) {
                com.mcgamer199.luckyblock.tellraw.RawText _a = new com.mcgamer199.luckyblock.tellraw.RawText("");
                com.mcgamer199.luckyblock.tellraw.RawText pl = new com.mcgamer199.luckyblock.tellraw.RawText(green + "[" + yellow + bold + "LB" + green + "]");
                pl.addAction(new com.mcgamer199.luckyblock.tellraw.TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + "Lucky Block"));
                com.mcgamer199.luckyblock.tellraw.RawText text1 = new com.mcgamer199.luckyblock.tellraw.RawText(": ");
                com.mcgamer199.luckyblock.tellraw.RawText text2 = new com.mcgamer199.luckyblock.tellraw.RawText(s);
                com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo((Player) sender, _a, pl, text1, text2);
            } else {
                sender.sendMessage(green + "[" + yellow + "LB" + green + "]" + reset + ": " + s);
            }
        }

    }

    private static void msg_b(CommandSender sender, String string, ColorsClass.ObjectType... objectTypes) {
        if (string != null) {
            string = c(string);
            if (!string.equalsIgnoreCase("")) {
                if (sender instanceof Player) {
                    com.mcgamer199.luckyblock.tellraw.RawText _a = new com.mcgamer199.luckyblock.tellraw.RawText("");
                    com.mcgamer199.luckyblock.tellraw.RawText pl = new com.mcgamer199.luckyblock.tellraw.RawText(green + "[" + yellow + bold + "LB" + green + "]");
                    pl.addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, yellow + "Lucky Block"));
                    com.mcgamer199.luckyblock.tellraw.RawText text1 = new com.mcgamer199.luckyblock.tellraw.RawText(": ");
                    com.mcgamer199.luckyblock.tellraw.RawText text2 = new com.mcgamer199.luckyblock.tellraw.RawText(string);
                    com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo((Player) sender, _a, pl, text1, text2);
                } else {
                    sender.sendMessage(green + "[" + yellow + "LB" + green + "]" + reset + ": " + string);
                }
            }
        }

    }

    protected static final void send_2(CommandSender sender, String string) {
        msg_b(sender, string);
    }

    protected static final String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    protected static final void Debug(String name, DebugData... datas) {
        IDebug.sendDebug(name, datas);
    }

    protected static final boolean compareItems(ItemStack item1, ItemStack item2) {
        if (item1 != null && item2 != null) {
            if (item1.getType() != item2.getType()) {
                return false;
            } else {
                if (item2.hasItemMeta()) {
                    if (!item1.hasItemMeta()) {
                        return false;
                    }

                    return !item2.getItemMeta().hasDisplayName() || (item1.getItemMeta().hasDisplayName() && item1.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName()));
                }

                return true;
            }
        } else {
            return false;
        }
    }

    protected final boolean isMainHand(PlayerInteractEvent event) {
        try {
            Method method = event.getClass().getMethod("getHand");
            Object o = method.invoke(event);
            if (o.toString().equalsIgnoreCase("OFF_HAND")) {
                return false;
            }
        } catch (Exception var4) {
        }

        return true;
    }

    protected final boolean isSword(ItemStack item) {
        return (item != null && item.getType() != Material.AIR) && ItemFactory.isSword(item);
    }

    protected enum ObjectType {
        BLOCK,
        ENTITY,
        ITEM,
        PLAYER;

        private Object obj;

        ObjectType() {
        }

        public static ColorsClass.ObjectType getByName(String name) {
            ColorsClass.ObjectType[] var4;
            int var3 = (var4 = values()).length;

            for (int var2 = 0; var2 < var3; ++var2) {
                ColorsClass.ObjectType obj = var4[var2];
                if (obj.name().equalsIgnoreCase(name)) {
                    return obj;
                }
            }

            return null;
        }

        public Object getObj() {
            return this.obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }
    }
}
