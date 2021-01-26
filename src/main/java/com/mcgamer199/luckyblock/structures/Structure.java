package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Structure {

    private int id;
    private Location loc;
    protected Random random = new Random();
    private static List<Structure> structures = new ArrayList();
    static File structureF = new File(LuckyBlock.d() + "Data/structures.yml");
    static FileConfiguration structure;

    static {
        structure = YamlConfiguration.loadConfiguration(structureF);
    }

    public Structure() {
    }

    public static boolean isStructure(String clss) {
        try {
            Class c = null;
            if (clss.startsWith("LB_")) {
                String[] d = clss.split("LB_");
                c = Class.forName("com.LuckyBlock.World.Structures." + d[1]);
            } else {
                c = Class.forName(clss);
            }

            if (Structure.class.isAssignableFrom(c)) {
                return true;
            }
        } catch (Exception var3) {
        }

        return false;
    }

    public int getId() {
        return this.id;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public void build(Location loc) {
        this.loc = loc;
        structures.add(this);
        this.save();
    }

    public Location getLocation() {
        return this.loc;
    }

    protected void add() {
        structures.add(this);
    }

    public static List<Structure> getStructures() {
        return structures;
    }

    public boolean saveAble() {
        return false;
    }

    public final void save() {
        List<String> list = new ArrayList();

        for(int x = 0; x < structures.size(); ++x) {
            Structure s = (Structure)structures.get(x);
            if (s.saveAble() && s.getLocation() != null && s.getLocation().getWorld() != null) {
                list.add(s.getId() + "," + s.getLocation().getWorld().getName() + "," + s.getLocation().getBlockX() + "," + s.getLocation().getBlockY() + "," + s.getLocation().getBlockZ());
            }
        }

        structure.set("BaseBlocks", BossDungeon.baseBlocks);
        structure.set("Structures", list);

        try {
            structure.save(structureF);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public static void loadStructures() {
        List<String> list = Structure.structure.getStringList("Structures");
        if (list != null && list.size() > 0) {
            for(int x = 0; x < list.size(); ++x) {
                String[] d = ((String)list.get(x)).split(",");
                int id = Integer.parseInt(d[0]);
                String world = d[1];
                int xx = Integer.parseInt(d[2]);
                int y = Integer.parseInt(d[3]);
                int z = Integer.parseInt(d[4]);
                Location l = new Location(Bukkit.getWorld(world), (double)xx, (double)y, (double)z);
                if (id == 15) {
                    Treasure treasure = new Treasure();
                    treasure.setLocation(l);
                    treasure.s();
                    treasure.add();
                } else {
                    Structure structure = new Structure();
                    structure.setLocation(l);
                    structure.add();
                }
            }
        }

        List<String> baseblocks = Structure.structure.getStringList("BaseBlocks");
        BossDungeon.baseBlocks = baseblocks;
    }
}
