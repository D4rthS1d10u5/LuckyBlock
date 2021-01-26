package com.mcgamer199.luckyblock.events;

import com.mcgamer199.luckyblock.lb.LB;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final LB lb;
    private final BlockFace clickedFace;

    public LBInteractEvent(Player player, LB lb, BlockFace clickedFace) {
        this.player = player;
        this.lb = lb;
        this.clickedFace = clickedFace;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
}
