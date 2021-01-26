package com.mcgamer199.luckyblock.logic;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {
    public SoundManager() {
    }

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
}
