package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class DispenserEvents implements Listener {

    public DispenserEvents() {}

    @EventHandler
    private void onShootLB(final BlockDispenseEvent event) {
        if (event.getBlock().getType() == Material.DISPENSER) {
            Block block = event.getBlock();
            if (event.getItem() != null && LBType.isLB(event.getItem())) {
                event.setCancelled(true);
                Dispenser dispenser = (Dispenser) block.getState();
                Block relative = block.getRelative(getDispenserFacing(block));
                if (!relative.getType().isSolid()) {
                    LuckyBlock.placeLB(relative.getLocation(), null, event.getItem(), relative);
                    Scheduler.later(() -> DispenserEvents.removeLBItem(event.getItem(), dispenser), 3);
                }
            }
        }
    }

    @EventHandler
    private void onSpawnCustomEntity(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        Block block = event.getBlock();
        NBTCompoundWrapper<?> itemTag = ItemStackUtils.getItemTag(item);
        String entityClass = itemTag.getString("EntityClass");
        if(entityClass != null) {
            CustomEntity customEntity = CustomEntityManager.createCustomEntity(entityClass);
            if(customEntity != null) {
                customEntity.spawn(block.getRelative(getDispenserFacing(block)).getLocation().add(0.5D, 0.0D, 0.5D));
            }
        }
    }

    private static BlockFace getDispenserFacing(Block block) {
        return ((org.bukkit.material.Dispenser) block.getState().getData()).getFacing();
    }

    private static void removeLBItem(ItemStack item, Dispenser d) {
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
}
