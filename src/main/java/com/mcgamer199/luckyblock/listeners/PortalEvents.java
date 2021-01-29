package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class PortalEvents extends ColorsClass implements Listener {
    public PortalEvents() {
    }

    public static void removePortal(Block block) {
        boolean t = false;
        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.UP)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.UP)));
            block.getRelative(BlockFace.UP).setType(Material.AIR);
            t = true;
        }

        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.EAST)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.EAST)));
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
            t = true;
        }

        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.WEST)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.WEST)));
            block.getRelative(BlockFace.WEST).setType(Material.AIR);
            t = true;
        }

        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.DOWN)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.DOWN)));
            block.getRelative(BlockFace.DOWN).setType(Material.AIR);
            t = true;
        }

        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.SOUTH)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.SOUTH)));
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
            t = true;
        }

        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.NORTH)))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.NORTH)));
            block.getRelative(BlockFace.NORTH).setType(Material.AIR);
            t = true;
        }

        if (t) {
            removePortal(block.getRelative(BlockFace.UP));
            removePortal(block.getRelative(BlockFace.EAST));
            removePortal(block.getRelative(BlockFace.DOWN));
            removePortal(block.getRelative(BlockFace.WEST));
            removePortal(block.getRelative(BlockFace.SOUTH));
            removePortal(block.getRelative(BlockFace.NORTH));
        }

    }

    @EventHandler
    private void activatePortal(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getClickedBlock().getType() != Material.AIR) {
                    Block block = null;
                    if (event.getBlockFace() == BlockFace.UP) {
                        block = event.getClickedBlock();
                    } else if (event.getBlockFace() == BlockFace.EAST) {
                        block = event.getClickedBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN);
                    } else if (event.getBlockFace() == BlockFace.WEST) {
                        block = event.getClickedBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN);
                    }

                    if (event.getItem().getType() == Material.FLINT_AND_STEEL) {
                        if (block != null) {
                            if (LuckyBlock.isLuckyBlock(block)) {
                                Player player = event.getPlayer();
                                if (player.hasPermission("lb.createportal")) {
                                    if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)) && LuckyBlock.getFromBlock(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)).getType().isPortal()) {
                                        this.a(block, BlockFace.EAST, player);
                                        SoundManager.playFixedSound(block.getLocation(), SoundManager.getSound("portal_activate"), 1.0F, 1.0F, 22);
                                    } else if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)) && LuckyBlock.getFromBlock(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)).getType().isPortal()) {
                                        this.a(block, BlockFace.WEST, player);
                                        SoundManager.playFixedSound(block.getLocation(), SoundManager.getSound("portal_activate"), 1.0F, 1.0F, 22);
                                    } else if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)) && LuckyBlock.getFromBlock(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)).getType().isPortal()) {
                                        this.a(block, BlockFace.SOUTH, player);
                                        SoundManager.playFixedSound(block.getLocation(), SoundManager.getSound("portal_activate"), 1.0F, 1.0F, 22);
                                    } else if (LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)) && LuckyBlock.getFromBlock(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)).getType().isPortal()) {
                                        this.a(block, BlockFace.NORTH, player);
                                        SoundManager.playFixedSound(block.getLocation(), SoundManager.getSound("portal_activate"), 1.0F, 1.0F, 22);
                                    }

                                    LuckyBlockAPI.savePortals();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void a(Block block, BlockFace face, Player player) {
        if (face == BlockFace.EAST) {
            if (!LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP))) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST), BlockFace.WEST, 2)) {
                return;
            }
        } else if (face == BlockFace.WEST) {
            if (!LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP))) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST), BlockFace.EAST, 2)) {
                return;
            }
        } else if (face == BlockFace.NORTH) {
            if (!LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP))) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH), BlockFace.UP, 3)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH), BlockFace.SOUTH, 2)) {
                return;
            }
        } else {
            if (face != BlockFace.SOUTH) {
                return;
            }

            if (!LuckyBlock.isLuckyBlock(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP))) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP), BlockFace.UP, 2)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH), BlockFace.UP, 3)) {
                return;
            }

            if (!LuckyBlock.isRelativesLB(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH), BlockFace.NORTH, 2)) {
                return;
            }
        }

        this.b(block, face, player);
    }

    private void b(Block block, BlockFace face, Player player) {
        Block b = block.getRelative(BlockFace.UP);
        int z;
        int y;
        if (face == BlockFace.EAST) {
            for (z = 0; z > -2; --z) {
                for (y = 0; y < 3; ++y) {
                    b.getLocation().add(z, y, 0.0D).getBlock().setType(Material.STAINED_GLASS);
                    b.getLocation().add(z, y, 0.0D).getBlock().setData((byte) 10);
                    LuckyBlockAPI.lbwblocks.add(LuckyBlock.blockToString(b.getLocation().add(z, y, 0.0D).getBlock()));
                }
            }
        } else if (face == BlockFace.WEST) {
            for (z = 0; z < 2; ++z) {
                for (y = 0; y < 3; ++y) {
                    b.getLocation().add(z, y, 0.0D).getBlock().setType(Material.STAINED_GLASS);
                    b.getLocation().add(z, y, 0.0D).getBlock().setData((byte) 10);
                    LuckyBlockAPI.lbwblocks.add(LuckyBlock.blockToString(b.getLocation().add(z, y, 0.0D).getBlock()));
                }
            }
        } else if (face == BlockFace.NORTH) {
            for (z = 0; z < 2; ++z) {
                for (y = 0; y < 3; ++y) {
                    b.getLocation().add(0.0D, y, z).getBlock().setType(Material.STAINED_GLASS);
                    b.getLocation().add(0.0D, y, z).getBlock().setData((byte) 10);
                    LuckyBlockAPI.lbwblocks.add(LuckyBlock.blockToString(b.getLocation().add(0.0D, y, z).getBlock()));
                }
            }
        } else if (face == BlockFace.SOUTH) {
            for (z = 0; z > -2; --z) {
                for (y = 0; y < 3; ++y) {
                    b.getLocation().add(0.0D, y, z).getBlock().setType(Material.STAINED_GLASS);
                    b.getLocation().add(0.0D, y, z).getBlock().setData((byte) 10);
                    LuckyBlockAPI.lbwblocks.add(LuckyBlock.blockToString(b.getLocation().add(0.0D, y, z).getBlock()));
                }
            }
        }

    }

    @EventHandler
    private void onRightClickPortal(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() != Material.AIR && LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(event.getClickedBlock()))) {
            Player player = event.getPlayer();
            if (player.hasPermission("lb.useportal")) {
                player.playSound(player.getLocation(), SoundManager.getSound("portal_teleport"), 100.0F, 1.0F);
                if (!LuckyBlockWorld.equals(player.getWorld().getGenerator())) {
                    LuckyBlockAPI.addLocation(player, player.getLocation());
                    World world = LuckyBlockWorld.getWorld(Arrays.asList(WorldOptions.NORMAL));
                    player.teleport(new Location(world, LuckyBlockPlugin.randoms.nextInt(1000) - 500, world.getSpawnLocation().getY(), LuckyBlockPlugin.randoms.nextInt(1000) - 500));
                    send(player, "event.portal.teleport");
                    LuckyBlockAPI.savePortals();
                } else {
                    Location loc = LuckyBlockAPI.getLocation(player);
                    if (loc != null) {
                        player.teleport(loc);
                    } else {
                        player.teleport(player.getBedSpawnLocation());
                    }
                }
            }
        }

    }

    @EventHandler
    public void onBreakPortal(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block))) {
            LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block));
            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.UP)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.UP)));
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.UP));
            }

            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.EAST)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.EAST)));
                block.getRelative(BlockFace.EAST).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.EAST));
            }

            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.WEST)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.WEST)));
                block.getRelative(BlockFace.WEST).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.WEST));
            }

            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.DOWN)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.DOWN)));
                block.getRelative(BlockFace.DOWN).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.DOWN));
            }

            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.SOUTH)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.SOUTH)));
                block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.SOUTH));
            }

            if (LuckyBlockAPI.lbwblocks.contains(LuckyBlock.blockToString(block.getRelative(BlockFace.NORTH)))) {
                LuckyBlockAPI.lbwblocks.remove(LuckyBlock.blockToString(block.getRelative(BlockFace.NORTH)));
                block.getRelative(BlockFace.NORTH).setType(Material.AIR);
                removePortal(block.getRelative(BlockFace.NORTH));
            }
        }

        LuckyBlockAPI.savePortals();
    }

    @EventHandler
    private void onPlacePortal(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR && player.getInventory().getItemInMainHand().getType() == Material.STAINED_GLASS && player.getInventory().getItemInMainHand().getDurability() == 2 && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Lucky Block Portal")) {
            LuckyBlockAPI.lbwblocks.add(LuckyBlock.blockToString(event.getBlock()));
            player.sendMessage(ChatColor.GREEN + "Placed lucky block portal!");
        }

    }
}
