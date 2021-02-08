package com.mcgamer199.luckyblock.lb;

import com.google.common.collect.Iterators;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.customentity.*;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.listeners.DropEvents;
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

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum LBDrop {

    NONE(false),
    CHEST(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();

        String path = "Chests";
        String path1 = null;
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        ChestFiller chestFiller = new com.mcgamer199.luckyblock.tags.ChestFiller(luckyBlock.getFile().getConfigurationSection(path), chest);
        chestFiller.loc1 = path1;
        chestFiller.fill();
    }),
    ENTITY(true, (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = "Entities";
        String path1 = null;
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        if (path1 == null) {
            EntityTags.spawnRandomEntity(file.getConfigurationSection(path), luckyBlock.getBlock().getLocation(), player);
        } else {
            EntityTags.spawnEntity(file.getConfigurationSection(path).getConfigurationSection(path1), luckyBlock.getBlock().getLocation(), file.getConfigurationSection(path), true, player);
        }
    }),
    LAVA(true, (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setData((byte) 0);
        block.setType(Material.LAVA);
        block.getRelative(BlockFace.EAST).setType(Material.LAVA);
        block.getRelative(BlockFace.WEST).setType(Material.LAVA);
        block.getRelative(BlockFace.SOUTH).setType(Material.LAVA);
        block.getRelative(BlockFace.NORTH).setType(Material.LAVA);
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
            String path = "AddedItems";
            String path1 = null;
            if (luckyBlock.hasDropOption("Path")) {
                path = luckyBlock.getDropOption("Path").getValues()[0].toString();
            }

            if (luckyBlock.hasDropOption("Path1")) {
                path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
            }

            ItemStack[] items;
            if (path1 != null) {
                items = ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1));
            } else {
                items = ItemStackTags.getRandomItems(file.getConfigurationSection(path));
            }

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
        String path = "Structures";
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        String path1;
        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        } else {
            path1 = BlockTags.getRandomL(file, path);
        }

        String locationType = file.getString(String.format("%s.%s.%s", path, path1, "LocationType"), "BLOCK");

        if (locationType.equalsIgnoreCase("PLAYER") && player != null) {
            BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), player.getLocation());
        } else {
            BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), luckyBlock.getBlock().getLocation());
        }
    }),
    RANDOM_ITEM(true, (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        String path = "RandomItems";
        String path1 = null;
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        String key;
        if (path1 != null) {
            key = path1;
        } else {
            key = BlockTags.getRandomL(file, path);
        }

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
    LB_STRUCTURE(true, (luckyBlock, player) -> {
        if (luckyBlock.hasDropOption("Class")) {
            DropEvents.b(luckyBlock.getDropOption("Class").getValues()[0].toString(), luckyBlock.getBlock().getLocation());
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
        int t = 15;
        if (luckyBlock.hasDropOption("Times")) {
            t = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

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
        }).count(t).timer(2, 2);
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
        String path = "FallingBlocks";
        String path1 = null;
        double height = 10.0D;
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Height")) {
            height = Double.parseDouble(luckyBlock.getDropOption("Height").getValues()[0].toString());
        }

        if (path1 == null) {
            BlockTags.spawnRandomFallingBlock(file, path, luckyBlock.getBlock().getLocation().add(0.5D, height, 0.5D));
        } else {
            BlockTags.spawnFallingBlock(file, path, path1, luckyBlock.getBlock().getLocation().add(0.5D, height, 0.5D));
        }
    }),
    VILLAGER(true, new Properties().putInt("Seconds", 4), (luckyBlock, player) -> {
        int times = 4;
        if (luckyBlock.hasDropOption("Seconds")) {
            int fuse = Integer.parseInt(luckyBlock.getDropOption("Seconds").getValues()[0].toString());
            if (fuse > 0 && fuse < 1000) {
                times = fuse;
            }
        }

        EntityLuckyVillager villager = new EntityLuckyVillager();
        villager.seconds = times;
        villager.spawn(luckyBlock.getBlock().getLocation());
    }),
    SPLASH_POTION(true, new Properties().putString("Effects", "SPEED%200%0"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        ItemStack tpotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta tpotionM = (PotionMeta) tpotion.getItemMeta();
        tpotionM.setBasePotionData(new PotionData(PotionType.AWKWARD));
        if (luckyBlock.getDropOption("Effects") != null) {
            Object[] obj = luckyBlock.getDropOption("Effects").getValues();

            for (Object b : obj) {
                String serializedBlock = b.toString();
                if (serializedBlock != null) {
                    String[] t = serializedBlock.split("%");
                    if (PotionEffectType.getByName(t[0].toUpperCase()) != null) {

                        int du;
                        byte am;
                        try {
                            du = Integer.parseInt(t[1]);
                            am = Byte.parseByte(t[2]);
                        } catch (NumberFormatException var23) {
                            ColorsClass.send_no(player, "invalid_number");
                            return;
                        }

                        tpotionM.addCustomEffect(new PotionEffect(PotionEffectType.getByName(t[0].toUpperCase()), du, am), true);
                        tpotion.setItemMeta(tpotionM);
                        ThrownPotion potion = (ThrownPotion) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 10.0D, 0.0D), EntityType.SPLASH_POTION);
                        potion.setItem(tpotion);
                    } else {
                        ColorsClass.send_no(player, "drops.potion_effect.invalid_effect");
                    }
                }
            }
        }
    }),
    PRIMED_TNT(true, new Properties().putFloat("TntPower", 3F).putInt("Fuse", 50), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        float yield = 3.0F;
        int fuse = 50;
        if (luckyBlock.hasDropOption("TntPower")) {
            yield = Float.parseFloat(luckyBlock.getDropOption("TntPower").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Fuse")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
        }

        TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 20.0D, 0.0D), EntityType.PRIMED_TNT);
        tnt.setYield(yield);
        tnt.setFireTicks(2000);
        tnt.setFuseTicks(fuse);
        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
        if (!breakBlocks) {
            tnt.setMetadata("tnt", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
        }
    }),
    LIGHTNING(true, new Properties().putInt("Times", 10), (luckyBlock, player) -> {
        if (player != null) {
            Block block = luckyBlock.getBlock();
            int times = 10;
            if (luckyBlock.hasDropOption("Times")) {
                times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
            }

            Scheduler.create(() -> player.getWorld().strikeLightning(block.getLocation())).count(times).timer(0, 4);
        }
    }),
    FAKE_ITEM(true, new Properties().putString("ItemMaterial", "DIAMOND").putShort("ItemData", (short) 0).putInt("ItemAmount", 64).putInt("Ticks", 85), (luckyBlock, player) -> {
        if (luckyBlock.hasDropOption("ItemMaterial")) {
            Block block = luckyBlock.getBlock();
            Material mat = Material.getMaterial(luckyBlock.getDropOption("ItemMaterial").getValues()[0].toString());
            int itemAmount = 1;
            short data = 0;
            if (luckyBlock.hasDropOption("ItemAmount")) {
                itemAmount = Integer.parseInt(luckyBlock.getDropOption("ItemAmount").getValues()[0].toString());
            }

            if (luckyBlock.hasDropOption("ItemData")) {
                data = Short.parseShort(luckyBlock.getDropOption("ItemData").getValues()[0].toString());
            }

            Item item = block.getWorld().dropItem(block.getLocation(), new ItemStack(mat, itemAmount, data));
            item.setPickupDelay(32000);
            int counter = 85;
            if (luckyBlock.hasDropOption("Ticks")) {
                counter = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
            }

            if (counter > 1024) {
                counter = 1024;
            }

            Scheduler.later(item::remove, counter);
        }
    }),
    DROPPED_ITEMS(true, new Properties().putBoolean("Effects", false).putBoolean("ShowItemName", false), (luckyBlock, player) -> {
        FileConfiguration file = luckyBlock.getFile();
        Block block = luckyBlock.getBlock();
        String path = "DroppedItems";
        String path1 = null;
        if (luckyBlock.hasDropOption("Path")) {
            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        ItemStack[] items;
        if (path1 == null) {
            items = ItemStackTags.getRandomItems(file.getConfigurationSection(path));
        } else {
            items = ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1));
        }

        if (luckyBlock.hasDropOption("Effects") && luckyBlock.getDropOption("Effects").getValues()[0].toString().equalsIgnoreCase("true")) {
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
            for (ItemStack i : items) {
                if (i != null) {
                    Item droppedItem = block.getWorld().dropItem(block.getLocation(), i);
                    if (luckyBlock.hasDropOption("ShowItemName") && luckyBlock.getDropOption("ShowItemName").getValues()[0].toString().equalsIgnoreCase("true") && i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                        droppedItem.setCustomName(i.getItemMeta().getDisplayName());
                        droppedItem.setCustomNameVisible(true);
                    }
                }
            }
        }
    }),
    STUCK(true, new Properties().putInt("Duration", 15), (luckyBlock, player) -> { //TODO сделать слушатель
        if (player != null) {
            PlayerData.set(player, "stuck", true);

            int fuse = 10;
            if (luckyBlock.hasDropOption("Duration")) {
                fuse = Integer.parseInt(luckyBlock.getDropOption("Duration").getValues()[0].toString());
            }

            Scheduler.later(() -> PlayerData.remove(player, "stuck"), fuse * 10);
        }
    }),
    DAMAGE(true, new Properties().putInt("Value", 5), (luckyBlock, player) -> {
        if (player != null) {
            double damage = 2.5D;
            if (luckyBlock.hasDropOption("Value") && luckyBlock.getDropOption("Value").getValues()[0] != null) {
                damage = Integer.parseInt(luckyBlock.getDropOption("Value").getValues()[0].toString());
            }

            player.damage(damage);
        }
    }),
    TOWER(true, new Properties().putString("Type", "a"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        String path = "a";
        if (luckyBlock.hasDropOption("Type")) {
            path = luckyBlock.getDropOption("Type").getValues()[0].toString();
        }

        Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
        block.getWorld().playEffect(loc, Effect.POTION_BREAK, RandomUtils.nextInt(10) + 1);
        int[] i1 = tower_rblock(path);
        block.getWorld().spawnFallingBlock(loc.add(0.0D, 10.0D, 0.0D), i1[0], (byte) i1[1]).setDropItem(false);

        String finalPath = path;
        Scheduler.timer(new BukkitRunnable() {
            private int loop = RandomUtils.nextInt(4) + 6;

            @Override
            public void run() {
                if (this.loop > 1) {
                    int[] i2 = tower_rblock(finalPath);
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
            float explosionPower = 11.0F;
            if (luckyBlock.hasDropOption("ExplosionPower")) {
                explosionPower = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
            }

            float finalExplosionPower = explosionPower;
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
                        fallingBlock.getWorld().createExplosion(fallingBlock.getLocation().getBlockX(), fallingBlock.getLocation().getBlockY(), fallingBlock.getLocation().getBlockZ(), finalExplosionPower, setFire, breakBlocks);

                        fallingBlock.remove();
                        cancel();
                    }
                }
            }, 3, 3);
        }
    }),
    ELEMENTAL_CREEPER(true, new Properties().putString("BlockMaterial", "DIRT").putByte("BlockData", (byte) 0), (luckyBlock, player) -> {
        EntityElementalCreeper elementalCreeper = new EntityElementalCreeper();
        elementalCreeper.spawn(luckyBlock.getBlock().getLocation());
        if (luckyBlock.hasDropOption("BlockMaterial")) {
            Material blockMaterial = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
            elementalCreeper.changeMaterial(blockMaterial, elementalCreeper.getBlockData());
        }

        if (luckyBlock.hasDropOption("BlockData")) {
            byte data = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
            elementalCreeper.changeMaterial(elementalCreeper.getBlockMaterial(), data);
        }
    }),
    JAIL(true, new Properties().putInt("Ticks", 70), (luckyBlock, player) -> {
        if (player != null) {
            int times = 70;
            if (luckyBlock.hasDropOption("Ticks")) {
                times = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
                if (times < 0) {
                    times = 0;
                }

                if (times > 1024) {
                    times = 1024;
                }
            }

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
            Scheduler.later(() -> block.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.AIR), times);
        }
    }),
    TREE(true, new Properties().putString("TreeType", "TREE"), (luckyBlock, player) -> {
        if (luckyBlock.getDropOption("TreeType") != null) {
            Block block = luckyBlock.getBlock();
            TreeType type = TreeType.valueOf(luckyBlock.getDropOption("TreeType").getValues()[0].toString().toUpperCase());
            if (type == TreeType.CHORUS_PLANT) {
                block.getRelative(BlockFace.DOWN).setType(Material.ENDER_STONE);
            } else {
                block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
            }

            Scheduler.later(() -> block.getWorld().generateTree(block.getLocation(), type), 1);
        }
    }),
    SIGN(true, new Properties().putStringArray("Texts", new String[]{"&cHello", "&5How are you?"}).putString("Facing", "PLAYER"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.SIGN_POST);
        Sign sign = (Sign) block.getState();
        if (luckyBlock.hasDropOption("Facing")) {
            String path1 = luckyBlock.getDropOption("Facing").getValues()[0].toString();
            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
            if (path1.equalsIgnoreCase("PLAYER") && player != null) {
                signData.setFacingDirection(LocationUtils.getFacingBetween(block.getLocation(), player.getLocation()));
            } else {
                signData.setFacingDirection(BlockFace.valueOf(path1.toUpperCase()));
            }
        }

        if (luckyBlock.hasDropOption("Texts")) {
            Object[] texts = luckyBlock.getDropOption("Texts").getValues();

            for (int i = 0; i < texts.length; i++) {
                if(texts[i] != null) {
                    sign.setLine(i, texts[i].toString());
                }
            }
        }

        sign.update(true);
    }),
    REPAIR(true, new Properties().putString("RepairType", "all"), (luckyBlock, player) -> {
        if (player != null) {
            ColorsClass.send_no(player, "drops.repair.1");
            if (luckyBlock.hasDropOption("RepairType")) {
                String repairType = luckyBlock.getDropOption("RepairType").getValues()[0].toString();
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
        }
    }),
    ENCHANT(true, new Properties().putStringArray("Enchants", new String[]{"DURABILITY", "DAMAGE_ALL"}).putIntArray("Levels", new Integer[]{2, 1}), (luckyBlock, player) -> {
        if (player != null) {
            ItemStack heldItem = player.getInventory().getItem(EquipmentSlot.HAND);
            if(!ItemStackUtils.isNullOrAir(heldItem)) {
                ItemMeta meta = heldItem.getItemMeta();
                if (luckyBlock.hasDropOption("Enchants") && luckyBlock.hasDropOption("Levels")) {
                    String enchantmentName = RandomUtils.getRandomObject((String[]) luckyBlock.getDropOption("Enchants").getValues());
                    int enchantmentLevel = RandomUtils.getRandomObject((Integer[]) luckyBlock.getDropOption("Levels").getValues());
                    meta.addEnchant(Enchantment.getByName(enchantmentName.toUpperCase()), enchantmentLevel, true);
                    heldItem.setItemMeta(meta);
                    ColorsClass.send_no(player, "drops.enchant_item.success");
                    player.updateInventory();
                }
            } else {
                ColorsClass.send_no(player, "drops.enchant_item.fail");
            }
        }
    }),
    XP(true, new Properties().putInt("XPAmount", 75), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
        if (luckyBlock.hasDropOption("XPAmount")) {
            exp.setExperience(Integer.parseInt(luckyBlock.getDropOption("XPAmount").getValues()[0].toString()));
        }
    }),
    RUN_COMMAND(true, new Properties().putString("Commmand", "/say hello!"), (luckyBlock, player) -> {
        if (luckyBlock.getDropOption("Command") != null) {
            String path = (String) luckyBlock.getDropOption("Command").getValues()[0];
            if (path != null) {
                if (player != null) {
                    path = ChatColor.translateAlternateColorCodes('&', path);
                    path = path.replace("{playername}", player.getName());
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), path);
            }
        }
    }),
    CLEAR_EFFECTS(true, new Properties().putStringArray("Effects", new String[]{"SLOW", "SLOW_DIGGING", "HARM", "CONFUSION", "BLINDNESS", "HUNGER", "WEAKNESS", "POISON", "WITHER"}), (luckyBlock, player) -> {
        if (player != null && luckyBlock.hasDropOption("Effects")) {
            Object[] effects = luckyBlock.getDropOption("Effects").getValues();

            for (Object effect : effects) {
                player.removePotionEffect(PotionEffectType.getByName(effect.toString()));
            }
        }
    }),
    TELEPORT(true, new Properties().putInt("Height", 20), (luckyBlock, player) -> {
        if (player != null && luckyBlock.getDropOption("Height") != null) {
            player.teleport(player.getLocation().add(0.0D, Integer.parseInt(luckyBlock.getDropOption("Height").getValues()[0].toString()), 0.0D));
        }
    }),
    PERFORM_ACTION(true, new DropOption("ObjType", new String[0]), new DropOption("ActionName", new String[0]), new DropOption("ActionValue", new Object[0])),
    TNT_RAIN(true, new Properties().putInt("Times", 20).putInt("Fuse", 60), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        int times = 10;
        int fuse = 60;
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Fuse")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
        }

        int finalFuse = fuse;
        Scheduler.create(() -> {
            TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            tnt.setFuseTicks(finalFuse);
            int h = RandomUtils.nextInt(4) - 2;
            double g = (double) h / 10.0D;
            int h1 = RandomUtils.nextInt(4) - 2;
            double g1 = (double) h1 / 10.0D;
            tnt.setVelocity(new Vector(g, 1.0D, g1));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_tntrain"), 1.0F, 0.0F, 50);
        }).count(times).timer(5, 5);
    }),
    ITEM_RAIN(true, new Properties().putInt("Times", 20).putStringArray("ItemMaterials", new String[]{"EMERALD", "DIAMOND", "IRON_INGOT", "GOLD_INGOT", "GOLD_NUGGET"}).putShortArray("ItemsData", new Short[] {0}), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        int times = 10;
        Material[] materials = new Material[64];
        Short[] itemsData = new Short[16];
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("ItemMaterials")) {
            Object[] itemMaterials = luckyBlock.getDropOption("ItemMaterials").getValues();
            materials = new Material[itemMaterials.length];
            for (int i = 0; i < itemMaterials.length; i++) {
                if(itemMaterials[i] != null) {
                    materials[i] = Material.getMaterial(itemMaterials[i].toString());
                }
            }
        }

        if (luckyBlock.hasDropOption("ItemsData")) {
            itemsData = (Short[]) luckyBlock.getDropOption("ItemsData").getValues();
        }

        Iterator<Material> iterator = Arrays.asList(materials).iterator();
        Iterators.removeIf(iterator, Objects::isNull);
        short itemData = RandomUtils.getRandomObject(itemsData);

        Scheduler.create(() -> {
            Material itemMaterial = iterator.next();
            Item item = location.getWorld().dropItem(location, new ItemStack(itemMaterial, 1, itemData));
            item.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 20);
        }).predicate(iterator::hasNext).count(times).timer(2, 2);
    }),
    BLOCK_RAIN(true, new Properties().putInt("Times", 20).putStringArray("BlockMaterials", new String[]{"EMERALD_BLOCK", "GOLD_BLOCK", "LAPIS_BLOCK", "DIAMOND_BLOCK", "IRON_BLOCK"}), (luckyBlock, player) -> {
        int times = 10;
        Material[] materials = new Material[64];
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("BlockMaterials")) {
            Object[] obj = luckyBlock.getDropOption("BlockMaterials").getValues();
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
            FallingBlock fb = location.getWorld().spawnFallingBlock(location, used, (byte) 0);
            fb.setDropItem(false);
            int h = RandomUtils.nextInt(4) - 2;
            double g = (double) h / 5.0D;
            int h1 = RandomUtils.nextInt(4) - 2;
            double g1 = (double) h1 / 5.0D;
            fb.setVelocity(new Vector(g, 1.0D, g1));
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_blockrain_launch"), 1.0F, 1.0F, 50);

            Scheduler.timer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fb.isValid()) {
                        MaterialData d = new MaterialData(fb.getMaterial(), fb.getBlockData());
                        fb.getWorld().spawnParticle(Particle.BLOCK_CRACK, fb.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, d);
                        SoundUtils.playFixedSound(fb.getLocation(), SoundUtils.getSound("lb_drop_blockrain_land"), 1.0F, 1.0F, 60);
                        cancel();
                    }
                }
            }, 0, 2);
        }).count(times).timer(3, 3);
    }),
    ARROW_RAIN(true, new Properties().putInt("Times", 20).putBoolean("Critical", true).putBoolean("Bounce", true), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation().add(0.5, 0, 0.5);
        int times = 10;
        boolean critical = true;
        boolean bounce = true;
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Critical")) {
            critical = Boolean.parseBoolean(luckyBlock.getDropOption("Critical").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Bounce")) {
            bounce = Boolean.parseBoolean(luckyBlock.getDropOption("Bounce").getValues()[0].toString());
        }

        boolean finalCritical = critical;
        boolean finalBounce = bounce;
        Scheduler.create(() -> {
            Arrow a = (Arrow) location.getWorld().spawnEntity(location, EntityType.ARROW);
            a.setVelocity(new Vector((RandomUtils.nextInt(16) - 8) / 50.0D, 1.2D, (RandomUtils.nextInt(16) - 8) / 50.0D));
            a.setCritical(finalCritical);
            a.setBounce(finalBounce);
            SoundUtils.playFixedSound(location, SoundUtils.getSound("lb_drop_arrowrain"), 1.0F, 1.0F, 50);
        }).count(times).timer(1, 1);
    }),
    SET_NEARBY_BLOCKS(true, new Properties().putString("BlockMaterial", "DIAMOND_BLOCK").putByte("BlockData", (byte) 0).putInt("Range", 10).putInt("Delay", 8).putString("Mode", "SURFACE"), (luckyBlock, player) -> {
        if (luckyBlock.hasDropOption("BlockMaterial")) {
            Material material = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
            byte data = 0;
            int fuse = 10;
            int delay = 8;
            String mode = "surface";
            if (luckyBlock.hasDropOption("BlockData")) {
                data = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
            }

            if (luckyBlock.hasDropOption("Range")) {
                fuse = Integer.parseInt(luckyBlock.getDropOption("Range").getValues()[0].toString());
            }

            if (luckyBlock.hasDropOption("Delay")) {
                delay = Integer.parseInt(luckyBlock.getDropOption("Delay").getValues()[0].toString());
            }

            if (luckyBlock.hasDropOption("Mode")) {
                mode = luckyBlock.getDropOption("Mode").getValues()[0].toString();
            }

            if (fuse > 50) {
                fuse = 50;
            }

            if (fuse < 0) {
                fuse = 0;
            }

            if (mode.equalsIgnoreCase("all") && fuse > 32) {
                fuse = 32;
            }
            Location location = luckyBlock.getBlock().getLocation();
            byte finalData = data;
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
                                    l.getBlock().setData(finalData);
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
                                    l.getBlock().setData(finalData);
                                }
                            }
                        }
                    }

                    ++this.g;
                }
            };

            Scheduler.create(mode.equalsIgnoreCase("all") ? all : surface).count(fuse).timer(delay, delay);
        }
    }),
    SOUND(true, new Properties().putString("Listener", "PLAYER").putString("SoundName", "UI_BUTTON_CLICK"), (luckyBlock, player) -> {
        String clss = null;
        if (luckyBlock.getDropOption("Listener") != null) {
            if (luckyBlock.getDropOption("Listener").getValues()[0].toString().equalsIgnoreCase("player")) {
                clss = "player";
            } else if (luckyBlock.getDropOption("Listener").getValues()[0].toString().equalsIgnoreCase("nearby")) {
                clss = "nearby";
            }
        }

        if (clss.equalsIgnoreCase("player")) {
            if (player != null && luckyBlock.getDropOption("SoundName") != null) {
                player.playSound(player.getLocation(), Sound.valueOf(luckyBlock.getDropOption("SoundName").getValues()[0].toString().toUpperCase()), 1.0F, 1.0F);
            }
        } else if (clss.equalsIgnoreCase("nearby")) {
            SoundUtils.playFixedSound(luckyBlock.getBlock().getLocation(), Sound.valueOf(luckyBlock.getDropOption("SoundName").getValues()[0].toString().toUpperCase()), 1.0F, 1.0F, 30);
        }
    }),
    XP_RAIN(true, new Properties().putInt("Times", 32), (luckyBlock, player) -> {
        int times = 30;
        Location location = luckyBlock.getBlock().getLocation();
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        Scheduler.create(() -> {
            ThrownExpBottle xp = (ThrownExpBottle) location.getWorld().spawnEntity(location, EntityType.THROWN_EXP_BOTTLE);
            xp.setVelocity(new Vector((RandomUtils.nextInt(8) - 4) / 50.0D, 0.9D, (RandomUtils.nextInt(8) - 4) / 60.0D));
            xp.setBounce(true);
        }).count(times).timer(1, 2);
    }),
    SET_BLOCK(true, new Properties().putString("BlockMaterial", "DIAMOND_BLOCK"), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        byte blockType = 0;
        Material blockMaterial;
        if (luckyBlock.hasDropOption("BlockMaterial")) {
            blockMaterial = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
            if (blockMaterial != null) {
                block.setType(blockMaterial);
            } else if (player != null) {
                ColorsClass.send_no(player, "drops.setblock.invalid_material");
            }

            if (luckyBlock.hasDropOption("BlockData")) {
                blockType = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
                block.setData(blockType);
            }

            if (blockMaterial != null && luckyBlock.hasDropOption("ShowParticles") && luckyBlock.getDropOption("ShowParticles").getValues()[0].toString().equalsIgnoreCase("true")) {
                MaterialData md = new MaterialData(blockMaterial, blockType);
                block.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, md);
            }
        }
    }),
    FALLING_ANVILS(true, new Properties().putInt("Height", 20).putByte("AnvilData", (byte) 0).putString("LocationType", "PLAYER"), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        byte blockType = 8;
        int fuseTicks = 20;
        if (luckyBlock.hasDropOption("Height")) {
            fuseTicks = Integer.parseInt(luckyBlock.getDropOption("Height").getValues()[0].toString());
        }

        Location l = location.add(0.0D, fuseTicks, 0.0D);
        if (luckyBlock.hasDropOption("LocationType") && luckyBlock.getDropOption("LocationType").getValues()[0].toString().equalsIgnoreCase("player") && player != null) {
            l = player.getLocation().add(0.0D, fuseTicks, 0.0D);
        }

        if (luckyBlock.hasDropOption("AnvilData")) {
            blockType = Byte.parseByte(luckyBlock.getDropOption("AnvilData").getValues()[0].toString());
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
        int fuse = 64;
        if (luckyBlock.hasDropOption("Times")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        while (fuse > 0) {
            if (fuse > 63) {
                dispenser.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                fuse -= 64;
            } else {
                dispenser.getInventory().addItem(new ItemStack(Material.ARROW, fuse));
                fuse = 0;
            }
        }

        Scheduler.create(dispenser::dispense)
                .predicate(() -> dispenser.getBlock().getType().equals(Material.DISPENSER) && dispenser.getInventory().contains(Material.ARROW)).timer(3, 1);
    }),
    POTION_EFFECT(true, new Properties().putStringArray("Effects", new String[] {"SPEED%200%0"}), (luckyBlock, player) -> {
        if (player != null && luckyBlock.getDropOption("Effects") != null) {

            for (Object effect : luckyBlock.getDropOption("Effects").getValues()) {
                String s = effect.toString();
                if (s != null) {
                    String[] t = s.split("%");
                    if (PotionEffectType.getByName(t[0].toUpperCase()) != null) {

                        byte amplifier;
                        int duration;
                        try {
                            duration = Integer.parseInt(t[1]);
                            amplifier = Byte.parseByte(t[2]);
                        } catch (NumberFormatException var20) {
                            ColorsClass.send_no(player, "invalid_number");
                            return;
                        }

                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(t[0].toUpperCase()), duration, amplifier));
                    } else {
                        ColorsClass.send_no(player, "drops.potion_effect.invalid_effect");
                    }
                }
            }
        }
    }),
    DAMAGE_1(true, new Properties().putInt("Times", 30).putInt("Ticks", 11), (luckyBlock, player) -> {
        if (player != null && luckyBlock.getDropOption("Times") != null) {
            int ticks = 11;
            if (luckyBlock.hasDropOption("Ticks")) {
                ticks = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
            }

            Scheduler.create(() -> {
                if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && player.getHealth() > 0.0D) {
                    player.setHealth(Math.max(0.0D, player.getHealth() - 1.0D));
                }
            }).count(Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString())).timer(ticks, ticks);
        }
    }),
    FIRE(true, new Properties().putInt("Range", 6), (luckyBlock, player) -> {
        Location location = luckyBlock.getBlock().getLocation();
        int ticks = 10;
        if (luckyBlock.hasDropOption("Range")) {
            ticks = Integer.parseInt(luckyBlock.getDropOption("Range").getValues()[0].toString());
        }

        if (ticks > 50) {
            ticks = 50;
        }

        if (ticks < 0) {
            ticks = 0;
        }

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
        }).count(ticks).timer(3, 20);
    }),
    EXPLOSION(true, new Properties().putFloat("ExplosionPower", 4.0F), (luckyBlock, player) -> {
        float power = 4.0F;
        if (luckyBlock.hasDropOption("ExplosionPower")) {
            power = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
        }

        luckyBlock.getBlock().getLocation().getWorld().createExplosion(luckyBlock.getBlock().getLocation(), power);
    }),
    LAVA_HOLE(true, new Properties().putByte("Radius", (byte) 2).putBoolean("WithWebs", true).putString("BordersMaterial", "STONE").putByte("BordersData", (byte) 0).putStringArray("Texts", new String[]{"Say goodbye :)"}), (luckyBlock, player) -> {
        if (player != null) {
            int playerX = player.getLocation().getBlockX();
            int playerY = player.getLocation().getBlockY();
            int playerZ = player.getLocation().getBlockZ();
            byte rad = 2;
            boolean cobs = true;
            Material ma = Material.STONE;
            byte data = 0;
            if (luckyBlock.hasDropOption("BordersMaterial")) {
                ma = Material.getMaterial(luckyBlock.getDropOption("BordersMaterial").getValues()[0].toString().toUpperCase());
            }
            if (luckyBlock.hasDropOption("BordersData")) {
                data = Byte.parseByte(luckyBlock.getDropOption("BordersData").getValues()[0].toString());
            }
            if ((luckyBlock.hasDropOption("WithWebs")) &&
                    (luckyBlock.getDropOption("WithWebs").getValues()[0].toString().equalsIgnoreCase("false"))) {
                cobs = false;
            }
            if (luckyBlock.hasDropOption("Radius")) {
                rad = Byte.parseByte(luckyBlock.getDropOption("Radius").getValues()[0].toString());
            }
            if (rad > 48) {
                rad = 48;
            }
            for (int x = rad * -1 - 1; x < rad + 2; x++) {
                for (int z = rad * -1 - 1; z < rad + 2; z++) {
                    for (int y = playerY - 1; y > 0; y--)
                    {
                        Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                        loc.getBlock().setType(ma);
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
            if (cobs) {
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
            if (luckyBlock.hasDropOption("Texts")) {
                Object[] text = luckyBlock.getDropOption("Texts").getValues();
                for (int x = 0; x < text.length; x++) {
                    if (text[x] != null) {
                        sign.setLine(x, ChatColor.translateAlternateColorCodes('&', text[x].toString()));
                    }
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
    VOID_HOLE(true, new Properties().putByte("Radius", (byte) 2).putString("BordersMaterial", "AIR").putByte("BordersData", (byte) 0), (luckyBlock, player) -> {
        if (player != null) {
            byte rad = 2;
            Material mat = Material.AIR;
            byte data = 0;
            if (luckyBlock.hasDropOption("BordersMaterial")) {
                mat = Material.getMaterial(luckyBlock.getDropOption("BordersMaterial").getValues()[0].toString().toUpperCase());
            }
            if (luckyBlock.hasDropOption("BordersData")) {
                data = Byte.parseByte(luckyBlock.getDropOption("BordersData").getValues()[0].toString());
            }
            if (luckyBlock.hasDropOption("Radius")) {
                rad = Byte.parseByte(luckyBlock.getDropOption("Radius").getValues()[0].toString());
            }
            if (rad > 48) {
                rad = 48;
            }
            int playerX = player.getLocation().getBlockX();
            int playerY = player.getLocation().getBlockY();
            int playerZ = player.getLocation().getBlockZ();
            if (mat != Material.AIR) {
                for (int x = rad * -1 - 1; x < rad + 2; x++) {
                    for (int z = rad * -1 - 1; z < rad + 2; z++) {
                        for (int y = playerY; y > -1; y--)
                        {
                            Location loc = new Location(player.getLocation().getWorld(), playerX + x, y, playerZ + z);
                            loc.getBlock().setType(mat);
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
        int fuse = 64;
        if (luckyBlock.hasDropOption("Times")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        while (fuse > 0) {
            if (fuse > 63) {
                dropper.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
                fuse -= 64;
            } else {
                dropper.getInventory().addItem(new ItemStack(Material.DIAMOND, fuse));
                fuse = 0;
            }
        }

        Scheduler.create(dropper::drop)
                .predicate(() -> dropper.getBlock().getType().equals(Material.DROPPER) && dropper.getInventory().contains(Material.DIAMOND)).timer(3, 3);
    }),
    EXPLOSIVE_CHEST(true, new Properties().putInt("Ticks", 50).putBoolean("ClearInventory", true), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        block.setType(Material.CHEST);
        int times = 50;
        String path1 = "Chests";
        String s = null;
        boolean clearInventory = true;
        if (luckyBlock.hasDropOption("Path")) {
            path1 = luckyBlock.getDropOption("Path").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("Path1")) {
            s = luckyBlock.getDropOption("Path1").getValues()[0].toString();
        }

        if (luckyBlock.hasDropOption("ClearInventory") && luckyBlock.getDropOption("ClearInventory").getValues()[0].toString().equalsIgnoreCase("false")) {
            clearInventory = false;
        }

        if (luckyBlock.hasDropOption("Ticks")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
            if (times > 1024) {
                times = 1024;
            }

            if (times < 0) {
                times = 0;
            }
        }

        if (path1 != null && s != null) {
            ChestFiller chestFiller = new ChestFiller(luckyBlock.getFile().getConfigurationSection(path1), (Chest) block.getState());
            chestFiller.loc1 = s;
            chestFiller.fill();
        }

        final Chest c = (Chest) block.getState();
        boolean finalBreakBlocks = clearInventory;
        Scheduler.later(() -> {
            if (finalBreakBlocks) {
                c.getBlockInventory().clear();
            }

            block.getWorld().createExplosion(block.getLocation(), 3.5F);
        }, times);
    }),
    ADD_LEVEL(true, new Properties().putInt("Amount", 10), (luckyBlock, player) -> {
        if (player != null && luckyBlock.hasDropOption("Amount")) {
            player.giveExpLevels(Integer.parseInt(luckyBlock.getDropOption("Amount").getValues()[0].toString()));
        }
    }),
    ADD_XP(true, new Properties().putInt("Amount", 550), (luckyBlock, player) -> {
        if (player != null && luckyBlock.hasDropOption("Amount")) {
            player.giveExp(Integer.parseInt(luckyBlock.getDropOption("Amount").getValues()[0].toString()));
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
                int[] i = new int[3];
                if (luckyBlock.hasDropOption("Loc") && luckyBlock.getDropOption("Loc").getValues().length == 3) {
                    Object[] a = luckyBlock.getDropOption("Loc").getValues();
                    i[0] = Integer.parseInt(a[0].toString());
                    i[1] = Integer.parseInt(a[1].toString());
                    i[2] = Integer.parseInt(a[2].toString());
                }

                String file = luckyBlock.getDropOption("File").getValues()[0].toString();
                File fi = new File(LuckyBlockPlugin.d() + "Drops/" + file + ".schematic");
                String s = luckyBlock.getDropOption("LocationType").getValues()[0].toString();
                Location location = null;
                if (s.equalsIgnoreCase("PLAYER")) {
                    if (player != null) {
                        location = player.getLocation();
                    }
                } else if (s.equalsIgnoreCase("BLOCK")) {
                    location = luckyBlock.getBlock().getLocation();
                }

                if (location != null) {
                    Schematic.loadArea(fi, location.add(i[0], i[1], i[2]));
                }
            }
        } else if (player != null) {
            ColorsClass.send(player, "schematic_error");
        }
    }),
    TNT_IN_THE_MIDDLE(true, new Properties().putString("BlocksMaterial", "DIAMOND_BLOCK").putByte("BlocksData", (byte) 0).putInt("Fuse", 65).putFloat("ExplosionPower", 5F), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        Material material = Material.DIAMOND_BLOCK;
        byte data = 0;
        int fuse = 65;
        float power = 5.0F;
        if (luckyBlock.hasDropOption("BlocksMaterial")) {
            material = Material.getMaterial(luckyBlock.getDropOption("BlocksMaterial").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("BlocksData")) {
            data = Byte.parseByte(luckyBlock.getDropOption("BlocksData").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Fuse")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("ExplosionPower")) {
            power = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
        }

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
        tnt.setFuseTicks(fuse);
        tnt.setYield(power);
    }),
    FLYING_TNTS(true, new Properties().putInt("Times", 8).putInt("Fuse", 80), (luckyBlock, player) -> {
        Block block = luckyBlock.getBlock();
        int fuse = 80;
        int t = 8;
        if (luckyBlock.hasDropOption("Times")) {
            t = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        if (luckyBlock.hasDropOption("Fuse")) {
            fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
        }

        for (fuse = 8; fuse > 0; --fuse) {
            Bat bat = (Bat) block.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
            bat.setHealth(0.5D);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
            TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(fuse);
            bat.addPassenger(tnt);
        }
    }),
    ANVIL_JAIL(true, new Properties().putDouble("Height", 35D), (luckyBlock, player) -> {
        if (player != null) {
            double h = 35.0D;
            if (luckyBlock.hasDropOption("Height")) {
                h = Double.parseDouble(luckyBlock.getDropOption("Height").getValues()[0].toString());
            }

            Block b = player.getLocation().getBlock();
            jail(b);
            b.getWorld().spawnFallingBlock(player.getLocation().add(0.0D, h, 0.0D), new MaterialData(Material.ANVIL));
        }
    }),
    LAVA_JAIL(true, new Properties().putInt("Ticks", 55), (luckyBlock, player) -> {
        if (player != null) {
            int t = 55;
            if (luckyBlock.hasDropOption("Ticks")) {
                t = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
            }

            Block b = player.getLocation().getBlock();
            jail(b);
            Scheduler.later(() -> luckyBlock.getBlock().getLocation().getBlock().setType(Material.LAVA), t);
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

    private com.mcgamer199.luckyblock.lb.DropOption[] defaultOptions = new com.mcgamer199.luckyblock.lb.DropOption[64];
    private Properties dropOptions;
    private boolean visible;
    private LBDrop.LBFunction function;
    private BiConsumer<LuckyBlock, Player> onBreakFunction;

    LBDrop(boolean visible, Properties dropOptions, BiConsumer<LuckyBlock, Player> onBreakFunction) {
        this.visible = visible;
        this.dropOptions = dropOptions;
        this.onBreakFunction = onBreakFunction;
    }

    LBDrop(boolean visible, Properties dropOptions) {
        this.visible = visible;
        this.dropOptions = dropOptions;
    }

    LBDrop(boolean visible, LBDrop.LBFunction function, com.mcgamer199.luckyblock.lb.DropOption... defaultOptions) {
        this.defaultOptions = defaultOptions;
        this.visible = visible;
        this.function = function;
    }

    LBDrop(boolean visible, com.mcgamer199.luckyblock.lb.DropOption... defaultOptions) {
        this.defaultOptions = defaultOptions;
        this.visible = visible;
    }

    LBDrop(boolean visible) {
        this.visible = visible;
    }

    LBDrop(boolean visible, BiConsumer<LuckyBlock, Player> onBreakFunction) {
        this.visible = visible;
        this.onBreakFunction = onBreakFunction;
    }

    LBDrop(int id, List<com.mcgamer199.luckyblock.lb.DropOption> defaultOptions) {
        for (int x = 0; x < defaultOptions.size(); ++x) {
            this.defaultOptions[x] = defaultOptions.get(x);
        }

    }

    public static boolean isValid(String name) {
        LBDrop[] var4;
        int var3 = (var4 = values()).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            LBDrop drop = var4[var2];
            if (drop.name().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static LBDrop getByName(String name) {
        LBDrop[] var4;
        int var3 = (var4 = values()).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            LBDrop drop = var4[var2];
            if (drop.name().equalsIgnoreCase(name)) {
                return drop;
            }
        }

        return null;
    }

    public DropOption[] getDefaultOptions() {
        return this.defaultOptions;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public LBDrop.LBFunction getFunction() {
        return this.function;
    }

    public interface LBFunction {
        void function(LuckyBlock var1, Player var2);
    }

    private static int[] tower_rblock(String type) {
        int[] i = new int[]{Material.STAINED_CLAY.getId(), RandomUtils.nextInt(15)};
        if (type.equalsIgnoreCase("b")) {
            i = new int[]{Material.GLASS.getId(), 0};
        } else if (type.equalsIgnoreCase("c")) {
            i = new int[]{getRandomMaterial(Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.EMERALD_BLOCK).getId(), 0};
        } else if (type.equalsIgnoreCase("d")) {
            i = new int[]{Material.SANDSTONE.getId(), RandomUtils.nextInt(3)};
        } else if (type.equalsIgnoreCase("e")) {
            i = new int[]{Material.WOOL.getId(), RandomUtils.nextInt(16)};
        } else if (type.equalsIgnoreCase("f")) {
            i = new int[]{Material.WOOD.getId(), RandomUtils.nextInt(6)};
        }

        return i;
    }

    private static Material getRandomMaterial(Material... mats) {
        return RandomUtils.getRandomObject(mats);
    }

    private static void jail(Block b) {
        int y;
        int x;
        for (y = -1; y < 2; ++y) {
            for (x = -1; x < 2; ++x) {
                b.getLocation().add(y, -1.0D, x).getBlock().setType(Material.SMOOTH_BRICK);
            }
        }

        for (y = 0; y < 2; ++y) {
            for (x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    if (x != 0 || z != 0) {
                        b.getLocation().add(x, y, z).getBlock().setType(Material.IRON_FENCE);
                    }
                }
            }
        }

    }
}