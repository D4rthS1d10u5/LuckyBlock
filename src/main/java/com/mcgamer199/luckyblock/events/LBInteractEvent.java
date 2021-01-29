package com.mcgamer199.luckyblock.events;

import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LBInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final LuckyBlock luckyBlock;
    private final BlockFace clickedFace;

    public LBInteractEvent(Player player, LuckyBlock luckyBlock, BlockFace clickedFace) {
        this.player = player;
        this.luckyBlock = luckyBlock;
        this.clickedFace = clickedFace;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LuckyBlock getLB() {
        return this.luckyBlock;
    }

    public BlockFace getClickedFace() {
        return this.clickedFace;
    }

    public Block getClickedBlock() {
        return this.luckyBlock.getBlock();
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
