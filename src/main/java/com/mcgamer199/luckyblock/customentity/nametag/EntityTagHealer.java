package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.boss.main.EntityHealer;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class EntityTagHealer extends EntityNameTag {
    public EntityHealer source;

    public EntityTagHealer() {
    }

    protected void tick() {
        if (this.source != null) {
            int s = this.source.health;
            if (s > 20) {
                this.armorStand.setCustomName("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + s);
            } else {
                this.armorStand.setCustomName("" + ChatColor.DARK_RED + ChatColor.BOLD + s);
            }
        }

    }

    protected int getTickTime() {
        return 5;
    }

    protected void onsave(ConfigurationSection c) {
        if (this.source != null) {
            c.set("Source", this.source.getEntity().getUniqueId().toString());
        }

    }

    protected void onload(ConfigurationSection c) {
        if (c.getString("Source") != null) {
            final UUID uuid = UUID.fromString(c.getString("Source"));
            Scheduler.later(() -> {
                CustomEntity customEntity = CustomEntity.getByUUID(uuid);
                if (customEntity != null) {
                    EntityTagHealer.this.source = (EntityHealer) customEntity;
                }
            }, 5);
        }

    }
}
