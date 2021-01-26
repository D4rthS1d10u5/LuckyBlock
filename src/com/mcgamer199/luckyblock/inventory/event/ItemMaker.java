package com.mcgamer199.luckyblock.inventory.event;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.mcgamer199.luckyblock.nbt.ItemNBT;
import com.mcgamer199.luckyblock.nbt.ItemReflection;

import java.util.ArrayList;
import java.util.List;

public class ItemMaker {
    public ItemMaker() {
    }

    public static ItemMaker.IBannerMeta getNewBannerMeta() {
        return new ItemMaker.IBannerMeta();
    }

    public static ItemMaker.IBannerMeta getNewBannerMeta(DyeColor baseColor, Pattern... patterns) {
        ItemMaker.IBannerMeta bannerMeta = new ItemMaker.IBannerMeta();
        bannerMeta.baseColor = baseColor;
        bannerMeta.patterns = patterns;
        return bannerMeta;
    }

    public static ItemStack createItem(Material material) {
        ItemStack item = new ItemStack(material);
        return item;
    }

    public static ItemStack createItem(Material material, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, short data) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability(data);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, int data) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability((short)data);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, short data, String displayName) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability(data);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, int data, String displayName) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability((short)data);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, short data, String displayName, List<String> list) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability(data);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        itemM.setLore(list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, int data, String displayName, List<String> list) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        item.setDurability((short)data);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        itemM.setLore(list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack setLore(ItemStack item, String[] strings) {
        ItemMeta itemM = item.getItemMeta();
        List<String> list = new ArrayList();

        for(int x = 0; x < strings.length; ++x) {
            if (strings[x] != null) {
                list.add(strings[x]);
            }
        }

        itemM.setLore(list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack setLore(ItemStack item, List<String> list) {
        ItemMeta itemM = item.getItemMeta();
        itemM.setLore(list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addLore(ItemStack item, String lore) {
        ItemMeta itemM = item.getItemMeta();
        Object list;
        if (itemM != null && itemM.hasLore()) {
            list = itemM.getLore();
        } else {
            list = new ArrayList();
        }

        ((List)list).add(lore);
        itemM.setLore((List)list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack setDisplayName(ItemStack item, String displayName) {
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addStoredEnchantment(ItemStack item, Enchantment enchantment, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSkullOwner(ItemStack item, String owner) {
        SkullMeta skull = (SkullMeta)item.getItemMeta();
        skull.setOwner(owner);
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack createSkull(ItemStack item, String id, String value) {
        ItemNBT n = new ItemNBT(item);
        Object base = n.getNewNBT();
        ItemNBT.NBTList l = new ItemNBT.NBTList();
        l.add(n.setTag("Value", n.getNewNBT(), n.getNewNBTTagString(value)));
        Object nbt = n.setTag("textures", n.getNewNBT(), l.getNBT());
        base = n.setTag("Id", n.getNewNBT(), n.getNewNBTTagString(id));
        base = n.setTag("Properties", base, nbt);
        n.set("SkullOwner", base);
        return n.getItem();
    }

    public static ItemStack makeUnbreakable(ItemStack item) {
        return ItemReflection.setBoolean(item, "Unbreakable", true);
    }

    public static ItemStack addMinecraftTag(ItemStack item, ItemMaker.MinecraftTag tag, Object... values) {
        ItemNBT nbt = new ItemNBT(item);
        ItemNBT.NBTList list;
        Object o;
        int var6;
        int var7;
        Object[] var8;
        if (tag == ItemMaker.MinecraftTag.CAN_PLACE_ON) {
            list = new ItemNBT.NBTList();
            var8 = values;
            var7 = values.length;

            for(var6 = 0; var6 < var7; ++var6) {
                o = var8[var6];
                list.add(nbt.getNewNBTTagString(o.toString()));
            }

            nbt.set("CanPlaceOn", list.getNBT());
        }

        if (tag == ItemMaker.MinecraftTag.CAN_DESTROY) {
            list = new ItemNBT.NBTList();
            var8 = values;
            var7 = values.length;

            for(var6 = 0; var6 < var7; ++var6) {
                o = var8[var6];
                list.add(nbt.getNewNBTTagString(o.toString()));
            }

            nbt.set("CanDestroy", list.getNBT());
        }

        return nbt.getItem();
    }

    public static ItemStack addAttribute(ItemStack item, String attributeName, int operation, String name, float amount, ItemMaker.AttributeSlot slot) {
        ItemNBT n = new ItemNBT(item);
        Object base = n.getNewNBT();
        ItemNBT.NBTList l = new ItemNBT.NBTList();
        base = n.setTag("Operation", n.getNewNBT(), n.getNewNBTTagInt(operation));
        base = n.setTag("AttributeName", base, n.getNewNBTTagString(attributeName));
        base = n.setTag("Name", base, n.getNewNBTTagString(name));
        base = n.setTag("Amount", base, n.getNewNBTTagFloat(amount));
        base = n.setTag("UUIDLeast", base, n.getNewNBTTagInt(1));
        base = n.setTag("UUIDMost", base, n.getNewNBTTagInt(1));
        base = n.setTag("Slot", base, n.getNewNBTTagString(slot.value));
        l.add(base);
        n.set("AttributeModifiers", l.getNBT());
        return n.getItem();
    }

    public static ItemStack addEnchant(ItemStack item, Enchantment ench, int level) {
        ItemMeta itemM = item.getItemMeta();
        itemM.addEnchant(ench, level, true);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addEnchants(ItemStack item, int[] levels, Enchantment... ench) {
        ItemMeta itemM = item.getItemMeta();

        for(int x = 0; x < ench.length; ++x) {
            itemM.addEnchant(ench[x], levels[x], true);
        }

        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addStoredEnchants(ItemStack item, int[] levels, Enchantment... ench) {
        EnchantmentStorageMeta e = (EnchantmentStorageMeta)item.getItemMeta();

        for(int x = 0; x < ench.length; ++x) {
            e.addStoredEnchant(ench[x], levels[x], true);
        }

        item.setItemMeta(e);
        return item;
    }

    public static ItemStack setLeatherArmorColor(ItemStack item, Color color) {
        if (item.getType() != Material.LEATHER_HELMET && item.getType() != Material.LEATHER_CHESTPLATE && item.getType() != Material.LEATHER_LEGGINGS && item.getType() != Material.LEATHER_BOOTS) {
            throw new Error("Item isn't a leather armor!");
        } else {
            LeatherArmorMeta leather = (LeatherArmorMeta)item.getItemMeta();
            leather.setColor(color);
            item.setItemMeta(leather);
            return item;
        }
    }

    public static ItemStack setLeatherArmorColor(ItemStack item, int r, int g, int b) {
        if (item.getType() != Material.LEATHER_HELMET && item.getType() != Material.LEATHER_CHESTPLATE && item.getType() != Material.LEATHER_LEGGINGS && item.getType() != Material.LEATHER_BOOTS) {
            throw new Error("Item isn't a leather armor!");
        } else {
            LeatherArmorMeta leather = (LeatherArmorMeta)item.getItemMeta();
            leather.setColor(Color.fromRGB(r, g, b));
            item.setItemMeta(leather);
            return item;
        }
    }

    public static ItemStack setBannerMeta(ItemStack item, ItemMaker.IBannerMeta bannerMeta) {
        if (item.getType() == Material.BANNER) {
            BannerMeta b = (BannerMeta)item.getItemMeta();
            if (bannerMeta.baseColor != null) {
                b.setBaseColor(bannerMeta.baseColor);
            }

            if (bannerMeta.patterns != null) {
                b.setPatterns(bannerMeta.patternsToList());
            }

            item.setItemMeta(b);
            return item;
        } else {
            throw new Error("Item isn't a banner!");
        }
    }

    public static ItemStack setPotionMeta(ItemStack item, PotionData basePotionData, PotionEffectType mainEffect, PotionEffect... effects) {
        PotionMeta potion = (PotionMeta)item.getItemMeta();
        if (basePotionData != null) {
            potion.setBasePotionData(basePotionData);
        }

        if (mainEffect != null) {
            potion.setMainEffect(mainEffect);
        }

        PotionEffect[] var8 = effects;
        int var7 = effects.length;

        for(int var6 = 0; var6 < var7; ++var6) {
            PotionEffect effect = var8[var6];
            if (effect != null) {
                potion.addCustomEffect(effect, true);
            }
        }

        return item;
    }

    public static enum AttributeSlot {
        MAIN_HAND("mainhand"),
        OFF_HAND("offhand"),
        HELMET("head"),
        CHEST("chest"),
        LEGS("legs"),
        FEET("feet");

        private String value;

        private AttributeSlot(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class IBannerMeta {
        private Pattern[] patterns;
        private DyeColor baseColor;

        private IBannerMeta() {
            this.patterns = new Pattern[128];
        }

        public void addPatern(Pattern pattern) {
            this.addToPatterns(pattern);
        }

        public void addPatern(DyeColor color, PatternType patternType) {
            Pattern p = new Pattern(color, patternType);
            this.addToPatterns(p);
        }

        private void addToPatterns(Pattern pattern) {
            for(int x = 0; x < this.patterns.length; ++x) {
                if (this.patterns[x] == null) {
                    this.patterns[x] = pattern;
                    break;
                }
            }

        }

        public void removePattern(int number) {
            if (this.patterns.length > number) {
                if (this.patterns[number] != null) {
                    this.patterns[number] = null;
                }

            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        public void removePattern(Pattern pattern) {
            for(int x = 0; x < this.patterns.length; ++x) {
                if (this.patterns[x] == pattern) {
                    this.patterns[x] = null;
                }
            }

        }

        public void setBaseColor(DyeColor color) {
            this.baseColor = color;
        }

        public DyeColor getBaseColor() {
            return this.baseColor;
        }

        public List<Pattern> patternsToList() {
            List<Pattern> p = new ArrayList();
            Pattern[] var5;
            int var4 = (var5 = this.patterns).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                Pattern pattern = var5[var3];
                if (pattern != null) {
                    p.add(pattern);
                }
            }

            return p;
        }
    }

    public static enum MinecraftTag {
        CAN_PLACE_ON,
        CAN_DESTROY;

        private MinecraftTag() {
        }
    }
}

