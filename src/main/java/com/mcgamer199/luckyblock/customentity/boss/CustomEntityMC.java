package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomEntityMC extends CustomEntity implements CustomEntityBoss {

    private static final ItemStack[] ARMOR_HELMETS;
    private static final ItemStack[] ARMOR_CHESTPLATES;
    private static final ItemStack[] ARMOR_LEGGINGS;
    private static final ItemStack[] ARMOR_BOOTS;
    private static final ItemStack ARMY_SWORD;

    static {
        ARMOR_HELMETS = new ItemStack[]{new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.DIAMOND_HELMET)};
        ARMOR_CHESTPLATES = new ItemStack[]{new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.DIAMOND_CHESTPLATE)};
        ARMOR_LEGGINGS = new ItemStack[]{new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.DIAMOND_LEGGINGS)};
        ARMOR_BOOTS = new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.DIAMOND_BOOTS)};
        ARMY_SWORD = ItemStackUtils.addEnchant(new ItemStack(Material.WOOD_SWORD), Enchantment.DAMAGE_ALL, 10);
    }

    private Wither wither;
    private boolean damaged = false;

    public CustomEntityMC() {}

    @Override
    public EntityType entityType() {
        return EntityType.WITHER;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Wither wither = (Wither) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.WITHER);
        wither.setSilent(true);
        this.wither = wither;
        return wither;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public LivingEntity getBossEntity() {
        return wither;
    }

    @Override
    public BossBar getBossBar() {
        return null;
    }

    @Override
    public int getBossBarRange() {
        return 0;
    }

    @Override
    public void onEntityHitWithBeam(LivingEntity livingEntity, String tag) {}

    @Override
    public int getCustomNamesTickDelay() {
        return 10;
    }

    @Override
    public List<String> getCustomNames() {
        return Arrays.asList("" + ChatColor.YELLOW + ChatColor.BOLD + "MCGamer199", "" + ChatColor.GOLD + ChatColor.BOLD + "MCGamer199");
    }

    @Override
    public double getDefense() {
        return 3D;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION};
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (!this.damaged) {
            this.damaged = true;
            if (event instanceof EntityDamageByEntityEvent && RandomUtils.nextPercent(90)) {
                EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent) event;
                Entity damager = ed.getDamager();
                if(damager instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) damager;
                    if (ItemStackUtils.hasEnchantment(livingEntity.getEquipment().getChestplate(), LuckyBlockPlugin.enchantment_reflect_prot, 1)) {
                        event.setCancelled(true);
                        livingEntity.damage(event.getDamage(), wither);
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        event.getEntity().setFallDistance(50.0F);
    }

    @Override
    public void onLoad(final ConfigurationSection c) {
        this.wither = (Wither) linkedEntity;
        startTimers();
    }

    private void startTimers() {
        Scheduler.create(() -> {
            LivingEntity target = wither.getTarget();
            if(target != null && wither.getWorld().getNearbyEntitiesByType(Monster.class, wither.getLocation(), 7, 7, 7).size() < 15) {
                for (int x = 0; x < RandomUtils.nextInt(5) + 4; x++) {
                    WitherSkeleton armySkeleton = (WitherSkeleton) wither.getWorld().spawnEntity(wither.getLocation(), EntityType.WITHER_SKELETON);
                    int armorIndex = RandomUtils.nextInt(ARMOR_HELMETS.length);
                    armySkeleton.getEquipment().setHelmet(ARMOR_HELMETS[armorIndex]);
                    armySkeleton.getEquipment().setChestplate(ARMOR_CHESTPLATES[armorIndex]);
                    armySkeleton.getEquipment().setLeggings(ARMOR_LEGGINGS[armorIndex]);
                    armySkeleton.getEquipment().setBoots(ARMOR_BOOTS[armorIndex]);
                    armySkeleton.getEquipment().setHelmetDropChance(0.0F);
                    armySkeleton.getEquipment().setChestplateDropChance(0.0F);
                    armySkeleton.getEquipment().setLeggingsDropChance(0.0F);
                    armySkeleton.getEquipment().setBootsDropChance(0.0F);
                    armySkeleton.getEquipment().setItemInMainHand(ARMY_SWORD);
                    armySkeleton.getEquipment().setItemInMainHandDropChance(0.0F);
                    armySkeleton.setTarget(target);
                }
            }
        }).predicate(this::isValid).timer(20, 20);

        Scheduler.create(() -> {
            Collection<LivingEntity> nearbyEntities = wither.getWorld().getNearbyEntitiesByType(LivingEntity.class, wither.getLocation(), 10, 10, 10, entity -> !entity.getType().equals(EntityType.SHEEP) && !entity.getType().equals(EntityType.WITHER_SKELETON));
            for (LivingEntity nearbyEntity : nearbyEntities) {
                nearbyEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
                nearbyEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                nearbyEntity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 0));
                nearbyEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
                nearbyEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1));
            }
        }).predicate(this::isValid).timer(100, 100);

        Scheduler.create(() -> {
            Item item = wither.getWorld().dropItem(wither.getLocation().add(0.0D, 2.0D, 0.0D), ItemStackUtils.createItem(Material.WOOL, 1, 11, ChatColor.GOLD + "LBWither's Bomb"));
            item.setVelocity(new Vector((RandomUtils.nextInt(80) - 40) / 100D, 0.5D, (RandomUtils.nextInt(80) - 40) / 100D));
            Scheduler.later(item::remove, 50);
        }).predicate(this::isValid).timer(20, 40);
    }
}
