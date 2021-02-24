package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.customentity.CustomEntityHealer;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomEntityHealerTag extends CustomEntity {

    private ArmorStand armorStand;
    private CustomEntityHealer source;
    private double[] offset = new double[3];

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation.add(offset[0], offset[1], offset[1]), entityType());
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        this.armorStand = armorStand;

        Scheduler.create(() -> armorStand.teleport(source.getLinkedEntity().getLocation().add(offset[0], offset[1], offset[1])))
                .predicate(() -> this.isValid() && source.isValid()).timer(1, 1);
        Scheduler.later(() -> armorStand.setCustomNameVisible(true), 10);
        return armorStand;
    }

    public void attachEntity(CustomEntityHealer source, double[] offset) {
        this.source = source;
        this.offset = offset;
    }

    @Override
    public void onTick() {
        if(source != null) {
            this.armorStand.setCustomName(source.getHealth() > 20 ? "§5§l" : "§4§l" + source.getHealth());
        }
    }

    @Override
    public int getTickTime() {
        return 5;
    }

    @Override
    public final void onSave(ConfigurationSection c) {
        c.set("Offset.X", this.offset[0]);
        c.set("Offset.Y", this.offset[1]);
        c.set("Offset.Z", this.offset[2]);
        if (this.source != null) {
            c.set("Source", this.source.getLinkedEntity().getUniqueId().toString());
        }
    }

    @Override
    public final void onLoad(final ConfigurationSection c) {
        this.offset[0] = c.getDouble("Offset.X");
        this.offset[1] = c.getDouble("Offset.Y");
        this.offset[2] = c.getDouble("Offset.Z");
        this.armorStand = (ArmorStand) this.linkedEntity;

        if (c.getString("Source") != null) {
            final UUID uuid = UUID.fromString(c.getString("Source"));
            Scheduler.later(() -> {
                CustomEntityHealer healer = CustomEntityManager.getCustomEntity(uuid);
                if (healer != null) {
                    this.source = healer;
                }
            }, 5);
        }
    }
}
