package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.lb.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneEvents implements Listener {
    public RedstoneEvents() {
    }

    public static void testForBlock(Block block, int newCurrent) {
        if (newCurrent > 0) {
            Block where = null;
            boolean done = false;
            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.DOWN))) {
                done = true;
                where = block.getRelative(BlockFace.DOWN);
            }

            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.EAST))) {
                done = true;
                where = block.getRelative(BlockFace.EAST);
            }

            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.WEST))) {
                done = true;
                where = block.getRelative(BlockFace.WEST);
            }

            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.SOUTH))) {
                done = true;
                where = block.getRelative(BlockFace.SOUTH);
            }

            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.NORTH))) {
                done = true;
                where = block.getRelative(BlockFace.NORTH);
            }

            if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.UP))) {
                done = true;
                where = block.getRelative(BlockFace.UP);
            }

            if (done) {
                BreakLuckyBlock.openLB(LuckyBlock.getFromBlock(where), null);
            }
        }

    }

    @EventHandler
    public void OnRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() > 0) {
            testForBlock(event.getBlock(), event.getNewCurrent());
        }

    }
}
