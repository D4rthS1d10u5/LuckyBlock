package com.mcgamer199.luckyblock.api.customdrop.impl;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class EffectsDrop implements CustomDrop {

    private static final Properties dropOptions = new Properties().putStringArray("Particles", new String[]{"BARRIER", "CLOUD", "CRIT", "CRIT_MAGIC", "DRIP_LAVA", "DRIP_WATER", "ENCHANTMENT_TABLE", "FIREWORKS_SPARK", "FLAME", "FOOTSTEP", "HEART", "LAVA", "NOTE", "PORTAL", "REDSTONE", "SLIME", "SMOKE_NORMAL", "SPELL", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL_WITCH", "SUSPENDED_DEPTH", "TOWN_AURA", "VILLAGER_ANGRY", "VILLAGER_HAPPY"});

    public String getName() {
        return "EFFECTS_DROP";
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isEnabledByCommands() {
        return true;
    }

    public String getDescription() {
        return MyTasks.val("desc.drop.effects_drop", false);
    }

    public Properties getDropOptions() {
        return dropOptions;
    }

    public void execute(final LuckyBlock luckyBlock, Player player) {
        String[] particles = luckyBlock.getDropOptions().getStringArray("Particles");
        if(particles.length > 0) {
            Scheduler.create(() -> {
                Particle particle = Particle.valueOf(RandomUtils.getRandomObject(particles));
                luckyBlock.getBlock().getWorld().spawnParticle(particle, luckyBlock.getBlock().getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
            }).count(10).timer(5, 5);
        }
    }
}
