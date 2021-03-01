//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.api.customdrop.impl.DropInventoryDrop;
import com.mcgamer199.luckyblock.api.customdrop.impl.EffectsDrop;
import com.mcgamer199.luckyblock.api.customdrop.impl.FakeTntDrop;
import com.mcgamer199.luckyblock.api.customdrop.impl.RandomBlockDrop;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.api.title.ITitle;
import com.mcgamer199.luckyblock.api.title.Title_1_12_R1;
import com.mcgamer199.luckyblock.command.engine.ConstructTabCompleter;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.enchantments.Glow;
import com.mcgamer199.luckyblock.enchantments.Lightning;
import com.mcgamer199.luckyblock.enchantments.ReflectProtectionEnchantment;
import com.mcgamer199.luckyblock.api.IObjects;
import com.mcgamer199.luckyblock.lb.LBEffects;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.listeners.*;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.resources.Trophy;
import com.mcgamer199.luckyblock.structures.Structure;
import com.mcgamer199.luckyblock.tags.EntityTags;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.yottaevents.LuckyDB;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class LuckyBlockPlugin extends JavaPlugin {

    public static final String pluginname = "LuckyBlock";
    public static final String version = "2.2.5";
    public static final int version_number = 225;
    static final List<String> no_versions = Arrays.asList("2.2.4");
    public static LuckyBlockPlugin instance;
    public static Random randoms = new Random();
    public static Glow enchantment_glow = new Glow(23);
    public static Lightning enchantment_lightning = new Lightning(25);
    public static ReflectProtectionEnchantment enchantment_reflect_prot = new ReflectProtectionEnchantment(27);
    public static String lang = "en_US";
    public static String command_main = "lb";
    public static int[] dungeon_loc = new int[]{1, 0};
    public static String sounds_file = "sounds";
    public static boolean allowLBGeneration;
    private static ITitle title;
    public File detectorsF;
    public FileConfiguration detectors;
    public File configf;
    public FileConfiguration config;
    public File folder1;

    public LuckyBlockPlugin() {
        this.detectorsF = new File(this.getDataFolder() + File.separator + "Data/detectors.yml");
        this.detectors = YamlConfiguration.loadConfiguration(this.detectorsF);
        this.configf = new File(this.getDataFolder() + File.separator + "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configf);
        this.folder1 = new File(this.getDataFolder() + File.separator + "Types/");
    }

    static boolean contains(String s) {
        for (String no_version : no_versions) {
            if (no_version.equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }

    public static String d() {
        return instance.getDataFolder() + File.separator;
    }

    public static boolean isWorldEditValid() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return p != null;
    }

    public static boolean isWorldGuardValid() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return p != null;
    }

    public static boolean reload_lang() {
        return IObjects.changeLanguage();
    }

    public static ITitle getTitle() {
        return title;
    }

    public static boolean isDebugEnabled() {
        return IObjects.getValue("debug_enabled").equals(true);
    }

    public static boolean action_bar_messages() {
        return IObjects.getValue("actionbar_messages").toString().equalsIgnoreCase("true");
    }

    public void onEnable() {
        instance = this;
        this.getLogger().info("Created by MCGamer199!");
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.getConfig().options().copyDefaults(true);
        String v = this.getConfig().getString("version");
        if (!v.equalsIgnoreCase("2.2.5")) {
            if (contains(v)) {
                this.getLogger().info("Change version in the config to 2.2.5");
            } else {
                this.getLogger().info("Outdated config! please delete your current config file.");
            }
        }

        if (!this.s_title()) {
            this.getLogger().info("Titles from this plugin don't support your server. Turn them off in the config to disable this message.");
        }

        lang = this.config.getString("lang_file");
        ILBCmd.color = ChatColor.valueOf(this.config.getString("CommandPage.color1").toUpperCase());
        ILBCmd.color1 = ChatColor.valueOf(this.config.getString("CommandPage.color2").toUpperCase());
        ILBCmd.color2 = ChatColor.valueOf(this.config.getString("CommandPage.color3").toUpperCase());
        ILBCmd.color3 = ChatColor.valueOf(this.config.getString("CommandPage.color4").toUpperCase());
        ILBCmd.color4 = ChatColor.valueOf(this.config.getString("CommandPage.color5").toUpperCase());
        if (this.config.getString("sounds_file") != null) {
            sounds_file = this.config.getString("sounds_file");
        }
        LuckyBlock.setPersistent(config.getBoolean("persistent", true));
        allowLBGeneration = this.config.getBoolean("Allow.LBGeneration");
        CustomDropManager.registerDrop(new EffectsDrop());
        CustomDropManager.registerDrop(new FakeTntDrop());
        CustomDropManager.registerDrop(new RandomBlockDrop());
        CustomDropManager.registerDrop(new DropInventoryDrop());
        PluginManager pm = this.getServer().getPluginManager();
        this.loadFiles();
        IObjects.load();
        this.loadWorlds();
        LuckyBlockAPI.loadPortals();
        Trophy.load();
        this.loadEnchantments();
        this.LuckyBlockConfig();
        ILBCmd.loadCP();
        LBCommand cmd = new LBCommand();
        command_main = this.config.getString("LuckyBlockCommand.Command");
        this.getCommand(command_main).setExecutor(cmd);
        this.getCommand(command_main).setTabCompleter(new ConstructTabCompleter());
        pm.registerEvents(new PlaceLuckyBlock(), this);
        pm.registerEvents(new BreakLuckyBlock(), this);
        pm.registerEvents(new LuckyBlockEvents(), this);
        pm.registerEvents(new Gui(), this);
        pm.registerEvents(new LuckyBlockWorld(), this);
        pm.registerEvents(new InteractLB(), this);
        pm.registerEvents(new CraftLB(), this);
        pm.registerEvents(new LBShootEvent(), this);
        pm.registerEvents(new LBGui(), this);
        pm.registerEvents(new EntityEvents(), this);
        pm.registerEvents(new EntitiesGui(), this);
        pm.registerEvents(new LBSpawnBoss(), this);
        pm.registerEvents(new RecipeLB(), this);
        pm.registerEvents(new LBBossEvents(), this);
        pm.registerEvents(new WorldGenerateLB(), this);
        pm.registerEvents(new CustomEntityEvents(), this);
        Structure.loadStructures();
        EntityTags.load();
        CustomEntityManager.initDefaults();
        CustomEntityManager.loadEntities();
        this.loadDetectors();
        LuckyBlockAPI.loadLuckyBlocks();
    }

    @Override
    public void onDisable() {
        LuckyBlock.saveAll();
        CustomEntityManager.saveAll();
        //LuckyDB.checkSave();
        this.getLogger().info("LuckyBlock, 2.2.5 Disabled.");
    }

    public void loadDetectors() {
        try {
            ConfigurationSection section = this.detectors.getConfigurationSection("Detectors");

            for (int x = 0; x < section.getKeys(false).size(); ++x) {
                ConfigurationSection cs = section.getConfigurationSection(section.getKeys(false).toArray()[x].toString());
                Detector detector = null;

                for (int y = 0; y < cs.getKeys(false).size(); ++y) {
                    String s = cs.getKeys(false).toArray()[y].toString();
                    if (s.equalsIgnoreCase("ID")) {
                        detector = new Detector(cs.getInt(s));
                    }

                    if (detector != null) {
                        int i;
                        if (s.equalsIgnoreCase("Range")) {
                            int dx = cs.getInt("Range.x");
                            i = cs.getInt("Range.y");
                            int dz = cs.getInt("Range.z");
                            Vector range = new Vector(dx, i, dz);
                            detector.setRange(range);
                        }

                        if (s.equalsIgnoreCase("Blocks")) {
                            List<String> bs = cs.getStringList(s);

                            for (i = 0; i < bs.size(); ++i) {
                                detector.addBlock(bs.get(i));
                            }
                        }

                        LuckyBlockAPI.detectors.add(detector);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void reloadConfig() {
        LuckyBlock.cache.clear();
        super.reloadConfig();
        try {
            LuckyDB.checkSave();
            LuckyDB.loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LuckyBlockConfig() {
        this.detectors.options().copyDefaults(true);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DATE) == 31 && LBType.getDefaultType() != null) {
            LBEffects.md(LBType.getDefaultType(), Material.PUMPKIN, (short) 0);
        }

        LBType.load();
        this.loadRecipes();
    }

    public void loadEnchantments() {
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            try {
                Enchantment.registerEnchantment(enchantment_glow);
                Enchantment.registerEnchantment(enchantment_lightning);
                Enchantment.registerEnchantment(enchantment_reflect_prot);
            } catch (IllegalArgumentException ignored) {}
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void loadWorlds() {
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new LuckyBlockWorld();
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return p instanceof WorldEditPlugin ? (WorldEditPlugin) p : null;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return p instanceof WorldGuardPlugin ? (WorldGuardPlugin) p : null;
    }

    private void loadRecipes() {
        ItemStack craftLB = ItemStackUtils.createItem(Material.NOTE_BLOCK, 1, 0, ChatColor.YELLOW + "Lucky Crafting Table");
        NamespacedKey k = new NamespacedKey(this, "lucky_crafting_table");
        ShapedRecipe sh = new ShapedRecipe(k, craftLB);
        sh.shape("cac", "aba", "cac");
        sh.setIngredient('a', Material.GOLD_BLOCK);
        sh.setIngredient('b', Material.WORKBENCH);
        sh.setIngredient('c', Material.LAPIS_BLOCK);
        Bukkit.getServer().addRecipe(sh);
        Scheduler.later(LuckyCraftingTable::load, 6);
        ItemStack adv = LBItem.A_LUCKY_TOOL.getItem();
        NamespacedKey k1 = new NamespacedKey(this, "advanced_lucky_tool");
        ShapedRecipe sh1 = new ShapedRecipe(k1, adv);
        sh1.shape(" B ", "BCB", "AAA");
        sh1.setIngredient('A', Material.DIAMOND_BLOCK);
        sh1.setIngredient('B', Material.NETHER_STAR);
        sh1.setIngredient('C', Material.SPONGE);
        Bukkit.getServer().addRecipe(sh1);
    }

    private void loadFiles() {
        if (!(new File(d() + "data/sounds/sounds.yml")).exists()) {
            this.saveResource("data/sounds/sounds.yml", false);
        }

        if (!(new File(d() + "data/messages/en_US.yml")).exists()) {
            this.saveResource("data/messages/en_US.yml", false);
        }

        if (!(new File(d() + "Types/LuckyBlock.yml")).exists()) {
            this.saveResource("Types/LuckyBlock.yml", false);
        }

        if (!(new File(d() + "data/detectors.yml")).exists()) {
            this.saveResource("data/detectors.yml", false);
        }

        if (!(new File(d() + "data/structures.yml")).exists()) {
            this.saveResource("data/structures.yml", false);
        }

        if (!(new File(d() + "data/plugin/str/witch/structure.schematic")).exists()) {
            this.saveResource("data/plugin/str/witch/structure.schematic", true);
        }

        if (!(new File(d() + "data/plugin/str/witch/chests.yml")).exists()) {
            this.saveResource("data/plugin/str/witch/chests.yml", true);
        }

        IObjects.loadLuckyBlockFiles();
        IObjects.load1();
    }

    private boolean s_title() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException var3) {
            return false;
        }

        if (version.equals("v1_12_R1")) {
            title = new Title_1_12_R1();
        }

        return title != null;
    }
}
