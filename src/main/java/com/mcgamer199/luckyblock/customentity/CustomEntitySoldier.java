package com.mcgamer199.luckyblock.customentity;

import com.destroystokyo.paper.ParticleBuilder;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomEntitySoldier extends CustomEntity {

    @Getter @Setter
    private SoldierType soldierType;
    @Getter @Setter
    private int xp;
    private final List<String> skulls = new ArrayList<>();

    public CustomEntitySoldier() {
        this.soldierType = SoldierType.MONSTER_KILLER;
        this.xp = 0;
        registerTargetPriority(EntityType.SKELETON, 80);
        registerTargetPriority(EntityType.CREEPER, 90);
        registerTargetPriority(EntityType.PIG_ZOMBIE, 70);
        registerTargetPriority(EntityType.OCELOT, 40);
        registerTargetPriority(EntityType.COW, 20);
        registerTargetPriority(EntityType.CHICKEN, 15);
        registerDropItem(new ItemStack(Material.BUCKET), 30);
        registerDropItem(new ItemStack(Material.CAKE), 40);
        registerDropItem(new ItemStack(Material.INK_SACK, RandomUtils.nextInt(4) + 1, (short) 3), 30);
        registerDropItem(new ItemStack(Material.WEB, RandomUtils.nextInt(6) + 1), 20);
    }

    @Override
    public EntityType entityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Zombie zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        zombie.setBaby(false);
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        zombie.setMaxHealth(225.0D);
        zombie.setHealth(50.0D);
        zombie.setCustomNameVisible(true);
        return zombie;
    }

    @Override
    public ParticleBuilder createDeathParticles() {
        return super.createDeathParticles().particle(Particle.VILLAGER_ANGRY);
    }

    @Override
    public double getAttackDamage() {
        return 8D;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.FALL, DamageCause.DROWNING, DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.LIGHTNING};
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 10;
    }

    @Override
    public List<String> getCustomNames() {
        LivingEntity living = (LivingEntity) this.linkedEntity;
        return Arrays.asList("" + ChatColor.DARK_RED + ChatColor.BOLD + "Soldier " + (int) living.getHealth(), "" + ChatColor.RED + ChatColor.BOLD + "Soldier " + (int) living.getHealth(), "" + ChatColor.GOLD + ChatColor.BOLD + "Soldier " + (int) living.getHealth(), "" + ChatColor.YELLOW + ChatColor.BOLD + "Soldier " + (int) living.getHealth());
    }

    @Override
    public int getXPtoDrop() {
        return 10 + xp;
    }

    @Override
    public boolean isAttackingNearbyEntities() {
        return true;
    }

    @Override
    public void onKillEntity(EntityDamageByEntityEvent event) {
        int deltaXP = 0;
        Entity entity = event.getEntity();
        if(entity instanceof Monster) {
            deltaXP += 25;
        } else if(entity instanceof Player) {
            deltaXP += 50;
        } else {
            deltaXP += 10;
        }

        if (entity instanceof Skeleton) {
            Skeleton sk = (Skeleton) event.getEntity();
            if (sk.getSkeletonType() == Skeleton.SkeletonType.NORMAL) {
                this.skulls.add("Entity_0");
            } else if (sk.getSkeletonType() == Skeleton.SkeletonType.WITHER) {
                this.skulls.add("Entity_1");
            }
        } else if (entity instanceof Zombie) {
            this.skulls.add("Entity_2");
        } else if (entity instanceof Creeper) {
            this.skulls.add("Entity_4");
        }

        xp += deltaXP;
    }

    public void onKillPlayer(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getEntity();
        if (!this.skulls.contains(player.getName())) {
            this.skulls.add(player.getName());
        }
    }

    public void onDeath(EntityDeathEvent event) {
        if (this.skulls.size() > 0) {
            for (String skullName : this.skulls) {
                if (!skullName.startsWith("Entity")) {
                    event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ItemStackUtils.setSkullOwner(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), skullName));
                } else {
                    String[] d = skullName.split("Entity_");
                    if (d.length == 2) {
                        int data = Integer.parseInt(d[1]);
                        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) data);
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
                    }
                }
            }
        }
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.ENTITY_EXPLOSION && event.getCause() != DamageCause.BLOCK_EXPLOSION && event.getCause() != DamageCause.CONTACT) {
            if (event.getCause() == DamageCause.MAGIC) {
                event.setDamage(event.getDamage() * 9.0D);
            }
        } else {
            event.setDamage(event.getDamage() * 5.0D);
        }
    }

    public void onSave(ConfigurationSection c) {
        c.set("Skulls", this.skulls);
        c.set("Type", soldierType.name());
        c.set("Xp", this.xp);
    }

    public void onLoad(ConfigurationSection c) {
        if (c.getStringList("Skulls") != null && c.getStringList("Skulls").size() > 0) {
            this.skulls.addAll(c.getStringList("Skulls"));
        }

        if (c.getString("Type") != null) {
            this.soldierType = SoldierType.valueOf(c.getString("Type").toUpperCase());
        }

        if (c.getInt("Xp") > 0) {
            this.xp = c.getInt("Xp");
        }
    }

    public enum SoldierType {
        DEFAULT,
        MONSTER_KILLER;

        SoldierType() {
        }
    }
}
