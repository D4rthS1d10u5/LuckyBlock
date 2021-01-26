package com.LuckyBlock.customentity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.core.entity.CustomEntity;
import org.core.entity.Immunity;
import org.core.inventory.ItemMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class EntitySoldier extends CustomEntity {
    private List<String> skulls = new ArrayList();
    EntitySoldier.SoldierType type;
    private int xp;

    public EntitySoldier() {
        this.type = EntitySoldier.SoldierType.MONSTER_KILLER;
        this.xp = 0;
    }

    public Entity spawnFunction(Location loc) {
        Zombie zombie = (Zombie)loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
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

    protected boolean targetsNearbyEntities() {
        return true;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.SKELETON, EntityType.CREEPER, EntityType.CREEPER, EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG_ZOMBIE, EntityType.OCELOT};
    }

    protected void onDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.ENTITY_EXPLOSION && event.getCause() != DamageCause.BLOCK_EXPLOSION && event.getCause() != DamageCause.CONTACT) {
            if (event.getCause() == DamageCause.MAGIC) {
                event.setDamage(event.getDamage() * 9.0D);
            }
        } else {
            event.setDamage(event.getDamage() * 5.0D);
        }

    }

    public List<String> getSkulls() {
        return this.skulls;
    }

    protected boolean isAnimated() {
        return true;
    }

    protected List<String> getNames() {
        LivingEntity living = (LivingEntity)this.entity;
        return Arrays.asList("" + ChatColor.DARK_RED + ChatColor.BOLD + "Soldier " + (int)living.getHealth(), "" + ChatColor.RED + ChatColor.BOLD + "Soldier " + (int)living.getHealth(), "" + ChatColor.GOLD + ChatColor.BOLD + "Soldier " + (int)living.getHealth(), "" + ChatColor.YELLOW + ChatColor.BOLD + "Soldier " + (int)living.getHealth());
    }

    protected int getNamesDelay() {
        return 10;
    }

    public int getAttackDamage() {
        return 8;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.FALL, Immunity.DRAWNING, Immunity.FIRE, Immunity.FIRE_TICK, Immunity.LAVA, Immunity.LIGHTNING};
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{new ItemStack(Material.BUCKET), new ItemStack(Material.CAKE), new ItemStack(Material.INK_SACK, this.random.nextInt(4) + 1, (short)3), new ItemStack(Material.WEB, this.random.nextInt(6) + 1)};
    }

    protected int[] getPercents() {
        return new int[]{30, 40, 30, 20};
    }

    public Particle getDeathParticles() {
        return Particle.VILLAGER_ANGRY;
    }

    public int getXp() {
        return 10 + this.xp;
    }

    protected void onDeath(EntityDeathEvent event) {
        if (this.skulls.size() > 0) {
            Iterator var3 = this.skulls.iterator();

            while(var3.hasNext()) {
                String s = (String)var3.next();
                if (!s.startsWith("Entity")) {
                    ItemStack item = ItemMaker.createItem(Material.SKULL_ITEM, 1, 3);
                    item = ItemMaker.setSkullOwner(item, s);
                    event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
                } else {
                    String[] d = s.split("Entity_");
                    if (d.length == 2) {
                        int data = Integer.parseInt(d[1]);
                        ItemStack item = ItemMaker.createItem(Material.SKULL_ITEM, 1, data);
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
                    }
                }
            }
        }

    }

    protected void onKillPlayer(EntityDamageByEntityEvent event) {
        Player player = (Player)event.getEntity();
        if (!this.skulls.contains(player.getName())) {
            this.skulls.add(player.getName());
            this.save_def();
        }

    }

    protected void onKillEntity(EntityDamageByEntityEvent event) {
        int more = 0;
        if (event.getEntity() instanceof Monster) {
            more = more + 25;
        } else if (event.getEntity() instanceof Player) {
            more = more + 50;
        } else {
            more = more + 10;
        }

        if (event.getEntity() instanceof Skeleton) {
            Skeleton sk = (Skeleton)event.getEntity();
            if (sk.getSkeletonType() == SkeletonType.NORMAL) {
                this.skulls.add("Entity_0");
            } else if (sk.getSkeletonType() == SkeletonType.WITHER) {
                this.skulls.add("Entity_1");
            }
        } else if (event.getEntity() instanceof Zombie) {
            this.skulls.add("Entity_2");
        } else if (event.getEntity() instanceof Creeper) {
            this.skulls.add("Entity_4");
        }

        this.xp += more;
        this.save_def();
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Skulls", this.skulls);
        c.set("Type", this.type.name());
        c.set("Xp", this.xp);
    }

    protected void onLoad(ConfigurationSection c) {
        if (c.getStringList("Skulls") != null && c.getStringList("Skulls").size() > 0) {
            Iterator var3 = c.getStringList("Skulls").iterator();

            while(var3.hasNext()) {
                String a = (String)var3.next();
                this.skulls.add(a);
            }
        }

        if (c.getString("Type") != null) {
            this.type = EntitySoldier.SoldierType.valueOf(c.getString("Type").toUpperCase());
        }

        if (c.getInt("Xp") > 0) {
            this.xp = c.getInt("Xp");
        }

    }

    public String getSpawnEggEntity() {
        return "Zombie";
    }

    public static enum SoldierType {
        DEFAULT,
        MONSTER_KILLER;

        private SoldierType() {
        }
    }
}
