package com.mcgamer199.luckyblock.api.sound;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.SchedulerTask;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundPlayer {

    @Getter
    private final List<SoundFile> sounds;

    public SoundPlayer(List<SoundFile> sounds) {
        this.sounds = sounds;
    }

    public void play(int times, int delay, final Location loc) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlockPlugin.instance, new Runnable() {
            private int timer = times;

            public void run() {
                if (this.timer > 0) {
                    for (int x = 0; x < SoundPlayer.this.sounds.size(); ++x) {
                        SoundPlayer.this.sounds.get(x).play(loc);
                        --this.timer;
                    }
                } else {
                    task.run();
                }

            }
        }, 0L, delay));
    }

    public void play(int times, int delay, final Player player) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlockPlugin.instance, new Runnable() {
            private int timer = times;

            public void run() {
                if (this.timer > 0) {
                    for (int x = 0; x < SoundPlayer.this.sounds.size(); ++x) {
                        SoundPlayer.this.sounds.get(x).play(player);
                        --this.timer;
                    }
                } else {
                    task.run();
                }

            }
        }, 0L, delay));
    }
}
