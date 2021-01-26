package com.mcgamer199.luckyblock.event.lb.block;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.engine.LuckyBlockAPI;
import com.mcgamer199.luckyblock.events.LBPlaceEvent;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LBType.BlockProperty;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.MyTasks;
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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlaceLuckyBlock extends ColorsClass implements Listener {

    public PlaceLuckyBlock() {
    }

    public static boolean hasOption(PlaceLuckyBlock.LBOption[] options, PlaceLuckyBlock.LBOption option) {
        PlaceLuckyBlock.LBOption[] var5 = options;
        int var4 = options.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            PlaceLuckyBlock.LBOption o = var5[var3];
            if (o == option) {
                return true;
            }
        }

        return false;
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
                if (!((List)worlds).contains("*All*") && !((List)worlds).contains(world)) {
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
                    for(int x = 0; x < item.getItemMeta().getLore().size(); ++x) {
                        l = (String)item.getItemMeta().getLore().get(x);
                        if (l.startsWith(ChatColor.DARK_PURPLE + "Drop: ")) {
                            String[] s = l.split("Drop: ");
                            if (s.length == 2) {
                                String g = ChatColor.stripColor(s[1].toUpperCase());
                                if (LBDrop.isValid(g)) {
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

    protected static boolean checkForBlocks(LBType lbType, Block block) {
        if (lbType.hasAdditionalBlocks()) {
            String[] var5;
            int var4 = (var5 = lbType.getAdditionalBlocks()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                String s = var5[var3];
                String[] d = s.split(" ");
                String[] g = d[0].split(",");
                int x = Integer.parseInt(g[0]);
                int y = Integer.parseInt(g[1]);
                int z = Integer.parseInt(g[2]);
                if (block.getLocation().add((double)x, (double)y, (double)z).getBlock().getType().isSolid()) {
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

        for(int var4 = 0; var4 < var5; ++var4) {
            String s = var6[var4];
            String[] d = s.split(" ");
            String[] g = d[0].split(",");
            int x = Integer.parseInt(g[0]);
            int y = Integer.parseInt(g[1]);
            int z = Integer.parseInt(g[2]);
            Material mat = Material.getMaterial(d[1]);
            byte b = Byte.parseByte(d[2]);
            block.getLocation().add((double)x, (double)y, (double)z).getBlock().setType(mat);
            block.getLocation().add((double)x, (double)y, (double)z).getBlock().setData(b);
        }

    }

    public static LB place(LBType type, Block block, Object placedBy, String drop, int luck, ItemStack item, boolean r, BlockFace face, PlaceLuckyBlock.LBOption... options) {
        if (!LB.canSaveMore()) {
            block.setType(Material.AIR);
            return null;
        } else if (type.disabled && placedBy != null && placedBy instanceof Player) {
            send((Player)placedBy, "event.placelb.error.disabled");
            return null;
        } else {
            if (type.hasAdditionalBlocks()) {
                if (!checkForBlocks(type, block)) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player)placedBy, "event.placelb.error.space");
                    }

                    block.setType(Material.AIR);
                    return null;
                }

                placeBlocks(block, type);
            }

            Location location = block.getLocation();
            String[] particleData;
            float pit;
            if (type.allowplaceparticles && type.placeparticles != null && (options == null || options.length == 0 || !hasOption(options, PlaceLuckyBlock.LBOption.NO_EFFECTS))) {
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
                block.getWorld().spawnParticle(MyTasks.getParticle(particleData[0]), location.add(lx, ly, lz), amount, (double)pit, (double)ry, (double)rz, (double)speed);
            }

            String[] s;
            if (type.allowplacesound && type.placesound != null && (options == null || options.length == 0 || !hasOption(options, PlaceLuckyBlock.LBOption.NO_SOUNDS))) {
                Sound sound = null;
                float vol = 100.0F;
                pit = 1.0F;
                s = type.placesound.split(" ");

                try {
                    sound = Sound.valueOf(s[0].toUpperCase());
                } catch (Exception var24) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player)placedBy, "invalid_sound");
                    }
                }

                try {
                    vol = Float.parseFloat(s[1]);
                    pit = Float.parseFloat(s[2]);
                } catch (NumberFormatException var23) {
                    if (placedBy != null && placedBy instanceof Player) {
                        send((Player)placedBy, "invalid_number");
                    }
                }

                BreakLuckyBlock.playFixedSound(block.getLocation(), sound, vol, pit);
            }

            if (!LBType.getFolder(type).exists() && placedBy != null && placedBy instanceof Player) {
                send((Player)placedBy, "invalid_file");
                return null;
            } else {
                LB lb = new LB(type, block, luck, placedBy, true, r);
                if (face != null) {
                    lb.facing = face;
                }

                if (drop != null) {
                    particleData = drop.split(":");
                    if (particleData[0].equalsIgnoreCase("LBDrop")) {
                        if (LBDrop.isValid(particleData[1])) {
                            lb.setDrop(LBDrop.getByName(particleData[1]), true, true);
                        }
                    } else if (particleData[0].equalsIgnoreCase("CustomDrop") && CustomDropManager.isValid(particleData[1])) {
                        lb.customDrop = CustomDropManager.getByName(particleData[1]);
                        lb.refreshCustomDrop();
                    }
                }

                lb.playEffects();
                String o = null;
                UUID owner = null;
                if (item != null && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0) {
                    for(int xx = 0; xx < item.getItemMeta().getLore().size(); ++xx) {
                        if (((String)item.getItemMeta().getLore().get(xx)).startsWith(gray + "Protected:")) {
                            o = (String)item.getItemMeta().getLore().get(xx);
                        } else if (((String)item.getItemMeta().getLore().get(xx)).startsWith(gray + "Owner:")) {
                            String[] d = ((String)item.getItemMeta().getLore().get(xx)).split("Owner: ");
                            owner = UUID.fromString(d[1]);
                        }
                    }
                }

                if (type.getProperties().size() > 0 && type.getProperties().contains(BlockProperty.EXPLOSION_RESISTANCE)) {
                    LuckyBlock.instance.Loops(lb);
                }

                if (o != null) {
                    s = o.split("Protected: ");
                    if (s.length > 1 && s[1].equalsIgnoreCase(green + "true")) {
                        if (placedBy != null && placedBy instanceof Player) {
                            lb.owner = ((Player)placedBy).getUniqueId();
                        }
                    } else if (owner != null) {
                        lb.owner = owner;
                    }
                } else {
                    lb.owner = owner;
                }

                if (LuckyBlock.isDebugEnabled()) {
                    Debug("Lucky block placed", new DebugData[]{new DebugData("Location", locToString(block.getLocation())), new DebugData("LBType", lb.getType().getId() + ", " + ChatColor.stripColor(lb.getType().getName())), new DebugData("Placed By", lb.getPlacedByClass()), new DebugData("Title", lb.hasDropOption("Title") ? ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', lb.getDropOption("Title").getValues()[0].toString())) : "unknown"), new DebugData("Drop Type", lb.customDrop != null ? lb.customDrop.getName() : lb.getDrop().name()), new DebugData("Luck", String.valueOf(lb.getLuck())), new DebugData("Owner", lb.hasOwner() ? lb.owner.toString() : "none")});
                }

                lb.save(true);
                return lb;
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
                    Detector d = new Detector(LuckyBlock.randoms.nextInt(99999) + 1);
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
                    block.getRelative(BlockFace.UP).setData((byte)1);
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
        String dim = LB.blockToString(event.getBlock());
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.getInventory().getItemInMainHand() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.WOOD_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.GOLD_SWORD || item.getType() == Material.DIAMOND_SWORD) {
                return;
            }
        }

        Detector d = null;
        Iterator var6 = LuckyBlockAPI.detectors.iterator();

        while(var6.hasNext()) {
            Detector detector = (Detector)var6.next();
            String[] var10;
            int var9 = (var10 = detector.getBlocks()).length;

            for(int var8 = 0; var8 < var9; ++var8) {
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

    protected static boolean isEnoughSpace(Location loc, int[] requiredSpace) {
        Block bl = loc.getBlock();

        for(int x = requiredSpace[0] * -1; x < requiredSpace[0] + 1; ++x) {
            for(int y = 0; y < requiredSpace[1] + 1; ++y) {
                for(int z = requiredSpace[2] * -1; z < requiredSpace[2] + 1; ++z) {
                    if (bl.getLocation().add((double)x, (double)y, (double)z).getBlock().getType() != Material.AIR && bl.getLocation().add((double)x, (double)y, (double)z).getBlock().getType() != loc.getBlock().getType()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static String dropToString(String drop) {
        if (LBDrop.isValid(drop)) {
            return "LBDrop:" + LBDrop.getByName(drop);
        } else {
            return CustomDropManager.getByName(drop) != null ? "CustomDrop:" + CustomDropManager.getByName(drop).getName() : null;
        }
    }

    public static enum LBOption {
        NO_EFFECTS,
        NO_SOUNDS;

        private LBOption() {
        }
    }
}
