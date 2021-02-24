package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CustomEntityUnderwaterMinion extends CustomEntity {

    @Setter
    private int age;
    @Setter
    private LivingEntity target;

    @Override
    public EntityType entityType() {
        return EntityType.GUARDIAN;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Guardian g = (Guardian) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.GUARDIAN);
        g.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(37.0D);
        g.setHealth(37.0D);
        g.setCollidable(false);
        g.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999, 2));

        if (this.target != null) {
            g.setTarget(this.target);
        }

        Scheduler.later(() -> CustomEntityManager.removeCustomEntity(this), age);
        return g;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION};
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("Age", this.age);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        this.age = c.getInt("Age");
        Scheduler.later(() -> CustomEntityManager.removeCustomEntity(this), age);
    }
}
