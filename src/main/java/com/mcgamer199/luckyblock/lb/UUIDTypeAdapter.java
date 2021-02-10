package com.mcgamer199.luckyblock.lb;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDTypeAdapter implements JsonDeserializer<UUID>, JsonSerializer<UUID> {

    @Override
    public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String uuid = json.getAsString();
        if (uuid.contains("-")) {
            return UUID.fromString(uuid);
        } else {
            return UUID.fromString(uuid.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }
    }
}
