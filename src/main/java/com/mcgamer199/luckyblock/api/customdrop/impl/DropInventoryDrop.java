package com.mcgamer199.luckyblock.api.customdrop.impl;

import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DropInventoryDrop implements CustomDrop {

    private static final Properties dropOptions = new Properties().putBoolean("ShowName", false);

    public DropInventoryDrop() {}

    public String getName() {
        return "DROP_INVENTORY";
    }

    public boolean isVisible() {
        return true;
    }

    public Properties getDropOptions() {
        return dropOptions;
    }

    public String getDescription() {
        return MyTasks.val("desc.drop.drop_inventory", false);
    }

    public boolean isEnabledByCommands() {
        return true;
    }

    public void execute(LuckyBlock luckyBlock, Player player) {
        if (player.getInventory().getContents() != null) {
            ItemStack[] items = player.getInventory().getContents();

            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    Item item = player.getWorld().dropItem(player.getLocation().add(RandomUtils.nextInt(4) - 2, 0.0D, RandomUtils.nextInt(4) - 2), itemStack);
                    item.setPickupDelay(60);
                    if (luckyBlock.getDropOptions().getBoolean("ShowName")) {
                        if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName()) {
                            item.setCustomName("Your " + item.getItemStack().getItemMeta().getDisplayName());
                        } else {
                            item.setCustomName("Your " + item.getItemStack().getType().name());
                        }

                        item.setCustomNameVisible(true);
                    }
                }
            }

            player.getInventory().clear();
        }
    }
}
