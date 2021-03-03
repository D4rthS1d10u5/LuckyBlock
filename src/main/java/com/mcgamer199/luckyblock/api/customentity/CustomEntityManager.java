package com.mcgamer199.luckyblock.api.customentity;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.api.CountingMap;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.customentity.*;
import com.mcgamer199.luckyblock.customentity.boss.*;
import com.mcgamer199.luckyblock.customentity.lct.CustomEntityLCTItem;
import com.mcgamer199.luckyblock.customentity.lct.CustomEntityLCTNameTag;
import com.mcgamer199.luckyblock.customentity.nametag.*;
import com.mcgamer199.luckyblock.util.*;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@UtilityClass
@Log
public class CustomEntityManager {

    private static final File customEntitiesFile = new File(LuckyBlockPlugin.instance.getDataFolder(), "CustomEntities.yml");
    private static final YamlConfiguration customEntitiesConfig = YamlConfiguration.loadConfiguration(customEntitiesFile);

    private static final CountingMap<UUID, CustomEntity> customEntities = new CountingMap<>(new ConcurrentHashMap<>(), 250, (entries, forced) -> {
        customEntitiesConfig.set("CustomEntities", null);
        log.info(String.format("Trying to save %d entities. Forced %s", entries.size(), forced));

        entries.forEach(entry -> {
            CustomEntity entity = entry.getValue();
            ConfigurationSection section = customEntitiesConfig.createSection(String.format("CustomEntities.Entity%s", entry.getKey()));
            section.set("Class", entity.getClass().getName());
            section.set("UUID", entry.getKey().toString());
            entity.onSave(section);
        });

        try {
            customEntitiesConfig.save(customEntitiesFile);
        } catch (IOException e) {
            log.warning(String.format("Ошибка записи конфигурации в файл %s", customEntitiesFile.getAbsolutePath()));
            e.printStackTrace();
        }
    });
    private static final Map<Class<? extends CustomEntity>, Supplier<? extends CustomEntity>> factories = new HashMap<>();

    public static void setCountingChanges(boolean countingChanges) {
        customEntities.setCountChanges(countingChanges);
    }

    public static void saveAll() {
        customEntities.processReachedLimit(true);
    }

    public static void initDefaults() {
        registerCustomEntityFactory(CustomEntityElementalCreeper.class, CustomEntityElementalCreeper::new);
        registerCustomEntityFactory(CustomEntityGuardian.class, CustomEntityGuardian::new);
        registerCustomEntityFactory(CustomEntityKillerSkeleton.class, CustomEntityKillerSkeleton::new);
        registerCustomEntityFactory(CustomEntitySuperWitherSkeleton.class, CustomEntitySuperWitherSkeleton::new);
        registerCustomEntityFactory(CustomEntityLuckyVillager.class, CustomEntityLuckyVillager::new);
        registerCustomEntityFactory(CustomEntityRandomItem.class, CustomEntityRandomItem::new);
        registerCustomEntityFactory(CustomEntityLCTItem.class, CustomEntityLCTItem::new);
        registerCustomEntityFactory(CustomEntityLCTNameTag.class, CustomEntityLCTNameTag::new);
        registerCustomEntityFactory(CustomEntitySoldier.class, CustomEntitySoldier::new);
        registerCustomEntityFactory(CustomEntityTalkingZombie.class, CustomEntityTalkingZombie::new);
        registerCustomEntityFactory(CustomEntitySuperSlime.class, CustomEntitySuperSlime::new);
        registerCustomEntityFactory(CustomEntityFloatingText.class, CustomEntityFloatingText::new);
        registerCustomEntityFactory(CustomEntityHealerTag.class, CustomEntityHealerTag::new);
        registerCustomEntityFactory(CustomEntityHealthTag.class, CustomEntityHealthTag::new);
        registerCustomEntityFactory(CustomEntityLuckyBlockNameTag.class, CustomEntityLuckyBlockNameTag::new);
        registerCustomEntityFactory(CustomEntityTrophyNameTag.class, CustomEntityTrophyNameTag::new);
        registerCustomEntityFactory(CustomEntityHealer.class, CustomEntityHealer::new);
        registerCustomEntityFactory(CustomEntityBlazeMinion.class, CustomEntityBlazeMinion::new);
        registerCustomEntityFactory(CustomEntityBossKnight.class, CustomEntityBossKnight::new);
        registerCustomEntityFactory(CustomEntityBossWitch.class, CustomEntityBossWitch::new);
        registerCustomEntityFactory(CustomEntityMC.class, CustomEntityMC::new);
        registerCustomEntityFactory(CustomEntityUnderwaterBoss.class, CustomEntityUnderwaterBoss::new);
        registerCustomEntityFactory(CustomEntityUnderwaterMinion.class, CustomEntityUnderwaterMinion::new);
        registerCustomEntityFactory(CustomEntityBaseballPlayer.class, CustomEntityBaseballPlayer::new);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static void loadEntities() {
        ConfigurationSection customEntities = customEntitiesConfig.getConfigurationSection("CustomEntities");
        setCountingChanges(false);
        if(customEntities != null) {
            for (String key : customEntities.getKeys(false)) {
                ConfigurationSection entitySection = customEntities.getConfigurationSection(key);
                String className = entitySection.getString("Class");
                String entityUUID = entitySection.getString("UUID");
                if(className != null && entityUUID != null) {
                    className = replaceOldClassNames(className);
                    try {
                        Class<?> entityClass = Class.forName(className);
                        if(!CustomEntity.class.isAssignableFrom(entityClass)) {
                            log.info(String.format("Класс %s не является CustomEntity", className));
                            return;
                        }

                        CustomEntity customEntity = createCustomEntity((Class<? extends CustomEntity>) entityClass);
                        if(customEntity == null) {
                            log.info(String.format("Не удалось загрузить сущность по имени класса %s", className));
                            return;
                        }

                        UUID uuid = UUID.fromString(entityUUID);
                        customEntity.setEntityUuid(uuid);
                        Entity linked = null;
                        for (World world : Bukkit.getWorlds()) {
                            for (Entity entity : world.getEntities()) {
                                if(entity.getUniqueId().equals(uuid)) {
                                    linked = entity;
                                    break;
                                }
                            }
                        }

                        customEntity.onLoad(entitySection);
                        if(linked != null) {
                            customEntity.init(linked);
                            customEntity.startBasicEntityTimers();
                        } else {
                            addCustomEntity(uuid, customEntity); //при загрузке чанков моб потом будет загружен
                        }
                    } catch (ClassNotFoundException | IllegalArgumentException e) {
                        log.severe(String.format("Ошибка загрузки сущности %s, UUID %s", className, entityUUID));
                        e.printStackTrace();
                    }
                }
            }
        }

        setCountingChanges(true);
        startBasicTimers();
    }

    public static void addCustomEntity(@NotNull UUID uuid, @NotNull CustomEntity customEntity) {
        customEntities.put(uuid, customEntity);
    }

    public static void removeCustomEntity(@NotNull CustomEntity customEntity) {
        customEntities.remove(customEntity.getEntityUuid());
        customEntity.stopTimers();
        if(customEntity instanceof CustomEntityBoss) {
            ((CustomEntityBoss) customEntity).getBossBar().removeAll();
            ((CustomEntityBoss) customEntity).getBossBar().setVisible(false);
        }
        if(customEntity.getLinkedEntity() != null) {
            customEntity.getLinkedEntity().remove();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomEntity> T getCustomEntity(UUID entityUuid) {
        return (T) customEntities.get(entityUuid);
    }

    public static <T extends CustomEntity> void registerCustomEntityFactory(Class<T> entityClass, Supplier<T> factoryFunction) {
        factories.put(entityClass, factoryFunction);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends CustomEntity> T createCustomEntity(String className) {
        if(className == null) {
            return null;
        }

        try {
            className = replaceOldClassNames(className);
            Class<?> entityClass = Class.forName(className);
            if(!CustomEntity.class.isAssignableFrom(entityClass)) {
                log.info(String.format("Класс %s не является CustomEntity", className));
                return null;
            }

            return createCustomEntity((Class<T>) entityClass);
        } catch (ClassNotFoundException e) {
            log.info(String.format("Не удается создать сущность с классом %s", className));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends CustomEntity> T createCustomEntity(@NotNull Class<T> targetType) {
        Supplier<T> factory = (Supplier<T>) factories.get(targetType);
        return factory == null ? null : factory.get();
    }

    public static ItemStack createSpawnEgg(@NotNull CustomEntity customEntity) {
        String className = customEntity.getClass().getName();
        ItemStack result = ItemStackUtils.createItem(Material.MONSTER_EGG, 1, 0, "§aSpawn Entity", Arrays.asList("", "§6Entity: §7" + ArrayUtils.getLast(className.split("\\.")), "§7Right click to spawn"));
        SpawnEggMeta meta = ((SpawnEggMeta) result.getItemMeta());
        meta.setSpawnedType(customEntity.entityType());
        result.setItemMeta(meta);
        NBTCompoundWrapper<?> itemTag = ItemStackUtils.getItemTag(result);
        itemTag.setString("EntityClass", className);
        return ItemStackUtils.setItemTag(result, itemTag);
    }

    public static Collection<Class<? extends CustomEntity>> entityClasses() {
        return Collections.unmodifiableCollection(factories.keySet());
    }

    private static void startBasicTimers() {
        Scheduler.timerAsync(() -> customEntities.values().stream().filter(customEntity -> customEntity instanceof CustomEntityBoss).map(CustomEntityBoss.class::cast).forEach(customEntity -> {
            if(customEntity.hasBossBar() && customEntity.getBossBarRange() > 0 && customEntity.getBossBarRange() < 225) {
                LivingEntity boss = customEntity.getBossEntity();
                for (Player player : boss.getWorld().getPlayers()) {
                    int distance = (int) boss.getLocation().distance(player.getLocation());
                    if(distance <= customEntity.getBossBarRange()) {
                        customEntity.getBossBar().addPlayer(player);
                    } else {
                        customEntity.getBossBar().removePlayer(player);
                    }
                }
            }
        }), 40, 40);

        Scheduler.timer(() -> {
            List<CustomEntity> entities = customEntities.values().stream()
                    .filter(CustomEntity::isAttackingNearbyEntities)
                    .filter(customEntity -> customEntity.getLinkedEntity() instanceof Creature)
                    .collect(Collectors.toList());
            for (CustomEntity entity : entities) {
                Creature linkedEntity = (Creature) entity.getLinkedEntity();
                LivingEntity target = linkedEntity.getTarget();
                if(target == null) {
                    List<LivingEntity> targets = findNearbyPossibleTargets(entity);
                    LivingEntity newTarget = getNearbyWithHighestPriority(entity, targets);

                    if(newTarget == null) {
                        newTarget = RandomUtils.getRandomObject(targets);
                    }

                    linkedEntity.setTarget(newTarget);
                } else if(!target.isValid() || !LocationUtils.hasDistance(linkedEntity.getLocation(), target.getLocation(), 20)) {
                    linkedEntity.setTarget(null);
                }
            }
        }, 30, 30);
    }

    private static List<LivingEntity> findNearbyPossibleTargets(CustomEntity customEntity) {
        if(!(customEntity.getLinkedEntity() instanceof Creature)) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(customEntity.getLinkedEntity().getWorld().getNearbyLivingEntities(customEntity.getLinkedEntity().getLocation(), 15, 10, 15, entity -> {
                if(entity instanceof Player) {
                    GameMode gameMode = ((Player) entity).getGameMode();
                    return gameMode.equals(GameMode.SURVIVAL) || gameMode.equals(GameMode.ADVENTURE);
                }
                return !entity.equals(customEntity.getLinkedEntity()) && entity.hasAI();
            }));
        }
    }

    private static LivingEntity getNearbyWithHighestPriority(CustomEntity customEntity, List<LivingEntity> candidates) {
        if(customEntity.getTargetPriorities().isEmpty()) {
            return null;
        }

        EntityType targetType = null;
        int highestValidPriority = -1;
        for (LivingEntity candidate : candidates) {
            int priority = customEntity.getTargetPriorities().getOrDefault(candidate.getType(), -1);
            if(priority > highestValidPriority) {
                highestValidPriority = priority;
                targetType = candidate.getType();
            }
        }

        if(targetType != null) {
            EntityType finalTargetType = targetType;
            return candidates.stream()
                    .filter(entity -> entity.getType().equals(finalTargetType))
                    .min(Comparator.comparingDouble(x -> x.getLocation().distanceSquared(customEntity.getLinkedEntity().getLocation()))).orElse(null);
        }

        return null;
    }

    private static String replaceOldClassNames(String input) {
        if(input.startsWith("com.mcgamer199.luckyblock.customentity")) {
            return input;
        }

        return input.replace("LB_", "com.mcgamer199.luckyblock.customentity")
                .replace("com.LuckyBlock.customentity", "com.mcgamer199.luckyblock.customentity")
                .replaceAll("^EntityElementalCreeper$", "CustomEntityElementalCreeper")
                .replaceAll("^EntityGuardian$", "CustomEntityGuardian")
                .replaceAll("^EntityKiller$", "CustomEntityKillerSkeleton")
                .replaceAll("^EntityKillerSkeleton$", "CustomEntityKillerSkeleton")
                .replaceAll("^EntityLuckyVillager$", "CustomEntityLuckyVillager")
                .replaceAll("^EntityRandomItem$", "CustomEntityRandomItem")
                .replaceAll("^EntitySoldier$", "CustomEntitySoldier")
                .replaceAll("^EntitySuperSlime$", "CustomEntitySuperSlime")
                .replaceAll("^EntitySuperWitherSkeleton$", "CustomEntitySuperWitherSkeleton")
                .replaceAll("^EntityTalkingZombie$", "CustomEntityTalkingZombie")
                .replaceAll("^EntityFloatingText$", "CustomEntityFloatingText")
                .replaceAll("^CustomEntityLBNameTag$", "CustomEntityLuckyBlockNameTag")
                .replaceAll("^INameTagHealth$", "CustomEntityHealthTag")
                .replaceAll("^EntityTagHealer$", "CustomEntityTagHealer")
                .replaceAll("^EntityTrophyNameTag$", "CustomEntityTrophyNameTag")
                .replaceAll("^EntityLCTItem$", "CustomEntityLCTItem")
                .replaceAll("^EntityLCTNameTag$", "CustomEntityLCTNameTag")
                .replaceAll("^EntityLBBlaze$", "CustomEntityBlazeMinion")
                .replaceAll("^EntityKnight$", "CustomEntityBossKnight")
                .replaceAll("^EntityBossWitch$", "CustomEntityBossWitch")
                .replaceAll("^EntityMC$", "CustomEntityMC")
                .replaceAll("^EntityUnderwaterBoss$", "CustomEntityUnderwaterBoss")
                .replaceAll("^EntityUnderwaterFollower$", "CustomEntityUnderwaterMinion")
                .replaceAll("^EntityFootballPlayer$", "CustomEntityBaseballPlayer")
                .replaceAll("^EntityHealer$", "CustomEntityHealer");
    }
}
