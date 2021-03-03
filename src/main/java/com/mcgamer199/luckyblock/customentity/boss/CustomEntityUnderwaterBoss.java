package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityFloatingText;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityHealthTag;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityHealthTag.HealthDisplayMode;
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
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CustomEntityUnderwaterBoss extends CustomEntity implements CustomEntityBoss {

    private static final ItemStack trophy;
    private static final ItemStack winItem;

    static {
        trophy = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.BLUE + "Underwater Boss", Arrays.asList("", ChatColor.GRAY + "Obtained by killing underwater bosses.")), "e56a8749-8a4a-40cc-9ded-3c90f8ae8c63", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM3OTc0ODJhMTRiZmNiODc3MjU3Y2IyY2ZmMWI2ZTZhOGI4NDEzMzM2ZmZiNGMyOWE2MTM5Mjc4YjQzNmIifX19");
        winItem = ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.PAPER, 1, 0, ChatColor.GREEN + "Congratulations", Arrays.asList("", ChatColor.GRAY + "You beat the underwater boss!")), LuckyBlockPlugin.enchantment_glow, 1);
    }

    private ElderGuardian elderGuardian;
    private int power = 1;
    private final BossBar bossBar = Bukkit.createBossBar("§6§lGuardian of the castle", BarColor.BLUE, BarStyle.SEGMENTED_6);
    private CustomEntityHealthTag healthTag;

    public CustomEntityUnderwaterBoss() {
        registerDropItem(LBItem.KEY_4.getItem(), 100);
        registerDropItem(trophy, 100);
        registerDropItem(winItem, 100);
        registerDropItem(ItemStackUtils.createItem(Material.PRISMARINE_SHARD, RandomUtils.nextInt(3) + 1, 0, ChatColor.DARK_AQUA + "Prismarine Shard"), 65);
        registerTargetPriority(EntityType.PLAYER, 80);
        registerTargetPriority(EntityType.IRON_GOLEM, 75);
        registerTargetPriority(EntityType.VILLAGER, 70);
        registerTargetPriority(EntityType.SNOWMAN, 60);
        registerTargetPriority(EntityType.WOLF, 60);
        registerTargetPriority(EntityType.CHICKEN, 10);
        registerTargetPriority(EntityType.COW, 10);
        registerTargetPriority(EntityType.DONKEY, 10);
        registerTargetPriority(EntityType.HORSE, 10);
        registerTargetPriority(EntityType.MULE, 10);
        registerTargetPriority(EntityType.MUSHROOM_COW, 10);
        registerTargetPriority(EntityType.OCELOT, 10);
        registerTargetPriority(EntityType.PARROT, 10);
        registerTargetPriority(EntityType.PIG, 10);
        registerTargetPriority(EntityType.RABBIT, 10);
        registerTargetPriority(EntityType.SHEEP, 10);
    }

    @Override
    public EntityType entityType() {
        return EntityType.ELDER_GUARDIAN;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ElderGuardian elder = (ElderGuardian) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        elder.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(325.0D);
        elder.setHealth(325.0D);
        elder.setCustomName("§6§lGuardian of the castle");
        elder.setCustomNameVisible(true);
        this.elderGuardian = elder;
        CustomEntityHealthTag healthTag = new CustomEntityHealthTag();
        healthTag.attachEntity(elder, new double[]{0.0D, 2.5D, 0.0D});
        healthTag.setMode(HealthDisplayMode.CUSTOM_HEARTS);
        healthTag.setHeartsAmount(5);
        healthTag.spawn(elder.getLocation());
        this.healthTag = healthTag;
        startTimers();
        return elder;
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public double getDefense() {
        return  9.5D;
    }

    @Override
    public LivingEntity getBossEntity() {
        return elderGuardian;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public int getBossBarRange() {
        return 26;
    }

    @Override
    public void onEntityHitWithBeam(LivingEntity livingEntity, String tag) {}

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.LIGHTNING, DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA};
    }

    @Override
    public void onChunkLoad() {
        this.elderGuardian = (ElderGuardian) this.linkedEntity;
        startTimers();
    }

    @Override
    public void onKilledByPlayer(EntityDamageByEntityEvent event, Player player) {
        CustomEntityFloatingText floatingText = new CustomEntityFloatingText();
        floatingText.setAge(55);
        floatingText.setShiftCount(160);
        floatingText.setText(String.format("§e%s §abeat the castle guardian", player.getName()));
        floatingText.spawn(linkedEntity.getLocation());
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (this.elderGuardian.getHealth() - event.getFinalDamage() < 101.0D && this.power < 4) {
            this.power += 4;
        }
    }

    @Override
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        if (player.getInventory().getItemInMainHand() != null) {
            Material mat = player.getInventory().getItemInMainHand().getType();
            if (mat != Material.WOOD_AXE && mat != Material.STONE_AXE && mat != Material.IRON_AXE && mat != Material.GOLD_AXE && mat != Material.DIAMOND_AXE) {
                event.setCancelled(true);
            }
        }

        if (RandomUtils.nextPercent(70)) {
            player.getWorld().strikeLightning(player.getLocation());
        }
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Arrow) {
            event.setDamage(event.getDamage() / 3.0D);
            if (RandomUtils.nextPercent(93)) {
                Arrow arrow = (Arrow) damager;
                ProjectileSource shooter = arrow.getShooter();
                if(shooter instanceof LivingEntity) {
                    Scheduler.create(() -> {
                        shootBeam((LivingEntity) shooter, 220, 0.32D);
                    }).count(RandomUtils.nextInt(5) + 3).predicate(this::isValid).timer(5, 30);
                }
            }
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        CustomEntityManager.removeCustomEntity(healthTag);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        this.elderGuardian = (ElderGuardian) this.linkedEntity;
        this.power = c.getInt("Power");
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("Power", this.power);
    }

    private void startTimers() {
        Scheduler.create(this::tickBossBarTimer).predicate(this::isValid).timer(1, 1);

        Scheduler.create(() -> {
            int x = 0;
            for (LivingEntity possibleTarget : elderGuardian.getWorld().getNearbyLivingEntities(elderGuardian.getLocation(), 15, 15, 15)) {
                if(canTarget(possibleTarget)) {
                    shootBeam(possibleTarget, power < 5 ? 72 : 95, power < 5 ? 0.3 : 0.45);
                    if(x++ > 6) {
                        break;
                    }
                }
            }
        });

        Scheduler.create(() -> {
            if (countMinions() < 6) {
                CustomEntityUnderwaterMinion minion = new CustomEntityUnderwaterMinion();
                minion.setAge(280);
                minion.setTarget(elderGuardian.getTarget());
                minion.spawn(elderGuardian.getLocation());
            }
        }).predicate(this::isValid).timer(160, 160);

        Scheduler.create(() -> {
            for (Squid squid : elderGuardian.getWorld().getNearbyEntitiesByType(Squid.class, elderGuardian.getLocation(), 7, 7, 7)) {
                squid.damage(60);
                EffectUtils.playEffects(Particle.FLAME, squid.getLocation(), 20, new double[]{0.5D, 0.5D, 0.5D}, 0.1F);
                if (power < 20 && RandomUtils.nextPercent(58)) {
                    power++;
                }
            }
        }).predicate(this::isValid).timer(70, 70);

        Scheduler.create(() -> {
            for (Player player : elderGuardian.getWorld().getNearbyEntitiesByType(Player.class, elderGuardian.getLocation(), 7, 7, 7)) {
                if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 1);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 260, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 1));
                }
            }
        }).predicate(this::isValid).timer(256, 256);

        Scheduler.create(() -> {
            if (elderGuardian.getTarget() != null) {
                TNTPrimed tnt = (TNTPrimed) elderGuardian.getWorld().spawnEntity(elderGuardian.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(2000);
                tnt.setVelocity(getRandomVelocity());
                tnt.setCustomName("" + ChatColor.RED + ChatColor.BOLD + "5s");
                tnt.setCustomNameVisible(true);
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

                            Scheduler.cancelTask(this);
                        }
                    }
                }, 20, 20);
            }
        }).predicate(this::isValid).timer(340, 340);

        Scheduler.create(() -> elderGuardian.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0)))
                .predicate(this::isValid).timer(250, 440);
    }

    private Vector getRandomVelocity() {
        return new Vector((RandomUtils.nextInt(80) - 40) / 50D, 0.3D, (RandomUtils.nextInt(80) - 40) / 50D);
    }

    private int countMinions() {
        int total = 0;

        for (Entity e : this.elderGuardian.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
            CustomEntity customEntity = CustomEntityManager.getCustomEntity(e.getUniqueId());
            if(customEntity instanceof CustomEntityUnderwaterMinion) {
                if(++total > 5) {
                    break;
                }
            }
        }

        return total;
    }

    private boolean canTarget(Entity entity) {
        if(entity.getType().equals(EntityType.PLAYER)) {
            Player player = (Player) entity;
            return !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR);
        }

        return getTargetPriorities().containsKey(entity.getType());
    }

    private void shootBeam(LivingEntity target, int maxDistance, double power) {
        if (this.elderGuardian.getLocation().getBlock().getType() == Material.WATER || this.elderGuardian.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            EffectUtils.playEffects(Particle.WATER_BUBBLE, this.elderGuardian.getLocation().add(0.0D, 1.0D, 0.0D), 75, new double[]{1.0D, 1.5D, 1.0D}, 1.0F);
        }

        EffectUtils.shootBeam(this, this.elderGuardian, target, maxDistance, power, new EffectUtils.ParticleHelper(Particle.CRIT, 1, new double[]{0.0D, 0.0D, 0.0D}, 0.0F));
    }
}
