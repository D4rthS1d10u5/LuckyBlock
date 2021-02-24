package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnEggEvents extends ColorsClass implements Listener {

    public SpawnEggEvents() {}

    @EventHandler(ignoreCancelled = true)
    public void onUseCustomSpawnEgg(PlayerInteractEvent event) {
        if (event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            ItemStack item = event.getItem();
            Player player = event.getPlayer();

            NBTCompoundWrapper<?> itemTag = ItemStackUtils.getItemTag(item);
            String entityClass = itemTag.getString("EntityClass");

            if(entityClass != null) {
                CustomEntity customEntity = CustomEntityManager.createCustomEntity(entityClass);
                if(customEntity == null) {
                    player.sendMessage("§cInvalid Entity!");
                    return;
                }

                Location loc;
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    Block b = player.getTargetBlock(null, 6);
                    if (b.getType() != Material.WATER && b.getType() != Material.STATIONARY_WATER) {
                        return;
                    }

                    loc = b.getLocation().add(0.5D, 0.5D, 0.5D);
                } else {
                    loc = event.getClickedBlock().getLocation();
                    BlockFace f = event.getBlockFace();
                    if (f == BlockFace.UP) {
                        loc = loc.add(0.5D, 1.0D, 0.5D);
                    } else if (f == BlockFace.DOWN) {
                        loc = loc.add(0.5D, -2.0D, 0.5D);
                    } else if (f == BlockFace.EAST) {
                        loc = loc.add(1.5D, 0.0D, 0.5D);
                    } else if (f == BlockFace.WEST) {
                        loc = loc.add(-0.5D, 0.0D, 0.5D);
                    } else if (f == BlockFace.NORTH) {
                        loc = loc.add(0.5D, 0.0D, -0.5D);
                    } else if (f == BlockFace.SOUTH) {
                        loc = loc.add(0.5D, 0.0D, 1.5D);
                    }
                }
                customEntity.spawn(loc);
            } else {
                player.sendMessage("§cInvalid Entity!");
            }
        }
    }
}
