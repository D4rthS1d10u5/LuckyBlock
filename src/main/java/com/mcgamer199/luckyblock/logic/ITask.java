package com.mcgamer199.luckyblock.logic;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ITask implements Runnable {
    private int id;

    public ITask() {
    }

    public static int getNewDelayed(Plugin plugin, Runnable r, long time) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r, time);
    }

    public static int getNewRepeating(Plugin plugin, Runnable r, long time, long time1) {
        return plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, r, time, time1);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void run() {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
