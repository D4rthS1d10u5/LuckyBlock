package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.customentity.EntityElementalCreeper;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;
import com.mcgamer199.luckyblock.logic.ITask;

import java.util.Arrays;
import java.util.List;

public class EntityLBBlaze extends CustomEntity {
    Blaze blaze;
    private boolean ai = false;
    private int startHealth = 20;

    public EntityLBBlaze() {
    }

    public Entity spawnFunction(Location loc) {
        Blaze blaze = (Blaze)loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
        blaze.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((double)this.startHealth);
        blaze.setHealth((double)this.startHealth);
        blaze.setCustomNameVisible(true);
        blaze.setAI(false);
        blaze.setInvulnerable(true);
        blaze.setSilent(true);
        blaze.setRemoveWhenFarAway(false);
        this.blaze = blaze;
        this.task1();
        return blaze;
    }

    public int getXp() {
        return 850;
    }

    protected int xpsize() {
        return 225;
    }

    protected void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * (double)(this.random.nextInt(3) + 2));
        event.getEntity().setFallDistance(25.0F);
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{new ItemStack(Material.BLAZE_POWDER, this.random.nextInt(2) + 3), EntityKnight.spellFortune};
    }

    protected int[] getPercents() {
        return new int[]{70, 55};
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.PLAYER, EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.GIANT};
    }

    protected int[] getPriorities() {
        return new int[]{3, 1, 1, 2};
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    public double getDefense() {
        return 100.0D;
    }

    void startAttacking() {
        this.blaze.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.blaze.getLocation(), 50, 2.0D, 2.0D, 2.0D, 1.0D);
        this.blaze.setAI(true);
        this.ai = true;
        this.blaze.setInvulnerable(false);
        this.save_def();
    }

    protected void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.ai) {
            if (event.getDamager() instanceof Arrow) {
                if (this.random.nextInt(100) + 1 > 90) {
                    event.setCancelled(true);
                    Arrow arrow = (Arrow)event.getDamager();
                    arrow.remove();
                    Arrow a = (Arrow)this.blaze.launchProjectile(Arrow.class);
                    a.setVelocity(arrow.getVelocity().multiply(-1));
                }
            } else if (event.getDamager() instanceof ShulkerBullet) {
                event.setCancelled(true);
            } else if (event.getDamager() instanceof LivingEntity && this.random.nextInt(100) + 1 > 65) {
                event.setCancelled(true);
                SoundManager.playFixedSound(this.blaze.getLocation(), MyTasks.getSound("boss_blaze_shield"), 1.0F, 1.0F, 25);
                this.blaze.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.blaze.getLocation(), 200, 1.0D, 1.0D, 1.0D, 0.0D);
            }
        }

    }

    protected void onTick() {
        if (this.blaze.getLocation().getBlock().getType().isSolid() && this.blaze.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
            this.blaze.getLocation().getBlock().breakNaturally();
            this.blaze.getLocation().getBlock().getRelative(BlockFace.UP).breakNaturally();
        }

    }

    private void task1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!EntityLBBlaze.this.blaze.isDead()) {
                    if (EntityLBBlaze.this.ai && EntityLBBlaze.this.blaze.getTarget() != null) {
                        if (EntityLBBlaze.this.random.nextInt(100) > 50) {
                            SmallFireball s = (SmallFireball)EntityLBBlaze.this.blaze.launchProjectile(SmallFireball.class);
                            s.setDirection(EntityLBBlaze.this.blaze.getLocation().getDirection());

                            for(int x = 3; x > 0; --x) {
                                EntityLBBlaze.this.shoot(s);
                            }
                        } else {
                            com.mcgamer199.luckyblock.customentity.EntityElementalCreeper c = new EntityElementalCreeper();
                            c.life = 85;
                            c.spawn(EntityLBBlaze.this.blaze.getLocation());
                            c.changeMaterial(Material.STONE, (byte)0);
                            ((Creeper)c.getEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.38D);
                            ((Creeper)c.getEntity()).setTarget(EntityLBBlaze.this.blaze.getTarget());
                        }
                    }
                } else {
                    task.run();
                }

            }
        }, 80L, 80L));
    }

    private void shoot(Projectile p) {
        final Vector v = p.getVelocity();
        int x_2 = this.random.nextInt(5) + 25;
        ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            private int x = x_2;

            public void run() {
                if (this.x > 0) {
                    double a1 = (double)(EntityLBBlaze.this.random.nextInt(20) - 10);
                    double a2 = (double)(EntityLBBlaze.this.random.nextInt(20) - 10);
                    double a3 = (double)(EntityLBBlaze.this.random.nextInt(20) - 10);
                    double x1 = a1 / 70.0D;
                    double x2 = a2 / 70.0D;
                    double x3 = a3 / 70.0D;
                    Vector v1 = new Vector(v.getX() + x1, v.getY() + x2, v.getZ() + x3);
                    SmallFireball b = (SmallFireball)EntityLBBlaze.this.blaze.launchProjectile(SmallFireball.class);
                    b.setShooter(EntityLBBlaze.this.blaze);
                    b.setVelocity(v1);
                    --this.x;
                }

            }
        }, 1L, 1L));
    }

    protected int getTickTime() {
        return 10;
    }

    protected boolean isAnimated() {
        return true;
    }

    protected List<String> getNames() {
        if (this.ai) {
            double f = this.blaze.getHealth() / (double)this.startHealth * 100.0D;
            return Arrays.asList(ChatColor.YELLOW + "Health " + ChatColor.GREEN + (int)f + ChatColor.WHITE + "%");
        } else {
            return Arrays.asList(ChatColor.LIGHT_PURPLE + "Blaze");
        }
    }

    protected int getNamesDelay() {
        return 5;
    }

    public int getAttackDamage() {
        return 32;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("ai", this.ai);
    }

    protected void onLoad(ConfigurationSection c) {
        this.blaze = (Blaze)this.entity;
        this.ai = c.getBoolean("ai");
        this.task1();
    }

    public Immunity[] getImmuneTo() {
        return this.ai ? new Immunity[]{Immunity.CONTACT, Immunity.DRAWNING, Immunity.FALL, Immunity.LIGHTNING, Immunity.THORNS, Immunity.SUFFOCATION, Immunity.ENTITY_EXPLOSION, Immunity.BLOCK_EXPLOSION} : new Immunity[]{Immunity.BLOCK_EXPLOSION, Immunity.CONTACT, Immunity.DRAGON_BREATH, Immunity.DRAWNING, Immunity.ENTITY_ATTACK, Immunity.ENTITY_EXPLOSION, Immunity.FALL, Immunity.FALLING_BLOCK, Immunity.FIRE, Immunity.FIRE_TICK, Immunity.LAVA, Immunity.LIGHTNING, Immunity.MAGIC, Immunity.POISON, Immunity.PROJECTILE, Immunity.SUFFOCATION, Immunity.WITHER};
    }
}