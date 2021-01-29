package com.mcgamer199.luckyblock.logic;

import com.mcgamer199.luckyblock.engine.IObjects;
import org.bukkit.Location;
import org.bukkit.Particle;

public class MyTasks {

    public MyTasks() {
    }

    public static boolean reloadLang() {
        return IObjects.changeLanguage();
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

    public static final String val(String loc, boolean colors) {
        return com.mcgamer199.luckyblock.logic.ColorsClass.val(loc, colors);
    }

    public static void playEffects(Particle particle, Location loc, int amount, double[] r, float speed) {
        if (r.length == 3) {
            loc.getWorld().spawnParticle(particle, loc, amount, r[0], r[1], r[2], speed);
        }

    }
}