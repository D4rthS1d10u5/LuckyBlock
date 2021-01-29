package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

public class LBEffects {
    public LBEffects() {
    }

    public static void md(LBType type, Material mat, short data) {
        type.type = mat;
        type.data = data;
        type.save();
    }

    protected static void testEffects(final LuckyBlock luckyBlock) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                final Block block = luckyBlock.getBlock();
                LBType type = luckyBlock.getType();
                boolean x = false;
                if (type.getBlockType() != block.getType()) {
                    x = true;
                }

                if (!luckyBlock.isValid()) {
                    x = true;
                }

                if (!x) {
                    if (type.allowtickparticles) {
                        try {
                            if (type.tickparticles != null) {
                                String particle = type.tickparticles;
                                String[] part = particle.split(" ");
                                float rx = Float.parseFloat(part[1]);
                                float ry = Float.parseFloat(part[2]);
                                float rz = Float.parseFloat(part[3]);
                                float speed = Float.parseFloat(part[4]);
                                double lx = Double.parseDouble(part[6]);
                                double ly = Double.parseDouble(part[7]);
                                double lz = Double.parseDouble(part[8]);
                                int amount = Integer.parseInt(part[5]);
                                Location l = new Location(block.getWorld(), (double) block.getX() + 0.4D + lx, (double) block.getY() + 0.4D + ly, (double) block.getZ() + 0.4D + lz);
                                block.getWorld().spawnParticle(Particle.valueOf(part[0].toUpperCase()), l, amount, rx, ry, rz, speed);
                            }
                        } catch (Exception var18) {
                        }
                    }
                } else if (!luckyBlock.isFreezed()) {
                    if (LuckyBlock.luckyBlocks.contains(luckyBlock)) {
                        final ITask tsk = new ITask();
                        tsk.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
                            public void run() {
                                if (!luckyBlock.isValid() || block.getType() != luckyBlock.getType().type) {
                                    luckyBlock.remove();
                                    tsk.run();
                                    task.run();
                                }

                            }
                        }, 1L));
                    } else {
                        task.run();
                    }
                }

            }
        }, luckyBlock.a, luckyBlock.a));
    }
}
