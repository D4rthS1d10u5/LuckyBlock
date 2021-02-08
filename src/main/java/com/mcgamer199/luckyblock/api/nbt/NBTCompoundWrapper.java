package com.mcgamer199.luckyblock.api.nbt;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sk89q.jnbt.*;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class NBTCompoundWrapper<T> {

    public static final BiMap<Class<?>, Integer> NBT_TYPES = HashBiMap.create();

    static {
        NBT_TYPES.put(Void.class, 0);
        NBT_TYPES.put(Byte.class, 1);
        NBT_TYPES.put(Short.class, 2);
        NBT_TYPES.put(Integer.class, 3);
        NBT_TYPES.put(Long.class, 4);
        NBT_TYPES.put(Float.class, 5);
        NBT_TYPES.put(Double.class, 6);
        NBT_TYPES.put(byte[].class, 7);
        NBT_TYPES.put(String.class, 8);
        NBT_TYPES.put(List.class, 9);
        NBT_TYPES.put(Map.class, 10);
        NBT_TYPES.put(int[].class, 11);
        NBT_TYPES.put(long[].class, 12);
    }

    @Getter
    protected T tag;

    public NBTCompoundWrapper(T tag) {
        this.tag = tag;
    }

    public NBTCompoundWrapper() {
        this.tag = createEmptyTag();
    }

    public abstract boolean isEmpty();

    public abstract T createEmptyTag();

    public abstract NBTCompoundWrapper<T> newCompound();

    public abstract NBTListWrapper<?> newList();

    public abstract boolean hasKey(String key);

    public abstract boolean hasKeyOfType(String key, int tagType);

    public boolean hasKeyOfType(String key, Class<?> typeClass) {
        return hasKeyOfType(key, NBT_TYPES.get(typeClass));
    }

    public abstract NBTCompoundWrapper<T> setString(String key, String value);

    public abstract String getString(String key);

    public abstract NBTCompoundWrapper<T> setByte(String key, byte value);

    public abstract byte getByte(String key);

    public abstract NBTCompoundWrapper<T> setShort(String key, short value);

    public abstract short getShort(String key);

    public abstract NBTCompoundWrapper<T> setInt(String key, int value);

    public abstract int getInt(String key);

    public abstract NBTCompoundWrapper<T> setIntArray(String key, int[] array);

    public abstract int[] getIntArray(String key);

    public abstract NBTCompoundWrapper<T> setLong(String key, long value);

    public abstract long getLong(String key);

    public abstract NBTCompoundWrapper<T> setLongArray(String key, long[] array);

    public abstract long[] getLongArray(String key);

    public abstract NBTCompoundWrapper<T> setFloat(String key, float value);

    public abstract float getFloat(String key);

    public abstract NBTCompoundWrapper<T> setDouble(String key, double value);

    public abstract double getDouble(String key);

    public abstract NBTCompoundWrapper<T> setCompound(String key, NBTCompoundWrapper<?> compoundMap);

    public abstract NBTCompoundWrapper<T> getCompound(String key);

    public abstract NBTCompoundWrapper<T> setList(String key, NBTListWrapper<?> list);

    public abstract NBTListWrapper<?> getList(String key, int tagType);

    public abstract NBTListWrapper<?> getList(String key, Class<?> typeClass);

    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    public NBTCompoundWrapper<T> setBoolean(String key, boolean flag) {
        setByte(key, (byte) (flag ? 1 : 0));
        return this;
    }

    /**
     * Преобразовать объект Java в связанный с ним NBT тип
     * @param <B> - базовый класс NBT
     * @param type - исходный тип объекта
     * @return NBT тег, связанный с текущим используемым ядром или NBT end (в том случае, если тип данных не связан с NBT)
     */
    protected abstract <B> B transform(Object type);

    public abstract Tag asWorldEditTag(Object nbtBase);

    public Class<? extends Tag> defineTagClass(Object nbtBase) {
        switch(nbtBase.getClass().getSimpleName()) {
            case "NBTTagCompound": return CompoundTag.class;
            case "NBTTagList": return ListTag.class;
            case "NBTTagString": return StringTag.class;
            case "NBTTagByte": return ByteTag.class;
            case "NBTTagShort": return ShortTag.class;
            case "NBTTagInt": return IntTag.class;
            case "NBTTagLong": return LongTag.class;
            case "NBTTagFloat": return FloatTag.class;
            case "NBTTagDouble": return DoubleTag.class;
            case "NBTTagIntArray": return IntArrayTag.class;
            default: return EndTag.class;
        }
    }

    public abstract String asString();

    public abstract String toJson();
}