package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class CustomEntityFloatingText extends CustomEntity {

    @Setter
    private int age = 20;
    @Setter
    private int shiftCount = 80;
    @Setter
    private String text = "No Text";

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomName(this.text);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setBasePlate(false);

        Scheduler.create(() -> {
            double deltaY =  Math.abs(Math.sin(shiftCount));
            armorStand.teleport(armorStand.getLocation().add(0.0D, deltaY / 30.0D, 0.0D));
            shiftCount -= 4;
            this.age--;
            if(age <= 0 || shiftCount <= 0) {
                CustomEntityManager.removeCustomEntity(this);
            }
        }).predicate(() -> this.age > 0 && this.shiftCount > 0).timer(1, 1);
        return armorStand;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return DamageCause.values();
    }
}
