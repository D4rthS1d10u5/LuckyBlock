package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.listeners.LuckyBlockWorld;
import com.mcgamer199.luckyblock.structures.Treasure;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EntityGuardian extends CustomEntity {
    public LBType type = LBType.getRandomType();
    private final List<UUID> army = new ArrayList();

    public EntityGuardian() {
    }

    public Entity spawnFunction(Location loc) {
        WitherSkeleton skeleton = (WitherSkeleton) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
        skeleton.setCustomNameVisible(true);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(460.0D);
        skeleton.setHealth(460.0D);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 1));
        Map<Enchantment, Integer> m = new HashMap();
        m.put(LuckyBlockPlugin.enchantment_lightning, 10);
        m.put(Enchantment.DAMAGE_ALL, 4);
        ItemStack item = ItemStackUtils.createItem(Material.DIAMOND_AXE, 1, (short) 0, null, null, m);
        skeleton.getEquipment().setItemInMainHand(item);
        skeleton.getEquipment().setHelmet(this.type.toItemStack());
        ItemStack item1 = new ItemStack(Material.GOLD_CHESTPLATE);
        skeleton.getEquipment().setChestplate(item1);
        item1.setType(Material.GOLD_LEGGINGS);
        skeleton.getEquipment().setLeggings(item1);
        item1.setType(Material.GOLD_BOOTS);
        skeleton.getEquipment().setBoots(item1);
        this.a();
        return skeleton;
    }

    protected boolean isAnimated() {
        return true;
    }

    protected List<String> getNames() {
        LivingEntity living = (LivingEntity) this.entity;
        return Collections.singletonList(ChatColor.YELLOW + "Guardian " + ChatColor.GREEN + (int) living.getHealth() + ChatColor.WHITE + "/" + ChatColor.GREEN + living.getMaxHealth());
    }

    private void run1() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!EntityGuardian.this.getEntity().isDead()) {
                    int total = 0;
                    Iterator var3 = EntityGuardian.this.getEntity().getNearbyEntities(10.0D, 5.0D, 10.0D).iterator();

                    while (true) {
                        while (true) {
                            Entity e;
                            do {
                                do {
                                    if (!var3.hasNext()) {
                                        if (total > 0) {
                                            if (((LivingEntity) EntityGuardian.this.getEntity()).getHealth() + (double) total < ((LivingEntity) EntityGuardian.this.getEntity()).getMaxHealth()) {
                                                ((LivingEntity) EntityGuardian.this.getEntity()).setHealth(((LivingEntity) EntityGuardian.this.getEntity()).getHealth() + (double) total);
                                            } else {
                                                ((LivingEntity) EntityGuardian.this.getEntity()).setHealth(((LivingEntity) EntityGuardian.this.getEntity()).getMaxHealth());
                                            }

                                            EntityGuardian.this.getEntity().getWorld().spawnParticle(Particle.HEART, EntityGuardian.this.getEntity().getLocation(), total / 2, 0.4D, 0.4D, 0.4D, 0.0D);
                                        }

                                        return;
                                    }

                                    e = (Entity) var3.next();
                                } while (!(e instanceof LivingEntity));
                            } while (e instanceof Monster);

                            if (!(e instanceof Player)) {
                                e.setFallDistance(10.0F);
                                total += 2;
                            } else {
                                Player p = (Player) e;
                                if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                                    if (!LuckyBlockWorld.equals(EntityGuardian.this.getEntity().getWorld().getGenerator())) {
                                        e.setFallDistance(10.0F);
                                    } else {
                                        e.setFallDistance(80.0F);
                                    }

                                    total += 2;
                                }
                            }
                        }
                    }
                } else {
                    cancel();
                }
            }
        }, 10, 60);
    }

    private void run2() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!EntityGuardian.this.getEntity().isDead()) {
                    int x = 0;
                    Iterator var3 = EntityGuardian.this.getEntity().getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();

                    while (var3.hasNext()) {
                        Entity e = (Entity) var3.next();
                        if (e instanceof LivingEntity) {
                            ++x;
                        }
                    }

                    if (x < 15 && ((Creature) EntityGuardian.this.getEntity()).getTarget() != null) {
                        EntitySuperWitherSkeleton p = new EntitySuperWitherSkeleton();
                        p.owner = EntityGuardian.this.getEntity().getUniqueId();
                        p.spawn(EntityGuardian.this.getEntity().getLocation());
                        ((Creature) p.getEntity()).setTarget(((Creature) EntityGuardian.this.getEntity()).getTarget());
                        EntityGuardian.this.army.add(p.getEntity().getUniqueId());
                    }
                } else {
                    cancel();
                }
            }
        }, 100, 150);
    }

    public ItemStack[] getDrops() {
        if (LuckyBlockWorld.equals(this.entity.getWorld().getGenerator())) {
            Treasure t = Treasure.getRandomTreasure();
            List<String> list;
            if (t != null) {
                list = Arrays.asList(ChatColor.BLUE + "Location: " + t.getLocation().getBlockX() + "," + t.getLocation().getBlockY() + "," + t.getLocation().getBlockZ(), ChatColor.RED + "<<Right click to break the top bedrock block>>");
            } else {
                list = Collections.singletonList(ChatColor.RED + "No treasure found!");
            }

            ItemStack map = ItemStackUtils.createItem(Material.MAP, 1, 0, "" + ChatColor.GREEN + ChatColor.ITALIC + "Treasure Map", list);
            return new ItemStack[]{map, new ItemStack(Material.GOLD_INGOT, (this.random.nextInt(3) + 3) * 3), new ItemStack(Material.DIAMOND, (this.random.nextInt(4) + 3) * 2), new ItemStack(Material.IRON_INGOT, (this.random.nextInt(8) + 6) * 3)};
        } else {
            return new ItemStack[]{new ItemStack(Material.GOLD_INGOT, (this.random.nextInt(3) + 3) * 3), new ItemStack(Material.DIAMOND, (this.random.nextInt(4) + 3) * 2), new ItemStack(Material.IRON_INGOT, (this.random.nextInt(8) + 6) * 3)};
        }
    }

    protected int[] getPercents() {
        return LuckyBlockWorld.equals(this.entity.getWorld().getGenerator()) ? new int[]{100, 80, 65, 90} : new int[]{80, 65, 90};
    }

    public LBType getType() {
        return this.type;
    }

    protected void onDeath(EntityDeathEvent event) {
        Iterator var3 = event.getEntity().getNearbyEntities(5.0D, 5.0D, 5.0D).iterator();

        while (var3.hasNext()) {
            Entity e = (Entity) var3.next();
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).damage(25.0D);
            }
        }

    }

    protected void onDamage(EntityDamageEvent event) {
        if (this.random.nextInt(100) > 90 && ((LivingEntity) this.entity).getEquipment().getHelmet() != null && LBType.fromMaterial(((LivingEntity) this.entity).getEquipment().getHelmet().getType()) != null) {
            this.entity.getWorld().dropItem(this.entity.getLocation(), this.type.toItemStack(LBType.getRandomP(-50, 50)));
        }

    }

    void a() {
        this.run1();
        this.run2();
    }

    public int getXp() {
        return 1600;
    }

    protected void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() != null && this.army.size() > 0) {
            for (int x = 0; x < this.army.size(); ++x) {
                if (this.army.get(x).toString().equalsIgnoreCase(event.getTarget().getUniqueId().toString())) {
                    event.setCancelled(true);
                }
            }
        }

    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.BLOCK_EXPLOSION, Immunity.DRAWNING, Immunity.ENTITY_EXPLOSION, Immunity.FALLING_BLOCK, Immunity.LIGHTNING, Immunity.POISON, Immunity.THORNS, Immunity.WITHER, Immunity.FALL, Immunity.PROJECTILE};
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Type", this.type.getId());
    }

    protected void onLoad(ConfigurationSection c) {
        if (c.getInt("Types") > 0) {
            this.type = LBType.fromId(c.getInt("Type"));
        }

        if (this.type != null) {
            this.a();
        }

    }

    public String getSpawnEggEntity() {
        return "Enderman";
    }
}
