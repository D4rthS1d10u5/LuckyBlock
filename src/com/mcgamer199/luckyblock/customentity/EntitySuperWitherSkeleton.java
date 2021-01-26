package com.mcgamer199.luckyblock.customentity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;
import com.mcgamer199.luckyblock.inventory.event.ItemMaker;

import java.util.UUID;

public class EntitySuperWitherSkeleton extends CustomEntity {
    protected UUID owner;

    public EntitySuperWitherSkeleton() {
    }

    public Entity spawnFunction(Location loc) {
        WitherSkeleton skeleton = (WitherSkeleton)loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
        skeleton.setGlowing(true);
        skeleton.setCustomName(ChatColor.GOLD + "LB Army");
        skeleton.setCustomNameVisible(true);
        skeleton.setMaxHealth(100.0D);
        skeleton.setHealth(100.0D);
        skeleton.getEquipment().setItemInMainHand(ItemMaker.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{4, 2}, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT}));
        skeleton.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        return skeleton;
    }

    protected void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() != null && this.owner != null && this.owner.toString().equalsIgnoreCase(event.getTarget().getUniqueId().toString())) {
            event.setCancelled(true);
        }

    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{ItemMaker.createItem(Material.IRON_BLOCK), ItemMaker.createItem(Material.BREAD, this.random.nextInt(4) + 1), ItemMaker.createItem(Material.IRON_INGOT, this.random.nextInt(5) + 3)};
    }

    protected int[] getPercents() {
        return new int[]{25, 85, 60};
    }

    public int getXp() {
        return 150;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.FALL, Immunity.THORNS};
    }

    protected void onTick() {
        this.entity.remove();
    }

    protected int getTickTime() {
        return 225;
    }

    protected void onSave(ConfigurationSection c) {
        if (this.owner != null) {
            c.set("Owner", this.owner.toString());
        }

    }

    protected void onLoad(ConfigurationSection c) {
        if (c.getString("Owner") != null) {
            this.owner = UUID.fromString(c.getString("Owner"));
        }

    }

    public String getSpawnEggEntity() {
        return "Skeleton";
    }
}
