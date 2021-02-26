package com.mcgamer199.luckyblock.api.customentity;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.collect.Iterators;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
@Getter
public abstract class CustomEntity {

    protected Entity linkedEntity;
    protected UUID entityUuid;
    private final EnumMap<EntityType, Integer> targetPriorities = new EnumMap<>(EntityType.class);
    private final Map<ItemStack, Integer> dropItemChances = new HashMap<>();

    public CustomEntity() {}

    public abstract EntityType entityType();

    @NotNull
    public abstract Entity summonEntity(@NotNull Location spawnLocation);

    public abstract int getTickTime();

    public void spawn(Location location) {
        if(linkedEntity == null) {
            init(summonEntity(location));
            startTickTimer();
            startCustomNamesTimer();
        }
    }

    public final void init(@NotNull Entity entity) {
        this.linkedEntity = entity;
        this.entityUuid = linkedEntity.getUniqueId();
    }

    public final void registerTargetPriority(EntityType target, int priority) {
        targetPriorities.put(target, priority);
    }

    public final void registerDropItem(ItemStack stack, int dropChance) {
        dropItemChances.put(stack, dropChance);
    }

    public final boolean isValid() {
        return getLinkedEntity() != null && getLinkedEntity().isValid();
    }

    public final void startCustomNamesTimer() {
        List<String> customNames = getCustomNames();
        if(!customNames.isEmpty() && getCustomNamesTickDelay() > 0) {
            Iterator<String> names = Iterators.cycle(customNames);
            Scheduler.create(() -> linkedEntity.setCustomName(names.next()))
                    .predicate(() -> isValid() && names.hasNext())
                    .timerAsync(1, getCustomNamesTickDelay());
        }
    }

    public final void startTickTimer() {
        if(getTickTime() > 0) {
            Scheduler.create(this::onTick).predicate(this::isValid).timer(getTickTime(), getTickTime());
        }
    }

    public boolean isAttackingNearbyEntities() {
        return false;
    }

    public boolean isSpawnsNaturally() {
        return false;
    }

    public int getSpawnChance() {
        return 0;
    }

    public Biome[] getSpawnBiomes() {
        return Biome.values();
    }

    public int getCustomNamesTickDelay() {
        return -1;
    }

    public List<String> getCustomNames() {
        return Collections.emptyList();
    }

    public int getXPtoDrop() {
        return 100;
    }

    public double getAttackDamage() {
        return -1;
    }

    public double getDefense() {
        return -1;
    }

    public DamageCause[] getImmunityTo() {
        return new DamageCause[0];
    }

    public ParticleBuilder createDeathParticles() {
        return new ParticleBuilder(Particle.SMOKE_NORMAL).location(linkedEntity.getLocation());
    }

    public void onTick() {}

    public void onSave(ConfigurationSection configurationSection) {}

    public void onLoad(ConfigurationSection configurationSection) {}

    public void onDeath(EntityDeathEvent event) {}

    public void onDamage(EntityDamageEvent event) {}

    public void onTarget(EntityTargetLivingEntityEvent event) {}

    public void onInteract(PlayerInteractEntityEvent event) {}

    public void onRegainHealth(EntityRegainHealthEvent event) {}

    public void onShootBow(EntityShootBowEvent event) {}

    public void onRegrowWool(SheepRegrowWoolEvent event) {}

    public void onEntityChangeBlock(EntityChangeBlockEvent event) {}

    public void onDamagePlayer(EntityDamageByEntityEvent event) {}

    public void onDamageEntity(EntityDamageByEntityEvent event) {}

    public void onKillPlayer(EntityDamageByEntityEvent event) {}

    public void onKillEntity(EntityDamageByEntityEvent event) {}

    public void onDyeWoolSheep(SheepDyeWoolEvent event) {}

    public void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {}

    public void onDamagePlayerWithProjectile(EntityDamageByEntityEvent event) {}

    public void onKillEntityWithProjectile(EntityDamageByEntityEvent event) {}

    public void onExplode(EntityExplodeEvent event) {}

    public void onSpawnNaturally(CreatureSpawnEvent event) {}

    public void onSlimeSplit(SlimeSplitEvent event) {}

    public void onCreeperPower(CreeperPowerEvent event) {}

    public void onCombust(EntityCombustEvent event) {}

    public void onCombustByEntity(EntityCombustByEntityEvent event) {}

    public void onCombustByBlock(EntityCombustByBlockEvent event) {}

    public void onEnterPortal(EntityPortalEnterEvent event) {}

    public void onExitPortal(EntityPortalExitEvent event) {}

    public void onEntityBreakDoor(EntityBreakDoorEvent event) {}

    public void onEntityCreatePortal(EntityCreatePortalEvent event) {}

    public void onTame(EntityTameEvent event) {}

    public void onTeleport(EntityTeleportEvent event) {}

    public void onHorseJump(HorseJumpEvent event) {}

    public void onAcquireTrade(VillagerAcquireTradeEvent event) {}

    public void onReplenishTrade(VillagerReplenishTradeEvent event) {}

    public void onDamageByEntity(EntityDamageByEntityEvent event) {}

    public void onDamageByPlayer(EntityDamageByEntityEvent event) {}

    public void onKilledByPlayer(EntityDamageByEntityEvent event, Player player) {}

    public void onKilledByEntity(EntityDamageByEntityEvent event) {}

    public void onShootProjectile(ProjectileLaunchEvent event) {}

    public void onChunkLoad() {}
}
