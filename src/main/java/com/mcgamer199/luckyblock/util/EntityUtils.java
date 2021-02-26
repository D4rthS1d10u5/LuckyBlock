package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
@UtilityClass
public class EntityUtils {

    private static final Plugin plugin;

    static {
        plugin = LuckyBlockPlugin.instance;
    }

    public static void followEntity(Entity target, LivingEntity entity) {
        followEntity(target.getLocation(), entity, 1.75D);
    }

    public static void followEntity(Entity target, LivingEntity entity, double speed) {
        followEntity(target.getLocation(), entity, speed);
    }

    public static void followEntity(Location target, LivingEntity entity, double speed) {
        try {
            Object handle = entity.getClass().getMethod("getHandle").invoke(entity);
            Object nav = handle.getClass().getMethod("getNavigation").invoke(handle);
            Method method = nav.getClass().getMethod("a", Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
            method.invoke(nav, target.getX(), target.getY(), target.getZ(), speed);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }

    public static <V> V getOrSetMeta(Entity entity, String key, V def) {
        return (V) getMetadata(entity).computeIfAbsent(key, (s) -> def);
    }

    public static <V> V getOrDefault(Entity entity, String key, V def) {
        return (V) getMetadata(entity).getOrDefault(key, def);
    }

    public static <V> V setMetadata(Entity entity, String key, V val) {
        return (V) getMetadata(entity).put(key, val);
    }

    public static <V> V removeMetadata(Entity entity, String key) {
        return (V) getMetadata(entity).remove(key);
    }

    public static Map<String, Object> getMetadata(Entity entity) {
        List<MetadataValue> luckydata = entity.getMetadata("luckydata");
        if (!luckydata.isEmpty()) {
            return (Map<String, Object>) luckydata.get(0).value();
        } else {
            Map<String, Object> result = new HashMap<>(8);
            entity.setMetadata("luckydata", new FixedMetadataValue(plugin, result));
            return result;
        }
    }

    public static int getOrbValue(int i) {
        if (i > 162670129) {
            return i - 100000;
        } else if (i > 81335063) {
            return 81335063;
        } else if (i > 40667527) {
            return 40667527;
        } else if (i > 20333759) {
            return 20333759;
        } else if (i > 10166857) {
            return 10166857;
        } else if (i > 5083423) {
            return 5083423;
        } else if (i > 2541701) {
            return 2541701;
        } else if (i > 1270849) {
            return 1270849;
        } else if (i > 635413) {
            return 635413;
        } else if (i > 317701) {
            return 317701;
        } else if (i > 158849) {
            return 158849;
        } else if (i > 79423) {
            return 79423;
        } else if (i > 39709) {
            return 39709;
        } else if (i > 19853) {
            return 19853;
        } else if (i > 9923) {
            return 9923;
        } else if (i > 4957) {
            return 4957;
        } else {
            return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
        }
    }
}
