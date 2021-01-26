package com.LuckyBlock.Event.LB;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.Event.LB.Block.BreakLuckyBlock;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.LB.LBType;
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
import org.bukkit.util.BlockIterator;
import org.core.logic.SchedulerTask;

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

    private void run(final Entity p, final Entity entity) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (p.isOnGround()) {
                    LBShootEvent.this.place(p.getLocation(), entity);
                    p.remove();
                    task.run();
                }

                if (!p.isValid()) {
                    task.run();
                }

            }
        }, 2L, 2L));
    }

    private void place(Location loc, Entity entity) {
        if (LuckyBlock.isWorldGuardValid()) {
            WorldGuardPlugin w = LuckyBlock.instance.getWorldGuard();
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
        block.setData((byte)type.getData());
        LB lb = new LB(type, block, 0, entity, true, true);
        lb.playEffects();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow)projectile;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player)arrow.getShooter();
                World world = arrow.getWorld();
                BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0.0D, 4);
                Block b = null;

                while(iterator.hasNext()) {
                    b = iterator.next();
                    if (b != null && b.getType() != Material.AIR) {
                        break;
                    }
                }

                if (LB.getFromBlock(b) != null) {
                    LB lb = LB.getFromBlock(b);
                    if (lb.getType().arrowRun) {
                        if (event.getEntity().isValid()) {
                            event.getEntity().remove();
                        }

                        BreakLuckyBlock.openLB(lb, player);
                    }
                }

            }
        }
    }
}

