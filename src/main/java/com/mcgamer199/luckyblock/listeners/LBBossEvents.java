package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.resources.Trophy;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LBBossEvents implements Listener {

    public LBBossEvents() {
    }

    @EventHandler
    public void onPickUpItem(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack() != null) {
            ItemStack item = event.getItem().getItemStack();
            if ((item.getType() == Material.WOOL || item.getType() == Material.COAL_BLOCK) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "LBWither's Bomb")) {
                Player player = event.getPlayer();
                Location l = player.getLocation();
                event.setCancelled(true);
                event.getItem().remove();
                if (item.getType() == Material.WOOL) {
                    if (item.getDurability() == 11) {
                        player.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 2.0F, false, false);
                    } else if (item.getDurability() == 12) {
                        player.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 3.0F, false, false);
                    } else if (item.getDurability() == 13) {
                        player.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 3.5F, false, false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 3));
                    }
                } else if (item.getType() == Material.COAL_BLOCK) {
                    player.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 5.0F, false, false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 160, 5));
                }
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true
    )
    private void onPlaceTrophy(BlockPlaceEvent event) {
        if (event.getItemInHand() != null) {
            ItemStack item = event.getItemInHand();
            if (item.getType() == Material.SKULL_ITEM && item.getDurability() == 3 && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String name = item.getItemMeta().getDisplayName();
                if (name.startsWith(ChatColor.GOLD + "Trophy: ")) {
                    item.setAmount(1);
                    Trophy.place(event.getBlock(), item);
                }
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true
    )
    private void onBreakTrophy(BlockBreakEvent event) {
        Trophy t = Trophy.getByBlock(event.getBlock());
        if (t != null) {
            event.setCancelled(true);
            if (t.getItemToDrop() != null && t.getItemToDrop().getType() != Material.AIR) {
                Player p = event.getPlayer();
                if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                    Item i = event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), t.getItemToDrop());
                    i.getItemStack().setAmount(1);
                }
            }

            t.remove();
            event.getBlock().setType(Material.AIR);
        }
    }
}
