//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.LuckyBlock.Tags;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.Tags.HTag.IDataType;
import com.LuckyBlock.Tags.HTag.TDataType;
import com.LuckyBlock.yottaevents.LuckyDB;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemStackGetter extends HTag {
    public ItemStackGetter() {
    }

    public static ItemStack getItemStack(FileConfiguration file, String loc) {
        return getItemStack(file.getConfigurationSection(loc));
    }

    public static ItemStack getItemStack(ConfigurationSection c) {
        ItemStack item = getItemStack(c, 0);
        if (item == null && c.getKeys(false).size() > 0) {
            String s = getRandomLoc(c);
            item = getItemStack(c.getConfigurationSection(s), 1);
        }

        return item;
    }

    public static ItemStack getItemStack(ConfigurationSection c, int A) {
        ItemStack item = null;
        if (c != null) {
            Iterator var4 = c.getKeys(false).iterator();

            while(var4.hasNext()) {
                String s = (String)var4.next();
                if (s.equalsIgnoreCase("BuiltIn")) {
                    String[] d = c.getString(s).split(",");
                    FileConfiguration a = getFileByLoc(d[0]);
                    item = a.getItemStack(d[1]);
                }

                if (s.equalsIgnoreCase("Loc")) {
                    return getItemStack(getSection(c.getString(s)));
                }

                if (s.equalsIgnoreCase("Type")) {
                    try {
                        item = new ItemStack(Material.getMaterial(c.getString(s).toUpperCase()));
                    } catch (Exception var21) {
                        LuckyBlock.instance.getLogger().info("Error: Invalid Material! [Section:" + c.getName() + "," + c.getString(s) + "]");
                    }
                } else if (s.equalsIgnoreCase("LBType")) {
                    LBType type = LBType.fromId(c.getInt(s));
                    item = type.toItemStack();
                }
            }
        }

        if (item != null) {
            ItemMeta itemM = item.getItemMeta();
            Iterator var16 = c.getKeys(false).iterator();

            while(var16.hasNext()) {
                String s = (String)var16.next();
                String[] d;
                if (s.equalsIgnoreCase("Amount")) {
                    d = c.getString(s).split("-");
                    item.setAmount(getRandomNumber(d));
                    if (LuckyDB.getUuidAndAmount(item) != null) {
                        LuckyDB.setAmount(item, item.getAmount());
                    }
                }

                int x;
                if (s.equalsIgnoreCase("Luck")) {
                    d = c.getString(s).split(":");
                    x = getRandomNumber(d);
                    if (LBType.isLB(item)) {
                        item = LBType.changeLuck(LBType.fromItem(item), item, x);
                    }
                }

                if (s.equalsIgnoreCase("Data")) {
                    d = c.getString(s).split("-");
                    item.setDurability((short)getRandomNumber(d));
                }

                if (s.equalsIgnoreCase("DisplayName")) {
                    itemM.setDisplayName(translateString(c.getString(s), new IDataType[]{new IDataType(TDataType.ITEM, item)}));
                    item.setItemMeta(itemM);
                }

                Iterator var8;
                Object[] o;
                Enchantment ench;
                String g;
                ConfigurationSection f;
                if (s.equalsIgnoreCase("Enchants") && c.getConfigurationSection(s) != null) {
                    if (c.getConfigurationSection(s).getString("Loc") != null) {
                        f = getSection(c.getString(s));
                        var8 = f.getKeys(false).iterator();

                        while(var8.hasNext()) {
                            g = (String)var8.next();
                            o = getEnchantment(f.getConfigurationSection(g));
                            ench = (Enchantment)o[0];
                            if (ench != null) {
                                itemM.addEnchant(ench, Integer.parseInt(o[1].toString()), true);
                            }
                        }
                    } else {
                        Iterator var20 = c.getConfigurationSection(s).getKeys(false).iterator();

                        while(var20.hasNext()) {
                            String next = (String)var20.next();
                            Object[] enchantment = getEnchantment(c.getConfigurationSection(s).getConfigurationSection(next));
                            Enchantment enchantment1 = (Enchantment)enchantment[0];
                            if (enchantment1 != null) {
                                itemM.addEnchant(enchantment1, Integer.parseInt(enchantment[1].toString()), true);
                            }
                        }
                    }

                    item.setItemMeta(itemM);
                }

                if (s.equalsIgnoreCase("Lore") && c.getStringList(s) != null && c.getStringList(s).size() > 0) {
                    List<String> lore = new ArrayList();

                    for(x = 0; x < c.getStringList(s).size(); ++x) {
                        lore.add(translateString((String)c.getStringList(s).get(x), new IDataType[]{new IDataType(TDataType.ITEM, item)}));
                    }

                    itemM.setLore(lore);
                    item.setItemMeta(itemM);
                }

                if (s.equalsIgnoreCase("ItemFlags") && c.getStringList(s) != null && c.getStringList(s).size() > 0) {
                    for(int i = 0; i < c.getStringList(s).size(); ++i) {
                        itemM.addItemFlags(new ItemFlag[]{ItemFlag.valueOf(((String)c.getStringList(s).get(i)).toUpperCase())});
                    }
                }

                if (item.getType() == Material.BOOK_AND_QUILL) {
                    BookMeta book = (BookMeta)itemM;
                    if (s.equalsIgnoreCase("Author")) {
                        book.setAuthor(ChatColor.translateAlternateColorCodes('&', c.getString(s)));
                    }

                    if (s.equalsIgnoreCase("Title")) {
                        book.setTitle(translateString(c.getString(s), new IDataType[]{new IDataType(TDataType.ITEM, item)}));
                    }

                    if (s.equalsIgnoreCase("Pages") && c.getStringList(s) != null && c.getStringList(s).size() > 0) {
                        for(x = 0; x < c.getStringList(s).size(); ++x) {
                            book.addPage(new String[]{translateString((String)c.getStringList(s).get(x), new IDataType[]{new IDataType(TDataType.ITEM, item)})});
                        }
                    }

                    item.setItemMeta(book);
                }

                if (s.equalsIgnoreCase("StoredEnchants") && c.getConfigurationSection(s) != null) {
                    EnchantmentStorageMeta m = (EnchantmentStorageMeta)itemM;
                    if (c.getConfigurationSection(s).getString("Loc") != null) {
                        f = getSection(c.getString(s));
                        Iterator var31 = f.getKeys(false).iterator();

                        while(var31.hasNext()) {
                            String next = (String)var31.next();
                            Object[] enchantment = getEnchantment(f.getConfigurationSection(next));
                            Enchantment enchantment1 = (Enchantment)enchantment[0];
                            if (enchantment1 != null) {
                                m.addStoredEnchant(enchantment1, Integer.parseInt(enchantment[1].toString()), true);
                            }
                        }
                    } else {
                        var8 = c.getConfigurationSection(s).getKeys(false).iterator();

                        while(var8.hasNext()) {
                            g = (String)var8.next();
                            o = getEnchantment(c.getConfigurationSection(s).getConfigurationSection(g));
                            ench = (Enchantment)o[0];
                            if (ench != null) {
                                m.addStoredEnchant(ench, Integer.parseInt(o[1].toString()), true);
                            }
                        }
                    }

                    item.setItemMeta(itemM);
                }

                if (s.equalsIgnoreCase("SkullOwner")) {
                    SkullMeta skull = (SkullMeta)itemM;
                    skull.setOwner(c.getString(s));
                    item.setItemMeta(skull);
                }

                PotionMeta pot;
                if (s.equalsIgnoreCase("BasePotionData") && c.getConfigurationSection(s) != null) {
                    pot = (PotionMeta)itemM;
                    PotionType p = null;
                    boolean colorR = false;
                    boolean colorG = false;
                    if (c.getConfigurationSection(s).getString("PotionType") != null) {
                        p = PotionType.valueOf(c.getConfigurationSection(s).getString("PotionType").toUpperCase());
                    }

                    colorR = c.getConfigurationSection(s).getBoolean("Extended");
                    colorG = c.getConfigurationSection(s).getBoolean("Upgraded");
                    if (p != null) {
                        pot.setBasePotionData(new PotionData(p, colorR, colorG));
                    }

                    item.setItemMeta(pot);
                }

                if (s.equalsIgnoreCase("CustomPotionEffects") && c.getConfigurationSection(s) != null) {
                    pot = (PotionMeta)itemM;
                    var8 = c.getConfigurationSection(s).getKeys(false).iterator();

                    while(var8.hasNext()) {
                        g = (String)var8.next();
                        ConfigurationSection configurationSection = c.getConfigurationSection(s).getConfigurationSection(g);
                        PotionEffect p = getPotionEffect(configurationSection);
                        if (p != null) {
                            pot.addCustomEffect(p, true);
                        }
                    }

                    item.setItemMeta(pot);
                }

                if (s.equalsIgnoreCase("LeatherArmor") && c.getConfigurationSection(s) != null && itemM instanceof LeatherArmorMeta) {
                    LeatherArmorMeta lea = (LeatherArmorMeta)itemM;
                    ConfigurationSection section = c.getConfigurationSection(s);
                    int colorr = section.getInt("Red");
                    int colorg = section.getInt("Green");
                    int colorB = section.getInt("Blue");
                    lea.setColor(Color.fromRGB(colorr, colorg, colorB));
                    item.setItemMeta(lea);
                }

                if (s.equalsIgnoreCase("Firework") && c.getConfigurationSection(s) != null && itemM instanceof FireworkMeta) {
                    FireworkMeta fw = (FireworkMeta)itemM;
                    fw = getFireworkMeta(c.getConfigurationSection(s), fw);
                    item.setItemMeta(fw);
                }

                if (s.equalsIgnoreCase("Unbreakable") && c.getBoolean(s)) {
                    itemM.setUnbreakable(true);
                    item.setItemMeta(itemM);
                }

                if (s.equalsIgnoreCase("SpawnEgg")) {
                    SpawnEggMeta sp = (SpawnEggMeta)itemM;
                    sp.setSpawnedType(EntityType.valueOf(c.getString(s).toUpperCase()));
                    item.setItemMeta(sp);
                }

                if (s.equalsIgnoreCase("SpawnedType") && item.getType() == Material.MOB_SPAWNER) {
                    BlockStateMeta b = (BlockStateMeta)itemM;
                    CreatureSpawner b1 = (CreatureSpawner)b.getBlockState();
                    b1.setSpawnedType(EntityType.valueOf(c.getString(s).toUpperCase()));
                    b.setBlockState(b1);
                    item.setItemMeta(b);
                }
            }
        }

        return item;
    }

    public static void saveToFile(ItemStack item, FileConfiguration file, String loc, int slot) {
        file.set(loc + ".Type", item.getType().name());
        file.set(loc + ".Amount", item.getAmount());
        file.set(loc + ".Data", item.getDurability());
        if (slot > -1) {
            file.set(loc + ".Slot", slot);
        }

        if (item.hasItemMeta()) {
            ItemMeta itemM = item.getItemMeta();
            if (itemM.hasDisplayName()) {
                file.set(loc + ".DisplayName", itemM.getDisplayName());
            }

            if (itemM.hasLore()) {
                file.set(loc + ".Lore", itemM.getLore());
            }

            Iterator var7;
            int x;
            if (itemM.hasEnchants()) {
                x = 1;

                for(var7 = itemM.getEnchants().keySet().iterator(); var7.hasNext(); ++x) {
                    Enchantment e = (Enchantment)var7.next();
                    file.set(loc + ".Enchants.Ench" + x + ".EnchantmentName", e.getName());
                    file.set(loc + ".Enchants.Ench" + x + ".Level", itemM.getEnchants().get(e));
                }
            }

            if (itemM.getItemFlags() != null && itemM.getItemFlags().size() > 0) {
                List<String> list = new ArrayList();
                var7 = itemM.getItemFlags().iterator();

                while(var7.hasNext()) {
                    ItemFlag flag = (ItemFlag)var7.next();
                    list.add(flag.name());
                }

                file.set(loc + ".ItemFlags", list);
            }

            if (item.getType() == Material.SKULL_ITEM) {
                SkullMeta skull = (SkullMeta)itemM;
                if (skull.hasOwner()) {
                    file.set(loc + ".SkullOwner", skull.getOwner());
                }
            }

            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta em = (EnchantmentStorageMeta)itemM;
                x = 1;

                for(Iterator var8 = em.getStoredEnchants().keySet().iterator(); var8.hasNext(); ++x) {
                    Enchantment e = (Enchantment)var8.next();
                    file.set(loc + ".StoredEnchants.Ench" + x + ".EnchantmentName", e.getName());
                    file.set(loc + ".StoredEnchants.Ench" + x + ".Level", em.getStoredEnchants().get(e));
                }
            }

            if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.BOOK_AND_QUILL) {
                BookMeta book = (BookMeta)itemM;
                if (book.hasAuthor()) {
                    file.set(loc + ".Author", book.getAuthor());
                }

                if (book.hasTitle()) {
                    file.set(loc + ".Title", book.getTitle());
                }

                if (book.hasPages()) {
                    file.set(loc + ".Pages", book.getPages());
                }
            }

            if (itemM instanceof PotionMeta) {
                PotionMeta p = (PotionMeta)itemM;
                if (p.hasCustomEffects()) {
                    for(x = 0; x < p.getCustomEffects().size(); ++x) {
                        PotionEffect e = (PotionEffect)p.getCustomEffects().get(x);
                        file.set(loc + ".PotionEffects.Effect" + x + ".PotionEffectType", e.getType().getName());
                        file.set(loc + ".PotionEffects.Effect" + x + ".Duration", e.getDuration());
                        file.set(loc + ".PotionEffects.Effect" + x + ".Amplifier", e.getAmplifier());
                    }
                }

                if (p.getBasePotionData() != null) {
                    file.set(loc + ".BasePotionData.PotionType", p.getBasePotionData().getType().name());
                    file.set(loc + ".BasePotionData.Extended", p.getBasePotionData().isExtended());
                    file.set(loc + ".BasePotionData.Upgraded", p.getBasePotionData().isUpgraded());
                }
            }

            if (itemM instanceof LeatherArmorMeta) {
                LeatherArmorMeta lea = (LeatherArmorMeta)itemM;
                file.set(loc + ".LeatherArmor.Red", lea.getColor().getRed());
                file.set(loc + ".LeatherArmor.Green", lea.getColor().getGreen());
                file.set(loc + ".LeatherArmor.Blue", lea.getColor().getBlue());
            }

            if (itemM.isUnbreakable()) {
                file.set(loc + ".Unbreakable", true);
            }
        }

    }

    public static void saveToFileF(ItemStack item, ConfigurationSection c, int slot) {
        c.set("Type", item.getType().name());
        c.set("Amount", item.getAmount());
        c.set("Data", item.getDurability());
        if (slot > -1) {
            c.set("Slot", slot);
        }

        if (item.hasItemMeta()) {
            ItemMeta itemM = item.getItemMeta();
            if (itemM.hasDisplayName()) {
                c.set("DisplayName", itemM.getDisplayName());
            }

            if (itemM.hasLore()) {
                c.set("Lore", itemM.getLore());
            }

            Iterator var6;
            int x;
            if (itemM.hasEnchants()) {
                x = 1;

                for(var6 = itemM.getEnchants().keySet().iterator(); var6.hasNext(); ++x) {
                    Enchantment e = (Enchantment)var6.next();
                    c.set("Enchants.Ench" + x + ".EnchantmentName", e.getName());
                    c.set("Enchants.Ench" + x + ".Level", itemM.getEnchants().get(e));
                }
            }

            if (itemM.getItemFlags() != null && itemM.getItemFlags().size() > 0) {
                List<String> list = new ArrayList();
                var6 = itemM.getItemFlags().iterator();

                while(var6.hasNext()) {
                    ItemFlag flag = (ItemFlag)var6.next();
                    list.add(flag.name());
                }

                c.set("ItemFlags", list);
            }

            if (item.getType() == Material.SKULL_ITEM) {
                SkullMeta skull = (SkullMeta)itemM;
                if (skull.hasOwner()) {
                    c.set("SkullOwner", skull.getOwner());
                }
            }

            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta em = (EnchantmentStorageMeta)itemM;
                x = 1;

                for(Iterator var7 = em.getStoredEnchants().keySet().iterator(); var7.hasNext(); ++x) {
                    Enchantment e = (Enchantment)var7.next();
                    c.set("StoredEnchants.Ench" + x + ".EnchantmentName", e.getName());
                    c.set("StoredEnchants.Ench" + x + ".Level", em.getStoredEnchants().get(e));
                }
            }

            if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.BOOK_AND_QUILL) {
                BookMeta book = (BookMeta)itemM;
                if (book.hasAuthor()) {
                    c.set("Author", book.getAuthor());
                }

                if (book.hasTitle()) {
                    c.set("Title", book.getTitle());
                }

                if (book.hasPages()) {
                    c.set("Pages", book.getPages());
                }
            }

            if (itemM instanceof PotionMeta) {
                PotionMeta p = (PotionMeta)itemM;
                if (p.hasCustomEffects()) {
                    for(x = 0; x < p.getCustomEffects().size(); ++x) {
                        PotionEffect e = (PotionEffect)p.getCustomEffects().get(x);
                        c.set("PotionEffects.Effect" + x + ".PotionEffectType", e.getType().getName());
                        c.set("PotionEffects.Effect" + x + ".Duration", e.getDuration());
                        c.set("PotionEffects.Effect" + x + ".Amplifier", e.getAmplifier());
                    }
                }

                if (p.getBasePotionData() != null) {
                    c.set("BasePotionData.PotionType", p.getBasePotionData().getType().name());
                    c.set("BasePotionData.Extended", p.getBasePotionData().isExtended());
                    c.set("BasePotionData.Upgraded", p.getBasePotionData().isUpgraded());
                }
            }

            if (itemM instanceof LeatherArmorMeta) {
                LeatherArmorMeta lea = (LeatherArmorMeta)itemM;
                c.set("LeatherArmor.Red", lea.getColor().getRed());
                c.set("LeatherArmor.Green", lea.getColor().getGreen());
                c.set("LeatherArmor.Blue", lea.getColor().getBlue());
            }

            if (itemM.isUnbreakable()) {
                c.set("Unbreakable", true);
            }
        }

    }

    public static void saveInventory(ItemStack[] items, FileConfiguration file, String loc, boolean slot) {
        int b = 1;
        ConfigurationSection c = file.getConfigurationSection(loc);
        if (c != null) {
            file.set(loc, (Object)null);
            Iterator var7 = c.getKeys(false).iterator();

            while(var7.hasNext()) {
                String s = (String)var7.next();
                if (s.startsWith("Item")) {
                    String[] d = s.split("Item");
                    if (d.length == 2) {
                        try {
                            int num = Integer.parseInt(d[1]);
                            if (num > b) {
                                b = num;
                            }
                        } catch (NumberFormatException var10) {
                            ;
                        }
                    }
                }
            }
        }

        for(int x = 0; x < items.length; ++x) {
            if (items[x] != null) {
                if (slot) {
                    saveToFile(items[x], file, loc + ".Item" + b, x);
                } else {
                    saveToFile(items[x], file, loc + ".Item" + b, -1);
                }

                ++b;
            }
        }

    }

    public static void saveInventoryF(ItemStack[] items, ConfigurationSection c) {
        int b = 1;
        if (c != null) {
            Iterator var4 = c.getKeys(false).iterator();

            while(var4.hasNext()) {
                String s = (String)var4.next();
                if (s.startsWith("Item")) {
                    String[] d = s.split("Item");
                    if (d.length == 2) {
                        try {
                            int num = Integer.parseInt(d[1]);
                            if (num > b) {
                                b = num;
                            }
                        } catch (NumberFormatException var7) {
                            ;
                        }
                    }
                }
            }
        }

        for(int x = 0; x < items.length; ++x) {
            if (items[x] != null) {
                ConfigurationSection cc = c.createSection("Item" + b);
                saveToFileF(items[x], cc, x);
                ++b;
            }
        }

    }

    public static Object[] getEnchantment(ConfigurationSection c) {
        Object[] obj = new Object[2];
        Enchantment e = null;
        int level = 1;
        if (c.getString("EnchantmentName") != null) {
            e = Enchantment.getByName(c.getString("EnchantmentName").toUpperCase());
        } else {
            e = Enchantment.getById(c.getInt("EnchantmentId"));
        }

        if (c.getString("Level") != null) {
            String[] d = c.getString("Level").split(":");
            level = getRandomNumber(d);
        }

        obj[0] = e;
        obj[1] = level;
        return obj;
    }

    static PotionEffect getPotionEffect(ConfigurationSection f) {
        if (f != null) {
            PotionEffectType p = null;
            int duration = 0;
            int amplifier = 0;
            if (f.getString("PotionEffectType") != null) {
                p = PotionEffectType.getByName(f.getString("PotionEffectType").toUpperCase());
            }

            String[] d;
            if (f.getString("Duration") != null) {
                d = f.getString("Duration").split("-");
                duration = getRandomNumber(d);
            }

            if (f.getString("Amplifier") != null) {
                d = f.getString("Amplifier").split("-");
                amplifier = getRandomNumber(d);
            }

            return new PotionEffect(p, duration, amplifier);
        } else {
            return null;
        }
    }

    static FireworkMeta getFireworkMeta(ConfigurationSection c, FireworkMeta m) {
        if (c.getInt("Power") > 0) {
            m.setPower(c.getInt("Power"));
        }

        Builder b;
        if (c.getConfigurationSection("Effects") != null) {
            for(Iterator var3 = c.getConfigurationSection("Effects").getKeys(false).iterator(); var3.hasNext(); m.addEffect(b.build())) {
                String s = (String)var3.next();
                ConfigurationSection f = c.getConfigurationSection("Effects." + s);
                b = FireworkEffect.builder();
                if (f.getBoolean("Flicker")) {
                    b.flicker(true);
                }

                if (f.getBoolean("Trail")) {
                    b.trail(true);
                }

                if (f.getString("Type") != null) {
                    b.with(Type.valueOf(f.getString("Type")));
                }

                if (f.getConfigurationSection("Colors") != null) {
                    b.withColor(getFireworkColors(f.getConfigurationSection("Colors")));
                }

                if (f.getConfigurationSection("Fades") != null) {
                    b.withFade(getFireworkColors(f.getConfigurationSection("Fades")));
                }
            }
        }

        return m;
    }

    static Color[] getFireworkColors(ConfigurationSection c) {
        Color[] colors = new Color[64];
        int x = 0;

        for(Iterator var4 = c.getKeys(false).iterator(); var4.hasNext(); ++x) {
            String s = (String)var4.next();
            ConfigurationSection f = c.getConfigurationSection(s);
            Color color = null;
            String name = null;
            int r = 255;
            int g = 255;
            int b = 255;
            if (f.getString("Name") != null) {
                name = f.getString("Name");
            }

            if (f.getInt("Red") > -1) {
                r = f.getInt("Red");
            }

            if (f.getInt("Green") > -1) {
                g = f.getInt("Green");
            }

            if (f.getInt("Blue") > -1) {
                b = f.getInt("Blue");
            }

            if (name != null) {
                color = getColorByName(name);
            } else {
                color = Color.fromRGB(r, g, b);
            }

            colors[x] = color;
        }

        Color[] fixedColors = new Color[x];
        int a = 0;

        for(int i = 0; i < colors.length; ++i) {
            if (colors[i] != null) {
                fixedColors[a] = colors[i];
                ++a;
            }
        }

        return fixedColors;
    }

    static Color getColorByName(String name) {
        if (name.equalsIgnoreCase("aqua")) {
            return Color.AQUA;
        } else if (name.equalsIgnoreCase("black")) {
            return Color.BLACK;
        } else if (name.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        } else if (name.equalsIgnoreCase("fuchsia")) {
            return Color.FUCHSIA;
        } else if (name.equalsIgnoreCase("gray")) {
            return Color.GRAY;
        } else if (name.equalsIgnoreCase("green")) {
            return Color.GREEN;
        } else if (name.equalsIgnoreCase("lime")) {
            return Color.LIME;
        } else if (name.equalsIgnoreCase("maroon")) {
            return Color.MAROON;
        } else if (name.equalsIgnoreCase("navy")) {
            return Color.NAVY;
        } else if (name.equalsIgnoreCase("olive")) {
            return Color.OLIVE;
        } else if (name.equalsIgnoreCase("orange")) {
            return Color.ORANGE;
        } else if (name.equalsIgnoreCase("purple")) {
            return Color.PURPLE;
        } else if (name.equalsIgnoreCase("red")) {
            return Color.RED;
        } else if (name.equalsIgnoreCase("silver")) {
            return Color.SILVER;
        } else if (name.equalsIgnoreCase("teal")) {
            return Color.TEAL;
        } else if (name.equalsIgnoreCase("white")) {
            return Color.WHITE;
        } else {
            return name.equalsIgnoreCase("yellow") ? Color.YELLOW : null;
        }
    }
}
