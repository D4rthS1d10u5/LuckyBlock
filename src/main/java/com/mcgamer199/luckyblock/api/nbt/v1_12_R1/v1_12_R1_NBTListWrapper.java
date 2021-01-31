package com.mcgamer199.luckyblock.api.nbt.v1_12_R1;

import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.api.nbt.NBTListWrapper;
import net.minecraft.server.v1_12_R1.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class v1_12_R1_NBTListWrapper<T extends NBTBase> extends NBTListWrapper<T> {

    public v1_12_R1_NBTListWrapper(NBTTagList list) {
        super(list);
    }

    public v1_12_R1_NBTListWrapper() {
        super();
    }

    @Override
    public NBTTagList createEmptyList(){
        return new NBTTagList();
    }

    @Override
    public int size(){
        return ((NBTTagList) list).size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Collection<R> transform(Function<T, R> nbtMapper) {
        return ((NBTTagList) list).list.stream()
                .map(base -> nbtMapper.apply((T) base))
                .collect(Collectors.toList());
    }

    @Override
    public void remove(int index) {
        ((NBTTagList) list).remove(index);
    }

    @Override
    public void add(String value){
        ((NBTTagList) list).add(new NBTTagString(value));
    }

    @Override
    public void add(byte value){
        ((NBTTagList) list).add(new NBTTagByte(value));
    }

    @Override
    public void add(short value){
        ((NBTTagList) list).add(new NBTTagShort(value));
    }

    @Override
    public void add(int value){
        ((NBTTagList) list).add(new NBTTagInt(value));
    }

    @Override
    public void add(long value){
        ((NBTTagList) list).add(new NBTTagLong(value));
    }

    @Override
    public void add(float value){
        ((NBTTagList) list).add(new NBTTagFloat(value));
    }

    @Override
    public void add(double value){
        ((NBTTagList) list).add(new NBTTagDouble(value));
    }

    @Override
    public void add(byte[] value){
        ((NBTTagList) list).add(new NBTTagByteArray(value));
    }

    @Override
    public void add(NBTListWrapper<T> value){
        ((NBTTagList) list).add((NBTTagList) value.getList());
    }

    @Override
    public void add(NBTCompoundWrapper<?> value){
        ((NBTTagList) list).add((NBTTagCompound) value.getTag());
    }

    @Override
    public void set(int index, String value){
        ((NBTTagList) list).a(index, new NBTTagString(value));
    }

    @Override
    public void set(int index, byte value){
        ((NBTTagList) list).a(index, new NBTTagByte(value));
    }

    @Override
    public void set(int index, short value){
        ((NBTTagList) list).a(index, new NBTTagShort(value));
    }

    @Override
    public void set(int index, int value){
        ((NBTTagList) list).a(index, new NBTTagInt(value));
    }

    @Override
    public void set(int index, long value){
        ((NBTTagList) list).a(index, new NBTTagLong(value));
    }

    @Override
    public void set(int index, float value){
        ((NBTTagList) list).a(index, new NBTTagFloat(value));
    }

    @Override
    public void set(int index, double value){
        ((NBTTagList) list).a(index, new NBTTagDouble(value));
    }

    @Override
    public void set(int index, byte[] value){
        ((NBTTagList) list).a(index, new NBTTagByteArray(value));
    }

    @Override
    public void set(int index, NBTListWrapper<T> value){
        ((NBTTagList) list).a(index, (NBTTagList) value.getList());
    }

    @Override
    public void set(int index, NBTCompoundWrapper<?> value){
        ((NBTTagList) list).a(index, (NBTTagCompound) value.getTag());
    }

    @Override
    public String getString(int index){
        return ((NBTTagList) list).getString(index);
    }

    @Override
    public byte getByte(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagByte) {
            return ((NBTTagByte) base).g();
        }
        return 0;
    }

    @Override
    public short getShort(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagShort) {
            return ((NBTTagShort) base).f();
        }
        return 0;
    }

    @Override
    public int getInt(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagInt) {
            return ((NBTTagInt) base).e();
        }
        return 0;
    }

    @Override
    public long getLong(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagLong) {
            return ((NBTTagLong) base).d();
        }
        return 0;
    }

    @Override
    public float getFloat(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagFloat) {
            return ((NBTTagFloat) base).i();
        }
        return 0;
    }

    @Override
    public double getDouble(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagDouble) {
            return ((NBTTagDouble) base).asDouble();
        }
        return 0;
    }

    @Override
    public byte[] getByteArray(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagByteArray) {
            return ((NBTTagByteArray) base).c();
        }
        return new byte[0];
    }

    @Override
    public v1_12_R1_NBTListWrapper<T> getList(int index) {
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagList) {
            return new v1_12_R1_NBTListWrapper<>((NBTTagList) base);
        }
        return new v1_12_R1_NBTListWrapper<>();
    }

    @Override
    public NBTCompoundWrapper<?> getCompound(int index){
        NBTBase base = ((NBTTagList) list).i(index);
        if(base instanceof NBTTagCompound) {
            return new v1_12_R1_NBTCompoundWrapper((NBTTagCompound) base);
        }
        return new v1_12_R1_NBTCompoundWrapper();
    }
}
