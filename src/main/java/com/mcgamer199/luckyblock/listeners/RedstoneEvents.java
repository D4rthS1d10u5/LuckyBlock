package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.ArrayUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneEvents implements Listener {

    public RedstoneEvents() {}

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() > 0) {
            for (BlockFace facing : ArrayUtils.concat(LocationUtils.horizontal(), LocationUtils.vertical())) {
                Block check = event.getBlock().getRelative(facing);
                LuckyBlock luckyBlock = LuckyBlock.getByBlock(check);
                if(luckyBlock != null) {
                    BreakLuckyBlock.openLB(luckyBlock, null);
                }
            }
        }
    }
}
