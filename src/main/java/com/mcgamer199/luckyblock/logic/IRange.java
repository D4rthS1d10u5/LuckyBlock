package com.mcgamer199.luckyblock.logic;

//TODO заменить на Vector от Bukkit
public class IRange {

    private int x;
    private int y;
    private int z;

    public IRange(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getTotal() {
        return this.x + this.y + this.z;
    }
}