package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.logic.ITask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SpawnerBlock {
    private static boolean loaded = false;
    public static List<SpawnerBlock> spawners = new ArrayList();
    private UUID uuid = UUID.randomUUID();
    static File fileF = new File(LuckyBlock.d() + "data/spawners.yml");
    static FileConfiguration file;
    private Material mat;
    private Block block;
    private int range = 16;
    private String entityToSpawn;

    static {
        file = YamlConfiguration.loadConfiguration(fileF);
    }

    public SpawnerBlock(Block block, int range, String entityToSpawn) {
        this.block = block;
        this.range = range;
        this.mat = block.getType();
        this.entityToSpawn = entityToSpawn;
        this.func_loop();
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

    private void func_loop() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (SpawnerBlock.this.block.getType() == SpawnerBlock.this.mat) {
                    Iterator var2 = SpawnerBlock.this.block.getWorld().getPlayers().iterator();

                    while(var2.hasNext()) {
                        Player p = (Player)var2.next();
                        if (p.getLocation().distance(SpawnerBlock.this.block.getLocation()) <= (double)SpawnerBlock.this.range) {
                            SpawnerBlock.this.activate();
                            task.run();
                        }
                    }
                } else {
                    SpawnerBlock.this.remove();
                    task.run();
                }

            }
        }, 100L, 10L));
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
        for(int x = 0; x < spawners.size(); ++x) {
            SpawnerBlock sb = (SpawnerBlock)spawners.get(x);
            String s = MyTasks.blockToString(sb.getBlock());
            if (s.equalsIgnoreCase(MyTasks.blockToString(this.block))) {
                spawners.remove(sb);
            }
        }

        spawners.add(this);
        if (saveToFile) {
            this.saveFile();
        }

    }

    public void remove() {
        for(int x = 0; x < spawners.size(); ++x) {
            SpawnerBlock s = (SpawnerBlock)spawners.get(x);
            String b = MyTasks.blockToString(s.block);
            if (b.equalsIgnoreCase(MyTasks.blockToString(this.block))) {
                spawners.remove(s);
            }
        }

        file.set("SpawnerBlocks.Spawner" + this.uuid.toString(), (Object)null);

        try {
            file.save(fileF);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private void saveFile() {
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".UUID", this.uuid.toString());
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".Block", MyTasks.blockToString(this.block));
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".Range", this.range);
        file.set("SpawnerBlocks.Spawner" + this.uuid.toString() + ".BlockMaterial", this.mat.name());

        try {
            file.save(fileF);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            if (file.getConfigurationSection("SpawnerBlocks") != null) {
                Iterator var1 = file.getConfigurationSection("SpawnerBlocks").getKeys(false).iterator();

                while(var1.hasNext()) {
                    String s = (String)var1.next();
                    ConfigurationSection c = file.getConfigurationSection("SpawnerBlocks").getConfigurationSection(s);
                    if (c != null) {
                        String b = c.getString("Block");
                        int range = c.getInt("Range");
                        UUID u = UUID.fromString(c.getString("UUID"));
                        String e = c.getString("SpawnEntity");
                        String mat = c.getString("BlockMaterial");
                        SpawnerBlock sb = new SpawnerBlock(MyTasks.stringToBlock(b), range, e);
                        sb.uuid = u;
                        sb.mat = Material.getMaterial(mat);
                        sb.save(false);
                        sb.func_loop();
                    }
                }
            }
        }

    }
}
