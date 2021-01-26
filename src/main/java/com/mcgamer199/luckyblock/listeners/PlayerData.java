package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {

    private static Plugin plugin;

    public PlayerData() {
    }

    public static <V> V getOrSet(Player player, String key, V def) {
        return (V) getMetadata(player).computeIfAbsent(key, (s) -> def);
    }

    public static <V> V set(Player player, String key, V val) {
        return (V) getMetadata(player).put(key, val);
    }

    public static Map<String, Object> getMetadata(Player player) {
        List<MetadataValue> luckydata = player.getMetadata("luckydata");
        if (!luckydata.isEmpty()) {
            return (Map)((MetadataValue)luckydata.get(0)).value();
        } else {
            Map<String, Object> result = new HashMap(8);
            player.setMetadata("luckydata", new FixedMetadataValue(plugin, result));
            return result;
        }
    }

    static {
        plugin = LuckyBlock.instance;
    }
}
