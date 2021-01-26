package com.mcgamer199.luckyblock.events;

import com.mcgamer199.luckyblock.lb.LBType;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBPreGenerateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private World world;
    private Chunk chunk;
    private Block block;
    private LBType lbType;

    public LBPreGenerateEvent(Block block, World world, Chunk chunk, LBType lbType) {
        this.world = world;
        this.chunk = chunk;
        this.block = block;
        this.lbType = lbType;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Block getBlock() {
        return this.block;
    }

    public LBType getType() {
        return this.lbType;
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
