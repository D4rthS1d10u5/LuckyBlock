package com.mcgamer199.luckyblock.engine;

import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.command.LBCRecDeleted;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.events.LanguageChangedEvent;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.util.EffectUtils;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@UtilityClass
public class IObjects {

    public static final ItemStack ITEM_SPAWN_ENTITY;
    public static FileConfiguration fLang;
    static List<String> missing = new ArrayList();
    private static boolean loaded = false;
    private static boolean loaded1 = false;
    private static final List<Object> objects = new ArrayList();
    private static final HashMap<String, Object> options = new HashMap();
    private static final HashMap<String, File> storedFiles = new HashMap();
    private static final HashMap<String, String> storedSounds = new HashMap();
    private static final HashMap<String, String> storedStrings = new HashMap();
    private static boolean MISSING_STRINGS = false;
    private static boolean MISSING_SOUNDS = false;

    static {
        ITEM_SPAWN_ENTITY = ItemStackUtils.createItem(Material.MONSTER_EGG, 1, 0,"§aSpawn Entity");
    }

    public static Object getValue(String key) {
        return options.containsKey(key) ? options.get(key) : null;
    }

    public static LBCommand getCommand(int place) {
        if (objects.size() > place) {
            Object o = objects.get(place);
            if (o instanceof LBCommand) {
                return (LBCommand) o;
            } else {
                throw new Error("Couldn't find object!");
            }
        } else {
            throw new ArrayIndexOutOfBoundsException("Couldn't find object!");
        }
    }

    public static List<LBCommand> getCommands() {
        List<LBCommand> c = new ArrayList();

        for (int x = 0; x < objects.size(); ++x) {
            Object o = objects.get(x);
            if (o instanceof LBCommand) {
                c.add((LBCommand) o);
            }
        }

        return c;
    }

    public static Object getObj(int place) {
        if (objects.size() > place) {
            return objects.get(place);
        } else {
            throw new ArrayIndexOutOfBoundsException("Couldn't find object!");
        }
    }

    public static File getStoredFile(String name) {
        return storedFiles.getOrDefault(name, null);
    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            objects.add(new com.mcgamer199.luckyblock.command.LBClearLbs());
            objects.add(new com.mcgamer199.luckyblock.command.LBCDropList());
            objects.add(new com.mcgamer199.luckyblock.command.LBCGive());
            objects.add(new com.mcgamer199.luckyblock.command.LBCLbs());
            objects.add(new com.mcgamer199.luckyblock.command.LBCPlaceLB());
            objects.add(new com.mcgamer199.luckyblock.command.LBCRegion());
            objects.add(new com.mcgamer199.luckyblock.command.LBCTypes());
            objects.add(new com.mcgamer199.luckyblock.command.LBCWorld());
            objects.add(new com.mcgamer199.luckyblock.command.LBCSpawnEgg());
            objects.add(new com.mcgamer199.luckyblock.command.LBCHelp());
            objects.add(new com.mcgamer199.luckyblock.command.LBCVersion());
            objects.add(new com.mcgamer199.luckyblock.command.LBCDetector());
            objects.add(new com.mcgamer199.luckyblock.command.LBCSetDrop());
            objects.add(new com.mcgamer199.luckyblock.command.LBCLbItem());
            objects.add(new com.mcgamer199.luckyblock.command.LBCInfo());
            objects.add(new com.mcgamer199.luckyblock.command.LBCLang());
            objects.add(new com.mcgamer199.luckyblock.command.LBCResourcePack());
            options.put("rp_enabled", LuckyBlockPlugin.instance.getConfig().getBoolean("resourcepack.enabled"));
            options.put("rp_url", LuckyBlockPlugin.instance.getConfig().getString("resourcepack.url"));
            options.put("lv_spawn", LuckyBlockPlugin.instance.getConfig().getBoolean("spawn.lucky_villager"));
            options.put("actionbar_messages", LuckyBlockPlugin.instance.getConfig().getBoolean("ActionbarMessages"));
            options.put("debug_enabled", LuckyBlockPlugin.instance.getConfig().getBoolean("debug"));
            objects.add(new com.mcgamer199.luckyblock.command.LBCGenerate());
            objects.add(new com.mcgamer199.luckyblock.command.LBCSaveStructure());
            objects.add(new com.mcgamer199.luckyblock.command.LBCommandDesc());
            options.put("lct_nameVisible", LuckyBlockPlugin.instance.getConfig().getBoolean("show_display_name"));
            objects.add(new com.mcgamer199.luckyblock.command.LBCBook());
            objects.add(new com.mcgamer199.luckyblock.command.LBCSaveItem());
            objects.add(new LBCRecDeleted());
            EffectUtils.loadSounds();
            loadStrings();
        }
    }

    public static void load1() {
        if (!loaded1) {
            loaded1 = true;
            storedFiles.put("witch_structure", new File(LuckyBlockPlugin.d() + "data/plugin/str/witch/structure.schematic"));
            storedFiles.put("witch_chests", new File(LuckyBlockPlugin.d() + "data/plugin/str/witch/chests.yml"));
        }
    }

    private static boolean loadStrings() {
        missing.clear();
        File file = new File(LuckyBlockPlugin.d() + "data/messages/" + LuckyBlockPlugin.lang + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(file);
        fLang = c;
        addString(c, "invalid_command", "command.main.invalid_command");
        addString(c, "lb.invalid_type", "command.main.invalid_type");
        addString(c, "error", "command.main.error");
        addString(c, "invalid_number", "command.main.invalid_number");
        addString(c, "command.invalid_sender", "command.main.invalid_sender");
        addString(c, "command.invalid_args", "command.main.invalid_args");
        addString(c, "invalid_player", "command.main.invalid_player");
        addString(c, "command.tf", "command.main.true_false");
        addString(c, "no_perm", "command.main.no_perm");
        addString(c, "command.no_perm", "command.main.no_perm_command");
        addString(c, "lb.type_disabled", "command.main.type_disabled");
        addString(c, "invalid_uid", "command.main.invalid_uid");
        addString(c, "invalid_block", "command.main.invalid_block");
        addString(c, "world_edit_not_found", "command.main.world_edit_not_found");
        addString(c, "schematic_error", "command.main.schematic_error");
        addString(c, "lb.invalid_drop", "command.main.invalid_drop");
        addString(c, "lb.invalid_tag", "command.main.invalid_tag");
        addString(c, "lb.invalid_lb", "command.main.invalid_lb");
        addString(c, "invalid_file", "command.main.invalid_file");
        addString(c, "invalid_sound", "command.main.invalid_sound");
        addString(c, "invalid_item", "command.main.invalid_item");
        addString(c, "command.placelb.success", "command.place_lb.success");
        addString(c, "command.placelb.error", "command.place_lb.error");
        addString(c, "command.givelb.success", "command.give_lb.success");
        addString(c, "command.givelb.invalid_luck", "command.give_lb.invalid_luck");
        addString(c, "command.givelb.invalid_options", "command.give_lb.invalid_options");
        addString(c, "command.book.1", "command.book.1");
        addString(c, "command.book.2", "command.book.2");
        addString(c, "command.reload.success", "command.reload.success");
        addString(c, "command.clearlbs.success", "command.clear_lbs.success");
        addString(c, "command.clearlbs.no_lb", "command.clear_lbs.no_lb");
        addString(c, "command.thor.success", "command.thor.success");
        addString(c, "command.detector.success", "command.detector.success");
        addString(c, "command.types.page", "command.types.page");
        addString(c, "command.types.disabled", "command.types.disabled");
        addString(c, "command.types.no_type", "command.types.no_type");
        addString(c, "command.lbs.page", "command.lbs.page");
        addString(c, "command.lbs.no_lb", "command.lbs.no_lb");
        addString(c, "command.lbs.data.drop", "command.lbs.data.drop");
        addString(c, "command.lbs.data.placedby", "command.lbs.data.placed_by");
        addString(c, "command.lbs.data.luck", "command.lbs.data.luck");
        addString(c, "command.lbs.data.type", "command.lbs.data.type");
        addString(c, "command.lbs.data.hover", "command.lbs.data.hover");
        addString(c, "command.region.invalid_action", "command.region.invalid_action");
        addString(c, "command.region.no_selection", "command.region.no_selection");
        addString(c, "command.region.progress", "command.region.progress");
        addString(c, "command.region.no_changes", "command.region.no_changes");
        addString(c, "command.region.action1.success", "command.region.action1.success");
        addString(c, "command.region.action1.error", "command.region.action1.error");
        addString(c, "command.region.action2", "command.region.action2");
        addString(c, "command.region.action3", "command.region.action3");
        addString(c, "command.region.action4", "command.region.action4");
        addString(c, "command.spawnegg.success", "command.spawn_egg.success");
        addString(c, "command.spawnegg.invalid_entity", "command.spawn_egg.invalid_entity");
        addString(c, "command.spawnegg.not_allowed", "command.spawn_egg.not_allowed");
        addString(c, "command.droplist.page", "command.drop_list.page");
        addString(c, "command.droplist.desc", "command.drop_list.desc");
        addString(c, "command.droplist.no_desc", "command.drop_list.no_desc");
        addString(c, "command.droplist.drop_options", "command.drop_list.drop_options");
        addString(c, "command.lbitem.success", "command.lb_item.success");
        addString(c, "command.lbitem.invalid_item", "command.lb_item.invalid_item");
        addString(c, "command.setdrop.success", "command.set_drop.success");
        addString(c, "command.version", "command.version.plugin_version");
        addString(c, "command.setlang.success", "command.set_lang.success");
        addString(c, "command.resourcepack.success", "command.resource_pack.success");
        addString(c, "command.resourcepack.disabled", "command.resource_pack.disabled");
        addString(c, "command.resourcepack.invalid_cmd", "command.resource_pack.invalid_command");
        addString(c, "command.resourcepack.invalid_resourcepack", "command.resource_pack.invalid_resourcepack");
        addString(c, "command.generate.success", "command.generate.success");
        addString(c, "command.generate.specify_structure", "command.generate.specify_structure");
        addString(c, "command.generate.invalid_structure", "command.generate.invalid_structure");
        addString(c, "command.savestructure.success", "command.save_structure.success");
        addString(c, "command.savestructure.file_exists", "command.save_structure.file_exists");
        addString(c, "command.savestructure.max_count_error", "command.save_structure.max_count_error");
        addString(c, "command.savestructure.y_error", "command.save_structure.y_error");
        addString(c, "command.savestructure.area_error", "command.save_structure.area_error");
        addString(c, "command.commanddesc.description", "command.command_desc.description");
        addString(c, "command.commanddesc.no_description", "command.command_desc.no_description");
        addString(c, "command.commanddesc.invalid_command", "command.command_desc.invalid_command");
        addString(c, "command.gui.success", "command.open_gui.success");
        addString(c, "command.lctstop.success", "command.lct_stop.success");
        addString(c, "command.lctstop.error", "command.lct_stop.error");
        addString(c, "command.lctextra.success", "command.lct_extra.success");
        addString(c, "command.lctextra.error1", "command.lct_extra.error1");
        addString(c, "command.lctextra.error2", "command.lct_extra.error2");
        addString(c, "command.runall.success", "command.run_all.success");
        addString(c, "command.runall.no_lb", "command.run_all.no_lb");
        addString(c, "command.help.1", "command.help.1");
        addString(c, "command.help.2", "command.help.2");
        addString(c, "command.help.3", "command.help.3");
        addString(c, "command.help.4", "command.help.4");
        addString(c, "event.detector.place", "event.detector.place");
        addString(c, "event.detector.break", "event.detector.break");
        addString(c, "event.detector.search", "event.detector.search");
        addString(c, "event.detector.found", "event.detector.found");
        addString(c, "event.detector.no_lb", "event.detector.no_lb");
        addString(c, "event.detector.no_space", "event.detector.no_space");
        addString(c, "event.placelb.error.world", "event.place_lb.error_world");
        addString(c, "event.placelb.error.permission", "event.place_lb.no_permission");
        addString(c, "event.placelb.error.disabled", "event.place_lb.disabled");
        addString(c, "event.placelb.error.space", "event.place_lb.no_space");
        addString(c, "event.breaklb.error.permission", "event.break_lb.no_permission");
        addString(c, "event.craftlb.error.permission", "event.craft_lb.no_permission");
        addString(c, "drops.repair.1", "event.drops.repair.1");
        addString(c, "drops.repair.2", "event.drops.repair.2");
        addString(c, "drops.enchant_item.success", "event.drops.enchant_item.success");
        addString(c, "drops.enchant_item.fail", "event.drops.enchant_item.fail");
        addString(c, "drops.potion_effect.invalid_effect", "event.drops.potion_effect.invalid_effect");
        addString(c, "drops.setblock.invalid_material", "event.drops.set_block.invalid_material");
        addString(c, "drops.perform_action.error", "event.drops.perform_action.error");
        addString(c, "drops.well.lucky", "event.drops.lucky_well.lucky");
        addString(c, "drops.well.unlucky", "event.drops.lucky_well.unlucky");
        addString(c, "lct.display_name", "event.lucky_crafting_table.display_name");
        addString(c, "lct.place.success", "event.lucky_crafting_table.place.success");
        addString(c, "lct.place.no_permission", "event.lucky_crafting_table.place.no_permission");
        addString(c, "lct.place.no_place", "event.lucky_crafting_table.place.no_space");
        addString(c, "lct.break.success", "event.lucky_crafting_table.break.success");
        addString(c, "lct.break.no_permission", "event.lucky_crafting_table.break.no_permission");
        addString(c, "lct.use.no_permission", "event.lucky_crafting_table.use.no_permission");
        addString(c, "lct.upgrade.success", "event.lucky_crafting_table.upgrade.success");
        addString(c, "lct.upgrade.no_permission", "event.lucky_crafting_table.upgrade.no_permission");
        addString(c, "lct.upgrade.max_level", "event.lucky_crafting_table.upgrade.max_level");
        addString(c, "lct.charge.no_permission", "event.lucky_crafting_table.charge.no_permission");
        addString(c, "lct.view.no_permission", "event.lucky_crafting_table.viewers.no_permission");
        addString(c, "lct.error1", "event.lucky_crafting_table.error1");
        addString(c, "lct.error2", "event.lucky_crafting_table.error2");
        addString(c, "lct.error3", "event.lucky_crafting_table.error3");
        addString(c, "lct.error4", "event.lucky_crafting_table.error4");
        addString(c, "lct.error5", "event.lucky_crafting_table.error5");
        addString(c, "lct.error6", "event.lucky_crafting_table.error6");
        addString(c, "lct.error7", "event.lucky_crafting_table.error7");
        addString(c, "lct.data.viewers", "event.lucky_crafting_table.gui.main.viewers");
        addString(c, "lct.data.none", "event.lucky_crafting_table.none");
        addString(c, "lct.no_fuel", "event.lucky_crafting_table.no_fuel");
        addString(c, "lct.running", "event.lucky_crafting_table.running");
        addString(c, "lct.data.settings", "event.lucky_crafting_table.settings");
        addString(c, "lct.level_up", "event.lucky_crafting_table.level_up");
        addString(c, "lct.data.stop.name", "event.lucky_crafting_table.stop.name");
        addString(c, "lct.data.stop.lore", "event.lucky_crafting_table.stop.lore");
        addString(c, "lct.data.exluck.name", "event.lucky_crafting_table.use_extra_luck.name");
        addString(c, "lct.data.exluck.lore", "event.lucky_crafting_table.use_extra_luck.lore");
        addString(c, "lct.data.main.stored_luck", "event.lucky_crafting_table.gui.main.stored_luck");
        addString(c, "lct.data.main.level", "event.lucky_crafting_table.gui.main.level");
        addString(c, "lct.data.main.max_luck", "event.lucky_crafting_table.gui.main.max_luck");
        addString(c, "lct.data.main.player", "event.lucky_crafting_table.gui.main.player");
        addString(c, "lct.data.main.fuel", "event.lucky_crafting_table.gui.main.fuel");
        addString(c, "lct.data.main.extra_luck", "event.lucky_crafting_table.gui.main.extra_luck");
        addString(c, "lct.data.main.luck", "event.lucky_crafting_table.gui.main.luck");
        addString(c, "lct.gui.itemclose.name", "event.lucky_crafting_table.gui.itemClose.name");
        addString(c, "lct.gui.itemclose.lore", "event.lucky_crafting_table.gui.itemClose.lore");
        addString(c, "lct.gui.itemlocked.name", "event.lucky_crafting_table.gui.itemLocked.name");
        addString(c, "lct.gui.iteminsert.name", "event.lucky_crafting_table.gui.itemInsert.name");
        addString(c, "lct.gui.iteminsert.lore", "event.lucky_crafting_table.gui.itemInsert.lore");
        addString(c, "lct.gui.itemextract.name", "event.lucky_crafting_table.gui.itemExtract.name");
        addString(c, "lct.gui.itemextract.lore", "event.lucky_crafting_table.gui.itemExtract.lore");
        addString(c, "lct.gui.itemtotal.name", "event.lucky_crafting_table.gui.itemTotal.name");
        addString(c, "lct.gui.itemtotal.lore", "event.lucky_crafting_table.gui.itemTotal.lore");
        addString(c, "lct.gui.itemother.name", "event.lucky_crafting_table.gui.itemOther.name");
        addString(c, "lct.gui.itemother.lore", "event.lucky_crafting_table.gui.itemOther.lore");
        addString(c, "lct.gui.itemresult.name", "event.lucky_crafting_table.gui.itemResult.name");
        addString(c, "lct.gui.itemresult.lore", "event.lucky_crafting_table.gui.itemResult.lore");
        addString(c, "event.opengui.success", "event.gui.open.success");
        addString(c, "event.opengui.no_permission", "event.gui.open.no_perm");
        addString(c, "event.item.thoraxe.1", "event.item.thor_axe.1");
        addString(c, "event.item.thoraxe.2", "event.item.thor_axe.2");
        addString(c, "event.portal.teleport", "event.portal.teleport");
        addString(c, "desc.cmd.book", "desc.command.book");
        addString(c, "desc.cmd.detector", "desc.command.detector");
        addString(c, "desc.cmd.droplist", "desc.command.droplist");
        addString(c, "desc.cmd.givelb", "desc.command.givelb");
        addString(c, "desc.cmd.help", "desc.command.help");
        addString(c, "desc.cmd.lang", "desc.command.lang");
        addString(c, "desc.cmd.lbitem", "desc.command.lbitem");
        addString(c, "desc.cmd.lbs", "desc.command.lbs");
        addString(c, "desc.cmd.clearlbs", "desc.command.clearlbs");
        addString(c, "desc.cmd.commanddesc", "desc.command.commanddesc");
        addString(c, "desc.cmd.placelb", "desc.command.placelb");
        addString(c, "desc.cmd.region", "desc.command.region");
        addString(c, "desc.cmd.resourcepack", "desc.command.resourcepack");
        addString(c, "desc.cmd.savestructure", "desc.command.savestructure");
        addString(c, "desc.cmd.setdrop", "desc.command.setdrop");
        addString(c, "desc.cmd.types", "desc.command.types");
        addString(c, "desc.cmd.saveitem", "desc.command.saveitem");
        addString(c, "desc.cmd.recdeleted", "desc.command.recdeleted");
        addString(c, "log.lb.loading", "log.luckyblocks.loading");
        addString(c, "log.lb.found", "log.luckyblocks.found");
        if (c.getConfigurationSection("desc.drop") != null) {
            Iterator var3 = c.getConfigurationSection("desc.drop").getKeys(false).iterator();

            while (var3.hasNext()) {
                String s = (String) var3.next();
                if (!LuckyBlockDrop.isValid(s) && !CustomDropManager.isValid(s)) {
                    throw new Error("Could not add string for " + s + " (invalid drop exception)");
                }

                addString(c, "desc.drop." + s, "desc.drop." + s);
            }
        }

        return !MISSING_STRINGS;
    }

    private static void addString(ConfigurationSection c, String key, String path) {
        if (storedStrings.containsKey(key)) {
            throw new Error("String: " + key + " is already loaded!");
        } else {
            if (c.getString(path) != null) {
                storedStrings.put(key, c.getString(path));
            } else {
                MISSING_STRINGS = true;
                missing.add(key);
            }

        }
    }

    private static void addSound(ConfigurationSection c, String key, String path) {
        if (c.getString(path) != null) {
            storedSounds.put(key, c.getString(path));
        } else {
            MISSING_SOUNDS = true;
        }

    }

    public static String getSound(String key) {
        return storedSounds.getOrDefault(key, null);
    }

    public static String getString(String key) {
        return getString(key, true);
    }

    public static String getString(String key, boolean log) {
        if (storedStrings.containsKey(key)) {
            String s = storedStrings.get(key);
            s = s.replace("%!", "'");
            return s;
        } else {
            if (log) {
                LuckyBlockPlugin.instance.getLogger().info(key + " is missing! check your language file (" + LuckyBlockPlugin.lang + ")");
            }

            return "null";
        }
    }

    public static boolean changeLanguage() {
        LanguageChangedEvent la = new LanguageChangedEvent(LuckyBlockPlugin.lang);
        Bukkit.getServer().getPluginManager().callEvent(la);
        LuckyBlockPlugin.instance.config.set("lang_file", LuckyBlockPlugin.lang);

        try {
            LuckyBlockPlugin.instance.config.save(LuckyBlockPlugin.instance.configf);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        MISSING_STRINGS = false;
        File file = new File(LuckyBlockPlugin.d() + "data/messages/" + LuckyBlockPlugin.lang + ".yml");
        if (file.exists()) {
            storedStrings.clear();
            return loadStrings();
        } else {
            return false;
        }
    }

    public static List<String> listM() {
        return missing;
    }

    static void loadLuckyBlockFiles() {
        a("Drops/default/chests/chest1.yml");
        a("Drops/default/chests/chest2.yml");
        a("Drops/default/chests/chest3.yml");
        a("Drops/default/chests/chest4.yml");
        a("Drops/default/chests/chest5.yml");
        a("Drops/default/entities/falling_blocks.yml");
        a("Drops/default/entities/one.yml");
        a("Drops/default/entities/two.yml");
        a("Drops/default/entities/three.yml");
        a("Drops/default/entities/four.yml");
        a("Drops/default/entities/five.yml");
        a("Drops/default/entities/six.yml");
        a("Drops/default/items/armor/enchanted.yml");
        a("Drops/default/items/armor/unenchanted.yml");
        a("Drops/default/items/added.yml");
        a("Drops/default/items/enchanted_books.yml");
        a("Drops/default/items/i.yml");
        a("Drops/default/items/item_changing.yml");
        a("Drops/default/items/special.yml");
        a("Drops/default/items/common.yml");
        a("Drops/default/items/rare.yml");
        a("Drops/default/items/epic.yml");
        a("Drops/default/items/legendary.yml");
        a("Drops/default/structures/beacon.schematic");
        a("Drops/default/structures/cobwebs.yml");
        a("Drops/default/structures/diamond_ore.schematic");
        a("Drops/default/structures/dragon.schematic");
        a("Drops/default/structures/emeralds.schematic");
        a("Drops/default/structures/haybale.schematic");
        a("Drops/default/structures/lava_trap.yml");
        a("Drops/default/structures/lucky_temple_lbs.yml");
        a("Drops/default/structures/lucky_temple.schematic");
        a("Drops/default/structures/m_luckyblock.yml");
        a("Drops/default/structures/m_luckyblock1.schematic");
        a("Drops/default/structures/p_diamond.schematic");
        a("Drops/default/structures/platform_sandstone.schematic");
        a("Drops/default/structures/platform.schematic");
        a("Drops/default/structures/s_goldblocks.schematic");
        a("Drops/default/structures/s_luckyblock.yml");
        a("Drops/default/structures/s_luckyblock1.schematic");
        a("Drops/default/structures/skulls.schematic");
        a("Drops/default/structures/temple1.schematic");
        a("Drops/default/structures/tnt_trap.schematic");
        a("Drops/default/lb_0.yml");
        a("Drops/default/lb_25.yml");
        a("Drops/default/lb_50.yml");
        a("Drops/default/lb_85.yml");
        a("Drops/default/lb_-50.yml");
        a("Drops/default/lb_-100.yml");
    }

    private static void a(String a) {
        LuckyBlockPlugin.instance.a(a);
    }
}
