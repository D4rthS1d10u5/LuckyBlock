package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.MinecraftTag;
import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.api.nbt.NBTListWrapper;
import com.mcgamer199.luckyblock.api.nbt.v1_12_R1.v1_12_R1_NBTCompoundWrapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemStackUtils {

    private static final List<Material> repairable = Arrays.asList(
            Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_SPADE,
            Material.DIAMOND_PICKAXE, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.GOLD_SWORD,
            Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_PICKAXE,
            Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS, Material.IRON_SWORD, Material.IRON_AXE,
            Material.IRON_SPADE, Material.IRON_PICKAXE, Material.DIAMOND_HOE,
            Material.GOLD_HOE, Material.IRON_HOE, Material.STONE_HOE,
            Material.WOOD_HOE, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.STONE_SWORD,
            Material.STONE_AXE, Material.STONE_SPADE, Material.STONE_PICKAXE,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS, Material.WOOD_SWORD, Material.WOOD_AXE,
            Material.WOOD_SPADE, Material.WOOD_PICKAXE, Material.FISHING_ROD,
            Material.ELYTRA, Material.BOW, Material.FLINT_AND_STEEL, Material.CARROT_STICK);

    public static boolean isRepairable(Material type) {
        return repairable.contains(type);
    }

    public static NBTCompoundWrapper<?> getItemTag(ItemStack item) {
        NBTTagCompound itemTag = CraftItemStack.asNMSCopy(item).getTag();
        return new v1_12_R1_NBTCompoundWrapper(itemTag != null ? itemTag : new NBTTagCompound());
    }

    public static ItemStack setItemTag(ItemStack item, NBTCompoundWrapper<?> tagWrapper) {
        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        nmsCopy.setTag((NBTTagCompound) tagWrapper.getTag());
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    public static ItemStackUtils.IBannerMeta getNewBannerMeta() {
        return new ItemStackUtils.IBannerMeta();
    }

    public static ItemStackUtils.IBannerMeta getNewBannerMeta(DyeColor baseColor, Pattern... patterns) {
        ItemStackUtils.IBannerMeta bannerMeta = new ItemStackUtils.IBannerMeta();
        bannerMeta.baseColor = baseColor;
        bannerMeta.patterns = patterns;
        return bannerMeta;
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
        item.setDurability((short) data);
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
        item.setDurability((short) data);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(displayName);
        itemM.setLore(list);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack createItem(Material type, int amount, short data, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(type);
        if (amount > 0) {
            item.setAmount(amount);
        }

        if (data > 0) {
            item.setDurability(data);
        }

        ItemMeta itemM = item.getItemMeta();
        if (name != null) {
            itemM.setDisplayName(name);
        }

        if (lore != null && lore.size() > 0) {
            itemM.setLore(lore);
        }

        if (enchants != null && enchants.size() > 0) {

            for (Enchantment e : enchants.keySet()) {
                itemM.addEnchant(e, enchants.get(e), true);
            }
        }

        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack setLore(ItemStack item, String[] strings) {
        ItemMeta itemM = item.getItemMeta();
        List<String> list = new ArrayList();

        for (int x = 0; x < strings.length; ++x) {
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

        ((List) list).add(lore);
        itemM.setLore((List) list);
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
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSkullOwner(ItemStack item, String owner) {
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwner(owner);
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack createSkull(ItemStack item, String id, String value) {
        NBTCompoundWrapper<?> compound = getItemTag(item);
        NBTCompoundWrapper<?> skullOwner = compound.newCompound();
        NBTListWrapper<?> textures = compound.newList();

        skullOwner.setString("Id", id);
        textures.add(compound.newCompound().setString("Value", value));
        skullOwner.setCompound("Properties", compound.newCompound().setList("textures", textures));

        compound.setCompound("SkullOwner", skullOwner);

        return setItemTag(item, compound);
    }

    public static ItemStack makeUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack addMinecraftTag(ItemStack item, MinecraftTag tag, Object... values) {
        NBTCompoundWrapper<?> compound = getItemTag(item);
        NBTListWrapper<?> list = compound.newList();
        for (Object value : values) {
            list.add(value.toString());
        }

        compound.setList(tag.getTagName(), list);

        return setItemTag(item, compound);
    }

    public static ItemStack addAttribute(ItemStack item, String attributeName, int operation, String name, float amount, ItemStackUtils.AttributeSlot slot) {
        NBTCompoundWrapper<?> itemTag = getItemTag(item);

        NBTCompoundWrapper<?> attributeModifier = itemTag.newCompound();
        NBTListWrapper<?> attributeModifiers = itemTag.getList("AttributeModifiers", List.class);

        attributeModifier.setInt("Operation", operation)
                .setString("AttributeName", attributeName)
                .setString("Name", name)
                .setFloat("Amount", amount)
                .setLong("UUIDLeast", 1)
                .setLong("UUIDMost", 1)
                .setString("Slot", slot.value);
        attributeModifiers.add(attributeModifier);
        itemTag.setList("AttributeModifiers", attributeModifiers);

        return setItemTag(item, itemTag);
    }

    public static ItemStack addEnchant(ItemStack item, Enchantment ench, int level) {
        ItemMeta itemM = item.getItemMeta();
        itemM.addEnchant(ench, level, true);
        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addEnchants(ItemStack item, int[] levels, Enchantment... ench) {
        ItemMeta itemM = item.getItemMeta();

        for (int x = 0; x < ench.length; ++x) {
            itemM.addEnchant(ench[x], levels[x], true);
        }

        item.setItemMeta(itemM);
        return item;
    }

    public static ItemStack addStoredEnchants(ItemStack item, int[] levels, Enchantment... ench) {
        EnchantmentStorageMeta e = (EnchantmentStorageMeta) item.getItemMeta();

        for (int x = 0; x < ench.length; ++x) {
            e.addStoredEnchant(ench[x], levels[x], true);
        }

        item.setItemMeta(e);
        return item;
    }

    public static ItemStack setLeatherArmorColor(ItemStack item, Color color) {
        if (item.getType() != Material.LEATHER_HELMET && item.getType() != Material.LEATHER_CHESTPLATE && item.getType() != Material.LEATHER_LEGGINGS && item.getType() != Material.LEATHER_BOOTS) {
            throw new Error("Item isn't a leather armor!");
        } else {
            LeatherArmorMeta leather = (LeatherArmorMeta) item.getItemMeta();
            leather.setColor(color);
            item.setItemMeta(leather);
            return item;
        }
    }

    public static ItemStack setLeatherArmorColor(ItemStack item, int r, int g, int b) {
        if (item.getType() != Material.LEATHER_HELMET && item.getType() != Material.LEATHER_CHESTPLATE && item.getType() != Material.LEATHER_LEGGINGS && item.getType() != Material.LEATHER_BOOTS) {
            throw new Error("Item isn't a leather armor!");
        } else {
            LeatherArmorMeta leather = (LeatherArmorMeta) item.getItemMeta();
            leather.setColor(Color.fromRGB(r, g, b));
            item.setItemMeta(leather);
            return item;
        }
    }

    public static ItemStack setBannerMeta(ItemStack item, ItemStackUtils.IBannerMeta bannerMeta) {
        if (item.getType() == Material.BANNER) {
            BannerMeta b = (BannerMeta) item.getItemMeta();
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
        PotionMeta potion = (PotionMeta) item.getItemMeta();
        if (basePotionData != null) {
            potion.setBasePotionData(basePotionData);
        }

        if (mainEffect != null) {
            potion.setMainEffect(mainEffect);
        }

        for (PotionEffect effect : effects) {
            if (effect != null) {
                potion.addCustomEffect(effect, true);
            }
        }

        return item;
    }

    public enum AttributeSlot {
        MAIN_HAND("mainhand"),
        OFF_HAND("offhand"),
        HELMET("head"),
        CHEST("chest"),
        LEGS("legs"),
        FEET("feet");

        private final String value;

        AttributeSlot(String value) {
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
            for (int x = 0; x < this.patterns.length; ++x) {
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
            for (int x = 0; x < this.patterns.length; ++x) {
                if (this.patterns[x] == pattern) {
                    this.patterns[x] = null;
                }
            }

        }

        public DyeColor getBaseColor() {
            return this.baseColor;
        }

        public void setBaseColor(DyeColor color) {
            this.baseColor = color;
        }

        public List<Pattern> patternsToList() {
            List<Pattern> p = new ArrayList();
            Pattern[] var5;
            int var4 = (var5 = this.patterns).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                Pattern pattern = var5[var3];
                if (pattern != null) {
                    p.add(pattern);
                }
            }

            return p;
        }
    }

    public static boolean isSword(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().endsWith("SWORD");
    }

    public static ItemStack setPages(ItemStack item, List<ChatComponent> components){
        if(!item.getType().equals(Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("item.getType() != Material.WRITTEN_BOOK");
        }

        List<BaseComponent> pages = components.stream().map(ChatComponent::getBaseComponent).collect(Collectors.toList());

        pages.forEach(ChatComponentUtils::fixComponent);
        item = fixItem(item);
        ItemMeta meta = item.getItemMeta();
        CraftMetaBook craft = (CraftMetaBook) meta;
        craft.pages = pages.stream()
                .map(ComponentSerializer::toString)
                .map(IChatBaseComponent.ChatSerializer::a)
                .collect(Collectors.toList());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack fixItem(ItemStack itemStack){
        if(itemStack == null) {
            return null;
        }
        if (itemStack instanceof CraftItemStack) {
            return itemStack; // уже преобразован в NMS формат с тегами
        }

        ItemStack fix = CraftItemStack.asBukkitCopy(CraftItemStack.asNMSCopy(itemStack));
        if(fix == null || fix.getType() != itemStack.getType()){
            throw new IllegalArgumentException("Нельзя преобразовать предмет " + itemStack + " в NMS предмет с тегами, " +
                    "его нельзя представить в виде предмета, только в виде блока!");
        }
        return fix;
    }

    public static boolean isNullOrAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static ItemStack setMetadata(ItemStack stack, String metadata) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setLocalizedName(metadata);
            stack.setItemMeta(meta);
        }

        return stack;
    }

    public static String getMetadata(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        return meta == null ? null : meta.getLocalizedName();
    }

    public static boolean hasMetadata(ItemStack stack, String metadata) {
        ItemMeta meta = stack.getItemMeta();
        return meta != null && metadata.equals(meta.getLocalizedName());
    }

    private static boolean has(String text, String data) {
        int index = data.length() * 2 + 1;
        if (text.length() - 1 < index) {
            return false;
        } else if (text.charAt(index) == 9773) {
            char[] chars = data.toCharArray();
            char[] textChars = text.toCharArray();

            for (int i = 0; i < chars.length; ++i) {
                if (chars[i] != textChars[i * 2 + 1]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static String serialize(ItemStack stack) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("items", stack);
        return configuration.saveToString();
    }

    @SneakyThrows
    public static ItemStack deserialize(String code) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(code);
        return configuration.getItemStack("items");
    }

    public static String[] serializeArray(ItemStack[] stacks) {
        String[] result = new String[stacks.length];
        for (int i = 0; i < stacks.length; i++) {
            result[i] = getItemTag(stacks[i]).toJson();
        }

        return result;
    }

    public static ItemStack[] deserializeArray(String[] keys) {
        ItemStack[] result = new ItemStack[keys.length];

        for (int i = 0; i < keys.length; i++) {
            try {
                result[i] = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(MojangsonParser.parse(keys[i])));
            } catch (MojangsonParseException ex) {
                result[i] = new ItemStack(Material.STONE);
            }
        }

        return result;
    }
}

