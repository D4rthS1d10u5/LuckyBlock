package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.SkullData;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomEntityBaseballPlayer extends CustomEntity {

    private final ItemStack baseballBat = ItemStackUtils.addEnchants(ItemStackUtils.createItem(Material.WOOD_SPADE, 1, 0, ChatColor.LIGHT_PURPLE + "Baseball Bat"), new int[]{1, 2}, LuckyBlockPlugin.enchantment_glow, Enchantment.KNOCKBACK);

    public CustomEntityBaseballPlayer() {
        registerDropItem(baseballBat, 24);
        registerTargetPriority(EntityType.ELDER_GUARDIAN, 20);
        registerTargetPriority(EntityType.WITHER_SKELETON, 15);
        registerTargetPriority(EntityType.BLAZE, 15);
        registerTargetPriority(EntityType.SKELETON, 10);
        registerTargetPriority(EntityType.ZOMBIE, 10);
        registerTargetPriority(EntityType.COW, 5);
        registerTargetPriority(EntityType.SHEEP, 5);
    }

    @Override
    public EntityType entityType() {
        return EntityType.HUSK;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        this.baseballBat.setDurability((short) RandomUtils.nextInt(this.baseballBat.getType().getMaxDurability()));
        Husk husk = (Husk) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.HUSK);
        SkullData skullData = SkullData.getRandomSkullData("FP");
        husk.getEquipment().setHelmet(ItemStackUtils.createSkull(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), skullData.getId(), skullData.getData()));
        Color equipmentColor = skullData == SkullData.FOOTBALL_PLAYER_1 ? Color.RED : Color.BLUE;
        husk.getEquipment().setChestplate(ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), equipmentColor));
        husk.getEquipment().setLeggings(ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.CHAINMAIL_LEGGINGS), equipmentColor));
        husk.getEquipment().setBoots(ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), equipmentColor));
        husk.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(30.0D);
        husk.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(20.0D);
        husk.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
        husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4D);
        husk.getEquipment().setItemInMainHand(this.baseballBat);
        return husk;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public boolean isAttackingNearbyEntities() {
        return true;
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) {
            event.setDamage(event.getDamage() * 2.0D);
        }
    }
}
