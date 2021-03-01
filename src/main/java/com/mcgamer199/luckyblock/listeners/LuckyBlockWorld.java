package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.structures.*;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.*;

public class LuckyBlockWorld extends ChunkGenerator implements Listener {

    public static String generatorName = LuckyBlockWorld.class.getName();
    public static HashMap<String, List<WorldOptions>> worlds = new HashMap<>();
    int id = 0;
    private boolean inited = false;

    public LuckyBlockWorld() {
    }

    public static boolean equals(ChunkGenerator generator) {
        boolean is = false;
        if (generator != null && generator.getClass().getName().equalsIgnoreCase(generatorName)) {
            is = true;
        }

        return is;
    }

    public static World getWorld(List<WorldOptions> options) {
        if (Bukkit.getWorld("luckyblockworld") == null) {
            WorldCreator creator = new WorldCreator("luckyblockworld");
            creator.generator(new LuckyBlockWorld());
            World world = Bukkit.createWorld(creator);
            world.setMonsterSpawnLimit(25);
            world.setAnimalSpawnLimit(30);
            world.save();
            addWorld(world.getName(), options);
            LuckyBlockPlugin.instance.getLogger().info("World has been created!");
            return world;
        } else {
            return Bukkit.getWorld("luckyblockworld");
        }
    }

    public static void addWorld(String name, List<WorldOptions> options) {
        worlds.put(name, options);
    }

    public static List<WorldOptions> getOptions(String name) {
        return worlds.get(name);
    }

    public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
        if (!this.inited) {
            this.inited = true;
        }

        if (this.id < 1) {
            if (getOptions(world.getName()) != null && getOptions(world.getName()).contains(WorldOptions.ID)) {
                Iterator var6 = getOptions(world.getName()).iterator();

                while (var6.hasNext()) {
                    WorldOptions o = (WorldOptions) var6.next();
                    if (o == WorldOptions.ID) {
                        this.id = o.getId();
                    }
                }
            } else {
                this.id = LBType.getTypes().get(0).getId();
            }
        }

        byte[] blocks = new byte['耀'];
        int y;
        int z;
        int x;
        if (getOptions(world.getName()) != null && getOptions(world.getName()).contains(WorldOptions.SUPER_FLAT)) {
            for (x = 0; x < 16; ++x) {
                for (z = 0; z < 16; ++z) {
                    blocks[this.coordsToByte(x, 0, z)] = (byte) Material.BEDROCK.getId();
                }
            }

            for (x = 0; x < 16; ++x) {
                for (y = 1; y < 6; ++y) {
                    for (z = 0; z < 16; ++z) {
                        blocks[this.coordsToByte(x, y, z)] = (byte) LBType.fromId(this.id).getType().getId();
                    }
                }
            }
        } else {
            Random rand = new Random(world.getSeed());
            SimplexOctaveGenerator octave = new SimplexOctaveGenerator(rand, 8);
            octave.setScale(0.015625D);

            for (x = 0; x < 16; ++x) {
                for (z = 0; z < 16; ++z) {
                    blocks[this.coordsToByte(x, 0, z)] = (byte) Material.BEDROCK.getId();
                    double noise = octave.noise(x + chunkX * 16, z + chunkZ * 16, 0.5D, 0.5D) * 12.0D;

                    for (y = 1; (double) y < 32.0D + noise; ++y) {
                        blocks[this.coordsToByte(x, y, z)] = (byte) LBType.fromId(this.id).getType().getId();
                    }
                }
            }
        }

        return blocks;
    }

    byte getBlock(int x, int y, int z, byte[][] chunk) {
        if (chunk[y >> 4] == null) {
            return 0;
        } else if (y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0) {
            try {
                return chunk[y >> 4][(y & 15) << 8 | z << 4 | x];
            } catch (Exception var6) {
                var6.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    private int coordsToByte(int x, int y, int z) {
        return (x * 16 + z) * 128 + y;
    }

    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> populators = new ArrayList();
        return populators;
    }

    void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }

        result[y >> 4][(y & 15) << 8 | z << 4 | x] = blkid;
    }

    @EventHandler
    private void onGenerate(ChunkLoadEvent event) {
        World world = event.getWorld();
        if (world.getGenerator() != null && world.getGenerator().getClass().getName().equalsIgnoreCase(this.getClass().getName())) {
            Random random = new Random();
            int x = random.nextInt(10) - 5;
            int z = random.nextInt(10) - 5;
            int x1 = event.getChunk().getX() + x;
            int z1 = event.getChunk().getZ() + z;
            int y = event.getWorld().getHighestBlockAt(x1, z1).getY();
            if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                ++y;
            }

            Block block = event.getChunk().getBlock(x1, y, z1);
            LBType type = LBType.getTypes().get(0);

            int h;
            for (h = 0; h < 100; ++h) {
                if (block.getRelative(BlockFace.DOWN).getType() != type.getType()) {
                    block = block.getRelative(BlockFace.DOWN);
                }
            }

            for (h = 0; h < 100; ++h) {
                if (block.getType() == type.getType()) {
                    block = block.getRelative(BlockFace.UP);
                }
            }

            Location loc;
            if (random.nextInt(100) > 97) {
                loc = new Location(world, block.getX(), block.getY(), block.getZ());
                LuckyTree tree = new LuckyTree();
                tree.build(loc);
                return;
            }

            if (random.nextInt(100) > 97) {
                if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                    ++y;
                }

                loc = new Location(world, block.getX(), random.nextInt(4) + 6, block.getZ());
                LuckyDungeon dungeon = new LuckyDungeon();
                dungeon.build(loc);
                return;
            }

            if (random.nextInt(100) > 97) {
                if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                    ++y;
                }

                loc = new Location(world, block.getX(), block.getY(), block.getZ());
                PumpkinTower tower = new PumpkinTower();
                tower.build(loc);
                return;
            }

            if (random.nextInt(100) > 97) {
                if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                    ++y;
                }

                loc = new Location(world, block.getX(), block.getY(), block.getZ());
                LuckyTrap trap = new LuckyTrap();
                trap.build(loc);
                return;
            }

            if (random.nextInt(100) > 97) {
                if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                    ++y;
                }

                loc = new Location(world, block.getX(), block.getY(), block.getZ());
                WoolHouse whouse = new WoolHouse();
                whouse.build(loc);
                return;
            }

            if (random.nextInt(100) > 98) {
                if (event.getWorld().getBlockAt(x1, y, z1).getType() != Material.AIR) {
                    ++y;
                }

                loc = new Location(world, block.getX(), random.nextInt(4) + 6, block.getZ());
                Treasure treasure = new Treasure();
                treasure.build(loc);
                return;
            }
        }

    }

    @EventHandler
    private void onMobSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world.getGenerator() != null && world.getGenerator().getClass().getName().equalsIgnoreCase(this.getClass().getName())) {
            int x;
            if (event.getEntity() instanceof Zombie) {
                if (event.getSpawnReason() == SpawnReason.NATURAL) {
                    x = (new Random()).nextInt(100) + 1;
                    if (x > 90) {
                        Zombie zombie = (Zombie) event.getEntity();
                        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                        zombie.setCustomName("" + ChatColor.YELLOW + ChatColor.BOLD + "Lucky Zombie");
                        zombie.setCustomNameVisible(true);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    Zombie zombie = (Zombie) event.getEntity();
                    zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    zombie.setCustomName("" + ChatColor.YELLOW + ChatColor.BOLD + "Lucky Zombie");
                    zombie.setCustomNameVisible(true);
                }
            } else if (event.getEntity() instanceof Skeleton) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                    event.setCancelled(true);
                    if (RandomUtils.nextInt(100) + 1 > 90) {
                        IronGolem golem = (IronGolem) world.spawnEntity(event.getLocation(), EntityType.IRON_GOLEM);
                        golem.setPlayerCreated(false);
                    }
                } else if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                    Skeleton sk = (Skeleton) event.getEntity();
                    ItemStack i = new ItemStack(Material.BOW, 1, (short) 0);
                    i = ItemStackUtils.addEnchant(i, Enchantment.ARROW_FIRE, 1);
                    sk.getEquipment().setItemInMainHand(i);
                }
            } else if (event.getEntity() instanceof Pig) {
                event.getEntity().setCustomName("" + ChatColor.YELLOW + ChatColor.BOLD + "Lucky Pig");
                event.getEntity().setCustomNameVisible(true);
            } else if (event.getEntity() instanceof Creeper) {
                if (event.getSpawnReason() == SpawnReason.NATURAL) {
                    ((Creeper) event.getEntity()).setPowered(true);
                }
            } else if (event.getEntity() instanceof Cow) {
                x = (new Random()).nextInt(100) + 1; //percentage
                if (x > 70) {
                    event.setCancelled(true);
                    MushroomCow c = (MushroomCow) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.MUSHROOM_COW);
                    c.setAdult();
                } else {
                    event.getEntity().setCustomName("Grumm");
                }
            } else if (event.getSpawnReason() == SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        World world = event.getEntity().getWorld();
        if (world.getGenerator() != null && world.getGenerator().getClass().getName().equalsIgnoreCase(this.getClass().getName()) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getFinalDamage() / 10.0D);
        }

    }
}
