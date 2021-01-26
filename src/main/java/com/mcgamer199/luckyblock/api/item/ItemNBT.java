package com.mcgamer199.luckyblock.api.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemNBT {
    private ItemStack item;

    public ItemNBT(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Object setTag(String key, Object tagCompound, Object nbtBase) {
        return ItemReflection.setNBTValue(tagCompound, key, nbtBase);
    }

    public void set(String key, Object nbt) {
        this.item = ItemReflection.set(this.item, key, nbt);
    }

    public Object getNewNBTTagString(String value) {
        return ItemReflection.getNewNBTTagString(value);
    }

    public Object getNewNBTTagInt(int value) {
        return ItemReflection.getNewNBTTagInt(value);
    }

    public Object getNewNBTTagDouble(double value) {
        return ItemReflection.getNewNBTTagDouble(value);
    }

    public Object getNewNBTTagFloat(float value) {
        return ItemReflection.getNewNBTTagFloat(value);
    }

    public Object getNewNBTTagByte(byte value) {
        return ItemReflection.getNewNBTTagByte(value);
    }

    public Object getNewNBTTagShort(short value) {
        return ItemReflection.getNewNBTTagShort(value);
    }

    public Object getNewNBTTagLong(long value) {
        return ItemReflection.getNewNBTTagLong(value);
    }

    public Object getNewNBTTagIntArray(int[] value) {
        return ItemReflection.getNewNBTTagIntArray(value);
    }

    public Object getNewNBTTagByteArray(byte[] value) {
        return ItemReflection.getNewNBTTagByteArray(value);
    }

    public Object getNewNBT() {
        return ItemReflection.getNewNBTTag();
    }

    public String getStringTag() {
        String s = null;
        Object o = ItemReflection.getNMSItemStack(this.item);

        try {
            Method method = o.getClass().getMethod("getTag");
            s = method.invoke(o.getClass()).toString();
        } catch (Exception var4) {
        }

        return s;
    }

    public static class NBTList {
        List<Object> nbts = new ArrayList();

        public NBTList() {
        }

        public void add(Object base) {
            this.nbts.add(base);
        }

        public Object getNBT() {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

            try {
                Class c = Class.forName("net.minecraft.server." + version + ".NBTTagList");
                Object o = c.newInstance();
                Method m = o.getClass().getMethod("add", ItemReflection.getNBTBase());
                Iterator var6 = this.nbts.iterator();

                while (var6.hasNext()) {
                    Object objs = var6.next();
                    m.invoke(o, objs);
                }

                return o;
            } catch (Exception var7) {
                var7.printStackTrace();
                return null;
            }
        }
    }
}
