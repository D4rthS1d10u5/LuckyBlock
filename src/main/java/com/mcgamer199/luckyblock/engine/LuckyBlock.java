//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.engine;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.Enchantments.Glow;
import com.mcgamer199.luckyblock.Enchantments.Lightning;
import com.mcgamer199.luckyblock.Enchantments.ReflectProtectionEnchantment;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.event.lb.*;
import com.mcgamer199.luckyblock.event.lb.block.BreakLuckyBlock;
import com.mcgamer199.luckyblock.event.lb.block.PlaceLuckyBlock;
import com.mcgamer199.luckyblock.event.lb.LBShootEvent;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.customentity.EntitySuperWitherSkeleton;
import com.mcgamer199.luckyblock.customentity.boss.EntityBossWitch;
import com.mcgamer199.luckyblock.inventory.event.EntitiesGui;
import com.mcgamer199.luckyblock.inventory.event.Gui;
import com.mcgamer199.luckyblock.inventory.event.LBGui;
import com.mcgamer199.luckyblock.inventory.event.RecipeLB;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBEffects;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LBType.BlockProperty;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.resources.Trophy;
import com.mcgamer199.luckyblock.tags.EntityTags;
import com.mcgamer199.luckyblock.world.Engine.LuckyBlockWorld;
import com.mcgamer199.luckyblock.world.Engine.WorldGenerateLB;
import com.mcgamer199.luckyblock.world.Structures.Structure;
import com.mcgamer199.luckyblock.command.engine.ConstructTabCompleter;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.logic.IRange;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.title.ITitle;
import com.mcgamer199.luckyblock.title.Title_1_12_R1;
import com.mcgamer199.luckyblock.yottaevents.LuckyDB;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.CustomEntityEvents;
import com.mcgamer199.luckyblock.entity.CustomEntityLoader;
import com.mcgamer199.luckyblock.event.lb.SpawnEggEvents;
import com.mcgamer199.luckyblock.inventory.event.ItemMaker;
import com.mcgamer199.luckyblock.logic.ITask;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class LuckyBlock extends JavaPlugin {
    public static final String pluginname = "LuckyBlock";
    public static final String version = "2.2.5";
    static final List<String> no_versions = Arrays.asList("2.2.4");
    public static final int version_number = 225;
    public static LuckyBlock instance;
    public static Random randoms = new Random();
    public File detectorsF;
    public FileConfiguration detectors;
    public File configf;
    public FileConfiguration config;
    public File folder1;
    public static Glow enchantment_glow = new Glow(23);
    public static Lightning enchantment_lightning = new Lightning(25);
    public static ReflectProtectionEnchantment enchantment_reflect_prot = new ReflectProtectionEnchantment(27);
    public static String lang = "en_US";
    public static String command_main = "lb";
    private static ITitle title;
    public static int[] dungeon_loc = new int[]{1, 0};
    public static String sounds_file = "sounds";
    public static boolean allowLBGeneration;

    public LuckyBlock() {
        this.detectorsF = new File(this.getDataFolder() + File.separator + "Data/detectors.yml");
        this.detectors = YamlConfiguration.loadConfiguration(this.detectorsF);
        this.configf = new File(this.getDataFolder() + File.separator + "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configf);
        this.folder1 = new File(this.getDataFolder() + File.separator + "Types/");
    }

    public void onEnable() {
        instance = this;
        this.getLogger().info("LuckyBlock , 2.2.5 Enabled.");
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

        allowLBGeneration = this.config.getBoolean("Allow.LBGeneration");
        com.mcgamer199.luckyblock.customdrop.CustomDropManager.registerDrop(new com.mcgamer199.luckyblock.customdrop.EffectsDrop());
        com.mcgamer199.luckyblock.customdrop.CustomDropManager.registerDrop(new com.mcgamer199.luckyblock.customdrop.FakeTntDrop());
        com.mcgamer199.luckyblock.customdrop.CustomDropManager.registerDrop(new com.mcgamer199.luckyblock.customdrop.RandomBlockDrop());
        CustomDropManager.registerDrop(new com.mcgamer199.luckyblock.customdrop.DropInventoryDrop());
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
        pm.registerEvents(new SomeEvents(), this);
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
        pm.registerEvents(new SpawnEggEvents(), this);
        Structure.loadStructures();
        EntityTags.load();
        this.loadCustomEntities();
        this.loadDetectors();
        LuckyBlockAPI.loadLuckyBlocks();
    }

    public void onDisable() {
        Iterator var2 = CustomEntity.entities.iterator();

        while(var2.hasNext()) {
            CustomEntity c = (CustomEntity)var2.next();
            c.hideBar(false);
        }

        LuckyDB.checkSave();
        this.getLogger().info("LuckyBlock , 2.2.5 Disabled.");
    }

    public void loadDetectors() {
        try {
            ConfigurationSection section = this.detectors.getConfigurationSection("Detectors");

            for(int x = 0; x < section.getKeys(false).size(); ++x) {
                ConfigurationSection cs = section.getConfigurationSection(section.getKeys(false).toArray()[x].toString());
                Detector detector = null;

                for(int y = 0; y < cs.getKeys(false).size(); ++y) {
                    String s = cs.getKeys(false).toArray()[y].toString();
                    if (s.equalsIgnoreCase("ID")) {
                        detector = new Detector(cs.getInt(s));
                    }

                    int i;
                    if (s.equalsIgnoreCase("Range")) {
                        int dx = cs.getInt("Range.x");
                        i = cs.getInt("Range.y");
                        int dz = cs.getInt("Range.z");
                        IRange range = new IRange(dx, i, dz);
                        detector.setRange(range);
                    }

                    if (s.equalsIgnoreCase("Blocks")) {
                        List<String> bs = cs.getStringList(s);

                        for(i = 0; i < bs.size(); ++i) {
                            detector.addBlock((String)bs.get(i));
                        }
                    }

                    if (detector != null) {
                        LuckyBlockAPI.detectors.add(detector);
                    }
                }
            }
        } catch (Exception var11) {
            ;
        }

    }

    static boolean contains(String s) {
        for(int x = 0; x < no_versions.size(); ++x) {
            if (((String)no_versions.get(x)).equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void reloadConfig() {
        LB.cache.clear();
        super.reloadConfig();
        try {
            LuckyDB.checkSave();
            LuckyDB.loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String d() {
        return instance.getDataFolder() + File.separator;
    }

    public void Loops(final LB lb) {
        final Block block = lb.getBlock();
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(this, new Runnable() {
            public void run() {
                if (block.getRelative(BlockFace.UP).getType() == Material.FIRE) {
                    if (lb.getType().getProperties().contains(BlockProperty.FIRE_RESISTANCE)) {
                        block.getRelative(BlockFace.UP).setType(Material.AIR);
                    } else {
                        task.run();
                    }
                }

            }
        }, 2L, 3L));
    }

    public void LuckyBlockConfig() {
        this.detectors.options().copyDefaults(true);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) == 9 && calendar.get(5) == 31 && LBType.getDefaultType() != null) {
            LBEffects.md(LBType.getDefaultType(), Material.PUMPKIN, (short)0);
        }

        LBType.load();
        this.loadRecipes();
    }

    public void loadEnchantments() {
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set((Object)null, true);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            try {
                Enchantment.registerEnchantment(enchantment_glow);
                Enchantment.registerEnchantment(enchantment_lightning);
                Enchantment.registerEnchantment(enchantment_reflect_prot);
            } catch (IllegalArgumentException var2) {
                ;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void loadWorlds() {
    }

    private void loadCustomEntities() {
        CustomEntity.classes.clear();
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntityElementalCreeper());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntityKillerSkeleton());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntityGuardian());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntityLuckyVillager());
        CustomEntity.classes.add(new EntitySuperWitherSkeleton());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntitySoldier());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntitySuperSlime());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.boss.EntityKnight());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.EntityTalkingZombie());
        CustomEntity.classes.add(new EntityBossWitch());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.boss.EntityMC());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.boss.EntityUnderwaterBoss());
        CustomEntity.classes.add(new com.mcgamer199.luckyblock.customentity.boss.EntityFootballPlayer());
        this.loadCustomEntities1();
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new LuckyBlockWorld();
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return p instanceof WorldEditPlugin ? (WorldEditPlugin)p : null;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return p instanceof WorldGuardPlugin ? (WorldGuardPlugin)p : null;
    }

    public static boolean isWorldEditValid() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return p != null;
    }

    public static boolean isWorldGuardValid() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return p != null;
    }

    private void loadRecipes() {
        ItemStack craftLB = ItemMaker.createItem(Material.NOTE_BLOCK, 1, 0, ChatColor.YELLOW + "Lucky Crafting Table");
        NamespacedKey k = new NamespacedKey(this, "lucky_crafting_table");
        ShapedRecipe sh = new ShapedRecipe(k, craftLB);
        sh.shape(new String[]{"cac", "aba", "cac"});
        sh.setIngredient('a', Material.GOLD_BLOCK);
        sh.setIngredient('b', Material.WORKBENCH);
        sh.setIngredient('c', Material.LAPIS_BLOCK);
        Bukkit.getServer().addRecipe(sh);
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(this, new Runnable() {
            public void run() {
                LuckyCraftingTable.load();
            }
        }, 6L));
        ItemStack adv = LBItem.A_LUCKY_TOOL.getItem();
        NamespacedKey k1 = new NamespacedKey(this, "advanced_lucky_tool");
        ShapedRecipe sh1 = new ShapedRecipe(k1, adv);
        sh1.shape(new String[]{" B ", "BCB", "AAA"});
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

    void a(String a) {
        if (!(new File(d() + a)).exists()) {
            this.saveResource(a, false);
        }

    }

    public static boolean reload_lang() {
        return MyTasks.reloadLang();
    }

    public static ITitle getTitle() {
        return title;
    }

    public static boolean isDebugEnabled() {
        return IObjects.getValue("debug_enabled").equals(true);
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

    public static boolean action_bar_messages() {
        return IObjects.getValue("actionbar_messages").toString().equalsIgnoreCase("true");
    }

    private void loadCustomEntities1() {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(this, new Runnable() {
            public void run() {
                CustomEntityLoader.load();
            }
        }, 3L));
    }
}
