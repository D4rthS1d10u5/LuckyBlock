package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.resources.Trophy;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomEntityTrophyNameTag extends CustomEntity {

    private Trophy trophy;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) trophy.getBlock().getWorld().spawnEntity(spawnLocation, entityType());
        armorStand.setCustomName(trophy.getItemToDrop().getItemMeta().getDisplayName());
        armorStand.setCustomNameVisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(true);
        return armorStand;
    }

    public void spawn(Trophy trophy) {
        this.trophy = trophy;
        ItemStack item = trophy.getItemToDrop();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            CustomEntityManager.removeCustomEntity(this);
            return;
        }

        spawn(trophy.getBlock().getLocation().add(0.5, 0.7, 0.5));
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return DamageCause.values();
    }

    @Override
    public void onTick() {
        if(trophy == null || !trophy.isValid()) {
            if(trophy != null) {
                System.out.println("trophy.isValid() = " + trophy.isValid());
            }
            CustomEntityManager.removeCustomEntity(this);
        }
    }

    @Override
    public int getTickTime() {
        return 1;
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("Trophy_Block", LocationUtils.asString(this.trophy.getBlock().getLocation()));
    }

    @Override
    public void onLoad(final ConfigurationSection c) {
        String b = c.getString("Trophy_Block");
        if(b != null) {
            Trophy trophy = Trophy.getByBlock(LocationUtils.blockFromString(b));
            System.out.println("trophy = " + trophy);
            if(trophy != null) {
                this.trophy = trophy;
            } else {
                CustomEntityManager.removeCustomEntity(this);
            }
        }
    }
}
