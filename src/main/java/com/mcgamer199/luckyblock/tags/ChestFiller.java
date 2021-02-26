package com.mcgamer199.luckyblock.tags;

import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestFiller {
    public Chest chest;
    public ConfigurationSection section;
    public String loc1;
    protected int inventory_size = 96;

    public ChestFiller(ConfigurationSection section, Chest chest) {
        this.section = section;
        this.chest = chest;
    }

    public ItemStack[] getRandomChestItems(ConfigurationSection c) {
        ItemStack[] items = new ItemStack[this.inventory_size];
        if (c != null) {
            String i = "Inventory";
            int times = 1;
            if (c.getString("Rolls") != null) {
                String[] d = c.getString("Rolls").split("-");
                times = com.mcgamer199.luckyblock.tags.BlockTags.getRandomNumber(d);
            }

            if (c.getConfigurationSection(i) != null) {
                List<String> disabledItems = new ArrayList();

                for (int x = times; x > 0; --x) {
                    String s = com.mcgamer199.luckyblock.tags.BlockTags.getRandomLoc(c.getConfigurationSection(i));
                    ConfigurationSection f = c.getConfigurationSection(i).getConfigurationSection(s);
                    int slot = com.mcgamer199.luckyblock.tags.BlockTags.getRandomNumber(new String[]{"0", "26"});
                    if (f.getString("Slot") != null) {
                        String[] slotT = f.getString("Slot").split("-");
                        slot = com.mcgamer199.luckyblock.tags.BlockTags.getRandomNumber(slotT);
                    }

                    if (f.getStringList("Disabled") != null && f.getStringList("Disabled").size() > 0) {
                        List<String> list = f.getStringList("Disabled");

                        disabledItems.addAll(list);
                    }

                    if (slot < this.inventory_size && !disabledItems.contains(s)) {
                        ConfigurationSection j = c.getConfigurationSection(i).getConfigurationSection(s);
                        ItemStack item = ItemStackGetter.getItemStack(j);
                        if (item != null) {
                            items[slot] = item;
                        }

                        if (f.getStringList("With") != null && f.getStringList("With").size() > 0) {
                            List<String> list = f.getStringList("With");

                            for (int z = 0; z < list.size(); ++z) {
                                String a = list.get(z);
                                ConfigurationSection h = c.getConfigurationSection(i).getConfigurationSection(a);
                                int slot1 = com.mcgamer199.luckyblock.tags.BlockTags.getRandomNumber(new String[]{"0", "26"});
                                if (h.getString("Slot") != null) {
                                    String[] slotT = h.getString("Slot").split("-");
                                    slot1 = com.mcgamer199.luckyblock.tags.BlockTags.getRandomNumber(slotT);
                                }

                                if (slot1 < this.inventory_size) {
                                    ItemStack item1 = ItemStackGetter.getItemStack(c.getConfigurationSection(i).getConfigurationSection(a));
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

    public void fill() {
        if (this.loc1 == null) {
            this.loc1 = BlockTags.getRandomL(this.section);
        }

        ItemStack[] items = this.getRandomChestItems(this.section.getConfigurationSection(this.loc1));

        for (int i = 0; i < items.length; ++i) {
            if (i < this.chest.getInventory().getSize() && items[i] != null) {
                this.chest.getInventory().setItem(i, items[i]);
            }
        }

    }
}
