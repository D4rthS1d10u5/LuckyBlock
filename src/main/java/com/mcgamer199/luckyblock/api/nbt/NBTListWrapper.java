package com.mcgamer199.luckyblock.api.nbt;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

public abstract class NBTListWrapper<B> {

    @Getter
    protected Object list;

    public NBTListWrapper(@Nullable Object list) {
        if(list == null) {
            list = createEmptyList();
        }

        if(!list.getClass().getName().endsWith("NBTTagList")) {
            throw new IllegalArgumentException(String.format("Предоставленный объект %s не является NBT списком", list));
        }

        this.list = list;
    }

    public NBTListWrapper() {
        this.list = createEmptyList();
    }

    public abstract Object createEmptyList();

    public abstract int size();

    public abstract <R> Collection<R> transform(Function<B, R> nbtMapper);

    public abstract void remove(int index);

    public abstract void add(String value);

    public abstract void add(byte value);

    public abstract void add(short value);

    public abstract void add(int value);

    public abstract void add(long value);

    public abstract void add(float value);

    public abstract void add(double value);

    public abstract void add(byte[] value);

    public abstract void add(NBTListWrapper<B> value);

    public abstract void add(NBTCompoundWrapper<?> value);

    public abstract void set(int index, String value);

    public abstract void set(int index, byte value);

    public abstract void set(int index, short value);

    public abstract void set(int index, int value);

    public abstract void set(int index, long value);

    public abstract void set(int index, float value);

    public abstract void set(int index, double value);

    public abstract void set(int index, byte[] value);

    public abstract void set(int index, NBTListWrapper<B> value);

    public abstract void set(int index, NBTCompoundWrapper<?> value);

    public abstract String getString(int index);

    public abstract byte getByte(int index);

    public abstract short getShort(int index);

    public abstract int getInt(int index);

    public abstract long getLong(int index);

    public abstract float getFloat(int index);

    public abstract double getDouble(int index);

    public abstract byte[] getByteArray(int index);

    public abstract NBTListWrapper<B> getList(int index);

    public abstract NBTCompoundWrapper<?> getCompound(int index);
}

