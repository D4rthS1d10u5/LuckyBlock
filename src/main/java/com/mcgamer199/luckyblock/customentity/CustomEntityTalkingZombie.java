package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;

public class CustomEntityTalkingZombie extends CustomEntity {

    private static final String[] texts = new String[]{"Stop hitting me!", "Ouch!", "Stop it!", "Don't kill me!", "Hey stop!", "How dare you!", "Stop"};

    private Zombie zombie;

    @Override
    public EntityType entityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Zombie zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0D);
        zombie.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(6.0D);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25D);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25.0D);
        zombie.setHealth(25.0D);
        zombie.getEquipment().setHelmetDropChance(1.0F);
        zombie.getEquipment().setChestplateDropChance(1.0F);
        zombie.getEquipment().setLeggingsDropChance(1.0F);
        zombie.getEquipment().setBootsDropChance(1.0F);
        zombie.setCustomName(ChatColor.GREEN + "Talking Zombie");
        zombie.setCustomNameVisible(true);
        this.zombie = zombie;
        return zombie;
    }

    @Override
    public void onTick() {
        this.zombie.setFireTicks(0);
        Entity target = zombie.getTarget();
        if (target instanceof Player && LocationUtils.hasDistance(target.getLocation(), zombie.getLocation(), 1.5)) {
            Player player = (Player) this.zombie.getTarget();
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                boolean hasArmor = false;
                EntityEquipment zombieEquipment = zombie.getEquipment();
                EntityEquipment playerEquipment = player.getEquipment();
                if(ItemStackUtils.isNullOrAir(zombieEquipment.getHelmet()) && !ItemStackUtils.isNullOrAir(playerEquipment.getHelmet())) {
                   zombieEquipment.setHelmet(playerEquipment.getHelmet());
                   playerEquipment.setHelmet(null);
                   hasArmor = true;
                }
                if(ItemStackUtils.isNullOrAir(zombieEquipment.getChestplate()) && !ItemStackUtils.isNullOrAir(playerEquipment.getChestplate())) {
                    zombieEquipment.setChestplate(playerEquipment.getChestplate());
                    playerEquipment.setChestplate(null);
                    hasArmor = true;
                }
                if(ItemStackUtils.isNullOrAir(zombieEquipment.getLeggings()) && !ItemStackUtils.isNullOrAir(playerEquipment.getLeggings())) {
                    zombieEquipment.setLeggings(playerEquipment.getLeggings());
                    playerEquipment.setLeggings(null);
                    hasArmor = true;
                }
                if(ItemStackUtils.isNullOrAir(zombieEquipment.getBoots()) && !ItemStackUtils.isNullOrAir(playerEquipment.getBoots())) {
                    zombieEquipment.setBoots(playerEquipment.getBoots());
                    playerEquipment.setBoots(null);
                    hasArmor = true;
                }

                if (hasArmor) {
                    player.sendMessage("§c§l[§3§lZombie§c§l]§r: §cNow give me your armour!");
                }
            }
        }
    }

    @Override
    public int getTickTime() {
        return 2;
    }

    @Override
    public void onChunkLoad() {
        this.zombie = (Zombie) this.linkedEntity;
    }

    @Override
    public int getXPtoDrop() {
        EntityEquipment zombieEquipment = zombie.getEquipment();
        int totalXP = 30;
        if(!ItemStackUtils.isNullOrAir(zombieEquipment.getHelmet())) {
            totalXP += 10;
        }
        if(!ItemStackUtils.isNullOrAir(zombieEquipment.getChestplate())) {
            totalXP += 20;
        }
        if(!ItemStackUtils.isNullOrAir(zombieEquipment.getLeggings())) {
            totalXP += 15;
        }
        if(!ItemStackUtils.isNullOrAir(zombieEquipment.getBoots())) {
            totalXP += 5;
        }

        return totalXP;
    }

    @Override
    public double getAttackDamage() {
        return 7D;
    }

    @Override
    public double getDefense() {
        return 3D;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.FIRE, DamageCause.FIRE_TICK};
    }

    @Override
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        double distance = 5;
        if(RandomUtils.nextPercent(60)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(linkedEntity.getLocation().distance(player.getLocation()) <= distance + 1) {
                    player.sendMessage("§c§l[§3§lZombie§c§l]§r: " + RandomUtils.getRandomObject(texts));
                }
            }
        }
    }
}