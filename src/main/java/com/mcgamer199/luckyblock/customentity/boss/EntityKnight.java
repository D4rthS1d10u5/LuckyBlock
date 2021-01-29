package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.item.ItemMaker;
import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.util.EntityUtils;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.resources.SkullData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityKnight extends CustomEntity implements EntityLBBoss {
    static final ItemStack spellFortune;
    private static final int startHealth = 50;
    private static final ItemStack head1;
    private static final ItemStack bow;
    private static final ItemStack sword;
    private static final ItemStack sword1;
    private static final ItemStack[] chestplates;
    private static final ItemStack[] leggings;
    private static final ItemStack[] boots;
    private static final ItemStack chestplate1;
    private static final ItemStack leggings1;
    private static final ItemStack boots1;

    static {
        spellFortune = ItemMaker.addEnchant(ItemMaker.createItem(Material.GHAST_TEAR, 1, 0, "" + ChatColor.GRAY + ChatColor.BOLD + "Spell of fortune", Arrays.asList("", ChatColor.GREEN + "+1000 luck")), LuckyBlockPlugin.enchantment_glow, 1);
        head1 = ItemMaker.createSkull(ItemMaker.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.GREEN + "Knight", Arrays.asList("", ChatColor.GRAY + "Obtained by killing lb bosses.", ChatColor.GREEN + "+2500 Luck")), "c86041e4-5b4e-4e8a-928c-9028b2437de6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ0NzcyZGM0ZGVmMjIyMTllZTZkODg5Y2NkYzJmOTIzMmVlMjNkMzU2ZGQ5ZTRhZGNlYTVmNzJjYzBjNjg5In19fQ==");
        bow = ItemMaker.addEnchants(new ItemStack(Material.BOW, 1), new int[]{10, 1}, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE);
        sword = ItemMaker.addEnchants(new ItemStack(Material.IRON_SWORD), new int[]{5}, Enchantment.FIRE_ASPECT);
        sword1 = ItemMaker.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{10, 10}, Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT);
        chestplates = new ItemStack[]{ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.BLUE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.RED), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.LIME), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.ORANGE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.PURPLE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.YELLOW), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.SILVER)};
        leggings = new ItemStack[]{ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.BLUE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.RED), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.LIME), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.ORANGE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.PURPLE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.YELLOW), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.SILVER)};
        boots = new ItemStack[]{ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.BLUE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.RED), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.LIME), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.ORANGE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.PURPLE), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.YELLOW), ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.SILVER)};
        chestplate1 = ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_CHESTPLATE), Color.BLACK);
        leggings1 = ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_LEGGINGS), Color.BLACK);
        boots1 = ItemMaker.setLeatherArmorColor(ItemMaker.createItem(Material.LEATHER_BOOTS), Color.BLACK);
    }

    private String status = "none";
    private String status_1 = "none";
    private WitherSkeleton l;
    private ItemStack head;
    private UUID blaze_uuid;
    private boolean angry;
    private BossBar bar;

    public EntityKnight() {
    }

    public Entity spawnFunction(Location loc) {
        SkullData sd = SkullData.getRandomSkullData("BOSS");
        this.head = ItemMaker.createSkull(ItemMaker.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.GREEN + "Knight", Arrays.asList("", ChatColor.GRAY + "Obtained by killing lb bosses.", ChatColor.GREEN + "+2500 Luck")), sd.getId(), sd.getData());
        WitherSkeleton skeleton = (WitherSkeleton) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50.0D);
        skeleton.setHealth(50.0D);
        skeleton.setSilent(true);
        skeleton.setCustomName(ChatColor.DARK_PURPLE + "Knight");
        skeleton.setCustomNameVisible(true);
        skeleton.getEquipment().setHelmet(this.head);
        skeleton.getEquipment().setItemInMainHand(bow);
        int x = this.random.nextInt(chestplates.length);
        skeleton.getEquipment().setChestplate(chestplates[x]);
        skeleton.getEquipment().setLeggings(leggings[x]);
        skeleton.getEquipment().setBoots(boots[x]);
        skeleton.getEquipment().setItemInOffHand(ItemMaker.createItem(Material.SHIELD));
        skeleton.setRemoveWhenFarAway(false);
        this.l = skeleton;
        com.mcgamer199.luckyblock.customentity.boss.EntityLBBlaze b = new com.mcgamer199.luckyblock.customentity.boss.EntityLBBlaze();
        b.spawn(loc);
        this.blaze_uuid = b.getUuid();
        this.ConstructProperties(sd.getName());
        this.func_ambient();
        this.func_angry();
        this.func_2();
        this.load_bar();
        return skeleton;
    }

    private void ConstructProperties(String type) {
    }

    private void func_1(final FallingBlock fb) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!fb.isValid()) {
                    if (EntityKnight.this.angry) {
                        fb.getWorld().createExplosion(fb.getLocation().getX(), fb.getLocation().getY(), fb.getLocation().getZ(), 12.0F, false, false);
                    } else {
                        fb.getWorld().createExplosion(fb.getLocation().getX(), fb.getLocation().getY(), fb.getLocation().getZ(), 7.0F, false, false);
                    }

                    if (fb.getLocation().getBlock().getType() == Material.COAL_BLOCK) {
                        fb.getLocation().getBlock().setType(Material.AIR);
                    }

                    task.run();
                }

            }
        }, 1L, 1L));
    }

    protected void onTick() {
        if (this.l.getTarget() != null) {
            if (this.l.getLocation().distance(this.l.getTarget().getLocation()) > 6.0D) {
                if (this.l.getEquipment().getItemInMainHand().getType() != Material.BOW) {
                    this.status = "fighting_bow";
                    this.l.getEquipment().setItemInMainHand(bow);
                }

                if (this.random.nextInt(100) + 1 > 90) {
                    int r = this.random.nextInt(1) + 1;
                    if (r == 1) {
                        this.status_1 = "spawn_bomb";
                        this.spawn_bomb();
                    } else if (r == 2) {
                        this.status_1 = "shoot_fire";
                        this.shoot_fire();
                    }
                }
            } else if (this.l.getEquipment().getItemInMainHand().getType() != Material.IRON_SWORD) {
                this.status = "fighting_sword";
                if (this.angry) {
                    this.l.getEquipment().setItemInMainHand(sword1);
                } else {
                    this.l.getEquipment().setItemInMainHand(sword);
                }
            }
        } else {
            this.status = "resting";
            CustomEntity e = CustomEntity.getByUUID(this.blaze_uuid);
            if (e != null) {
                if (this.l.getLocation().distance(e.getEntity().getLocation()) > 5.0D) {
                    EntityUtils.followEntity(e.getEntity().getLocation(), this.l, 1.2D);
                }

                if (this.l.getLocation().distance(e.getEntity().getLocation()) > 15.0D) {
                    this.l.teleport(e.getEntity());
                }

                if (this.l.getLocation().distance(e.getEntity().getLocation()) < 3.0D && this.l.getHealth() < this.l.getMaxHealth()) {
                    SoundManager.playFixedSound(this.l.getLocation(), SoundManager.getSound("boss_lb_heal"), 1.0F, 0.0F, 10);
                    this.l.getWorld().spawnParticle(Particle.HEART, this.l.getLocation(), 10, 0.5D, 0.5D, 0.5D, 1.0D);
                    if (this.l.getHealth() + 1.0D < this.l.getMaxHealth()) {
                        this.l.setHealth(this.l.getHealth() + 1.0D);
                    } else {
                        this.l.setHealth(this.l.getMaxHealth());
                    }
                }
            }
        }

    }

    private void spawn_bomb() {
        Entity e = this.l.getTarget();
        FallingBlock fb = this.l.getWorld().spawnFallingBlock(e.getLocation().add(0.0D, 6.0D, 0.0D), Material.COAL_BLOCK, (byte) 0);
        fb.setCustomName(ChatColor.RED + "Bomb");
        fb.setCustomNameVisible(true);
        this.func_1(fb);
    }

    private void shoot_fire() {
        Arrow a = this.l.launchProjectile(Arrow.class);
        FallingBlock b = a.getWorld().spawnFallingBlock(a.getLocation(), Material.FIRE, (byte) 0);
        b.setVelocity(a.getVelocity());
        a.remove();
    }

    protected int getTickTime() {
        return 40;
    }

    protected void onDeath(EntityDeathEvent event) {
        SoundManager.playFixedSound(this.l.getLocation(), SoundManager.getSound("boss_lb_death"), 1.0F, 0.0F, 10);
        com.mcgamer199.luckyblock.customentity.boss.EntityLBBlaze b = (EntityLBBlaze) CustomEntity.getByUUID(this.blaze_uuid);
        if (b != null && b.getEntity().isValid()) {
            b.startAttacking();
        }

    }

    protected List<String> getNames() {
        double f = this.l.getHealth() / 50.0D * 100.0D;
        return Arrays.asList(ChatColor.DARK_PURPLE + "Knight " + ChatColor.YELLOW + "Health " + ChatColor.GREEN + (int) f + ChatColor.WHITE + "%");
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    public int getXp() {
        return 17500;
    }

    protected int xpsize() {
        return 1600;
    }

    protected boolean isAnimated() {
        return true;
    }

    protected int getNamesDelay() {
        return 5;
    }

    public int getAttackDamage() {
        return 25;
    }

    public double getDefense() {
        return 82.0D;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.PLAYER, EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.SNOWMAN, EntityType.HORSE};
    }

    protected int[] getPriorities() {
        return new int[]{2, 1, 1, 1, 1};
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{spellFortune, LBItem.KEY_1.getItem(), head1, new ItemStack(Material.SHIELD)};
    }

    protected int[] getPercents() {
        return new int[]{100, 100, 100, 25};
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.CONTACT, Immunity.DRAWNING, Immunity.FALL, Immunity.LIGHTNING, Immunity.BLOCK_EXPLOSION, Immunity.ENTITY_EXPLOSION, Immunity.PROJECTILE};
    }

    public String getStatus() {
        return this.status;
    }

    public String getStatus_1() {
        return this.status_1;
    }

    private void load_bar() {
        this.bar = Bukkit.createBossBar(ChatColor.DARK_PURPLE + "LBBoss", BarColor.BLUE, BarStyle.SOLID);
        this.func_boss_bar();
    }

    private void func_boss_bar() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityKnight.this.l.isDead()) {
                    if (EntityKnight.this.bar != null) {
                        EntityKnight.this.bar.setProgress(EntityKnight.this.l.getHealth() / EntityKnight.this.l.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                    }
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    public BossBar getBossBar() {
        return this.bar;
    }

    public int getBossBarRange() {
        return 16;
    }

    protected boolean hasBossBar() {
        return true;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("blaze", this.blaze_uuid.toString());
        c.set("angry", this.angry);
    }

    protected void onLoad(ConfigurationSection c) {
        this.l = (WitherSkeleton) this.entity;
        if (c.getString("blaze") != null) {
            this.blaze_uuid = UUID.fromString(c.getString("blaze"));
        }

        this.angry = c.getBoolean("angry");
        this.func_ambient();
        this.func_angry();
        this.func_2();
        this.load_bar();
    }

    protected void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        if (this.angry) {
            event.setDamage(event.getDamage() * 5.0D);
        } else {
            event.setDamage(event.getDamage() * 3.0D);
        }

    }

    protected void onShootBow(final EntityShootBowEvent event) {
        if (event.getProjectile() != null && event.getProjectile() instanceof Projectile) {
            Projectile p = (Projectile) event.getProjectile();
            final Vector v = p.getVelocity();
            this.func_a(p);
            int x_1;
            if (this.angry) {
                x_1 = this.random.nextInt(5) + 30;
            } else {
                x_1 = this.random.nextInt(18) + 12;
            }

            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
                private int x = x_1;

                public void run() {
                    if (this.x > 0) {
                        if (!EntityKnight.this.l.isDead()) {
                            double a1 = EntityKnight.this.random.nextInt(20) - 10;
                            double a2 = EntityKnight.this.random.nextInt(20) - 10;
                            double a3 = EntityKnight.this.random.nextInt(20) - 10;
                            double x1 = a1 / 70.0D;
                            double x2 = a2 / 70.0D;
                            double x3 = a3 / 70.0D;
                            Vector v1 = new Vector(v.getX() + x1, v.getY() + x2, v.getZ() + x3);
                            Arrow arrow = event.getEntity().launchProjectile(Arrow.class);
                            arrow.setShooter(EntityKnight.this.l);
                            arrow.setVelocity(v1);
                            arrow.setBounce(true);
                            if (EntityKnight.this.angry) {
                                arrow.setFireTicks(100);
                                arrow.setCritical(true);
                            }

                            EntityKnight.this.func_a(arrow);
                            --this.x;
                        } else {
                            task.run();
                        }
                    }

                }
            }, 2L, 2L));
        }

    }

    private void func_a(final Projectile p) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (p.isValid() && !p.isOnGround()) {
                    p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation(), 3, 0.1D, 0.1D, 0.1D, 0.0D);
                } else {
                    p.remove();
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    protected void onDamage(EntityDamageEvent event) {
        SoundManager.playFixedSound(this.l.getLocation(), SoundManager.getSound("boss_lb_hurt"), 1.0F, 0.0F, 10);
        double d1 = this.l.getHealth();
        double d2 = 50.0D;
        double d3 = d1 / d2;
        if (d3 < 0.4D && !this.angry) {
            this.makeAngry();
        }

    }

    private void func_ambient() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityKnight.this.getEntity() != null && !EntityKnight.this.getEntity().isDead()) {
                    SoundManager.playFixedSound(EntityKnight.this.l.getLocation(), SoundManager.getSound("boss_lb_ambient"), 1.0F, 0.0F, 35);
                } else {
                    task.run();
                }

            }
        }, 150L, 150L));
    }

    private void func_angry() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityKnight.this.l != null && EntityKnight.this.l.isValid()) {
                    if (EntityKnight.this.angry) {
                        Iterator var2 = EntityKnight.this.l.getNearbyEntities(7.0D, 7.0D, 7.0D).iterator();

                        while (var2.hasNext()) {
                            Entity e = (Entity) var2.next();
                            if (e instanceof Animals) {
                                e.getWorld().strikeLightning(e.getLocation());
                            }
                        }
                    }

                    if (EntityKnight.this.l.getTarget() == null) {
                        Block b = EntityKnight.this.getB(4);
                        if (b != null) {
                            EntityUtils.followEntity(b.getLocation(), EntityKnight.this.l, 1.1D);
                        }
                    }
                } else {
                    task.run();
                }

            }
        }, 60L, 60L));
        final ITask task1 = new ITask();
        task1.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityKnight.this.l != null && EntityKnight.this.l.isValid()) {
                    if (EntityKnight.this.angry && EntityKnight.this.l.getTarget() != null) {
                        Zombie zombie = (Zombie) EntityKnight.this.l.getWorld().spawnEntity(EntityKnight.this.l.getLocation().add(EntityKnight.this.random.nextInt(10) - 5, 5.0D, EntityKnight.this.random.nextInt(10) - 5), EntityType.ZOMBIE);
                        zombie.setTarget(EntityKnight.this.l.getTarget());
                        EntityKnight.this.func_zombie(zombie);
                    }
                } else {
                    task1.run();
                }

            }
        }, 150L, 150L));
    }

    private void func_zombie(final Zombie zombie) {
        final ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!zombie.isDead()) {
                    zombie.remove();
                }

                task.run();
            }
        }, 138L));
    }

    public boolean isAngry() {
        return this.angry;
    }

    public String getSpawnEggEntity() {
        return "wither_skeleton";
    }

    private Block getB(int range) {
        for (int x = range * -1; x < range + 1; ++x) {
            for (int y = range * -1; y < range + 1; ++y) {
                for (int z = range * -1; z < range + 1; ++z) {
                    Block b = this.l.getLocation().add(x, y, z).getBlock();
                    if (LuckyCraftingTable.getByBlock(b) != null) {
                        return b;
                    }
                }
            }
        }

        return null;
    }

    private void func_2() {
        ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                LuckyCraftingTable c = LuckyCraftingTable.getByBlock(EntityKnight.this.l.getLocation().getBlock().getRelative(BlockFace.DOWN));
                if (c != null) {
                    c.remove();
                    c.getBlock().setType(Material.AIR);
                }

            }
        }, 20L, 20L));
    }

    public void makeAngry() {
        this.talk(ChatColor.RED + "How dare you!", 10);
        this.angry = true;
        if (this.l.getEquipment().getItemInMainHand() != null && this.l.getEquipment().getItemInMainHand().getType() == Material.IRON_SWORD) {
            this.l.getEquipment().setItemInMainHand(sword1);
        }

        this.l.getEquipment().setHelmet(head1);
        this.l.getEquipment().setChestplate(chestplate1);
        this.l.getEquipment().setLeggings(leggings1);
        this.l.getEquipment().setBoots(boots1);
        this.l.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0D);
        this.l.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
        this.l.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3D);
        this.save_def();
    }

    protected void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) event.getEntity();
            l.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 2));
            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
        }

    }

    private void talk(String msg, int d) {
        Iterator var4 = Bukkit.getOnlinePlayers().iterator();

        while (var4.hasNext()) {
            Player p = (Player) var4.next();
            if (this.l.getLocation().distance(p.getLocation()) < (double) (d + 1)) {
                p.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "LBBoss" + ChatColor.GOLD + ChatColor.BOLD + "]" + ChatColor.RESET + ": " + msg);
            }
        }

    }

    public void onHitEntityWithBeam(LivingEntity entity, String tag) {
    }
}
