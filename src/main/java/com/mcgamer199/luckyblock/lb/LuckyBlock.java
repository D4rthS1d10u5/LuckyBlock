//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.api.CountingMap;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.enums.BlockProperty;
import com.mcgamer199.luckyblock.api.enums.PlacingSource;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityLuckyBlockNameTag;
import com.mcgamer199.luckyblock.customentity.nametag.CustomEntityLuckyBlockNameTag.DisplayType;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.listeners.PlaceLuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.tags.BlockTags;
import com.mcgamer199.luckyblock.util.JsonUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LuckyBlock {

    @Getter @Setter
    private static boolean persistent;
    public static final List<String> hiddenOptions = Arrays.asList("title", "player");
    public static Map<LBType, FileConfiguration> cache = new HashMap<>();

    private static final CountingMap<Location, LuckyBlock> storage = new CountingMap<>(new HashMap<>(), 500, (entries, forced) -> {
        if(persistent) {
            List<LuckyBlock> values = entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());
            LuckyBlockAPI.lbs.set("LuckyBlocks", values.stream().map(luckyBlock -> JsonUtils.toJsonString(luckyBlock, LuckyBlock.class)).collect(Collectors.toList()));
            LuckyBlockAPI.saveLBFile();
        }
    });

    @Getter
    private LBType type;
    @Getter
    private LuckyBlockDrop luckyBlockDrop;
    @Getter @Setter
    public CustomDrop customDrop;
    @Getter @Setter
    public BlockFace facing;
    private Block block;
    private World world;
    @Getter
    private int luck;
    @Getter @Setter
    public UUID owner;
    @Getter @Setter
    public int tickDelay;

    public static final int MAX_LB_COUNT = 1048;
    public static List<LuckyBlock> lastDeleted = new ArrayList<>();
    @Getter
    private final Properties dropOptions = new Properties();
    @Getter
    private PlacingSource placingSource;
    @Getter
    private String placedBy;
    private FileConfiguration file;
    private FileConfiguration customFile;
    private String floc; //TODO продебажить это поле, вообще неясно что оно значит
    private final String folder = "Drops";
    private boolean locked;
    private CustomEntityLuckyBlockNameTag dropDisplay;

    public LuckyBlock() {
        this.facing = BlockFace.EAST;
    }

    public LuckyBlock(LBType type, Block block, int luck, Object placedBy, boolean saveAll, boolean random) {
        this.facing = BlockFace.EAST;
        if (getByBlock(block) == null) {
            if (canSaveMore()) {
                init(type, block, luck, placedBy, saveAll, random);
            }
        }
    }

    public static void clearStorage(boolean removeBlock) {
        if(removeBlock) {
            storage.values().forEach(luckyBlock -> luckyBlock.getBlock().setType(Material.AIR));
        }

        storage.setCountChanges(false);
        storage.clear();
        LuckyBlockAPI.lbs.set("LuckyBlocks", null);
        LuckyBlockAPI.saveLBFile();
        storage.setCountChanges(true);
    }

    public static int startTimer() {
        return Scheduler.timer(() -> storage.values().stream()
                .filter(LuckyBlock::isActive)
                .filter(luckyBlock -> luckyBlock.getBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE))
                .forEach(luckyBlock -> {
                    LBType type = luckyBlock.getType();
                    if(type.hasProperty(BlockProperty.EXPLODE_ON_FIRE)) {
                        luckyBlock.explode();
                    } else if(type.hasProperty(BlockProperty.REMOVE_ON_FIRE)) {
                        luckyBlock.remove();
                    } else if(type.hasProperty(BlockProperty.FIRE_RESISTANCE)) {
                        luckyBlock.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    }
                }), 5, 5).getTaskId();
    }

    public static Map<Location, LuckyBlock> getStorage() {
        return Collections.unmodifiableMap(storage);
    }

    public static boolean canSaveMore() {
        return storage.size() < MAX_LB_COUNT;
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType, ItemStack item, Object placedBy) {
        return placeLB(loc, lbType, item, placedBy, null, 0);
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType, ItemStack item, Object placedBy, String drop, int luck, LBOption... options) {
        if (!canSaveMore()) {
            return null;
        } else {
            if (lbType == null && LBType.isLB(item)) {
                lbType = LBType.fromItem(item);
            }

            loc.getBlock().setType(lbType.getBlockType());
            loc.getBlock().setData((byte) lbType.getData());
            if (item != null) {
                luck = LBType.getLuck(item);
            }

            return PlaceLuckyBlock.place(lbType, loc.getBlock(), placedBy, PlaceLuckyBlock.dropToString(drop), luck, item, true, null, options);
        }
    }

    public static boolean isHiddenOption(String dropOptionName) {
        return hiddenOptions.contains(dropOptionName.toLowerCase());
    }

    public static void saveAll() {
        storage.processReachedLimit(true);
    }

    @Nullable
    public static LuckyBlock getByBlock(@NotNull Block block) {
        return storage.get(block.getLocation());
    }

    public static boolean isLuckyBlock(Block block) {
        return getByBlock(block) != null;
    }

    public static boolean isRelativesLB(Block block, BlockFace face, int times) {
        --times;
        if (isLuckyBlock(block.getRelative(face))) {
            return times <= 0 || isRelativesLB(block.getRelative(face), face, times);
        } else {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public void init(LBType type, Block block, int luck, Object placedBy, boolean saveAll, boolean random) {
        if(storage.containsKey(block.getLocation())) {
            return;
        }

        this.block = block;
        this.world = block.getWorld();
        this.type = type;
        this.luck = luck;

        if (placedBy instanceof String) {
            String placedByDefinition = (String) placedBy;
            if(placedByDefinition.startsWith("pl=")) {
                this.placingSource = PlacingSource.PLAYER;
                this.placedBy = placedByDefinition.replace("pl=", "");
            } else if(placedByDefinition.startsWith("a=Entity")) {
                this.placingSource = PlacingSource.ENTITY;
                this.placedBy = StringUtils.substringBetween(placedByDefinition, ",", "]");
            } else if(placedByDefinition.startsWith("a=Block")) {
                this.placingSource = PlacingSource.BLOCK;
                this.placedBy = StringUtils.substringBetween(placedByDefinition, "[", "]");
            } else {
                this.placingSource = PlacingSource.NONE;
                this.placedBy = "NONE";
            }
        } else if(placedBy instanceof Pair) {
            this.placingSource = (PlacingSource) ((Pair) placedBy).getKey();
            this.placedBy = (String) ((Pair) placedBy).getValue();
        } else {
            this.placingSource = PlacingSource.define(placedBy);
            this.placedBy = placingSource.fromObject(placedBy);
        }

        this.setFile();
        if (random) {
            this.setRandomDrop();
            if (placingSource.equals(PlacingSource.PLAYER)) {
                dropOptions.putString("Player", this.placedBy);
            }
        }

        this.save();
        if (type.isNameVisible() && random) {
            CustomEntityLuckyBlockNameTag typeDisplay = new CustomEntityLuckyBlockNameTag();
            typeDisplay.spawn(this, type.hasNameOffset() ? type.getOffset(0) : new double[3], DisplayType.LUCKY_BLOCK_TYPE);
            if(type.showData) {
                CustomEntityLuckyBlockNameTag luckDisplay = new CustomEntityLuckyBlockNameTag();
                CustomEntityLuckyBlockNameTag dropDisplay = new CustomEntityLuckyBlockNameTag();
                luckDisplay.spawn(this, type.hasNameOffset() ? type.getOffset(1) : new double[3], DisplayType.LUCK);
                dropDisplay.spawn(this, type.hasNameOffset() ? type.getOffset(2) : new double[3], DisplayType.DROP_NAME);
                this.dropDisplay = dropDisplay;
            }
        }
    }

    public boolean isActive() {
        return world.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4);
    }

    public Object getWhoPlaced() {
        return this.placedBy;
    }

    public FileConfiguration getFile() {
        String customFileName = getDropOptions().getString("File", null);
        if(customFileName != null && customFile == null) {
            File drops = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "Drops/" + customFileName);
            if (drops.exists()) {
                customFile = YamlConfiguration.loadConfiguration(drops);
                return customFile;
            }
        }

        return this.file;
    }

    public Location getLocation() {
        return block.getLocation();
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void playEffects() {
        if (this.tickDelay < 1) {
            this.tickDelay = (new Random()).nextInt(this.type.a_random[1]) + this.type.a_random[0];
        }

        LBEffects.testEffects(this);
    }

    public Block getBlock() {
        return this.block;
    }

    public void setFile() {
        if (this.type == null) {
            return;
        }
        this.file = cache.computeIfAbsent(this.type, lbType -> {
            String[] ad = LuckyBlockPlugin.instance.configf.getPath().split(LuckyBlockPlugin.instance.configf.getName());
            File[] files = (new File(ad[0] + "/" + this.folder + "/" + lbType.getFolder() + "/")).listFiles();

            for (File f : files) {
                if (f.getName().endsWith(".yml")) {
                    String g = YamlConfiguration.loadConfiguration(f).getString("[Luck]");
                    if (g != null) {
                        String[] d = g.split(":");
                        if (d.length == 2) {
                            int a1 = Integer.parseInt(d[0]);
                            int a2 = Integer.parseInt(d[1]);
                            if (this.luck >= a1 && this.luck <= a2) {
                                return YamlConfiguration.loadConfiguration(f);
                            }
                        } else if (this.luck == Integer.parseInt(d[0])) {
                            return YamlConfiguration.loadConfiguration(f);
                        }
                    }
                }
            }
            return null;
        });
    }

    public void setRandomDrop() {
        if (this.file != null) {
            if (this.file.getConfigurationSection("Drops") != null) {
                String g = BlockTags.getRandomL(this.file, "Drops");
                if (g != null) {
                    this.floc = g;
                    ConfigurationSection c = this.file.getConfigurationSection("Drops").getConfigurationSection(g);
                    String dropName = c.getString("DropName");
                    if (dropName != null) {
                        if (LuckyBlockDrop.isValid(dropName)) {
                            this.luckyBlockDrop = LuckyBlockDrop.getByName(dropName);
                        } else if (CustomDropManager.getByName(dropName) != null) {
                            this.customDrop = CustomDropManager.getByName(dropName);
                        }
                    }
                }
            }

            this.reloadOptions();
        }

    }

    public void reloadOptions() { //TODO починить загрузку данных из файла
        if (this.luckyBlockDrop != null || this.customDrop != null) {
            String player = dropOptions.getString("Player");
            dropOptions.clear();
            if(player != null) {
                dropOptions.putString("Player", player);
            }

            if (this.floc != null) {
                ConfigurationSection c = this.file.getConfigurationSection("Drops").getConfigurationSection(this.floc);
                if (c != null) {

                    for (String s : c.getKeys(false)) {
                        String loc = "Drops." + this.floc + "." + s;
                        Object[] obj = this.getValue(loc);

                        for (int x = 0; x < obj.length; ++x) {
                            if (obj[x] instanceof String) {
                                obj[x] = obj[x].toString().replace("%s%", "'");
                            }
                        }

                        if (s.equalsIgnoreCase("TreeType")) {
                            dropOptions.putEnum("TreeType", TreeType.valueOf(obj[0].toString().toUpperCase()));
                        } else if (!s.equalsIgnoreCase("DropName") && !s.equalsIgnoreCase("Chance")) {
                            dropOptions.putString(s, Arrays.toString(obj));
                        }
                    }

                    dropOptions.merge(luckyBlockDrop.getDropOptions(), false);
                    if(dropOptions.has("Title")) {
                        dropOptions.putString("Title", "§c" + luckyBlockDrop.name());
                    }
                }
            } else {
                dropOptions.merge(luckyBlockDrop.getDropOptions(), true);
                dropOptions.putString("Title", "§c" + luckyBlockDrop.name());
            }
        }
    }

    private Object[] getValue(String loc) {
        return this.file.getStringList(loc) != null && this.file.getStringList(loc).size() > 0 ? this.file.getStringList(loc).toArray() : new Object[]{this.file.get(loc)};
    }

    public void setLuck(int luck) {
        this.luck = luck;
        if (this.luck > this.type.getMaxLuck()) {
            this.luck = this.type.getMaxLuck();
        }

        if (this.luck < this.type.getMinLuck()) {
            this.luck = this.type.getMinLuck();
        }
    }

    public void changed() {
        if (this.isValid()) {
            MyTasks.playEffects(Particle.NOTE, this.block.getLocation().add(0.5D, 1.0D, 0.5D), 5, new double[]{0.0D, 0.5D, 0.0D}, 0.0F);
        }

    }

    public void setDrop(LuckyBlockDrop drop, boolean s, boolean reload) {
        if (this.luckyBlockDrop != null && this.luckyBlockDrop != drop && hasDropOption("Title")) {
            this.removeDropOption("Title");
        }

        this.luckyBlockDrop = drop;
        boolean h = false;
        int x = 0;
        if (this.luckyBlockDrop != null) {
            for (Iterator<String> var7 = this.file.getConfigurationSection("Drops").getKeys(false).iterator(); var7.hasNext(); ++x) {
                String a = var7.next();
                if (x < this.file.getConfigurationSection("Drops").getKeys(false).size() && this.file.getString("Drops." + a + ".DropName") != null && this.luckyBlockDrop.name().equalsIgnoreCase(this.file.getString("Drops." + a + ".DropName"))) {
                    this.floc = a;
                    h = true;
                }
            }
        }

        if (!h) {
            this.floc = null;
        }

        if (reload) {
            this.reloadOptions();
        }

        if (this.type.showName && this.type.showData && dropDisplay != null) {
            dropDisplay.reloadText();
        }
    }

    public void refreshCustomDrop() {
        if(customDrop == null) {
            return;
        }

        Properties customDropOptions = customDrop.getDropOptions();
        if(customDropOptions != null && !customDropOptions.isEmpty()) {
            dropOptions.clear();
            dropOptions.merge(customDropOptions, true);
        }

        if (!this.hasDropOption("Title")) {
            dropOptions.putString("Title", "§c" + customDrop.getName());
        }
    }

    public void removeDropOption(String dropOption) {
        dropOptions.remove(dropOption);
    }

    public void save() {
        storage.put(getBlock().getLocation(), this);
    }

    public void remove(boolean removeBlock) {
        if (removeBlock) {
            this.block.setType(Material.AIR);
        }

        this.remove();
    }

    public void remove() {
        if (lastDeleted.size() > 100) {
            lastDeleted.remove(0);
        }

        lastDeleted.add(this);
        storage.values().remove(this);
    }

    public boolean isValid() {
        return storage.containsKey(getBlock().getLocation());
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof LuckyBlock) {
            return ((LuckyBlock) other).getBlock().equals(getBlock());
        }

        return false;
    }

    public void explode() {
        this.remove();
        final Location location = this.block.getLocation().add(0.4D, 0.6D, 0.4D);
        Scheduler.create(() -> block.getWorld().spawnParticle(Particle.SPELL, location, 50, 0.3D, 0.7D, 0.3D, 0.0D))
                .count(10)
                .onCancel(() -> block.getWorld().createExplosion(block.getLocation(), 3.0F))
                .timer(3, 3);
    }

    public void changeBlock(Block block) {
        this.block = block;
    }

    public boolean hasDropOption(String name) {
        return dropOptions.has(name);
    }

    public String getPlacementInfo() {
        return String.format("%s %s", placingSource.getDescription(), placedBy);
    }

    public boolean hasOwner() {
        return this.owner != null;
    }
}
