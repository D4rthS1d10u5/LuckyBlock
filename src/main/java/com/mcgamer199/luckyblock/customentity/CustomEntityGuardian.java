package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.PluginConstants;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.listeners.LuckyBlockWorld;
import com.mcgamer199.luckyblock.structures.Structure;
import com.mcgamer199.luckyblock.structures.Treasure;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomEntityGuardian extends CustomEntity {

    @Getter @Setter
    private LBType luckyBlockType = LBType.getRandomType();
    private final List<UUID> luckyBlockArmy = new ArrayList<>();

    public CustomEntityGuardian() {
        registerDropItem(new ItemStack(Material.GOLD_INGOT, (RandomUtils.nextInt(3) + 3) * 3), 80);
        registerDropItem(new ItemStack(Material.DIAMOND, (RandomUtils.nextInt(4) + 3) * 2), 60);
        registerDropItem(new ItemStack(Material.IRON_INGOT, (RandomUtils.nextInt(8) + 6) * 3), 90);
    }

    @Override
    public EntityType entityType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        WitherSkeleton skeleton = (WitherSkeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        skeleton.setCustomNameVisible(true);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(460.0D);
        skeleton.setHealth(460.0D);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 1));
        Map<Enchantment, Integer> m = new HashMap<>();
        m.put(LuckyBlockPlugin.enchantment_lightning, 10);
        m.put(Enchantment.DAMAGE_ALL, 4);
        ItemStack item = ItemStackUtils.createItem(Material.DIAMOND_AXE, 1, (short) 0, null, null, m);
        skeleton.getEquipment().setItemInMainHand(item);
        skeleton.getEquipment().setHelmet(this.luckyBlockType.toItemStack());
        ItemStack item1 = new ItemStack(Material.GOLD_CHESTPLATE);
        skeleton.getEquipment().setChestplate(item1);
        item1.setType(Material.GOLD_LEGGINGS);
        skeleton.getEquipment().setLeggings(item1);
        item1.setType(Material.GOLD_BOOTS);
        skeleton.getEquipment().setBoots(item1);

        if(LuckyBlockWorld.equals(skeleton.getWorld().getGenerator())) {
            Treasure t = Structure.getRandomTreasure();
            List<String> list;
            if (t != null) {
                list = Arrays.asList(ChatColor.BLUE + "Location: " + t.getLocation().getBlockX() + "," + t.getLocation().getBlockY() + "," + t.getLocation().getBlockZ(), ChatColor.RED + "<<Right click to break the top bedrock block>>");
            } else {
                list = Collections.singletonList(ChatColor.RED + "No treasure found!");
            }

            ItemStack map = ItemStackUtils.createItem(Material.MAP, 1, 0, "" + ChatColor.GREEN + ChatColor.ITALIC + "Treasure Map", list);
            registerDropItem(map, 100);
        }

        startTimers();
        return skeleton;
    }

    @Override
    public int getCustomNamesTickDelay() {
        return 1;
    }

    @Override
    public List<String> getCustomNames() {
        LivingEntity living = (LivingEntity) linkedEntity;
        return Collections.singletonList(ChatColor.YELLOW + "Guardian " + ChatColor.GREEN + (int) living.getHealth() + ChatColor.WHITE + "/" + ChatColor.GREEN + living.getMaxHealth());
    }

    @Override
    public int getTickTime() {
        return -1;
    }

    @Override
    public int getXPtoDrop() {
        return 1600;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.DROWNING, DamageCause.FALLING_BLOCK, DamageCause.POISON, DamageCause.THORNS, DamageCause.WITHER, DamageCause.FALL, DamageCause.PROJECTILE};
    }

    @Override
    public void onTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (target != null && !luckyBlockArmy.isEmpty()) {
            for (UUID uuid : luckyBlockArmy) {
                if(target.getUniqueId().equals(uuid)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        EntityEquipment equipment = ((Wither) linkedEntity).getEquipment();
        if(RandomUtils.nextPercent(90) && LBType.fromMaterial(equipment.getHelmet().getType()) != null) {
            linkedEntity.getWorld().dropItem(linkedEntity.getLocation(), luckyBlockType.toItemStack(RandomUtils.nextInt(-50, 50)));
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        for (Entity e : linkedEntity.getNearbyEntities(5.0D, 5.0D, 5.0D)) {
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).damage(25.0D);
            }
        }
    }

    public void onSave(ConfigurationSection section) {
        section.set("Type", this.luckyBlockType.getId());
    }

    public void onLoad(ConfigurationSection section) {
        if (section.getInt("Types") > 0) {
            this.luckyBlockType = LBType.fromId(section.getInt("Type"));
        }

        if (this.luckyBlockType != null) {
            this.startTimers();
        }
    }

    public void startTimers() {
        Scheduler.create(() -> { //vampirize effect
            int total = 0;
            for (Entity entity : linkedEntity.getNearbyEntities(10.0D, 5.0D, 10.0D)) {
                if(entity instanceof Player) {
                    Player player = (Player) entity;
                    if ((player.getGameMode() == GameMode.SURVIVAL) || (player.getGameMode() == GameMode.ADVENTURE)) {
                        entity.setFallDistance(LuckyBlockWorld.equals(linkedEntity.getWorld().getGenerator()) ? 80F :  10F);
                        total += 2;
                    }
                } else if(!(entity instanceof Monster) && entity instanceof LivingEntity) {
                    entity.setFallDistance(10.0F);
                    total += 2;
                }
            }

            if (total > 0) {
                Wither wither = (Wither) linkedEntity;
                wither.setHealth((wither.getHealth() + total) < wither.getMaxHealth() ? wither.getHealth() + total : wither.getMaxHealth());
                linkedEntity.getWorld().spawnParticle(Particle.HEART, linkedEntity.getLocation(), total / 2, 0.4D, 0.4D, 0.4D, 0.0D);
            }
        }).predicate(() -> PluginConstants.CUSTOM_ENTITY_VALID.test(this)).timer(10, 60);

        Scheduler.create(() -> { //army spawner
            LivingEntity target = ((Wither) linkedEntity).getTarget();
            if(target != null) {
                int livingEntityCount = 0;
                for (Entity e : linkedEntity.getNearbyEntities(10.0D, 10.0D, 10.0D)) {
                    if (e instanceof LivingEntity) {
                        ++livingEntityCount;
                    }
                }

                if (livingEntityCount < 15) {
                    CustomEntitySuperWitherSkeleton skeleton = new CustomEntitySuperWitherSkeleton();
                    skeleton.setOwner(entityUuid);
                    skeleton.spawn(linkedEntity.getLocation());
                    ((Creature) skeleton.getLinkedEntity()).setTarget(target);
                    luckyBlockArmy.add(skeleton.getEntityUuid());
                }
            }
        }).predicate(() -> PluginConstants.CUSTOM_ENTITY_VALID.test(this)).timer(100, 150);
    }
}
