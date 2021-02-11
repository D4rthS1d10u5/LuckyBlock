package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SpawnerBlock {
    public static List<SpawnerBlock> spawners = new ArrayList();
    static File fileF = new File(LuckyBlockPlugin.d() + "data/spawners.yml");
    static FileConfiguration file;
    private static boolean loaded = false;

    static {
        file = YamlConfiguration.loadConfiguration(fileF);
    }

    private UUID uuid = UUID.randomUUID();
    private Material mat;
    private final Block block;
    private int range = 16;
    private final String entityToSpawn;

    public SpawnerBlock(Block block, int range, String entityToSpawn) {
        this.block = block;
        this.range = range;
        this.mat = block.getType();
        this.entityToSpawn = entityToSpawn;
        this.func_loop();
    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            if (file.getConfigurationSection("SpawnerBlocks") != null) {
                Iterator var1 = file.getConfigurationSection("SpawnerBlocks").getKeys(false).iterator();

                while (var1.hasNext()) {
                    String s = (String) var1.next();
                    ConfigurationSection c = file.getConfigurationSection("SpawnerBlocks").getConfigurationSection(s);
                    if (c != null) {
                        String b = c.getString("Block");
                        int range = c.getInt("Range");
                        UUID u = UUID.fromString(c.getString("UUID"));
                        String e = c.getString("SpawnEntity");
                        String mat = c.getString("BlockMaterial");
                        SpawnerBlock sb = new SpawnerBlock(LocationUtils.blockFromString(b), range, e);
                        sb.uuid = u;
                        sb.mat = Material.getMaterial(mat);
                        sb.save(false);
                        sb.func_loop();
                    }
                }
            }
        }

    }

    public Block getBlock() {
        return this.block;
    }

    public int getRange() {
        return this.range;
    }

    public String getEntityToSpawn() {
        return this.entityToSpawn;
    }

    //TODO сделать общий таймер
    private void func_loop() {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (SpawnerBlock.this.block.getType() == SpawnerBlock.this.mat) {

                    for (Player p : SpawnerBlock.this.block.getWorld().getPlayers()) {
                        if (p.getLocation().distance(SpawnerBlock.this.block.getLocation()) <= (double) SpawnerBlock.this.range) {
                            SpawnerBlock.this.activate();
                            Scheduler.cancelTask(this);
                        }
                    }
                } else {
                    SpawnerBlock.this.remove();
                    Scheduler.cancelTask(this);
                }
            }
        }, 100, 10);
    }

    private void activate() {
        this.block.setType(Material.AIR);
        if (this.entityToSpawn != null) {
            try {
                Class c = Class.forName(this.entityToSpawn.replace("-", "."));
                if (CustomEntity.class.isAssignableFrom(c)) {
                    Object o = c.newInstance();
                    o.getClass().getMethod("spawn", Location.class).invoke(o, this.block.getLocation());
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException var3) {
                var3.printStackTrace();
            }
        }

        this.remove();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void save(boolean saveToFile) {
        for (int x = 0; x < spawners.size(); ++x) {
            SpawnerBlock sb = spawners.get(x);
            String s = LocationUtils.asString(sb.getBlock().getLocation());
            if (s.equalsIgnoreCase(LocationUtils.asString(this.block.getLocation()))) {
                spawners.remove(sb);
            }
        }

        spawners.add(this);
        if (saveToFile) {
            this.saveFile();
        }

    }

    public void remove() {
        for (int x = 0; x < spawners.size(); ++x) {
            SpawnerBlock s = spawners.get(x);
            String b = LocationUtils.asString(s.block.getLocation());
            if (b.equalsIgnoreCase(LocationUtils.asString(this.block.getLocation()))) {
                spawners.remove(s);
            }
        }

        file.set("SpawnerBlocks.Spawner" + this.uuid.toString(), null);

        try {
            file.save(fileF);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private void saveFile() {
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".UUID", this.uuid.toString());
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".Block", LocationUtils.asString(this.block.getLocation()));
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".Range", this.range);
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".BlockMaterial", this.mat.name());

        try {
            file.save(fileF);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
