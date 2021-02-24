package com.mcgamer199.luckyblock.api;

import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.util.JsonUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
@Log4j2
public class LuckyBlockAPI {

    public static File lbsF;
    public static FileConfiguration lbs;
    public static List<String> lbwblocks;
    public static List<Detector> detectors;
    static List<String> locations;
    static File portalsF;
    static FileConfiguration portals;
    private static boolean loaded;
    private static int luckyBlockTimerId;

    static {
        lbsF = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "LuckyBlocks.yml");
        lbs = YamlConfiguration.loadConfiguration(lbsF);
        lbwblocks = new ArrayList();
        detectors = new ArrayList();
        locations = new ArrayList();
        portalsF = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "Data/Portals.yml");
        portals = YamlConfiguration.loadConfiguration(portalsF);
        loaded = false;
    }

    public static void loadLuckyBlocks() {
        if (!loaded) {
            loaded = true;
            LuckyBlockPlugin.instance.getLogger().info(MyTasks.val("log.lb.loading", false));
            int total = 0;

            List<String> luckyBlocks = lbs.getStringList("LuckyBlocks");
            for (String luckyBlock : luckyBlocks) {
                try {
                    if(luckyBlock.startsWith("{")) {
                        log.info(String.format("Loading Lucky Block %s", JsonUtils.deserialize(luckyBlock, LuckyBlock.class)));
                        total++;
                    } else if(luckyBlock.startsWith("[")) {
                        legacyLoad(luckyBlock);
                        total++;
                    }
                } catch (Exception e) {
                    log.warn(String.format("Error while loading Lucky Block '%s'", luckyBlock), e);
                }
            }

            log.info(MyTasks.val("log.lb.found", false).replace("%total%", String.valueOf(total)));
            luckyBlockTimerId = LuckyBlock.startTimer();
        }
    }

    private static void legacyLoad(String serializedLuckyBlock) {
        if(LuckyBlockPlugin.isDebugEnabled()) {
            log.info("Loading legacy-serialized Lucky Block " + serializedLuckyBlock);
        }

        serializedLuckyBlock = serializedLuckyBlock.substring(1, serializedLuckyBlock.length() - 1);
        String[] data = serializedLuckyBlock.split("/s/");
        LBType type = null;
        Block block = null;
        String placedBy = null;
        int luck = 0;

        for (String part : data) {
            String[] partData = part.split(":=");
            if (partData.length == 2) {
                if (partData[0].equalsIgnoreCase("LBType")) {
                    type = LBType.fromId(Integer.parseInt(partData[1]));
                } else if (partData[0].equalsIgnoreCase("Block")) {
                    block = LocationUtils.blockFromString(partData[1]);
                } else if (partData[0].equalsIgnoreCase("PlacedBy")) {
                    placedBy = partData[1];
                } else if (partData[0].equalsIgnoreCase("Luck")) {
                    luck = Integer.parseInt(partData[1]);
                }
            }
        }

        if (block != null && type != null) {
            LuckyBlock luckyBlock = new LuckyBlock(type, block, luck, placedBy, false, false);

            if (block.getType() == Material.AIR) {
                luckyBlock.remove();
                return;
            }

            for (String part : data) {
                String[] partData = part.split(":=");
                if (partData.length == 2) {
                    if (partData[0].equalsIgnoreCase("Drop")) {
                        if (LuckyBlockDrop.isValid(partData[1])) {
                            luckyBlock.setDrop(LuckyBlockDrop.valueOf(partData[1]), false, false);
                        }
                    } else if (partData[0].equalsIgnoreCase("Tick_a")) {
                        luckyBlock.tickDelay = Integer.parseInt(partData[1]);
                    } else if (partData[0].equalsIgnoreCase("CustomDrop")) {
                        luckyBlock.customDrop = CustomDropManager.getByName(partData[1]);
                    } else if (partData[0].equalsIgnoreCase("Owner")) {
                        if (!partData[1].equalsIgnoreCase("null")) {
                            luckyBlock.owner = UUID.fromString(partData[1]);
                        }
                    } else if (partData[0].equalsIgnoreCase("Facing")) {
                        luckyBlock.facing = BlockFace.valueOf(partData[1].toUpperCase());
                    } else if (partData[0].equalsIgnoreCase("Freezed")) {
                        if (partData[1].equalsIgnoreCase("true")) {
                            luckyBlock.lock();
                        }
                    } else if (partData[0].equalsIgnoreCase("Options")) {
                        String p = partData[1].replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace("'", "");
                        String[] c = p.split(";");

                        for (String value : c) {
                            String[] propertyData = value.split(":");
                            if (propertyData.length > 1) {
                                String propertyName = propertyData[0];
                                String propertyValue = propertyData[1];
                                if(propertyValue.contains(",")) {
                                    luckyBlock.getDropOptions().putStringArray(propertyName, propertyValue.split(","));
                                } else if(Properties.BOOLEAN.matcher(propertyValue).matches()) {
                                    luckyBlock.getDropOptions().putBoolean(propertyName, Boolean.parseBoolean(propertyValue));
                                } else if(Properties.INT.matcher(propertyValue).matches()) {
                                    luckyBlock.getDropOptions().putInt(propertyName, Integer.parseInt(propertyValue));
                                } else if(Properties.FLOAT.matcher(propertyValue).matches()) {
                                    luckyBlock.getDropOptions().putDouble(propertyName, Float.parseFloat(propertyValue));
                                } else {
                                    log.info(String.format("Неизвестный параметр %s для ключа %s, записываем как строку", propertyName, propertyValue));
                                    luckyBlock.getDropOptions().putString(propertyName, propertyValue);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void savePortals() {
        portals.set("Portals", lbwblocks);
        portals.set("Locations", locations);
        savePortalsFile();
    }

    public static Location getLocation(Player player) {
        for (String location : locations) {
            String[] d = location.split(",");
            if (d.length == 5) {
                String uuid = d[0];
                if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    String all = d[1] + "," + d[2] + "," + d[3] + "," + d[4];
                    return LocationUtils.locationFromString(all);
                }
            }
        }

        return null;
    }

    public static void addLocation(Player player, Location location) {
        for (int x = 0; x < locations.size(); ++x) {
            String s = locations.get(x);
            String[] d = s.split(",");
            if (d.length == 5) {
                String uuid = d[0];
                if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    locations.remove(s);
                }
            }
        }

        locations.add(player.getUniqueId().toString() + "," + LocationUtils.asString(location));
        savePortals();
    }

    public static void loadPortals() {
        List<String> list = portals.getStringList("Portals");
        if (list.size() > 0) {
            lbwblocks.addAll(list);
        }

        locations = portals.getStringList("Locations");
    }

    private static void savePortalsFile() {
        try {
            portals.save(portalsF);
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    public static String getDetector(int id) {
        String s = null;
        ConfigurationSection cs = LuckyBlockPlugin.instance.detectors.getConfigurationSection("Detectors");

        try {
            for (int x = 0; x < cs.getKeys(false).size(); ++x) {
                if (cs.getInt(cs.getKeys(false).toArray()[x].toString() + ".ID") != 0) {
                    s = "Detectors." + cs.getKeys(false).toArray()[x].toString();
                    x = cs.getKeys(false).size();
                }
            }
        } catch (Exception var4) {
        }

        return s;
    }

    public static void saveLBFile() {
        try {
            lbs.save(lbsF);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void saveDetFile() {
        try {
            LuckyBlockPlugin.instance.detectors.save(LuckyBlockPlugin.instance.detectorsF);
        } catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public static void spawnLuckyBlockItem(LuckyBlock luckyBlock, int luck, Location loc) {
        ItemStack item = luckyBlock.getType().toItemStack(luck);
        loc.getWorld().dropItem(loc, item);
    }
}

