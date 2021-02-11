package com.mcgamer199.luckyblock.structures;

import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.listeners.LuckyBlockWorld;
import com.mcgamer199.luckyblock.listeners.WorldOptions;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class LuckyDungeon extends Structure {
    public LuckyDungeon() {
    }

    public int getId() {
        return 2;
    }

    public void build(Location loc) {
        if (!LuckyBlockWorld.worlds.containsKey(loc.getWorld().getName()) || !LuckyBlockWorld.getOptions(loc.getWorld().getName()).contains(WorldOptions.SUPER_FLAT)) {
            Block block = loc.getBlock();

            int x;
            int r;
            for (x = -3; x < 4; ++x) {
                for (x = -3; x < 4; ++x) {
                    for (r = -3; r < 4; ++r) {
                        block.getLocation().add(x, x, r).getBlock().setType(Material.AIR);
                    }
                }
            }

            for (x = -3; x < 4; ++x) {
                for (x = -3; x < 4; ++x) {
                    block.getLocation().add(x, -4.0D, x).getBlock().setType(Material.MOSSY_COBBLESTONE);
                }
            }

            block.getLocation().add(0.0D, -3.0D, -2.0D).getBlock().setType(Material.CHEST);
            block.getLocation().add(1.0D, -3.0D, -2.0D).getBlock().setType(Material.TORCH);
            block.getLocation().add(-1.0D, -3.0D, -2.0D).getBlock().setType(Material.TORCH);
            Chest chest = (Chest) block.getLocation().add(0.0D, -3.0D, -2.0D).getBlock().getState();

            for (x = 0; x < RandomUtils.nextInt(7) + 10; ++x) {
                r = RandomUtils.nextInt(10) + 1;
                if (r == 1) {
                    chest.getBlockInventory().setItem(RandomUtils.nextInt(27), new ItemStack(Material.DIAMOND, RandomUtils.nextInt(3) + 1));
                } else if (r > 1 && r < 4) {
                    chest.getBlockInventory().setItem(RandomUtils.nextInt(27), new ItemStack(Material.GOLD_INGOT, RandomUtils.nextInt(4) + 1));
                } else if (r > 3 && r < 7) {
                    chest.getBlockInventory().setItem(RandomUtils.nextInt(27), new ItemStack(Material.IRON_INGOT, RandomUtils.nextInt(5) + 2));
                } else if (r > 6 && r < 8) {
                    chest.getBlockInventory().setItem(RandomUtils.nextInt(27), new ItemStack(Material.EMERALD, RandomUtils.nextInt(3) + 1));
                } else {
                    LBType t;
                    ItemStack item;
                    if (!LuckyBlockWorld.worlds.containsKey(loc.getWorld().getName())) {
                        t = LBType.getDefaultType();
                        item = t.toItemStack(t.getRandomP(), RandomUtils.nextInt(6) + 1);
                        chest.getBlockInventory().setItem(RandomUtils.nextInt(27), item);
                    } else {
                        t = LBType.getTypes().get(0);

                        for (Object value : LuckyBlockWorld.worlds.get(loc.getWorld().getName())) {
                            WorldOptions o = (WorldOptions) value;
                            if (o == WorldOptions.ID) {
                                t = LBType.fromId(o.getId());
                            }
                        }

                        item = t.toItemStack(t.getRandomP(), RandomUtils.nextInt(6) + 1);
                        chest.getInventory().setItem(RandomUtils.nextInt(27), item);
                    }
                }
            }

            super.build(loc);
        }
    }
}
