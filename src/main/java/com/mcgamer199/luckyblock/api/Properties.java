package com.mcgamer199.luckyblock.api;

import com.google.gson.*;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Properties {

    public static final Pattern INT        = Pattern.compile("-?[1-9]+[0-9]*");
    public static final Pattern FLOAT      = Pattern.compile("-?[0-9]+\\.?[0-9]*");
    public static final Pattern BOOLEAN    = Pattern.compile("(?i)true|false|on|off|yes|no");
    private static final JsonParser jsonParser = new JsonParser();
    private JsonObject jsonParams;

    public Properties() {
        this.jsonParams = new JsonObject();
    }

    public Properties(String key, String value) {
        this();
        this.jsonParams.addProperty(key, value);
    }

    public Properties(String param) {
        try {
            this.jsonParams = (JsonObject) jsonParser.parse(param);
        } catch(JsonSyntaxException ex) {
            this.jsonParams = new JsonObject();
            System.out.println("Ошибка парсинга параметров: `" + param + "`");
            ex.printStackTrace();
        }
    }

    public Properties(Map<String, String> params) {
        this.jsonParams = new JsonObject();
        params.forEach(this.jsonParams::addProperty);
    }

    public Properties(JsonObject jsonParams) {
        this.jsonParams = jsonParams;
    }

    public JsonObject getJsonParams() {
        return jsonParams;
    }

    public boolean has(String key) {
        return jsonParams.has(key);
    }

    public int size() {
        return jsonParams.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<String> keys() {
        return jsonParams.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public Properties merge(Properties properties, boolean rewriteValuesIfPresent) {
        if(properties != null) {
            Set<Map.Entry<String, JsonElement>> entries = properties.getJsonParams().entrySet();
            entries.forEach(sourceEntry -> {
                if(!getJsonParams().has(sourceEntry.getKey()) || rewriteValuesIfPresent) {
                    getJsonParams().add(sourceEntry.getKey(), sourceEntry.getValue());
                }
            });
        }

        return this;
    }

    public void clear() {
        jsonParams.entrySet().clear();
    }

    public void remove(String key) {
        jsonParams.remove(key);
    }

    public String toString(JsonElement element) {
        if(element == null || element.isJsonNull()) {
            return "null";
        }

        if(element.isJsonPrimitive()) {
            return element.toString();
        } else if(element.isJsonArray()) {
            StringBuilder array = new StringBuilder();
            element.getAsJsonArray().forEach(jsonElement -> array.append(toString(jsonElement)).append(", "));
            return array.toString();
        } else if(element.isJsonObject()) {
            StringBuilder object = new StringBuilder();
            element.getAsJsonObject().entrySet().forEach(entry -> object.append(String.format("%s: %s", entry.getKey(), toString(entry.getValue()))));
            return object.toString();
        }

        return "";
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

    public String[] getStringArray(String key) {
        return getStringArray(key, new String[0]);
    }

    public Properties putStringArray(String key, String[] value) {
        JsonArray jsonArray = new JsonArray();
        for(String s : value) {
            jsonArray.add(s);
        }
        jsonParams.add(key, jsonArray);

        return this;
    }

    public Short[] getShortArray(String key, Short[] defaultValue) {
        if(!jsonParams.has(key)) { return defaultValue; }
        JsonArray jsonArray = jsonParams.get(key).getAsJsonArray();
        Short[] array = new Short[jsonArray.size()];
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

    public Integer[] getIntArray(String key, Integer[] defParam) {
        if(!jsonParams.has(key)) { return defParam; }
        JsonArray jsonArray = jsonParams.get(key).getAsJsonArray();
        Integer[] array = new Integer[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).getAsInt();
        }
        return array;
    }

    public Integer[] getIntArray(String key) {
        return getIntArray(key, new Integer[0]);
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

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType, T defaultValue) {
        String enumName = getString(key);
        return enumName == null ? defaultValue : Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(enumName))
                .findFirst()
                .orElse(defaultValue);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        String enumName = getString(key);
        return enumName == null ? null : Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(enumName))
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
