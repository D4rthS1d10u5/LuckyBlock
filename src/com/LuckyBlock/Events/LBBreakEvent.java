package com.LuckyBlock.Events;

import com.LuckyBlock.LB.LB;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancelled;
    private LB lb;

    public LBBreakEvent(LB lb, Player player) {
        this.lb = lb;
        this.player = player;
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
        return this.lb.getBlock();
    }

    public LB getLB() {
        return this.lb;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
