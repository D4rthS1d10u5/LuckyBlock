package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class LBEffects {
    public LBEffects() {
    }

    public static void md(LBType type, Material mat, short data) {
        type.type = mat;
        type.data = data;
        type.save();
    }

    protected static void testEffects(final LuckyBlock luckyBlock) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                Block block = luckyBlock.getBlock();
                LBType type = luckyBlock.getType();
                if(block != null && type != null) {
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
                            } catch (Exception ignored) {}
                        }
                    } else if (!luckyBlock.isLocked()) {
                        if (LuckyBlock.getStorage().containsValue(luckyBlock)) {
                            Scheduler.later(() -> {
                                if (!luckyBlock.isValid() || block.getType() != luckyBlock.getType().type) {
                                    luckyBlock.remove(false, false);
                                    Scheduler.cancelTask(this);
                                }
                            }, 1);
                        } else {
                            Scheduler.cancelTask(this);
                        }
                    }
                } else {
                    System.out.println("type and block is null");
                }
            }
        }, luckyBlock.tickDelay, luckyBlock.tickDelay);
    }
}
