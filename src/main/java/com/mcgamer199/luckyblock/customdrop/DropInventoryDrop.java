package com.mcgamer199.luckyblock.customdrop;

import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DropInventoryDrop implements CustomDrop {
    static Random random = new Random();

    public DropInventoryDrop() {
    }

    public String getName() {
        return "DROP_INVENTORY";
    }

    public boolean isVisible() {
        return true;
    }

    public DropOption[] getDefaultOptions() {
        return new DropOption[]{new DropOption("ShowName", new Boolean[]{false})};
    }

    public String getDescription() {
        return MyTasks.val("desc.drop.drop_inventory", false);
    }

    public boolean isEnabledByCommands() {
        return true;
    }

    public void function(LB lb, Player player) {
        if (player.getInventory().getContents() != null) {
            ItemStack[] items = player.getInventory().getContents();

            for(int x = 0; x < items.length; ++x) {
                if (items[x] != null) {
                    Item item = player.getWorld().dropItem(player.getLocation().add((double)(random.nextInt(4) - 2), 0.0D, (double)(random.nextInt(4) - 2)), items[x]);
                    item.setPickupDelay(60);
                    if (lb.hasDropOption("ShowName") && lb.getDropOption("ShowName").getValues()[0].toString().equalsIgnoreCase("true")) {
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
