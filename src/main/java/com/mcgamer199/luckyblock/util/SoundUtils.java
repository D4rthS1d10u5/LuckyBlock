package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SoundUtils {

    private static final Map<String, Sound> sounds = new HashMap<>();

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit) {
        playFixedSound(loc, sound, vol, pit, 30);
    }

    public static void playFixedSound(Location loc, Sound sound, float vol, float pit, int maxDistance) {
        if (sound != null) {
            loc.getWorld().getNearbyPlayers(loc, maxDistance).forEach(player -> {
                double distance = player.getLocation().distance(loc);
                if (distance < (double) maxDistance) {
                    float volume = (float) (1.0D - distance / (double) maxDistance);

                    try {
                        player.playSound(player.getLocation(), sound, vol * volume, pit);
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    public static Sound getSound(String name) {
        return sounds.getOrDefault(name, null);
    }

    public static void loadSounds() {
        File file = new File(LuckyBlockPlugin.d() + "data/sounds/" + LuckyBlockPlugin.sounds_file + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        addSound(config, "lct_stop", "core.lucky_crafting_table.stop");
        addSound(config, "lct_run", "core.lucky_crafting_table.run");
        addSound(config, "lct_finish", "core.lucky_crafting_table.done");
        addSound(config, "lct_insert", "core.lucky_crafting_table.insert");
        addSound(config, "lct_upgrade", "core.lucky_crafting_table.upgrade");
        addSound(config, "lb_break_fortune", "core.lb.break.fortune");
        addSound(config, "lb_drop_repair", "core.lb.drop.repair");
        addSound(config, "lb_drop_tntrain", "core.lb.drop.tnt_rain");
        addSound(config, "lb_drop_itemrain", "core.lb.drop.item_rain");
        addSound(config, "lb_drop_blockrain_launch", "core.lb.drop.block_rain.launch");
        addSound(config, "lb_drop_blockrain_land", "core.lb.drop.block_rain.land");
        addSound(config, "lb_drop_arrowrain", "core.lb.drop.arrow_rain");
        addSound(config, "lb_drop_lbrain", "core.lb.drop.lb_rain");
        addSound(config, "lb_drop_randomitem1", "core.lb.drop.random_item.switch");
        addSound(config, "lb_drop_randomitem2", "core.lb.drop.random_item.drop");
        addSound(config, "lb_gui_getitem", "core.lb.gui.get_item");
        addSound(config, "lb_stwell_activate", "core.lb.structure.lucky_well.activate");
        addSound(config, "lb_stwell_lucky", "core.lb.structure.lucky_well.lucky");
        addSound(config, "lb_stwell.unlucky", "core.lb.structure.lucky_well.unlucky");
        addSound(config, "portal_activate", "core.portal.activate");
        addSound(config, "portal_teleport", "core.portal.teleport");
        addSound(config, "boss_lb_hurt", "core.boss.lb.hurt");
        addSound(config, "boss_lb_death", "core.boss.lb.death");
        addSound(config, "boss_lb_ambient", "core.boss.lb.ambient");
        addSound(config, "boss_lb_heal", "core.boss.lb.heal");
        addSound(config, "boss_blaze_shield", "core.boss.lb.blaze_shield");
        addSound(config, "boss_witch_hurt", "core.boss.witch.hurt");
        addSound(config, "boss_witch_death", "core.boss.witch.death");
        addSound(config, "boss_witch_ambient", "core.boss.witch.ambient");
        addSound(config, "boss_healer_damage", "core.boss.healer.damage");
        addSound(config, "boss_healer_death", "core.boss.healer.death");
        addSound(config, "boss_healer_heal", "core.boss.healer.heal");
        addSound(config, "ritual_witch_particles", "core.lb.spawn_boss_ritual.witch.particles");
        addSound(config, "ritual_wch_spawn", "core.lb.spawn_boss_ritual.witch.spawn");
    }

    private static void addSound(ConfigurationSection section, String soundName, String path) {
        if (section.getString(path) != null) {
            try {
                Sound sound = Sound.valueOf(section.getString(path));
                sounds.put(soundName, sound);
            } catch (Exception ignored) {}
        }
    }

    static {
        for (Sound value : Sound.values()) {
            sounds.put(value.name(), value);
        }
    }
}
