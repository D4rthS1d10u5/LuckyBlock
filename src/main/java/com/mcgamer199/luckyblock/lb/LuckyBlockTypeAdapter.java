package com.mcgamer199.luckyblock.lb;

import com.google.gson.*;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.enums.PlacingSource;
import com.mcgamer199.luckyblock.util.JsonUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Type;
import java.util.UUID;

public class LuckyBlockTypeAdapter implements JsonSerializer<LuckyBlock>, JsonDeserializer<LuckyBlock> {

    @Override
    public JsonElement serialize(LuckyBlock luckyBlock, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject serialized = new JsonObject();
        serialized.addProperty("block", LocationUtils.asString(luckyBlock.getLocation()));
        serialized.addProperty("lucky_block_type", luckyBlock.getType().getId());
        serialized.addProperty("luck", luckyBlock.getLuck());

        if(luckyBlock.getCustomDrop() != null) {
            serialized.addProperty("custom_drop", luckyBlock.getCustomDrop().getName());
        } else if(luckyBlock.getLuckyBlockDrop() != null) {
            serialized.addProperty("drop", luckyBlock.getLuckyBlockDrop().name());
        }

        serialized.addProperty("ticks_delay", luckyBlock.getTickDelay());

        if(luckyBlock.hasOwner()) {
            serialized.addProperty("owner", luckyBlock.getOwner().toString());
        }

        if(luckyBlock.getWhoPlaced() != null) {
            JsonObject placedBy = new JsonObject();
            placedBy.addProperty("type", luckyBlock.getPlacingSource().name());
            placedBy.addProperty("value", luckyBlock.getPlacedBy());
            serialized.add("placed_by", placedBy);
        }

        serialized.addProperty("facing", luckyBlock.getFacing().name());

        serialized.addProperty("locked", luckyBlock.isLocked());

        if(!luckyBlock.getDropOptions().isEmpty()) {
            serialized.add("options", luckyBlock.getDropOptions().getJsonParams());
        }

        return serialized;
    }

    @Override
    public LuckyBlock deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement.isJsonObject()) {
            JsonObject serialized = jsonElement.getAsJsonObject();
            String blockData = serialized.get("block").getAsString();
            int luckyBlockTypeId = serialized.get("lucky_block_type").getAsInt();
            LBType lbType = LBType.fromId(luckyBlockTypeId);

            Block block = LocationUtils.blockFromString(blockData);
            if((block == null || block.getType().equals(Material.AIR)) || lbType == null) {
                System.out.println(String.format("block = %s type = %s lbType = %s", block, block != null ? block.getType() : Material.BEDROCK, lbType));
                return null;
            }

            int luck = serialized.get("luck").getAsInt();

            String dropName = null;
            if(serialized.has("drop")) {
                dropName = serialized.get("drop").getAsString();
            } else if(serialized.has("custom_drop")) {
                dropName = serialized.get("custom_drop").getAsString();
            }

            int ticksDelay = serialized.get("ticks_delay").getAsInt();

            UUID ownerUuid = null;
            if(serialized.has("owner")) {
                ownerUuid = JsonUtils.deserialize(serialized.get("owner"), UUID.class);
            }

            Pair<PlacingSource, String> placedBy = null;
            if(serialized.has("placed_by")) {
                JsonObject placed = serialized.getAsJsonObject("placed_by");
                PlacingSource placingSource = PlacingSource.getByName(placed.get("type").getAsString());
                String value = placed.get("value").getAsString();
                placedBy = Pair.of(placingSource, value);
            }

            String facingName = serialized.get("facing").getAsString();
            boolean locked = serialized.get("locked").getAsBoolean();

            Properties options = null;
            if(serialized.has("options")) {
                options = new Properties(serialized.get("options").getAsJsonObject());
            }

            LuckyBlock luckyBlock = new LuckyBlock(lbType, block, luck, placedBy, false, false);
            luckyBlock.setOwner(ownerUuid);

            if(locked) {
                luckyBlock.lock();
            }

            luckyBlock.setFacing(BlockFace.valueOf(facingName));
            luckyBlock.setTickDelay(ticksDelay);

            if(LuckyBlockDrop.isValid(dropName)) {
                luckyBlock.setDrop(LuckyBlockDrop.getByName(dropName), false, false);
            } else if(CustomDropManager.isValid(dropName)) {
                luckyBlock.setCustomDrop(CustomDropManager.getByName(dropName));
                luckyBlock.refreshCustomDrop();
            }

            if(options != null) {
                luckyBlock.getDropOptions().merge(options, true);
            }

            return luckyBlock;
        } else {
            System.out.println("json is not obj");
        }
        return null;
    }
}
