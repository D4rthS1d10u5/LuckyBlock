package com.LuckyBlock.customdrop;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.LB.DropOption;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.logic.MyTasks;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.core.logic.SchedulerTask;

import java.util.Random;

public class EffectsDrop implements CustomDrop {
    public EffectsDrop() {
    }

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
        return new DropOption[]{new DropOption("Particles", new String[]{"BARRIER", "CLOUD", "CRIT", "CRIT_MAGIC", "DRIP_LAVA", "DRIP_WATER", "ENCHANTMENT_TABLE", "FIREWORKS_SPARK", "FLAME", "FOOTSTEP", "\nHEART", "LAVA", "NOTE", "PORTAL", "REDSTONE", "SLIME", "SMOKE_NORMAL", "SPELL", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL_WITCH", "SUSPENDED_DEPTH", "TOWN_AURA", "VILLAGER_ANGRY", "VILLAGER_HAPPY"})};
    }

    public void function(final LB lb, Player player) {
        if (lb.hasDropOption("Particles")) {
            final String[] s = (String[])lb.getDropOption("Particles").getValues();
            final SchedulerTask task = new SchedulerTask();
            task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
                int x = 10;

                public void run() {
                    int r = (new Random()).nextInt(s.length);
                    Particle e = Particle.valueOf(s[r].toUpperCase());
                    lb.getBlock().getWorld().spawnParticle(e, lb.getBlock().getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
                    --this.x;
                    if (this.x < 1) {
                        task.run();
                    }

                }
            }, 5L, 5L));
        }

    }
}
