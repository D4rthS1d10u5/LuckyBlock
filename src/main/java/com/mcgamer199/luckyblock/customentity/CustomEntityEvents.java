package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CustomEntityEvents implements Listener {

    public CustomEntityEvents() {
    }

    public static void onSpawn(Entity entity) {
        CustomEntity customEntity = CustomEntity.getByUUID(entity.getUniqueId());
        if(customEntity != null && customEntity.entity != null && customEntity.getTickTime() > -1) {
            Scheduler.timer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (customEntity.entity != null && !customEntity.entity.isDead()) {
                        customEntity.onTick();
                    } else {
                        Scheduler.cancelTask(this);
                    }
                }
            }, customEntity.getTickTime(), customEntity.getTickTime());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            if(customEntity.isImmuneTo(event.getCause())) {
                event.setCancelled(true);
            }

            if(customEntity.getDefense() > 0D) {
                event.setDamage(event.getFinalDamage() / customEntity.getDefense());
            }

            customEntity.onDamage(event);

            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                customEntity.onDamageByEntity(e);
                if (e.getDamager() instanceof Player) {
                    customEntity.onDamageByPlayer(e);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            if (customEntity.getDeathParticles() != null) {
                if (customEntity.particleOptions() != null) {
                    if (customEntity.particleOptions().length == 10) {
                        double x1 = customEntity.particleOptions()[0];
                        double y1 = customEntity.particleOptions()[1];
                        double z1 = customEntity.particleOptions()[2];
                        event.getEntity().getWorld().spawnParticle(customEntity.getDeathParticles(), event.getEntity().getLocation().add(x1, y1, z1), (int) customEntity.particleOptions()[3], customEntity.particleOptions()[6], customEntity.particleOptions()[7], customEntity.particleOptions()[8], customEntity.particleOptions()[9]);
                    }
                } else {
                    event.getEntity().getWorld().spawnParticle(customEntity.getDeathParticles(), event.getEntity().getLocation(), 25, 0.3D, 0.3D, 0.3D, 0.0D);
                }
            }

            if (!customEntity.defaultDrops() && event.getEntity().getWorld().getGameRuleValue("doMobLoot").equalsIgnoreCase("true")) {
                event.getDrops().clear();
                if (!customEntity.itemsEdited()) {
                    ItemStack[] items = customEntity.getRandomItems();

                    for (int s = 0; s < items.length; ++s) {
                        if (items[s] != null && customEntity.getDrops()[s] != null) {
                            event.getDrops().add(items[s]);
                        }
                    }
                } else {
                    List<Item> itemEntities = new ArrayList<>();
                    ItemStack[] items = customEntity.getRandomItems();

                    for (int x = 0; x < items.length; ++x) {
                        if (items[x] != null && customEntity.getDrops()[x] != null) {
                            Item i = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), items[x]);
                            itemEntities.add(i);
                        }
                    }

                    if (itemEntities.size() > 0) {
                        Item[] it = new Item[itemEntities.size()];

                        for (int x = 0; x < itemEntities.size(); ++x) {
                            it[x] = itemEntities.get(x);
                        }

                        customEntity.itemsToDrop(it);
                    }
                }
            }

            if (event.getEntity().getWorld().getGameRuleValue("doMobLoot").equalsIgnoreCase("true")) {
                int xp = customEntity.getXp();
                int s = customEntity.xpsize();
                if (s < 1) {
                    s = 1;
                }

                if (s > 9999) {
                    s = 9999;
                }

                List<ExperienceOrb> xps = new ArrayList<>();

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

                    customEntity.xpToDrop(e);
                }
            }

            customEntity.onDeath(event);
            customEntity.hideBar();
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onTarget(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getRightClicked().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onInteract(event);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onShootBow(event);
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onRegainHealth(event);
        }
    }

    @EventHandler
    public void onRegrowWool(SheepRegrowWoolEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onRegrowWool(event);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onEntityChangeBlock(event);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && event.getEntity() instanceof Player && customEntity.entity != null) {
            customEntity.onDamagePlayer(event);
        }
    }

    @EventHandler
    public void onDyeWoolSheep(SheepDyeWoolEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onDyeWoolSheep(event);
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
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onDamageEntity(event);
            if(customEntity.getAttackDamage() > 0) {
                event.setDamage(customEntity.getAttackDamage());
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
        CustomEntity customEntity = CustomEntity.getByUUID(event.getDamager().getUniqueId());
        if(customEntity != null && customEntity.entity != null && event.getEntity() instanceof LivingEntity) {
            Scheduler.later(() -> {
                if(event.getEntity().isDead()) {
                    customEntity.onKillEntity(event);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onKillPlayer(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getDamager().getUniqueId());
        Entity entity = event.getEntity();
        if(customEntity != null && entity instanceof Player && customEntity.entity != null) {
            Scheduler.later(() -> {
                Player player = (Player) entity;
                if(player.isDead()) {
                    customEntity.onKillPlayer(event);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onExplode(event);
        }
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        CustomEntity customEntity = CustomEntity.getByReplacedEntity(event.getEntity().getType());
        if(customEntity != null && customEntity.entity != null && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            Location entityLocation = entity.getLocation();
            if (customEntity.spawnNaturally() && customEntity.canSpawn(entity.getWorld().getBiome(entityLocation.getBlockX(), entityLocation.getBlockZ())) && customEntity.getSpawnChance() > 0) {
                if (customEntity.getSpawnChance() >= (RandomUtils.nextInt(100) + 1)) {
                    event.setCancelled(true);
                    customEntity.spawn(event.getEntity().getLocation());
                    customEntity.onSpawnNaturally(event);
                }
            }
        }
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onSlimeSplit(event);
        }
    }

    @EventHandler
    public void onCreeperPower(CreeperPowerEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onCreeperPower(event);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onCombust(event);
            if (event instanceof EntityCombustByBlockEvent) {
                EntityCombustByBlockEvent combustByBlockEvent = (EntityCombustByBlockEvent) event;
                customEntity.onCombustByBlock(combustByBlockEvent);
            }

            if (event instanceof EntityCombustByEntityEvent) {
                EntityCombustByEntityEvent entityCombustByEntityEvent = (EntityCombustByEntityEvent) event;
                customEntity.onCombustByEntity(entityCombustByEntityEvent);
            }
        }
    }

    @EventHandler
    public void onInterPortal(EntityPortalEnterEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onEnterPortal(event);
        }
    }

    @EventHandler
    public void onExitPortal(EntityPortalExitEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onExitPortal(event);
        }
    }

    @EventHandler
    public void onBreakDoor(EntityBreakDoorEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onEntityBreakDoor(event);
        }
    }

    @EventHandler
    public void onCreatePortal(EntityCreatePortalEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onEntityCreatePortal(event);
        }
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onTame(event);
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onTeleport(event);
        }
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onHorseJump(event);
        }
    }

    @EventHandler
    public void onAquireTrade(VillagerAcquireTradeEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onAquireTrade(event);
        }
    }

    @EventHandler
    public void onReplenishTrade(VillagerReplenishTradeEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            customEntity.onReplenishTrade(event);
        }
    }

    @EventHandler
    public void onKilledByPlayer(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            Player player = (Player) event.getDamager();
            Scheduler.later(() -> {
                if(event.getEntity().isDead()) {
                    customEntity.onKilledByPlayer(event, player);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onKilledByEntity(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.entity != null) {
            Scheduler.later(() -> {
                if(event.getEntity().isDead()) {
                    customEntity.onKilledByEntity(event);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onChunkLoaded(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            CustomEntity customEntity = CustomEntity.getByUUID(entity.getUniqueId());
            if(customEntity != null) {
                customEntity.entity = entity;
                customEntity.onChunkLoad();
            }
        }
    }

    @EventHandler
    public void onProjectileSpawn(ProjectileLaunchEvent event) {
        CustomEntity customEntity = CustomEntity.getByUUID(event.getEntity().getUniqueId());
        if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Entity && customEntity != null && customEntity.entity != null) {
            customEntity.onShootProjectile(event);
        }
    }
}

