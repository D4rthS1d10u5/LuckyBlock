package com.mcgamer199.luckyblock.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockTypeAdapter;
import com.mcgamer199.luckyblock.lb.UUIDTypeAdapter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class JsonUtils {

    private static final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(LuckyBlock.class, new LuckyBlockTypeAdapter());
    private static final Gson gson = builder.create();

    @SneakyThrows
    public static <T> T deserialize(String json, Class<T> typeClass) {
        return gson.getAdapter(typeClass).fromJson(json);
    }

    public static <T> String toJsonString(T object, Class<T> typeClass) {
        return gson.getAdapter(typeClass).toJson(object);
    }

    public static <T> T deserialize(JsonElement element, Class<T> typeClass) {
        return gson.getAdapter(typeClass).fromJsonTree(element);
    }

    public static <T> JsonElement serialize(T object, Class<T> typeClass) {
        return gson.getAdapter(typeClass).toJsonTree(object);
    }
}
