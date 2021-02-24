package com.mcgamer199.luckyblock.customentity;

import com.destroystokyo.paper.ParticleBuilder;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomEntityKillerSkeleton extends CustomEntity {

    private static final List<Item> blood = new ArrayList<>();

    public CustomEntityKillerSkeleton() {
        registerDropItem(new ItemStack(Material.DIAMOND_PICKAXE), 20);
        registerDropItem(new ItemStack(Material.DIAMOND_AXE), 20);
        registerDropItem(new ItemStack(Material.DIAMOND_SPADE), 20);
    }

    @Override
    public EntityType entityType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        WitherSkeleton skeleton = (WitherSkeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        skeleton.setMaxHealth(120.0D);
        skeleton.setHealth(120.0D);
        skeleton.setCustomName(ChatColor.RED + "Killer Skeleton");
        skeleton.setCustomNameVisible(true);
        skeleton.getEquipment().setItemInMainHand(ItemStackUtils.addEnchants(new ItemStack(Material.BOW), new int[]{3}, Enchantment.ARROW_DAMAGE));
        skeleton.getEquipment().setHelmet(new ItemStack(Material.GLASS));
        skeleton.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        skeleton.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        skeleton.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
        return skeleton;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.PROJECTILE};
    }

    @Override
    public double getDefense() {
        return 2D;
    }

    @Override
    public ParticleBuilder createDeathParticles() {
        return super.createDeathParticles().particle(Particle.CRIT_MAGIC);
    }

    @Override
    public int getXPtoDrop() {
        return RandomUtils.nextInt(350) + 200;
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        for (int i = 0; i < (RandomUtils.nextInt(5) + event.getDamage() > 100 ? 100 : event.getDamage()); i++) {
            Item item = linkedEntity.getWorld().dropItem(linkedEntity.getLocation(), new ItemStack(Material.WOOL, 1, (short) 15));
            item.setPickupDelay(2000);
            blood.add(item);
            Scheduler.later(() -> {
                item.remove();
                blood.remove(item);
            }, (RandomUtils.nextInt(20) + 6) * 3);
        }
    }
}
