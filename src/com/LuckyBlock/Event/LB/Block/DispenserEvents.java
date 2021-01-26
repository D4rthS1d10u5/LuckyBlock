package com.LuckyBlock.Event.LB.Block;

import com.LuckyBlock.Engine.IObjects;
import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.logic.ColorsClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.core.entity.CustomEntity;
import org.core.entity.CustomEntityLoader;
import org.core.logic.ITask;
import org.core.nbt.ItemReflection;

public class DispenserEvents extends ColorsClass implements Listener {
    public DispenserEvents() {
    }

    @EventHandler
    private void onShootLB(final BlockDispenseEvent event) {
        if (event.getBlock().getType() == Material.DISPENSER) {
            Block block = event.getBlock();
            if (event.getItem() != null && LBType.isLB(event.getItem())) {
                event.setCancelled(true);
                Block b = block.getRelative(getDispenserF(block));
                if (!b.getType().isSolid()) {
                    LB.placeLB(b.getLocation(), (LBType)null, event.getItem(), b);
                    ITask task = new ITask();
                    task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
                        public void run() {
                            DispenserEvents.removeLBItem(event.getItem(), (Dispenser)event.getBlock().getState());
                        }
                    }, 3L));
                }
            }
        }

    }

    static BlockFace getDispenserF(Block block) {
        BlockFace face = null;
        MaterialData md = block.getState().getData();
        org.bukkit.material.Dispenser d = (org.bukkit.material.Dispenser)md;
        face = d.getFacing();
        return face;
    }

    static void removeLBItem(ItemStack item, Dispenser d) {
        LBType type = LBType.fromItem(item);
        boolean found = false;

        for(int x = 0; x < d.getInventory().getSize(); ++x) {
            if (!found) {
                ItemStack it = d.getInventory().getItem(x);
                if (it != null && LBType.isLB(it) && LBType.fromItem(it) == type) {
                    found = true;
                    if (it.getAmount() > 1) {
                        it.setAmount(it.getAmount() - 1);
                        d.getInventory().setItem(x, it);
                    } else {
                        d.getInventory().removeItem(new ItemStack[]{d.getInventory().getItem(x)});
                    }

                    d.update(true);
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
                CustomEntity c = (CustomEntity)o;
                c.spawn(b.getLocation().add(0.5D, 0.0D, 0.5D));
            }
        }

    }
}
