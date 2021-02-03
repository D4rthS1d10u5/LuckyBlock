package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import com.mcgamer199.luckyblock.customentity.boss.main.BossFunctions;
import com.mcgamer199.luckyblock.customentity.boss.main.BossFunctions.ParticleHelper;
import com.mcgamer199.luckyblock.customentity.nametag.EntityFloatingText;
import com.mcgamer199.luckyblock.customentity.nametag.INameTagHealth;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class EntityUnderwaterBoss extends CustomEntity implements EntityLBBoss {
    private static final ItemStack trophy;
    private static final ItemStack winItem;

    static {
        trophy = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.BLUE + "Underwater Boss", Arrays.asList("", ChatColor.GRAY + "Obtained by killing underwater bosses.")), "e56a8749-8a4a-40cc-9ded-3c90f8ae8c63", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM3OTc0ODJhMTRiZmNiODc3MjU3Y2IyY2ZmMWI2ZTZhOGI4NDEzMzM2ZmZiNGMyOWE2MTM5Mjc4YjQzNmIifX19");
        winItem = ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.PAPER, 1, 0, ChatColor.GREEN + "Congratulations", Arrays.asList("", ChatColor.GRAY + "You beat the underwater boss!")), LuckyBlockPlugin.enchantment_glow, 1);
    }

    ElderGuardian elder;
    private int power = 1;
    private BossBar bar;

    public EntityUnderwaterBoss() {
    }

    protected Entity spawnFunction(Location loc) {
        ElderGuardian elder = (ElderGuardian) loc.getWorld().spawnEntity(loc, EntityType.ELDER_GUARDIAN);
        elder.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(325.0D);
        elder.setHealth(325.0D);
        elder.setCustomName("" + ChatColor.GOLD + ChatColor.BOLD + "Guardian of the castle");
        elder.setCustomNameVisible(true);
        this.elder = elder;
        INameTagHealth n = new INameTagHealth();
        n.mode_base = INameTagHealth.NameTagMode.CUSTOM_HEARTS;
        n.heartsAmount = 5;
        n.spawn(elder, new double[]{0.0D, 2.5D, 0.0D});
        this.load_bar();
        this.func_all();
        return elder;
    }

    private void func_all() {
        this.func_boss_bar();
        this.func_Beam();
        this.func_Followers();
        this.func_Squids();
        this.func_Effects();
        this.func_Tnt();
        this.func_Invisible();
    }

    private void load_bar() {
        this.bar = Bukkit.createBossBar(this.elder.getCustomName(), BarColor.BLUE, BarStyle.SEGMENTED_6);
    }

    private void func_Beam() {
        Scheduler.create(() -> {
            int x = 0;

            for (Entity e : EntityUnderwaterBoss.this.elder.getNearbyEntities(15.0D, 15.0D, 15.0D)) {
                if (EntityUnderwaterBoss.this.isTarget(e)) {
                    EntityUnderwaterBoss.this.shootBeam((LivingEntity) e);
                    ++x;
                    if (x > 6) {
                        break;
                    }
                }
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(10, 100);
    }

    private void shootBeam(LivingEntity target) {
        if (this.power < 5) {
            BossFunctions.shoot_beam(this, this.elder, target, 72, 0.3D, new ParticleHelper(Particle.CRIT, 1, new double[]{0.0D, 0.0D, 0.0D}, 0.0F));
        } else {
            BossFunctions.shoot_beam(this, this.elder, target, 95, 0.45D, new ParticleHelper(Particle.CRIT, 1, new double[]{0.0D, 0.0D, 0.0D}, 0.0F));
        }

    }

    private void shootBeam(LivingEntity target, int a, double b) {
        if (this.elder.getLocation().getBlock().getType() == Material.WATER || this.elder.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            MyTasks.playEffects(Particle.WATER_BUBBLE, this.elder.getLocation().add(0.0D, 1.0D, 0.0D), 75, new double[]{1.0D, 1.5D, 1.0D}, 1.0F);
        }

        BossFunctions.shoot_beam(this, this.elder, target, a, b, new ParticleHelper(Particle.CRIT, 1, new double[]{0.0D, 0.0D, 0.0D}, 0.0F));
    }

    private void func_Effects() {
        Scheduler.create(() -> {
            for (Entity e : EntityUnderwaterBoss.this.elder.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
                if (e instanceof Player) {
                    Player p = (Player) e;
                    if (EntityUnderwaterBoss.this.bPlayer(p)) {
                        p.spawnParticle(Particle.MOB_APPEARANCE, p.getLocation(), 1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 260, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 1));
                    }
                }
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(256, 256);
    }

    private void func_Tnt() {
        Scheduler.create(() -> {
            if (EntityUnderwaterBoss.this.elder.getTarget() != null) {
                TNTPrimed tnt = (TNTPrimed) EntityUnderwaterBoss.this.elder.getWorld().spawnEntity(EntityUnderwaterBoss.this.elder.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(2000);
                tnt.setVelocity(EntityUnderwaterBoss.this.getRandomVelocity());
                tnt.setCustomName("" + ChatColor.RED + ChatColor.BOLD + "5s");
                tnt.setCustomNameVisible(true);
                EntityUnderwaterBoss.this.spawnTnt(tnt);
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(340, 340);
    }

    private void func_boss_bar() {
        Scheduler.create(() -> {
            if (EntityUnderwaterBoss.this.bar != null) {
                EntityUnderwaterBoss.this.bar.setProgress(EntityUnderwaterBoss.this.elder.getHealth() / EntityUnderwaterBoss.this.elder.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(1, 1);
    }

    private void spawnTnt(final TNTPrimed tnt) {
        Scheduler.timer(new BukkitRunnable() {
            private int times = 4;

            @Override
            public void run() {
                if (this.times > 0) {
                    tnt.setCustomName("" + ChatColor.RED + ChatColor.BOLD + this.times + "s");
                    --this.times;
                } else {
                    if (!tnt.isDead()) {
                        tnt.remove();
                        tnt.getWorld().createExplosion(tnt.getLocation().getX(), tnt.getLocation().getY(), tnt.getLocation().getZ(), 3.0F, false, false);
                    }

                    cancel();
                }
            }
        }, 20, 20);
    }

    private void func_Invisible() {
        Scheduler.create(() -> EntityUnderwaterBoss.this.elder.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0)))
                .predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(250, 440);
    }

    private void func_Followers() {
        Scheduler.create(() -> {
            if (EntityUnderwaterBoss.this.checkFollowers() < 6) {
                com.mcgamer199.luckyblock.customentity.boss.EntityUnderwaterFollower follower = new com.mcgamer199.luckyblock.customentity.boss.EntityUnderwaterFollower();
                follower.age = 280;
                follower.target = EntityUnderwaterBoss.this.elder.getTarget();
                follower.spawn(EntityUnderwaterBoss.this.elder.getLocation());
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(160, 160);
    }

    private void func_Squids() {
        Scheduler.create(() -> {
            for (Entity e : EntityUnderwaterBoss.this.elder.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
                if (e.getType() == EntityType.SQUID) {
                    LivingEntity l = (LivingEntity) e;
                    l.damage(60.0D);
                    MyTasks.playEffects(Particle.FLAME, l.getLocation(), 20, new double[]{0.5D, 0.5D, 0.5D}, 0.1F);
                    if (EntityUnderwaterBoss.this.power < 20 && EntityUnderwaterBoss.this.random.nextInt(100) > 58) {
                        EntityUnderwaterBoss var10000 = EntityUnderwaterBoss.this;
                        var10000.power = var10000.power + 1;
                        EntityUnderwaterBoss.this.save_def();
                    }
                }
            }
        }).predicate(() -> !EntityUnderwaterBoss.this.elder.isDead()).timer(70, 70);
    }

    private int checkFollowers() {
        int total = 0;

        for (Entity e : this.elder.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
            CustomEntity c = CustomEntity.getByUUID(e.getUniqueId());
            if (c != null && c instanceof EntityUnderwaterFollower) {
                ++total;
                if (total > 5) {
                    break;
                }
            }
        }

        return total;
    }

    private boolean isTarget(Entity entity) {
        EntityType[] var5;
        int var4 = (var5 = this.getTargets()).length;

        for (int var3 = 0; var3 < var4; ++var3) {
            EntityType types = var5[var3];
            if (entity.getType() == EntityType.PLAYER) {
                return this.bPlayer((Player) entity);
            }

            if (entity.getType() == types) {
                return true;
            }
        }

        return false;
    }

    private boolean bPlayer(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR;
    }

    protected EntityType[] getTargets() {
        return new EntityType[]{EntityType.PLAYER, EntityType.BAT, EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.CHICKEN, EntityType.COW, EntityType.DONKEY, EntityType.HORSE, EntityType.MULE, EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PARROT, EntityType.PIG, EntityType.RABBIT, EntityType.SHEEP, EntityType.SNOWMAN, EntityType.WOLF};
    }

    protected boolean targetsNearbyEntities() {
        return true;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.BLOCK_EXPLOSION, Immunity.ENTITY_EXPLOSION, Immunity.LIGHTNING, Immunity.FIRE, Immunity.FIRE_TICK, Immunity.LAVA};
    }

    public double getDefense() {
        return 9.5D;
    }

    protected boolean hasBossBar() {
        return true;
    }

    public int getBossBarRange() {
        return 26;
    }

    public BossBar getBossBar() {
        return this.bar;
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{LBItem.KEY_4.getItem(), trophy, winItem, ItemStackUtils.createItem(Material.PRISMARINE_SHARD, this.random.nextInt(3) + 1, 0, ChatColor.DARK_AQUA + "Prismarine Shard")};
    }

    protected int[] getPercents() {
        return new int[]{100, 100, 100, 62};
    }

    public String getSpawnEggEntity() {
        return "ghast";
    }

    public void onHitEntityWithBeam(LivingEntity entity, String tag) {
        MyTasks.playEffects(Particle.CLOUD, entity.getLocation(), 10, new double[]{0.2D, 0.2D, 0.2D}, 0.0F);
        double i = this.power * 21;
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p.getEquipment().getChestplate() != null && p.getEquipment().getChestplate().containsEnchantment(Enchantment.PROTECTION_PROJECTILE)) {
                int level = p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
                double l = level;
                i *= 1.0D / l;
            }
        }

        entity.damage(i, this.elder);
        entity.setFireTicks(40 + this.power * 10);
    }

    protected void onDamageByPlayer(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        if (player.getInventory().getItemInMainHand() != null) {
            Material mat = player.getInventory().getItemInMainHand().getType();
            if (mat != Material.WOOD_AXE && mat != Material.STONE_AXE && mat != Material.IRON_AXE && mat != Material.GOLD_AXE && mat != Material.DIAMOND_AXE) {
                event.setCancelled(true);
            }
        }

        if (this.random.nextInt(100) > 70) {
            player.getWorld().strikeLightning(player.getLocation());
        }

    }

    protected void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            event.setDamage(event.getDamage() / 3.0D);
            if (this.random.nextInt(100) + 1 > 93) {
                Arrow a = (Arrow) event.getDamager();
                if (a.getShooter() != null && a.getShooter() instanceof LivingEntity) {
                    this.func_gatling((LivingEntity) a.getShooter(), this.random.nextInt(5) + 3);
                }
            }
        }

    }

    private void func_gatling(final LivingEntity target, int times) {
        Scheduler.timer(new BukkitRunnable() {
            private int t = times;

            @Override
            public void run() {
                if (!EntityUnderwaterBoss.this.elder.isDead() && this.t > 0) {
                    EntityUnderwaterBoss.this.shootBeam(target, 220, 0.32D);
                    --this.t;
                } else {
                    cancel();
                }
            }
        }, 5, 30);
    }

    protected void onDamage(EntityDamageEvent event) {
        if (this.elder.getHealth() - event.getFinalDamage() < 101.0D && this.power < 4) {
            this.power += 4;
            this.save_def();
        }

    }

    private Vector getRandomVelocity() {
        double x1 = this.random.nextInt(80) - 40;
        double z1 = this.random.nextInt(80) - 40;
        double x2 = x1 / 50.0D;
        double z2 = z1 / 50.0D;
        Vector v = new Vector(x2, 0.3D, z2);
        return v;
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

    public int getXp() {
        return 12700;
    }

    protected int xpsize() {
        return 1490;
    }

    protected void onChunkLoad() {
        this.elder = (ElderGuardian) this.entity;
    }

    protected void onKilledByPlayer(EntityDamageByEntityEvent event, Player player) {
        com.mcgamer199.luckyblock.customentity.nametag.EntityFloatingText f = new EntityFloatingText();
        f.age = 55;
        f.mode = 1;
        f.b = 160;
        f.text = ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " beat the castle guardian";
        f.spawn(this.entity.getLocation());
    }

    protected void xpToDrop(ExperienceOrb[] xp) {
        ExperienceOrb[] var5 = xp;
        int var4 = xp.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            ExperienceOrb exp = var5[var3];
            exp.setInvulnerable(true);
        }

    }

    protected void onLoad(ConfigurationSection c) {
        this.elder = (ElderGuardian) this.entity;
        this.power = c.getInt("Power");
        this.load_bar();
        this.func_all();
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Power", this.power);
    }
}
