package com.LuckyBlock.World.Engine;

public enum WorldOptions {

    SUPER_FLAT,
    NORMAL,
    ID;

    private int blkid;

    private WorldOptions() {
    }

    public int getId() {
        return this.blkid;
    }

    public void setId(int blkid) {
        this.blkid = blkid;
    }
}
