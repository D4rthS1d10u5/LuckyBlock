package com.mcgamer199.luckyblock.customentity.lct;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class CustomEntityLCTItem extends CustomEntity {

    private LuckyCraftingTable luckyCraftingTable;
    private ArmorStand armorStand;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setVisible(false);
        this.armorStand = armorStand;
        return armorStand;
    }

    public void spawn(LuckyCraftingTable luckyCraftingTable) {
        this.luckyCraftingTable = luckyCraftingTable;
        super.spawn(luckyCraftingTable.getBlock().getLocation().add(0.5D, -0.9D, 0.5D));
    }

    @Override
    public int getTickTime() {
        return 5;
    }

    @Override
    public void onTick() {
        if(luckyCraftingTable == null || !luckyCraftingTable.isValid()) {
            CustomEntityManager.removeCustomEntity(this);
        }
    }

    private void startTimer() {
        Scheduler.create(() -> {
            boolean setAir = false;
            if (luckyCraftingTable.slot > -1 && luckyCraftingTable.isRunning() && luckyCraftingTable.i().getItem(luckyCraftingTable.slot) != null && luckyCraftingTable.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                this.armorStand.setHelmet(luckyCraftingTable.i().getItem(luckyCraftingTable.slot));
                setAir = true;
            }

            if (!setAir) {
                this.armorStand.setHelmet(null);
            }
        }).predicate(() -> luckyCraftingTable != null && luckyCraftingTable.isValid()).timer(20, 2);
    }

    public void onLoad(final ConfigurationSection c) {
        this.armorStand = (ArmorStand) this.linkedEntity;
        Scheduler.later(() -> this.luckyCraftingTable = LuckyCraftingTable.getByBlock(LocationUtils.blockFromString(c.getString("LCT_Block"))), 10);
    }

    public void onSave(ConfigurationSection c) {
        c.set("LCT_Block", LocationUtils.asString(this.luckyCraftingTable.getBlock().getLocation()));
    }
}
