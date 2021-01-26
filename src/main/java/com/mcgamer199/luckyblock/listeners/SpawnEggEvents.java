package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.item.ItemReflection;
import com.mcgamer199.luckyblock.engine.IObjects;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.CustomEntityLoader;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.ChatColor;
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

import java.util.Set;

public class SpawnEggEvents extends ColorsClass implements Listener {

    public SpawnEggEvents() {
    }

    @EventHandler
    public void onUseCustomSpawnEgg(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null) {
            ItemStack item = event.getItem();
            if (compareItems(item, IObjects.ITEM_SPAWN_ENTITY)) {
                Player player = event.getPlayer();
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

                event.setCancelled(true);
                if (!ItemReflection.hasKey(item, "EntityClass") || ItemReflection.getString(item, "EntityClass") == null) {
                    player.sendMessage(ChatColor.RED + "Invalid Entity!");
                    return;
                }

                String val = ItemReflection.getString(item, "EntityClass");
                Object o = CustomEntityLoader.getCustomEntity(val);
                if (o != null) {
                    CustomEntity c = (CustomEntity) o;
                    c.spawn(loc);
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Entity!");
                }
            }
        }

    }
}
