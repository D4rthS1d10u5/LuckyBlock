package com.LuckyBlock.Events;

import com.LuckyBlock.LB.LB;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBGenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private World world;
    private Chunk chunk;
    private LB lb;

    public LBGenerateEvent(LB lb, World world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;
        this.lb = lb;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LB getLB() {
        return this.lb;
    }

    public Block getBlock() {
        return this.lb.getBlock();
    }

    public World getWorld() {
        return this.world;
    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
