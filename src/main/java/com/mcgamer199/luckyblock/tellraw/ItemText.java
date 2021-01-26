package com.mcgamer199.luckyblock.tellraw;

import org.bukkit.inventory.ItemStack;

public class ItemText {

    public ItemText() {
    }

    public static String itemToString(ItemStack item) {
        String text = "{";
        text = text + "id:" + item.getType().name().toLowerCase();
        text = text + ",amount:" + item.getAmount();
        text = text + ",damage:" + item.getDurability();
        if (item.hasItemMeta() && (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore())) {
            text = text + ",display:{";
            if (item.getItemMeta().hasDisplayName()) {
                text = text + "Name:" + item.getItemMeta().getDisplayName();
            }

            if (item.getItemMeta().hasLore()) {
                if (item.getItemMeta().hasDisplayName()) {
                    text = text + ",";
                }

                text = text + "Lore:[";

                for(int x = 0; x < item.getItemMeta().getLore().size(); ++x) {
                    text = text + (String)item.getItemMeta().getLore().get(x) + ",";
                }

                text = text + "]";
            }

            text = text + "}";
        }

        text = text + "}";
        return text;
    }
}
