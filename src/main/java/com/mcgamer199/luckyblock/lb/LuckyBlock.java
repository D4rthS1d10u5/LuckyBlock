//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.customentity.nametag.EntityLBNameTag;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.listeners.PlaceLuckyBlock;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.tags.BlockTags;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class LuckyBlock {

    public static final int MAX_LB_COUNT = 1048;
    public static final List<String> hiddenOptions = Arrays.asList("Title", "Player");
    public static final String sp = "/s/";
    public static List<LuckyBlock> luckyBlocks = new ArrayList();
    public static List<LuckyBlock> lastDeleted = new ArrayList();
    public static HashMap<String, List<LuckyBlock>> regions = new HashMap();
    public static Map<LBType, FileConfiguration> cache = new HashMap<>();
    public final int _d = 5;
    public UUID owner;
    public int a;
    public CustomDrop customDrop;
    public BlockFace facing;
    Block block;
    private int luck;
    private com.mcgamer199.luckyblock.lb.LBDrop drop;
    private LBType type;
    private List<com.mcgamer199.luckyblock.lb.DropOption> dropOptions = new ArrayList();
    private Object b;
    private FileConfiguration file;
    private String floc;
    private final String folder = "Drops";
    private boolean freezed;

    public LuckyBlock() {
        this.facing = BlockFace.EAST;
    }

    public LuckyBlock(LBType type, Block block, int luck, Object placedBy, boolean s, boolean r) {
        this.facing = BlockFace.EAST;
        if (getFromBlock(block) == null) {
            if (canSaveMore()) {
                init(type, block, luck, placedBy, s, r);
            }
        }
    }

    public static boolean canSaveMore() {
        return luckyBlocks.size() < 1048;
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType) {
        return placeLB(loc, lbType, null);
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType, ItemStack item) {
        return placeLB(loc, lbType, item, null);
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType, ItemStack item, Object placedBy) {
        return placeLB(loc, lbType, item, placedBy, null, 0);
    }

    public static LuckyBlock placeLB(Location loc, LBType lbType, ItemStack item, Object placedBy, String drop) {
        return placeLB(loc, lbType, item, placedBy, drop, 0);
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

    public static boolean isHidden(String name) {
        Iterator var2 = hiddenOptions.iterator();

        while (var2.hasNext()) {
            String s = (String) var2.next();
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static LuckyBlock getByABlock(Block block) {
        for (int x = 0; x < luckyBlocks.size(); ++x) {
            if (luckyBlocks.get(x).getType().hasAdditionalBlocks() && luckyBlocks.get(x).block.getWorld().getName().equalsIgnoreCase(block.getWorld().getName())) {
                String[] a = luckyBlocks.get(x).getType().getAdditionalBlocks();
                String[] var6 = a;
                int var5 = a.length;

                for (int var4 = 0; var4 < var5; ++var4) {
                    String s = var6[var4];
                    String[] d = s.split(" ");
                    String[] g = d[0].split(",");
                    Block b = luckyBlocks.get(x).block;
                    int x1 = Integer.parseInt(g[0]);
                    int y1 = Integer.parseInt(g[1]);
                    int z1 = Integer.parseInt(g[2]);
                    if (blockToString(b.getLocation().add(x1, y1, z1).getBlock()).equalsIgnoreCase(blockToString(block))) {
                        return luckyBlocks.get(x);
                    }
                }
            }
        }

        return null;
    }

    public static void saveAll() {
        List<String> list = new ArrayList();

        for (int x = 0; x < luckyBlocks.size(); ++x) {
            LuckyBlock luckyBlock = luckyBlocks.get(x);
            list.add(luckyBlock.toString());
        }

        LuckyBlockAPI.lbs.set("LuckyBlocks", list);
        LuckyBlockAPI.saveLBFile();
    }

    public static LuckyBlock getFromBlock(Block block) {
        for (int x = 0; x < luckyBlocks.size(); ++x) {
            LuckyBlock luckyBlock = luckyBlocks.get(x);
            if (luckyBlock.blockToString().equalsIgnoreCase(blockToString(block))) {
                return luckyBlock;
            }
        }

        return null;
    }

    public static boolean isLuckyBlock(Block block) {
        return getFromBlock(block) != null;
    }

    public static Block getRelatives(Block block, BlockFace face, int times) {
        --times;
        return times > 0 ? getRelatives(block.getRelative(face), face, times) : block.getRelative(face);
    }

    public static boolean isRelativesType(Block block, BlockFace face, int times, Material mat) {
        --times;
        if (block.getRelative(face).getType() == mat) {
            return times <= 0 || isRelativesType(block.getRelative(face), face, times, mat);
        } else {
            return false;
        }
    }

    public static boolean isRelativesLB(Block block, BlockFace face, int times) {
        --times;
        if (isLuckyBlock(block.getRelative(face))) {
            return times <= 0 || isRelativesLB(block.getRelative(face), face, times);
        } else {
            return false;
        }
    }

    public static boolean isRelatives(Block block, Material[] mats, BlockFace... face) {
        boolean g = false;

        for (int x = 0; x < mats.length; ++x) {
            g = block.getRelative(face[x]).getType() == mats[x];
        }

        return g;
    }

    public static String blockToString(Block block) {
        if (block.getWorld() != null) {
            String world = block.getWorld().getName();
            return world + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        } else {
            return null;
        }
    }

    public void init(LBType type, Block block, int luck, Object placedBy, boolean s, boolean r) {
        this.block = block;
        this.type = type;
        this.luck = luck;
        if (placedBy != null) {
            if (placedBy instanceof Entity) {
                if (placedBy instanceof Player) {
                    this.b = "pl=" + ((Player) placedBy).getName();
                } else {
                    this.b = "a=Entity[" + ((Entity) placedBy).getType() + "," + ((Entity) placedBy).getUniqueId().toString() + "]";
                }
            } else if (placedBy instanceof Block) {
                Block blk = (Block) placedBy;
                this.b = "a=Block[" + blk.getX() + "," + blk.getY() + "," + blk.getZ() + "]";
            } else {
                this.b = placedBy;
            }
        } else {
            this.b = "a=None";
        }

        Iterator var8 = luckyBlocks.iterator();

        while (var8.hasNext()) {
            LuckyBlock luckyBlock = (LuckyBlock) var8.next();
            if (this.blockToString().equalsIgnoreCase(luckyBlock.blockToString())) {
                return;
            }
        }

        this.setFile();
        if (r) {
            this.setRandomDrop();
            if (this.b != null && this.b.toString().startsWith("pl:")) {
                String[] d = this.b.toString().split("pl:");
                this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption("Player", new String[]{d[1]}));
            }
        }

        LBLoops.loop(this);
        this.save(s);
        if (type.isNameVisible() && r) {
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
                    ent3.text = ChatColor.translateAlternateColorCodes('&', this.getDropOption("Title").getValues()[0].toString());
                } else if (this.customDrop != null) {
                    ent3.text = ChatColor.RED + this.customDrop.getName();
                } else {
                    ent3.text = ChatColor.RED + this.drop.name();
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

    public Object getWhoPlaced() {
        return this.b;
    }

    public FileConfiguration getFile() {
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
        if (this.a < 1) {
            int x = (new Random()).nextInt(this.type.a_random[1]) + this.type.a_random[0];
            this.a = x;
        }

        LBEffects.testEffects(this);
    }

    public Block getBlock() {
        return this.block;
    }

    public List<com.mcgamer199.luckyblock.lb.DropOption> getDropOptions() {
        return this.dropOptions;
    }

    public void setFile() {
        if (this.type == null) {
            return;
        }
        this.file = cache.computeIfAbsent(this.type, lbType -> {
            String[] ad = LuckyBlockPlugin.instance.configf.getPath().split(LuckyBlockPlugin.instance.configf.getName());
            File[] files = (new File(ad[0] + "/" + this.folder + "/" + lbType.getFolder() + "/")).listFiles();
            File[] var6 = files;
            int var5 = files.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                File f = var6[var4];
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
                        if (com.mcgamer199.luckyblock.lb.LBDrop.isValid(dropName)) {
                            this.drop = com.mcgamer199.luckyblock.lb.LBDrop.getByName(dropName);
                        } else if (CustomDropManager.getByName(dropName) != null) {
                            this.customDrop = CustomDropManager.getByName(dropName);
                        }
                    }
                }
            }

            this.reloadOptions();
        }

    }

    public void reloadOptions() {
        if (this.drop != null || this.customDrop != null) {
            com.mcgamer199.luckyblock.lb.DropOption pn = null;
            if (this.hasDropOption("Player")) {
                pn = this.getDropOption("Player");
            }

            this.dropOptions.clear();
            if (pn != null) {
                this.dropOptions.add(pn);
            }

            int var11;
            if (this.floc != null) {
                ConfigurationSection c = this.file.getConfigurationSection("Drops").getConfigurationSection(this.floc);
                if (c != null) {
                    Iterator var4 = c.getKeys(false).iterator();

                    while (var4.hasNext()) {
                        String s = (String) var4.next();
                        String loc = "Drops." + this.floc + "." + s;
                        Object[] obj = this.getValue(loc);

                        for (int x = 0; x < obj.length; ++x) {
                            if (obj[x] instanceof String) {
                                obj[x] = obj[x].toString().replace("%s%", "'");
                            }
                        }

                        if (s.equalsIgnoreCase("TreeType")) {
                            this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption(s, new TreeType[]{TreeType.valueOf(obj[0].toString().toUpperCase())}));
                        } else if (!s.equalsIgnoreCase("DropName") && !s.equalsIgnoreCase("Chance")) {
                            this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption(s, obj));
                        }
                    }

                    com.mcgamer199.luckyblock.lb.DropOption[] var14;
                    int var12 = (var14 = this.drop.getDefaultOptions()).length;

                    for (var11 = 0; var11 < var12; ++var11) {
                        com.mcgamer199.luckyblock.lb.DropOption o = var14[var11];
                        if (o != null && !this.hasDropOption(o.getName())) {
                            this.dropOptions.add(o);
                        }
                    }

                    if (!this.hasDropOption("Title")) {
                        this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption("Title", new String[]{ChatColor.RED + this.drop.name()}));
                    }
                }
            } else {
                com.mcgamer199.luckyblock.lb.DropOption[] var13;
                var11 = (var13 = this.drop.getDefaultOptions()).length;

                for (int var10 = 0; var10 < var11; ++var10) {
                    com.mcgamer199.luckyblock.lb.DropOption o = var13[var10];
                    if (o != null) {
                        this.dropOptions.add(o);
                    }
                }

                this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption("Title", new String[]{ChatColor.RED + this.drop.name()}));
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

    public void setDrop(com.mcgamer199.luckyblock.lb.LBDrop drop, boolean s, boolean dp) {
        if (this.drop != null && this.drop != drop && this.getDropOption("Title") != null) {
            this.removeDropOptions("Title");
        }

        this.drop = drop;
        boolean h = false;
        int x = 0;
        if (this.drop != null) {
            for (Iterator var7 = this.file.getConfigurationSection("Drops").getKeys(false).iterator(); var7.hasNext(); ++x) {
                String a = (String) var7.next();
                if (x < this.file.getConfigurationSection("Drops").getKeys(false).size() && this.file.getString("Drops." + a + ".DropName") != null && this.drop.name().equalsIgnoreCase(this.file.getString("Drops." + a + ".DropName"))) {
                    this.floc = a;
                    h = true;
                }
            }
        }

        if (!h) {
            this.floc = null;
        }

        if (dp) {
            this.reloadOptions();
        }

        this.save(s);
        if (this.type.showName && this.type.showData) {
            List<EntityLBNameTag> en = EntityLBNameTag.getByLB(this);
            if (en != null && en.size() > 0) {
                for (int i = 0; i < en.size(); ++i) {
                    EntityLBNameTag l = en.get(i);
                    if (l.hasTag("IType") && l.getTag("IType").toString().equalsIgnoreCase("Drop")) {
                        l.reload(true);
                    }
                }
            }
        }

    }

    public void refreshCustomDrop() {
        this.dropOptions.clear();
        if (this.customDrop.getDefaultOptions() != null) {
            for (int x = 0; x < this.customDrop.getDefaultOptions().length; ++x) {
                if (this.customDrop.getDefaultOptions()[x] != null) {
                    this.dropOptions.add(this.customDrop.getDefaultOptions()[x]);
                }
            }
        }

        if (!this.hasDropOption("Title")) {
            this.dropOptions.add(new com.mcgamer199.luckyblock.lb.DropOption("Title", new String[]{ChatColor.RED + this.customDrop.getName()}));
        }

    }

    public void removeDropOptions(String dropOption) {
        for (int x = 0; x < this.dropOptions.size(); ++x) {
            if (this.dropOptions.get(x).getName().equalsIgnoreCase(dropOption)) {
                this.dropOptions.remove(x);
                x = 0;
            }
        }

    }

    public LBDrop getDrop() {
        return this.drop;
    }

    public void save(boolean saveAll) {
        for (int x = 0; x < luckyBlocks.size(); ++x) {
            LuckyBlock luckyBlock = luckyBlocks.get(x);
            if (this.equals(luckyBlock)) {
                luckyBlocks.remove(luckyBlock);
            }
        }

        luckyBlocks.add(this);
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
        for (int x = 0; x < luckyBlocks.size(); ++x) {
            LuckyBlock luckyBlock = luckyBlocks.get(x);
            if (luckyBlock.blockToString().equalsIgnoreCase(this.blockToString())) {
                luckyBlocks.remove(luckyBlock);
            }
        }

        if (lastDeleted.size() > 100) {
            lastDeleted.remove(0);
        }

        lastDeleted.add(this);
        List<String> list = LuckyBlockAPI.lbs.getStringList("LuckyBlocks");

        String s;
        String[] g;
        for (int x = 0; x < list.size(); ++x) {
            s = list.get(x);
            String[] d = s.substring(1, s.length() - 1).split("/s/");
            g = d;
            int var7 = d.length;

            for (int var6 = 0; var6 < var7; ++var6) {
                String s1 = g[var6];
                String[] a = s1.split(":=");
                if (a.length == 2 && a[0].equalsIgnoreCase("Block") && this.blockToString().equalsIgnoreCase(a[1])) {
                    list.remove(x);
                }
            }
        }

        LuckyBlockAPI.lbs.set("LuckyBlocks", list);
        LuckyBlockAPI.saveLBFile();
        if (this.type.hasAdditionalBlocks()) {
            String[] a = this.type.getAdditionalBlocks();
            String[] var19 = a;
            int var18 = a.length;

            for (int var17 = 0; var17 < var18; ++var17) {
                s = var19[var17];
                String[] d = s.split(" ");
                g = d[0].split(",");
                int x = Integer.parseInt(g[0]);
                int y = Integer.parseInt(g[1]);
                int z = Integer.parseInt(g[2]);
                Material mat = Material.getMaterial(d[1]);
                Block bl = this.block.getLocation().add(x, y, z).getBlock();
                if (bl.getType() == mat) {
                    bl.setType(Material.AIR);
                }
            }
        }

    }

    public boolean isValid() {
        for (int x = 0; x < luckyBlocks.size(); ++x) {
            if (luckyBlocks.get(x).equals(this)) {
                return true;
            }
        }

        return false;
    }

    public boolean equals(LuckyBlock luckyBlock) {
        return luckyBlock.blockToString().equalsIgnoreCase(this.blockToString());
    }

    public void explode() {
        this.remove();
        final Location l = this.block.getLocation().add(0.4D, 0.6D, 0.4D);
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            int x = 10;

            public void run() {
                if (this.x > 0) {
                    LuckyBlock.this.block.getWorld().spawnParticle(Particle.SPELL, l, 50, 0.3D, 0.7D, 0.3D, 0.0D);
                    --this.x;
                } else {
                    LuckyBlock.this.block.getWorld().createExplosion(LuckyBlock.this.block.getLocation(), 3.0F);
                    task.run();
                }

            }
        }, 3L, 3L));
    }

    public void changeTo(LBType type) {
        this.freeze();
        this.type = type;
        if (this.block != null) {
            this.block.setType(type.getType());
            this.block.setData((byte) type.getData());
        }

        this.unfreeze();
        this.save(true);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setData(LuckyBlock luckyBlock) {
        this.luck = luckyBlock.luck;
        this.drop = luckyBlock.drop;
        this.customDrop = luckyBlock.customDrop;
        this.dropOptions = luckyBlock.dropOptions;
        this.owner = luckyBlock.owner;
        this.b = luckyBlock.b;
        this.a = luckyBlock.a;
    }

    public String blockToString() {
        return blockToString(this.block);
    }

    public com.mcgamer199.luckyblock.lb.DropOption getDropOption(String name) {
        for (int x = 0; x < this.dropOptions.size(); ++x) {
            if (this.dropOptions.get(x).getName().equalsIgnoreCase(name)) {
                return this.dropOptions.get(x);
            }
        }

        return null;
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
        return this.getDropOption(name) != null;
    }

    public String getPlacedByClass() {
        String s = null;
        if (this.b != null) {
            if (this.b.toString().startsWith("pl=")) {
                s = this.b.toString().split("pl=")[1];
            } else if (this.b.toString().startsWith("a=")) {
                s = this.b.toString().split("a=")[1];
            } else {
                String[] d = this.b.getClass().getName().replace(".", "_").split("_");
                s = d[d.length - 1];
            }
        }

        return s;
    }

    private String getPlacedByClass_1() {
        String s = null;
        if (this.b != null) {
            if (this.b.toString().startsWith("pl=")) {
                s = this.b.toString();
            } else if (this.b.toString().startsWith("a=")) {
                s = this.b.toString();
            } else {
                String[] d = this.b.getClass().getName().replace(".", "_").split("_");
                s = d[d.length - 1];
            }
        }

        return s;
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    private String addVal(String s1, String s2, boolean first) {
        if (first) {
            s1 = s1 + s2;
        } else {
            s1 = s1 + "/s/" + s2;
        }

        return s1;
    }

    public String toString() {
        String s = "";
        s = this.addVal(s, "[Block:=" + this.blockToString(), true);
        if (this.customDrop != null) {
            s = this.addVal(s, "CustomDrop:=" + this.customDrop.getName(), false);
        } else if (this.drop != null) {
            s = this.addVal(s, "Drop:=" + this.drop.name(), false);
        }

        s = this.addVal(s, "Luck:=" + this.luck, false);
        s = this.addVal(s, "LBType:=" + this.type.getId(), false);
        s = this.addVal(s, "Tick_a:=" + this.a, false);
        if (this.owner != null) {
            s = this.addVal(s, "Owner:=" + this.owner.toString(), false);
        }

        if (this.b != null) {
            s = this.addVal(s, "PlacedBy:=" + this.getPlacedByClass_1(), false);
        }

        s = this.addVal(s, "Facing:=" + this.facing.toString(), false);
        if (this.freezed) {
            s = this.addVal(s, "Freezed:=" + this.freezed, false);
        }

        if (this.dropOptions != null && this.dropOptions.size() > 0) {
            s = s + "/s/" + "Options:={";

            for (int x = 0; x < this.dropOptions.size(); ++x) {
                com.mcgamer199.luckyblock.lb.DropOption o = this.dropOptions.get(x);
                if (o != null) {
                    if (x == 0) {
                        s = s + o.getName() + ":[";
                    } else {
                        s = s + ";" + o.getName() + ":[";
                    }

                    for (int i = 0; i < o.getValues().length; ++i) {
                        if (o.getValues()[i] != null) {
                            String t = o.getValues()[i].toString();
                            boolean num = false;

                            try {
                                Integer.parseInt(t);
                                num = true;
                            } catch (NumberFormatException var8) {
                            }

                            if (!num && !t.equalsIgnoreCase("true") && !t.equalsIgnoreCase("false")) {
                                t = "'" + t + "'";
                            }

                            if (i == 0) {
                                s = s + t;
                            } else {
                                s = s + "," + t;
                            }
                        }
                    }

                    s = s + "]";
                }
            }

            s = s + "}";
        }

        s = s + "]";
        return s;
    }
}
