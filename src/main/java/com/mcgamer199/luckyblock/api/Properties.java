package com.mcgamer199.luckyblock.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class Properties {

    private final static Pattern INT        = Pattern.compile("-?[1-9]+[0-9]*");
    private final static Pattern FLOAT      = Pattern.compile("-?[0-9]+\\.?[0-9]*");
    private final static Pattern BOOLEAN    = Pattern.compile("(?i)true|on|yes");
    private static final JsonParser jsonParser = new JsonParser();
    private JsonObject jsonParams;

    public Properties(String param) {
        try {
            this.jsonParams = (JsonObject) jsonParser.parse(param);
        } catch(JsonSyntaxException ex) {
            this.jsonParams = new JsonObject();
            System.out.println("Ошибка парсинга параметров: `" + param + "`");
            ex.printStackTrace();
        }
    }

    public Properties(String key, String value) {
        this();
        this.jsonParams.addProperty(key, value);
    }

    public Properties(Map<String, String> params) {
        this.jsonParams = new JsonObject();
        params.forEach(this.jsonParams::addProperty);
    }

    public Properties() {
        this.jsonParams = new JsonObject();
    }

    public JsonObject getJsonParams() {
        return jsonParams;
    }

    public void remove(String key) {
        jsonParams.remove(key);
    }

    public String getString(String key, String defParam) {
        if(!jsonParams.has(key)) { return defParam; }
        return jsonParams.get(key).getAsString();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public Properties putString(String key, String value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public byte getByte(String key, byte defaultValue) {
        if(jsonParams.has(key)) {
            String str = jsonParams.get(key).getAsString();
            if(INT.matcher(str).matches()) {
                return Byte.parseByte(str);
            }
        }
        return defaultValue;
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public Properties putByte(String key, byte value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public short getShort(String key, short defaultValue) {
        if(jsonParams.has(key)) {
            String str = jsonParams.get(key).getAsString();
            if(INT.matcher(str).matches()) {
                return Short.parseShort(str);
            }
        }
        return defaultValue;
    }

    public short getShort(String key) {
        return getShort(key,  (short) 0);
    }

    public Properties putShort(String key, short value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public int getInt(String key, int defParam) {
        if(!jsonParams.has(key)) { return defParam; }
        String str = jsonParams.get(key).getAsString();
        if(!INT.matcher(str).matches()) { return defParam; }
        return Integer.parseInt(str);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public Properties putInt(String key, int value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public double getDouble(String key, double defaultValue) {
        if(jsonParams.has(key)) {
            String str = jsonParams.get(key).getAsString();
            if(FLOAT.matcher(str).matches()) {
                return Double.parseDouble(str);
            }
        }

        return defaultValue;
    }

    public double getDouble(String key) {
        return getDouble(key, 0D);
    }

    public float getFloat(String key, float defaultValue) {
        if(jsonParams.has(key)) {
            String str = jsonParams.get(key).getAsString();
            if(FLOAT.matcher(str).matches()) {
                return Float.parseFloat(str);
            }
        }

        return defaultValue;
    }

    public float getFloat(String key) {
        return getFloat(key, 0F);
    }

    public Properties putFloat(String key, float value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public Properties putDouble(String key, double value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public boolean getBoolean(String key, boolean def) {
        if(!jsonParams.has(key)) { return def; }
        return jsonParams.get(key).getAsBoolean();
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Properties putBoolean(String key, boolean value) {
        jsonParams.addProperty(key, value);
        return this;
    }

    public String[] getStringArray(String key, String[] defParam) {
        if(!jsonParams.has(key)) { return defParam; }
        JsonArray jsonArray = jsonParams.get(key).getAsJsonArray();
        String[] array = new String[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).getAsString();
        }
        return array;
    }

    public Properties putStringArray(String key, String[] value) {
        JsonArray jsonArray = new JsonArray();
        for(String s : value) {
            jsonArray.add(s);
        }
        jsonParams.add(key, jsonArray);

        return this;
    }

    public short[] getShortArray(String key, short[] defaultValue) {
        if(!jsonParams.has(key)) { return defaultValue; }
        JsonArray jsonArray = jsonParams.get(key).getAsJsonArray();
        short[] array = new short[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).getAsShort();
        }
        return array;
    }

    public Properties putShortArray(String key, Short[] value) {
        JsonArray jsonArray = new JsonArray();
        for(short i : value) {
            jsonArray.add(i);
        }
        jsonParams.add(key, jsonArray);

        return this;
    }

    public int[] getIntArray(String key, int[] defParam) {
        if(!jsonParams.has(key)) { return defParam; }
        JsonArray jsonArray = jsonParams.get(key).getAsJsonArray();
        int[] array = new int[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).getAsInt();
        }
        return array;
    }

    public Properties putIntArray(String key, Integer[] value) {
        JsonArray jsonArray = new JsonArray();
        for(int i : value) {
            jsonArray.add(i);
        }
        jsonParams.add(key, jsonArray);

        return this;
    }

    public ItemStack[] getItemStackArray(String key, ItemStack[] defParam) {
        String[] strings = getStringArray(key, ItemStackUtils.serializeArray(defParam));
        if(strings.length != 0) {
            return ItemStackUtils.deserializeArray(strings);
        } else {
            return defParam;
        }
    }

    public Properties putItemStackArray(String key, ItemStack[] value) {
        putStringArray(key, ItemStackUtils.serializeArray(value));
        return this;
    }

    public ItemStack getItemStack(String key, ItemStack defParam) {
        String string = getString(key, ItemStackUtils.serialize(defParam));
        if(!string.isEmpty()) {
            return ItemStackUtils.deserialize(string);
        } else {
            return defParam;
        }
    }

    public Properties putItemStack(String key, ItemStack value) {
        jsonParams.addProperty(key, ItemStackUtils.serialize(value));
        return this;
    }

    public Location getLocation(String key, Location def) {
        String string = getString(key, LocationUtils.asString(def));
        if(!string.isEmpty()) {
            return LocationUtils.locationFromString(string);
        } else {
            return def;
        }
    }

    public Properties putLocation(String key, Location value) {
        putString(key, LocationUtils.asString(value));
        return this;
    }

    public <T extends Enum<T>> Enum<T> getEnum(String key, Class<T> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(key))
                .findFirst()
                .orElse(null);
    }

    public Properties putEnum(String key, Enum<?> value) {
        putString(key, value.name());
        return this;
    }

    public String toString() {
        return this.jsonParams.toString();
    }
}
