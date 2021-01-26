package com.mcgamer199.luckyblock.listeners;

public enum WorldOptions {

    SUPER_FLAT,
    NORMAL,
    ID;

    private int blkid;

    WorldOptions() {
    }

    public int getId() {
        return this.blkid;
    }

    public void setId(int blkid) {
        this.blkid = blkid;
    }
}
