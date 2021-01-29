package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.resources.SkullData;
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

public class EntityFootballPlayer extends CustomEntity {
    private static final Color[] colors;

    static {
        colors = new Color[]{Color.RED, Color.BLUE};
    }

    private final ItemStack baseball;

    public EntityFootballPlayer() {
        this.baseball = ItemMaker.addEnchants(ItemMaker.createItem(Material.WOOD_SPADE, 1, 0, ChatColor.LIGHT_PURPLE + "Baseball Bat"), new int[]{1, 2}, LuckyBlockPlugin.enchantment_glow, Enchantment.KNOCKBACK);
    }

    protected Entity spawnFunction(Location loc) {
        this.baseball.setDurability((short) this.random.nextInt(this.baseball.getType().getMaxDurability()));
        Husk husk = (Husk) loc.getWorld().spawnEntity(loc, EntityType.HUSK);
        SkullData sk = SkullData.getRandomSkullData("FP");
        husk.getEquipment().setHelmet(ItemMaker.createSkull(ItemMaker.createItem(Material.SKULL_ITEM, 1, 3), sk.getId(), sk.getData()));
        int r = sk == SkullData.FOOTBALL_PLAYER_1 ? 0 : 1;
        husk.getEquipment().setChestplate(this.createArmor(Material.LEATHER_CHESTPLATE, colors[r]));
        husk.getEquipment().setLeggings(this.createArmor(Material.LEATHER_LEGGINGS, colors[r]));
        husk.getEquipment().setBoots(this.createArmor(Material.LEATHER_BOOTS, colors[r]));
        husk.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(30.0D);
        husk.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(20.0D);
        husk.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
        husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4D);
        husk.getEquipment().setItemInMainHand(this.baseball);
        return husk;
    }

    private ItemStack createArmor(Material mat, Color color) {
        return ItemMaker.setLeatherArmorColor(new ItemStack(mat), color);
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.SHEEP, EntityType.ZOMBIE, EntityType.COW, EntityType.SKELETON, EntityType.BLAZE, EntityType.ELDER_GUARDIAN, EntityType.WITHER_SKELETON};
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{this.baseball};
    }

    protected int[] getPercents() {
        return new int[]{24};
    }

    protected void onDamage(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) {
            event.setDamage(event.getDamage() * 2.0D);
        }

    }

    public String getSpawnEggEntity() {
        return "zombie";
    }
}
