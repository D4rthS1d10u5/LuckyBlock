package com.mcgamer199.luckyblock.events;

import com.mcgamer199.luckyblock.lb.LBType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class LBCraftEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final HumanEntity human;
    private boolean cancelled;
    private final ItemStack item;
    private final LBType lbtype;

    public LBCraftEvent(HumanEntity human, ItemStack item, LBType lbtype) {
        this.human = human;
        this.item = item;
        this.lbtype = lbtype;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LBType getLBType() {
        return this.lbtype;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HumanEntity getHuman() {
        return this.human;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
