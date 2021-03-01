package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class LBShootEvent implements Listener {

    public LBShootEvent() {
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getBow() != null) {
            ItemStack item = event.getBow();
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("" + ChatColor.GOLD + ChatColor.BOLD + "LB Bow")) {
                this.run(event.getProjectile(), event.getEntity());
            }
        }

    }

    private void run(final Entity projectile, final Entity target) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isOnGround()) {
                    LBShootEvent.this.place(projectile.getLocation(), target);
                    projectile.remove();
                    Scheduler.cancelTask(this);
                }

                if (!projectile.isValid()) {
                    Scheduler.cancelTask(this);
                }
            }
        }, 2, 2);
    }

    private void place(Location loc, Entity entity) {
        if (LuckyBlockPlugin.isWorldGuardValid()) {
            WorldGuardPlugin w = LuckyBlockPlugin.instance.getWorldGuard();
            if (w != null) {
                RegionManager m = w.getRegionManager(loc.getWorld());
                ApplicableRegionSet s = m.getApplicableRegions(loc);
                if (s.allows(DefaultFlag.BLOCK_PLACE)) {
                    return;
                }
            }
        }

        LBType type = LBType.getDefaultType();
        Block block = loc.getBlock();
        block.setType(type.getType());
        block.setData((byte) type.getData());
        LuckyBlock luckyBlock = new LuckyBlock(type, block, 0, entity, true);
        luckyBlock.playEffects();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                World world = arrow.getWorld();
                BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0.0D, 4);
                Block b = null;

                while (iterator.hasNext()) {
                    b = iterator.next();
                    if (b != null && b.getType() != Material.AIR) {
                        break;
                    }
                }

                if (LuckyBlock.getByBlock(b) != null) {
                    LuckyBlock luckyBlock = LuckyBlock.getByBlock(b);
                    if (luckyBlock.getType().arrowRun) {
                        if (event.getEntity().isValid()) {
                            event.getEntity().remove();
                        }

                        BreakLuckyBlock.openLB(luckyBlock, player);
                    }
                }

            }
        }
    }
}

