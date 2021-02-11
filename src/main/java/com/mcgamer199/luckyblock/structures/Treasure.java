package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Treasure extends Structure {

    public Treasure() {}

    public void build(Location loc) {
        Block block = loc.getBlock();

        int x;
        int y;
        int z;
        for (x = -3; x < 4; ++x) {
            for (y = -1; y < 4; ++y) {
                for (z = -3; z < 4; ++z) {
                    block.getLocation().add(x, y, z).getBlock().setType(Material.BEDROCK);
                }
            }
        }

        for (x = -2; x < 3; ++x) {
            for (y = 0; y < 3; ++y) {
                for (z = -2; z < 3; ++z) {
                    block.getLocation().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }

        block.setType(Material.PACKED_ICE);
        block.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(-2.0D, 0.0D, 0.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(2.0D, 0.0D, 0.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(0.0D, 0.0D, 2.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(0.0D, 0.0D, -2.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(-1.0D, 0.0D, -1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(-1.0D, 0.0D, 1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(1.0D, 0.0D, -1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(1.0D, 0.0D, 1.0D).getBlock().setType(Material.PACKED_ICE);
        block.getLocation().add(-2.0D, 1.0D, 0.0D).getBlock().setType(Material.CHEST);
        this.fillChest((Chest) block.getLocation().add(-2.0D, 1.0D, 0.0D).getBlock().getState());
        block.getLocation().add(2.0D, 1.0D, 0.0D).getBlock().setType(Material.CHEST);
        this.fillChest((Chest) block.getLocation().add(2.0D, 1.0D, 0.0D).getBlock().getState());
        block.getLocation().add(0.0D, 1.0D, 2.0D).getBlock().setType(Material.CHEST);
        this.fillChest((Chest) block.getLocation().add(0.0D, 1.0D, 2.0D).getBlock().getState());
        block.getLocation().add(0.0D, 1.0D, -2.0D).getBlock().setType(Material.CHEST);
        this.fillChest((Chest) block.getLocation().add(0.0D, 1.0D, -2.0D).getBlock().getState());
        super.build(loc);
    }

    public boolean canSave() {
        return true;
    }

    private void fillChest(Chest chest) {
        int r = RandomUtils.nextInt(6) + 5;

        for (int x = 0; x < r; ++x) {
            int i = RandomUtils.nextInt(10) + 1;
            ItemStack item = new ItemStack(Material.DIAMOND_BLOCK, RandomUtils.nextInt(3) + 2);
            if (i == 2) {
                item = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
            } else if (i == 3) {
                LBType t = LBType.getRandomType();
                item = t.toItemStack(t.getMaxLuck());
            } else if (i == 4) {
                item = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_SWORD), new int[]{4, 2}, Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT);
            } else if (i == 5) {
                item = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_HELMET), new int[]{4}, Enchantment.PROTECTION_ENVIRONMENTAL);
            } else if (i == 6) {
                item = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_CHESTPLATE), new int[]{4}, Enchantment.PROTECTION_ENVIRONMENTAL);
            } else if (i == 7) {
                item = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_LEGGINGS), new int[]{4}, Enchantment.PROTECTION_ENVIRONMENTAL);
            } else if (i == 8) {
                item = ItemStackUtils.addEnchants(new ItemStack(Material.DIAMOND_BOOTS), new int[]{4}, Enchantment.PROTECTION_ENVIRONMENTAL);
            } else if (i == 9) {
                item = new ItemStack(Material.EMERALD_BLOCK, RandomUtils.nextInt(3) + 1);
            } else if (i == 10) {
                item = new ItemStack(Material.GOLD_BLOCK, RandomUtils.nextInt(4) + 3);
            }

            chest.getInventory().setItem(RandomUtils.nextInt(27), item);
        }
    }

    public int getId() {
        return 15;
    }
}

