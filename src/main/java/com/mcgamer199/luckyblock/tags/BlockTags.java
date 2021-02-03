package com.mcgamer199.luckyblock.tags;

import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.IDirection;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.SoundUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BlockTags extends HTag {
    private static final List<Material> PREVENTED;
    static int inventory_size = 96;

    static {
        PREVENTED = Arrays.asList(Material.BEDROCK, Material.BARRIER, Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_REPEATING, Material.STRUCTURE_BLOCK, Material.END_GATEWAY, Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME, Material.ANVIL, Material.ENCHANTMENT_TABLE, Material.OBSIDIAN, Material.ENDER_CHEST);
    }

    public BlockTags() {
    }

    protected static void setBlock(ConfigurationSection c1, Location location, String locationType) {
        Block block = null;
        Location l = getLocation(c1.getConfigurationSection("Location"), location, locationType);
        if (l != null) {
            block = l.getBlock();
        }

        if (block != null) {
            if (PREVENTED.contains(block.getType())) {
                return;
            }

            Iterator var6 = c1.getKeys(false).iterator();

            String t;
            while (var6.hasNext()) {
                t = (String) var6.next();
                if (t.equalsIgnoreCase("Type")) {
                    block.setType(Material.getMaterial(c1.getString(t)));
                }

                if (t.equalsIgnoreCase("TypeId")) {
                    block.setTypeId(getRandomNumber(c1.getString(t).split("-")));
                }

                if (t.equalsIgnoreCase("Data")) {
                    String[] d = c1.getString(t).split("-");
                    block.setData((byte) getRandomNumber(d));
                }
            }

            var6 = c1.getKeys(false).iterator();

            while (var6.hasNext()) {
                t = (String) var6.next();
                CommandBlock commandBlock;
                if (t.equalsIgnoreCase("Command") && block.getType() == Material.COMMAND) {
                    commandBlock = (CommandBlock) block.getState();
                    commandBlock.setCommand(c1.getString(t));
                    commandBlock.update(true);
                }

                if (t.equalsIgnoreCase("Name") && block.getType() == Material.COMMAND) {
                    commandBlock = (CommandBlock) block.getState();
                    commandBlock.setName(c1.getString(t));
                    commandBlock.update(true);
                }

                String a;
                if (t.equalsIgnoreCase("Sign") && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                    Sign sign = (Sign) block.getState();
                    if (c1.getString("Sign.Text1") != null) {
                        sign.setLine(0, ChatColor.translateAlternateColorCodes('&', c1.getString("Sign.Text1")));
                    }

                    if (c1.getString("Sign.Text2") != null) {
                        sign.setLine(1, ChatColor.translateAlternateColorCodes('&', c1.getString("Sign.Text2")));
                    }

                    if (c1.getString("Sign.Text3") != null) {
                        sign.setLine(2, ChatColor.translateAlternateColorCodes('&', c1.getString("Sign.Text3")));
                    }

                    if (c1.getString("Sign.Text4") != null) {
                        sign.setLine(3, ChatColor.translateAlternateColorCodes('&', c1.getString("Sign.Text4")));
                    }

                    if (c1.getString("Sign.Facing") != null) {
                        a = c1.getString("Sign.Facing");
                        org.bukkit.material.Sign si = (org.bukkit.material.Sign) sign.getData();
                        if (a.equalsIgnoreCase("player")) {
                            a = IDirection.getByLoc(l, location).name();
                        }

                        si.setFacingDirection(BlockFace.valueOf(a.toUpperCase()));
                        sign.setData(si);
                    }

                    sign.update(true);
                }

                CreatureSpawner b;
                if (t.equalsIgnoreCase("CreatureType") && block.getType() == Material.MOB_SPAWNER) {
                    b = (CreatureSpawner) block.getState();
                    b.setCreatureTypeByName(c1.getString(t).toUpperCase());
                    b.update(true);
                }

                if (t.equalsIgnoreCase("Delay") && block.getType() == Material.MOB_SPAWNER) {
                    b = (CreatureSpawner) block.getState();
                    b.setDelay(c1.getInt(t));
                    b.update(true);
                }

                Iterator var18;
                if (t.equalsIgnoreCase("Inventory")) {
                    ItemStack[] items = new ItemStack[inventory_size];
                    if (c1.getConfigurationSection(t) != null) {
                        var18 = c1.getConfigurationSection(t).getKeys(false).iterator();

                        while (var18.hasNext()) {
                            a = (String) var18.next();
                            String[] slotT = c1.getConfigurationSection(t).getConfigurationSection(a).getString("Slot").split("-");
                            int slot = getRandomNumber(slotT);
                            if (slot < inventory_size) {
                                ItemStack item = ItemStackGetter.getItemStack(c1.getConfigurationSection(t).getConfigurationSection(a));
                                if (item != null) {
                                    items[slot] = item;
                                }
                            }
                        }
                    }

                    int i;
                    if (block.getType() == Material.CHEST) {
                        Chest chest = (Chest) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < chest.getInventory().getSize() && items[i] != null) {
                                chest.getInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (block.getType() == Material.HOPPER) {
                        Hopper hopper = (Hopper) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < hopper.getInventory().getSize() && items[i] != null) {
                                hopper.getInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (block.getType() == Material.DROPPER) {
                        Dropper dropper = (Dropper) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < dropper.getInventory().getSize() && items[i] != null) {
                                dropper.getInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (block.getType() == Material.DISPENSER) {
                        Dispenser dispenser = (Dispenser) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < dispenser.getInventory().getSize() && items[i] != null) {
                                dispenser.getInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (block.getType() == Material.BREWING_STAND) {
                        BrewingStand brewer = (BrewingStand) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < brewer.getInventory().getSize() && items[i] != null) {
                                brewer.getInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (block.getType() == Material.FURNACE) {
                        Furnace furn = (Furnace) block.getState();

                        for (i = 0; i < items.length; ++i) {
                            if (i < furn.getInventory().getSize() && items[i] != null) {
                                furn.getInventory().setItem(i, items[i]);
                            }
                        }
                    }

                    block.getState().update(true);
                }

                if (t.equalsIgnoreCase("Item") && block.getType() == Material.JUKEBOX) {
                    Jukebox juke = (Jukebox) block.getState();
                    if (c1.getString(t) != null) {
                        juke.setPlaying(Material.getMaterial(c1.getString(t).toUpperCase()));
                        juke.update(true);
                    }
                }

                Note.Tone tone;
                if (block.getType() == Material.NOTE_BLOCK) {
                    NoteBlock note = (NoteBlock) block.getState();
                    if (t.equalsIgnoreCase("Note") && c1.getConfigurationSection(t) != null) {
                        ConfigurationSection f = c1.getConfigurationSection(t);
                        String g = f.getString("Type");
                        tone = null;
                        if (f.getString("Tone") != null) {
                            tone = Note.Tone.valueOf(f.getString("Tone").toUpperCase());
                        }

                        int octave = f.getInt("Octave");
                        if (g.equalsIgnoreCase("FLAT")) {
                            note.setNote(Note.flat(octave, tone));
                        } else if (g.equalsIgnoreCase("NATURAL")) {
                            note.setNote(Note.natural(octave, tone));
                        } else if (g.equalsIgnoreCase("SHARP")) {
                            note.setNote(Note.sharp(octave, tone));
                        }

                        note.update(true);
                    }
                }

                if (block.getType() == Material.SKULL) {
                    Skull skull = (Skull) block.getState();
                    if (t.equalsIgnoreCase("SkullOwner")) {
                        skull.setOwner(c1.getString(t));
                    }

                    if (t.equalsIgnoreCase("SkullRotation")) {
                        skull.setRotation(BlockFace.valueOf(c1.getString(t).toUpperCase()));
                    }

                    if (t.equalsIgnoreCase("SkullType")) {
                        skull.setSkullType(SkullType.valueOf(c1.getString(t).toUpperCase()));
                    }

                    skull.update(true);
                }

                ConfigurationSection c2;
                if (block.getType() == Material.BANNER) {
                    Banner banner = (Banner) block.getState();
                    if (t.equalsIgnoreCase("BaseColor")) {
                        banner.setBaseColor(DyeColor.getByDyeData((byte) c1.getInt(t)));
                    }

                    if (t.equalsIgnoreCase("Patterns") && c1.getConfigurationSection(t) != null) {
                        var18 = c1.getConfigurationSection(t).getKeys(false).iterator();

                        while (var18.hasNext()) {
                            a = (String) var18.next();
                            c2 = c1.getConfigurationSection(t).getConfigurationSection(a);
                            if (c2 != null) {
                                tone = null;
                                PatternType type = null;
                                DyeColor color = DyeColor.getByDyeData((byte) c2.getInt("Color"));
                                if (c2.getString("PatternType") != null) {
                                    type = PatternType.valueOf(c2.getString("PatternType").toUpperCase());
                                }

                                if (color != null && type != null) {
                                    banner.addPattern(new Pattern(color, type));
                                }
                            }
                        }
                    }

                    banner.update(true);
                }

                int luck;
                if (t.equalsIgnoreCase("LB_ID")) {
                    luck = c1.getInt(t);
                    if (LBType.fromId(luck) != null) {
                        LBType type = LBType.fromId(luck);
                        if (!type.disabled) {
                            block.setType(LBType.fromId(luck).getType());
                            block.setData((byte) LBType.fromId(luck).getData());
                            LuckyBlock luckyBlock = new LuckyBlock(type, block, 0, null, true, true);
                            luckyBlock.playEffects();
                        }
                    }
                }

                if (t.equalsIgnoreCase("LB_LUCK")) {
                    luck = getRandomNumber(c1.getString(t).split(":"));
                    if (LuckyBlock.getFromBlock(block) != null) {
                        LuckyBlock.getFromBlock(block).setLuck(luck);
                        LuckyBlock.getFromBlock(block).save(true);
                    }
                }

                if (t.equalsIgnoreCase("LB_DROP")) {
                    String drop = c1.getString(t);
                    if (LuckyBlock.getFromBlock(block) != null) {
                        LuckyBlock luckyBlock = LuckyBlock.getFromBlock(block);
                        if (LBDrop.isValid(drop)) {
                            luckyBlock.setDrop(LBDrop.valueOf(drop.toUpperCase()), true, true);
                            luckyBlock.save(true);
                        } else if (CustomDropManager.getByName(drop) != null) {
                            luckyBlock.customDrop = CustomDropManager.getByName(drop);
                            luckyBlock.save(true);
                        }
                    }
                }

                if (t.equalsIgnoreCase("LB_DROP_OPTIONS") && c1.getConfigurationSection(t) != null && LuckyBlock.getFromBlock(block) != null) {
                    LuckyBlock luckyBlock = LuckyBlock.getFromBlock(block);
                    var18 = c1.getConfigurationSection(t).getKeys(false).iterator();

                    while (var18.hasNext()) {
                        a = (String) var18.next();
                        c2 = c1.getConfigurationSection(t).getConfigurationSection(a);
                        String name = c2.getString("Name");
                        List<String> list = c2.getStringList("Values");
                        if (name != null && list != null && list.size() > 0) {
                            DropOption dr = new DropOption(name, list.toArray());
                            luckyBlock.getDropOptions().add(dr);
                            luckyBlock.save(true);
                        }
                    }
                }

                if (t.equalsIgnoreCase("Chest") && block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();
                    ChestFiller c = new ChestFiller(c1.getConfigurationSection(t), chest);
                    c.fill();
                }
            }
        }

    }

    protected static String getLocationType(ConfigurationSection c) {
        String locationType = "block";
        if (c.getString("LocationType") != null && c.getString("LocationType").equalsIgnoreCase("player")) {
            locationType = "player";
        }

        return locationType;
    }

    protected static int getTicks(ConfigurationSection c) {
        int ticks = 0;
        if (c.getInt("Delay") > -1) {
            ticks = c.getInt("Delay");
        }

        return ticks;
    }

    protected static void playSound(ConfigurationSection c, Location location) {
        if (c.getString("Sound") != null) {
            SoundUtils.playFixedSound(location, Sound.valueOf(c.getString("Sound").toUpperCase()), 1.0F, 1.0F, 15);
        }

    }

    protected static void buildSchem(ConfigurationSection c, Location location) {
        if (c.getConfigurationSection("Schematics") != null) {
            Iterator var3 = c.getConfigurationSection("Schematics").getKeys(false).iterator();

            while (var3.hasNext()) {
                String s = (String) var3.next();
                ConfigurationSection f = c.getConfigurationSection("Schematics").getConfigurationSection(s);
                if (f != null) {
                    String[] d = f.getString("Loc").split(",");
                    int x = Integer.parseInt(d[0]);
                    int y = Integer.parseInt(d[1]);
                    int z = Integer.parseInt(d[2]);
                    Location l = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
                    File file = new File(LuckyBlockPlugin.d() + "Drops/" + f.getString("File") + ".schematic");
                    int ticks = getTicks(f);
                    if (ticks > 0) {
                        perform_schem(l, file, ticks);
                    } else {
                        Schematic.loadArea(file, l);
                    }
                }
            }
        }

    }

    protected static Location newLoc(ConfigurationSection c, Location location) {
        if (c.getString("Loc") != null) {
            String[] d = c.getString("Loc").split(",");
            int x = Integer.parseInt(d[0]);
            int y = Integer.parseInt(d[1]);
            int z = Integer.parseInt(d[2]);
            location = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
        }

        return location;
    }

    protected static void buildPieces(ConfigurationSection c, Location location) {
        if (c.getConfigurationSection("Pieces") != null) {
            ConfigurationSection f = c.getConfigurationSection("Pieces");
            Iterator var4 = f.getKeys(false).iterator();

            while (var4.hasNext()) {
                String s = (String) var4.next();
                ConfigurationSection f1 = f.getConfigurationSection(s);
                if (f1 != null) {
                    String fil = f1.getString("File");
                    String loc = f1.getString("Path");
                    File fc = new File(LuckyBlockPlugin.d() + "Drops/" + fil);
                    if (fc.exists()) {
                        FileConfiguration fc1 = YamlConfiguration.loadConfiguration(fc);
                        if (fc1.getConfigurationSection(loc) != null) {
                            ConfigurationSection g = fc1.getConfigurationSection(loc);
                            int ticks = getTicks(g);
                            if (ticks > 0) {
                                perform_piece(g, location, ticks);
                            } else {
                                setBlocks(g, location);
                            }
                        }
                    }
                }
            }
        }

    }

    protected static void setBlocks(ConfigurationSection c, Location location) {
        String locationType = getLocationType(c);
        buildSchem(c, location);
        location = newLoc(c, location);
        playSound(c, location);
        filler(c, location);
        buildPieces(c, location);
        String s;
        Iterator var4;
        ConfigurationSection s1;
        if (c.getConfigurationSection("Blocks") != null) {
            var4 = c.getConfigurationSection("Blocks").getKeys(false).iterator();

            while (var4.hasNext()) {
                s = (String) var4.next();
                s1 = c.getConfigurationSection("Blocks").getConfigurationSection(s);
                if (s1 != null) {
                    int ticks = getTicks(s1);
                    if (ticks > 0) {
                        perform_setblock(s1, location, locationType, ticks);
                    } else {
                        setBlock(s1, location, locationType);
                    }
                }
            }
        }

        if (c.getConfigurationSection("Entities") != null) {
            var4 = c.getConfigurationSection("Entities").getKeys(false).iterator();

            while (var4.hasNext()) {
                s = (String) var4.next();
                s1 = c.getConfigurationSection("Entities").getConfigurationSection(s);
                Location l = getLocation(s1.getConfigurationSection("Location"), location, locationType);
                if (l != null) {
                    int ticks = getTicks(s1);
                    if (ticks > 0) {
                        perform_spawnentity(s1, c, location, ticks);
                    } else {
                        EntityTags.spawnEntity(s1, l, c.getConfigurationSection("Entities"), true, null);
                    }
                }
            }
        }

    }

    public static void fillRandomChest(FileConfiguration file, String loc, Chest chest) {
        String s = getRandomL(file, loc);
        ItemStack[] items = getRandomChestItems(file, loc + "." + s);

        for (int i = 0; i < items.length; ++i) {
            if (i < chest.getInventory().getSize() && items[i] != null) {
                chest.getInventory().setItem(i, items[i]);
            }
        }

    }

    public static void spawnRandomFallingBlock(FileConfiguration file, String loc, Location location) {
        String s = getRandomL(file, loc);
        spawnFallingBlock(file, loc, s, location);
    }

    public static void spawnFallingBlock(FileConfiguration file, String loc, String loc1, Location location) {
        Material mat = null;
        byte data = 0;
        boolean dropItem = true;
        boolean glowing = false;
        boolean hurtEntities = false;
        if (file.getConfigurationSection(loc) != null) {
            ConfigurationSection c = file.getConfigurationSection(loc).getConfigurationSection(loc1);
            if (c != null) {
                if (c.getString("Type") != null) {
                    mat = Material.getMaterial(c.getString("Type"));
                }

                if (c.getString("Data") != null) {
                    String[] d = c.getString("Data").split("-");
                    data = (byte) getRandomNumber(d);
                }

                FallingBlock fb = location.getWorld().spawnFallingBlock(location, mat, data);
                fb.setDropItem(dropItem);
                fb.setGlowing(glowing);
                fb.setHurtEntities(hurtEntities);
            }
        }

    }

    protected static ItemStack[] getRandomChestItems(FileConfiguration file, String loc) {
        ConfigurationSection c = file.getConfigurationSection(loc);
        ItemStack[] items = new ItemStack[inventory_size];
        if (c != null) {
            String i = "Inventory";
            int times = 1;
            if (c.getString("Rolls") != null) {
                String[] d = c.getString("Rolls").split("-");
                times = getRandomNumber(d);
            }

            if (c.getConfigurationSection(i) != null) {
                for (int x = times; x > 0; --x) {
                    String s = getRandomLoc(file, loc + "." + i);
                    ConfigurationSection f = c.getConfigurationSection(i).getConfigurationSection(s);
                    int slot = getRandomNumber(new String[]{"0", "26"});
                    if (f.getString("Slot") != null) {
                        String[] slotT = f.getString("Slot").split("-");
                        slot = getRandomNumber(slotT);
                    }

                    f.getString("Disable");
                    if (slot < inventory_size) {
                        ItemStack item = ItemStackGetter.getItemStack(file, loc + "." + i + "." + s);
                        if (item != null) {
                            items[slot] = item;
                        }

                        if (f.getStringList("With") != null && f.getStringList("With").size() > 0) {
                            List<String> list = f.getStringList("With");

                            for (int z = 0; z < list.size(); ++z) {
                                String a = list.get(z);
                                ConfigurationSection h = c.getConfigurationSection(i).getConfigurationSection(a);
                                int slot1 = getRandomNumber(new String[]{"0", "26"});
                                if (h.getString("Slot") != null) {
                                    String[] slotT = h.getString("Slot").split("-");
                                    slot1 = getRandomNumber(slotT);
                                }

                                if (slot1 < inventory_size) {
                                    ItemStack item1 = ItemStackGetter.getItemStack(file, loc + "." + i + "." + a);
                                    if (item1 != null) {
                                        items[slot1] = item1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return items;
    }

    public static ItemStack[] getInventoryItems(ConfigurationSection c, String l) {
        ItemStack[] items = new ItemStack[inventory_size];
        if (c != null && c.getConfigurationSection(l) != null) {
            Iterator var4 = c.getConfigurationSection(l).getKeys(false).iterator();

            while (var4.hasNext()) {
                String g = (String) var4.next();
                String[] slotT = c.getConfigurationSection(l).getConfigurationSection(g).getString("Slot").split("-");
                int slot = getRandomNumber(slotT);
                if (slot < inventory_size) {
                    ItemStack item = ItemStackGetter.getItemStack(c.getConfigurationSection(l + "." + g));
                    if (item != null) {
                        items[slot] = item;
                    }
                }
            }
        }

        return items;
    }

    public static ItemStack[] getInventory(FileConfiguration file, String loc) {
        ConfigurationSection c = file.getConfigurationSection(loc);
        ItemStack[] items = new ItemStack[inventory_size];
        if (c != null) {
            String i = "Inventory";
            if (c.getConfigurationSection(i) != null) {
                Iterator var6 = c.getConfigurationSection(i).getKeys(false).iterator();

                while (var6.hasNext()) {
                    String g = (String) var6.next();
                    String[] slotT = c.getConfigurationSection(i).getConfigurationSection(g).getString("Slot").split("-");
                    int slot = getRandomNumber(slotT);
                    if (slot < inventory_size) {
                        ItemStack item = ItemStackGetter.getItemStack(file, loc + "." + i + "." + g);
                        if (item != null) {
                            items[slot] = item;
                        }
                    }
                }
            }
        }

        return items;
    }

    public static ItemStack[] getCInventory(FileConfiguration file, String loc) {
        ConfigurationSection c = file.getConfigurationSection(loc);
        ItemStack[] items = new ItemStack[inventory_size];
        if (c != null) {
            Iterator var5 = c.getKeys(false).iterator();

            while (var5.hasNext()) {
                String g = (String) var5.next();
                String[] slotT = c.getConfigurationSection(g).getString("Slot").split("-");
                int slot = getRandomNumber(slotT);
                if (slot < inventory_size) {
                    ItemStack item = ItemStackGetter.getItemStack(file, loc + "." + g);
                    if (item != null) {
                        items[slot] = item;
                    }
                }
            }
        }

        return items;
    }

    public static String getValue(FileConfiguration file, String loc, String loc1, String loc2) {
        return file.getString(loc + "." + loc1 + "." + loc2);
    }

    public static void buildStructure(ConfigurationSection c, Location location) {
        if (c != null) {
            setBlocks(c, location);
        }

    }

    public static String getRandomL(FileConfiguration file, String loc) {
        return getRandomLoc(file, loc);
    }

    public static String getRandomL(ConfigurationSection c) {
        return getRandomLoc(c);
    }

    protected static Location getLocation(ConfigurationSection c, Location location, String locationType) {
        String world = null;
        Location l = null;
        if (c != null) {
            world = c.getString("World");
            String a1 = c.getString("X");
            String a2 = c.getString("Y");
            String a3 = c.getString("Z");
            int x;
            int y;
            int z;
            if (!locationType.equalsIgnoreCase("block") && !locationType.equalsIgnoreCase("player")) {
                x = Integer.parseInt(a1);
                y = Integer.parseInt(a2);
                z = Integer.parseInt(a3);
            } else {
                x = location.getBlockX() + Integer.parseInt(a1);
                y = location.getBlockY() + Integer.parseInt(a2);
                z = location.getBlockZ() + Integer.parseInt(a3);
            }

            if (world != null) {
                l = Bukkit.getWorld(world).getBlockAt(x, y, z).getLocation();
            } else {
                Location locat = new Location(location.getWorld(), x, y, z);
                l = locat;
            }
        }

        return l;
    }

    private static void filler(ConfigurationSection c, Location location) {
        if (c.getConfigurationSection("Fillers") != null) {
            ConfigurationSection f = c.getConfigurationSection("Fillers");
            Iterator var4 = f.getKeys(false).iterator();

            while (true) {
                while (true) {
                    ConfigurationSection f1;
                    Material blockMat;
                    int xM;
                    int yM;
                    int zM;
                    int xL;
                    int yL;
                    int zL;
                    int ticks;
                    int i;
                    byte blockData;
                    do {
                        do {
                            if (!var4.hasNext()) {
                                return;
                            }

                            String s = (String) var4.next();
                            f1 = f.getConfigurationSection(s);
                        } while (f1 == null);

                        int sizeX = 0;
                        int sizeY = 0;
                        int sizeZ = 0;
                        blockMat = null;
                        if (f1.getString("Material") != null) {
                            blockMat = Material.getMaterial(f1.getString("Material"));
                        }

                        blockData = (byte) f1.getInt("Data");
                        if (f1.getConfigurationSection("Size") != null) {
                            sizeX = f1.getInt("Size.X");
                            sizeY = f1.getInt("Size.Y");
                            sizeZ = f1.getInt("Size.Z");
                        }

                        xM = sizeX * -1;
                        yM = sizeY * -1;
                        zM = sizeZ * -1;
                        xL = sizeX + 1;
                        yL = sizeY + 1;
                        zL = sizeZ + 1;
                        if (f1.getStringList("Values") != null) {
                            for (ticks = 0; ticks < f1.getStringList("Values").size(); ++ticks) {
                                String[] d = f1.getStringList("Values").get(ticks).split("=");
                                i = Integer.parseInt(d[1]);
                                if (d.length == 2) {
                                    if (d[0].equalsIgnoreCase("xM")) {
                                        xM = i;
                                    } else if (d[0].equalsIgnoreCase("yM")) {
                                        yM = i;
                                    } else if (d[0].equalsIgnoreCase("zM")) {
                                        zM = i;
                                    } else if (d[0].equalsIgnoreCase("xL")) {
                                        xL = i;
                                    } else if (d[0].equalsIgnoreCase("yL")) {
                                        yL = i;
                                    } else if (d[0].equalsIgnoreCase("zL")) {
                                        zL = i;
                                    }
                                }
                            }
                        }
                    } while (blockMat == null);

                    ticks = getTicks(f1);
                    if (ticks > 0) {
                        perform_filler(location, blockMat, blockData, xM, yM, zM, xL, yL, zL, ticks);
                    } else {
                        for (int x = xM; x < xL; ++x) {
                            for (i = yM; i < yL; ++i) {
                                for (int z = zM; z < zL; ++z) {
                                    Location newL = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) i, location.getZ() + (double) z);
                                    Block b = newL.getBlock();
                                    b.setType(blockMat);
                                    b.setData(blockData);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void perform_schem(final Location location, final File file, int ticks) {
        Scheduler.later(() -> Schematic.loadArea(file, location), ticks);
    }

    private static void perform_piece(final ConfigurationSection c, final Location location, int ticks) {
        Scheduler.later(() -> BlockTags.setBlocks(c, location), ticks);
    }

    private static void perform_setblock(final ConfigurationSection c, final Location location, final String locationType, int ticks) {
        Scheduler.later(() -> BlockTags.setBlock(c, location, locationType), ticks);
    }

    private static void perform_spawnentity(final ConfigurationSection c1, final ConfigurationSection c2, final Location location, int ticks) {
        Scheduler.later(() -> EntityTags.spawnEntity(c1, location, c2.getConfigurationSection("Entities"), true, null), ticks);
    }

    private static void perform_filler(final Location location, final Material blockMat, final byte blockData, final int xM, final int yM, final int zM, final int xL, final int yL, final int zL, int ticks) {
        Scheduler.later(() -> {
            for (int x = xM; x < xL; ++x) {
                for (int y = yM; y < yL; ++y) {
                    for (int z = zM; z < zL; ++z) {
                        Location newL = new Location(location.getWorld(), location.getX() + (double) x, location.getY() + (double) y, location.getZ() + (double) z);
                        Block b = newL.getBlock();
                        b.setType(blockMat);
                        b.setData(blockData);
                    }
                }
            }
        }, ticks);
    }
}
