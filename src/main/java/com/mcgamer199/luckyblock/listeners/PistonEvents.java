package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LBType.BlockProperty;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class PistonEvents implements Listener {
    public PistonEvents() {
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Iterator var3 = event.getBlocks().iterator();

        while (var3.hasNext()) {
            final Block block = (Block) var3.next();
            if (LuckyBlock.isLuckyBlock(block)) {
                final LuckyBlock luckyBlock = LuckyBlock.getFromBlock(block);
                if (!luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_PUSHED) && !luckyBlock.getType().hasProperty(BlockProperty.RUN_ON_PUSH) && !luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_THROWN)) {
                    event.setCancelled(true);
                    return;
                }

                if (luckyBlock.getType().hasAdditionalBlocks()) {
                    event.setCancelled(true);
                    return;
                }

                if (luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_THROWN) && event.getDirection() == BlockFace.UP && block.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                    Scheduler.later(() -> {
                        luckyBlock.freeze();
                        block.getRelative(BlockFace.UP).setType(Material.AIR);
                        FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5D, 0.0D, 0.5D), luckyBlock.getType().getType(), (byte) luckyBlock.getType().getData());
                        fb.setVelocity(new Vector(0.0D, 1.2D, 0.0D));
                        PistonEvents.this.fb_run(fb, luckyBlock);
                    }, 5);
                }

                if (luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_PUSHED)) {
                    luckyBlock.freeze();
                    luckyBlock.changeBlock(block.getRelative(event.getDirection()));
                    luckyBlock.unfreeze();
                }

                if (luckyBlock.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    BreakLuckyBlock.openLB(luckyBlock, null);
                }
            } else if (LBType.isAdditionalBlocksFound() && LuckyBlock.getByABlock(block) != null) {
                event.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Iterator var3 = event.getBlocks().iterator();

        while (var3.hasNext()) {
            Block block = (Block) var3.next();
            if (LuckyBlock.isLuckyBlock(block)) {
                LuckyBlock luckyBlock = LuckyBlock.getFromBlock(block);
                if (!luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_PUSHED) && !luckyBlock.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    event.setCancelled(true);
                    return;
                }

                if (luckyBlock.getType().hasAdditionalBlocks()) {
                    event.setCancelled(true);
                    return;
                }

                if (luckyBlock.getType().hasProperty(BlockProperty.CAN_BE_PUSHED)) {
                    luckyBlock.freeze();
                    luckyBlock.changeBlock(block.getRelative(event.getDirection()));
                    luckyBlock.unfreeze();
                }

                if (luckyBlock.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    BreakLuckyBlock.openLB(luckyBlock, null);
                }
            } else if (LBType.isAdditionalBlocksFound() && LuckyBlock.getByABlock(block) != null) {
                event.setCancelled(true);
                return;
            }
        }

    }

    private void fb_run(final FallingBlock fb, final LuckyBlock luckyBlock) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!fb.isValid()) {
                    if (fb.getLocation().getBlock().getRelative(BlockFace.DOWN) != null) {
                        luckyBlock.changeBlock(fb.getLocation().getBlock());
                    }

                    luckyBlock.unfreeze();
                    cancel();
                }
            }
        }, 1, 1);
    }
}
