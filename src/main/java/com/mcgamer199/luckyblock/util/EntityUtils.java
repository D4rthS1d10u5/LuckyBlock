package com.mcgamer199.luckyblock.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;

@UtilityClass
public class EntityUtils {

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
}
