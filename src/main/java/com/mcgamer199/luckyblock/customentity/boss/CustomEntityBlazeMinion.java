package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.CustomEntityElementalCreeper;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.EffectUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CustomEntityBlazeMinion extends CustomEntity {

    private Blaze blaze;
    private boolean ai = false;
    private final int startHealth = 20;

    public CustomEntityBlazeMinion() {
        registerTargetPriority(EntityType.PLAYER, 30);
        registerTargetPriority(EntityType.GIANT, 20);
        registerTargetPriority(EntityType.IRON_GOLEM, 10);
        registerTargetPriority(EntityType.VILLAGER, 10);
        registerDropItem(new ItemStack(Material.BLAZE_POWDER, RandomUtils.nextInt(2) + 3), 70);
        registerDropItem(CustomEntityBossKnight.spellFortune, 55);
    }

    @Override
    public EntityType entityType() {
        return EntityType.BLAZE;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Blaze blaze = (Blaze) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        blaze.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.startHealth);
        blaze.setHealth(this.startHealth);
        blaze.setCustomNameVisible(true);
        blaze.setAI(false);
        blaze.setInvulnerable(true);
        blaze.setSilent(true);
        blaze.setRemoveWhenFarAway(false);
        this.blaze = blaze;
        startTimers();
        return blaze;
    }

    @Override
    public void onTick() {
        if (this.blaze.getLocation().getBlock().getType().isSolid() && this.blaze.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
            this.blaze.getLocation().getBlock().breakNaturally();
            this.blaze.getLocation().getBlock().getRelative(BlockFace.UP).breakNaturally();
        }
    }

    @Override
    public int getTickTime() {
        return 10;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        DamageCause[] basic = new DamageCause[] {DamageCause.CONTACT, DamageCause.DROWNING, DamageCause.FALL, DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.LIGHTNING, DamageCause.SUFFOCATION};
        return ArrayUtils.addAll(basic, ai ? DamageCause.THORNS : DamageCause.DRAGON_BREATH, DamageCause.POISON, DamageCause.PROJECTILE, DamageCause.WITHER, DamageCause.MAGIC);
    }

    @Override
    public int getXPtoDrop() {
        return 850;
    }

    @Override
    public double getAttackDamage() {
        return 32D;
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 5;
    }

    @Override
    public List<String> getCustomNames() {
        if (this.ai) {
            double f = this.blaze.getHealth() / (double) this.startHealth * 100.0D;
            return Collections.singletonList(ChatColor.YELLOW + "Health " + ChatColor.GREEN + (int) f + ChatColor.WHITE + "%");
        } else {
            return Collections.singletonList(ChatColor.LIGHT_PURPLE + "Blaze");
        }
    }

    @Override
    public boolean isAttackingNearbyEntities() {
        return true;
    }

    @Override
    public void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * (double) (RandomUtils.nextInt(3) + 2));
        event.getEntity().setFallDistance(25.0F);
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.ai) {
            if (event.getDamager() instanceof Arrow) {
                if (RandomUtils.nextPercent(90)) {
                    event.setCancelled(true);
                    Arrow arrow = (Arrow) event.getDamager();
                    arrow.remove();
                    Arrow a = this.blaze.launchProjectile(Arrow.class);
                    a.setVelocity(arrow.getVelocity().multiply(-1));
                }
            } else if (event.getDamager() instanceof ShulkerBullet) {
                event.setCancelled(true);
            } else if (event.getDamager() instanceof LivingEntity && RandomUtils.nextPercent(65)) {
                event.setCancelled(true);
                EffectUtils.playFixedSound(this.blaze.getLocation(), EffectUtils.getSound("boss_blaze_shield"), 1.0F, 1.0F, 25);
                this.blaze.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.blaze.getLocation(), 200, 1.0D, 1.0D, 1.0D, 0.0D);
            }
        }
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("ai", this.ai);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        this.ai = c.getBoolean("ai");
    }

    @Override
    public void onChunkLoad() {
        this.blaze = (Blaze) linkedEntity;
        startTimers();
    }

    public void startAttacking() {
        this.blaze.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.blaze.getLocation(), 50, 2.0D, 2.0D, 2.0D, 1.0D);
        this.blaze.setAI(true);
        this.ai = true;
        this.blaze.setInvulnerable(false);
    }

    private void startTimers() {
        Scheduler.create(() -> {
            LivingEntity target = blaze.getTarget();
            if(ai && target != null) {
                if(RandomUtils.nextPercent(50)) {
                    SmallFireball smallFireball = blaze.launchProjectile(SmallFireball.class, blaze.getLocation().getDirection());

                    for (int i = 0; i < 3; i++) {
                        Scheduler.create(() -> blaze.launchProjectile(SmallFireball.class,  smallFireball.getVelocity().clone()
                                .add(new Vector((RandomUtils.nextInt(20) - 10) / 70D,
                                        (RandomUtils.nextInt(20) - 10) / 70D,
                                        (RandomUtils.nextInt(20) - 10) / 70D)))).count(RandomUtils.nextInt(5) + 25).timer(1, 1);
                    }
                } else {
                    CustomEntityElementalCreeper elementalCreeper = new CustomEntityElementalCreeper();
                    elementalCreeper.setLife(85);
                    elementalCreeper.setBlockMaterial(Material.STONE);
                    elementalCreeper.spawn(blaze.getLocation());
                    ((Creeper) elementalCreeper.getLinkedEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.38D);
                    ((Creeper) elementalCreeper.getLinkedEntity()).setTarget(target);
                }
            }
        }).predicate(this::isValid).timer(80, 80);
    }
}
