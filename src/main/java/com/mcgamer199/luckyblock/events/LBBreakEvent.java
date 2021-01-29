package com.mcgamer199.luckyblock.events;

import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled;
    private final LuckyBlock luckyBlock;

    public LBBreakEvent(LuckyBlock luckyBlock, Player player) {
        this.luckyBlock = luckyBlock;
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
        return this.luckyBlock.getBlock();
    }

    public LuckyBlock getLB() {
        return this.luckyBlock;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
