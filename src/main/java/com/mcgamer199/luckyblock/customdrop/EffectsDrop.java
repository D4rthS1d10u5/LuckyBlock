package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectsDrop implements CustomDrop {

    private static final DropOption[] DEFAULT_OPTIONS = new DropOption[]{new DropOption("Particles", new String[]{"BARRIER", "CLOUD", "CRIT", "CRIT_MAGIC", "DRIP_LAVA", "DRIP_WATER", "ENCHANTMENT_TABLE", "FIREWORKS_SPARK", "FLAME", "FOOTSTEP", "\nHEART", "LAVA", "NOTE", "PORTAL", "REDSTONE", "SLIME", "SMOKE_NORMAL", "SPELL", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL_WITCH", "SUSPENDED_DEPTH", "TOWN_AURA", "VILLAGER_ANGRY", "VILLAGER_HAPPY"})};

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

    public DropOption[] getDefaultOptions() {
        return DEFAULT_OPTIONS;
    }

    public void function(final LuckyBlock luckyBlock, Player player) {
        if (luckyBlock.hasDropOption("Particles")) {
            String[] s = (String[]) luckyBlock.getDropOption("Particles").getValues();
            Scheduler.timerAsync(new BukkitRunnable() {
                private int x = 10;

                @Override
                public void run() {
                    Particle particle = Particle.valueOf(RandomUtils.getRandomObject(s));
                    luckyBlock.getBlock().getWorld().spawnParticle(particle, luckyBlock.getBlock().getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
                    if(--x < 1) {
                        Scheduler.cancelTask(this);
                    }
                }
            }, 5, 5);
        }
    }
}
