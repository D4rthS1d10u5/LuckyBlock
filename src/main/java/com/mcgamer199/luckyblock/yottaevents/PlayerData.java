//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.yottaevents;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PlayerData {
    private static final Plugin plugin;

    static {
        plugin = LuckyBlockPlugin.instance;
    }

    public PlayerData() {
    }

    public static <V> V getOrSet(Player player, String key, V def) {
        return (V) getMetadata(player).computeIfAbsent(key, (s) -> def);
    }

    public static <V> V set(Player player, String key, V val) {
        return (V) getMetadata(player).put(key, val);
    }

    public static <V> V remove(Player player, String key) {
        return (V) getMetadata(player).remove(key);
    }

    public static Map<String, Object> getMetadata(Player player) {
        List<MetadataValue> luckydata = player.getMetadata("luckydata");
        if (!luckydata.isEmpty()) {
            return (Map<String, Object>) luckydata.get(0).value();
        } else {
            Map<String, Object> result = new HashMap<>(8);
            player.setMetadata("luckydata", new FixedMetadataValue(plugin, result));
            return result;
        }
    }
}
