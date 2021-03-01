package com.mcgamer199.luckyblock.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.util.EntityUtils;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomEntityEvents implements Listener {

    public CustomEntityEvents() {}

    @EventHandler(ignoreCancelled = true)
    public void onUseCustomSpawnEgg(PlayerInteractEvent event) {
        if (event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            ItemStack item = event.getItem();

            Player player = event.getPlayer();

            NBTCompoundWrapper<?> itemTag = ItemStackUtils.getItemTag(item);
            if(itemTag.hasKeyOfType("EntityClass", 8)) {
                event.setCancelled(true);
                String entityClass = itemTag.getString("EntityClass");

                if(entityClass != null) {
                    CustomEntity customEntity = CustomEntityManager.createCustomEntity(entityClass);
                    if(customEntity == null) {
                        player.sendMessage("§cInvalid Entity!");
                        return;
                    }

                    Location loc;
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        Block b = player.getTargetBlock(null, 6);
                        if (b.getType() != Material.WATER && b.getType() != Material.STATIONARY_WATER) {
                            return;
                        }

                        loc = b.getLocation().add(0.5D, 0.5D, 0.5D);
                    } else {
                        loc = event.getClickedBlock().getLocation();
                        BlockFace f = event.getBlockFace();
                        if (f == BlockFace.UP) {
                            loc = loc.add(0.5D, 1.0D, 0.5D);
                        } else if (f == BlockFace.DOWN) {
                            loc = loc.add(0.5D, -2.0D, 0.5D);
                        } else if (f == BlockFace.EAST) {
                            loc = loc.add(1.5D, 0.0D, 0.5D);
                        } else if (f == BlockFace.WEST) {
                            loc = loc.add(-0.5D, 0.0D, 0.5D);
                        } else if (f == BlockFace.NORTH) {
                            loc = loc.add(0.5D, 0.0D, -0.5D);
                        } else if (f == BlockFace.SOUTH) {
                            loc = loc.add(0.5D, 0.0D, 1.5D);
                        }
                    }
                    customEntity.spawn(loc);
                } else {
                    player.sendMessage("§cInvalid Entity!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            if(ArrayUtils.contains(customEntity.getImmunityTo(), event.getCause())) {
                event.setCancelled(true);
                return;
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
        LivingEntity entity = event.getEntity();
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(entity.getUniqueId());
        if(customEntity != null) {
            System.out.println("validation success");
            CustomEntityManager.removeCustomEntity(customEntity);

            customEntity.createDeathParticles().spawn();
            World world = entity.getWorld();
            if(Boolean.parseBoolean(world.getGameRuleValue("doMobLoot"))) {
                boolean boss = customEntity instanceof CustomEntityBoss;

                if(!customEntity.getDropItemChances().isEmpty()) {
                    event.getDrops().clear();
                    customEntity.getDropItemChances().entrySet()
                            .stream()
                            .filter(entry -> RandomUtils.nextPercent(entry.getValue()))
                            .map(Map.Entry::getKey).forEach(itemStack -> {
                                Item item = world.dropItemNaturally(entity.getLocation(), itemStack);
                                if(boss) {
                                    item.setInvulnerable(true);
                                }
                            });
                }

                int xpToDrop = customEntity.getXPtoDrop();
                List<ExperienceOrb> orbs = new ArrayList<>();
                while (xpToDrop > 0) {
                    int xp = EntityUtils.getOrbValue(xpToDrop);
                    ExperienceOrb orb = ((ExperienceOrb) world.spawnEntity(entity.getLocation(), EntityType.EXPERIENCE_ORB));
                    orb.setExperience(xp);
                    orbs.add(orb);
                    xpToDrop -= xp;
                }

                if(boss) {
                    orbs.forEach(orb -> orb.setInvulnerable(true));
                }
            }

            customEntity.onDeath(event);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onTarget(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getRightClicked().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onInteract(event);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onShootBow(event);
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onRegainHealth(event);
        }
    }

    @EventHandler
    public void onRegrowWool(SheepRegrowWoolEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onRegrowWool(event);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onEntityChangeBlock(event);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && event.getEntity() instanceof Player && customEntity.isValid()) {
            customEntity.onDamagePlayer(event);
        }
    }

    @EventHandler
    public void onDyeWoolSheep(SheepDyeWoolEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onDyeWoolSheep(event);
        }
    }

    @EventHandler
    public void onDamagePlayerWithProjectile(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Entity) {
                Entity shooter = (Entity) projectile.getShooter();
                CustomEntity customEntity = CustomEntityManager.getCustomEntity(shooter.getUniqueId());
                if (customEntity != null && customEntity.isValid()) {
                    if (event.getEntity() instanceof Player) {
                        customEntity.onDamagePlayerWithProjectile(event);
                    } else {
                        customEntity.onDamageEntityWithProjectile(event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onDamageEntity(event);
            if(customEntity.getAttackDamage() > 0) {
                event.setDamage(customEntity.getAttackDamage());
            }
        }
    }

    @EventHandler
    public void onKillEntityWithProjectile(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Entity) {
                Entity e = (Entity) projectile.getShooter();
                Entity hitEntity = event.getEntity();
                CustomEntity customEntity = CustomEntityManager.getCustomEntity(e.getUniqueId());
                if (customEntity != null && customEntity.isValid() && hitEntity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) hitEntity;
                    if ((int) (livingEntity.getHealth() - event.getFinalDamage()) <= 0) {
                        customEntity.onKillEntityWithProjectile(event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKillEntity(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getDamager().getUniqueId());
        if(customEntity != null && customEntity.isValid() && event.getEntity() instanceof LivingEntity) {
            Scheduler.later(() -> {
                if(event.getEntity().isDead()) {
                    customEntity.onKillEntity(event);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onKillPlayer(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getDamager().getUniqueId());
        Entity entity = event.getEntity();
        if(customEntity != null && entity instanceof Player && customEntity.isValid()) {
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
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onExplode(event);
        }
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onSlimeSplit(event);
        }
    }

    @EventHandler
    public void onCreeperPower(CreeperPowerEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onCreeperPower(event);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
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
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onEnterPortal(event);
        }
    }

    @EventHandler
    public void onExitPortal(EntityPortalExitEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onExitPortal(event);
        }
    }

    @EventHandler
    public void onBreakDoor(EntityBreakDoorEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onEntityBreakDoor(event);
        }
    }

    @EventHandler
    public void onCreatePortal(EntityCreatePortalEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onEntityCreatePortal(event);
        }
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onTame(event);
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onTeleport(event);
        }
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onHorseJump(event);
        }
    }

    @EventHandler
    public void onAquireTrade(VillagerAcquireTradeEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onAcquireTrade(event);
        }
    }

    @EventHandler
    public void onReplenishTrade(VillagerReplenishTradeEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
            customEntity.onReplenishTrade(event);
        }
    }

    @EventHandler
    public void onKilledByPlayer(final EntityDamageByEntityEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid() && event.getDamager() instanceof Player) {
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
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(customEntity != null && customEntity.isValid()) {
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
            CustomEntity customEntity = CustomEntityManager.getCustomEntity(entity.getUniqueId());
            if(customEntity != null) {
                customEntity.init(entity);
                customEntity.startBasicEntityTimers();
                customEntity.onChunkLoad();
            }
        }
    }

    @EventHandler
    public void onProjectileSpawn(ProjectileLaunchEvent event) {
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(event.getEntity().getUniqueId());
        if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Entity && customEntity != null && customEntity.isValid()) {
            customEntity.onShootProjectile(event);
        }
    }

    @EventHandler
    public void onRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        Entity entity = event.getEntity();
        CustomEntity customEntity = CustomEntityManager.getCustomEntity(entity.getUniqueId());
        if(customEntity != null) {
            System.out.println("FOUND CustomEntity by uuid " + entity.getUniqueId());
        }
    }
}

