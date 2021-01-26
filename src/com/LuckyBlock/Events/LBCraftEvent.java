package com.LuckyBlock.Events;

import com.LuckyBlock.LB.LBType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class LBCraftEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private HumanEntity human;
    private boolean cancelled;
    private ItemStack item;
    private LBType lbtype;

    public LBCraftEvent(HumanEntity human, ItemStack item, LBType lbtype) {
        this.human = human;
        this.item = item;
        this.lbtype = lbtype;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
