package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LBType.BlockProperty;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
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
            if (LB.isLuckyBlock(block)) {
                final LB lb = LB.getFromBlock(block);
                if (!lb.getType().hasProperty(BlockProperty.CAN_BE_PUSHED) && !lb.getType().hasProperty(BlockProperty.RUN_ON_PUSH) && !lb.getType().hasProperty(BlockProperty.CAN_BE_THROWN)) {
                    event.setCancelled(true);
                    return;
                }

                if (lb.getType().hasAdditionalBlocks()) {
                    event.setCancelled(true);
                    return;
                }

                if (lb.getType().hasProperty(BlockProperty.CAN_BE_THROWN) && event.getDirection() == BlockFace.UP && block.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                    final ITask task = new ITask();
                    task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
                        public void run() {
                            lb.freeze();
                            block.getRelative(BlockFace.UP).setType(Material.AIR);
                            FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5D, 0.0D, 0.5D), lb.getType().getType(), (byte) lb.getType().getData());
                            fb.setVelocity(new Vector(0.0D, 1.2D, 0.0D));
                            PistonEvents.this.fb_run(fb, lb);
                            task.run();
                        }
                    }, 5L));
                }

                if (lb.getType().hasProperty(BlockProperty.CAN_BE_PUSHED)) {
                    lb.freeze();
                    lb.changeBlock(block.getRelative(event.getDirection()));
                    lb.unfreeze();
                }

                if (lb.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    BreakLuckyBlock.openLB(lb, null);
                }
            } else if (LBType.isAdditionalBlocksFound() && LB.getByABlock(block) != null) {
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
            if (LB.isLuckyBlock(block)) {
                LB lb = LB.getFromBlock(block);
                if (!lb.getType().hasProperty(BlockProperty.CAN_BE_PUSHED) && !lb.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    event.setCancelled(true);
                    return;
                }

                if (lb.getType().hasAdditionalBlocks()) {
                    event.setCancelled(true);
                    return;
                }

                if (lb.getType().hasProperty(BlockProperty.CAN_BE_PUSHED)) {
                    lb.freeze();
                    lb.changeBlock(block.getRelative(event.getDirection()));
                    lb.unfreeze();
                }

                if (lb.getType().hasProperty(BlockProperty.RUN_ON_PUSH)) {
                    BreakLuckyBlock.openLB(lb, null);
                }
            } else if (LBType.isAdditionalBlocksFound() && LB.getByABlock(block) != null) {
                event.setCancelled(true);
                return;
            }
        }

    }

    private void fb_run(final FallingBlock fb, final LB lb) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!fb.isValid()) {
                    if (fb.getLocation().getBlock().getRelative(BlockFace.DOWN) != null) {
                        lb.changeBlock(fb.getLocation().getBlock());
                    }

                    lb.unfreeze();
                    task.run();
                }

            }
        }, 1L, 1L));
    }
}
