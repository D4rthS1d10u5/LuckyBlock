package com.mcgamer199.luckyblock.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by дартЪ on 02.05.2020
 */
public final class LocationUtils {

    public static long getChunkKey(int chunkX, int chunkZ) {
        return (chunkX << 31 | chunkZ & 0xFFFFFFFFL);
    }

    public static long getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ());
    }

    public static long getChunkKey(Location location) {
        return getChunkKey(getChunkX(location.getBlockX()), getChunkZ(location.getBlockZ()));
    }

    public static int getChunkX(int x) {
        return x >> 4;
    }

    public static int getChunkX(long key) {
        return getChunkX(getX(key));
    }

    public static int getChunkZ(int z) {
        return z >> 4;
    }

    public static int getChunkZ(long key) {
        return getChunkZ(getZ(key));
    }

    public static int getX(long key) {
        return (int) (key >> 31);
    }

    public static int getZ(long key) {
        return (int) key;
    }

    public static long asLong(Location location) {
        return (((long)location.getBlockX() & 0x3FFFFFF) << 38) | (((long)location.getBlockY() & 0xFFF) << 26) | (location.getBlockZ() & 0x3FFFFFF);
    }

    public static Location fromLong(long key) {
        int x = (int) (key >> 38);
        int y = (int) ((key >> 26) & 0xfff);
        int z = (int) (key << 38 >> 38);
        return new Location(null, x, y ,z);
    }

    public static String asString(Location location) {
        return String.format("%s,%s,%s,%s", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static Location locationFromString(String code) {
        String[] data = code.split(",");
        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        return new Location(world, x, y, z);
    }

    public static Block blockFromString(String code) {
        String[] data = code.split(",");
        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);

        Block result = null;

        if (world != null) {
            result = world.getBlockAt((int) x, (int) y, (int) z);
        }

        return result;
    }

    public static BlockFace getFacingBetween(Location firstLocation, Location secondLocation) {
        int bx = firstLocation.getBlockX();
        int bz = firstLocation.getBlockZ();
        int px = secondLocation.getBlockX();
        int pz = secondLocation.getBlockZ();

        if (px < bx && Math.abs(pz - bz) < 3) {
            return BlockFace.NORTH;
        } else if (px > bx && Math.abs(pz - bz) < 3) {
            return BlockFace.SOUTH;
        } else if (pz < bz && Math.abs(px - bx) < 3) {
            return BlockFace.EAST;
        } else if (pz > bz && Math.abs(px - bx) < 3) {
            return BlockFace.WEST;
        }

        return BlockFace.SELF;
    }

    public static BlockFace[] horizontal() {
        return new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    }

    public static BlockFace[] allHorizontal() {
        return new BlockFace[] {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    }

    public static BlockFace[] vertical() {
        return new BlockFace[] {BlockFace.UP, BlockFace.DOWN};
    }
}

