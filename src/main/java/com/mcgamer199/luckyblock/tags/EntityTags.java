package com.mcgamer199.luckyblock.tags;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class EntityTags extends HTag {
    public static HashMap<UUID, ItemStack[]> entityDrops = new HashMap();
    static File AfileF;
    public static FileConfiguration Afile;

    static {
        AfileF = new File(LuckyBlock.instance.getDataFolder() + File.separator + "Data/entityDrops.yml");
        Afile = YamlConfiguration.loadConfiguration(AfileF);
    }

    public EntityTags() {
    }

    public static Entity spawnEntity(ConfigurationSection c, Location loc1, ConfigurationSection main, boolean loop, Player player) {
        Entity entity = null;
        EntityType entitytype = null;
        Iterator var8 = c.getKeys(false).iterator();

        while(true) {
            String entityMetadataType;
            String type;
            while(var8.hasNext()) {
                entityMetadataType = (String)var8.next();
                if (entityMetadataType.equalsIgnoreCase("EntityType")) {
                    type = c.getString(entityMetadataType);

                    try {
                        entitytype = EntityType.valueOf(type.toUpperCase());
                    } catch (Exception var16) {
                        LuckyBlock.instance.getLogger().info("Invalid EntityType: " + type);
                    }

                    if (type.equalsIgnoreCase("horse")) {
                        entitytype = EntityType.HORSE;
                    }

                    if (type.equalsIgnoreCase("ocelot")) {
                        entitytype = EntityType.OCELOT;
                    }

                    if (type.equalsIgnoreCase("item") || type.equalsIgnoreCase("dropped_item")) {
                        entitytype = EntityType.DROPPED_ITEM;
                    }

                    if (type.equalsIgnoreCase("mushroom_cow")) {
                        entitytype = EntityType.MUSHROOM_COW;
                    }

                    if (type.equalsIgnoreCase("iron_golem")) {
                        entitytype = EntityType.IRON_GOLEM;
                    }
                } else if (entityMetadataType.equalsIgnoreCase("EntityTypeId")) {
                    entitytype = EntityType.fromId(c.getInt(entityMetadataType));
                }
            }

            if (entitytype != null) {
                if (entitytype != EntityType.DROPPED_ITEM) {
                    entity = loc1.getWorld().spawnEntity(loc1, entitytype);
                } else {
                    entity = loc1.getWorld().dropItem(loc1, new ItemStack(Material.STONE));
                }
            }

            if (entity != null) {
                var8 = c.getKeys(false).iterator();

                while(var8.hasNext()) {
                    entityMetadataType = (String)var8.next();
                    Iterator var10;
                    if (entityMetadataType.equalsIgnoreCase("With") && c.getStringList(entityMetadataType) != null && c.getStringList(entityMetadataType).size() > 0) {
                        var10 = c.getStringList(entityMetadataType).iterator();

                        while(var10.hasNext()) {
                            type = (String)var10.next();
                            spawnEntity(main.getConfigurationSection(type), loc1, main, true, player);
                        }
                    }

                    int number;
                    int place;
                    if (entityMetadataType.equalsIgnoreCase("Times") && loop) {
                        String[] d = c.getString(entityMetadataType).split("-");
                        place = getRandomNumber(d);

                        for(number = place; number > 1; --number) {
                            spawnEntity(c, loc1, main, false, player);
                        }
                    }

                    if (entityMetadataType.equalsIgnoreCase("CustomName")) {
                        ((Entity)entity).setCustomName(translateString(c.getString(entityMetadataType), new IDataType[]{new IDataType(TDataType.ENTITY, entity)}));
                    }

                    String[] itemData;
                    if (entityMetadataType.equalsIgnoreCase("Drops") && c.getStringList(entityMetadataType) != null && c.getStringList(entityMetadataType).size() > 0) {
                        ItemStack[] items = new ItemStack[64];
                        place = 0;

                        for(number = 0; number < c.getStringList(entityMetadataType).size(); ++number) {
                            if (place < 64) {
                                itemData = ((String)c.getStringList(entityMetadataType).get(number)).split(" ");
                                if (itemData.length == 2) {
                                    double percent = Double.parseDouble(itemData[0]);
                                    String name = itemData[1];
                                    if ((double)random.nextInt(100) < percent) {
                                        items[place] = ItemStackGetter.getItemStack(getSection(name));
                                        ++place;
                                    }
                                }
                            }
                        }

                        boolean notNull = false;

                        for(int x = 0; x < items.length; ++x) {
                            if (items[x] != null) {
                                notNull = true;
                                break;
                            }
                        }

                        if (notNull) {
                            entityDrops.put(((Entity)entity).getUniqueId(), items);
                            save();
                        }
                    }

                    if (entityMetadataType.equalsIgnoreCase("CustomNameVisible")) {
                        ((Entity)entity).setCustomNameVisible(c.getBoolean(entityMetadataType));
                    }

                    if (entityMetadataType.equalsIgnoreCase("Glowing")) {
                        ((Entity)entity).setGlowing(c.getBoolean(entityMetadataType));
                    }

                    if (entityMetadataType.equalsIgnoreCase("FallDistance")) {
                        ((Entity)entity).setFallDistance((float)c.getDouble(entityMetadataType));
                    }

                    if (entityMetadataType.equalsIgnoreCase("FireTicks")) {
                        ((Entity)entity).setFireTicks(c.getInt(entityMetadataType));
                    }

                    if (entityMetadataType.equalsIgnoreCase("Velocity")) {
                        Vector v = getVector(c.getConfigurationSection("Velocity"));
                        ((Entity)entity).setVelocity(v);
                    }

                    Entity e;
                    if (entityMetadataType.equalsIgnoreCase("Passengers") && c.getStringList(entityMetadataType) != null && c.getStringList(entityMetadataType).size() > 0) {
                        var10 = c.getStringList(entityMetadataType).iterator();

                        while(var10.hasNext()) {
                            type = (String)var10.next();
                            e = spawnEntity(main.getConfigurationSection(type), loc1, main, true, player);
                            ((Entity)entity).addPassenger(e);
                        }
                    }

                    if (entityMetadataType.equalsIgnoreCase("Vehicle") || entityMetadataType.equalsIgnoreCase("Riding")) {
                        Entity spawnEntity = spawnEntity(main.getConfigurationSection(c.getString(entityMetadataType)), loc1, main, true, player);
                        spawnEntity.addPassenger((Entity)entity);
                    }

                    ConfigurationSection pressF;
                    if (entityMetadataType.equalsIgnoreCase("Metadata") && c.getConfigurationSection(entityMetadataType) != null) {
                        var10 = c.getConfigurationSection(entityMetadataType).getKeys(false).iterator();

                        while(var10.hasNext()) {
                            type = (String)var10.next();
                            pressF = c.getConfigurationSection(entityMetadataType).getConfigurationSection(type);
                            String name = pressF.getString("Name");
                            Object value = pressF.get("Value");
                            ((Entity)entity).setMetadata(name, new FixedMetadataValue(LuckyBlock.instance, value));
                        }
                    }

                    if (entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity)entity;
                        if (entityMetadataType.equalsIgnoreCase("MaxHealth")) {
                            living.setMaxHealth(c.getDouble(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("Health")) {
                            if (living.getMaxHealth() >= c.getDouble(entityMetadataType)) {
                                living.setHealth(c.getDouble(entityMetadataType));
                            } else {
                                living.setMaxHealth(c.getDouble(entityMetadataType));
                                living.setHealth(c.getDouble(entityMetadataType));
                            }
                        }

                        if (entityMetadataType.equalsIgnoreCase("CanPickupItems")) {
                            living.setCanPickupItems(c.getBoolean(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("RemoveWhenFarAway")) {
                            living.setRemoveWhenFarAway(c.getBoolean(entityMetadataType));
                        }

                        String t;
                        ConfigurationSection f;
                        Iterator var41;
                        if (entityMetadataType.equalsIgnoreCase("PotionEffects") && c.getConfigurationSection(entityMetadataType) != null) {
                            var41 = c.getConfigurationSection(entityMetadataType).getKeys(false).iterator();

                            while(var41.hasNext()) {
                                t = (String)var41.next();
                                f = c.getConfigurationSection(entityMetadataType).getConfigurationSection(t);
                                if (f != null) {
                                    PotionEffect effect = ItemStackGetter.getPotionEffect(f);
                                    if (effect != null) {
                                        living.addPotionEffect(effect);
                                    }
                                }
                            }
                        }

                        if (entityMetadataType.equalsIgnoreCase("Equipment") && c.getConfigurationSection(entityMetadataType) != null) {
                            var41 = c.getConfigurationSection(entityMetadataType).getKeys(false).iterator();

                            while(var41.hasNext()) {
                                t = (String)var41.next();
                                itemData = null;
                                if (c.getConfigurationSection(entityMetadataType).getConfigurationSection(t) != null) {
                                    ItemStack item = ItemStackGetter.getItemStack(c.getConfigurationSection(entityMetadataType).getConfigurationSection(t));
                                    if (item != null) {
                                        if (t.equalsIgnoreCase("Helmet")) {
                                            living.getEquipment().setHelmet(item);
                                        } else if (t.equalsIgnoreCase("Chestplate")) {
                                            living.getEquipment().setChestplate(item);
                                        } else if (t.equalsIgnoreCase("Leggings")) {
                                            living.getEquipment().setLeggings(item);
                                        } else if (t.equalsIgnoreCase("Boots")) {
                                            living.getEquipment().setBoots(item);
                                        } else if (t.equalsIgnoreCase("ItemInHand")) {
                                            living.getEquipment().setItemInMainHand(item);
                                        } else if (t.equalsIgnoreCase("ItemInOffHand")) {
                                            living.getEquipment().setItemInOffHand(item);
                                        }
                                    }
                                }
                            }
                        }

                        if (entityMetadataType.equalsIgnoreCase("Attributes") && c.getConfigurationSection(entityMetadataType) != null) {
                            var41 = c.getConfigurationSection(entityMetadataType).getKeys(false).iterator();

                            while(var41.hasNext()) {
                                t = (String)var41.next();
                                f = c.getConfigurationSection(entityMetadataType).getConfigurationSection(t);
                                if (f != null) {
                                    Attribute attribute = null;
                                    double value = 0.0D;
                                    if (f.getString("AttributeName") != null) {
                                        attribute = Attribute.valueOf(f.getString("AttributeName").toUpperCase());
                                    }

                                    value = f.getDouble("Value");
                                    if (attribute != null) {
                                        living.getAttribute(attribute).setBaseValue(value);
                                    }
                                }
                            }
                        }

                        if (entityMetadataType.equalsIgnoreCase("DropChances")) {
                            String[] d = c.getString("DropChances").split(",");

                            for(number = 0; number < d.length; ++number) {
                                float num = Float.parseFloat(d[number]);
                                if (num == 0) {
                                    living.getEquipment().setItemInMainHandDropChance(num);
                                } else if (num == 4) {
                                    living.getEquipment().setHelmetDropChance(num);
                                } else if (num == 3) {
                                    living.getEquipment().setChestplateDropChance(num);
                                } else if (num == 2) {
                                    living.getEquipment().setLeggingsDropChance(num);
                                } else if (num == 1) {
                                    living.getEquipment().setBootsDropChance(num);
                                }
                            }
                        }

                        if (living instanceof Ageable) {
                            Ageable ageable = (Ageable)entity;
                            if (entityMetadataType.equalsIgnoreCase("Adult") && c.getBoolean(entityMetadataType)) {
                                ageable.setAdult();
                            }

                            if (entityMetadataType.equalsIgnoreCase("Baby") && c.getBoolean(entityMetadataType)) {
                                ageable.setBaby();
                            }

                            if (entityMetadataType.equalsIgnoreCase("AgeLock")) {
                                ageable.setAgeLock(c.getBoolean(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Breed")) {
                                ageable.setBreed(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Creature) {
                            Creature m = (Creature)living;
                            if (entityMetadataType.equalsIgnoreCase("Target")) {
                                e = spawnEntity(main.getConfigurationSection(c.getString(entityMetadataType)), loc1, main, true, player);
                                if (e instanceof LivingEntity) {
                                    m.setTarget((LivingEntity)e);
                                }
                            }
                        }

                        if (living instanceof Zombie) {
                            Zombie zombie = (Zombie)living;
                            if (entityMetadataType.equalsIgnoreCase("IsBaby")) {
                                zombie.setBaby(c.getBoolean(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("IsVillager")) {
                                zombie.setVillager(c.getBoolean(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Profession")) {
                                zombie.setVillagerProfession(Villager.Profession.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }
                        }

                        if (living instanceof Bat) {
                            Bat bat = (Bat)living;
                            if (entityMetadataType.equalsIgnoreCase("Awake")) {
                                bat.setAwake(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Creeper) {
                            Creeper creeper = (Creeper)living;
                            if (entityMetadataType.equalsIgnoreCase("Charged") || entityMetadataType.equalsIgnoreCase("Powered")) {
                                creeper.setPowered(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Snowman) {
                            Snowman snow = (Snowman)living;
                            if (entityMetadataType.equalsIgnoreCase("Derp") && c.getString(entityMetadataType).equalsIgnoreCase("true")) {
                                snow.setDerp(true);
                            }
                        }

                        if (living instanceof Enderman) {
                            Enderman enderman = (Enderman)living;
                            if (entityMetadataType.equalsIgnoreCase("CarriedMaterial") && c.getConfigurationSection(entityMetadataType) != null) {
                                f = c.getConfigurationSection(entityMetadataType);
                                Material material = null;
                                if (f.getString("Type") != null) {
                                    material = Material.getMaterial(f.getString("Type").toUpperCase());
                                }

                                byte data = (byte)f.getInt(entityMetadataType);
                                if (material != null) {
                                    enderman.setCarriedMaterial(new MaterialData(material, data));
                                }
                            }
                        }

                        if (living instanceof Guardian) {
                            Guardian guardian = (Guardian)living;
                            if (entityMetadataType.equalsIgnoreCase("Elder")) {
                                guardian.setElder(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Villager) {
                            Villager villager = (Villager)living;
                            if (entityMetadataType.equalsIgnoreCase("Profession")) {
                                villager.setProfession(Villager.Profession.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Riches")) {
                                villager.setRiches(c.getInt(entityMetadataType));
                            }

                            entityMetadataType.equalsIgnoreCase("Offers");
                        }

                        if (living instanceof IronGolem) {
                            IronGolem golem = (IronGolem)living;
                            if (entityMetadataType.equalsIgnoreCase("PlayerCreated")) {
                                golem.setPlayerCreated(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Horse) {
                            Horse horse = (Horse)living;
                            if (entityMetadataType.equalsIgnoreCase("CarryingChest")) {
                                horse.setCarryingChest(c.getBoolean(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Color")) {
                                horse.setColor(Horse.Color.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Domestication")) {
                                horse.setDomestication(c.getInt(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("JumpStrength")) {
                                horse.setJumpStrength(c.getDouble(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Style")) {
                                horse.setStyle(Horse.Style.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Variant")) {
                                horse.setVariant(Horse.Variant.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }

                            ItemStack itemStack;
                            if (entityMetadataType.equalsIgnoreCase("SaddleItem")) {
                                itemStack = ItemStackGetter.getItemStack(c.getConfigurationSection(entityMetadataType));
                                horse.getInventory().setSaddle(itemStack);
                            }

                            if (entityMetadataType.equalsIgnoreCase("ArmorItem")) {
                                itemStack = ItemStackGetter.getItemStack(c.getConfigurationSection(entityMetadataType));
                                horse.getInventory().setArmor(itemStack);
                            }
                        }

                        if (living instanceof Tameable) {
                            Tameable tameable = (Tameable)living;
                            if (entityMetadataType.equalsIgnoreCase("Owner") && c.getString(entityMetadataType).equalsIgnoreCase("{player}")) {
                                tameable.setOwner(player);
                            }

                            if (entityMetadataType.equalsIgnoreCase("Tamed")) {
                                tameable.setTamed(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Slime) {
                            Slime slime = (Slime)living;
                            if (entityMetadataType.equalsIgnoreCase("Size")) {
                                slime.setSize(getRandomNumber(c.getString(entityMetadataType).split("-")));
                            }
                        }

                        if (living instanceof Ocelot) {
                            Ocelot ocelot = (Ocelot)living;
                            if (entityMetadataType.equalsIgnoreCase("CatType")) {
                                ocelot.setCatType(Ocelot.Type.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Sitting")) {
                                ocelot.setSitting(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof PigZombie) {
                            PigZombie pz = (PigZombie)living;
                            if (entityMetadataType.equalsIgnoreCase("Anger")) {
                                pz.setAnger(c.getInt(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Angry")) {
                                pz.setAngry(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Rabbit) {
                            Rabbit rabbit = (Rabbit)living;
                            if (entityMetadataType.equalsIgnoreCase("RabbitType")) {
                                rabbit.setRabbitType(org.bukkit.entity.Rabbit.Type.valueOf(c.getString(entityMetadataType).toUpperCase()));
                            }
                        }

                        if (living instanceof Sheep) {
                            Sheep sheep = (Sheep)living;
                            if (entityMetadataType.equalsIgnoreCase("Color")) {
                                number = getRandomNumber(c.getString(entityMetadataType).split("-"));
                                sheep.setColor(DyeColor.getByDyeData((byte)number));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Sheared")) {
                                sheep.setSheared(c.getBoolean(entityMetadataType));
                            }
                        }

                        if (living instanceof Wolf) {
                            Wolf wolf = (Wolf)living;
                            if (entityMetadataType.equalsIgnoreCase("")) {
                                wolf.setAngry(c.getBoolean(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("CollarColor")) {
                                number = getRandomNumber(c.getString(entityMetadataType).split("-"));
                                wolf.setCollarColor(DyeColor.getByDyeData((byte)number));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Sitting")) {
                                wolf.setSitting(c.getBoolean(entityMetadataType));
                            }
                        }
                    }

                    if (entity instanceof Projectile) {
                        Projectile projectile = (Projectile)entity;
                        if (entityMetadataType.equalsIgnoreCase("Bounce")) {
                            projectile.setBounce(c.getBoolean(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("Shooter")) {
                            projectile.setShooter((ProjectileSource)null);
                        }

                        if (entity instanceof Fireball) {
                            Fireball fireball = (Fireball)projectile;
                            if (entityMetadataType.equalsIgnoreCase("Direction")) {
                                fireball.setDirection(getVector(c.getConfigurationSection("Velocity")));
                            }
                        }
                    }

                    if (entity instanceof Explosive) {
                        Explosive explosive = (Explosive)entity;
                        if (entityMetadataType.equalsIgnoreCase("IsIncendiary")) {
                            explosive.setIsIncendiary(c.getBoolean(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("Yield")) {
                            explosive.setYield((float)c.getDouble(entityMetadataType));
                        }
                    }

                    boolean var10000 = entity instanceof ArmorStand;
                    if (entity instanceof ExperienceOrb) {
                        ExperienceOrb orb = (ExperienceOrb)entity;
                        if (entityMetadataType.equalsIgnoreCase("Amount") || entityMetadataType.equalsIgnoreCase("Experience")) {
                            orb.setExperience(c.getInt(entityMetadataType));
                        }
                    }

                    if (entity instanceof FallingBlock) {
                        FallingBlock falling = (FallingBlock)entity;
                        if (entityMetadataType.equalsIgnoreCase("DropItem")) {
                            falling.setDropItem(c.getBoolean(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("HurtEntities")) {
                            falling.setHurtEntities(c.getBoolean(entityMetadataType));
                        }
                    }

                    var10000 = entity instanceof Firework;
                    var10000 = entity instanceof ItemFrame;
                    var10000 = entity instanceof ThrownPotion;
                    if (entity instanceof TNTPrimed) {
                        TNTPrimed tnt = (TNTPrimed)entity;
                        if (entityMetadataType.equalsIgnoreCase("FuseTicks")) {
                            tnt.setFuseTicks(c.getInt(entityMetadataType));
                        }
                    }

                    if (entity instanceof Minecart) {
                        Minecart minecart = (Minecart)entity;
                        if (entityMetadataType.equalsIgnoreCase("DisplayBlockOffset")) {
                            minecart.setDisplayBlockOffset(c.getInt(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("MaxSpeed")) {
                            minecart.setMaxSpeed(c.getDouble(entityMetadataType));
                        }

                        if (entityMetadataType.equalsIgnoreCase("SlowWhenEmpty")) {
                            minecart.setSlowWhenEmpty(c.getBoolean(entityMetadataType));
                        }

                        var10000 = minecart instanceof StorageMinecart;
                        var10000 = minecart instanceof HopperMinecart;
                        if (minecart instanceof CommandMinecart) {
                            CommandMinecart cmd = (CommandMinecart)minecart;
                            if (entityMetadataType.equalsIgnoreCase("Command")) {
                                cmd.setCommand(c.getString(entityMetadataType));
                            }

                            if (entityMetadataType.equalsIgnoreCase("Name")) {
                                cmd.setName(c.getString(entityMetadataType));
                            }
                        }
                    }

                    if (entity instanceof Item) {
                        Item item = (Item)entity;
                        if (entityMetadataType.equalsIgnoreCase("ItemStack") && c.getConfigurationSection(entityMetadataType) != null) {
                            item.setItemStack(ItemStackGetter.getItemStack(c.getConfigurationSection(entityMetadataType)));
                        }

                        if (entityMetadataType.equalsIgnoreCase("PickupDelay")) {
                            item.setPickupDelay(c.getInt(entityMetadataType));
                        }
                    }
                }
            }

            return (Entity)entity;
        }
    }

    protected static Vector getVector(ConfigurationSection c) {
        Vector v = null;
        if (c != null) {
            int x = c.getInt("X");
            int y = c.getInt("Y");
            int z = c.getInt("Z");
            v = new Vector(x, y, z);
        }

        return v;
    }

    public static Entity spawnRandomEntity(ConfigurationSection c, Location loc1, Player player) {
        return spawnEntity(c.getConfigurationSection(getRandomLoc(c)), loc1, c, true, player);
    }

    public static void save() {
        Afile.set("Entities", (Object)null);
        if (entityDrops.size() > 0) {
            int i = 0;

            for(Iterator var2 = entityDrops.keySet().iterator(); var2.hasNext(); ++i) {
                UUID uuid = (UUID)var2.next();
                Afile.set("Entities.Entity" + i + ".UUID", uuid.toString());

                for(int x = 0; x < ((ItemStack[])entityDrops.get(uuid)).length; ++x) {
                    ItemStack item = ((ItemStack[])entityDrops.get(uuid))[x];
                    if (item != null) {
                        Afile.set("Entities.Entity" + i + ".Items.Item" + x, item);
                    }
                }
            }
        }

        try {
            Afile.save(AfileF);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static void load() {
        if (AfileF.exists()) {
            ConfigurationSection c = Afile.getConfigurationSection("Entities");
            if (c != null) {
                Iterator var2 = c.getKeys(false).iterator();

                while(true) {
                    ConfigurationSection f;
                    do {
                        if (!var2.hasNext()) {
                            return;
                        }

                        String s = (String)var2.next();
                        f = c.getConfigurationSection(s);
                    } while(f == null);

                    UUID uuid = UUID.fromString(f.getString("UUID"));
                    ItemStack[] items = new ItemStack[64];
                    if (f.getConfigurationSection("Items") != null) {
                        int x = 0;

                        for(Iterator var8 = f.getConfigurationSection("Items").getKeys(false).iterator(); var8.hasNext(); ++x) {
                            String t = (String)var8.next();
                            items[x] = f.getConfigurationSection("Items").getItemStack(t);
                        }
                    }

                    entityDrops.put(uuid, items);
                }
            }
        }

    }

    public static void addRandomDrops(UUID uuid, double[] chances, ItemStack... it) {
        try {
            ItemStack[] items = new ItemStack[64];
            int place = 0;

            for(int x = 0; x < it.length; ++x) {
                if (place < 64) {
                    double percent = chances[place];
                    if ((double)random.nextInt(100) < percent) {
                        items[place] = it[x];
                        ++place;
                    }
                }
            }

            entityDrops.put(uuid, items);
            save();
        } catch (Exception var8) {
            throw new Error("An error occured while trying to add random drops!");
        }
    }

    public static void addRandomDrops(Entity entity, double[] chances, ItemStack... it) {
        addRandomDrops(entity.getUniqueId(), chances, it);
    }
}
