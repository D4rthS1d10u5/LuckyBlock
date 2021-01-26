package com.mcgamer199.luckyblock.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBPlaceEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled;
    private final Block block;

    public LBPlaceEvent(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
