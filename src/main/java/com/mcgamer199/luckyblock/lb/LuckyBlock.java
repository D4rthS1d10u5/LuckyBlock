//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.enums.BlockProperty;
import com.mcgamer199.luckyblock.api.enums.PlacingSource;
import com.mcgamer199.luckyblock.customentity.nametag.EntityLBNameTag;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.listeners.PlaceLuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.tags.BlockTags;
import com.mcgamer199.luckyblock.util.JsonUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LuckyBlock {

    public static final List<String> hiddenOptions = Arrays.asList("title", "player");
    public static Map<LBType, FileConfiguration> cache = new HashMap<>();
    private static final Map<Location, LuckyBlock> luckyBlockStorage = new HashMap<>();
    private LBType type;
    private LuckyBlockDrop luckyBlockDrop;
    @Getter @Setter
    public CustomDrop customDrop;
    @Getter @Setter
    public BlockFace facing;
    private Block block;
    private World world;
    private int luck;
    @Getter
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
    private boolean freezed;

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
            luckyBlockStorage.values().forEach(luckyBlock -> luckyBlock.getBlock().setType(Material.AIR));
        }

        luckyBlockStorage.clear();
        LuckyBlockAPI.lbs.set("LuckyBlocks", null);
        LuckyBlockAPI.saveLBFile();
    }

    public static int startTimer() {
        return Scheduler.timer(() -> {
            luckyBlockStorage.values().stream()
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
                    });
        }, 5, 5).getTaskId();
    }

    public static Map<Location, LuckyBlock> getStorage() {
        return Collections.unmodifiableMap(luckyBlockStorage);
    }

    public static boolean canSaveMore() {
        return luckyBlockStorage.size() < MAX_LB_COUNT;
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
        LuckyBlockAPI.lbs.set("LuckyBlocks", luckyBlockStorage.values().stream()
                .map(luckyBlock -> JsonUtils.toJsonString(luckyBlock, LuckyBlock.class))
                .collect(Collectors.toList()));
        LuckyBlockAPI.saveLBFile();
    }

    @Nullable
    public static LuckyBlock getByBlock(@NotNull Block block) {
        return luckyBlockStorage.get(block.getLocation());
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
        if(luckyBlockStorage.containsKey(block.getLocation())) {
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

        this.save(saveAll);
        if (type.isNameVisible() && random) {
            EntityLBNameTag ent1;
            if (type.showData) {
                ent1 = new EntityLBNameTag();
                EntityLBNameTag ent2 = new EntityLBNameTag();
                EntityLBNameTag ent3 = new EntityLBNameTag();
                if (type.hasNameOffset()) {
                    ent1.a_ = type.getOffset(0);
                    ent2.a_ = type.getOffset(1);
                    ent3.a_ = type.getOffset(2);
                }

                ent1.text = type.getName();
                ent2.text = type.getLuckString(luck);
                if (this.hasDropOption("Title")) {
                    ent3.text = ChatColor.translateAlternateColorCodes('&', getDropOptions().getString("Title", "&cnull"));
                } else if (this.customDrop != null) {
                    ent3.text = ChatColor.RED + this.customDrop.getName();
                } else {
                    ent3.text = ChatColor.RED + this.luckyBlockDrop.name();
                }

                ent1.setValue("IType", "LBType");
                ent2.setValue("IType", "Luck");
                ent3.setValue("IType", "Drop");
                ent1.spawn(this, 2);
                ent2.spawn(this, 1);
                ent3.spawn(this, 0);
            } else {
                ent1 = new EntityLBNameTag();
                if (type.hasNameOffset()) {
                    ent1.a_ = type.getOffset(0);
                }

                ent1.text = type.getName();
                ent1.setValue("IType", "LBType");
                ent1.spawn(this, 1);
            }
        }
    }

    public boolean isActive() {
        return world.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4);
    }

    public Object getWhoPlaced() {
        return this.placedBy;
    }

    //TODO добавить свои конфиги
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

    public void freeze() {
        this.freezed = true;
    }

    public void unfreeze() {
        this.freezed = false;
    }

    public boolean isFreezed() {
        return this.freezed;
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

    public LBType getType() {
        return this.type;
    }

    public int getLuck() {
        return this.luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
        if (this.luck > this.type.getMaxLuck()) {
            this.luck = this.type.getMaxLuck();
        }

        if (this.luck < this.type.getMinLuck()) {
            this.luck = this.type.getMinLuck();
        }

        this.save(true);
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

        this.save(s);
        if (this.type.showName && this.type.showData) {
            List<EntityLBNameTag> nameTags = EntityLBNameTag.getByLB(this);
            if (!nameTags.isEmpty()) {
                for (EntityLBNameTag l : nameTags) {
                    if (l.hasTag("IType") && l.getTag("IType").toString().equalsIgnoreCase("Drop")) {
                        l.reload(true);
                    }
                }
            }
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

    public LuckyBlockDrop getLuckyBlockDrop() {
        return this.luckyBlockDrop;
    }

    public void save(boolean saveAll) {
        luckyBlockStorage.put(getBlock().getLocation(), this);

        if (saveAll) {
            saveAll();
        }
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
        luckyBlockStorage.values().remove(this);

        saveAll();
    }

    public boolean isValid() {
        return luckyBlockStorage.containsKey(getBlock().getLocation());
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
        Scheduler.timer(new BukkitRunnable() {
            private int times = 10;

            @Override
            public void run() {
                if (this.times > 0) {
                    LuckyBlock.this.block.getWorld().spawnParticle(Particle.SPELL, location, 50, 0.3D, 0.7D, 0.3D, 0.0D);
                    --this.times;
                } else {
                    LuckyBlock.this.block.getWorld().createExplosion(LuckyBlock.this.block.getLocation(), 3.0F);
                    Scheduler.cancelTask(this);
                }
            }
        }, 3, 3);
    }

    public String blockToString() {
        return LocationUtils.asString(this.block.getLocation());
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.save(true);
    }

    public void changeBlock(Block block) {
        this.block = block;
        this.save(true);
    }

    public boolean hasDropOption(String name) {
        return dropOptions.has(name);
    }

    public String getPlacedByClass() {
        String s = null;
        if (this.placedBy != null) {
            if (this.placedBy.toString().startsWith("pl=")) {
                s = this.placedBy.toString().split("pl=")[1];
            } else if (this.placedBy.toString().startsWith("a=")) {
                s = this.placedBy.toString().split("a=")[1];
            } else {
                String[] d = this.placedBy.getClass().getName().replace(".", "_").split("_");
                s = d[d.length - 1];
            }
        }

        return s;
    }

    public boolean hasOwner() {
        return this.owner != null;
    }
}
