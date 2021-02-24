package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CustomEntitySuperSlime extends CustomEntity {

    @Getter @Setter
    private int size = 2;

    public CustomEntitySuperSlime() {
        registerDropItem(new ItemStack(Material.EXP_BOTTLE, RandomUtils.nextInt(3) + 2), 65);
    }

    @Override
    public EntityType entityType() {
        return EntityType.SLIME;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Slime slime = (Slime) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        slime.setSize(this.size);
        slime.setMaxHealth(20.0D);
        slime.setHealth(20.0D);
        slime.setCustomName("Super Slime");
        slime.setCustomNameVisible(true);
        return slime;
    }

    @Override
    public void onTick() {
        for (Entity e : this.linkedEntity.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
            if (e instanceof LivingEntity) {
                LivingEntity l = (LivingEntity) e;
                if (!(l instanceof Player) && l.getHealth() < l.getMaxHealth() && l.getHealth() > 0.0D) {
                    try {
                        l.setHealth(l.getHealth() + 3.0D);
                    } catch (Exception var6) {
                        l.setHealth(l.getMaxHealth());
                    }

                    this.linkedEntity.getWorld().spawnParticle(Particle.HEART, l.getLocation(), 30, 0.7D, 0.7D, 0.7D, 0.0D);
                }
            }
        }

        LivingEntity l = (LivingEntity) this.linkedEntity;
        if (l.getHealth() < l.getMaxHealth() && l.getHealth() > 0.0D) {
            try {
                l.setHealth(l.getHealth() + 3.0D);
            } catch (Exception var5) {
                l.setHealth(l.getMaxHealth());
            }

            this.linkedEntity.getWorld().spawnParticle(Particle.HEART, l.getLocation(), 28, 0.7D, 0.7D, 0.7D, 0.0D);
        }
    }

    @Override
    public int getTickTime() {
        return 85;
    }

    @Override
    public int getXPtoDrop() {
        return 150;
    }

    @Override
    public double getAttackDamage() {
        return 15;
    }

    @Override
    public double getDefense() {
        return 1.5D;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.POISON};
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 25;
    }

    @Override
    public List<String> getCustomNames() {
        return Arrays.asList("§aSuper Slime", "§1Super Slime");
    }
}
