package com.mcgamer199.luckyblock.logic;

import org.bukkit.Location;

public enum IDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    private IDirection() {
    }

    public static IDirection getByLoc(Location bloc, Location ploc) {
        IDirection direction = null;
        int bx = bloc.getBlockX();
        int bz = bloc.getBlockZ();
        int px = ploc.getBlockX();
        int pz = ploc.getBlockZ();
        if (px < bx && Math.abs(pz - bz) < 3) {
            direction = NORTH;
        } else if (px > bx && Math.abs(pz - bz) < 3) {
            direction = SOUTH;
        } else if (pz < bz && Math.abs(px - bx) < 3) {
            direction = EAST;
        } else if (pz > bz && Math.abs(px - bx) < 3) {
            direction = WEST;
        }

        return direction;
    }
}
