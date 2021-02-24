package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.LBOption;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.events.LBPlaceEvent;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.util.EffectUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaceLuckyBlock extends ColorsClass implements Listener {

    public PlaceLuckyBlock() {
    }

    public static boolean hasOption(LBOption[] options, LBOption option) {
        LBOption[] var5 = options;
        int var4 = options.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            LBOption o = var5[var3];
            if (o == option) {
                return true;
            }
        }

        return false;
    }

    protected static boolean checkForBlocks(LBType lbType, Block block) {
        if (lbType.hasAdditionalBlocks()) {
            String[] var5;
            int var4 = (var5 = lbType.getAdditionalBlocks()).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                String s = var5[var3];
                String[] d = s.split(" ");
                String[] g = d[0].split(",");
                int x = Integer.parseInt(g[0]);
                int y = Integer.parseInt(g[1]);
                int z = Integer.parseInt(g[2]);
                if (block.getLocation().add(x, y, z).getBlock().getType().isSolid()) {
                    return false;
                }
            }
        }

        return true;
    }

    protected static void placeBlocks(Block block, LBType type) {
        String[] ad = type.getAdditionalBlocks();
        String[] var6 = ad;
        int var5 = ad.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            String s = var6[var4];
            String[] d = s.split(" ");
            String[] g = d[0].split(",");
            int x = Integer.parseInt(g[0]);
            int y = Integer.parseInt(g[1]);
            int z = Integer.parseInt(g[2]);
            Material mat = Material.getMaterial(d[1]);
            byte b = Byte.parseByte(d[2]);
            block.getLocation().add(x, y, z).getBlock().setType(mat);
            block.getLocation().add(x, y, z).getBlock().setData(b);
        }

    }

    public static LuckyBlock place(LBType type, Block block, Object placedBy, String drop, int luck, ItemStack item, boolean r, BlockFace face, LBOption... options) {
        if (!LuckyBlock.canSaveMore()) {
            block.setType(Material.AIR);
            return null;
        } else if (type.disabled && placedBy != null && placedBy instanceof Player) {
            send((Player) placedBy, "event.placelb.error.disabled");
            return null;
        } else {
            if (type.hasAdditionalBlocks()) {
                if (!checkForBlocks(type, block)) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player) placedBy, "event.placelb.error.space");
                    }

                    block.setType(Material.AIR);
                    return null;
                }

                placeBlocks(block, type);
            }

            Location location = block.getLocation();
            String[] particleData;
            float pit;
            if (type.allowplaceparticles && type.placeparticles != null && (options == null || options.length == 0 || !hasOption(options, LBOption.NO_EFFECTS))) {
                String particle = type.placeparticles;
                particleData = particle.split(" ");
                pit = Float.parseFloat(particleData[1]);
                float ry = Float.parseFloat(particleData[2]);
                float rz = Float.parseFloat(particleData[3]);
                float speed = Float.parseFloat(particleData[4]);
                int amount = Integer.parseInt(particleData[5]);
                double lx = Double.parseDouble(particleData[6]);
                double ly = Double.parseDouble(particleData[7]);
                double lz = Double.parseDouble(particleData[8]);
                block.getWorld().spawnParticle(MyTasks.getParticle(particleData[0]), location.add(lx, ly, lz), amount, pit, ry, rz, speed);
            }

            String[] s;
            if (type.allowplacesound && type.placesound != null && (options == null || options.length == 0 || !hasOption(options, LBOption.NO_SOUNDS))) {
                Sound sound = null;
                float vol = 100.0F;
                pit = 1.0F;
                s = type.placesound.split(" ");

                try {
                    sound = Sound.valueOf(s[0].toUpperCase());
                } catch (Exception var24) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player) placedBy, "invalid_sound");
                    }
                }

                try {
                    vol = Float.parseFloat(s[1]);
                    pit = Float.parseFloat(s[2]);
                } catch (NumberFormatException var23) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player) placedBy, "invalid_number");
                    }
                }

                EffectUtils.playFixedSound(block.getLocation(), sound, vol, pit, 30);
            }

            if (!LBType.getFolder(type).exists() && placedBy != null && placedBy instanceof Player) {
                send((Player) placedBy, "invalid_file");
                return null;
            } else {
                LuckyBlock luckyBlock = new LuckyBlock(type, block, luck, placedBy, true, r);
                if (face != null) {
                    luckyBlock.facing = face;
                }

                if (drop != null) {
                    particleData = drop.split(":");
                    if (particleData[0].equalsIgnoreCase("LBDrop")) {
                        if (LuckyBlockDrop.isValid(particleData[1])) {
                            luckyBlock.setDrop(LuckyBlockDrop.getByName(particleData[1]), true, true);
                        }
                    } else if (particleData[0].equalsIgnoreCase("CustomDrop") && CustomDropManager.isValid(particleData[1])) {
                        luckyBlock.customDrop = CustomDropManager.getByName(particleData[1]);
                        luckyBlock.refreshCustomDrop();
                    }
                }

                luckyBlock.playEffects();
                String o = null;
                UUID owner = null;
                if (item != null && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0) {
                    for (int xx = 0; xx < item.getItemMeta().getLore().size(); ++xx) {
                        if (item.getItemMeta().getLore().get(xx).startsWith(gray + "Protected:")) {
                            o = item.getItemMeta().getLore().get(xx);
                        } else if (item.getItemMeta().getLore().get(xx).startsWith(gray + "Owner:")) {
                            String[] d = item.getItemMeta().getLore().get(xx).split("Owner: ");
                            owner = UUID.fromString(d[1]);
                        }
                    }
                }

                if (o != null) {
                    if (owner != null) {
                        luckyBlock.owner = owner;
                    }
                } else {
                    luckyBlock.owner = owner;
                }

                if (LuckyBlockPlugin.isDebugEnabled()) {
                    Debug("Lucky block placed",
                            new DebugData("Location", LocationUtils.asString(block.getLocation())),
                            new DebugData("LBType", luckyBlock.getType().getId() + ", " + ChatColor.stripColor(luckyBlock.getType().getName())),
                            new DebugData("Placed By", luckyBlock.getPlacementInfo()),
                            new DebugData("Title", luckyBlock.hasDropOption("Title") ? ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOptions().getString("Title", "&cnull"))) : "unknown"),
                            new DebugData("Drop Type", luckyBlock.customDrop != null ? luckyBlock.customDrop.getName() : luckyBlock.getLuckyBlockDrop().name()),
                            new DebugData("Luck", String.valueOf(luckyBlock.getLuck())),
                            new DebugData("Owner", luckyBlock.hasOwner() ? luckyBlock.owner.toString() : "none"));
                }

                luckyBlock.save();
                return luckyBlock;
            }
        }
    }

    protected static boolean isEnoughSpace(Location loc, int[] requiredSpace) {
        Block bl = loc.getBlock();

        for (int x = requiredSpace[0] * -1; x < requiredSpace[0] + 1; ++x) {
            for (int y = 0; y < requiredSpace[1] + 1; ++y) {
                for (int z = requiredSpace[2] * -1; z < requiredSpace[2] + 1; ++z) {
                    if (bl.getLocation().add(x, y, z).getBlock().getType() != Material.AIR && bl.getLocation().add(x, y, z).getBlock().getType() != loc.getBlock().getType()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static String dropToString(String drop) {
        if (LuckyBlockDrop.isValid(drop)) {
            return "LBDrop:" + LuckyBlockDrop.getByName(drop);
        } else {
            return CustomDropManager.getByName(drop) != null ? "CustomDrop:" + CustomDropManager.getByName(drop).getName() : null;
        }
    }

    @EventHandler
    public void onPlaceLuckyBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack inh = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        String world = block.getWorld().getName();
        if (inh != null && inh.getType() != Material.AIR) {
            boolean found = false;
            List<String> worlds = new ArrayList();
            LBType types = null;
            if (LBType.fromItem(inh) != null) {
                types = LBType.fromItem(inh);
                found = true;
                worlds = types.getWorlds();
            }

            if (found) {
                if (!worlds.contains("*All*") && !worlds.contains(world)) {
                    send(player, "event.placelb.error.world");
                    event.setCancelled(true);
                    return;
                }

                if (types.hasPermission("Placing") && !player.hasPermission(types.getPermission("Placing")) && !player.getName().equalsIgnoreCase(pn)) {
                    event.setCancelled(true);
                    send(player, "event.placelb.error.permission");
                    return;
                }

                if (types.disabled) {
                    event.setCancelled(true);
                    send(player, "event.placelb.error.disabled");
                    return;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                String l = "0%";
                int luck = LBType.getLuck(item);
                boolean r = true;
                String drop = null;
                if (item != null && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0) {
                    for (int x = 0; x < item.getItemMeta().getLore().size(); ++x) {
                        l = item.getItemMeta().getLore().get(x);
                        if (l.startsWith(ChatColor.DARK_PURPLE + "Drop: ")) {
                            String[] s = l.split("Drop: ");
                            if (s.length == 2) {
                                String g = ChatColor.stripColor(s[1].toUpperCase());
                                if (LuckyBlockDrop.isValid(g)) {
                                    r = false;
                                    drop = "LBDrop:" + g;
                                } else if (CustomDropManager.isValid(g)) {
                                    r = false;
                                    drop = "CustomDrop:" + g;
                                }
                            }
                        }
                    }
                }

                LBPlaceEvent lbe = new LBPlaceEvent(block, player);
                Bukkit.getPluginManager().callEvent(lbe);
                if (lbe.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }

                place(types, block, player, drop, luck, player.getInventory().getItemInMainHand(), r, event.getBlockAgainst().getFace(block));
            }

        }
    }

    @EventHandler
    public void placeDetector(BlockPlaceEvent event) {
        if (event.getItemInHand() != null && event.getItemInHand().getType() == Material.PISTON_BASE) {
            ItemStack item = event.getItemInHand();
            if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("" + blue + bold + "Detector")) {
                Block block = event.getBlock();
                Player player = event.getPlayer();
                if (isEnoughSpace(block.getLocation(), new int[]{2, 1, 2})) {
                    Detector d = new Detector(RandomUtils.nextInt(99999) + 1);
                    d.setLoc(block.getLocation());
                    block.setType(Material.OBSIDIAN);
                    d.addBlock(block);
                    block.getRelative(BlockFace.EAST).setType(Material.OBSIDIAN);
                    d.addBlock(block.getRelative(BlockFace.EAST));
                    block.getRelative(BlockFace.WEST).setType(Material.OBSIDIAN);
                    d.addBlock(block.getRelative(BlockFace.WEST));
                    block.getRelative(BlockFace.SOUTH).setType(Material.OBSIDIAN);
                    d.addBlock(block.getRelative(BlockFace.SOUTH));
                    block.getRelative(BlockFace.NORTH).setType(Material.OBSIDIAN);
                    d.addBlock(block.getRelative(BlockFace.NORTH));
                    block.getRelative(BlockFace.UP).setType(Material.GOLD_BLOCK);
                    d.addBlock(block.getRelative(BlockFace.UP));
                    block.getRelative(BlockFace.UP).setData((byte) 1);
                    send_no(player, "event.detector.break");
                    player.sendMessage(green + "ID:" + d.getId());
                    d.save();
                } else {
                    event.setCancelled(true);
                    send(player, "event.detector.no_space");
                }
            }
        }

    }

    @EventHandler
    public void breakDetector(BlockBreakEvent event) {
        String dim = LocationUtils.asString(event.getBlock().getLocation());
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.getInventory().getItemInMainHand() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.WOOD_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.GOLD_SWORD || item.getType() == Material.DIAMOND_SWORD) {
                return;
            }
        }

        Detector d = null;

        for (Detector detector : LuckyBlockAPI.detectors) {
            String[] var10;
            int var9 = (var10 = detector.getBlocks()).length;

            for (int var8 = 0; var8 < var9; ++var8) {
                String s = var10[var8];
                if (s != null && s.equalsIgnoreCase(dim)) {
                    d = detector;
                }
            }
        }

        if (d != null) {
            d.dispose();
            send_no(player, "event.detector.break");
        }

    }
}
