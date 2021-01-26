package com.LuckyBlock.Events;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LuckyBlockWorldChunkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private World world;
    private Chunk chunk;

    public LuckyBlockWorldChunkEvent(World w, Chunk ch) {
        this.world = w;
        this.chunk = ch;
    }

    public World getWorld() {
        return this.world;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
