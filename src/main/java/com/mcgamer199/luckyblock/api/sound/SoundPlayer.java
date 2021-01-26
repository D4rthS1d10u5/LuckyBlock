package com.mcgamer199.luckyblock.api.sound;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.logic.SchedulerTask;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundPlayer {

    @Getter
    private List<SoundFile> sounds;

    public SoundPlayer(List<SoundFile> sounds) {
        this.sounds = sounds;
    }

    public void play(int times, int delay, final Location loc) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            private int timer = times;

            public void run() {
                if (this.timer > 0) {
                    for(int x = 0; x < SoundPlayer.this.sounds.size(); ++x) {
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
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            private int timer = times;

            public void run() {
                if (this.timer > 0) {
                    for(int x = 0; x < SoundPlayer.this.sounds.size(); ++x) {
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
