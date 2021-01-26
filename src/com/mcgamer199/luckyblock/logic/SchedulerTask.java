package com.mcgamer199.luckyblock.logic;

import org.bukkit.Bukkit;

public class SchedulerTask implements Runnable {
    private int id;

    public SchedulerTask() {
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
