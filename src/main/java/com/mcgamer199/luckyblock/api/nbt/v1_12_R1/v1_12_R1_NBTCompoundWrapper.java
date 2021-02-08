package com.mcgamer199.luckyblock.api.nbt.v1_12_R1;

import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.api.nbt.NBTListWrapper;
import com.mcgamer199.luckyblock.util.MyObject;
import com.sk89q.jnbt.*;
import net.minecraft.server.v1_12_R1.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class v1_12_R1_NBTCompoundWrapper extends NBTCompoundWrapper<NBTTagCompound> {

    public v1_12_R1_NBTCompoundWrapper(NBTTagCompound serverTag) {
        super(serverTag);
    }

    public v1_12_R1_NBTCompoundWrapper() {
        super();
    }

    @Override
    public NBTTagCompound createEmptyTag() {
        return new NBTTagCompound();
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> newCompound() {
        return new v1_12_R1_NBTCompoundWrapper();
    }

    @Override
    public NBTListWrapper<?> newList() {
        return new v1_12_R1_NBTListWrapper<>();
    }

    @Override
    public boolean isEmpty(){
        return tag.isEmpty();
    }

    @Override
    public boolean hasKey(String key){
        return tag.hasKey(key);
    }

    @Override
    public boolean hasKeyOfType(String key, int tagType){
        return tag.hasKeyOfType(key, tagType);
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setString(String key, String value){
        tag.setString(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setByte(String key, byte value){
        tag.setByte(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setShort(String key, short value){
        tag.setShort(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setInt(String key, int value){
        tag.setInt(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setIntArray(String key, int[] array){
        tag.setIntArray(key, array);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setLong(String key, long value){
        tag.setLong(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setLongArray(String key, long[] array){
        tag.set(key, new NBTTagLongArray(array));
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setFloat(String key, float value){
        tag.setFloat(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setDouble(String key, double value){
        tag.setDouble(key, value);
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setCompound(String key, NBTCompoundWrapper<?> compoundMap) {
        tag.set(key, transform(compoundMap));
        return this;
    }

    @Override
    public NBTCompoundWrapper<NBTTagCompound> setList(String key, NBTListWrapper<?> tags) {
        tag.set(key, transform(tags));
        return this;
    }

    @Override
    public String getString(String key){
        return tag.getString(key);
    }

    @Override
    public byte getByte(String key){
        return tag.getByte(key);
    }

    @Override
    public short getShort(String key){
        return tag.getShort(key);
    }

    @Override
    public int getInt(String key){
        return tag.getInt(key);
    }

    @Override
    public int[] getIntArray(String key){
        return tag.getIntArray(key);
    }

    @Override
    public long getLong(String key){
        return tag.getLong(key);
    }

    @Override
    public long[] getLongArray(String key){
        if(tag.hasKeyOfType(key, 12)) {
            return MyObject.wrap(tag.get(key)).getField("b").getObject();
        }

        return new long[0];
    }

    @Override
    public float getFloat(String key){
        return tag.getFloat(key);
    }

    @Override
    public double getDouble(String key){
        return tag.getDouble(key);
    }

    @Override
    public v1_12_R1_NBTCompoundWrapper getCompound(String key) {
        return new v1_12_R1_NBTCompoundWrapper(tag.getCompound(key));
    }

    @Override
    public v1_12_R1_NBTListWrapper<?> getList(String key, int tagType) {
        return new v1_12_R1_NBTListWrapper<>(tag.getList(key, tagType));
    }

    @Override
    public v1_12_R1_NBTListWrapper<?> getList(String key, Class<?> typeClass){
        return new v1_12_R1_NBTListWrapper<>(tag.getList(key, NBT_TYPES.get(typeClass)));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected NBTBase transform(Object type) {
        if(type instanceof v1_12_R1_NBTCompoundWrapper) {
            return ((v1_12_R1_NBTCompoundWrapper) type).getTag();
        } else if(type instanceof v1_12_R1_NBTListWrapper) {
            return (NBTTagList) ((v1_12_R1_NBTListWrapper<?>) type).getList();
        } else if(type instanceof String) {
            return new NBTTagString((String) type);
        } else if(type instanceof Byte) {
            return new NBTTagByte((byte) type);
        } else if(type instanceof Short) {
            return new NBTTagShort((short) type);
        } else if(type instanceof Integer) {
            return new NBTTagInt((int) type);
        } else if(type instanceof Long) {
            return new NBTTagLong((long) type);
        } else if(type instanceof Float) {
            return new NBTTagFloat((float) type);
        } else if(type instanceof Double) {
            return new NBTTagDouble((double) type);
        } else if(type instanceof int[]) {
            return new NBTTagIntArray((int[]) type);
        } else if(type instanceof long[]) {
            return new NBTTagLongArray((long[]) type);
        }

        return MyObject.wrap(NBTTagEnd.class).newInstance().getObject();
    }

    @Override
    public Tag asWorldEditTag(Object nbtBase) {
        if(nbtBase instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbtBase;
            Map<String, Tag> tagMap = new HashMap<>();
            compound.map.forEach((name, tag) -> tagMap.put(name, asWorldEditTag(tag)));
            return new CompoundTag(tagMap);
        } else if(nbtBase instanceof NBTTagList) {
            NBTTagList list = (NBTTagList) nbtBase;
            List<Tag> tags = new ArrayList<>();
            list.list.forEach(tag -> tags.add(asWorldEditTag(tag)));
            return new ListTag(defineTagClass(list.get(0)), tags);
        } else if(nbtBase instanceof NBTTagString) {
            return new StringTag(((NBTTagString) nbtBase).c_());
        } else if(nbtBase instanceof NBTTagByte) {
            return new ByteTag(((NBTTagByte) nbtBase).g());
        } else if(nbtBase instanceof NBTTagShort) {
            return new ShortTag(((NBTTagShort) nbtBase).f());
        } else if(nbtBase instanceof NBTTagInt) {
            return new IntTag(((NBTTagInt) nbtBase).e());
        } else if(nbtBase instanceof NBTTagLong) {
            return new LongTag(((NBTTagLong) nbtBase).d());
        } else if(nbtBase instanceof NBTTagFloat) {
            return new FloatTag(((NBTTagFloat) nbtBase).i());
        } else if(nbtBase instanceof NBTTagDouble) {
            return new DoubleTag(((NBTTagDouble) nbtBase).asDouble());
        } else if(nbtBase instanceof NBTTagIntArray) {
            return new IntArrayTag(((NBTTagIntArray) nbtBase).d());
        } else {
            return new EndTag();
        }
    }

    @Override
    public String asString() {
        return tag.map.entrySet().stream().map(entry -> String.format("§7%s: %s", entry.getKey(), entry.getValue().toString())).collect(Collectors.joining("\n"));
    }

    @Override
    public String toJson() {
        return tag.toString();
    }
}

