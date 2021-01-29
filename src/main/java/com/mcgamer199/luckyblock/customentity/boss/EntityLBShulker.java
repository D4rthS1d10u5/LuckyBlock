package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityLBShulker extends CustomEntity {
    private int killedEntities;

    public EntityLBShulker() {
    }

    public Entity spawnFunction(Location loc) {
        Shulker shulker = (Shulker) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
        shulker.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25.0D);
        shulker.setHealth(25.0D);
        shulker.setRemoveWhenFarAway(false);
        return shulker;
    }

    public int getXp() {
        return 275;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.PLAYER, EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.WOLF};
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    protected int[] getPriorities() {
        return new int[]{3, 1, 2, 2};
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{new ItemStack(Material.IRON_INGOT, this.random.nextInt(2) + 1), new ItemStack(Material.ENDER_PEARL, this.random.nextInt(2) + 2)};
    }

    protected int[] getPercents() {
        return new int[]{10, 60};
    }

    protected void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.random.nextInt(100) > 90 && event.getDamager() instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) event.getDamager();
            l.damage(4.0D);
            l.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 65, 2));
        }

    }

    protected void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) event.getEntity();
            l.damage((this.killedEntities + 1) * 4 + 7);
            l.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 145, 3));
        }

    }

    protected void onKillEntityWithProjectile(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();
        String t = e.getType().name();
        if (!(e instanceof Shulker)) {
            if (e instanceof Player) {
                t = e.getName();
            } else if (e instanceof LivingEntity) {
                LivingEntity l = (LivingEntity) e;
                if (l.getCustomName() != null) {
                    t = l.getCustomName();
                }
            }

            e.getWorld().dropItem(event.getEntity().getLocation(), ItemMaker.createItem(Material.WEB, 1, 0, "" + ChatColor.RED + ChatColor.BOLD + t.toLowerCase() + " was killed by shulker"));
            ++this.killedEntities;
            this.save_def();
        }
    }

    public double getDefense() {
        return 7.5D;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.PROJECTILE, Immunity.CONTACT, Immunity.WITHER};
    }

    protected void onSave(ConfigurationSection c) {
        c.set("killedEntities", this.killedEntities);
    }

    protected void onLoad(ConfigurationSection c) {
        this.killedEntities = c.getInt("killedEntities");
    }
}
