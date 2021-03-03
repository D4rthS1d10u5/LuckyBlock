package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomEntitySuperWitherSkeleton extends CustomEntity {

    @Getter @Setter
    private UUID owner;

    public CustomEntitySuperWitherSkeleton() {
        registerDropItem(new ItemStack(Material.IRON_BLOCK), 25);
        registerDropItem(new ItemStack(Material.BREAD, RandomUtils.nextInt(4) + 1), 85);
        registerDropItem(new ItemStack(Material.IRON_INGOT, RandomUtils.nextInt(5) + 3), 60);
    }

    @Override
    public EntityType entityType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        WitherSkeleton skeleton = (WitherSkeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        skeleton.setGlowing(true);
        skeleton.setCustomName(ChatColor.GOLD + "LB Army");
        skeleton.setCustomNameVisible(true);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0D);
        skeleton.setHealth(100.0D);
        skeleton.getEquipment().setItemInMainHand(ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{4, 2}, Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT));
        skeleton.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        Scheduler.later(() -> CustomEntityManager.removeCustomEntity(this), 225);
        return skeleton;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.FALL, DamageCause.THORNS};
    }

    @Override
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() != null && event.getTarget().getUniqueId().equals(owner)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onSave(ConfigurationSection c) {
        if (this.owner != null) {
            c.set("Owner", this.owner.toString());
        }
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        if (c.getString("Owner") != null) {
            this.owner = UUID.fromString(c.getString("Owner"));
        }
        Scheduler.later(() -> CustomEntityManager.removeCustomEntity(this), 225);
    }
}
