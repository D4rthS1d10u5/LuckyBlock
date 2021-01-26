package com.LuckyBlock.Event.LB;

import com.LuckyBlock.Events.LBInteractEvent;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.World.Engine.LuckyBlockWorld;
import com.LuckyBlock.World.Structures.BossDungeon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractLB implements Listener {
    public InteractLB() {
    }

    @EventHandler
    private void onInteractLB(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && LB.isLuckyBlock(block)) {
                LBInteractEvent e = new LBInteractEvent(event.getPlayer(), LB.getFromBlock(block), event.getBlockFace());
                Bukkit.getPluginManager().callEvent(e);
            }
        }

    }

    @EventHandler
    public void onInteract(LBInteractEvent event) {
        Block block = event.getClickedBlock();
        LB lb = LB.getFromBlock(block);
        Player player = event.getPlayer();
        if (lb != null) {
            if (lb.getLuck() == lb.getType().getMaxLuck() && block.getRelative(BlockFace.UP).getType() == Material.CHEST && block.getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK && LuckyBlockWorld.equals(player.getWorld().getGenerator())) {
                Chest chest = (Chest)block.getRelative(BlockFace.UP).getState();
                int total = 0;

                for(int x = 0; x < chest.getInventory().getSize(); ++x) {
                    if (chest.getInventory().getItem(x) != null && chest.getInventory().getItem(x).getType() != Material.AIR) {
                        ItemStack item = chest.getInventory().getItem(x);
                        if (InteractLB.ItemWorth.getByMaterial(item.getType()) != null) {
                            total += InteractLB.ItemWorth.getByMaterial(item.getType()).value * item.getAmount();
                        }
                    }
                }

                if (total > 1000) {
                    block.setType(Material.AIR);
                    block.getRelative(BlockFace.UP).setType(Material.AIR);
                    block.getRelative(BlockFace.DOWN).setType(Material.AIR);
                    BossDungeon dungeon = new BossDungeon();
                    dungeon.build(block.getLocation());
                }
            }

        }
    }

    public static enum ItemWorth {
        IRON_INGOT(10),
        GOLD_INGOT(18),
        DIAMOND(22),
        EMERALD(25),
        COAL(8),
        GOLD_NUGGET(2),
        IRON_BLOCK(90),
        GOLD_BLOCK(160),
        DIAMOND_BLOCK(200),
        EMERALD_BLOCK(225),
        NETHER_STAR(350),
        GOLDEN_APPLE(100);

        private int value;

        private ItemWorth(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static InteractLB.ItemWorth getByMaterial(Material mat) {
            return getByName(mat.name());
        }

        public static InteractLB.ItemWorth getByName(String name) {
            InteractLB.ItemWorth[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                InteractLB.ItemWorth item = var4[var2];
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }

            return null;
        }
    }
}

