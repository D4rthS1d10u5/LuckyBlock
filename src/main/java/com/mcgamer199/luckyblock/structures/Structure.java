package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.MyObject;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Structure {

    private static final List<Structure> structures = new ArrayList<>();
    private static final File structuresFile = new File(LuckyBlockPlugin.d() + "Data/structures.yml");
    private static final FileConfiguration structuresConfig = YamlConfiguration.loadConfiguration(structuresFile);

    private int id;
    private Location location;

    public Structure() {}

    public static Treasure getRandomTreasure() {
        return (Treasure) RandomUtils.getRandomObject(structures.stream().filter(structure -> structure.getId() == 15).collect(Collectors.toList()));
    }

    public static boolean buildStructure(String className, Location location) {
        Structure structure = getStructure(className);
        if (structure != null) {
            structure.build(location);
            return true;
        }

        return false;
    }

    private static Structure getStructure(String className) {
        try {
            Class<?> structureClass = Class.forName(className.replace("LB_", "com.mcgamer199.luckyblock.structures"));

            if (Structure.class.isAssignableFrom(structureClass)) {
                return MyObject.wrap(structureClass).newInstance().getObject();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static List<Structure> getStructures() {
        return structures;
    }

    public static void loadStructures() {
        List<String> list = Structure.structuresConfig.getStringList("Structures");
        if (list != null && list.size() > 0) {
            for (String s : list) {
                String[] d = s.split(",");
                int id = Integer.parseInt(d[0]);
                String world = d[1];
                int xx = Integer.parseInt(d[2]);
                int y = Integer.parseInt(d[3]);
                int z = Integer.parseInt(d[4]);
                Location l = new Location(Bukkit.getWorld(world), xx, y, z);
                if (id == 15) {
                    Treasure treasure = new Treasure();
                    treasure.setLocation(l);
                    treasure.add();
                } else {
                    Structure structure = new Structure();
                    structure.setLocation(l);
                    structure.add();
                }
            }
        }

        List<String> baseblocks = Structure.structuresConfig.getStringList("BaseBlocks");
        BossDungeon.baseBlocks = baseblocks;
    }

    public int getId() {
        return this.id;
    }

    public void build(Location loc) {
        this.location = loc;
        structures.add(this);
        this.save();
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location loc) {
        this.location = loc;
    }

    protected void add() {
        structures.add(this);
    }

    public boolean canSave() {
        return false;
    }

    public boolean isValidLocation() {
        return getLocation() != null && getLocation().getWorld() != null;
    }

    public final void save() {
        List<String> list = new ArrayList<>();

        for (Structure structure : structures) {
            if (structure.canSave() && structure.isValidLocation()) {
                list.add(String.format("%d,%s", id, LocationUtils.asString(structure.getLocation())));
            }
        }

        structuresConfig.set("BaseBlocks", BossDungeon.baseBlocks);
        structuresConfig.set("Structures", list);

        try {
            structuresConfig.save(structuresFile);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }
}
