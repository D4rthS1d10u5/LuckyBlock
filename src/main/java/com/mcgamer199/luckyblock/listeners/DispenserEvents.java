package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.item.ItemReflection;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.CustomEntityLoader;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class DispenserEvents extends ColorsClass implements Listener {
    public DispenserEvents() {
    }

    static BlockFace getDispenserF(Block block) {
        BlockFace face = null;
        MaterialData md = block.getState().getData();
        org.bukkit.material.Dispenser d = (org.bukkit.material.Dispenser) md;
        face = d.getFacing();
        return face;
    }

    static void removeLBItem(ItemStack item, Dispenser d) {
        LBType type = LBType.fromItem(item);
        boolean found = false;

        for (int x = 0; x < d.getInventory().getSize(); ++x) {
            if (!found) {
                ItemStack it = d.getInventory().getItem(x);
                if (it != null && LBType.isLB(it) && LBType.fromItem(it) == type) {
                    found = true;
                    if (it.getAmount() > 1) {
                        it.setAmount(it.getAmount() - 1);
                        d.getInventory().setItem(x, it);
                    } else {
                        d.getInventory().removeItem(d.getInventory().getItem(x));
                    }

                    d.update(true);
                }
            }
        }

    }

    @EventHandler
    private void onShootLB(final BlockDispenseEvent event) {
        if (event.getBlock().getType() == Material.DISPENSER) {
            Block block = event.getBlock();
            if (event.getItem() != null && LBType.isLB(event.getItem())) {
                event.setCancelled(true);
                Block b = block.getRelative(getDispenserF(block));
                if (!b.getType().isSolid()) {
                    LuckyBlock.placeLB(b.getLocation(), null, event.getItem(), b);
                    ITask task = new ITask();
                    task.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
                        public void run() {
                            DispenserEvents.removeLBItem(event.getItem(), (Dispenser) event.getBlock().getState());
                        }
                    }, 3L));
                }
            }
        }

    }

    @EventHandler
    private void onSpawnCustomEntity(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        Block block = event.getBlock();
        if (item != null && compareItems(item, IObjects.ITEM_SPAWN_ENTITY)) {
            event.setCancelled(true);
            if (!ItemReflection.hasKey(item, "EntityClass") || ItemReflection.getString(item, "EntityClass") == null) {
                return;
            }

            String val = ItemReflection.getString(item, "EntityClass");
            Object o = CustomEntityLoader.getCustomEntity(val);
            Block b = block.getRelative(getDispenserF(block));
            if (o != null) {
                CustomEntity c = (CustomEntity) o;
                c.spawn(b.getLocation().add(0.5D, 0.0D, 0.5D));
            }
        }

    }
}
