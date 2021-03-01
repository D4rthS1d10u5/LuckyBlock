package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityHealerTag;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.EffectUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public class CustomEntityHealer extends CustomEntity {

    private EnderCrystal enderCrystal;
    @Setter
    private double healValue = 0.0D;
    @Setter
    private int delay = 0;
    @Setter
    private boolean damageNearby = true;
    @Getter @Setter
    private int health = 100;
    @Setter
    private boolean running;
    @Setter
    private LivingEntity healEntity;
    @Setter
    private boolean damageable = true;

    @Override
    public EntityType entityType() {
        return EntityType.ENDER_CRYSTAL;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        EnderCrystal enderCrystal = (EnderCrystal) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        enderCrystal.setInvulnerable(true);
        enderCrystal.setShowingBottom(true);
        this.enderCrystal = enderCrystal;
        CustomEntityHealerTag healerTag = new CustomEntityHealerTag();
        healerTag.attachEntity(this, new double[]{0.0D, 1.3D, 0.0D});
        startTimers();
        return enderCrystal;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return DamageCause.values();
    }

    @Override
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if (this.running && this.damageable && event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            ItemStack heldItem = p.getInventory().getItemInMainHand();
            if (ItemStackUtils.hasEnchantment(heldItem, Enchantment.DIG_SPEED, 5)) {
                this.health = (int) (event.getDamage() / 5D);
                if(this.health < 0) this.health = 0;

                EffectUtils.playEffects(Particle.FLAME, enderCrystal.getLocation().add(0.0D, 0.5D, 0.0D), 30, new double[]{0.2D, 0.0D, 0.2D}, 1.0F);
                if (this.health < 1) {
                    for (int x = RandomUtils.nextInt(5) + 100; x > 0; --x) {
                        Item item = enderCrystal.getWorld().dropItem(enderCrystal.getLocation(), new ItemStack(Material.GLASS, 1));
                        item.setPickupDelay(2000);
                        Scheduler.later(item::remove, (RandomUtils.nextInt(20) + 6) * 3);
                    }

                    EffectUtils.playFixedSound(enderCrystal.getLocation(), EffectUtils.getSound("boss_healer_death"), 1.0F, 1.0F, 8);
                    CustomEntityManager.removeCustomEntity(this);
                    return;
                }

                EffectUtils.playFixedSound(enderCrystal.getLocation(), EffectUtils.getSound("boss_healer_damage"), 1.0F, 1.0F, 8);
                this.damageable = false;
                Scheduler.later(() -> this.damageable = true, 20);
            }
        }
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("HealValue", this.healValue);
        if(healEntity != null) {
            c.set("HealEntity", this.healEntity.getUniqueId().toString());
        }
        c.set("Running", this.running);
        c.set("Delay", this.delay);
        c.set("DamageNearby", this.damageNearby);
        c.set("Health", this.health);
    }

    @Override
    public void onLoad(final ConfigurationSection c) {
        enderCrystal = (EnderCrystal) linkedEntity;
        this.healValue = c.getDouble("HealValue");
        this.delay = c.getInt("Delay");
        this.running = c.getBoolean("Running");
        this.damageNearby = c.getBoolean("DamageNearby");
        this.health = c.getInt("Health");
        Scheduler.later(() -> {
            String healEntity = c.getString("HealEntity");
            if(healEntity == null) {
                CustomEntityManager.removeCustomEntity(this);
                return;
            }

            CustomEntity healingTarget = CustomEntityManager.getCustomEntity(UUID.fromString(healEntity));
            this.healEntity = (LivingEntity) healingTarget.getLinkedEntity();
            if(running) {
                enableHealing();
            }

            startTimers();
        }, 15);
    }

    public void enableHealing() {
        if (this.delay > 0 && this.healValue > 0.0D && this.healEntity != null) {
            this.running = true;
            Scheduler.create(() -> {
                if (running && healEntity.getHealth() < healEntity.getMaxHealth()) {
                    if (healEntity.getHealth() + healValue < healEntity.getMaxHealth()) {
                        healEntity.setHealth(healEntity.getHealth() + healValue);
                    } else {
                        healEntity.setHealth(healEntity.getMaxHealth());
                    }

                    EffectUtils.playFixedSound(enderCrystal.getLocation(), EffectUtils.getSound("boss_healer_heal"), 1.0F, 2.0F, 8);
                    EffectUtils.playEffects(Particle.HEART, enderCrystal.getLocation(), 5, new double[]{0.7D, 0.7D, 0.7D}, 0.0F);
                }
            }).predicate(() -> this.isValid() && healEntity != null && healEntity.isValid()).onCancel(() -> CustomEntityManager.removeCustomEntity(this)).timer(delay, delay);
        }
    }

    private void startTimers() {
        Scheduler.create(() -> enderCrystal.setBeamTarget(healEntity.getLocation()))
                .predicate(() -> this.isValid() && healEntity != null && healEntity.isValid())
                .onCancel(() -> CustomEntityManager.removeCustomEntity(this))
                .timer(20, 10);

        Scheduler.create(() -> {
            if(damageNearby) {
                Collection<Player> players = enderCrystal.getWorld().getNearbyPlayers(enderCrystal.getLocation(), 3, 2, 3);
                if(!players.isEmpty()) {
                    EffectUtils.playEffects(Particle.CRIT, enderCrystal.getLocation(), 250, new double[]{1.5D, 0.0D, 1.5D}, 0.0F);
                    players.forEach(player -> player.damage(3));
                }
            }
        }).predicate(this::isValid).timer(20, 15);
    }
}
