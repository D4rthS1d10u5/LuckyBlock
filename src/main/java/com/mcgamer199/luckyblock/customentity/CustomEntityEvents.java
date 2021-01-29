package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.SchedulerTask;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomEntityEvents implements Listener {

    public CustomEntityEvents() {
    }

    public static void onSpawn(Entity entity) {
        if (CustomEntity.getByUUID(entity.getUniqueId()) != null) {
            final CustomEntity c = CustomEntity.getByUUID(entity.getUniqueId());
            if (c.entity != null && c.getTickTime() > -1) {
                final SchedulerTask task = new SchedulerTask();
                task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlockPlugin.instance, new Runnable() {
                    public void run() {
                        if (c.entity != null && !c.entity.isDead()) {
                            c.onTick();
                        } else {
                            task.run();
                        }

                    }
                }, c.getTickTime(), c.getTickTime()));
            }
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.isImmuneTo(event.getCause())) {
                event.setCancelled(true);
            }

            if (c.getDefense() > 0.0D) {
                double f = event.getFinalDamage() / c.getDefense();
                event.setDamage(f);
            }

            c.onDamage(event);
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                c.onDamageByEntity(e);
                if (e.getDamager() instanceof Player) {
                    c.onDamageByPlayer(e);
                }
            }
        }

    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.getDeathParticles() != null) {
                if (c.particleOptions() != null) {
                    if (c.particleOptions().length == 10) {
                        double x1 = c.particleOptions()[0];
                        double y1 = c.particleOptions()[1];
                        double z1 = c.particleOptions()[2];
                        event.getEntity().getWorld().spawnParticle(c.getDeathParticles(), event.getEntity().getLocation().add(x1, y1, z1), (int) c.particleOptions()[3], c.particleOptions()[6], c.particleOptions()[7], c.particleOptions()[8], c.particleOptions()[9]);
                    }
                } else {
                    event.getEntity().getWorld().spawnParticle(c.getDeathParticles(), event.getEntity().getLocation(), 25, 0.3D, 0.3D, 0.3D, 0.0D);
                }
            }

            if (!c.defaultDrops() && event.getEntity().getWorld().getGameRuleValue("doMobLoot").equalsIgnoreCase("true")) {
                event.getDrops().clear();
                if (!c.itemsEdited()) {
                    ItemStack[] items = c.getRandomItems();

                    for (int s = 0; s < items.length; ++s) {
                        if (items[s] != null && c.getDrops()[s] != null) {
                            event.getDrops().add(items[s]);
                        }
                    }
                } else {
                    List<Item> itemEntities = new ArrayList();
                    ItemStack[] items = c.getRandomItems();

                    for (int x = 0; x < items.length; ++x) {
                        if (items[x] != null && c.getDrops()[x] != null) {
                            Item i = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), items[x]);
                            itemEntities.add(i);
                        }
                    }

                    if (itemEntities.size() > 0) {
                        Item[] it = new Item[itemEntities.size()];

                        for (int x = 0; x < itemEntities.size(); ++x) {
                            it[x] = itemEntities.get(x);
                        }

                        c.itemsToDrop(it);
                    }
                }
            }

            if (event.getEntity().getWorld().getGameRuleValue("doMobLoot").equalsIgnoreCase("true")) {
                int xp = c.getXp();
                int s = c.xpsize();
                if (s < 1) {
                    s = 1;
                }

                if (s > 9999) {
                    s = 9999;
                }

                List<ExperienceOrb> xps = new ArrayList();

                for (int x = 0; x < xp; ++x) {
                    if (xp > s) {
                        ExperienceOrb orb = (ExperienceOrb) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
                        orb.setExperience(s);
                        xp -= s;
                        xps.add(orb);
                    } else {
                        x = xp;
                    }
                }

                if (xp > 0) {
                    ExperienceOrb orb = (ExperienceOrb) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
                    orb.setExperience(xp);
                    xps.add(orb);
                }

                if (xps.size() > 0) {
                    ExperienceOrb[] e = new ExperienceOrb[xps.size()];

                    for (int x = 0; x < xps.size(); ++x) {
                        e[x] = xps.get(x);
                    }

                    c.xpToDrop(e);
                }
            }

            c.onDeath(event);
            c.hideBar();
        }

    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null && c.entity instanceof Creature) {
                c.onTarget(event);
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (CustomEntity.getByUUID(event.getRightClicked().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getRightClicked().getUniqueId());
            if (c.entity != null) {
                c.onInteract(event);
            }
        }

    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onShootBow(event);
            }
        }

    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onRegainHealth(event);
            }
        }

    }

    @EventHandler
    public void onRegrowWool(SheepRegrowWoolEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onRegrowWool(event);
            }
        }

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onEntityChangeBlock(event);
            }
        }

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getDamager().getUniqueId()) != null && event.getEntity() instanceof Player) {
            CustomEntity c = CustomEntity.getByUUID(event.getDamager().getUniqueId());
            if (c.entity != null) {
                c.onDamagePlayer(event);
            }
        }

    }

    @EventHandler
    public void onDyeWoolSheep(SheepDyeWoolEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onDyeWoolSheep(event);
            }
        }

    }

    @EventHandler
    public void onDamagePlayerWithProjectile(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Entity) {
                Entity e = (Entity) proj.getShooter();
                CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
                if (c != null && c.entity != null) {
                    if (c.getProjectileDamage() > 0.0D) {
                        event.setDamage(c.getProjectileDamage());
                    }

                    if (event.getEntity() instanceof Player) {
                        c.onDamagePlayerWithProjectile(event);
                    }

                    c.onDamageEntityWithProjectile(event);
                }
            }
        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getDamager().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getDamager().getUniqueId());
            if (c.entity != null) {
                c.onDamageEntity(event);
                if (c.getAttackDamage() > 0) {
                    event.setDamage(c.getAttackDamage());
                }
            }
        }

    }

    @EventHandler
    public void onKillEntityWithProjectile(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Entity) {
                Entity e = (Entity) proj.getShooter();
                CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
                if (c != null && c.entity != null && event.getEntity() instanceof LivingEntity) {
                    LivingEntity l = (LivingEntity) event.getEntity();
                    if ((int) (l.getHealth() - event.getFinalDamage()) <= 0) {
                        c.onKillEntityWithProjectile(event);
                    }
                }
            }
        }

    }

    @EventHandler
    public void onKillEntity(final EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getDamager().getUniqueId()) != null) {
            final CustomEntity c = CustomEntity.getByUUID(event.getDamager().getUniqueId());
            if (c.entity != null && event.getEntity() instanceof LivingEntity) {
                SchedulerTask task = new SchedulerTask();
                task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
                    public void run() {
                        if (event.getEntity().isDead()) {
                            c.onKillEntity(event);
                        }

                    }
                }, 5L));
            }
        }

    }

    @EventHandler
    public void onKillPlayer(final EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getDamager().getUniqueId()) != null && event.getEntity() instanceof Player) {
            final CustomEntity c = CustomEntity.getByUUID(event.getDamager().getUniqueId());
            if (c.entity != null) {
                final Player player = (Player) event.getEntity();
                SchedulerTask task = new SchedulerTask();
                task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
                    public void run() {
                        if (player.isDead()) {
                            c.onKillPlayer(event);
                        }

                    }
                }, 5L));
            }
        }

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onExplode(event);
            }
        }

    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && CustomEntity.getByReplacedEntity(event.getEntity().getType()) != null) {
            CustomEntity c = CustomEntity.getByReplacedEntity(event.getEntity().getType());
            if (c.spawnNaturally() && c.canSpawn(event.getEntity().getWorld().getBiome(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) && c.getSpawnChance() > 0) {
                int r = (new Random()).nextInt(100) + 1;
                if (c.getSpawnChance() >= r) {
                    event.setCancelled(true);
                    c.spawn(event.getEntity().getLocation());
                    c.onSpawnNaturally(event);
                }
            }
        }

    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onSlimeSplit(event);
            }
        }

    }

    @EventHandler
    public void onCreeperPower(CreeperPowerEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                c.onCreeperPower(event);
            }
        }

    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onCombust(event);
                if (event instanceof EntityCombustByBlockEvent) {
                    EntityCombustByBlockEvent b = (EntityCombustByBlockEvent) event;
                    c.onCombustByBlock(b);
                }

                if (event instanceof EntityCombustByEntityEvent) {
                    EntityCombustByEntityEvent b = (EntityCombustByEntityEvent) event;
                    c.onCombustByEntity(b);
                }
            }
        }

    }

    @EventHandler
    public void onInterPortal(EntityPortalEnterEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onInterPortal(event);
            }
        }

    }

    @EventHandler
    public void onExitPortal(EntityPortalExitEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onExitPortal(event);
            }
        }

    }

    @EventHandler
    public void onBreakDoor(EntityBreakDoorEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onEntityBreakDoor(event);
            }
        }

    }

    @EventHandler
    public void onCreatePortal(EntityCreatePortalEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onEntityCreatePortal(event);
            }
        }

    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onTame(event);
            }
        }

    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onTeleport(event);
            }
        }

    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onHorseJump(event);
            }
        }

    }

    @EventHandler
    public void onAquireTrade(VillagerAcquireTradeEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onAquireTrade(event);
            }
        }

    }

    @EventHandler
    public void onReplenishTrade(VillagerReplenishTradeEvent event) {
        Entity e = event.getEntity();
        if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c.entity != null) {
                c.onReplenishTrade(event);
            }
        }

    }

    @EventHandler
    public void onKilledByPlayer(final EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null && event.getDamager() instanceof Player) {
            final CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                final Player player = (Player) event.getDamager();
                SchedulerTask task = new SchedulerTask();
                task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
                    public void run() {
                        if (event.getEntity().isDead()) {
                            c.onKilledByPlayer(event, player);
                        }

                    }
                }, 5L));
            }
        }

    }

    @EventHandler
    public void onKilledByEntity(final EntityDamageByEntityEvent event) {
        if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
            final CustomEntity c = CustomEntity.getByUUID(event.getEntity().getUniqueId());
            if (c.entity != null) {
                SchedulerTask task = new SchedulerTask();
                task.setId(LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
                    public void run() {
                        if (event.getEntity().isDead()) {
                            c.onKilledByEntity(event);
                        }

                    }
                }, 5L));
            }
        }

    }

    @EventHandler
    public void onChunkLoaded(ChunkLoadEvent event) {
        if (event.getChunk().getEntities().length > 0) {
            Entity[] var5;
            int var4 = (var5 = event.getChunk().getEntities()).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                Entity e = var5[var3];
                if (CustomEntity.getByUUID(e.getUniqueId()) != null) {
                    CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
                    c.entity = e;
                    c.onChunkLoad();
                }
            }
        }

    }

    @EventHandler
    public void onProjectileSpawn(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Entity) {
            Entity e = (Entity) event.getEntity().getShooter();
            if (CustomEntity.getByUUID(event.getEntity().getUniqueId()) != null) {
                CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
                if (c.entity != null) {
                    c.onShootProjectile(event);
                }
            }
        }

    }
}

