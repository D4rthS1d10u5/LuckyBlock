package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EffectUtils {

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

    public static void shootBeam(CustomEntityBoss boss, LivingEntity entity, LivingEntity target, int max, ParticleHelper helper) {
        shootBeam(entity.getLocation(), boss, target, max, 0.4D, helper, null);
    }

    public static void shootBeam(CustomEntityBoss boss, LivingEntity entity, LivingEntity target, int max, double s, ParticleHelper helper) {
        shootBeam(entity.getLocation(), boss, target, max, s, helper, null);
    }

    public static void shootBeam(Location start, CustomEntityBoss  boss, LivingEntity target, int max, double s, ParticleHelper helper, final String tag) {
        start.setDirection(target.getLocation().toVector().subtract(start.toVector()));
        final Vector increase = start.getDirection().multiply(s);
        Scheduler.timer(new BukkitRunnable() {
            private int m = max;
            private int a = 0;

            @Override
            public void run() {
                if (this.m < 1) {
                    Scheduler.cancelTask(this);
                } else {
                    if (target.isDead()) {
                        Scheduler.cancelTask(this);
                        return;
                    }

                    ++this.a;
                    Location point = start.add(increase);
                    playEffects(helper.particle, point, helper.amount, helper.doubles, helper.speed);
                    --this.m;
                    if (point.distance(target.getLocation()) < 1.0D) {
                        if (boss != null) {
                            boss.onEntityHitWithBeam(target, tag);
                        }

                        Scheduler.cancelTask(this);
                    } else if (this.a > 10) {
                        Scheduler.cancelTask(this);
                        shootBeam(point, boss, target, this.m, s, helper, tag);
                    }
                }
            }
        }, 0, 1);
    }

    public static Particle getParticle(String s) {
        Particle p;
        if (s.equalsIgnoreCase("spell_witch")) {
            p = Particle.SPELL_WITCH;
        } else {
            p = Particle.valueOf(s.toUpperCase());
        }

        return p;
    }

    public static void playEffects(Particle particle, Location loc, int amount, double[] r, float speed) {
        if (r.length == 3) {
            loc.getWorld().spawnParticle(particle, loc, amount, r[0], r[1], r[2], speed);
        }

    }

    private static void addSound(ConfigurationSection section, String soundName, String path) {
        if (section.getString(path) != null) {
            try {
                Sound sound = Sound.valueOf(section.getString(path));
                sounds.put(soundName, sound);
            } catch (Exception ignored) {}
        }
    }

    public static class ParticleHelper {
        Particle particle;
        double[] doubles;
        int amount;
        float speed;

        public ParticleHelper(Particle particle, int amount, double[] doubles, float speed) {
            this.particle = particle;
            this.amount = amount;
            this.doubles = doubles;
            this.speed = speed;
        }

        public int getAmount() {
            return this.amount;
        }

        public Particle getParticle() {
            return this.particle;
        }

        public float getSpeed() {
            return this.speed;
        }

        public double[] getDoubles() {
            return this.doubles;
        }
    }

    static {
        for (Sound value : Sound.values()) {
            sounds.put(value.name(), value);
        }
    }
}
