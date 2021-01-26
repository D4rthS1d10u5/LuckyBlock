package org.core.nbt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Iterator;

public class ItemFactory {
    private ItemStack bukkitItem;
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public ItemFactory(ItemStack item) {
        this.bukkitItem = item.clone();
    }

    public ItemStack getItem() {
        return this.bukkitItem;
    }

    public void setString(String key, String value) {
        this.bukkitItem = ItemReflection.setString(this.bukkitItem, key, value);
    }

    public String getString(String key) {
        return ItemReflection.getString(this.bukkitItem, key);
    }

    public void setInteger(String key, int value) {
        this.bukkitItem = ItemReflection.setInt(this.bukkitItem, key, value);
    }

    public Integer getInteger(String key) {
        return ItemReflection.getInt(this.bukkitItem, key);
    }

    public void setDouble(String key, double value) {
        this.bukkitItem = ItemReflection.setDouble(this.bukkitItem, key, value);
    }

    public double getDouble(String key) {
        return ItemReflection.getDouble(this.bukkitItem, key);
    }

    public void setBoolean(String key, boolean value) {
        this.bukkitItem = ItemReflection.setBoolean(this.bukkitItem, key, value);
    }

    public boolean getBoolean(String key) {
        return ItemReflection.getBoolean(this.bukkitItem, key);
    }

    public boolean hasKey(String key) {
        return ItemReflection.hasKey(this.bukkitItem, key);
    }

    public static boolean isSword(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);

        try {
            Method method = o.getClass().getMethod("getItem", o.getClass());
            Object val = method.invoke(o.getClass());
            if (val.getClass().isAssignableFrom(getItemClass("sword"))) {
                return true;
            }
        } catch (Exception var4) {
        }

        return false;
    }

    public static boolean isAxe(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);
        return o.getClass().isAssignableFrom(getItemClass("axe"));
    }

    public static boolean isShovel(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);
        return o.getClass().isAssignableFrom(getItemClass("spade"));
    }

    public static boolean isHoe(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);
        return o.getClass().isAssignableFrom(getItemClass("hoe"));
    }

    public static boolean isPickaxe(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);
        return o.getClass().isAssignableFrom(getItemClass("pickaxe"));
    }

    public static boolean isArmor(ItemStack item) {
        Object o = ItemReflection.getNMSItemStack(item);
        return o.getClass().isAssignableFrom(getItemClass("armor"));
    }

    static Class getItemClass(String name) {
        try {
            Class c = null;
            if (name.equalsIgnoreCase("sword")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemSword");
            } else if (name.equalsIgnoreCase("spade")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemSpade");
            } else if (name.equalsIgnoreCase("axe")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemAxe");
            } else if (name.equalsIgnoreCase("pickaxe")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemPickaxe");
            } else if (name.equalsIgnoreCase("hoe")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemHoe");
            } else if (name.equalsIgnoreCase("armor")) {
                c = Class.forName("net.minecraft.server." + version + ".ItemArmor");
            }

            return c;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static boolean isMainHand(PlayerInteractEvent event) {
        try {
            Method method = event.getClass().getMethod("getHand");
            Object o = method.invoke(event);
            if (o.toString().equalsIgnoreCase("OFF_HAND")) {
                return false;
            }
        } catch (Exception var3) {
        }

        return true;
    }

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit) {
        int maxdistance = 30;
        Iterator var6 = Bukkit.getOnlinePlayers().iterator();

        while(var6.hasNext()) {
            Player p = (Player)var6.next();
            if (p.getWorld() == loc.getWorld()) {
                double distance = p.getLocation().distance(loc);
                if (distance < (double)maxdistance) {
                    float volume = (float)(1.0D - distance / (double)maxdistance);
                    p.playSound(p.getLocation(), sound, vol * volume, pit);
                }
            }
        }

    }

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit, int maxdistance) {
        Iterator var6 = Bukkit.getOnlinePlayers().iterator();

        while(var6.hasNext()) {
            Player p = (Player)var6.next();
            if (p.getWorld() == loc.getWorld()) {
                double distance = p.getLocation().distance(loc);
                if (distance < (double)maxdistance) {
                    float volume = (float)(1.0D - distance / (double)maxdistance);
                    p.playSound(p.getLocation(), sound, vol * volume, pit);
                }
            }
        }

    }

    public static String locToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    public static String blockToString(Block block) {
        return block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static Block stringToBlock(String s) {
        String[] d = s.split(",");
        return Bukkit.getWorld(d[0]).getBlockAt(Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3]));
    }
}