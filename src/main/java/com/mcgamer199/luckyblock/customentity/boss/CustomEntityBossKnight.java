package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityBoss;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.resources.SkullData;
import com.mcgamer199.luckyblock.util.*;
import lombok.Getter;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomEntityBossKnight extends CustomEntity implements CustomEntityBoss {

    static final ItemStack spellFortune;
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
        spellFortune = ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.GHAST_TEAR, 1, 0, "" + ChatColor.GRAY + ChatColor.BOLD + "Spell of fortune", Arrays.asList("", ChatColor.GREEN + "+1000 luck")), LuckyBlockPlugin.enchantment_glow, 1);
        head1 = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.GREEN + "Knight", Arrays.asList("", ChatColor.GRAY + "Obtained by killing lb bosses.", ChatColor.GREEN + "+2500 Luck")), "c86041e4-5b4e-4e8a-928c-9028b2437de6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ0NzcyZGM0ZGVmMjIyMTllZTZkODg5Y2NkYzJmOTIzMmVlMjNkMzU2ZGQ5ZTRhZGNlYTVmNzJjYzBjNjg5In19fQ==");
        bow = ItemStackUtils.addEnchants(new ItemStack(Material.BOW, 1), new int[]{10, 1}, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE);
        sword = ItemStackUtils.addEnchants(new ItemStack(Material.IRON_SWORD), new int[]{5}, Enchantment.FIRE_ASPECT);
        sword1 = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{10, 10}, Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT);
        chestplates = new ItemStack[]{ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLUE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.RED), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.LIME), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.ORANGE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.PURPLE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.YELLOW), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.SILVER)};
        leggings = new ItemStack[]{ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.BLUE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.RED), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.LIME), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.ORANGE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.PURPLE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.YELLOW), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.SILVER)};
        boots = new ItemStack[]{ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.BLUE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.RED), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.LIME), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.ORANGE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.PURPLE), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.YELLOW), ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.SILVER)};
        chestplate1 = ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK);
        leggings1 = ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK);
        boots1 = ItemStackUtils.setLeatherArmorColor(new ItemStack(Material.LEATHER_BOOTS), Color.BLACK);

        Scheduler.timerAsync(() -> { //эффект стрел босса "Рыцарь"
            for (World world : Bukkit.getWorlds()) {
                world.getEntitiesByClass(Arrow.class).stream().filter(arrow -> EntityUtils.getOrDefault(arrow, "knight_arrow", false))
                        .forEach(arrow -> {
                            if(arrow.isValid() && !arrow.isOnGround()) {
                                arrow.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, arrow.getLocation(), 3, 0.1D, 0.1D, 0.1D, 0.0D);
                            } else {
                                arrow.remove();
                            }
                        });
            }
        }, 10, 10);
    }

    private WitherSkeleton witherSkeleton;
    @Getter
    private UUID blazeUuid;
    @Getter
    private boolean angry;
    @Getter
    private KnightStatus status;
    private final BossBar bossBar = Bukkit.createBossBar(ChatColor.DARK_PURPLE + "LBBoss", BarColor.BLUE, BarStyle.SOLID);

    public CustomEntityBossKnight() {
        registerTargetPriority(EntityType.PLAYER, 20);
        registerTargetPriority(EntityType.VILLAGER, 10);
        registerTargetPriority(EntityType.IRON_GOLEM, 10);
        registerTargetPriority(EntityType.SNOWBALL, 10);
        registerTargetPriority(EntityType.HORSE, 10);
        registerDropItem(spellFortune, 100);
        registerDropItem(LBItem.KEY_1.getItem(), 100);
        registerDropItem(new ItemStack(Material.SHIELD), 25);
    }

    @Override
    public EntityType entityType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        SkullData sd = SkullData.getRandomSkullData("BOSS");
        ItemStack head = ItemStackUtils.createSkull(ItemStackUtils.createItem(Material.SKULL_ITEM, 1, 3, ChatColor.GOLD + "Trophy: " + ChatColor.GREEN + "Knight", Arrays.asList("", ChatColor.GRAY + "Obtained by killing lb bosses.", ChatColor.GREEN + "+2500 Luck")), sd.getId(), sd.getData());
        registerDropItem(head, 100);
        WitherSkeleton skeleton = (WitherSkeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.WITHER_SKELETON);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50.0D);
        skeleton.setHealth(50.0D);
        skeleton.setSilent(true);
        skeleton.setCustomName(ChatColor.DARK_PURPLE + "Knight");
        skeleton.setCustomNameVisible(true);
        skeleton.getEquipment().setHelmet(head);
        skeleton.getEquipment().setItemInMainHand(bow);
        int armorIndex = RandomUtils.nextInt(chestplates.length);
        skeleton.getEquipment().setChestplate(chestplates[armorIndex]);
        skeleton.getEquipment().setLeggings(leggings[armorIndex]);
        skeleton.getEquipment().setBoots(boots[armorIndex]);
        skeleton.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
        skeleton.setRemoveWhenFarAway(false);
        this.witherSkeleton = skeleton;
        CustomEntityBlazeMinion minion = new CustomEntityBlazeMinion();
        minion.spawn(spawnLocation);
        this.blazeUuid = minion.getEntityUuid();
        startTimers();
        return skeleton;
    }

    @Override
    public void onTick() {
        LivingEntity target = witherSkeleton.getTarget();
        if(target != null) {
            EntityEquipment equipment = witherSkeleton.getEquipment();
            if(witherSkeleton.getLocation().distance(target.getLocation()) > 6D) {
                if(!ItemStackUtils.equalsType(equipment.getItemInMainHand(), Material.BOW)) {
                    this.status = KnightStatus.FIGHTING_WITH_BOW;
                    equipment.setItemInMainHand(bow);
                }

                if(RandomUtils.nextPercent(90)) {
                    this.status = RandomUtils.nextBoolean() ? KnightStatus.SPAWNING_BOMB : KnightStatus.SHOOTING_FIRE;
                    switch (status) {
                        case SPAWNING_BOMB: {
                            FallingBlock fallingBlock = witherSkeleton.getWorld().spawnFallingBlock(target.getLocation().add(0.0D, 6.0D, 0.0D), new MaterialData(Material.COAL_BLOCK));
                            EntityUtils.setMetadata(fallingBlock, "knight_bomb", true);
                            EntityUtils.setMetadata(fallingBlock, "angry", angry);

                            fallingBlock.setCustomName(ChatColor.RED + "Bomb");
                            fallingBlock.setCustomNameVisible(true);
                            break;
                        }
                        case SHOOTING_FIRE: {
                            Arrow arrow = witherSkeleton.launchProjectile(Arrow.class);
                            witherSkeleton.getWorld().spawnFallingBlock(witherSkeleton.getLocation(), new MaterialData(Material.FIRE)).setVelocity(arrow.getVelocity());
                            arrow.remove();
                        }
                    }
                }
            } else if(!ItemStackUtils.equalsType(equipment.getItemInMainHand(), Material.IRON_SWORD)) {
                this.status = KnightStatus.FIGHTING_WITH_SWORD;
                equipment.setItemInMainHand(angry ? sword1 : sword);
            }
        } else {
            this.status = KnightStatus.RESTING;
            CustomEntity customEntity = CustomEntityManager.getCustomEntity(blazeUuid);
            if (customEntity != null) {
                if (witherSkeleton.getLocation().distance(customEntity.getLinkedEntity().getLocation()) > 5.0D) {
                    EntityUtils.followEntity(customEntity.getLinkedEntity().getLocation(), witherSkeleton, 1.2D);
                }

                if (witherSkeleton.getLocation().distance(customEntity.getLinkedEntity().getLocation()) > 15.0D) {
                    witherSkeleton.teleport(customEntity.getLinkedEntity());
                }

                if (witherSkeleton.getLocation().distance(customEntity.getLinkedEntity().getLocation()) < 3.0D && witherSkeleton.getHealth() < witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
                    EffectUtils.playFixedSound(witherSkeleton.getLocation(), EffectUtils.getSound("boss_lb_heal"), 1.0F, 0.0F, 10);
                    witherSkeleton.getWorld().spawnParticle(Particle.HEART, witherSkeleton.getLocation(), 10, 0.5D, 0.5D, 0.5D, 1.0D);
                    witherSkeleton.setHealth(Math.min(witherSkeleton.getHealth() + 1.0D, witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
                }
            }
        }
    }

    @Override
    public int getTickTime() {
        return 40;
    }

    @Override
    public LivingEntity getBossEntity() {
        return witherSkeleton;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public int getBossBarRange() {
        return 16;
    }

    @Override
    public int getXPtoDrop() {
        return 17500;
    }

    @Override
    public double getAttackDamage() {
        return 25D;
    }

    @Override
    public double getDefense() {
        return 82D;
    }

    @Override
    public boolean isAttackingNearbyEntities() {
        return true;
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 5;
    }

    @Override
    public List<String> getCustomNames() {
        return Collections.singletonList(String.format("§5Knight §eHealth §a%d§f%%", (int) (witherSkeleton.getHealth() / 50.0D * 100.0D)));
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.CONTACT, DamageCause.DROWNING, DamageCause.FALL, DamageCause.LIGHTNING, DamageCause.PROJECTILE, DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION};
    }

    @Override
    public void onEntityHitWithBeam(LivingEntity livingEntity, String tag) {}

    @Override
    public void onDeath(EntityDeathEvent event) {
        EffectUtils.playFixedSound(this.witherSkeleton.getLocation(), EffectUtils.getSound("boss_lb_death"), 1.0F, 0.0F, 10);
        CustomEntityBlazeMinion blazeMinion = CustomEntityManager.getCustomEntity(blazeUuid);
        if(blazeMinion != null && blazeMinion.isValid()) {
            blazeMinion.startAttacking();
        }
    }

    @Override
    public void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * (angry ? 5D : 3D));
    }

    @Override
    public void onShootBow(final EntityShootBowEvent event) {
        if (event.getProjectile() != null && event.getProjectile() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getProjectile();
            EntityUtils.setMetadata(projectile, "knight_arrow", true);
            Vector sourceVelocity = projectile.getVelocity();
            Scheduler.create(() -> {
                Arrow arrow = event.getEntity().launchProjectile(Arrow.class);
                EntityUtils.setMetadata(arrow, "knight_arrow", true);
                arrow.setShooter(witherSkeleton);
                arrow.setVelocity(new Vector(sourceVelocity.getX() + (RandomUtils.nextInt(20) - 10) / 70D, sourceVelocity.getY() + (RandomUtils.nextInt(20) - 10) / 70D, sourceVelocity.getZ() + (RandomUtils.nextInt(20) - 10) / 70D));
                arrow.setBounce(true);
                if (angry) {
                    arrow.setFireTicks(100);
                    arrow.setCritical(true);
                }
            }).count(angry ? RandomUtils.nextInt(5) + 30 : RandomUtils.nextInt(18) + 12).predicate(this::isValid).timer(2, 2);
        }
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        EffectUtils.playFixedSound(witherSkeleton.getLocation(), EffectUtils.getSound("boss_lb_hurt"), 1.0F, 0.0F, 10);
        if ((witherSkeleton.getHealth() / 50D) < 0.4D && !this.angry) {
            this.makeAngry();
        }
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("blaze", this.blazeUuid.toString());
        c.set("angry", this.angry);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        if (c.getString("blaze") != null) {
            this.blazeUuid = UUID.fromString(c.getString("blaze"));
        }

        this.angry = c.getBoolean("angry");
    }

    @Override
    public void onChunkLoad() {
        this.witherSkeleton = (WitherSkeleton) linkedEntity;
        startTimers();
    }

    private void startTimers() {
        Scheduler.create(this::tickBossBarTimer).predicate(this::isValid).timer(1, 1);

        Scheduler.create(() -> EffectUtils.playFixedSound(witherSkeleton.getLocation(), EffectUtils.getSound("boss_lb_ambient"), 1.0F, 0.0F, 35))
                .predicate(this::isValid).timer(150, 150);

        Scheduler.create(() -> {
            if (angry) {
                for (Animals animal : witherSkeleton.getWorld().getNearbyEntitiesByType(Animals.class, witherSkeleton.getLocation(), 7, 7, 7)) {
                    animal.getWorld().strikeLightning(animal.getLocation());
                }
            }

            if (witherSkeleton.getTarget() == null) {
                Block b = findNearbyCraftingTable(4);
                if (b != null) {
                    EntityUtils.followEntity(b.getLocation(), witherSkeleton, 1.1D);
                }
            }
        }).predicate(this::isValid).timer(60, 60);

        Scheduler.create(() -> {
            LivingEntity target = witherSkeleton.getTarget();
            if (angry && target != null) {
                Zombie zombie = (Zombie) witherSkeleton.getWorld().spawnEntity(witherSkeleton.getLocation().add(RandomUtils.nextInt(10) - 5, 5.0D, RandomUtils.nextInt(10) - 5), EntityType.ZOMBIE);
                zombie.setTarget(target);
                Scheduler.later(zombie::remove, 138);
            }
        }).predicate(this::isValid).timer(150, 150);

        Scheduler.create(() -> {
            LuckyCraftingTable craftingTable = LuckyCraftingTable.getByBlock(witherSkeleton.getLocation().getBlock().getRelative(BlockFace.DOWN));
            if (craftingTable != null) {
                craftingTable.remove();
                craftingTable.getBlock().setType(Material.AIR);
            }
        }).predicate(this::isValid).timer(20, 20);
    }

    @SuppressWarnings("SameParameterValue")
    private Block findNearbyCraftingTable(int range) {
        for (int x = range * -1; x < range + 1; ++x) {
            for (int y = range * -1; y < range + 1; ++y) {
                for (int z = range * -1; z < range + 1; ++z) {
                    Block b = witherSkeleton.getLocation().add(x, y, z).getBlock();
                    if (LuckyCraftingTable.getByBlock(b) != null) {
                        return b;
                    }
                }
            }
        }

        return null;
    }

    private void makeAngry() {
        this.talk(ChatColor.RED + "How dare you!", 10);
        this.angry = true;
        EntityEquipment equipment = witherSkeleton.getEquipment();
        if (ItemStackUtils.equalsType(equipment.getItemInMainHand(), Material.IRON_SWORD)) {
            equipment.setItemInMainHand(sword1);
        }

        equipment.setHelmet(head1);
        equipment.setChestplate(chestplate1);
        equipment.setLeggings(leggings1);
        equipment.setBoots(boots1);
        witherSkeleton.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0D);
        witherSkeleton.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
        witherSkeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @SuppressWarnings("SameParameterValue")
    private void talk(String msg, int d) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (witherSkeleton.getLocation().distance(player.getLocation()) < (double) (d + 1)) {
                player.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + "LBBoss" + ChatColor.GOLD + ChatColor.BOLD + "]" + ChatColor.RESET + ": " + msg);
            }
        }
    }

    public enum KnightStatus {
        FIGHTING_WITH_BOW,
        FIGHTING_WITH_SWORD,
        SHOOTING_FIRE,
        SPAWNING_BOMB,
        RESTING
    }
}
