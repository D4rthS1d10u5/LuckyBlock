package com.mcgamer199.luckyblock.customentity.lct;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class CustomEntityLCTNameTag extends CustomEntity {

    private LuckyCraftingTable luckyCraftingTable;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand stand = (ArmorStand) spawnLocation.getBlock().getWorld().spawnEntity(spawnLocation, entityType());
        stand.setCustomName(ChatColor.GREEN + MyTasks.val("lct.display_name", false));
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setVisible(false);
        return stand;
    }

    public void spawn(LuckyCraftingTable luckyCraftingTable) {
        this.luckyCraftingTable = luckyCraftingTable;
        super.spawn(luckyCraftingTable.getBlock().getLocation().add(0.5D, 1.2D, 0.5D));
    }

    @Override
    public int getTickTime() {
        return 1;
    }

    @Override
    public void onTick() {
        if(luckyCraftingTable != null && luckyCraftingTable.isValid()) {
            CustomEntityManager.removeCustomEntity(this);
        }
    }

    public void onSave(ConfigurationSection c) {
        c.set("LCT_Block", LocationUtils.asString(this.luckyCraftingTable.getBlock().getLocation()));
    }

    public void onLoad(final ConfigurationSection c) {
        Scheduler.later(() -> {
            String block = c.getString("LCT_Block");
            if (block != null) {
                LuckyCraftingTable table = LuckyCraftingTable.getByBlock(LocationUtils.blockFromString(block));
                if(table != null) {
                    this.luckyCraftingTable = table;
                    linkedEntity.setCustomName(ChatColor.GREEN + MyTasks.val("lct.display_name", false));
                }
            } else {
                CustomEntityManager.removeCustomEntity(this);
            }
        }, 15);
    }
}
