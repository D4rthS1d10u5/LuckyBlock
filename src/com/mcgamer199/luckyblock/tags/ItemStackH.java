package com.mcgamer199.luckyblock.tags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemStackH {
    private ItemStack[] items = new ItemStack[64];
    private int[] chances = new int[64];

    public ItemStackH() {
    }

    public void addItem(ItemStack item, int chance) {
        this.items[this.items.length] = item;
        this.chances[this.chances.length] = chance;
    }

    public void removeItem(int b) {
        if (this.items[b] != null) {
            this.items[b] = null;
        }

        if (this.chances[b] > 0) {
            this.chances[b] = 0;
        }

    }

    public int removeItem(ItemStack item) {
        int total = 0;

        for(int x = 0; x < this.items.length; ++x) {
            if (this.items[x] != null && this.items[x] == item) {
                this.items[x] = null;
                ++total;
            }
        }

        return total;
    }

    public int removeItem(Material mat) {
        int total = 0;

        for(int x = 0; x < this.items.length; ++x) {
            if (this.items[x] != null && this.items[x].getType() == mat) {
                this.items[x] = null;
                ++total;
            }
        }

        return total;
    }

    public int getTotal() {
        int i = 0;

        for(int x = 0; x < this.chances.length; ++x) {
            i += this.chances[x];
        }

        return i;
    }

    public ItemStack getRandomItem() {
        int random = (new Random()).nextInt(this.getTotal()) + 1;
        int prev = 0;

        for(int x = 0; x < this.chances.length; ++x) {
            if (this.chances[x] + prev >= random) {
                return this.items[x];
            }

            prev += this.chances[x];
        }

        return null;
    }
}

