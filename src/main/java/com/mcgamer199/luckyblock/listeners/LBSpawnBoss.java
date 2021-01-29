package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.newstr.FileStructure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LBSpawnBoss implements Listener {
    public LBSpawnBoss() {
    }

    @EventHandler
    private void onPotionHit(PotionSplashEvent event) {
        Block block = event.getPotion().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (LuckyBlock.isLuckyBlock(block)) {
            LuckyBlock luckyBlock = LuckyBlock.getFromBlock(block);
            if (luckyBlock.getLuck() == luckyBlock.getType().getMaxLuck()) {
                Block b = block.getRelative(BlockFace.DOWN);
                if (b.getRelative(BlockFace.EAST).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.WEST).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.SOUTH).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.NORTH).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.NORTH_EAST).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.NORTH_WEST).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.SOUTH_EAST).getType() == Material.STATIONARY_WATER && b.getRelative(BlockFace.SOUTH_WEST).getType() == Material.STATIONARY_WATER && event.getPotion().getEffects() != null) {
                    boolean f = false;
                    Iterator var7 = event.getPotion().getEffects().iterator();

                    label58:
                    {
                        PotionEffect e;
                        do {
                            if (!var7.hasNext()) {
                                break label58;
                            }

                            e = (PotionEffect) var7.next();
                        } while (e.getType().getName() != PotionEffectType.INVISIBILITY.getName() && e.getType().getName() != PotionEffectType.NIGHT_VISION.getName());

                        f = true;
                    }

                    if (f) {
                        int a = 0;
                        List<Item> item = new ArrayList();
                        Iterator var9 = event.getPotion().getNearbyEntities(2.0D, 2.0D, 2.0D).iterator();

                        while (var9.hasNext()) {
                            Entity e = (Entity) var9.next();
                            if (e instanceof Item) {
                                Item i = (Item) e;
                                if (i.getItemStack() != null && i.getItemStack().getType() == Material.SUGAR) {
                                    ++a;
                                    item.add(i);
                                }
                            }
                        }

                        if (a > 4) {
                            luckyBlock.remove(true);
                            var9 = item.iterator();

                            while (var9.hasNext()) {
                                Item i = (Item) var9.next();
                                i.remove();
                            }

                            this.func_2(block.getLocation());
                        }
                    }
                }
            }
        }

    }

    private void func_2(final Location loc) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            int i = 25;

            public void run() {
                if (this.i > 0) {
                    MyTasks.playEffects(Particle.CLOUD, loc, 110, new double[]{1.0D, 0.0D, 1.0D}, 0.5F);
                    SoundManager.playFixedSound(loc, SoundManager.getSound("ritual_witch_particles"), 1.0F, 0.0F, 59);
                    --this.i;
                } else {
                    for (int x = -1; x < 2; ++x) {
                        for (int z = -1; z < 2; ++z) {
                            Location l = new Location(loc.getWorld(), loc.getX() + (double) x, loc.getY() - 1.0D, loc.getZ() + (double) z);
                            l.getBlock().setType(Material.OBSIDIAN);
                        }
                    }

                    FileStructure.generateWitchDungeon(loc);
                    SoundManager.playFixedSound(loc, SoundManager.getSound("ritual_witch_spawn"), 1.0F, 0.0F, 26);
                    task.run();
                }

            }
        }, 20L, 10L));
    }
}
