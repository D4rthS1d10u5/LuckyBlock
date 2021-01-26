package com.LuckyBlock.Events;

import com.LuckyBlock.LB.LB;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private LB lb;
    private BlockFace clickedFace;

    public LBInteractEvent(Player player, LB lb, BlockFace clickedFace) {
        this.player = player;
        this.lb = lb;
        this.clickedFace = clickedFace;
    }

    public LB getLB() {
        return this.lb;
    }

    public BlockFace getClickedFace() {
        return this.clickedFace;
    }

    public Block getClickedBlock() {
        return this.lb.getBlock();
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
