package com.mcgamer199.luckyblock.api.enums;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mcgamer199.luckyblock.util.JsonUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public enum PlacingSource {

    PLAYER {
        @Override
        public String getDescription() {
            return "Player with name";
        }

        @Override
        public String fromObject(@NotNull Object object) {
            if(!(object instanceof Player)) {
                return null;
            }

            return ((Player) object).getName();
        }

        @Override
        public String read(@NotNull JsonElement element) {
            return element.getAsString();
        }

        @Override
        public JsonElement write(@NotNull Object object) {
            if(!(object instanceof Player)) {
                return null;
            }

            return new JsonPrimitive(((Player) object).getName());
        }
    }, ENTITY {
        @Override
        public String getDescription() {
            return "Entity with uuid";
        }

        @Override
        public String fromObject(@NotNull Object object) {
            if(!(object instanceof Entity)) {
                return null;
            }

            return ((Entity) object).getUniqueId().toString();
        }

        @Override
        public String read(@NotNull JsonElement element) {
            return element.getAsString();
        }

        @Override
        public JsonElement write(@NotNull Object object) {
            if(!(object instanceof Entity)) {
                return null;
            }

            return JsonUtils.serialize(((Entity) object).getUniqueId(), UUID.class);
        }
    }, BLOCK {
        @Override
        public String getDescription() {
            return "Block at coordinates";
        }

        @Override
        public String fromObject(@NotNull Object object) {
            if(!(object instanceof Block)) {
                return null;
            }

            return LocationUtils.asString(((Block) object).getLocation());
        }

        @Override
        public String read(@NotNull JsonElement element) {
            return String.format("[%s]", element.getAsString());
        }

        @Override
        public JsonElement write(@NotNull Object object) {
            if(!(object instanceof Block)) {
                return null;
            }

            return new JsonPrimitive(LocationUtils.asString(((Block) object).getLocation()));
        }
    },
    NONE {
        @Override
        public String getDescription() {
            return "NONE";
        }

        @Override
        public String fromObject(@NotNull Object object) {
            return "NONE";
        }

        @Override
        public String read(@NotNull JsonElement element) {
            return "NONE";
        }

        @Override
        public JsonElement write(@NotNull Object object) {
            return JsonNull.INSTANCE;
        }
    };

    private static final PlacingSource[] values = values();

    PlacingSource() {}

    public abstract String getDescription();

    public abstract String fromObject(@NotNull Object object);

    public abstract String read(@NotNull JsonElement element);

    public abstract JsonElement write(@NotNull Object object);

    public static PlacingSource getByName(String name) {
        for (PlacingSource value : values) {
            if(value.name().equals(name.toLowerCase())) {
                return value;
            }
        }

        return NONE;
    }

    public static PlacingSource define(Object type) {
        PlacingSource source;
        if(type instanceof Player) {
            source = PLAYER;
        } else if(type instanceof Entity) {
            source = ENTITY;
        } else if(type instanceof Block) {
            source = BLOCK;
        } else {
            source = NONE;
        }

        return source;
    }
}
