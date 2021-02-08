//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.api.enums.BlockProperty;
import com.mcgamer199.luckyblock.api.enums.ItemProperty;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.listeners.PlaceLuckyBlock;
import com.mcgamer199.luckyblock.resources.CItem;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.yottaevents.LuckyDB;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.*;

public class LBType {
    public static final String LUCK_STRING;
    public static final String folderName = "Drops";
    static final Random random;
    private static final List<String> allowedPermissions;
    private static final List<LBType> lbs;

    static {
        LUCK_STRING = ChatColor.BLUE + "Удача: ";
        random = new Random();
        allowedPermissions = Arrays.asList("Crafting", "Placing", "Breaking", "SilkTouch");
        lbs = new ArrayList();
    }

    public boolean defaultBlock;
    public boolean creativeMode = true;
    public boolean showName;
    public String placesound;
    public String breaksound;
    public String placeparticles;
    public String breakparticles;
    public String tickparticles;
    public boolean allowplacesound;
    public boolean allowbreaksound;
    public boolean allowplaceparticles;
    public boolean allowbreakparticles;
    public boolean allowtickparticles;
    public com.mcgamer199.luckyblock.lb.LuckySkin skin;
    public boolean disabled;
    public boolean arrowRun = true;
    public boolean rightClick;
    public String[] percent_colors = new String[]{"0=gold", "1:49=yellow", "50:I=green", "I:-1=red"};
    public String[] skin_data = new String[2];
    public String[] name_offset;
    public boolean showData;
    public boolean luckEnchantment;
    public boolean generateNaturally = false;
    public List<String> spawnWorlds = new ArrayList();
    public int spawnRate = 0;
    public List<Biome> spawnBiomes = new ArrayList();
    public int[] generateWithLuck = new int[2];
    public double[] humidityRequired = new double[]{-1.0D, -1.0D};
    public double[] temperatureRequired = new double[]{-1.0D, -1.0D};
    public int[] spawnY = new int[2];
    Material type;
    short data;
    int[] a_random = new int[]{30, 60};
    private final String name;
    private final short maxLuck;
    private final short minLuck;
    private final String folder;
    private List<String> allowedWorlds = new ArrayList();
    private final int id;
    private final List<BlockProperty> properties = new ArrayList();
    private final List<ItemProperty> itemProperties = new ArrayList();
    private boolean useSkin;
    private int delay = 0;
    private int version = 225;
    private boolean isDefault;
    private String[] additional_blocks;
    private final HashMap<String, Permission> permissions = new HashMap();
    private byte animation_type;
    private final boolean spawnBoss = true;
    private boolean portal;
    private final Object[] breakItem = new Object[4];
    private final List<String> itemLore = new ArrayList();
    private int[] random_luck = new int[2];

    private LBType(int id, String name, short maxLuck, short minLuck, String folder, Material type, short data) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.maxLuck = maxLuck;
        this.minLuck = minLuck;
        this.folder = folder;
        this.type = type;
        this.data = data;
        this.id = id;
        this.random_luck = new int[]{minLuck, maxLuck};
    }

    public static LBType fromMaterial(Material mat) {
        LBType type = null;
        Iterator var3 = lbs.iterator();

        while (var3.hasNext()) {
            LBType t = (LBType) var3.next();
            if (t.getType() == mat) {
                type = t;
            }
        }

        return type;
    }

    public static LBType fromMaterialAndData(Material mat, byte data) {
        LBType type = null;
        Iterator var4 = lbs.iterator();

        while (var4.hasNext()) {
            LBType t = (LBType) var4.next();
            if (t.getType() == mat && t.getData() == data) {
                type = t;
            }
        }

        return type;
    }

    public static LBType fromId(int id) {
        LBType type = null;
        Iterator var3 = lbs.iterator();

        while (var3.hasNext()) {
            LBType t = (LBType) var3.next();
            if (t.getId() == id) {
                type = t;
            }
        }

        return type;
    }

    public static LBType fromItem(ItemStack item) {
        LBType type = null;

        for (int x = 0; x < lbs.size(); ++x) {
            LBType t = lbs.get(x);
            if (item.getType() == t.getType() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(t.getName())) {
                type = t;
            }
        }

        return type;
    }

    public static boolean isLB(ItemStack item) {
        boolean is = false;

        for (LBType type : lbs) {
            if (item.getType() == type.getType() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(type.getName())) {
                is = true;
            }
        }

        return is;
    }

    public static File getFolder(LBType type) {
        File f = null;
        String[] ad = LuckyBlockPlugin.instance.configf.getPath().split(LuckyBlockPlugin.instance.configf.getName());
        if (type != null) {
            f = new File(ad[0] + "/Drops/" + type.getFolder() + "/");
        }

        return f;
    }

    public static FileConfiguration getFile(LBType type, int number) {
        File f = null;
        FileConfiguration file = null;
        if (type != null) {
            f = new File(LuckyBlockPlugin.instance.getDataFolder() + File.separator + "Drops/" + type.getFolder() + "/");
            f = new File(f.getPath() + "/" + f.listFiles()[number].getName());
        }

        file = YamlConfiguration.loadConfiguration(f);
        return file;
    }

    public static List<LBType> getTypes() {
        return lbs;
    }

    public static List<LBType> getGTypes() {
        List<LBType> t = new ArrayList();

        for (int x = 0; x < lbs.size(); ++x) {
            if (lbs.get(x).generateNaturally) {
                t.add(lbs.get(x));
            }
        }

        return t;
    }

    public static int getRandomP(int min, int max) {
        int p = (new Random()).nextInt(max - min + 1) + min;
        return p;
    }

    public static LBType getRandomType() {
        return lbs.size() > 0 ? lbs.get((new Random()).nextInt(lbs.size())) : null;
    }

    public static int getLuck(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            String s = lore.get(getLuckData(item));
            String l = ChatColor.stripColor(s);
            if (l.endsWith("%")) {
                l = l.substring(0, l.length() - 1);
            }

            String[] d = l.split(ChatColor.stripColor(LUCK_STRING));
            if (d.length == 2) {
                if (d[1].startsWith("O")) {
                    LBType type = fromItem(item);
                    return getRandomP(type.random_luck[0], type.random_luck[1]);
                }

                return Integer.parseInt(d[1]);
            }
        }

        return 0;
    }

    public static int getLuckData(ItemStack item) {
        if (isLB(item) && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();

            for (int x = 0; x < lore.size(); ++x) {
                String l = lore.get(x);
                if (l != null && l.startsWith(LUCK_STRING)) {
                    return x;
                }
            }
        }

        return 0;
    }

    public static ItemStack changeLuck(LBType type, ItemStack item, int luck) {
        ItemMeta itemM = item.getItemMeta();
        List<String> lore = null;
        if (itemM.hasLore()) {
            lore = itemM.getLore();
        } else {
            lore = new ArrayList();
        }

        ((List) lore).set(getLuckData(item), type.getFixedLuckString(luck));
        itemM.setLore(lore);
        item.setItemMeta(itemM);
        return item;
    }

    public static String getFirstChestLoc() {
        FileConfiguration f = getFile(getTypes().get(0), 0);
        Iterator var2 = f.getKeys(false).iterator();

        label37:
        while (true) {
            String s;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                s = (String) var2.next();
            } while (f.getConfigurationSection(s) == null);

            Iterator var4 = f.getConfigurationSection(s).getKeys(false).iterator();

            while (true) {
                String g;
                ConfigurationSection c;
                do {
                    if (!var4.hasNext()) {
                        continue label37;
                    }

                    g = (String) var4.next();
                    c = f.getConfigurationSection(s).getConfigurationSection(g);
                } while (c == null);

                Iterator var7 = c.getKeys(false).iterator();

                while (var7.hasNext()) {
                    String i = (String) var7.next();
                    if (i.equalsIgnoreCase("Rolls")) {
                        return s + "." + g;
                    }
                }
            }
        }
    }

    public static void load() {
        File[] files = LuckyBlockPlugin.instance.folder1.listFiles();
        int id = 1;
        String last = "";
        int tot = files.length;
        int totalLoaded = 0;

        for (int a = 0; a < tot; ++id) {
            for (int x = 0; x < files.length; ++x) {
                File f = files[x];
                FileConfiguration c = YamlConfiguration.loadConfiguration(f);
                if (c.getInt("ID") == id && !f.getName().equalsIgnoreCase(last)) {
                    last = f.getName();
                    Material mat = Material.getMaterial(c.getString("Type").toUpperCase());
                    LBType type = new LBType(c.getInt("ID"), c.getString("Name"), (short) c.getInt("MaxLuck"), (short) c.getInt("MinLuck"), c.getString("Folder"), mat, (short) c.getInt("Data"));
                    type.setWorlds(c.getStringList("Worlds"));
                    type.defaultBlock = c.getBoolean("UseDefaultBlock");
                    if (c.getString("CreativeMode") != null && c.getString("CreativeMode").equalsIgnoreCase("false")) {
                        type.creativeMode = false;
                    }

                    String s;
                    if (c.getStringList("Lore") != null) {

                        for (String value : c.getStringList("Lore")) {
                            s = value;
                            type.itemLore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                    }

                    int i;
                    if (c.getStringList("Properties").size() > 0) {
                        for (i = 0; i < c.getStringList("Properties").size(); ++i) {
                            BlockProperty p = BlockProperty.valueOf(c.getStringList("Properties").get(i).toUpperCase());
                            if (!type.properties.contains(p)) {
                                type.properties.add(p);
                            }
                        }
                    }

                    if (c.getStringList("ItemProperties").size() > 0) {
                        for (i = 0; i < c.getStringList("ItemProperties").size(); ++i) {
                            ItemProperty p = ItemProperty.valueOf(c.getStringList("ItemProperties").get(i).toUpperCase());
                            if (!type.itemProperties.contains(p)) {
                                type.itemProperties.add(p);
                            }
                        }
                    }

                    if (c.getBoolean("AllowPlaceSound")) {
                        type.allowplacesound = true;
                    }

                    if (c.getBoolean("AllowBreakSound")) {
                        type.allowbreaksound = true;
                    }

                    if (c.getBoolean("AllowPlaceParticles")) {
                        type.allowplaceparticles = true;
                    }

                    if (c.getBoolean("AllowBreakParticles")) {
                        type.allowbreakparticles = true;
                    }

                    if (c.getBoolean("AllowTickParticles")) {
                        type.allowtickparticles = true;
                    }

                    if (c.getString("PlaceSound") != null) {
                        type.placesound = c.getString("PlaceSound");
                    }

                    if (c.getString("BreakSound") != null) {
                        type.breaksound = c.getString("BreakSound");
                    }

                    if (c.getString("PlaceParticles") != null) {
                        type.placeparticles = c.getString("PlaceParticles");
                    }

                    if (c.getString("BreakParticles") != null) {
                        type.breakparticles = c.getString("BreakParticles");
                    }

                    if (c.getString("TickParticles") != null) {
                        type.tickparticles = c.getString("TickParticles");
                    }

                    type.showName = c.getBoolean("ShowName");
                    type.useSkin = c.getBoolean("UseSkin");
                    if (c.getString("Default") != null && c.getString("Default").equalsIgnoreCase("true")) {
                        type.isDefault = true;
                    }

                    if (c.getString("OnArrowHit") != null) {
                        type.arrowRun = c.getBoolean("OnArrowHit");
                    }

                    if (c.getString("Skin") != null) {
                        type.skin = LuckySkin.valueOf(c.getString("Skin"));
                    }

                    if (c.getString("SkinId") != null) {
                        type.skin_data[0] = c.getString("SkinId");
                    }

                    if (c.getString("SkinValue") != null) {
                        type.skin_data[1] = c.getString("SkinValue");
                    }

                    String[] d;
                    int i2;
                    if (c.getString("a_Random") != null) {
                        s = c.getString("a_Random");
                        d = s.split(",");
                        if (d.length == 2) {
                            i2 = Integer.parseInt(d[0]);
                            int i_lol = Integer.parseInt(d[1]);
                            if (i2 < 5) {
                                i2 = 5;
                            } else if (i2 > 1000) {
                                i_lol = 1000;
                            }

                            if (i_lol < 1) {
                                i_lol = 1;
                            } else if (i_lol > 1000) {
                                i_lol = 1000;
                            }

                            type.a_random = new int[]{i2, i_lol};
                        }
                    }

                    int i_kek;
                    if (c.getString("RandomLuck") != null) {
                        d = c.getString("RandomLuck").split(",");
                        if (d.length == 2) {
                            i_kek = Integer.parseInt(d[0]);
                            i2 = Integer.parseInt(d[1]);
                            if (i_kek < type.minLuck) {
                                i_kek = type.minLuck;
                            } else if (i_kek > type.maxLuck) {
                                i2 = type.maxLuck;
                            }

                            if (i2 < type.minLuck) {
                                i2 = type.minLuck;
                            } else if (i2 > type.maxLuck) {
                                i2 = type.maxLuck;
                            }

                            type.random_luck = new int[]{i_kek, i2};
                        }
                    }

                    type.disabled = c.getBoolean("Disabled");
                    type.setDelay(c.getInt("Delay"));
                    type.rightClick = c.getBoolean("RightClick");
                    if (c.getString("Version") != null) {
                        type.version = c.getInt("Version");
                    }

                    type.showData = c.getBoolean("ShowData");
                    if (c.getStringList("PercentColors") != null && type.hasItemProperty(ItemProperty.CUSTOM_PERCENT_COLORS)) {
                        for (i_kek = 0; i_kek < c.getStringList("PercentColors").size(); ++i_kek) {
                            type.percent_colors[i_kek] = c.getStringList("PercentColors").get(i_kek);
                        }
                    }

                    if (c.getStringList("Offset") != null) {
                        d = new String[c.getStringList("Offset").size()];

                        for (i_kek = 0; i_kek < c.getStringList("Offset").size(); ++i_kek) {
                            d[i_kek] = c.getStringList("Offset").get(i_kek);
                        }

                        type.name_offset = d;
                    }

                    if (c.getString("LuckEnchantment") != null && c.getString("LuckEnchantment").equalsIgnoreCase("true")) {
                        type.luckEnchantment = true;
                    }

                    type.portal = c.getBoolean("Portal");
                    type.generateNaturally = c.getBoolean("GenerateNaturally");
                    if (c.getStringList("SpawnWorlds") != null) {
                        type.spawnWorlds = c.getStringList("SpawnWorlds");
                    }

                    type.spawnRate = c.getInt("SpawnRate");
                    type.spawnWorlds = c.getStringList("SpawnWorlds");
                    if (c.getStringList("SpawnBiomes") != null) {
                        for (i_kek = 0; i_kek < c.getStringList("SpawnBiomes").size(); ++i_kek) {
                            String b = c.getStringList("SpawnBiomes").get(i_kek);
                            if (b != null) {
                                try {
                                    Biome biome = Biome.valueOf(b);
                                    type.spawnBiomes.add(biome);
                                } catch (Exception var36) {
                                    throw new Error("Invalid biome! File: " + f.getName() + ", " + b);
                                }
                            }
                        }
                    }

                    if (c.getString("GenerateWithLuck") != null) {
                        d = c.getString("GenerateWithLuck").split(",");
                        if (d.length == 2) {
                            try {
                                i_kek = Integer.parseInt(d[0]);
                                type.generateWithLuck[0] = i_kek;
                            } catch (Exception var35) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }

                            try {
                                i_kek = Integer.parseInt(d[1]);
                                type.generateWithLuck[1] = i_kek;
                            } catch (Exception var34) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }
                        }
                    }

                    double i2_lol;
                    if (c.getString("HumidityRequired") != null) {
                        d = c.getString("HumidityRequired").split(",");
                        if (d.length == 2) {
                            try {
                                i2_lol = Double.parseDouble(d[0]);
                                type.humidityRequired[0] = i2_lol;
                            } catch (Exception var33) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }

                            try {
                                i2_lol = Double.parseDouble(d[1]);
                                type.humidityRequired[1] = i2_lol;
                            } catch (Exception var32) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }
                        }
                    }

                    if (c.getString("SpawnY") != null) {
                        d = c.getString("SpawnY").split(",");
                        if (d.length == 2) {
                            try {
                                i_kek = Integer.parseInt(d[0]);
                                type.spawnY[0] = i_kek;
                            } catch (Exception var31) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }

                            try {
                                i_kek = Integer.parseInt(d[1]);
                                type.spawnY[1] = i_kek;
                            } catch (Exception var30) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }
                        }
                    }

                    if (c.getString("TemperatureRequired") != null) {
                        d = c.getString("TemperatureRequired").split(",");
                        if (d.length == 2) {
                            try {
                                i2_lol = Double.parseDouble(d[0]);
                                type.temperatureRequired[0] = i2_lol;
                            } catch (Exception var29) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }

                            try {
                                i2_lol = Double.parseDouble(d[1]);
                                type.temperatureRequired[1] = i2_lol;
                            } catch (Exception var28) {
                                throw new Error("Invalid number! File: " + f.getName() + ", " + d[0]);
                            }
                        }
                    }

                    List<Permission> perms = new ArrayList();
                    ConfigurationSection g;
                    String bl;
                    Iterator var38;
                    String s_lol;
                    if (c.getConfigurationSection("Permissions") != null) {
                        i_kek = 0;
                        var38 = c.getConfigurationSection("Permissions").getKeys(false).iterator();

                        while (var38.hasNext()) {
                            s_lol = (String) var38.next();
                            if (i_kek + 1 > allowedPermissions.size()) {
                                throw new Error("Maximum amount for permissions for a LBType is " + allowedPermissions.size() + "!");
                            }

                            g = c.getConfigurationSection("Permissions").getConfigurationSection(s_lol);
                            if (g != null && g.getString("Name") != null) {
                                bl = g.getString("Name");
                                if (!isPermissionAllowed(s_lol)) {
                                    throw new Error("Invalid permission! Permission " + s_lol + " is not allowed.");
                                }

                                if (type.hasPermission(s_lol)) {
                                    throw new Error("Found 2 or more permissions with the name " + s_lol + "!");
                                }

                                Permission perm = new Permission(bl);
                                if (g.getString("Default") != null && PermissionDefault.getByName(g.getString("Default").toUpperCase()) != null) {
                                    perm.setDefault(PermissionDefault.getByName(g.getString("Default").toUpperCase()));
                                }

                                if (g.getString("Description") != null) {
                                    perm.setDescription(g.getString("Description"));
                                }

                                type.permissions.put(s_lol, perm);
                                ++i_kek;
                                perms.add(perm);
                            }
                        }
                    }

                    if (c.getConfigurationSection("AdditionalBlocks") != null) {
                        List<String> lst = new ArrayList();
                        var38 = c.getConfigurationSection("AdditionalBlocks").getKeys(false).iterator();

                        while (var38.hasNext()) {
                            s_lol = (String) var38.next();
                            g = c.getConfigurationSection("AdditionalBlocks").getConfigurationSection(s_lol);
                            if (g != null) {
                                bl = g.getString("Loc");
                                String ma = g.getString("Material");
                                byte data = (byte) g.getInt("Data");
                                if (data < 0) {
                                    data = 0;
                                }

                                if (bl != null && ma != null) {
                                    Material ma1 = Material.getMaterial(ma);
                                    if (ma1 != Material.AIR) {
                                        lst.add(bl + " " + ma1.name() + " " + data);
                                    }
                                }
                            }
                        }

                        if (lst.size() > 0) {
                            String[] d_sas = new String[lst.size()];

                            for (i_kek = 0; i_kek < lst.size(); ++i_kek) {
                                d_sas[i_kek] = lst.get(i_kek);
                            }

                            type.additional_blocks = d_sas;
                        }
                    }

                    if (type.getFolder() != null) {
                        CItem.loadItemsFromFolder(type.getFolder());
                    }

                    type.save();
                    ++a;
                    ++totalLoaded;
                }
            }
        }

        if (totalLoaded > 0) {
            if (totalLoaded == 1) {
                LuckyBlockPlugin.instance.getLogger().info("Loaded " + totalLoaded + " LB Type!");
            } else {
                LuckyBlockPlugin.instance.getLogger().info("Loaded " + totalLoaded + " LB Types!");
            }
        }

        loadRecipes();
    }

    private static void loadRecipes() {
        label67:
        for (int x = 0; x < lbs.size(); ++x) {
            LBType type = lbs.get(x);
            FileConfiguration f = type.getConfig();
            if (f.getBoolean("Craftable")) {
                ConfigurationSection cs = f.getConfigurationSection("Recipes");
                if (cs != null) {
                    Iterator var5 = cs.getKeys(false).iterator();

                    while (true) {
                        String s;
                        List shape;
                        List items;
                        boolean shapeless;
                        ItemStack item;
                        int o;
                        do {
                            if (!var5.hasNext()) {
                                continue label67;
                            }

                            s = (String) var5.next();
                            shape = f.getStringList("Recipes." + s + ".Shape");
                            items = f.getStringList("Recipes." + s + ".Ingredients");
                            int amount = f.getInt("Recipes." + s + ".Amount");
                            o = f.getInt("Recipes." + s + ".Luck");
                            LuckyBlockDrop drop = null;
                            CustomDrop cdrop = null;
                            String a = f.getString("Recipes." + s + ".Drop"); //value might be null
                            if (LuckyBlockDrop.isValid(a)) {
                                drop = LuckyBlockDrop.valueOf(a.toUpperCase());
                            } else if (CustomDropManager.isValid(a)) {
                                cdrop = CustomDropManager.getByName(a.toUpperCase());
                            }

                            shapeless = f.getBoolean("Recipes." + s + ".Shapeless");
                            if (amount < 1) {
                                amount = 1;
                            }

                            item = type.toItemStack(o);
                            item.setAmount(amount);
                            if (drop != null) {
                                item = ItemStackUtils.addLore(item, ChatColor.GRAY + "Drop: " + drop.name());
                            } else if (cdrop != null) {
                                item = ItemStackUtils.addLore(item, ChatColor.GRAY + "Drop: " + cdrop.getName());
                            }
                        } while (shapeless);

                        ShapedRecipe sh = new ShapedRecipe(new NamespacedKey(LuckyBlockPlugin.instance, "lbtype." + ChatColor.stripColor(type.name.toLowerCase()).replace(" ", "") + "." + s), item);
                        sh.shape((String) shape.get(0), (String) shape.get(1), (String) shape.get(2));

                        for (o = 0; o < items.size(); ++o) {
                            String[] d = ((String) items.get(o)).split(" ");
                            if (d.length == 2) {
                                String[] t = d[1].split(",");
                                if (t.length == 2) {
                                    int data = Integer.parseInt(t[1]);
                                    sh.setIngredient(d[0].charAt(0), Material.getMaterial(t[0].toUpperCase()), data);
                                } else {
                                    sh.setIngredient(d[0].charAt(0), Material.getMaterial(d[1].toUpperCase()));
                                }
                            }
                        }

                        Bukkit.getServer().addRecipe(sh);
                    }
                }
            }
        }

    }

    public static boolean isAdditionalBlocksFound() {
        for (int x = 0; x < lbs.size(); ++x) {
            if (lbs.get(x).hasAdditionalBlocks()) {
                return true;
            }
        }

        return false;
    }

    public static LBType getDefaultType() {
        for (int x = 0; x < lbs.size(); ++x) {
            if (lbs.get(x).isDefault) {
                return lbs.get(x);
            }
        }

        return null;
    }

    private static boolean isPermissionAllowed(String name) {
        for (int x = 0; x < allowedPermissions.size(); ++x) {
            if (allowedPermissions.get(x).equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean generateLB() {
        Iterator var1 = lbs.iterator();

        LBType type;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            type = (LBType) var1.next();
        } while (!type.generateNaturally || type.spawnWorlds == null || type.spawnWorlds.size() <= 0);

        return true;
    }

    private static List<LBType> getAllowedTypesToBeGenerated(World world, Location loc, Biome biome) {
        List<LBType> types = new ArrayList();
        Iterator var5 = lbs.iterator();

        while (var5.hasNext()) {
            LBType type = (LBType) var5.next();
            if (type.generateNaturally && type.spawnWorlds != null && type.spawnWorlds.size() > 0 && type.canSpawnIn(world) && type.canSpawnIn(biome) && type.canSpawnIn(loc)) {
                types.add(type);
            }
        }

        return types;
    }

    public static LBType getRandomLBToGenerate(World world, Location loc, Biome biome) {
        List<LBType> types = getAllowedTypesToBeGenerated(world, loc, biome);
        int r = random.nextInt(1000);
        if (types != null && types.size() > 0) {
            while (types.size() > 0) {
                LBType t = types.get(random.nextInt(types.size()));
                if (t.spawnRate > r) {
                    return t;
                }

                types.remove(t);
            }
        }

        return null;
    }

    public List<Recipe> getRecipes() {
        return Bukkit.getServer().getRecipesFor(this.toItemStack());
    }

    public boolean canBreak(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && this.breakItem != null && this.breakItem[0] != null) {
            Material mat = Material.getMaterial(this.breakItem[0].toString());
            return item.getType() == mat;
        } else {
            return true;
        }
    }

    public Object[] getBreakItem() {
        return this.breakItem;
    }

    public boolean hasProperty(BlockProperty property) {
        if (this.properties != null) {
            for (BlockProperty blockProperty : this.properties) {
                if (blockProperty == property) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasItemProperty(ItemProperty property) {
        if (this.itemProperties != null) {
            for (ItemProperty itemProperty : this.itemProperties) {
                if (itemProperty == property) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean IsDefault() {
        return this.isDefault;
    }

    public boolean hasAdditionalBlocks() {
        return this.additional_blocks != null && this.additional_blocks.length > 0 && this.additional_blocks[0] != null;
    }

    public boolean hasNameOffset() {
        return this.name_offset != null && this.name_offset.length > 0 && this.name_offset[0] != null;
    }

    public double[] getOffset(int i) {
        double[] d = new double[3];
        if (this.hasNameOffset() && this.name_offset.length > i && this.name_offset[i] != null) {
            String[] a = this.name_offset[i].split(",");
            d[0] = Double.parseDouble(a[0]);
            d[1] = Double.parseDouble(a[1]);
            d[2] = Double.parseDouble(a[2]);
        }

        return d;
    }

    public boolean isSpawnBoss() {
        return this.spawnBoss;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAnimated() {
        return this.animation_type == 0;
    }

    public byte getAnimation() {
        return this.animation_type;
    }

    public int getVersion() {
        return this.version;
    }

    public short getMinLuck() {
        return this.minLuck;
    }

    public boolean isPortal() {
        return this.portal;
    }

    public boolean isNameVisible() {
        return this.showName;
    }

    public boolean usesSkin() {
        return this.useSkin;
    }

    public short getMaxLuck() {
        return this.maxLuck;
    }

    public String getFolder() {
        return this.folder;
    }

    public short getData() {
        return this.data > 0 ? this.data : 0;
    }

    public Material getType() {
        return this.type;
    }

    public List<String> getWorlds() {
        return this.allowedWorlds;
    }

    public void setWorlds(List<String> worlds) {
        this.allowedWorlds = worlds;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        if (delay > 0 && delay < 999) {
            this.delay = delay;
        }

    }

    public List<BlockProperty> getProperties() {
        return this.properties;
    }

    public List<ItemProperty> getItemProperties() {
        return this.itemProperties;
    }

    public int getId() {
        return this.id;
    }

    void save() {
        int x;
        Error error;
        for (x = 0; x < lbs.size(); ++x) {
            if (lbs.get(x) != this && lbs.get(x).getId() == this.id) {
                error = new Error("Id duplication! " + ChatColor.stripColor(this.name) + " And " + ChatColor.stripColor(lbs.get(x).name));
                throw error;
            }
        }

        if (this.id >= 1 && this.id <= 255) {
            lbs.add(this);
            if (this.version > 225) {
                LuckyBlockPlugin.instance.getLogger().info("LBType:" + this.id + " is newer than the current version of lb!");
            }

            for (x = 0; x < lbs.size(); ++x) {
                if (this.isDefault && lbs.get(x) != this && lbs.get(x).isDefault) {
                    throw new Error("Found more than one default type! " + ChatColor.stripColor(this.name) + " And " + ChatColor.stripColor(lbs.get(x).name));
                }
            }

        } else {
            error = new Error("Id must be between 0 and 256");
            throw error;
        }
    }

    public FileConfiguration getConfig() {
        FileConfiguration file = null;
        String path = LuckyBlockPlugin.instance.folder1.getPath();
        File f = new File(path);
        File[] files = f.listFiles();

        for (int x = 0; x < files.length; ++x) {
            FileConfiguration c = YamlConfiguration.loadConfiguration(files[x]);
            if (c.getInt("ID") == this.id) {
                file = c;
            }
        }

        return file;
    }

    public String getFixedLuckString(int luck) {
        return LUCK_STRING + this.getLuckString(luck);
    }

    public String getFixedLuckString() {
        ChatColor[] colors = new ChatColor[]{ChatColor.RED, ChatColor.WHITE};
        String a = "";
        int mx = String.valueOf(this.random_luck[1]).length();

        for (int x = 0; x < mx; ++x) {
            a = a + colors[x % colors.length] + ChatColor.MAGIC + "O";
        }

        return LUCK_STRING + a;
    }

    public ItemStack toItemStack() {
        return this.toItemStack(0);
    }

    public ItemStack toItemStack(int luck, int amount) {
        return this.toItemStack(luck, null, null, false, amount);
    }

    public ItemStack toItemStack(int luck) {
        return this.toItemStack(luck, null, null);
    }

    public ItemStack toItemStack(int luck, String drop) {
        return this.toItemStack(luck, null, drop);
    }

    public ItemStack toItemStack(int luck, LBOption[] options) {
        return this.toItemStack(luck, options, null);
    }

    public ItemStack toItemStack(int luck, LBOption[] options, String drop) {
        return this.toItemStack(luck, options, drop, false, 1);
    }

    public ItemStack toItemStack(int luck, LBOption[] options, String drop, boolean randomLuck) {
        return this.toItemStack(luck, options, drop, randomLuck, 1);
    }

    public ItemStack toItemStack(int luck, LBOption[] options, String drop, boolean randomLuck, int amount) {
        List<String> list = new ArrayList();
        if (this.itemLore != null && this.itemLore.size() > 0) {
            Iterator var7 = this.itemLore.iterator();

            while (var7.hasNext()) {
                String s = (String) var7.next();
                list.add(s);
            }
        }

        if (randomLuck) {
            list.add(this.getFixedLuckString());
        } else {
            list.add(this.getFixedLuckString(luck));
        }

        ItemStack item = ItemStackUtils.createItem(this.type, 1, this.data, this.name, list);
        if (this.type == Material.SKULL_ITEM && this.useSkin) {
            if (this.skin != null) {
                item = ItemStackUtils.createSkull(item, this.skin.getId(), this.skin.getValue());
            } else {
                if (this.skin_data[0] == null || this.skin_data[1] == null) {
                    throw new NullPointerException("Invalid skull data!");
                }

                item = ItemStackUtils.createSkull(item, this.skin_data[0], this.skin_data[1]);
            }
        }

        if (drop != null && PlaceLuckyBlock.dropToString(drop) != null) {
            item = ItemStackUtils.addLore(item, ChatColor.DARK_PURPLE + "Drop: " + ChatColor.GRAY + drop);
        }

        if (options != null) {
            LBOption[] var10 = options;
            int var9 = options.length;

            for (int var8 = 0; var8 < var9; ++var8) {
                LBOption option = var10[var8];
                if (option == LBOption.PROTECTED) {
                    item = ItemStackUtils.addLore(item, ChatColor.GRAY + "Protected: " + ChatColor.GREEN + "true");
                }
            }
        }

        if (randomLuck) {
            item = ItemStackUtils.setItemTag(item, ItemStackUtils.getItemTag(item).setBoolean("hasRandomLuck", true));
        }

        LuckyDB.makeLucky(item, amount);
        item.setAmount(amount);
        return item;
    }

    public String[] getISound(String sound) {
        return sound.split(" ");
    }

    public String getLuckString(int luck) {
        if (luck > 9999) {
            luck = 9999;
        }

        if (luck < -9999) {
            luck = -9999;
        }

        return "" + this.getPercentColor(luck) + luck + "%";
    }

    public int getRandomP() {
        return getRandomP(this.minLuck, this.maxLuck);
    }

    public String[] getAdditionalBlocks() {
        return this.additional_blocks;
    }

    public ChatColor getPercentColor(int luck) {
        if (this.percent_colors != null) {
            String[] var5 = this.percent_colors;
            int var4 = this.percent_colors.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                String s = var5[var3];
                String[] d = s.split("=");
                String[] g = d[0].split(":");
                ChatColor color = ChatColor.valueOf(d[1].toUpperCase());
                if (g.length < 2) {
                    if (luck == Integer.parseInt(g[0])) {
                        return color;
                    }
                } else if (g[0].equalsIgnoreCase("I")) {
                    if (luck <= Integer.parseInt(g[1])) {
                        return color;
                    }
                } else if (g[1].equalsIgnoreCase("I")) {
                    if (luck >= Integer.parseInt(g[0])) {
                        return color;
                    }
                } else {
                    int i1 = Integer.parseInt(g[0]);
                    int i2 = Integer.parseInt(g[1]);
                    if (luck >= i1 && luck <= i2) {
                        return color;
                    }
                }
            }
        }

        return ChatColor.RED;
    }

    public Permission getPermission(String name) {
        return this.permissions.get(name);
    }

    public boolean hasPermissions() {
        return this.permissions.size() > 0;
    }

    public boolean hasPermission(String name) {
        return this.hasPermissions() && this.permissions.containsKey(name);
    }

    public Material getBlockType() {
        Material mat = null;
        if (this.type == Material.SKULL_ITEM) {
            mat = Material.SKULL;
        } else if (this.type == Material.BED) {
            mat = Material.BED_BLOCK;
        } else {
            mat = this.type;
        }

        return mat;
    }

    public String toString() {
        return "LBType:[" + this.id + "," + ChatColor.stripColor(this.name) + "," + this.type.name() + "," + this.data + "]";
    }

    public boolean canSpawnIn(World world) {
        if (world == null) {
            return false;
        } else {
            if (this.spawnWorlds != null && this.spawnWorlds.size() > 0 && this.generateNaturally) {
                if (this.spawnWorlds.contains("*All*")) {
                    return true;
                }

                Iterator var3 = this.spawnWorlds.iterator();

                while (var3.hasNext()) {
                    String s = (String) var3.next();
                    if (s.equalsIgnoreCase(world.getName())) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean canSpawnIn(Biome biome) {
        if (this.generateNaturally) {
            if (biome == null) {
                return true;
            } else {
                return this.spawnBiomes == null || this.spawnBiomes.size() <= 0 || this.spawnBiomes.contains(biome);
            }
        } else {
            return false;
        }
    }

    public boolean canSpawnIn(Location loc) {
        if (this.generateNaturally) {
            if (loc == null) {
                return false;
            } else {
                double t;
                if (this.humidityRequired[0] > -1.0D && this.humidityRequired[1] > -1.0D) {
                    t = loc.getBlock().getHumidity();
                    if (t <= this.humidityRequired[0] || t >= this.humidityRequired[1]) {
                        return false;
                    }
                }

                if (this.temperatureRequired[0] > -1.0D && this.temperatureRequired[1] > -1.0D) {
                    t = loc.getBlock().getTemperature();
                    if (t <= this.temperatureRequired[0] || t >= this.temperatureRequired[1]) {
                        return false;
                    }
                }

                return this.spawnY[0] <= 0 || this.spawnY[1] <= 0 || (loc.getBlockY() > this.spawnY[0] && loc.getBlockY() < this.spawnY[1]);
            }
        } else {
            return false;
        }
    }
}
