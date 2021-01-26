//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.yottaevents;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtils {
    public ItemStackUtils() {
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
}
