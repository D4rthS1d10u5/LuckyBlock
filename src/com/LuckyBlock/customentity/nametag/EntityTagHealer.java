package com.LuckyBlock.customentity.nametag;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.customentity.boss.main.EntityHealer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.core.entity.CustomEntity;
import org.core.logic.ITask;

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
            ITask task = new ITask();
            task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
                public void run() {
                    CustomEntity c = CustomEntity.getByUUID(uuid);
                    if (c != null) {
                        EntityTagHealer.this.source = (EntityHealer)c;
                    }

                }
            }, 5L));
        }

    }
}
