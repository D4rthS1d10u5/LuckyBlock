package com.mcgamer199.luckyblock.lb;

import com.google.common.collect.Iterators;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.customentity.*;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.resources.LBEntitiesSpecial;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.structures.LuckyWell;
import com.mcgamer199.luckyblock.tags.BlockTags;
import com.mcgamer199.luckyblock.tags.ChestFiller;
import com.mcgamer199.luckyblock.tags.EntityTags;
import com.mcgamer199.luckyblock.tags.ItemStackTags;
import com.mcgamer199.luckyblock.util.*;
import com.mcgamer199.luckyblock.yottaevents.PlayerData;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum LuckyBlockDrop {

    NONE(false),
    CHEST(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();

        String path = luckyBlock.getDropOptions().getString("Path", "Chests");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);
        ChestFiller chestFiller = new com.mcgamer199.luckyblock.tags.ChestFiller(luckyBlock.getFile().getConfigurationSection(path), chest);
        chestFiller.loc1 = path1;
        chestFiller.fill();
    }),
    ENTITY(true, (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = luckyBlock.getDropOptions().getString("Path", "Entities");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);
        if (path1 == null) {
            EntityTags.spawnRandomEntity(file.getConfigurationSection(path), luckyBlock.getBlock().getLocation(), player);
        } else {
            EntityTags.spawnEntity(file.getConfigurationSection(path).getConfigurationSection(path1), luckyBlock.getBlock().getLocation(), file.getConfigurationSection(path), true, player);
        }
    }),
    LAVA(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        for (BlockFace face : LocationUtils.allHorizontal()) {
            block.getRelative(face).setType(Material.STATIONARY_LAVA);
        }
    }),
    FIREWORK(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Firework firework = (Firework) block.getWorld().spawnEntity(block.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(RandomUtils.nextBoolean()).withColor(Color.GREEN).withColor(Color.RED).withColor(Color.YELLOW).withFade(Color.AQUA).with(type).trail(RandomUtils.nextBoolean()).build();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(RandomUtils.nextInt(2) + 1);
        firework.setFireworkMeta(fireworkMeta);
        Scheduler.later(() -> {
            if(RandomUtils.nextBoolean()) {
                TNTPrimed tnt = (TNTPrimed) firework.getWorld().spawnEntity(firework.getLocation(), EntityType.PRIMED_TNT);

                boolean allowExplosionGrief = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                if (!allowExplosionGrief) {
                    tnt.setMetadata("tnt", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
                }

                tnt.setFuseTicks(70);
                tnt.setYield(5.0F);
            } else {
                FallingBlock tntfake = firework.getWorld().spawnFallingBlock(firework.getLocation().add(0.5D, 0.0D, 0.5D), Material.TNT, (byte) 0);
                tntfake.setDropItem(false);
            }
        }, 60);
    }),
    F_PIGS(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Scheduler.create(() -> {
            Bat bat = (Bat) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 0.0D, 0.0D), EntityType.BAT);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
            Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
            if(RandomUtils.nextBoolean()) {
                pig.setCustomName("§eLucky Pig §a+1 Health");
            } else {
                pig.setCustomName("§eLucky Pig §a+2 Health");
            }

            pig.setCustomNameVisible(true);
            bat.addPassenger(pig);
            bat.setMetadata("luckybat", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));

            Scheduler.create(() -> pig.getWorld().spawnParticle(Particle.REDSTONE, pig.getLocation(), 100, 0.3D, 0.3D, 0.3D, 1.0D))
                    .predicate(() -> !pig.isDead() && !bat.isDead())
                    .timer(2, 2);
        }).count(RandomUtils.nextInt(5) + 4).timer(1, 1);
    }),
    SLIMES(true, (luckyBlock, player) -> {
        Location blockLocation = luckyBlock.getBlock().getLocation();
        int slimeSize = RandomUtils.nextInt(3) + 1;
        int slimeCount = RandomUtils.nextInt(3) + 3;
        SuperSlime superSlime = new SuperSlime();
        for (int i = 0; i < slimeCount; i++) {
            EntitySuperSlime entitySuperSlime = new EntitySuperSlime();
            entitySuperSlime.setSize(slimeSize); entitySuperSlime.spawn(blockLocation);
            superSlime.add(entitySuperSlime);
        }
        superSlime.ride();
    }),
    F_LB(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Bat bat = (Bat) block.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
        FallingBlock fallingBlock = bat.getWorld().spawnFallingBlock(bat.getLocation().add(0.0D, 5.0D, 0.0D), luckyBlock.getType().getBlockType(), (byte) luckyBlock.getType().getData());
        bat.setPassenger(fallingBlock);
        bat.setMetadata("flyinglb", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
        fallingBlock.setDropItem(false);

        Scheduler.later(() -> {
            if (bat.isValid() && bat.getPassengers().isEmpty()) {
                bat.remove();
            }

            bat.getWorld().dropItem(bat.getLocation(), luckyBlock.getType().toItemStack(RandomUtils.nextInt(0, 20)));
        }, 20);
    }),
    SOLDIER(true, (luckyBlock, player) -> new EntitySoldier().spawn(luckyBlock.getBlock().getLocation())),
    LB_ITEM(true, (luckyBlock, player) -> luckyBlock.getBlock().getWorld().dropItem(luckyBlock.getBlock().getLocation(), luckyBlock.getType().toItemStack(RandomUtils.nextInt(-10, 10)))),
    BEDROCK(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Scheduler.later(() -> {
            block.setType(Material.BEDROCK);
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                block.getRelative(BlockFace.UP).setType(Material.SIGN_POST);
                Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
                sign.setLine(1, "Well, there's");
                sign.setLine(2, "your problem");
                sign.update(true);
            }
        }, 1);
    }),
    SINK_IN_GROUND(true, (luckyBlock, player) -> {
        if(player != null) {
            Scheduler.create(() -> player.teleport(player.getLocation().add(0.0D, -1.0D, 0.0D)))
                    .predicate(() -> player.getLocation().getY() > 0)
                    .timer(7, 7);
        }
    }),
    WOLVES_OCELOTS(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Scheduler.create(() -> {
            if (RandomUtils.nextBoolean()) {
                Wolf wolf = (Wolf) block.getWorld().spawnEntity(block.getLocation(), EntityType.WOLF);
                wolf.setTamed(true);
                wolf.setOwner(player);
                wolf.setMaxHealth(30.0D);
                wolf.setHealth(30.0D);
                wolf.setSitting(true);
                wolf.setCollarColor(RandomUtils.getRandomObject(DyeColor.values()));
                wolf.setCustomName(String.format("§e§lWolf §a%.00f§f/§a%.00f", wolf.getHealth(), wolf.getMaxHealth()));
                wolf.setCustomNameVisible(true);
            } else {
                Ocelot ocelot = (Ocelot) block.getWorld().spawnEntity(block.getLocation(), EntityType.OCELOT);
                ocelot.setCatType(RandomUtils.getRandomObject(Ocelot.Type.values()));
                ocelot.setSitting(true);
                ocelot.setOwner(player);
                ocelot.setTamed(true);
                ocelot.setMaxHealth(20.0D);
                ocelot.setHealth(20.0D);
                ocelot.setCustomName(String.format("§e§lOcelot §a%.00f§f/§a%.00f", ocelot.getHealth(), ocelot.getMaxHealth()));
                ocelot.setCustomNameVisible(true);
            }
        }).count(RandomUtils.nextInt(5) + 7).timer(1, 1);
    }),
    FIREWORKS(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Scheduler.create(() -> {
            for (int x = RandomUtils.nextInt(18) + 8; x > 0; --x) {
                Firework fwork = (Firework) block.getWorld().spawnEntity(block.getLocation(), EntityType.FIREWORK);
                FireworkMeta fwm = fwork.getFireworkMeta();
                FireworkEffect.Type type = RandomUtils.getRandomObject(FireworkEffect.Type.values());

                FireworkEffect f = FireworkEffect.builder().flicker(RandomUtils.nextBoolean())
                        .withColor(Color.fromBGR(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255)))
                        .withColor(Color.fromBGR(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255)))
                        .withColor(Color.fromBGR(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255)))
                        .withFade(Color.fromBGR(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255)))
                        .with(type)
                        .trail(RandomUtils.nextBoolean())
                        .build();

                fwm.clearEffects();
                fwm.addEffect(f);
                fwm.setPower(RandomUtils.nextInt(3) + 1);
                fwork.setFireworkMeta(fwm);
            }
        }).count(RandomUtils.nextInt(5) + 5).timer(5, 20);
    }),
    FEED(true, (luckyBlock, player) -> {
        if(player != null) {
            player.setFoodLevel(20);
        }
    }),
    HEAL(true, (luckyBlock, player) -> {
        if(player != null) {
            player.setHealth(player.getMaxHealth());
        }
    }),
    ADD_ITEM(true, (luckyBlock, player) -> {
        if (player != null) {
            FileConfiguration file = luckyBlock.getFile();
            String path = luckyBlock.getDropOptions().getString("Path", "AddedItems");
            String path1 = luckyBlock.getDropOptions().getString("Path1", null);
            ItemStack[] items = path1 != null ? ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1)) : ItemStackTags.getRandomItems(file.getConfigurationSection(path));
            player.getInventory().addItem(items);
        }
    }),
    POISON_ENTITIES(true, (luckyBlock, player) -> {
        if (player != null) {
            for (Entity entity : player.getNearbyEntities(10.0D, 10.0D, 10.0D)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    if(entity instanceof Tameable && player.getUniqueId().equals(((Tameable) entity).getOwnerUniqueId())) {
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 10));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 10));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 10));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 15));
                }
            }
        }
    }),
    CUSTOM_STRUCTURE(true, (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = luckyBlock.getDropOptions().getString("Path", "Structures");
        String path1 = luckyBlock.getDropOptions().getString("Path1", BlockTags.getRandomL(file, path));
        String locationType = file.getString(String.format("%s.%s.%s", path, path1, "LocationType"), "BLOCK");
        if (locationType.equalsIgnoreCase("PLAYER") && player != null) {
            BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), player.getLocation());
        } else {
            BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), luckyBlock.getBlock().getLocation());
        }
    }),
    RANDOM_ITEM(true, (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = luckyBlock.getDropOptions().getString("Path", "RandomItems");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);
        String key = path1 != null ? path1 : BlockTags.getRandomL(file, path);
        EntityRandomItem entityRandomItem = new EntityRandomItem();

        for (String g : file.getConfigurationSection(path + "." + key + ".Items").getKeys(false)) {
            entityRandomItem.items.add(com.mcgamer199.luckyblock.tags.ItemStackGetter.getItemStack(file, path + "." + key + ".Items." + g));
        }

        entityRandomItem.spawn(luckyBlock.getBlock().getLocation());
    }),
    ZOMBIE_TRAP(true, (luckyBlock, player) -> {
        if (player != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 100));
            Zombie zombie = (Zombie) player.getWorld().spawnEntity(luckyBlock.getBlock().getLocation(), EntityType.ZOMBIE);
            zombie.setMaxHealth(300.0D);
            zombie.setHealth(300.0D);
            zombie.getEquipment().setItemInMainHand(ItemStackUtils.addEnchant(new ItemStack(Material.DIAMOND_SWORD), Enchantment.DAMAGE_ALL, 200));
            zombie.setTarget(player);
            Scheduler.later(zombie::remove, 80);
        }
    }),
    TRADES(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Villager villager = (Villager) block.getWorld().spawnEntity(block.getLocation(), EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.BLACKSMITH);
        villager.setCustomName("§e§lLucky Villager");
        villager.setCustomNameVisible(true);

        List<MerchantRecipe> recipes = new ArrayList<>();
        int randomLuck = luckyBlock.getType().getRandomP();
        ItemStack stack = ItemStackUtils.createItem(Material.POTION, 1, RandomUtils.nextInt(3) + 1, "§e§lLucky Potion");
        PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
        PotionData potionData = new PotionData(PotionType.FIRE_RESISTANCE);
        potionMeta.setBasePotionData(potionData);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 400, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1200, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 2), true);
        stack.setItemMeta(potionMeta);

        MerchantRecipe recipe1 = new MerchantRecipe(luckyBlock.getType().toItemStack(randomLuck), 30);
        recipe1.addIngredient(new ItemStack(Material.DIAMOND, randomLuck / 10 + RandomUtils.nextInt(25) + 16));

        MerchantRecipe recipe2 = new MerchantRecipe(stack, 30);
        recipe2.addIngredient(new ItemStack(Material.EMERALD, RandomUtils.nextInt(31) + 16));
        recipes.add(recipe1);
        recipes.add(recipe2);
        villager.setRecipes(recipes);
    }),
    ROCKET(true, (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX(), location.getY() - 1.0D, location.getZ()), EntityType.ARMOR_STAND);
        armorStand.teleport(location);
        armorStand.setMetadata("hrocket", new FixedMetadataValue(LuckyBlockPlugin.instance, armorStand.getUniqueId().toString()));
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setHelmet(new ItemStack(Material.IRON_BLOCK));

        Scheduler.timer(new BukkitRunnable() {

            private int times = 80;

            @Override
            public void run() {
                if (this.times > 0) {
                    Location loc = new Location(armorStand.getWorld(), armorStand.getLocation().getX(), armorStand.getLocation().getY() + 1.0D, armorStand.getLocation().getZ());
                    if (loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
                        cancel();
                        armorStand.getWorld().createExplosion(armorStand.getLocation(), 7.0F, true);
                        armorStand.remove();
                    }

                    armorStand.teleport(armorStand.getLocation().add(0.0D, 2.2D, 0.0D));
                    armorStand.getWorld().spawnParticle(Particle.SMOKE_LARGE, armorStand.getLocation(), 35, 1.0D, 0.5D, 1.0D, 0.0D);

                    for (Entity e : armorStand.getNearbyEntities(2.0D, 4.0D, 2.0D)) {
                        if (!(e instanceof Player)) {
                            e.setFireTicks(60);
                        }
                    }

                    --this.times;
                } else {
                    cancel();
                    armorStand.getWorld().spawnParticle(Particle.FLAME, armorStand.getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
                    armorStand.remove();
                }
            }
        }, 1, 1);
    }),
    TALKING_ZOMBIE(true, (luckyBlock, player) -> new EntityTalkingZombie().spawn(luckyBlock.getBlock().getLocation())),
    BOB(true, (luckyBlock, player) -> LBEntitiesSpecial.spawnBob(luckyBlock.getBlock().getLocation(), false)),
    PETER(true, (luckyBlock, player) -> LBEntitiesSpecial.spawnPeter(luckyBlock.getBlock().getLocation(), false)),
    KILL(true, (luckyBlock, player) -> {
        if(player != null) {
            player.setHealth(0);
        }
    }),
    WATER_TRAP(true, (luckyBlock, player) -> {
        if (player != null) {
            Block block = player.getLocation().getBlock();

            for (BlockFace face : LocationUtils.allHorizontal()) {
                block.getRelative(face).setType(Material.OBSIDIAN);
                block.getRelative(face).getRelative(BlockFace.UP).setType(Material.OBSIDIAN);
            }

            for (BlockFace face : LocationUtils.horizontal()) {
                block.getRelative(face).getRelative(BlockFace.UP).setType(Material.GLASS);
            }

            player.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
            player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
            player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.WATER);
        }
    }),
    LB_STRUCTURE(true, (luckyBlock, player) -> { //TODO переделать строительство структур
        if (luckyBlock.hasDropOption("Class")) {
            TemporaryUtils.b(luckyBlock.getDropOptions().getString("Class"), luckyBlock.getBlock().getLocation());
        }
    }),
    LAVA_POOL(true, (luckyBlock, player) -> {
        if(player != null) {
            Block block = player.getLocation().getBlock();
            block.getRelative(BlockFace.DOWN).setType(Material.STATIONARY_LAVA);
            for (BlockFace face : LocationUtils.allHorizontal()) {
                block.getRelative(face).getRelative(BlockFace.DOWN).setType(Material.STATIONARY_LAVA);
            }
        }
    }),
    LUCKY_WELL(true, (luckyBlock, player) -> {
        new LuckyWell().build(luckyBlock.getBlock().getLocation());
        if (player != null) {
            ItemStack item = ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.GOLD_NUGGET, 1, 0, "§6§lCoin", Collections.singletonList("§7Drop it in the well")), Enchantment.DURABILITY, 1);
            ItemMeta itemM = item.getItemMeta();
            itemM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemM);
            player.getInventory().addItem(item);
        }
    }),
    KARL(true, (luckyBlock, player) -> LBEntitiesSpecial.spawnKarl(player, luckyBlock.getBlock().getLocation(), false)),
    HELL_HOUND(true, (luckyBlock, player) -> LBEntitiesSpecial.spawnHellHound(player, luckyBlock.getBlock().getLocation(), false)),
    METEORS_1(true, (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        Scheduler.create(() -> {
            FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location.add(0.0D, 1.0D, 0.0D), Material.COBBLESTONE, (byte) 0);
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector((RandomUtils.nextInt(6) - 3) / 5D, (RandomUtils.nextInt(15) + 14) / 10D, (RandomUtils.nextInt(6) - 3) / 5D));

            Scheduler.timer(new BukkitRunnable() {
                private boolean disabled = false;

                @Override
                public void run() {
                    if (fallingBlock.isValid()) {
                        fallingBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 170, 0.3D, 0.2D, 0.3D, 0.0D);

                        for (Entity e : fallingBlock.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
                            if (e instanceof LivingEntity) {
                                e.setFireTicks(100);
                                e.setFallDistance(15.0F);
                            }
                        }

                        Item item = fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(RandomUtils.nextBoolean() ? Material.COBBLESTONE : Material.STONE));
                        item.setPickupDelay(1000);
                        Scheduler.timer(new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (item.isValid()) {

                                    for (Entity e : item.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
                                        if (e instanceof LivingEntity) {
                                            e.setFireTicks(600);
                                        }
                                    }

                                    if (item.isOnGround()) {
                                        Block block = item.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                                        if (block != null && block.getType() != item.getItemStack().getType() && block.getType().isSolid() && !block.getType().isTransparent()) {
                                            Material mat = block.getType();
                                            block.setType(item.getItemStack().getType());
                                            Scheduler.later(() -> {
                                                block.setType(mat);
                                                block.setData(block.getData());
                                            }, 360);
                                            cancel();
                                        }
                                    }
                                } else {
                                    cancel();
                                }
                            }
                        }, 10, 10);
                        Scheduler.later(item::remove, 300);
                    } else if ((fallingBlock.isDead() || fallingBlock.isOnGround()) && !this.disabled) {
                        this.disabled = true;

                        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionFire");
                        fallingBlock.getWorld().createExplosion(fallingBlock.getLocation().getBlockX(), fallingBlock.getLocation().getBlockY(), fallingBlock.getLocation().getBlockZ(), 15F, setFire, breakBlocks);

                        fallingBlock.remove();
                        cancel();
                    }
                }
            }, 3, 3);
        }).count(luckyBlock.getDropOptions().getInt("Times", 15)).timer(2, 2);
    }),
    ILLUMINATI(false, (luckyBlock, player) -> {
        if (player != null) {
            ChatComponent component = new ChatComponent();
            component.addText("§5[§6Illuminati§5]", Hover.show_text, "§cIlluminati confirmed!\n\n§eDon't click!", Click.run_command, String.format("/%s illuminati", ILBCmd.lcmd));
            component.send(player);
        }
    }),
    FALLING_BLOCK(true, new Properties().putDouble("Height", 10D), (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = luckyBlock.getDropOptions().getString("Path", "FallingBlocks");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);
        double height = luckyBlock.getDropOptions().getDouble("Height", 10D);
        if (path1 == null) {
            BlockTags.spawnRandomFallingBlock(file, path, luckyBlock.getBlock().getLocation().add(0.5D, height, 0.5D));
        } else {
            BlockTags.spawnFallingBlock(file, path, path1, luckyBlock.getBlock().getLocation().add(0.5D, height, 0.5D));
        }
    }),
    VILLAGER(true, new Properties().putInt("Seconds", 4), (luckyBlock, player) -> {
        EntityLuckyVillager villager = new EntityLuckyVillager();
        villager.seconds = MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Seconds", 4), 0, 1000);
        villager.spawn(luckyBlock.getBlock().getLocation());
    }),
    SPLASH_POTION(true, new Properties().putStringArray("Effects", new String[] {"SPEED%200%0"}), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(PotionType.AWKWARD));
        String[] effects = luckyBlock.getDropOptions().getStringArray("Effects");
        for (String effect : effects) {
            if (effect != null) {
                String[] potionData = effect.split("%");
                if (PotionEffectType.getByName(potionData[0].toUpperCase()) != null) {

                    int du;
                    byte am;
                    try {
                        du = Integer.parseInt(potionData[1]);
                        am = Byte.parseByte(potionData[2]);
                    } catch (NumberFormatException var23) {
                        ColorsClass.send_no(player, "invalid_number");
                        return;
                    }

                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potionData[0].toUpperCase()), du, am), true);
                    potion.setItemMeta(potionMeta);
                    ThrownPotion thrownPotion = (ThrownPotion) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 10.0D, 0.0D), EntityType.SPLASH_POTION);
                    thrownPotion.setItem(potion);
                } else {
                    ColorsClass.send_no(player, "drops.potion_effect.invalid_effect");
                }
            }
        }
    }),
    PRIMED_TNT(true, new Properties().putFloat("TntPower", 3F).putInt("Fuse", 50), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 20.0D, 0.0D), EntityType.PRIMED_TNT);
        tnt.setYield(luckyBlock.getDropOptions().getFloat("TntPower", 3F));
        tnt.setFireTicks(2000);
        tnt.setFuseTicks(luckyBlock.getDropOptions().getInt("Fuse", 50));
        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
        if (!breakBlocks) {
            tnt.setMetadata("tnt", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
        }
    }),
    LIGHTNING(true, new Properties().putInt("Times", 10), (luckyBlock, player) -> {
        if (player != null) {
            Block block = luckyBlock.getBlock();
            Scheduler.create(() -> player.getWorld().strikeLightning(block.getLocation())).count(luckyBlock.getDropOptions().getInt("Times", 10)).timer(0, 4);
        }
    }),
    FAKE_ITEM(true, new Properties().putEnum("ItemMaterial", Material.DIAMOND).putShort("ItemData", (short) 0).putInt("ItemAmount", 64).putInt("Ticks", 85), (luckyBlock, player) -> {
        if (luckyBlock.hasDropOption("ItemMaterial")) {
            Block block = luckyBlock.getBlock();
            Material material = luckyBlock.getDropOptions().getEnum("ItemMaterial", Material.class, Material.DIAMOND);
            int itemAmount = luckyBlock.getDropOptions().getInt("ItemAmount", 1);
            short data = luckyBlock.getDropOptions().getShort("ItemData");
            Item item = block.getWorld().dropItem(block.getLocation(), new ItemStack(material, itemAmount, data));
            item.setPickupDelay(32000);

            Scheduler.later(item::remove, MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Ticks", 85), 1, 1024) );
        }
    }),
    DROPPED_ITEMS(true, new Properties().putBoolean("Effects", false).putBoolean("ShowItemName", false), (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        Block block = luckyBlock.getBlock();
        String path = luckyBlock.getDropOptions().getString("Path", "DroppedItems");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);

        ItemStack[] items = path1 == null ? ItemStackTags.getRandomItems(file.getConfigurationSection(path)) : ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1));
        if (luckyBlock.getDropOptions().getBoolean("Effects")) {
            Iterator<ItemStack> iterator = Arrays.asList(items).iterator();
            Scheduler.create(() -> {
                ItemStack itemStack = iterator.next();
                Item item = block.getLocation().getWorld().dropItem(block.getLocation(), itemStack);
                item.setVelocity(new Vector((RandomUtils.nextInt(4) - 2) / 50D, 0.4D, (RandomUtils.nextInt(4) - 2) / 50D));
                if (luckyBlock.hasDropOption("ShowItemName") && luckyBlock.getDropOption("ShowItemName").getValues()[0].toString().equalsIgnoreCase("true") && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                    item.setCustomName(itemStack.getItemMeta().getDisplayName());
                    item.setCustomNameVisible(true);
                }

                SoundUtils.playFixedSound(block.getLocation(), SoundUtils.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 50);
            }).predicate(iterator::hasNext).timer(5, 5);
        } else {
            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    Item droppedItem = block.getWorld().dropItem(block.getLocation(), itemStack);
                    if (luckyBlock.getDropOptions().getBoolean("ShowItemName") && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                        droppedItem.setCustomName(itemStack.getItemMeta().getDisplayName());
                        droppedItem.setCustomNameVisible(true);
                    }
                }
            }
        }
    }),
    STUCK(true, new Properties().putInt("Duration", 15), (luckyBlock, player) -> { //TODO сделать слушатель
        if (player != null) {
            PlayerData.set(player, "stuck", true);
            Scheduler.later(() -> PlayerData.remove(player, "stuck"), luckyBlock.getDropOptions().getInt("Duration", 10) * 10);
        }
    }),
    DAMAGE(true, new Properties().putInt("Value", 5), (luckyBlock, player) -> {
        if (player != null) {
            player.damage(luckyBlock.getDropOptions().getDouble("Value", 2.5D));
        }
    }),
    TOWER(true, new Properties().putString("Type", "a"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        String type = luckyBlock.getDropOptions().getString("Type", "a");
        Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
        block.getWorld().playEffect(loc, Effect.POTION_BREAK, RandomUtils.nextInt(10) + 1);
        int[] i1 = TemporaryUtils.tower_rblock(type);
        block.getWorld().spawnFallingBlock(loc.add(0.0D, 10.0D, 0.0D), i1[0], (byte) i1[1]).setDropItem(false);

        Scheduler.timer(new BukkitRunnable() {
            private int loop = RandomUtils.nextInt(4) + 6;

            @Override
            public void run() {
                if (this.loop > 1) {
                    int[] i2 = TemporaryUtils.tower_rblock(type);
                    block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, i2[0], (byte) i2[1]).setDropItem(false);
                    --this.loop;
                } else if (this.loop == 1) {
                    FallingBlock fallingBlock = block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, Material.DIAMOND_BLOCK, (byte) 0);
                    Scheduler.later(() -> fallingBlock.getLocation().getWorld().strikeLightning(fallingBlock.getLocation()), 3);
                    cancel();
                }
            }
        }, 6, 6);
    }),
    METEORS(true, new Properties().putFloat("ExplosionPower", 11F), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        for (int times = 8; times > 0; --times) {
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(RandomUtils.nextInt(10), 35.0D, RandomUtils.nextInt(10)), Material.STONE, (byte) 0);
            fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(2));
            float explosionPower = luckyBlock.getDropOptions().getFloat("ExplosionPower", 11F);
            Scheduler.timer(new BukkitRunnable() {
                private boolean disabled = false;

                @Override
                public void run() {
                    if (fallingBlock.isValid()) {
                        fallingBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 170, 0.3D, 0.2D, 0.3D, 0.0D);

                        for (Entity e : fallingBlock.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
                            if (e instanceof LivingEntity) {
                                e.setFireTicks(100);
                                e.setFallDistance(15.0F);
                            }
                        }

                        Item item = fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(RandomUtils.nextBoolean() ? Material.COBBLESTONE : Material.STONE));
                        item.setPickupDelay(1000);
                        Scheduler.timer(new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (item.isValid()) {

                                    for (Entity e : item.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
                                        if (e instanceof LivingEntity) {
                                            e.setFireTicks(600);
                                        }
                                    }

                                    if (item.isOnGround()) {
                                        Block block = item.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                                        if (block != null && block.getType() != item.getItemStack().getType() && block.getType().isSolid() && !block.getType().isTransparent()) {
                                            Material mat = block.getType();
                                            block.setType(item.getItemStack().getType());
                                            Scheduler.later(() -> {
                                                block.setType(mat);
                                                block.setData(block.getData());
                                            }, 360);
                                            cancel();
                                        }
                                    }
                                } else {
                                    cancel();
                                }
                            }
                        }, 10, 10);
                        Scheduler.later(item::remove, 300);
                    } else if ((fallingBlock.isDead() || fallingBlock.isOnGround()) && !this.disabled) {
                        this.disabled = true;

                        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionFire");
                        fallingBlock.getWorld().createExplosion(fallingBlock.getLocation().getBlockX(), fallingBlock.getLocation().getBlockY(), fallingBlock.getLocation().getBlockZ(), explosionPower, setFire, breakBlocks);

                        fallingBlock.remove();
                        cancel();
                    }
                }
            }, 3, 3);
        }
    }),
    ELEMENTAL_CREEPER(true, new Properties().putEnum("BlockMaterial", Material.DIRT).putByte("BlockData", (byte) 0), (luckyBlock, player) -> {
        Properties dropOptions = luckyBlock.getDropOptions();
        EntityElementalCreeper elementalCreeper = new EntityElementalCreeper();
        elementalCreeper.spawn(luckyBlock.getBlock().getLocation());
        elementalCreeper.changeMaterial(dropOptions.getEnum("BlockMaterial", Material.class, Material.DIRT), dropOptions.getByte("BlockData"));
    }),
    JAIL(true, new Properties().putInt("Ticks", 70), (luckyBlock, player) -> {
        if (player != null) {
            //TODO я так понимаю эту гору кода можно заменить методом #jail(Block), надо проверить
            Block block = player.getLocation().getBlock();
            block.getLocation().add(2.0D, 0.0D, 0.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(2.0D, 0.0D, 1.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(2.0D, 0.0D, -1.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(2.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(2.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            int a = -2;
            int b = -2;

            int c;
            int d;
            for (c = 5; c > 0; --c) {
                for (d = 5; d > 0; --d) {
                    block.getLocation().add(a, -1.0D, b).getBlock().setType(Material.SMOOTH_BRICK);
                    ++a;
                }

                a = -2;
                ++b;
            }

            block.getLocation().add(1.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(1.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(0.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(0.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-1.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-1.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-2.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-2.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-2.0D, 0.0D, 0.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-2.0D, 0.0D, 1.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(-2.0D, 0.0D, -1.0D).getBlock().setType(Material.SMOOTH_BRICK);
            block.getLocation().add(2.0D, 1.0D, 0.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(2.0D, 1.0D, 1.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(2.0D, 1.0D, -1.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(2.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(2.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(1.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(1.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(0.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(0.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-1.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-1.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-2.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-2.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-2.0D, 1.0D, 0.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-2.0D, 1.0D, 1.0D).getBlock().setType(Material.IRON_FENCE);
            block.getLocation().add(-2.0D, 1.0D, -1.0D).getBlock().setType(Material.IRON_FENCE);
            c = -2;
            d = -2;

            int e;
            int f;
            for (e = 5; e > 0; --e) {
                for (f = 5; f > 0; --f) {
                    block.getLocation().add(c, 2.0D, d).getBlock().setType(Material.SMOOTH_BRICK);
                    ++c;
                }

                c = -2;
                ++d;
            }

            e = -1;
            f = -1;

            for (int x = 3; x > 0; --x) {
                for (int y = 3; y > 0; --y) {
                    block.getLocation().add(e, 3.0D, f).getBlock().setType(Material.SMOOTH_BRICK);
                    ++e;
                }

                e = -1;
                ++f;
            }

            block.getLocation().add(0.0D, 3.0D, 0.0D).getBlock().setType(Material.LAVA);
            Scheduler.later(() -> block.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.AIR), MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Ticks", 70), 0, 1024));
        }
    }),
    TREE(true, new Properties().putEnum("TreeType", TreeType.TREE), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        TreeType type = luckyBlock.getDropOptions().getEnum("TreeType", TreeType.class, TreeType.TREE);
        block.getRelative(BlockFace.DOWN).setType(type.equals(TreeType.CHORUS_PLANT) ? Material.ENDER_STONE : Material.DIRT);
        Scheduler.later(() -> block.getWorld().generateTree(block.getLocation(), type), 1);
    }),
    SIGN(true, new Properties().putStringArray("Texts", new String[]{"&cHello", "&5How are you?"}).putString("Facing", "PLAYER"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.SIGN_POST);
        Sign sign = (Sign) block.getState();
        String facing = luckyBlock.getDropOptions().getString("Facing", "PLAYER");
        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
        if (facing.equalsIgnoreCase("PLAYER") && player != null) {
            signData.setFacingDirection(LocationUtils.getFacingBetween(block.getLocation(), player.getLocation()));
        } else {
            signData.setFacingDirection(BlockFace.valueOf(facing.toUpperCase()));
        }

        if (luckyBlock.hasDropOption("Texts")) {
            String[] texts = luckyBlock.getDropOptions().getStringArray("Texts");
            for (int i = 0; i < texts.length; i++) {
                if(texts[i] != null) {
                    sign.setLine(i, texts[i]);
                }
            }
        }
        sign.update(true);
    }),
    REPAIR(true, new Properties().putString("RepairType", "all"), (luckyBlock, player) -> {
        if (player != null) {
            ColorsClass.send_no(player, "drops.repair.1");
            String repairType = luckyBlock.getDropOptions().getString("RepairType", "ALL");
            AtomicInteger repairCount = new AtomicInteger();
            Predicate<ItemStack> repairCandidate = (item) -> !ItemStackUtils.isNullOrAir(item) && ItemStackUtils.isRepairable(item.getType()) && item.getDurability() > 0;
            Consumer<ItemStack> repairAction = (item) -> {
                item.setDurability((short) 0);
                repairCount.incrementAndGet();
            };

            if(repairType.equalsIgnoreCase("all")) {
                Arrays.stream(ArrayUtils.concat(player.getInventory().getContents(), player.getInventory().getArmorContents())).filter(repairCandidate).forEach(repairAction);
            } else if(StringUtils.containsIgnoreCase(repairType, "inv")) {
                Arrays.stream(player.getInventory().getContents()).filter(repairCandidate).forEach(repairAction);
            } else if(StringUtils.containsIgnoreCase(repairType, "armo")) {
                Arrays.stream(player.getInventory().getArmorContents()).filter(repairCandidate).forEach(repairAction);
            } else if(repairType.equalsIgnoreCase("hand")) {
                ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
                if(repairCandidate.test(item)) {
                    repairAction.accept(item);
                }
            }

            if (repairCount.get() > 0) {
                SoundUtils.playFixedSound(player.getLocation(), SoundUtils.getSound("lb_drop_repair"), 1.0F, 1.0F, 20);
                player.sendMessage(ColorsClass.val("drops.repair.2").replace("%total%", String.valueOf(repairCount.get())));
            }
        }
    }),
    ENCHANT(true, new Properties().putStringArray("Enchants", new String[]{"DURABILITY", "DAMAGE_ALL"}).putIntArray("Levels", new Integer[]{2, 1}), (luckyBlock, player) -> {
        if (player != null) {
            ItemStack heldItem = player.getInventory().getItem(EquipmentSlot.HAND);
            if(!ItemStackUtils.isNullOrAir(heldItem)) {
                ItemMeta meta = heldItem.getItemMeta();
                String enchantmentName = RandomUtils.getRandomObject(luckyBlock.getDropOptions().getStringArray("Enchants"));
                int enchantmentLevel = RandomUtils.getRandomObject(luckyBlock.getDropOptions().getIntArray("Levels"));
                meta.addEnchant(Enchantment.getByName(enchantmentName.toUpperCase()), enchantmentLevel, true);
                heldItem.setItemMeta(meta);
                ColorsClass.send_no(player, "drops.enchant_item.success");
                player.updateInventory();
            } else {
                ColorsClass.send_no(player, "drops.enchant_item.fail");
            }
        }
    }),
    XP(true, new Properties().putInt("XPAmount", 75), (luckyBlock, player) -> {
        Location blockLocation = luckyBlock.getBlock().getLocation();
        ExperienceOrb exp = (ExperienceOrb) blockLocation.getWorld().spawnEntity(blockLocation, EntityType.EXPERIENCE_ORB);
        exp.setExperience(luckyBlock.getDropOptions().getInt("XPAmount", 75));
    }),
    RUN_COMMAND(true, new Properties().putString("Commmand", "/say hello!"), (luckyBlock, player) -> {
        String command = luckyBlock.getDropOptions().getString("Command");
        if(!command.isEmpty()) {
            if (player != null) {
                command = ChatColor.translateAlternateColorCodes('&', command).replace("{playername}", player.getName());
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }),
    CLEAR_EFFECTS(true, new Properties().putStringArray("Effects", new String[]{"SLOW", "SLOW_DIGGING", "HARM", "CONFUSION", "BLINDNESS", "HUNGER", "WEAKNESS", "POISON", "WITHER"}), (luckyBlock, player) -> {
        if (player != null) {
            for (String effect : luckyBlock.getDropOptions().getStringArray("Effects")) {
                player.removePotionEffect(PotionEffectType.getByName(effect));
            }
        }
    }),
    TELEPORT(true, new Properties().putInt("Height", 20), (luckyBlock, player) -> {
        if (player != null) {
            player.teleport(player.getLocation().add(0.0D, luckyBlock.getDropOptions().getInt("Height", 20), 0.0D));
        }
    }),
    PERFORM_ACTION(true, new Properties().putEnum("ObjType", ColorsClass.ObjectType.NONE).putString("ActionName", null)),
    TNT_RAIN(true, new Properties().putInt("Times", 20).putInt("Fuse", 60), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        int fuse = luckyBlock.getDropOptions().getInt("Fuse", 60);
        Scheduler.create(() -> {
            TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            tnt.setFuseTicks(fuse);
            tnt.setVelocity(new Vector((RandomUtils.nextInt(4) - 2) / 10.D, 1.0D, (RandomUtils.nextInt(4) - 2) / 10.D));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_tntrain"), 1.0F, 0.0F, 50);
        }).count(luckyBlock.getDropOptions().getInt("Times", 10)).timer(5, 5);
    }),
    ITEM_RAIN(true, new Properties().putInt("Times", 20).putStringArray("ItemMaterials", new String[]{"EMERALD", "DIAMOND", "IRON_INGOT", "GOLD_INGOT", "GOLD_NUGGET"}).putShortArray("ItemsData", new Short[] {0}), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        Material[] materials = new Material[64];
        Short[] itemsData = luckyBlock.getDropOptions().getShortArray("ItemsData", new Short[] {0});

        if (luckyBlock.hasDropOption("ItemMaterials")) {
            String[] itemMaterials = luckyBlock.getDropOptions().getStringArray("ItemMaterials");
            materials = new Material[itemMaterials.length];
            for (int i = 0; i < itemMaterials.length; i++) {
                if(itemMaterials[i] != null) {
                    materials[i] = Material.getMaterial(itemMaterials[i]);
                }
            }
        }

        Iterator<Material> iterator = Arrays.asList(materials).iterator();
        Iterators.removeIf(iterator, Objects::isNull);
        short itemData = RandomUtils.getRandomObject(itemsData);

        Scheduler.create(() -> {
            Material itemMaterial = iterator.next();
            Item item = location.getWorld().dropItem(location, new ItemStack(itemMaterial, 1, itemData));
            item.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 20);
        }).predicate(iterator::hasNext).count(luckyBlock.getDropOptions().getInt("Times", 10)).timer(2, 2);
    }),
    BLOCK_RAIN(true, new Properties().putInt("Times", 20).putStringArray("BlockMaterials", new String[]{"EMERALD_BLOCK", "GOLD_BLOCK", "LAPIS_BLOCK", "DIAMOND_BLOCK", "IRON_BLOCK"}), (luckyBlock, player) -> {
        Material[] materials = new Material[64];

        if (luckyBlock.hasDropOption("BlockMaterials")) {
            String[] obj = luckyBlock.getDropOptions().getStringArray("BlockMaterials");
            materials = new Material[obj.length];
            for (int i = 0; i < obj.length; i++) {
                if(obj[i] != null) {
                    materials[i] = Material.getMaterial(obj[i].toString());
                }
            }
        }

        Iterator<Material> iterator = Arrays.asList(materials).iterator();
        Iterators.removeIf(iterator, Objects::isNull);
        Material used = RandomUtils.getRandomObject(iterator);
        Location location = luckyBlock.getBlock().getLocation();
        Scheduler.create(() -> {
            FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, used, (byte) 0);
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector((RandomUtils.nextInt(4) - 2) / 5.0D, 1.0D, (RandomUtils.nextInt(4) - 2) / 5.0D));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_blockrain_launch"), 1.0F, 1.0F, 50);

            Scheduler.timer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fallingBlock.isValid()) {
                        MaterialData d = new MaterialData(fallingBlock.getMaterial(), fallingBlock.getBlockData());
                        fallingBlock.getWorld().spawnParticle(Particle.BLOCK_CRACK, fallingBlock.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, d);
                        SoundUtils.playFixedSound(fallingBlock.getLocation(), SoundUtils.getSound("lb_drop_blockrain_land"), 1.0F, 1.0F, 60);
                        cancel();
                    }
                }
            }, 0, 2);
        }).count(luckyBlock.getDropOptions().getInt("Times", 10)).timer(3, 3);
    }),
    ARROW_RAIN(true, new Properties().putInt("Times", 20).putBoolean("Critical", true).putBoolean("Bounce", true), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation().add(0.5, 0, 0.5);
        boolean critical = luckyBlock.getDropOptions().getBoolean("Critical", true);
        boolean bounce = luckyBlock.getDropOptions().getBoolean("Bounce", true);
        Scheduler.create(() -> {
            Arrow a = (Arrow) location.getWorld().spawnEntity(location, EntityType.ARROW);
            a.setVelocity(new Vector((RandomUtils.nextInt(16) - 8) / 50.0D, 1.2D, (RandomUtils.nextInt(16) - 8) / 50.0D));
            a.setCritical(critical);
            a.setBounce(bounce);
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_arrowrain"), 1.0F, 1.0F, 50);
        }).count(luckyBlock.getDropOptions().getInt("Times", 10)).timer(1, 1);
    }),
    SET_NEARBY_BLOCKS(true, new Properties().putString("BlockMaterial", "DIAMOND_BLOCK").putByte("BlockData", (byte) 0).putInt("Range", 10).putInt("Delay", 8).putString("Mode", "SURFACE"), (luckyBlock, player) -> {
        if (luckyBlock.hasDropOption("BlockMaterial")) {
            Material material = luckyBlock.getDropOptions().getEnum("BlockMaterial", Material.class, Material.DIAMOND_BLOCK);
            byte data = luckyBlock.getDropOptions().getByte("BlockData");
            int times = MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Range", 10), 0, 50);
            int delay = luckyBlock.getDropOptions().getInt("Delay", 8);
            String mode = luckyBlock.getDropOptions().getString("Mode", "SURFACE");

            if (mode.equalsIgnoreCase("all") && times > 32) {
                times = 32;
            }
            Location location = luckyBlock.getBlock().getLocation();
            Runnable surface = new Runnable() {
                private int g = 1;
                @Override
                public void run() {
                    for (int x = this.g * -1; x < this.g + 1; ++x) {
                        for (int y = -4; y < 5; ++y) {
                            for (int z = this.g * -1; z < this.g + 1; ++z) {
                                Location l = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
                                if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                    l.getBlock().setType(material);
                                    l.getBlock().setData(data);
                                }
                            }
                        }
                    }

                    ++this.g;
                }
            };

            Runnable all = new Runnable() {
                private int g = 1;

                @Override
                public void run() {
                    for (int x = this.g * -1; x < this.g + 1; ++x) {
                        for (int y = this.g * -1; y < this.g + 1; ++y) {
                            for (int z = this.g * -1; z < this.g + 1; ++z) {
                                Location l = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
                                if (l.getBlock().getType().isSolid() && l.getBlock().getType() != Material.AIR) {
                                    l.getBlock().setType(material);
                                    l.getBlock().setData(data);
                                }
                            }
                        }
                    }

                    ++this.g;
                }
            };

            Scheduler.create(mode.equalsIgnoreCase("all") ? all : surface).count(times).timer(delay, delay);
        }
    }),
    SOUND(true, new Properties().putString("Listener", "PLAYER").putString("SoundName", "UI_BUTTON_CLICK"), (luckyBlock, player) -> {
        String listenerType = luckyBlock.getDropOptions().getString("Listener", "PLAYER");
        Sound sound = Sound.valueOf(luckyBlock.getDropOptions().getString("SoundName", "UI_BUTTON_CLICK"));
        if (listenerType.equalsIgnoreCase("player") && player != null) {
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        } else if (listenerType.equalsIgnoreCase("nearby")) {
            SoundUtils.playFixedSound(luckyBlock.getBlock().getLocation(), sound, 1.0F, 1.0F, 30);
        }
    }),
    XP_RAIN(true, new Properties().putInt("Times", 32), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        Scheduler.create(() -> {
            ThrownExpBottle xp = (ThrownExpBottle) location.getWorld().spawnEntity(location, EntityType.THROWN_EXP_BOTTLE);
            xp.setVelocity(new Vector((RandomUtils.nextInt(8) - 4) / 50.0D, 0.9D, (RandomUtils.nextInt(8) - 4) / 60.0D));
            xp.setBounce(true);
        }).count(luckyBlock.getDropOptions().getInt("Times", 30)).timer(1, 2);
    }),
    SET_BLOCK(true, new Properties().putEnum("BlockMaterial", Material.DIAMOND_BLOCK), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        byte blockType = luckyBlock.getDropOptions().getByte("BlockData");
        Material blockMaterial = luckyBlock.getDropOptions().getEnum("BlockMaterial", Material.class, Material.DIAMOND_BLOCK);
        if (luckyBlock.getDropOptions().getBoolean("ShowParticles")) {
            MaterialData md = new MaterialData(blockMaterial, blockType);
            block.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, md);
        }
    }),
    FALLING_ANVILS(true, new Properties().putInt("Height", 20).putByte("AnvilData", (byte) 0).putString("LocationType", "PLAYER"), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        byte blockType = luckyBlock.getDropOptions().getByte("AnvilData");
        int height = luckyBlock.getDropOptions().getInt("Height", 20);

        Location l = location.add(0.0D, height, 0.0D);
        if(player != null && luckyBlock.hasDropOption("LocationType") && luckyBlock.getDropOptions().getString("LocationType").equalsIgnoreCase("player")) {
            l = player.getLocation().add(0.0D, height, 0.0D);
        }

        for (int playerX = -1; playerX < 2; ++playerX) {
            for (int playerY = -1; playerY < 2; ++playerY) {
                FallingBlock b = l.getWorld().spawnFallingBlock(new Location(l.getWorld(), l.getX() + playerX + 0.5D, l.getY(), l.getZ() + playerY + 0.5D), Material.ANVIL, blockType);
                b.setDropItem(false);
            }
        }
    }),
    DISPENSER(true, new Properties().putInt("Times", 64), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.DISPENSER);
        block.setData((byte) 1);
        Dispenser dispenser = (Dispenser) block.getState();
        int repeatCount = luckyBlock.getDropOptions().getInt("Times", 64);

        while (repeatCount > 0) {
            if (repeatCount > 63) {
                dispenser.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                repeatCount -= 64;
            } else {
                dispenser.getInventory().addItem(new ItemStack(Material.ARROW, repeatCount));
                repeatCount = 0;
            }
        }

        Scheduler.create(dispenser::dispense)
                .predicate(() -> dispenser.getBlock().getType().equals(Material.DISPENSER) && dispenser.getInventory().contains(Material.ARROW)).timer(3, 1);
    }),
    POTION_EFFECT(true, new Properties().putStringArray("Effects", new String[] {"SPEED%200%0"}), (luckyBlock, player) -> {
        if (player != null && luckyBlock.hasDropOption("Effects")) {
            for (String effect : luckyBlock.getDropOptions().getStringArray("Effects")) {
                if (effect != null) {
                    String[] effectData = effect.split("%");
                    if (PotionEffectType.getByName(effectData[0].toUpperCase()) != null) {

                        byte amplifier;
                        int duration;
                        try {
                            duration = Integer.parseInt(effectData[1]);
                            amplifier = Byte.parseByte(effectData[2]);
                        } catch (NumberFormatException var20) {
                            ColorsClass.send_no(player, "invalid_number");
                            return;
                        }

                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectData[0].toUpperCase()), duration, amplifier));
                    } else {
                        ColorsClass.send_no(player, "drops.potion_effect.invalid_effect");
                    }
                }
            }
        }
    }),
    DAMAGE_1(true, new Properties().putInt("Times", 30).putInt("Ticks", 11), (luckyBlock, player) -> {
        if (player != null) {
            int ticks = luckyBlock.getDropOptions().getInt("Ticks", 11);
            Scheduler.create(() -> {
                if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && player.getHealth() > 0.0D) {
                    player.setHealth(Math.max(0.0D, player.getHealth() - 1.0D));
                }
            }).count(luckyBlock.getDropOptions().getInt("Times", 30)).timer(ticks, ticks);
        }
    }),
    FIRE(true, new Properties().putInt("Range", 6), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        Scheduler.create(new Runnable() {
            private int g = 1;

            @Override
            public void run() {
                for (int x = this.g * -1; x < this.g + 1; ++x) {
                    for (int y = -4; y < 5; ++y) {
                        for (int z = this.g * -1; z < this.g + 1; ++z) {
                            Location l = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
                            if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                l.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
                            }
                        }
                    }
                }
                ++g;
            }
        }).count(MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Range", 10), 0, 50)).timer(3, 20);
    }),
    EXPLOSION(true, new Properties().putFloat("ExplosionPower", 4.0F), (luckyBlock, player) -> luckyBlock.getBlock().getLocation().getWorld().createExplosion(luckyBlock.getBlock().getLocation(), luckyBlock.getDropOptions().getFloat("ExplosionPower", 4.0F))),
    LAVA_HOLE(true, new Properties().putByte("Radius", (byte) 2).putBoolean("WithWebs", true).putEnum("BordersMaterial", Material.STONE).putByte("BordersData", (byte) 0).putStringArray("Texts", new String[]{"Say goodbye :)"}), (luckyBlock, player) -> {
        if (player != null) {
            int playerX = player.getLocation().getBlockX();
            int playerY = player.getLocation().getBlockY();
            int playerZ = player.getLocation().getBlockZ();
            byte rad = (byte) MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Radius", 2), 2, 48);
            boolean withWebs = luckyBlock.getDropOptions().getBoolean("WithWebs", true);
            Material border = luckyBlock.getDropOptions().getEnum("BordersMaterial", Material.class, Material.STONE);
            byte data = luckyBlock.getDropOptions().getByte("BordersData");

            for (int x = rad * -1 - 1; x < rad + 2; x++) {
                for (int z = rad * -1 - 1; z < rad + 2; z++) {
                    for (int y = playerY - 1; y > 0; y--)
                    {
                        Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                        loc.getBlock().setType(border);
                        loc.getBlock().setData(data);
                    }
                }
            }
            for (int x = rad * -1; x < rad + 1; x++) {
                for (int z = rad * -1; z < rad + 1; z++) {
                    for (int y = playerY; y > 0; y--)
                    {
                        Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
            if (withWebs) {
                for (int x = rad * -1; x < rad + 1; x++) {
                    for (int z = rad * -1; z < rad + 1; z++)
                    {
                        Location loc = new Location(player.getLocation().getWorld(), playerX + x, 4.0D, playerZ + z);
                        loc.getBlock().setType(Material.WEB);
                    }
                }
            }
            Location loc = new Location(player.getLocation().getWorld(), playerX - rad, 5.0D, playerZ);
            Material mat = Material.WALL_SIGN;
            if (loc.getBlock().getRelative(BlockFace.WEST).getType() == Material.AIR) {
                loc.getBlock().getRelative(BlockFace.WEST).setType(Material.STONE);
            }
            loc.getBlock().setType(mat);
            loc.getBlock().setData((byte)5);
            Sign sign = (Sign)loc.getBlock().getState();
            String[] texts = luckyBlock.getDropOptions().getStringArray("Texts", new String[0]);
            if(texts.length > 0) {
                for (int i = 0; i < texts.length; i++) {
                    sign.setLine(i, texts[i]);
                }
            }

            sign.update();
            for (int x = rad * -1; x < rad + 1; x++) {
                for (int z = rad * -1; z < rad + 1; z++) {
                    for (int y = 2; y > 0; y--) {
                        Location location = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                        location.getBlock().setType(Material.LAVA);
                    }
                }
            }
        }
    }),
    VOID_HOLE(true, new Properties().putByte("Radius", (byte) 2).putEnum("BordersMaterial", Material.AIR).putByte("BordersData", (byte) 0), (luckyBlock, player) -> {
        if (player != null) {
            byte rad = (byte) MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Radius", 2), 2, 48);
            Material material = luckyBlock.getDropOptions().getEnum("BordersMaterial", Material.class, Material.AIR);//Material.AIR;
            byte data = luckyBlock.getDropOptions().getByte("BordersData");

            int playerX = player.getLocation().getBlockX();
            int playerY = player.getLocation().getBlockY();
            int playerZ = player.getLocation().getBlockZ();
            if (material != Material.AIR) {
                for (int x = rad * -1 - 1; x < rad + 2; x++) {
                    for (int z = rad * -1 - 1; z < rad + 2; z++) {
                        for (int y = playerY; y > -1; y--)
                        {
                            Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                            loc.getBlock().setType(material);
                            loc.getBlock().setData(data);
                        }
                    }
                }
            }
            for (int x = rad * -1; x < rad + 1; x++) {
                for (int z = rad * -1; z < rad + 1; z++) {
                    for (int y = playerY; y > -1; y--)
                    {
                        Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }),
    DROPPER(true, new Properties().putInt("Times", 64), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.DROPPER);
        block.setData((byte) 1);
        Dropper dropper = (Dropper) block.getState();
        int times = luckyBlock.getDropOptions().getInt("Times", 64);

        while (times > 0) {
            if (times > 63) {
                dropper.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
                times -= 64;
            } else {
                dropper.getInventory().addItem(new ItemStack(Material.DIAMOND, times));
                times = 0;
            }
        }

        Scheduler.create(dropper::drop)
                .predicate(() -> dropper.getBlock().getType().equals(Material.DROPPER) && dropper.getInventory().contains(Material.DIAMOND)).timer(3, 3);
    }),
    EXPLOSIVE_CHEST(true, new Properties().putInt("Ticks", 50).putBoolean("ClearInventory", true), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.CHEST);
        int times = MathUtils.ensureRange(luckyBlock.getDropOptions().getInt("Ticks", 50), 0, 1024);
        String path = luckyBlock.getDropOptions().getString("Path", "Chests");
        String path1 = luckyBlock.getDropOptions().getString("Path1", null);

        if (path != null && path1 != null) {
            ChestFiller chestFiller = new ChestFiller(luckyBlock.getFile().getConfigurationSection(path), (Chest) block.getState());
            chestFiller.loc1 = path1;
            chestFiller.fill();
        }

        Scheduler.later(() -> {
            if (luckyBlock.getDropOptions().getBoolean("ClearInventory", true)) {
                ((Chest) block.getState()).getBlockInventory().clear();
            }

            block.getWorld().createExplosion(block.getLocation(), 3.5F);
        }, times);
    }),
    ADD_LEVEL(true, new Properties().putInt("Amount", 10), (luckyBlock, player) -> {
        if (player != null) {
            player.giveExpLevels(luckyBlock.getDropOptions().getInt("Amount", 10));
        }
    }),
    ADD_XP(true, new Properties().putInt("Amount", 550), (luckyBlock, player) -> {
        if (player != null) {
            player.giveExp(luckyBlock.getDropOptions().getInt("Amount", 550));
        }
    }),
    LB_RAIN(true, (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        LBType type = luckyBlock.getType();

        Scheduler.create(() -> {
            FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, type.getType(), (byte) type.getData());
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector((RandomUtils.nextInt(4) - 2) / 5.0D, 1.0D, (RandomUtils.nextInt(4) - 2) / 5.0D));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_lbrain"), 1.0F, 1.0F, 50);

            Scheduler.timer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fallingBlock.isValid()) {
                        Block block = fallingBlock.getLocation().getBlock();
                        if (block.getType().equals(type.getType())) {
                            LuckyBlock luckyBlock = new LuckyBlock(LBType.fromMaterialAndData(type.getType(), (byte) type.getData()), block, 0, null, true, true);
                            luckyBlock.playEffects();
                        }

                        cancel();
                    }
                }
            }, 5, 5);
        }).count(10).timer(3, 3);
    }),
    SCHEMATIC_STRUCTURE(true, new Properties().putString("LocationType", "PLAYER").putIntArray("Loc", new Integer[]{0, 0, 0}), (luckyBlock, player) -> {
        if (LuckyBlockPlugin.isWorldEditValid()) {
            if (luckyBlock.hasDropOption("LocationType") && luckyBlock.hasDropOption("File")) {
                Integer[] locationOffset = luckyBlock.getDropOptions().getIntArray("Loc", new Integer[] {0, 0, 0});

                File file = new File(LuckyBlockPlugin.d() + "Drops/" + luckyBlock.getDropOptions().getString("File") + ".schematic");
                String locationType = luckyBlock.getDropOptions().getString("LocationType");
                Location location = null;
                if (locationType.equalsIgnoreCase("PLAYER") && player != null) {
                    location = player.getLocation();
                } else if (locationType.equalsIgnoreCase("BLOCK")) {
                    location = luckyBlock.getBlock().getLocation();
                }

                if (location != null) {
                    Schematic.loadArea(file, location.add(locationOffset[0], locationOffset[1], locationOffset[2]));
                }
            }
        } else if (player != null) {
            ColorsClass.send(player, "schematic_error");
        }
    }),
    TNT_IN_THE_MIDDLE(true, new Properties().putEnum("BlocksMaterial", Material.DIAMOND_BLOCK).putByte("BlocksData", (byte) 0).putInt("Fuse", 65).putFloat("ExplosionPower", 5F), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Material material = luckyBlock.getDropOptions().getEnum("BlocksMaterial", Material.class, Material.DIAMOND_BLOCK);
        byte data = luckyBlock.getDropOptions().getByte("BlocksData");

        block.getRelative(BlockFace.EAST).setType(material);
        block.getRelative(BlockFace.EAST).setData(data);
        block.getRelative(BlockFace.WEST).setType(material);
        block.getRelative(BlockFace.WEST).setData(data);
        block.getRelative(BlockFace.SOUTH).setType(material);
        block.getRelative(BlockFace.SOUTH).setData(data);
        block.getRelative(BlockFace.NORTH).setType(material);
        block.getRelative(BlockFace.NORTH).setData(data);
        block.getRelative(BlockFace.UP).setType(material);
        block.getRelative(BlockFace.UP).setData(data);
        block.getRelative(BlockFace.DOWN).setType(material);
        block.getRelative(BlockFace.DOWN).setData(data);
        TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation().add(0.5D, 0.0D, 0.5D), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(luckyBlock.getDropOptions().getInt("Fuse", 65));
        tnt.setYield(luckyBlock.getDropOptions().getFloat("ExplosionPower", 5.0F));
    }),
    FLYING_TNTS(true, new Properties().putInt("Times", 8).putInt("Fuse", 80), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();

        int fuse = luckyBlock.getDropOptions().getInt("Fuse", 80);
        for (int i = 0; i < luckyBlock.getDropOptions().getInt("Times", 8); i++) {
            Bat bat = (Bat)block.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
            bat.setHealth(0.5D);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
            TNTPrimed tnt = (TNTPrimed)block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(fuse);
            bat.addPassenger(tnt);
        }
    }),
    ANVIL_JAIL(true, new Properties().putDouble("Height", 35D), (luckyBlock, player) -> {
        if (player != null) {
            Block block = player.getLocation().getBlock();
            TemporaryUtils.jail(player.getLocation().getBlock());
            block.getWorld().spawnFallingBlock(player.getLocation().add(0.0D, luckyBlock.getDropOptions().getDouble("Height", 35.0D), 0.0D), new MaterialData(Material.ANVIL));
        }
    }),
    LAVA_JAIL(true, new Properties().putInt("Ticks", 55), (luckyBlock, player) -> {
        if (player != null) {
            TemporaryUtils.jail(player.getLocation().getBlock());
            Scheduler.later(() -> luckyBlock.getBlock().getLocation().getBlock().setType(Material.LAVA), luckyBlock.getDropOptions().getInt("Ticks", 55));
        }
    }),
    RIP(false, new Properties().putBoolean("ClearInventory", true), (luckyBlock, player) -> {
        if(player != null) {
            Block block = luckyBlock.getBlock();
            block.setType(Material.SKULL);
            block.setData((byte) 1);
            block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
            block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).setType(Material.DIRT);
            block.getRelative(BlockFace.NORTH).setType(Material.STONE);
            block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).setType(Material.STONE);
            block.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
            block.getRelative(BlockFace.UP).setData((byte) 3);
            Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
            sign.setLine(0, "RIP");
            sign.setLine(2, player.getName());
            sign.update(true);
            Skull skull = (Skull) block.getState();
            skull.setSkullType(SkullType.PLAYER);
            skull.setRotation(BlockFace.SOUTH);
            skull.setOwningPlayer(player);
            skull.update(true);
        }
    }),
    //Отсутствует информация о коде
    EQUIP_ITEM(false),
    ANVIL_RAIN(false, new Properties().putInt("Duration", 200).putString("BorderMaterial", "OBSIDIAN")),
    DONT_MINE(false, new Properties().putString("BlocksMaterial", "DIAMOND_ORE").putFloat("ExplosionPower", 5.5F));

    @Getter
    private Properties dropOptions;
    @Getter
    private final boolean visible;
    private BiConsumer<LuckyBlock, Player> onBreakFunction;
    public static final LuckyBlockDrop[] values = values();
    private static final TreeMap<String, LuckyBlockDrop> BY_NAME = new TreeMap<>();

    LuckyBlockDrop(boolean visible, Properties dropOptions, BiConsumer<LuckyBlock, Player> onBreakFunction) {
        this.visible = visible;
        this.dropOptions = dropOptions;
        this.onBreakFunction = onBreakFunction;
    }

    LuckyBlockDrop(boolean visible, Properties dropOptions) {
        this.visible = visible;
        this.dropOptions = dropOptions;
    }

    LuckyBlockDrop(boolean visible) {
        this.visible = visible;
    }

    LuckyBlockDrop(boolean visible, BiConsumer<LuckyBlock, Player> onBreakFunction) {
        this.visible = visible;
        this.onBreakFunction = onBreakFunction;
    }

    public void execute(LuckyBlock luckyBlock, @Nullable Player player) {
        if(onBreakFunction != null) {
            onBreakFunction.accept(luckyBlock, player);
        }
    }

    public static boolean isValid(String name) {
        return name != null && BY_NAME.containsKey(name.toUpperCase());
    }

    public static LuckyBlockDrop getByName(String name) {
        return name == null ? null : BY_NAME.get(name.toUpperCase());
    }

    static {
        for (LuckyBlockDrop value : values) {
            BY_NAME.put(value.name(), value);
        }
    }
}