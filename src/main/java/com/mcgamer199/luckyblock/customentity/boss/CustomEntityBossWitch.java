package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityHealthTag;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityHealthTag.HealthDisplayMode;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.EffectUtils;
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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CustomEntityBossWitch extends CustomEntity implements CustomEntityBoss {

    private static final ItemStack head = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.DARK_PURPLE + "Witch", Arrays.asList("", ChatColor.GRAY + "Obtained by killing witch bosses.")), "0a9e8efb-9191-4c81-80f5-e27ca5433156", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19");
    private static final PotionEffectType[] types = new PotionEffectType[] {PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.POISON, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS};
    private static final Color[] colors = new Color[]{Color.BLACK, Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW};
    private Witch witch;
    private final BossBar bossBar = Bukkit.createBossBar(ChatColor.LIGHT_PURPLE + "Witch", BarColor.BLUE, BarStyle.SOLID);
    private int fireDamage = 7;

    public CustomEntityBossWitch() {
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
        registerDropItem(potion, 100);
        registerDropItem(LBItem.KEY_2.getItem(), 100);
        registerDropItem(head, 100);
        registerDropItem(new ItemStack(Material.QUARTZ, RandomUtils.nextInt(3) + 3), 75);
        registerDropItem(new ItemStack(Material.GLASS_BOTTLE, RandomUtils.nextInt(2) + 1), 25);
        registerTargetPriority(EntityType.PLAYER, 85);
        registerTargetPriority(EntityType.IRON_GOLEM, 80);
        registerTargetPriority(EntityType.VILLAGER, 75);
        registerTargetPriority(EntityType.ENDERMAN, 65);
        registerTargetPriority(EntityType.CREEPER, 60);
    }

    @Override
    public EntityType entityType() {
        return EntityType.WITCH;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Witch witch = (Witch) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.WITCH);
        witch.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(90.0D);
        witch.setHealth(90.0D);
        witch.setCustomName(ChatColor.LIGHT_PURPLE + "Witch");
        witch.setCustomNameVisible(true);
        witch.setSilent(true);
        this.witch = witch;
        CustomEntityHealthTag healthTag = new CustomEntityHealthTag();
        healthTag.setMode(HealthDisplayMode.THREE_HEARTS);
        healthTag.attachEntity(witch, new double[]{0.0D, 2.5D, 0.0D});
        healthTag.spawn(witch.getLocation());
        startTimers();
        return witch;
    }

    private void startTimers() {
        Scheduler.create(this::tickBossBarTimer).predicate(this::isValid).timer(1, 1);

        Scheduler.create(() -> { //throw random potions
            Location location = witch.getLocation().add(0, 2.5, 0);
            boolean lingering = RandomUtils.nextBoolean();
            ThrownPotion potion = (ThrownPotion) witch.getWorld().spawnEntity(location, lingering ? EntityType.LINGERING_POTION : EntityType.SPLASH_POTION);
            potion.setShooter(witch);
            potion.setBounce(true);
            potion.setVelocity(getRandomVelocity());
            potion.setItem(createRandomPotion(lingering));
        }).predicate(this::isValid);

        Scheduler.create(() -> witch.getWorld().strikeLightning(witch.getLocation())).predicate(this::isValid).timer(100, 100);

        Scheduler.create(() -> EffectUtils.playFixedSound(witch.getLocation(), EffectUtils.getSound("boss_witch_ambient"), 1.0F, 0.0F, 26))
                .predicate(this::isValid).timer(115, 105);

        Scheduler.create(() -> witch.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 180, 0)))
                .predicate(this::isValid).timer(250, 460);

        Scheduler.create(() -> {
            if(canTarget(witch.getTarget())) {
                Scheduler.create(() -> {
                    spawnFallingFire(1.0D, 0.0D);
                    spawnFallingFire(0.0D, 1.0D);
                    spawnFallingFire(-1.0D, 0.0D);
                    spawnFallingFire(0.0D, -1.0D);
                    spawnFallingFire(1.0D, 1.0D);
                    spawnFallingFire(-1.0D, 1.0D);
                    spawnFallingFire(1.0D, -1.0D);
                    spawnFallingFire(-1.0D, -1.0D);

                    for (int i = 10; i > 0; --i) {
                        spawnFallingFire((RandomUtils.nextInt(200) - 100) / 100D, (RandomUtils.nextInt(200) - 100) / 100D);
                    }
                }).count(15).predicate(this::isValid).timer(5, 3);
            }
        }).predicate(this::isValid).timer(120, 120);
    }

    private void spawnFallingFire(double offsetX, double offsetZ) {
        Location spawn = witch.getLocation().add(0, 1, 0);
        FallingBlock fb = spawn.getWorld().spawnFallingBlock(spawn, new MaterialData(Material.FIRE));
        fb.setVelocity(new Vector(offsetX, (RandomUtils.nextInt(30) + 10) / 100D, offsetZ));
        fb.setDropItem(false);
        double damage = fireDamage - RandomUtils.nextInt(3);
        Scheduler.create(() -> {
            for (LivingEntity livingEntity : fb.getWorld().getNearbyLivingEntities(fb.getLocation(),2, 2, 2, e -> !e.equals(witch))) {
                livingEntity.damage(damage);
                livingEntity.setFireTicks(100);
            }
        }).predicate(fb::isValid).timer(3, 3);
    }

    private boolean canTarget(LivingEntity target) {
        return target != null && (ItemStackUtils.hasEnchantment(target.getEquipment().getChestplate(), Enchantment.PROTECTION_FIRE, 0));
    }

    private Vector getRandomVelocity() {
        return new Vector((RandomUtils.nextInt(60) - 30) / 100D, 0.5, (RandomUtils.nextInt(60) - 30) / 100D);
    }

    private ItemStack createRandomPotion(boolean lingering) {
        ItemStack potion = new ItemStack(lingering ? Material.LINGERING_POTION : Material.SPLASH_POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.addCustomEffect(createRandomEffect(), true);
        potionMeta.setColor(RandomUtils.getRandomObject(colors));
        potion.setItemMeta(potionMeta);
        return potion;
    }

    private PotionEffect createRandomEffect() {
        PotionEffectType type = RandomUtils.getRandomObject(types);
        int duration = RandomUtils.nextInt(200) + 300;
        int amplifier = RandomUtils.nextInt(3);
        return new PotionEffect(type, duration, amplifier);
    }

    @Override
    public boolean isAttackingNearbyEntities() {
        return true;
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 10;
    }

    @Override
    public List<String> getCustomNames() {
        return Arrays.asList(ChatColor.LIGHT_PURPLE + "Witch", ChatColor.DARK_PURPLE + "Witch");
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public int getXPtoDrop() {
        return 7560;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.SUFFOCATION, DamageCause.LIGHTNING, DamageCause.ENTITY_EXPLOSION, DamageCause.FIRE_TICK, DamageCause.FIRE, DamageCause.LAVA, DamageCause.MAGIC, DamageCause.FALL};
    }

    @Override
    public double getDefense() {
        return 4D;
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.LIGHTNING) {
            if (this.witch.getHealth() + 5.0D < this.witch.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
                this.witch.setHealth(this.witch.getHealth() + 3.0D);
            } else {
                this.witch.setHealth(this.witch.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }
        } else {
            if (event.getCause() == DamageCause.FALLING_BLOCK) {
                event.setDamage(event.getDamage() * 8.5D);
            } else if (event.getCause() == DamageCause.PROJECTILE) {
                event.setDamage(event.getDamage() / 10.0D);
            } else if ((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && this.fireDamage < 67) {
                this.fireDamage += 2;
                MyTasks.playEffects(Particle.FLAME, this.witch.getLocation(), 34, new double[]{0.5D, 0.5D, 0.5D}, 0.0F);
            }

            if (!event.isCancelled()) {
                EffectUtils.playFixedSound(this.witch.getLocation(), EffectUtils.getSound("boss_witch_hurt"), 1.0F, 0.0F, 20);
            }
        }
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (entity instanceof LivingEntity && ItemStackUtils.equalsType(((LivingEntity) entity).getEquipment().getItemInMainHand(), Material.GOLD_SWORD)) {
            event.setDamage(event.getDamage() * 5.0D);
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        EffectUtils.playFixedSound(witch.getLocation(), EffectUtils.getSound("boss_witch_death"), 1.0F, 0.0F, 20);
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("FireDamage", this.fireDamage);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        this.witch = (Witch) this.linkedEntity;
        this.fireDamage = c.getInt("FireDamage");
        startTimers();
    }

    @Override
    public LivingEntity getBossEntity() {
        return witch;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public int getBossBarRange() {
        return 32;
    }

    @Override
    public void onEntityHitWithBeam(LivingEntity livingEntity, String tag) {}
}
