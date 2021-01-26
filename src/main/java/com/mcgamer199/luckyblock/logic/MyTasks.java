package com.mcgamer199.luckyblock.logic;

import com.mcgamer199.luckyblock.engine.IObjects;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;

public class MyTasks {
    public MyTasks() {
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

    public static boolean reloadLang() {
        return IObjects.changeLanguage();
    }

    public static final void playFixedSound(Location loc, Sound sound, float vol, float pit) {
        com.mcgamer199.luckyblock.logic.ColorsClass.playFixedSound(loc, sound, vol, pit);
    }

    public static final void playFixedSound(Location loc, Sound sound, float vol, float pit, int maxdistance) {
        com.mcgamer199.luckyblock.logic.ColorsClass.playFixedSound(loc, sound, vol, pit, maxdistance);
    }

    public static final String blockToString(Block block) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.blockToString(block);
    }

    public static final String locToString(Location loc) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.locToString(loc);
    }

    public static final Particle getParticle(String s) {
        Particle p = null;
        if (s.equalsIgnoreCase("spell_witch")) {
            p = Particle.SPELL_WITCH;
        } else {
            p = Particle.valueOf(s.toUpperCase());
        }

        return p;
    }

    public static final Location stringToLoc(String s) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.stringToLoc(s);
    }

    public static final Block stringToBlock(String s) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.stringToBlock(s);
    }

    public static final Sound getSound(String name) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.getSound(name);
    }

    public static final String val(String loc, boolean colors) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.val(loc, colors);
    }

    public static void playEffects(Particle particle, Location loc, int amount, double[] r, float speed) {
        if (r.length == 3) {
            loc.getWorld().spawnParticle(particle, loc, amount, r[0], r[1], r[2], (double)speed);
        }

    }

    public static String c(String a) {
        return ColorsClass.c(a);
    }
}