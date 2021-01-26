package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.customentity.nametag.EntityTrophyNameTag;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Trophy {
    public static List<Trophy> trophies;
    static File fileF = new File(LuckyBlock.d() + "data/trophies.yml");
    static FileConfiguration file;
    private static boolean loaded = false;

    static {
        file = YamlConfiguration.loadConfiguration(fileF);
        trophies = new ArrayList();
    }

    private UUID uuid = UUID.randomUUID();
    private final Block block;
    private final ItemStack itemToDrop;

    private Trophy(Block block, ItemStack itemToDrop) {
        this.block = block;
        if (itemToDrop != null) {
            itemToDrop.setAmount(1);
        }

        this.itemToDrop = itemToDrop;
    }

    public static void place(Block block, ItemStack itemToDrop) {
        Trophy t = new Trophy(block, itemToDrop);
        t.save(true);
        t.func_loop();
        EntityTrophyNameTag tr = new EntityTrophyNameTag();
        tr.spawn(t);
    }

    public static Trophy getByBlock(Block block) {
        for (int x = 0; x < trophies.size(); ++x) {
            String b = MyTasks.blockToString(trophies.get(x).block);
            if (b.equalsIgnoreCase(MyTasks.blockToString(block))) {
                return trophies.get(x);
            }
        }

        return null;
    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            if (file.getConfigurationSection("Trophies") != null) {
                Iterator var1 = file.getConfigurationSection("Trophies").getKeys(false).iterator();

                while (var1.hasNext()) {
                    String s = (String) var1.next();
                    ConfigurationSection c = file.getConfigurationSection("Trophies").getConfigurationSection(s);
                    if (c != null) {
                        String b = c.getString("Block");
                        ItemStack i = c.getItemStack("Item");
                        UUID u = UUID.fromString(c.getString("UUID"));
                        Trophy t = new Trophy(MyTasks.stringToBlock(b), i);
                        t.uuid = u;
                        t.save(false);
                        t.func_loop();
                    }
                }
            }
        }

    }

    private void func_loop() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (Trophy.this.block.getType() != Material.SKULL) {
                    Trophy.this.remove();
                    task.run();
                }

            }
        }, 20L, 20L));
    }

    private void save(boolean saveToFile) {
        for (int x = 0; x < trophies.size(); ++x) {
            Trophy t = trophies.get(x);
            String s = MyTasks.blockToString(t.getBlock());
            if (s.equalsIgnoreCase(MyTasks.blockToString(this.block))) {
                trophies.remove(t);
            }
        }

        trophies.add(this);
        if (saveToFile) {
            this.saveFile();
        }

    }

    public void remove() {
        for (int x = 0; x < trophies.size(); ++x) {
            Trophy t = trophies.get(x);
            String b = MyTasks.blockToString(t.block);
            if (b.equalsIgnoreCase(MyTasks.blockToString(this.block))) {
                trophies.remove(t);
            }
        }

        file.set("Trophies.Trophy" + this.uuid.toString(), null);

        try {
            file.save(fileF);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private void saveFile() {
        file.set("Trophies.Trophy" + this.uuid.toString() + ".UUID", this.uuid.toString());
        file.set("Trophies.Trophy" + this.uuid.toString() + ".Block", MyTasks.blockToString(this.block));
        file.set("Trophies.Trophy" + this.uuid.toString() + ".Item", this.itemToDrop);

        try {
            file.save(fileF);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public Block getBlock() {
        return this.block;
    }

    public ItemStack getItemToDrop() {
        return this.itemToDrop;
    }

    public boolean isValid() {
        return getByBlock(this.block) != null;
    }
}
