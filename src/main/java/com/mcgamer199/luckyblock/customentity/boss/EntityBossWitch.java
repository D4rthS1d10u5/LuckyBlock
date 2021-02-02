package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.SoundUtils;
import com.mcgamer199.luckyblock.customentity.nametag.INameTagHealth;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.LBItem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class EntityBossWitch extends CustomEntity implements EntityLBBoss {
    private static final ItemStack head;

    static {
        head = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.DARK_PURPLE + "Witch", Arrays.asList("", ChatColor.GRAY + "Obtained by killing witch bosses.")), "0a9e8efb-9191-4c81-80f5-e27ca5433156", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19");
    }

    private Witch w;
    private BossBar bar;
    private int fire_damage = 7;

    public EntityBossWitch() {
    }

    protected Entity spawnFunction(Location loc) {
        Witch witch = (Witch) loc.getWorld().spawnEntity(loc, EntityType.WITCH);
        witch.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(90.0D);
        witch.setHealth(90.0D);
        witch.setCustomName(ChatColor.LIGHT_PURPLE + "Witch");
        witch.setCustomNameVisible(true);
        witch.setSilent(true);
        this.w = witch;
        com.mcgamer199.luckyblock.customentity.nametag.INameTagHealth n = new com.mcgamer199.luckyblock.customentity.nametag.INameTagHealth();
        n.mode_base = INameTagHealth.NameTagMode.THREE_HEARTS;
        n.spawn(witch, new double[]{0.0D, 2.5D, 0.0D});
        this.load_bar();
        this.func_all();
        return witch;
    }

    private void load_bar() {
        this.bar = Bukkit.createBossBar(ChatColor.LIGHT_PURPLE + "Witch", BarColor.BLUE, BarStyle.SOLID);
    }

    public ItemStack[] getDrops() {
        ItemStack potion = ItemStackUtils.createItem(Material.POTION, 1, 0, "" + ChatColor.RED + ChatColor.BOLD + "Super Potion");
        PotionMeta pM = (PotionMeta) potion.getItemMeta();
        pM.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4000, 0), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 4000, 2), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 4000, 0), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 4000, 1), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 4000, 0), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 4000, 0), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 4000, 4), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 4000, 1), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 2), true);
        pM.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 4000, 3), true);
        pM.setColor(Color.LIME);
        potion.setItemMeta(pM);
        return new ItemStack[]{potion, LBItem.KEY_2.getItem(), head, new ItemStack(Material.QUARTZ, this.random.nextInt(3) + 3), new ItemStack(Material.GLASS_BOTTLE, this.random.nextInt(2) + 1), new ItemStack(Material.POTION)};
    }

    protected int[] getPercents() {
        return new int[]{100, 100, 100, 75, 25, 15};
    }

    protected List<String> getNames() {
        return Arrays.asList(ChatColor.LIGHT_PURPLE + "Witch", ChatColor.DARK_PURPLE + "Witch");
    }

    protected boolean hasBossBar() {
        return true;
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.IRON_GOLEM, EntityType.CREEPER, EntityType.SNOWMAN, EntityType.VILLAGER, EntityType.PLAYER, EntityType.ENDERMAN};
    }

    public BossBar getBossBar() {
        return this.bar;
    }

    public int getBossBarRange() {
        return 32;
    }

    protected int getNamesDelay() {
        return 10;
    }

    protected boolean isAnimated() {
        return true;
    }

    public int getXp() {
        return 7560;
    }

    protected int xpsize() {
        return 550;
    }

    private void func_all() {
        this.func_potions();
        this.func_lightning();
        this.func_boss_bar();
        this.func_throw_fire1();
        this.func_ambient();
        this.func_invisible();
    }

    private void func_boss_bar() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityBossWitch.this.w.isDead()) {
                    if (EntityBossWitch.this.bar != null) {
                        EntityBossWitch.this.bar.setTitle(EntityBossWitch.this.w.getCustomName());
                        EntityBossWitch.this.bar.setProgress(EntityBossWitch.this.w.getHealth() / EntityBossWitch.this.w.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                    }
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    private void func_potions() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityBossWitch.this.w.isDead()) {
                    Location loc = new Location(EntityBossWitch.this.w.getWorld(), EntityBossWitch.this.w.getLocation().getX(), EntityBossWitch.this.w.getLocation().getY() + 2.5D, EntityBossWitch.this.w.getLocation().getZ());
                    boolean isLingering = false;
                    int i = EntityBossWitch.this.random.nextInt(13) + 1;
                    Object s;
                    if (i > 1) {
                        s = EntityBossWitch.this.w.getWorld().spawnEntity(loc, EntityType.SPLASH_POTION);
                    } else {
                        s = EntityBossWitch.this.w.getWorld().spawnEntity(loc, EntityType.LINGERING_POTION);
                        isLingering = true;
                    }

                    ((ThrownPotion) s).setShooter(EntityBossWitch.this.w);
                    ((ThrownPotion) s).setVelocity(EntityBossWitch.this.getRandomVelocity());
                    ((ThrownPotion) s).setBounce(true);
                    ((ThrownPotion) s).setItem(EntityBossWitch.this.getRandomPotion(isLingering));
                } else {
                    task.run();
                }

            }
        }, 40L, 17L));
    }

    private void func_throw_fire1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityBossWitch.this.w.isDead()) {
                    if (EntityBossWitch.this.w.getTarget() != null && EntityBossWitch.this.aTarget(EntityBossWitch.this.w.getTarget())) {
                        EntityBossWitch.this.func_throw_fire2();
                    }
                } else {
                    task.run();
                }

            }
        }, 120L, 120L));
    }

    private boolean aTarget(LivingEntity target) {
        return target.getEquipment().getChestplate() == null || !target.getEquipment().getChestplate().containsEnchantment(Enchantment.PROTECTION_FIRE);
    }

    private void func_throw_fire2() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            int times = 15;

            public void run() {
                if (!EntityBossWitch.this.w.isDead() && EntityBossWitch.this.w.getTarget() != null) {
                    if (this.times > 0) {
                        EntityBossWitch.this.spawn_fire_block(1.0D, 0.0D);
                        EntityBossWitch.this.spawn_fire_block(0.0D, 1.0D);
                        EntityBossWitch.this.spawn_fire_block(-1.0D, 0.0D);
                        EntityBossWitch.this.spawn_fire_block(0.0D, -1.0D);
                        EntityBossWitch.this.spawn_fire_block(1.0D, 1.0D);
                        EntityBossWitch.this.spawn_fire_block(-1.0D, 1.0D);
                        EntityBossWitch.this.spawn_fire_block(1.0D, -1.0D);
                        EntityBossWitch.this.spawn_fire_block(-1.0D, -1.0D);

                        for (int i = 10; i > 0; --i) {
                            double x1 = EntityBossWitch.this.random.nextInt(200) - 100;
                            double z1 = EntityBossWitch.this.random.nextInt(200) - 100;
                            double x2 = x1 / 100.0D;
                            double z2 = z1 / 100.0D;
                            EntityBossWitch.this.spawn_fire_block(x2, z2);
                        }

                        --this.times;
                    } else {
                        task.run();
                    }
                }

            }
        }, 5L, 3L));
    }

    private void spawn_fire_block(double x, double z) {
        Location l = new Location(this.w.getWorld(), this.w.getLocation().getX(), this.w.getLocation().getY() + 1.0D, this.w.getLocation().getZ());
        double r = this.random.nextInt(30) + 10;
        double d = r / 100.0D;
        FallingBlock fb = this.w.getWorld().spawnFallingBlock(l, new MaterialData(Material.FIRE));
        Vector v = new Vector(x, d, z);
        fb.setVelocity(v);
        fb.setDropItem(false);
        this.func_throw_fire3(fb);
    }

    private void func_throw_fire3(final FallingBlock fb) {
        final int dmg = this.fire_damage - this.random.nextInt(3);
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (fb.isValid()) {
                    Iterator var2 = fb.getNearbyEntities(1.0D, 1.0D, 1.0D).iterator();

                    while (var2.hasNext()) {
                        Entity e = (Entity) var2.next();
                        if (e instanceof LivingEntity) {
                            LivingEntity l = (LivingEntity) e;
                            if (e != EntityBossWitch.this.w) {
                                l.damage(dmg);
                                e.setFireTicks(100);
                            }
                        }
                    }
                } else {
                    task.run();
                }

            }
        }, 3L, 3L));
    }

    private void func_invisible() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityBossWitch.this.w.isDead()) {
                    EntityBossWitch.this.w.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 180, 0));
                } else {
                    task.run();
                }

            }
        }, 250L, 460L));
    }

    private PotionEffect getRandomEffect() {
        PotionEffect p = null;
        int i = this.random.nextInt(8) + 1;
        if (i == 1) {
            p = new PotionEffect(PotionEffectType.BLINDNESS, this.random.nextInt(200) + 300, 0);
        } else if (i == 2) {
            p = new PotionEffect(PotionEffectType.CONFUSION, this.random.nextInt(200) + 300, 0);
        } else if (i == 3) {
            p = new PotionEffect(PotionEffectType.HARM, 0, this.random.nextInt(2));
        } else if (i == 4) {
            p = new PotionEffect(PotionEffectType.HUNGER, this.random.nextInt(200) + 300, this.random.nextInt(3));
        } else if (i == 5) {
            p = new PotionEffect(PotionEffectType.POISON, this.random.nextInt(200) + 300, this.random.nextInt(3));
        } else if (i == 6) {
            p = new PotionEffect(PotionEffectType.SLOW, this.random.nextInt(200) + 300, this.random.nextInt(3));
        } else if (i == 7) {
            p = new PotionEffect(PotionEffectType.SLOW_DIGGING, this.random.nextInt(200) + 300, this.random.nextInt(5));
        } else if (i == 8) {
            p = new PotionEffect(PotionEffectType.WEAKNESS, this.random.nextInt(200) + 300, this.random.nextInt(4));
        }

        return p;
    }

    private Vector getRandomVelocity() {
        double x1 = this.random.nextInt(60) - 30;
        double z1 = this.random.nextInt(60) - 30;
        double x2 = x1 / 100.0D;
        double z2 = z1 / 100.0D;
        Vector v = new Vector(x2, 0.5D, z2);
        return v;
    }

    private ItemStack getRandomPotion(boolean isLingering) {
        ItemStack item;
        if (isLingering) {
            item = new ItemStack(Material.LINGERING_POTION);
        } else {
            item = new ItemStack(Material.SPLASH_POTION);
        }

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(this.getRandomEffect(), true);
        Color[] colors = new Color[]{Color.BLACK, Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW};
        meta.setColor(colors[this.random.nextInt(colors.length)]);
        item.setItemMeta(meta);
        return item;
    }

    private void func_lightning() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityBossWitch.this.w.isDead()) {
                    EntityBossWitch.this.w.getWorld().strikeLightning(EntityBossWitch.this.w.getLocation());
                } else {
                    task.run();
                }

            }
        }, 100L, 100L));
    }

    private void func_ambient() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityBossWitch.this.getEntity() != null && EntityBossWitch.this.getEntity().isValid()) {
                    SoundUtils.playFixedSound(EntityBossWitch.this.w.getLocation(), SoundUtils.getSound("boss_witch_ambient"), 1.0F, 0.0F, 26);
                } else {
                    task.run();
                }

            }
        }, 115L, 105L));
    }

    protected void onDamage(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.LIGHTNING) {
            if (this.w.getHealth() + 5.0D < this.w.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
                this.w.setHealth(this.w.getHealth() + 3.0D);
            } else {
                this.w.setHealth(this.w.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }

        } else {
            if (event.getCause() == DamageCause.FALLING_BLOCK) {
                event.setDamage(event.getDamage() * 8.5D);
            } else if (event.getCause() == DamageCause.PROJECTILE) {
                event.setDamage(event.getDamage() / 10.0D);
            } else if ((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && this.fire_damage < 67) {
                this.fire_damage += 2;
                MyTasks.playEffects(Particle.FLAME, this.w.getLocation(), 34, new double[]{0.5D, 0.5D, 0.5D}, 0.0F);
                this.save_def();
            }

            if (!event.isCancelled()) {
                SoundUtils.playFixedSound(this.w.getLocation(), SoundUtils.getSound("boss_witch_hurt"), 1.0F, 0.0F, 20);
            }
        }
    }

    protected void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) e;
            if (l.getEquipment().getItemInMainHand() != null && l.getEquipment().getItemInMainHand().getType() == Material.GOLD_SWORD) {
                event.setDamage(event.getDamage() * 5.0D);
            }
        }

    }

    protected void onDeath(EntityDeathEvent event) {
        SoundUtils.playFixedSound(this.w.getLocation(), SoundUtils.getSound("boss_witch_death"), 1.0F, 0.0F, 20);
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.ENTITY_EXPLOSION, Immunity.FALL, Immunity.FIRE, Immunity.FIRE_TICK, Immunity.LAVA, Immunity.POISON, Immunity.MAGIC, Immunity.LIGHTNING, Immunity.SUFFOCATION};
    }

    public double getDefense() {
        return 4.0D;
    }

    public String getSpawnEggEntity() {
        return "creeper";
    }

    public boolean itemsEdited() {
        return true;
    }

    public void itemsToDrop(Item[] items) {
        Item[] var5 = items;
        int var4 = items.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            Item i = var5[var3];
            i.setInvulnerable(true);
        }

    }

    protected void onSave(ConfigurationSection c) {
        c.set("FireDamage", this.fire_damage);
    }

    protected void onLoad(ConfigurationSection c) {
        this.w = (Witch) this.entity;
        this.fire_damage = c.getInt("FireDamage");
        this.load_bar();
        this.func_all();
    }

    public void onHitEntityWithBeam(LivingEntity entity, String tag) {
    }
}

