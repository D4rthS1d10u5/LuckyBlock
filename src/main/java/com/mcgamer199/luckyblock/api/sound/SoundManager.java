package com.mcgamer199.luckyblock.api.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class SoundManager {

    public static void runFixedSound(String name1, String name2, Player player, float vol, float pit) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(name1), vol, pit);
        } catch (Exception var8) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(name2), vol, pit);
            } catch (Exception var7) {
            }
        }
    }

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit) {
        playFixedSound(loc, sound, vol, pit, 30);
    }

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit, int maxdistance) {
        if (sound != null) {
            Iterator var6 = loc.getWorld().getPlayers().iterator();

            while(var6.hasNext()) {
                Player p = (Player)var6.next();
                if (p.getWorld() == loc.getWorld()) {
                    double distance = p.getLocation().distance(loc);
                    if (distance < (double)maxdistance) {
                        float volume = (float)(1.0D - distance / (double)maxdistance);

                        try {
                            p.playSound(p.getLocation(), sound, vol * volume, pit);
                        } catch (Exception var11) {
                        }
                    }
                }
            }
        }

    }
}
