package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.customentity.EntityLuckyVillager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.IDirection;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.structures.Structure;
import com.mcgamer199.luckyblock.tags.BlockTags;
import com.mcgamer199.luckyblock.util.*;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class DropEvents extends ColorsClass {
    public static final int maxTicks = 1024;
    static final List<Material> repairable;

    static {
        repairable = Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_SPADE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.GOLD_SWORD, Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_PICKAXE, Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.IRON_SWORD, Material.IRON_AXE, Material.IRON_SPADE, Material.IRON_PICKAXE, Material.DIAMOND_HOE, Material.GOLD_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.WOOD_HOE, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.STONE_SWORD, Material.STONE_AXE, Material.STONE_SPADE, Material.STONE_PICKAXE, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.WOOD_SWORD, Material.WOOD_AXE, Material.WOOD_SPADE, Material.WOOD_PICKAXE, Material.FISHING_ROD, Material.ELYTRA, Material.BOW, Material.FLINT_AND_STEEL, Material.CARROT_STICK);
    }

    public DropEvents() {
    }

    static void run(final Block block, LuckyBlock luckyBlock, Player player, LBDrop drop, CustomDrop customDrop, boolean first) {
        Location bloc = block.getLocation();
        FileConfiguration file = luckyBlock.getFile();
        if (player != null && luckyBlock.hasDropOption("Message")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) luckyBlock.getDropOption("Message").getValues()[0]));
        }

        String[] objs;
        int fuse;
        if (luckyBlock.hasDropOption("IParticles")) {
            objs = (String[]) luckyBlock.getDropOption("IParticles").getValues();

            for (fuse = 0; fuse < objs.length; ++fuse) {
                if (objs[fuse] != null) {
                    BreakLuckyBlock.spawnParticle(bloc, objs[fuse]);
                }
            }
        }

        String path;
        if (player == null && luckyBlock.hasDropOption("Player")) {
            path = luckyBlock.getDropOption("Player").getValues()[0].toString();
            if (Bukkit.getPlayer(path) != null) {
                player = Bukkit.getPlayer(path);
            }
        }

        File drops;
        if (luckyBlock.hasDropOption("File")) {
            path = luckyBlock.getDropOption("File").getValues()[0].toString();
            drops = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "Drops/" + path);
            if (drops.exists()) {
                file = YamlConfiguration.loadConfiguration(drops);
            }
        }

        int randomP;
        int x;
        String path1;
        if (luckyBlock.hasDropOption("With") && first) {
            objs = (String[]) luckyBlock.getDropOption("With").getValues();
            String[] var12 = objs;
            x = objs.length;

            for (randomP = 0; randomP < x; ++randomP) {
                path1 = var12[randomP];
                if (path1 != null) {
                    if (CustomDropManager.getByName(path1) != null) {
                        run(block, luckyBlock, player, null, CustomDropManager.getByName(path1), false);
                    } else {
                        run(block, luckyBlock, player, LBDrop.getByName(path1), null, false);
                    }
                }
            }
        }

        if (LuckyBlockPlugin.isDebugEnabled()) {
            Debug("Lucky block broken", new DebugData("Player", player != null ? player.getName() : "none"), new DebugData("Location", LocationUtils.asString(bloc)), new DebugData("LBType", luckyBlock.getType().getId() + ", " + ChatColor.stripColor(luckyBlock.getType().getName())), new DebugData("Placed By", luckyBlock.getPlacedByClass()), new DebugData("Title", luckyBlock.hasDropOption("Title") ? ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOption("Title").getValues()[0].toString())) : "unknown"), new DebugData("Drop Type", luckyBlock.customDrop != null ? luckyBlock.customDrop.getName() : luckyBlock.getDrop().name()), new DebugData("Luck", String.valueOf(luckyBlock.getLuck())), new DebugData("Owner", luckyBlock.hasOwner() ? luckyBlock.owner.toString() : "none"));
        }

        if (customDrop == null) {
            if (drop != null) {
                if (drop.getFunction() != null) {
                    drop.getFunction().function(luckyBlock, player);
                }

                com.mcgamer199.luckyblock.tags.ChestFiller chestFiller;
                if (drop == LBDrop.CHEST) {
                    block.setType(Material.CHEST);
                    path = "Chests";
                    path1 = null;
                    if (luckyBlock.hasDropOption("Path")) {
                        path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                    }

                    if (luckyBlock.hasDropOption("Path1")) {
                        path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                    }

                    if (block.getState() instanceof Chest) {
                        chestFiller = new com.mcgamer199.luckyblock.tags.ChestFiller(file.getConfigurationSection(path), (Chest) block.getState());
                        chestFiller.loc1 = path1;
                        chestFiller.fill();
                    }
                } else {
                    int times;
                    String s;
                    boolean breakBlocks;
                    if (drop == LBDrop.EXPLOSIVE_CHEST) {
                        block.setType(Material.CHEST);
                        times = 50;
                        path1 = "Chests";
                        s = null;
                        breakBlocks = true;
                        if (luckyBlock.hasDropOption("Path")) {
                            path1 = luckyBlock.getDropOption("Path").getValues()[0].toString();
                        }

                        if (luckyBlock.hasDropOption("Path1")) {
                            s = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                        }

                        if (luckyBlock.hasDropOption("ClearInventory") && luckyBlock.getDropOption("ClearInventory").getValues()[0].toString().equalsIgnoreCase("false")) {
                            breakBlocks = false;
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
                            com.mcgamer199.luckyblock.tags.ChestFiller c = new com.mcgamer199.luckyblock.tags.ChestFiller(file.getConfigurationSection(path1), (Chest) block.getState());
                            c.loc1 = s;
                            c.fill();
                        }

                        final Chest c = (Chest) block.getState();
                        boolean finalBreakBlocks = breakBlocks;
                        Scheduler.later(() -> {
                            if (finalBreakBlocks) {
                                c.getBlockInventory().clear();
                            }

                            block.getWorld().createExplosion(block.getLocation(), 3.5F);
                        }, times);
                    } else if (drop == LBDrop.FALLING_BLOCK) {
                        path = "FallingBlocks";
                        path1 = null;
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
                            com.mcgamer199.luckyblock.tags.BlockTags.spawnRandomFallingBlock(file, path, bloc.add(0.5D, height, 0.5D));
                        } else {
                            com.mcgamer199.luckyblock.tags.BlockTags.spawnFallingBlock(file, path, path1, bloc.add(0.5D, height, 0.5D));
                        }
                    } else if (drop == LBDrop.ENTITY) {
                        path = "Entities";
                        path1 = null;
                        if (luckyBlock.hasDropOption("Path")) {
                            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                        }

                        if (luckyBlock.hasDropOption("Path1")) {
                            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                        }

                        if (path1 == null) {
                            com.mcgamer199.luckyblock.tags.EntityTags.spawnRandomEntity(file.getConfigurationSection(path), bloc, player);
                        } else {
                            com.mcgamer199.luckyblock.tags.EntityTags.spawnEntity(file.getConfigurationSection(path).getConfigurationSection(path1), bloc, file.getConfigurationSection(path), true, player);
                        }
                    } else if (drop == LBDrop.LAVA) {
                        block.setData((byte) 0);
                        block.setType(Material.LAVA);
                        block.getRelative(BlockFace.EAST).setType(Material.LAVA);
                        block.getRelative(BlockFace.WEST).setType(Material.LAVA);
                        block.getRelative(BlockFace.SOUTH).setType(Material.LAVA);
                        block.getRelative(BlockFace.NORTH).setType(Material.LAVA);
                    } else if (drop == LBDrop.VILLAGER) {
                        times = 4;
                        if (luckyBlock.hasDropOption("Seconds")) {
                            fuse = Integer.parseInt(luckyBlock.getDropOption("Seconds").getValues()[0].toString());
                            if (fuse > 0 && fuse < 1000) {
                                times = fuse;
                            }
                        }

                        com.mcgamer199.luckyblock.customentity.EntityLuckyVillager villager = new EntityLuckyVillager();
                        villager.seconds = times;
                        villager.spawn(bloc);
                    } else {
                        int rp;
                        int counter;
                        Object[] obj;
                        if (drop == LBDrop.SPLASH_POTION) {
                            ItemStack tpotion = new ItemStack(Material.SPLASH_POTION);
                            PotionMeta tpotionM = (PotionMeta) tpotion.getItemMeta();
                            tpotionM.setBasePotionData(new PotionData(PotionType.AWKWARD));
                            if (luckyBlock.getDropOption("Effects") != null) {
                                obj = luckyBlock.getDropOption("Effects").getValues();
                                Object[] var14 = obj;
                                rp = obj.length;

                                for (counter = 0; counter < rp; ++counter) {
                                    Object b = var14[counter];
                                    String serializedBlock = b.toString();
                                    if (serializedBlock != null) {
                                        String[] t = serializedBlock.split("%");
                                        if (PotionEffectType.getByName(t[0].toUpperCase()) != null) {
                                            boolean var18 = false;

                                            int du;
                                            byte am;
                                            try {
                                                du = Integer.parseInt(t[1]);
                                                am = Byte.parseByte(t[2]);
                                            } catch (NumberFormatException var23) {
                                                send_no(player, "invalid_number");
                                                return;
                                            }

                                            tpotionM.addCustomEffect(new PotionEffect(PotionEffectType.getByName(t[0].toUpperCase()), du, am), true);
                                            tpotion.setItemMeta(tpotionM);
                                            ThrownPotion potion = (ThrownPotion) block.getWorld().spawnEntity(bloc.add(0.0D, 10.0D, 0.0D), EntityType.SPLASH_POTION);
                                            potion.setItem(tpotion);
                                        } else {
                                            send_no(player, "drops.potion_effect.invalid_effect");
                                        }
                                    }
                                }
                            }
                        } else if (drop == LBDrop.PRIMED_TNT) {
                            float yield = 3.0F;
                            fuse = 50;
                            if (luckyBlock.hasDropOption("TntPower")) {
                                yield = Float.parseFloat(luckyBlock.getDropOption("TntPower").getValues()[0].toString());
                            }

                            if (luckyBlock.hasDropOption("Fuse")) {
                                fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
                            }

                            TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(bloc.add(0.0D, 20.0D, 0.0D), EntityType.PRIMED_TNT);
                            tnt.setYield(yield);
                            tnt.setFireTicks(2000);
                            tnt.setFuseTicks(fuse);
                            breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                            if (!breakBlocks) {
                                tnt.setMetadata("tnt", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
                            }
                        } else if (drop == LBDrop.LIGHTNING) {
                            if (player != null) {
                                times = 10;
                                if (luckyBlock.hasDropOption("Times")) {
                                    times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                }

                                HTasks.LightningR(player, block, times);
                            }
                        } else if (drop == LBDrop.FAKE_ITEM) {
                            if (luckyBlock.hasDropOption("ItemMaterial")) {
                                Material mat = Material.getMaterial(luckyBlock.getDropOption("ItemMaterial").getValues()[0].toString());
                                fuse = 1;
                                short data = 0;
                                if (luckyBlock.hasDropOption("ItemAmount")) {
                                    fuse = Integer.parseInt(luckyBlock.getDropOption("ItemAmount").getValues()[0].toString());
                                }

                                if (luckyBlock.hasDropOption("ItemData")) {
                                    data = Short.parseShort(luckyBlock.getDropOption("ItemData").getValues()[0].toString());
                                }

                                Item item = block.getWorld().dropItem(block.getLocation(), new ItemStack(mat, fuse, data));
                                item.setPickupDelay(32000);
                                counter = 85;
                                if (luckyBlock.hasDropOption("Ticks")) {
                                    counter = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
                                }

                                if (counter > 1024) {
                                    counter = 1024;
                                }

                                HTasks.FakeDiamond(item, counter);
                            }
                        } else if (drop == LBDrop.FIREWORK) {
                            final Firework fwork = (Firework) block.getWorld().spawnEntity(block.getLocation(), EntityType.FIREWORK);
                            FireworkMeta fwm = fwork.getFireworkMeta();
                            Random r = new Random();
                            Type type = Type.BALL_LARGE;
                            FireworkEffect f = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(Color.GREEN).withColor(Color.RED).withColor(Color.YELLOW).withFade(Color.AQUA).with(type).trail(r.nextBoolean()).build();
                            fwm.clearEffects();
                            fwm.addEffect(f);
                            rp = r.nextInt(2) + 1;
                            fwm.setPower(rp);
                            fwork.setFireworkMeta(fwm);
                            Scheduler.later(() -> {
                                int trap = LuckyBlockPlugin.randoms.nextInt(2);
                                if (trap > 0) {
                                    TNTPrimed t = (TNTPrimed) fwork.getWorld().spawnEntity(fwork.getLocation(), EntityType.PRIMED_TNT);

                                    try {
                                        boolean allowExplosionGrief = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                                        if (!allowExplosionGrief) {
                                            t.setMetadata("tnt", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
                                        }
                                    } catch (Exception var4) {
                                        var4.printStackTrace();
                                    }

                                    t.setFuseTicks(70);
                                    t.setYield(5.0F);
                                } else if (trap == 0) {
                                    FallingBlock tntfake = fwork.getWorld().spawnFallingBlock(fwork.getLocation().add(0.5D, 0.0D, 0.5D), Material.TNT, (byte) 0);
                                    tntfake.setDropItem(false);
                                }
                            }, 60);
                        } else {
                            ItemStack i;
                            ItemStack[] var46;
                            ItemStack[] items;
                            if (drop == LBDrop.DROPPED_ITEMS) {
                                path = "DroppedItems";
                                path1 = null;
                                if (luckyBlock.hasDropOption("Path")) {
                                    path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                                }

                                if (luckyBlock.hasDropOption("Path1")) {
                                    path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                                }

                                items = null;
                                if (path1 == null) {
                                    items = com.mcgamer199.luckyblock.tags.ItemStackTags.getRandomItems(file.getConfigurationSection(path));
                                } else {
                                    items = com.mcgamer199.luckyblock.tags.ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1));
                                }

                                if (luckyBlock.hasDropOption("Effects") && luckyBlock.getDropOption("Effects").getValues()[0].toString().equalsIgnoreCase("true")) {
                                    HTasks.d_Item(items, bloc, luckyBlock);
                                } else {
                                    var46 = items;
                                    rp = items.length;

                                    for (counter = 0; counter < rp; ++counter) {
                                        i = var46[counter];
                                        if (i != null) {
                                            Item droppedItem = block.getWorld().dropItem(bloc, i);
                                            if (luckyBlock.hasDropOption("ShowItemName") && luckyBlock.getDropOption("ShowItemName").getValues()[0].toString().equalsIgnoreCase("true") && i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                                                droppedItem.setCustomName(i.getItemMeta().getDisplayName());
                                                droppedItem.setCustomNameVisible(true);
                                            }
                                        }
                                    }
                                }
                            } else if (drop == LBDrop.STUCK) {
                                if (player != null) {
                                    Location loc = player.getLocation();
                                    fuse = 10;
                                    if (luckyBlock.hasDropOption("Duration")) {
                                        fuse = Integer.parseInt(luckyBlock.getDropOption("Duration").getValues()[0].toString());
                                    }

                                    HTasks.STUCK(player, loc, fuse);
                                }
                            } else if (drop == LBDrop.DAMAGE) {
                                if (player != null) {
                                    double damage = 2.5D;
                                    if (luckyBlock.hasDropOption("Value") && luckyBlock.getDropOption("Value").getValues()[0] != null) {
                                        try {
                                            damage = Integer.parseInt(luckyBlock.getDropOption("Value").getValues()[0].toString());
                                        } catch (Exception var22) {
                                        }
                                    }

                                    player.damage(damage);
                                }
                            } else if (drop == LBDrop.TOWER) {
                                path = "a";
                                if (luckyBlock.hasDropOption("Type")) {
                                    path = luckyBlock.getDropOption("Type").getValues()[0].toString();
                                }

                                HTasks.Tower(block, LuckyBlockPlugin.randoms.nextInt(10) + 1, path);
                            } else {
                                if (drop == LBDrop.F_PIGS) {
                                    for (times = LuckyBlockPlugin.randoms.nextInt(5) + 4; times > 0; --times) {
                                        final Bat bat = (Bat) block.getWorld().spawnEntity(block.getLocation().add(0.0D, 0.0D, 0.0D), EntityType.BAT);
                                        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                                        final Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
                                        if (LuckyBlockPlugin.randoms.nextInt(2) + 1 == 1) {
                                            pig.setCustomName(yellow + "Lucky Pig " + green + "+1 Health");
                                        } else {
                                            pig.setCustomName(yellow + "Lucky Pig " + green + "+2 Health");
                                        }

                                        pig.setCustomNameVisible(true);
                                        bat.setPassenger(pig);
                                        bat.setMetadata("luckybat", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));

                                        Scheduler.timer(new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (!pig.isDead() && !bat.isDead()) {
                                                    pig.getWorld().spawnParticle(Particle.REDSTONE, pig.getLocation(), 100, 0.3D, 0.3D, 0.3D, 1.0D);
                                                } else {
                                                    cancel();
                                                }
                                            }
                                        }, 2, 2);
                                    }
                                } else if (drop == LBDrop.SLIMES) {
                                    times = (new Random()).nextInt(3) + 1;
                                    com.mcgamer199.luckyblock.customentity.SuperSlime ss = new com.mcgamer199.luckyblock.customentity.SuperSlime();
                                    randomP = (new Random()).nextInt(3) + 3;

                                    for (counter = randomP; counter > 0; --counter) {
                                        com.mcgamer199.luckyblock.customentity.EntitySuperSlime su = new com.mcgamer199.luckyblock.customentity.EntitySuperSlime();
                                        su.setSize(times);
                                        su.spawn(bloc);
                                        ss.add(su);
                                    }

                                    ss.ride();
                                } else {
                                    FallingBlock fb;
                                    if (drop == LBDrop.METEORS) {
                                        for (times = 8; times > 0; --times) {
                                            fb = block.getWorld().spawnFallingBlock(block.getLocation().add(LuckyBlockPlugin.randoms.nextInt(10), 35.0D, LuckyBlockPlugin.randoms.nextInt(10)), Material.STONE, (byte) 0);
                                            fb.setVelocity(fb.getVelocity().multiply(2));
                                            float ep = 11.0F;
                                            if (luckyBlock.hasDropOption("ExplosionPower")) {
                                                ep = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
                                            }

                                            HTasks.Meteor(fb, ep);
                                        }
                                    } else if (drop == LBDrop.F_LB) {
                                        final Bat bat = (Bat) block.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
                                        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                                        fb = bat.getWorld().spawnFallingBlock(bat.getLocation().add(0.0D, 5.0D, 0.0D), luckyBlock.getType().getBlockType(), (byte) luckyBlock.getType().getData());
                                        bat.setPassenger(fb);
                                        bat.setMetadata("flyinglb", new FixedMetadataValue(LuckyBlockPlugin.instance, "true"));
                                        fb.setDropItem(false);

                                        Scheduler.later(() -> {
                                            if (bat.isValid() && bat.getPassengers().isEmpty()) {
                                                bat.remove();
                                            }

                                            bat.getWorld().dropItem(bat.getLocation(), luckyBlock.getType().toItemStack(RandomUtils.randInt(0, 20)));
                                        }, 20);
                                    } else if (drop == LBDrop.SOLDIER) {
                                        com.mcgamer199.luckyblock.customentity.EntitySoldier soldier = new com.mcgamer199.luckyblock.customentity.EntitySoldier();
                                        soldier.spawn(bloc);
                                    } else if (drop == LBDrop.LB_ITEM) {
                                        block.getWorld().dropItem(bloc, luckyBlock.getType().toItemStack(LBType.getRandomP(-10, 10)));
                                    } else if (drop == LBDrop.BEDROCK) {
                                        HTasks.Bedrock(block);
                                    } else if (drop == LBDrop.JAIL) {
                                        times = 70;
                                        if (player != null) {
                                            if (luckyBlock.hasDropOption("Ticks")) {
                                                times = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
                                                if (times < 0) {
                                                    times = 0;
                                                }

                                                if (times > 1024) {
                                                    times = 1024;
                                                }
                                            }

                                            HTasks.Trap(player, times);
                                        }
                                    } else if (drop == LBDrop.TREE) {
                                        if (luckyBlock.getDropOption("TreeType") != null) {
                                            TreeType type = TreeType.valueOf(luckyBlock.getDropOption("TreeType").getValues()[0].toString().toUpperCase());
                                            HTasks.Tree(block, type);
                                        }
                                    } else if (drop == LBDrop.WOLVES_OCELOTS) {
                                        times = LuckyBlockPlugin.randoms.nextInt(2) + 1;
                                        if (times == 1) {
                                            for (fuse = LuckyBlockPlugin.randoms.nextInt(5) + 7; fuse > 0; --fuse) {
                                                Wolf wolf = (Wolf) block.getWorld().spawnEntity(bloc, EntityType.WOLF);
                                                wolf.setTamed(true);
                                                wolf.setOwner(player);
                                                wolf.setMaxHealth(30.0D);
                                                wolf.setHealth(30.0D);
                                                wolf.setSitting(true);
                                                wolf.setCollarColor(DyeColor.getByDyeData((byte) LuckyBlockPlugin.randoms.nextInt(16)));
                                                wolf.setCustomName("" + yellow + bold + "Wolf " + green + wolf.getHealth() + white + "/" + green + wolf.getMaxHealth());
                                                wolf.setCustomNameVisible(true);
                                            }
                                        } else {
                                            for (fuse = LuckyBlockPlugin.randoms.nextInt(5) + 7; fuse > 0; --fuse) {
                                                Ocelot ocelot = (Ocelot) block.getWorld().spawnEntity(bloc, EntityType.OCELOT);
                                                ocelot.setCatType(org.bukkit.entity.Ocelot.Type.getType(LuckyBlockPlugin.randoms.nextInt(4)));
                                                ocelot.setSitting(true);
                                                ocelot.setOwner(player);
                                                ocelot.setTamed(true);
                                                ocelot.setMaxHealth(20.0D);
                                                ocelot.setHealth(20.0D);
                                            }
                                        }
                                    } else if (drop == LBDrop.FIREWORKS) {
                                        HTasks.FireWorks(block);
                                    } else if (drop == LBDrop.FEED) {
                                        if (player != null) {
                                            try {
                                                player.setFoodLevel(20);
                                            } catch (Exception var21) {
                                                player.setFoodLevel(20);
                                            }
                                        }
                                    } else if (drop == LBDrop.HEAL) {
                                        if (player != null) {
                                            player.setHealth(player.getMaxHealth());
                                        }
                                    } else if (drop == LBDrop.SIGN) {
                                        for (times = 0; times < 10; ++times) {
                                        }

                                        block.setType(Material.SIGN_POST);
                                        Sign sign = (Sign) block.getState();
                                        if (luckyBlock.hasDropOption("Facing")) {
                                            path1 = luckyBlock.getDropOption("Facing").getValues()[0].toString();
                                            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
                                            if (path1.equalsIgnoreCase("PLAYER")) {
                                                if (player != null) {
                                                    signData.setFacingDirection(BlockFace.valueOf(IDirection.getByLoc(bloc, player.getLocation()).name()));
                                                }
                                            } else {
                                                signData.setFacingDirection(BlockFace.valueOf(path1.toUpperCase()));
                                            }
                                        }

                                        if (luckyBlock.hasDropOption("Texts")) {
                                            Object[] text = luckyBlock.getDropOption("Texts").getValues();

                                            for (randomP = 0; randomP < text.length; ++randomP) {
                                                if (text[randomP] != null) {
                                                    sign.setLine(randomP, ChatColor.translateAlternateColorCodes('&', text[randomP].toString()));
                                                }
                                            }
                                        }

                                        sign.update(true);
                                    } else if (drop == LBDrop.REPAIR) {
                                        if (player != null) {
                                            send_no(player, "drops.repair.1");
                                            if (luckyBlock.hasDropOption("RepairType")) {
                                                path = luckyBlock.getDropOption("RepairType").getValues()[0].toString();
                                                fuse = 0;
                                                ItemStack[] var43;
                                                ItemStack item;
                                                if (path.equalsIgnoreCase("all")) {
                                                    for (randomP = 0; randomP < player.getInventory().getSize(); ++randomP) {
                                                        if (player.getInventory().getItem(randomP) != null && player.getInventory().getItem(randomP).getType() != Material.AIR && repairable.contains(player.getInventory().getItem(randomP).getType()) && player.getInventory().getItem(randomP).getDurability() > 0) {
                                                            player.getInventory().getItem(randomP).setDurability((short) 0);
                                                            ++fuse;
                                                        }
                                                    }

                                                    counter = (var43 = player.getInventory().getArmorContents()).length;

                                                    for (counter = 0; counter < counter; ++counter) {
                                                        item = var43[counter];
                                                        if (item != null && item.getType() != Material.AIR && repairable.contains(item.getType()) && item.getDurability() > 0) {
                                                            item.setDurability((short) 0);
                                                            ++fuse;
                                                        }
                                                    }
                                                } else {
                                                    if (path.equalsIgnoreCase("inv") || path.equalsIgnoreCase("inventory")) {
                                                        for (randomP = 0; randomP < player.getInventory().getSize(); ++randomP) {
                                                            if (player.getInventory().getItem(randomP) != null && player.getInventory().getItem(randomP).getType() != Material.AIR && repairable.contains(player.getInventory().getItem(randomP).getType()) && player.getInventory().getItem(randomP).getDurability() > 0) {
                                                                player.getInventory().getItem(randomP).setDurability((short) 0);
                                                                ++fuse;
                                                            }
                                                        }
                                                    }

                                                    if (path.equalsIgnoreCase("armor") || path.equalsIgnoreCase("armour")) {
                                                        counter = (var43 = player.getInventory().getArmorContents()).length;

                                                        for (counter = 0; counter < counter; ++counter) {
                                                            item = var43[counter];
                                                            if (item != null && item.getType() != Material.AIR && repairable.contains(item.getType()) && item.getDurability() > 0) {
                                                                item.setDurability((short) 0);
                                                                ++fuse;
                                                            }
                                                        }
                                                    }

                                                    if (path.equalsIgnoreCase("hand") && player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                                        item = player.getInventory().getItemInMainHand();
                                                        if (repairable.contains(item.getType()) && item.getDurability() > 0) {
                                                            item.setDurability((short) 0);
                                                            ++fuse;
                                                        }
                                                    }
                                                }

                                                if (fuse > 0) {
                                                    SoundUtils.playFixedSound(player.getLocation(), SoundUtils.getSound("lb_drop_repair"), 1.0F, 1.0F, 20);
                                                    s = val("drops.repair.2");
                                                    s = s.replace("%total%", String.valueOf(fuse));
                                                    player.sendMessage(s);
                                                }
                                            }
                                        }
                                    } else if (drop == LBDrop.ENCHANT) {
                                        try {
                                            if (player != null) {
                                                if (player.getInventory().getItemInMainHand() == null) {
                                                    send_no(player, "drops.enchant_item.fail");
                                                    return;
                                                }

                                                ItemMeta im = player.getInventory().getItemInMainHand().getItemMeta();
                                                if (luckyBlock.hasDropOption("Enchants") && luckyBlock.hasDropOption("Levels")) {
                                                    fuse = random.nextInt(luckyBlock.getDropOption("Enchants").getValues().length);
                                                    s = luckyBlock.getDropOption("Enchants").getValues()[fuse].toString();
                                                    counter = Integer.parseInt(luckyBlock.getDropOption("Levels").getValues()[fuse].toString());
                                                    im.addEnchant(Enchantment.getByName(s.toUpperCase()), counter, true);
                                                    player.getInventory().getItemInMainHand().setItemMeta(im);
                                                    send_no(player, "drops.enchant_item.success");
                                                    player.updateInventory();
                                                }
                                            }
                                        } catch (Exception var20) {
                                        }
                                    } else if (drop == LBDrop.ADD_ITEM) {
                                        if (player != null) {
                                            path = "AddedItems";
                                            path1 = null;
                                            if (luckyBlock.hasDropOption("Path")) {
                                                path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                                            }

                                            if (luckyBlock.hasDropOption("Path1")) {
                                                path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                                            }

                                            if (path1 != null) {
                                                items = com.mcgamer199.luckyblock.tags.ItemStackTags.getItems(file.getConfigurationSection(path).getConfigurationSection(path1));
                                            } else {
                                                items = com.mcgamer199.luckyblock.tags.ItemStackTags.getRandomItems(file.getConfigurationSection(path));
                                            }

                                            var46 = items;
                                            rp = items.length;

                                            for (counter = 0; counter < rp; ++counter) {
                                                i = var46[counter];
                                                if (i != null) {
                                                    player.getInventory().addItem(i);
                                                }
                                            }
                                        }
                                    } else if (drop == LBDrop.XP) {
                                        ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawnEntity(bloc, EntityType.EXPERIENCE_ORB);
                                        if (luckyBlock.hasDropOption("XPAmount")) {
                                            fuse = Integer.parseInt(luckyBlock.getDropOption("XPAmount").getValues()[0].toString());
                                            exp.setExperience(fuse);
                                        }
                                    } else if (drop == LBDrop.POISON_ENTITIES) {
                                        if (player != null) {
                                            Iterator var87 = player.getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();

                                            while (var87.hasNext()) {
                                                Entity e = (Entity) var87.next();
                                                if (e instanceof LivingEntity) {
                                                    LivingEntity l = (LivingEntity) e;
                                                    if (l.getUniqueId() != player.getUniqueId()) {
                                                        if (!(l instanceof Tameable)) {
                                                            l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 10));
                                                            l.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 10));
                                                            l.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 10));
                                                            l.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 15));
                                                        } else {
                                                            Tameable t = (Tameable) l;
                                                            if (!t.isTamed()) {
                                                                l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 10));
                                                                l.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 10));
                                                                l.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 10));
                                                                l.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 15));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (drop == LBDrop.CUSTOM_STRUCTURE) {
                                        path = "Structures";
                                        if (luckyBlock.hasDropOption("Path")) {
                                            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                                        }

                                        s = null;
                                        if (luckyBlock.hasDropOption("Path1")) {
                                            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                                        } else {
                                            path1 = com.mcgamer199.luckyblock.tags.BlockTags.getRandomL(file, path);
                                        }

                                        s = com.mcgamer199.luckyblock.tags.BlockTags.getValue(file, path, path1, "LocationType");
                                        if (s == null) {
                                            s = "BLOCK";
                                        }

                                        if (s.equalsIgnoreCase("PLAYER")) {
                                            if (player != null) {
                                                com.mcgamer199.luckyblock.tags.BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), player.getLocation());
                                            } else {
                                                com.mcgamer199.luckyblock.tags.BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), bloc);
                                            }
                                        } else {
                                            com.mcgamer199.luckyblock.tags.BlockTags.buildStructure(file.getConfigurationSection(path).getConfigurationSection(path1), bloc);
                                        }
                                    } else if (drop == LBDrop.RUN_COMMAND) {
                                        if (luckyBlock.getDropOption("Command") != null) {
                                            path = (String) luckyBlock.getDropOption("Command").getValues()[0];
                                            if (path != null) {
                                                if (player != null) {
                                                    path = ChatColor.translateAlternateColorCodes('&', path);
                                                    path = path.replace("{playername}", player.getName());
                                                }

                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), path);
                                            }
                                        }
                                    } else if (drop == LBDrop.CLEAR_EFFECTS) {
                                        if (player != null && luckyBlock.hasDropOption("Effects")) {
                                            Object[] effects = luckyBlock.getDropOption("Effects").getValues();

                                            for (fuse = 0; fuse < effects.length; ++fuse) {
                                                player.removePotionEffect(PotionEffectType.getByName(effects[fuse].toString()));
                                            }
                                        }
                                    } else if (drop == LBDrop.TELEPORT) {
                                        if (player != null && luckyBlock.getDropOption("Height") != null) {
                                            times = Integer.parseInt(luckyBlock.getDropOption("Height").getValues()[0].toString());
                                            player.teleport(player.getLocation().add(0.0D, times, 0.0D));
                                        }
                                    } else if (drop == LBDrop.RANDOM_ITEM) {
                                        path = "RandomItems";
                                        path1 = null;
                                        if (luckyBlock.hasDropOption("Path")) {
                                            path = luckyBlock.getDropOption("Path").getValues()[0].toString();
                                        }

                                        if (luckyBlock.hasDropOption("Path1")) {
                                            path1 = luckyBlock.getDropOption("Path1").getValues()[0].toString();
                                        }

                                        if (path1 != null) {
                                            s = path1;
                                        } else {
                                            s = BlockTags.getRandomL(file, path);
                                        }

                                        com.mcgamer199.luckyblock.customentity.EntityRandomItem r = new com.mcgamer199.luckyblock.customentity.EntityRandomItem();
                                        Iterator var49 = file.getConfigurationSection(path + "." + s + ".Items").getKeys(false).iterator();

                                        while (var49.hasNext()) {
                                            String g = (String) var49.next();
                                            r.items.add(com.mcgamer199.luckyblock.tags.ItemStackGetter.getItemStack(file, path + "." + s + ".Items." + g));
                                        }

                                        r.spawn(bloc);
                                    } else if (drop == LBDrop.TRADES) {
                                        Villager v = (Villager) block.getWorld().spawnEntity(bloc, EntityType.VILLAGER);
                                        v.setProfession(Profession.BLACKSMITH);
                                        v.setCustomName("" + yellow + bold + "Lucky Villager");
                                        v.setCustomNameVisible(true);
                                        List<MerchantRecipe> recipes = new ArrayList();
                                        randomP = luckyBlock.getType().getRandomP();
                                        i = ItemStackUtils.createItem(Material.POTION, 1, random.nextInt(3) + 1, "" + yellow + bold + "Lucky Potion");
                                        PotionMeta iM = (PotionMeta) i.getItemMeta();
                                        PotionData data = new PotionData(PotionType.FIRE_RESISTANCE);
                                        iM.setBasePotionData(data);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2), true);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 2), true);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 400, 2), true);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 2), true);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1200, 2), true);
                                        iM.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 2), true);
                                        i.setItemMeta(iM);
                                        MerchantRecipe recipe1 = new MerchantRecipe(luckyBlock.getType().toItemStack(randomP), 30);
                                        MerchantRecipe recipe2 = new MerchantRecipe(i, 30);
                                        recipe1.addIngredient(new ItemStack(Material.DIAMOND, randomP / 10 + random.nextInt(25) + 16));
                                        recipe2.addIngredient(new ItemStack(Material.EMERALD, random.nextInt(32) + 16));
                                        recipes.add(recipe1);
                                        recipes.add(recipe2);
                                        v.setRecipes(recipes);
                                    } else if (drop == LBDrop.TNT_RAIN) {
                                        times = 10;
                                        fuse = 60;
                                        if (luckyBlock.hasDropOption("Times")) {
                                            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                        }

                                        if (luckyBlock.hasDropOption("Fuse")) {
                                            fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
                                        }

                                        HTasks.rain(bloc, times, fuse);
                                    } else if (drop == LBDrop.ZOMBIE_TRAP) {
                                        if (player != null) {
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 100));
                                            Zombie zombie = (Zombie) player.getWorld().spawnEntity(bloc, EntityType.ZOMBIE);
                                            zombie.setMaxHealth(300.0D);
                                            zombie.setHealth(300.0D);
                                            zombie.getEquipment().setItemInMainHand(ItemStackUtils.addEnchant(new ItemStack(Material.DIAMOND_SWORD), Enchantment.DAMAGE_ALL, 200));
                                            zombie.setTarget(player);
                                            HTasks.a(player, zombie);
                                        }
                                    } else {
                                        Material[] mats;
                                        if (drop == LBDrop.ITEM_RAIN) {
                                            times = 10;
                                            mats = new Material[64];
                                            Short[] itemsData = new Short[16];
                                            if (luckyBlock.hasDropOption("Times")) {
                                                times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                            }

                                            if (luckyBlock.hasDropOption("ItemMaterials")) {
                                                Object[] itemMaterials = luckyBlock.getDropOption("ItemMaterials").getValues();
                                                mats = new Material[itemMaterials.length];

                                                for (counter = 0; counter < itemMaterials.length; ++counter) {
                                                    if (itemMaterials[counter] != null) {
                                                        mats[counter] = Material.getMaterial(itemMaterials[counter].toString());
                                                    }
                                                }
                                            }

                                            if (luckyBlock.hasDropOption("ItemsData")) {
                                                itemsData = (Short[]) luckyBlock.getDropOption("ItemsData").getValues();
                                            }

                                            HTasks.itemRain(bloc, times, mats, itemsData);
                                        } else if (drop == LBDrop.BLOCK_RAIN) {
                                            times = 10;
                                            mats = new Material[64];
                                            if (luckyBlock.hasDropOption("Times")) {
                                                times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                            }

                                            if (luckyBlock.hasDropOption("BlockMaterials")) {
                                                obj = luckyBlock.getDropOption("BlockMaterials").getValues();
                                                mats = new Material[obj.length];

                                                for (counter = 0; counter < obj.length; ++counter) {
                                                    if (obj[counter] != null) {
                                                        mats[counter] = Material.getMaterial(obj[counter].toString().toUpperCase());
                                                    }
                                                }
                                            }

                                            HTasks.b(bloc, times, mats);
                                        } else if (drop == LBDrop.ARROW_RAIN) {
                                            times = 10;
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

                                            HTasks.c(bloc.add(0.5D, 0.0D, 0.5D), times, critical, bounce);
                                        } else if (drop == LBDrop.ROCKET) {
                                            HTasks.d(bloc);
                                        } else if (drop == LBDrop.TALKING_ZOMBIE) {
                                            com.mcgamer199.luckyblock.customentity.EntityTalkingZombie e = new com.mcgamer199.luckyblock.customentity.EntityTalkingZombie();
                                            e.spawn(bloc);
                                        } else {
                                            DropEvents1.run(block, luckyBlock, player, drop, customDrop, first);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            luckyBlock.customDrop.function(luckyBlock, player);
        }

    }

    private static Object getStructure(String clss) {
        try {
            Class c = null;
            if (clss.startsWith("LB_")) {
                String[] d = clss.split("LB_");
                c = Class.forName("com.LuckyBlock.World.Structures." + d[1]);
            } else {
                c = Class.forName(clss);
            }

            if (Structure.class.isAssignableFrom(c)) {
                return c.newInstance();
            }
        } catch (Exception ignored) {}

        return null;
    }

    public static void b(String clss, Location loc) {
        Object str = getStructure(clss);
        if (str != null) {
            Structure s = (Structure) str;
            s.build(loc);
        }
    }
}
