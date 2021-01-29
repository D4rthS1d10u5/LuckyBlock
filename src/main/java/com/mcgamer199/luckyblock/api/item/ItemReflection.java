package com.mcgamer199.luckyblock.api.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class ItemReflection {
    static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public ItemReflection() {
    }

    private static Class getCraftItemStack() {
        try {
            Class c = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            return c;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    static Class getNBTBase() {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTBase");
            return c;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTag() {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            return c.newInstance();
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagString(String value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagString");
            return c.getConstructor(String.class).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagInt(int value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagInt");
            return c.getConstructor(Integer.TYPE).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagDouble(double value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagDouble");
            return c.getConstructor(Double.TYPE).newInstance(value);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagShort(short value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagShort");
            return c.getConstructor(Short.TYPE).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagLong(long value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagLong");
            return c.getConstructor(Long.TYPE).newInstance(value);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagFloat(float value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagFloat");
            return c.getConstructor(Float.TYPE).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagByte(byte value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagByte");
            return c.getConstructor(Byte.TYPE).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagIntArray(int[] value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagIntArray");
            return c.getConstructor(int[].class).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static Object getNewNBTTagByteArray(byte[] value) {
        try {
            Class c = Class.forName("net.minecraft.server." + version + ".NBTTagByteArray");
            return c.getConstructor(byte[].class).newInstance(value);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static Object setNBTTag(Object NBTTag, Object NMSItem) {
        try {
            Method method = NMSItem.getClass().getMethod("setTag", NBTTag.getClass());
            method.invoke(NMSItem, NBTTag);
            return NMSItem;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    static Object getNMSItemStack(ItemStack item) {
        Class cis = getCraftItemStack();

        try {
            Method method = cis.getMethod("asNMSCopy", ItemStack.class);
            Object answer = method.invoke(cis, item);
            return answer;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static ItemStack getBukkitItemStack(Object item) {
        Class cis = getCraftItemStack();

        try {
            Method method = cis.getMethod("asCraftMirror", item.getClass());
            Object answer = method.invoke(cis, item);
            return (ItemStack) answer;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static Object getNBTTagCompound(Object nmsitem) {
        Class c = nmsitem.getClass();

        try {
            Method method = c.getMethod("getTag");
            Object answer = method.invoke(nmsitem);
            return answer;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    static Object setNBTValue(Object nbt, String key, Object value) {
        try {
            Method method = nbt.getClass().getMethod("set", String.class, getNBTBase());
            method.invoke(nbt, key, value);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return nbt;
    }

    static ItemStack set(ItemStack item, String key, Object nbt) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("set", String.class, getNBTBase());
                method.invoke(nbttag, key, nbt);
                nmsitem = setNBTTag(nbttag, nmsitem);
                return getBukkitItemStack(nmsitem);
            } catch (Exception var7) {
                var7.printStackTrace();
                return item;
            }
        }
    }

    static ItemStack setString(ItemStack item, String key, String Text) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("setString", String.class, String.class);
                method.invoke(nbttag, key, Text);
                nmsitem = setNBTTag(nbttag, nmsitem);
                return getBukkitItemStack(nmsitem);
            } catch (Exception var7) {
                var7.printStackTrace();
                return item;
            }
        }
    }

    public static String getString(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("getString", String.class);
                return (String) method.invoke(nbttag, key);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    static ItemStack setInt(ItemStack item, String key, Integer i) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("setInt", String.class, Integer.TYPE);
                method.invoke(nbttag, key, i);
                nmsitem = setNBTTag(nbttag, nmsitem);
                return getBukkitItemStack(nmsitem);
            } catch (Exception var7) {
                var7.printStackTrace();
                return item;
            }
        }
    }

    public static Integer getInt(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("getInt", String.class);
                return (Integer) method.invoke(nbttag, key);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    static ItemStack setDouble(ItemStack item, String key, Double d) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("setDouble", String.class, Double.TYPE);
                method.invoke(nbttag, key, d);
                nmsitem = setNBTTag(nbttag, nmsitem);
                return getBukkitItemStack(nmsitem);
            } catch (Exception var7) {
                var7.printStackTrace();
                return item;
            }
        }
    }

    public static Double getDouble(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("getDouble", String.class);
                return (Double) method.invoke(nbttag, key);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    public static ItemStack setBoolean(ItemStack item, String key, Boolean d) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("setBoolean", String.class, Boolean.TYPE);
                method.invoke(nbttag, key, d);
                nmsitem = setNBTTag(nbttag, nmsitem);
                return getBukkitItemStack(nmsitem);
            } catch (Exception var7) {
                var7.printStackTrace();
                return item;
            }
        }
    }

    public static Boolean getBoolean(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("getBoolean", String.class);
                return (Boolean) method.invoke(nbttag, key);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    public static Boolean hasKey(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        } else {
            Object nbttag = getNBTTagCompound(nmsitem);
            if (nbttag == null) {
                nbttag = getNewNBTTag();
            }

            try {
                Method method = nbttag.getClass().getMethod("hasKey", String.class);
                return (Boolean) method.invoke(nbttag, key);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }
}

