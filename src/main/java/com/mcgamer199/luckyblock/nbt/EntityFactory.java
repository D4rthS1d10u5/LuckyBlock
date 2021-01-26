package com.mcgamer199.luckyblock.nbt;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;

public class EntityFactory {
    public EntityFactory() {
    }

    public static void followEntity(Entity target, LivingEntity entity) {
        Location location = target.getLocation();

        try {
            Object handle = entity.getClass().getMethod("getHandle").invoke(entity);
            Object nav = handle.getClass().getMethod("getNavigation").invoke(handle);
            Method method = nav.getClass().getMethod("a", Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
            method.invoke(nav, location.getX(), location.getY(), location.getZ(), 1.75D);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
}
