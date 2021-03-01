package com.mcgamer199.luckyblock.api;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.IDebug;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

    protected static void addToList(final UUID uuid) {
        if (!wait.contains(uuid)) {
            wait.add(uuid);
            Scheduler.later(() -> wait.remove(uuid), delay);
        }
    }

    protected static final boolean canRun(UUID uuid) {
        return !wait.contains(uuid);
    }

    public static String val(String loc) {
        return getMessage(loc);
    }

    public static String val(String loc, boolean colors) {
        return getMessage1(loc, colors);
    }

    public static void send(CommandSender sender, String loc) {
        sendMessage(sender, loc);
    }

    protected static final void send(CommandSender sender, String loc, String apnd) {
        msg_a(sender, loc, apnd);
    }

    public static void send_no(CommandSender sender, String loc) {
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

    protected static void sendInvalid(CommandSender sender) {
        String invalidCommand = getMessage("invalid_command");
        if(StringUtils.isNotEmpty(invalidCommand)) {
            ChatComponent component = new ChatComponent();
            component.addText("§a[§e§lLB§a]: ", Hover.show_text, "§Lucky Block");
            String command = String.format("/%s help", ILBCmd.lcmd);
            component.addText(invalidCommand, Hover.show_text, command, Click.run_command, command);
            component.send(sender);
        }
    }

    private static void msg_a(CommandSender sender, String loc, String ap, ObjectType... objectTypes) {
        String message = getMessage(loc, objectTypes);
        if (message != null && !message.equalsIgnoreCase("")) {
            if (ap != null) {
                message = message + ap;
            }

            ChatComponent component = new ChatComponent();
            component.addText(String.format("§a[§e§lLB§a]: %s", message), Hover.show_text, "§Lucky Block");
            component.send(sender);
        }
    }

    private static void msg_b(CommandSender sender, String message, ColorsClass.ObjectType... objectTypes) {
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            if (!message.equalsIgnoreCase("")) {
                ChatComponent component = new ChatComponent();
                component.addText(String.format("§a[§e§lLB§a]: %s", message), Hover.show_text, "§Lucky Block");
                component.send(sender);
            }
        }

    }

    protected static final void send_2(CommandSender sender, String string) {
        msg_b(sender, string);
    }

    protected static void Debug(String name, DebugData... datas) {
        IDebug.sendDebug(name, datas);
    }

    protected static boolean compareItems(ItemStack item1, ItemStack item2) {
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
        return (item != null && item.getType() != Material.AIR) && ItemStackUtils.isSword(item);
    }

    public enum ObjectType {
        BLOCK,
        ENTITY,
        ITEM,
        PLAYER,
        NONE;

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
