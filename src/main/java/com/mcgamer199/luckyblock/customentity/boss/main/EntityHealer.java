package com.mcgamer199.luckyblock.customentity.boss.main;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import com.mcgamer199.luckyblock.customentity.nametag.EntityTagHealer;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.SoundUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityHealer extends CustomEntity {
    public double healValue = 0.0D;
    public int delay = 0;
    public boolean damageNearby = true;
    public int health = 100;
    private boolean running;
    private LivingEntity healEntity;
    private EnderCrystal ender;
    private boolean damageable = true;

    public EntityHealer() {
    }

    public EntityHealer(LivingEntity healEntity) {
        this.healEntity = healEntity;
    }

    protected void onChunkLoad() {
        Iterator var2 = this.entity.getWorld().getEntitiesByClass(this.healEntity.getClass()).iterator();

        while (var2.hasNext()) {
            Entity e = (Entity) var2.next();
            if (e.getUniqueId().toString().equalsIgnoreCase(this.healEntity.getUniqueId().toString())) {
                this.healEntity = (LivingEntity) e;
                break;
            }
        }

    }

    protected Entity spawnFunction(Location loc) {
        EnderCrystal e = (EnderCrystal) loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
        e.setInvulnerable(true);
        e.setShowingBottom(true);
        this.ender = e;
        this.spawn_nametag();
        this.func_target();
        this.func_protect();
        return e;
    }

    private void spawn_nametag() {
        Scheduler.later(this::a, 3);
    }

    private void a() {
        com.mcgamer199.luckyblock.customentity.nametag.EntityTagHealer e = new EntityTagHealer();
        e.source = this;
        e.spawn(this.ender, new double[]{0.0D, 1.3D, 0.0D});
    }

    public Immunity[] getImmuneTo() {
        return Immunity.values();
    }

    public void func_run() {
        if (this.delay > 0 && this.healValue > 0.0D && this.healEntity != null) {
            this.running = true;
            this.save_def();
            Scheduler.timer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!EntityHealer.this.healEntity.isDead() && !EntityHealer.this.ender.isDead()) {
                        if (EntityHealer.this.running && EntityHealer.this.healEntity.getHealth() < EntityHealer.this.healEntity.getMaxHealth()) {
                            if (EntityHealer.this.healEntity.getHealth() + EntityHealer.this.healValue < EntityHealer.this.healEntity.getMaxHealth()) {
                                EntityHealer.this.healEntity.setHealth(EntityHealer.this.healEntity.getHealth() + EntityHealer.this.healValue);
                            } else {
                                EntityHealer.this.healEntity.setHealth(EntityHealer.this.healEntity.getMaxHealth());
                            }

                            SoundUtils.playFixedSound(EntityHealer.this.ender.getLocation(), SoundUtils.getSound("boss_healer_heal"), 1.0F, 2.0F, 8);
                            MyTasks.playEffects(Particle.HEART, EntityHealer.this.ender.getLocation(), 5, new double[]{0.7D, 0.7D, 0.7D}, 0.0F);
                        }
                    } else {
                        EntityHealer.this.remove();
                        cancel();
                    }
                }
            }, delay, delay);
        }
    }

    private void func_target() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!EntityHealer.this.healEntity.isDead() && !EntityHealer.this.ender.isDead()) {
                    EntityHealer.this.ender.setBeamTarget(EntityHealer.this.healEntity.getLocation());
                } else {
                    EntityHealer.this.remove();
                    cancel();
                }
            }
        }, 20, 10);
    }

    public boolean isDamageable() {
        return this.damageable;
    }

    private void func_protect() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!EntityHealer.this.ender.isDead()) {
                    if (EntityHealer.this.damageNearby) {
                        List<LivingEntity> l = EntityHealer.this.getNearbyEnemies(EntityHealer.this.ender.getNearbyEntities(3.0D, 2.0D, 3.0D));
                        if (l.size() > 0) {
                            MyTasks.playEffects(Particle.CRIT, EntityHealer.this.ender.getLocation(), 250, new double[]{1.5D, 0.0D, 1.5D}, 0.0F);

                            for (LivingEntity e : l) {
                                e.damage(3.0D);
                            }
                        }
                    }
                } else {
                    cancel();
                }
            }
        }, 20, 15);
    }

    public void func_stop() {
        this.running = false;
    }

    private List<LivingEntity> getNearbyEnemies(List<Entity> l) {
        List<LivingEntity> list = new ArrayList<>();

        for (Entity e : l) {
            if (e instanceof Player) {
                list.add((LivingEntity) e);
            }
        }

        return list;
    }

    public boolean isRunning() {
        return this.running;
    }

    protected void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if (this.running && this.damageable) {
            Player p = (Player) event.getDamager();
            if (p.getInventory().getItemInMainHand() != null) {
                ItemStack i = p.getInventory().getItemInMainHand();
                if (i.hasItemMeta() && i.getItemMeta().hasEnchant(Enchantment.DIG_SPEED) && i.getItemMeta().getEnchantLevel(Enchantment.DIG_SPEED) > 4) {
                    int d = (int) (event.getDamage() / 5.0D);
                    if (this.health - d > 0) {
                        this.health -= d;
                    } else {
                        this.health = 0;
                    }

                    MyTasks.playEffects(Particle.FLAME, this.ender.getLocation().add(0.0D, 0.5D, 0.0D), 30, new double[]{0.2D, 0.0D, 0.2D}, 1.0F);
                    if (this.health < 1) {
                        for (int x = LuckyBlockPlugin.randoms.nextInt(5) + 100; x > 0; --x) {
                            Item a = this.ender.getWorld().dropItem(this.ender.getLocation(), new ItemStack(Material.GLASS, 1));
                            a.setPickupDelay(2000);
                            this.remove(a, (LuckyBlockPlugin.randoms.nextInt(20) + 6) * 3);
                        }

                        SoundUtils.playFixedSound(this.ender.getLocation(), SoundUtils.getSound("boss_healer_death"), 1.0F, 1.0F, 8);
                        this.remove();
                        return;
                    }

                    SoundUtils.playFixedSound(this.ender.getLocation(), SoundUtils.getSound("boss_healer_damage"), 1.0F, 1.0F, 8);
                    this.func_wait_damage();
                    this.save_def();
                }
            }
        }

    }

    private void remove(final Item i, int time) {
        LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                i.remove();
            }
        }, time);
    }

    private void func_wait_damage() {
        this.damageable = false;
        Scheduler.later(() -> this.damageable = true, 20);
    }

    protected void onSave(ConfigurationSection c) {
        c.set("HealValue", this.healValue);
        c.set("HealEntity", this.healEntity.getUniqueId().toString());
        c.set("Running", this.running);
        c.set("Delay", this.delay);
        c.set("DamageNearby", this.damageNearby);
        c.set("Health", this.health);
    }

    protected void onLoad(final ConfigurationSection c) {
        this.ender = (EnderCrystal) this.entity;
        this.healValue = c.getDouble("HealValue");
        this.delay = c.getInt("Delay");
        this.running = c.getBoolean("Running");
        this.damageNearby = c.getBoolean("DamageNearby");
        this.health = c.getInt("Health");
        Scheduler.later(() -> {
            EntityHealer.this.healEntity = (LivingEntity) CustomEntity.getByUUID(UUID.fromString(c.getString("HealEntity"))).getEntity();
            if (EntityHealer.this.running) {
                EntityHealer.this.func_run();
            }

            EntityHealer.this.func_target();
            EntityHealer.this.func_protect();
        }, 15);
    }
}
