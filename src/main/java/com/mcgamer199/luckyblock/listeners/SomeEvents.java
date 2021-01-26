package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.events.LBInteractEvent;
import com.mcgamer199.luckyblock.lb.DropOption;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.tags.ChestFiller;
import com.mcgamer199.luckyblock.tellraw.TextAction;
import com.mcgamer199.luckyblock.structures.BossDungeon;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SomeEvents extends ColorsClass implements Listener {

    public SomeEvents() {
    }

    @EventHandler
    public void onPlaceRandomChest(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.CHEST && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("" + green + bold + italic + "Random Chest")) {
                Chest chest = (Chest)block.getState();
                ChestFiller c = new ChestFiller(LBType.getFile((LBType)LBType.getTypes().get(0), 0).getConfigurationSection("Chests"), chest);
                c.fill();
            }
        }

    }

    @EventHandler
    public void onPlaceExplodingBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.STAINED_CLAY && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(darkred + "Exploding Block")) {
            Block block = event.getBlock();
            this.BombBlock(block, LuckyBlock.randoms.nextInt(100) + 200);
        }

    }

    @EventHandler
    public void onUseLuckyTool(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (player.getInventory().getItemInMainHand().getType() == Material.CARROT_STICK && this.isMainHand(event) && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null) {
                LB lb;
                int luckyb;
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Lucky Block Tool")) {
                    if (LB.isLuckyBlock(block)) {
                        lb = LB.getFromBlock(block);
                        luckyb = lb.getLuck();
                        player.sendMessage(blue + "Lucky Block : " + green + "true");
                        player.sendMessage(lb.getType().getLuckString(luckyb));
                    } else {
                        player.sendMessage(blue + "Lucky Block : " + red + "false");
                    }
                } else if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Advanced Lucky Block Tool")) {
                    if (LB.isLuckyBlock(block)) {
                        lb = LB.getFromBlock(block);
                        luckyb = lb.getLuck();
                        player.sendMessage(blue + "Lucky Block : " + green + "true");
                        player.sendMessage(lb.getType().getLuckString(luckyb));
                        if (lb.getPlacedByClass() != null) {
                            player.sendMessage(yellow + val("command.lbs.data.placedby") + ": " + gold + lb.getPlacedByClass());
                        }

                        if ((lb.getDrop() != null || lb.customDrop != null) && lb.getDropOption("Title") != null && lb.getDropOption("Title").getValues().length > 0) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', (String)lb.getDropOption("Title").getValues()[0]));
                        }

                        if (lb.getDropOptions().size() > 1) {
                            com.mcgamer199.luckyblock.tellraw.RawText text = new com.mcgamer199.luckyblock.tellraw.RawText("" + green + bold + "Drop Options");
                            String r = "";
                            int g = 0;

                            for(int x = 0; x < lb.getDropOptions().size(); ++x) {
                                DropOption dr = (DropOption)lb.getDropOptions().get(x);
                                if (!LB.isHidden(dr.getName())) {
                                    if (g == 0) {
                                        r = lightpurple + dr.getName() + ": ";
                                    } else {
                                        r = r + lightpurple + "\n" + lightpurple + dr.getName() + ": ";
                                    }

                                    for(int i = 0; i < dr.getValues().length; ++i) {
                                        if (dr.getValues()[i] != null) {
                                            if (i == 0) {
                                                r = r + blue + ChatColor.translateAlternateColorCodes('&', dr.getValues()[i].toString());
                                            } else {
                                                r = r + lightpurple + "," + blue + ChatColor.translateAlternateColorCodes('&', dr.getValues()[i].toString());
                                            }
                                        }
                                    }

                                    ++g;
                                }
                            }

                            if (g > 0) {
                                text.addAction(new TextAction(com.mcgamer199.luckyblock.tellraw.EnumTextEvent.HOVER_EVENT, com.mcgamer199.luckyblock.tellraw.EnumTextAction.SHOW_TEXT, r));
                                com.mcgamer199.luckyblock.tellraw.TellRawSender.sendTo(player, new com.mcgamer199.luckyblock.tellraw.RawText[]{text});
                            }
                        }
                    } else {
                        player.sendMessage(blue + "Lucky Block : " + red + "false");
                    }
                }
            }
        }

    }

    @EventHandler
    public void Switch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.ENDER_PEARL) {
                ItemStack item = event.getItem();
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(blue + "Random Teleport")) {
                    event.setCancelled(true);
                    List<UUID> uuids = new ArrayList();
                    Iterator var6 = player.getNearbyEntities(20.0D, 20.0D, 20.0D).iterator();

                    while(var6.hasNext()) {
                        Entity e = (Entity)var6.next();
                        if (e instanceof LivingEntity) {
                            uuids.add(e.getUniqueId());
                        }
                    }

                    if (uuids.size() > 0) {
                        int thing = LuckyBlock.randoms.nextInt(uuids.size());
                        Iterator var7 = player.getNearbyEntities(20.0D, 20.0D, 20.0D).iterator();

                        while(var7.hasNext()) {
                            Entity e = (Entity)var7.next();
                            if (e.getUniqueId() == uuids.get(thing)) {
                                Location loc = e.getLocation();
                                Location loca = player.getLocation();
                                player.teleport(loc);
                                e.teleport(loca);
                                if (player.getGameMode() != GameMode.CREATIVE) {
                                    if (item.getAmount() > 1) {
                                        item.setAmount(item.getAmount() - 1);
                                    } else {
                                        player.getInventory().removeItem(new ItemStack[]{item});
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @EventHandler
    public void onExplodeSuperLuckyBlock(EntityExplodeEvent event) {
        for(int x = 0; x < event.blockList().size(); ++x) {
            if (LB.getFromBlock((Block)event.blockList().get(x)) != null && LB.getFromBlock((Block)event.blockList().get(x)).getType().getProperties().contains(LBType.BlockProperty.EXPLOSION_RESISTANCE)) {
                ((Block)event.blockList().get(x)).getWorld().spawnParticle(Particle.CRIT_MAGIC, ((Block)event.blockList().get(x)).getLocation(), 150, 0.5D, 0.5D, 0.5D, 0.0D);
                event.blockList().remove(x);
            }
        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType() != Material.AIR) {
            ItemStack item = event.getItem();
            if (item.hasItemMeta() && item.getItemMeta().hasEnchant(LuckyBlock.enchantment_lightning)) {
                Player player = event.getPlayer();
                if (player.getTargetBlock((Set)null, 200) != null && player.getTargetBlock((Set)null, 100).getType() != Material.AIR) {
                    int level = item.getEnchantmentLevel(LuckyBlock.enchantment_lightning);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        if (level < 2) {
                            player.getInventory().getItemInMainHand().removeEnchantment(LuckyBlock.enchantment_lightning);
                        } else {
                            player.getInventory().getItemInMainHand().removeEnchantment(LuckyBlock.enchantment_lightning);
                            player.getInventory().getItemInMainHand().addEnchantment(LuckyBlock.enchantment_lightning, level - 1);
                            player.updateInventory();
                        }

                        player.sendMessage(val("event.item.thoraxe.1") + ": " + gold + (level - 1));
                    }

                    player.getWorld().strikeLightning(player.getTargetBlock((Set)null, 200).getLocation());
                } else {
                    send_no(player, "event.item.thoraxe.2");
                }
            }
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if (block.getType() == Material.GOLD_BLOCK) {
                Detector detector = null;
                Iterator var6 = LuckyBlockAPI.detectors.iterator();

                while(var6.hasNext()) {
                    Detector det = (Detector)var6.next();
                    String[] var10;
                    int var9 = (var10 = det.getBlocks()).length;

                    for(int var8 = 0; var8 < var9; ++var8) {
                        String s = var10[var8];
                        if (s != null && LB.blockToString(stringToBlock(s)).equalsIgnoreCase(LB.blockToString(block))) {
                            detector = det;
                        }
                    }
                }

                if (detector != null) {
                    if (!player.hasPermission("lb.usedetector")) {
                        return;
                    }

                    send_no(player, "event.detector.search");
                    boolean i = false;
                    detector.getRange().getTotal();
                    if (!i) {
                        send_no(player, "event.detector.no_lb");
                    }
                }
            }
        }

    }

    @EventHandler
    private void onBreakBaseBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (BossDungeon.baseBlocks.contains(LB.blockToString(block))) {
            BossDungeon.baseBlocks.remove(LB.blockToString(block));

            for(int i = -40; i < 1; ++i) {
                for(int x = -6; x < 7; ++x) {
                    for(int z = -6; z < 7; ++z) {
                        block.getLocation().add((double)x, (double)i, (double)z).getBlock().setType(Material.AIR);
                    }
                }
            }
        }

    }

    @EventHandler
    private void onRightClickMap(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            ItemStack item = event.getItem();
            if (item.getType() == Material.MAP && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("" + green + italic + "Treasure Map") && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0 && ((String)item.getItemMeta().getLore().get(0)).startsWith(blue + "Location:")) {
                String[] d = ChatColor.stripColor((String)item.getItemMeta().getLore().get(0)).split("Location: ");
                String[] d1 = d[1].split(",");
                Player player = event.getPlayer();
                if (LuckyBlockWorld.equals(player.getWorld().getGenerator())) {
                    int x = Integer.parseInt(d1[0]);
                    int y = Integer.parseInt(d1[1]) + 3;
                    int z = Integer.parseInt(d1[2]);
                    Location loc = new Location(player.getWorld(), (double)x, (double)y, (double)z);
                    if (loc.getBlock().getType() == Material.BEDROCK) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

    }

    @EventHandler
    public void onDMGBlock(EntityExplodeEvent event) {
        List<Block> effected = event.blockList();
        List<Block> blks = new ArrayList();

        int x;
        Block block;
        LB lb;
        for(x = 0; x < effected.size(); ++x) {
            block = (Block)effected.get(x);
            if (LB.isLuckyBlock(block)) {
                lb = LB.getFromBlock(block);
                if (!lb.getType().getProperties().contains(LBType.BlockProperty.EXPLOSION_RESISTANCE)) {
                    event.blockList().remove(block);
                    blks.add(block);
                }
            }
        }

        for(x = 0; x < blks.size(); ++x) {
            block = (Block)blks.get(x);
            if (LB.isLuckyBlock(block)) {
                lb = LB.getFromBlock(block);
                lb.remove();
                block.setType(Material.AIR);
                if (lb.getType().hasProperty(LBType.BlockProperty.DROP_ON_EXPLODE)) {
                    this.spawnItem(lb);
                }
            }
        }

    }

    private void spawnItem(LB lb) {
        lb.getBlock().getWorld().dropItem(lb.getBlock().getLocation(), lb.getType().toItemStack(lb.getLuck()));
    }

    public void BombBlock(final Block block, int i) {
        LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (block.getType() == Material.STAINED_CLAY) {
                    int x = block.getLocation().getBlockX();
                    int y = block.getLocation().getBlockY();
                    int z = block.getLocation().getBlockZ();

                    try {
                        boolean breakBlocks = LuckyBlock.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlock.instance.config.getBoolean("Allow.ExplosionFire");
                        boolean damage = LuckyBlock.instance.config.getBoolean("Allow.ExplosionDamage");
                        if (damage) {
                            block.getWorld().createExplosion((double)x, (double)y, (double)z, 4.0F, setFire, breakBlocks);
                        } else {
                            block.getWorld().createExplosion((double)x, (double)y, (double)z, 0.0F, setFire, breakBlocks);
                        }
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

            }
        }, (long)i);
    }

    @EventHandler
    private void onPlaceBossKey(BlockPlaceEvent event) {
        if (event.getItemInHand() != null) {
            ItemStack i = event.getItemInHand();
            if (i.getType() == Material.TRIPWIRE_HOOK && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Boss Key")) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    private void onDropLuckyBlockAsItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (item != null && item.getItemStack() != null) {
            ItemStack i = item.getItemStack();
            if (LBType.isLB(i)) {
                LBType type = LBType.fromItem(i);
                if (type.hasItemProperty(LBType.ItemProperty.SHOW_ITEM_DROPPED_NAME)) {
                    item.setCustomName(type.getName());
                    item.setCustomNameVisible(true);
                }
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true
    )
    private void onTakeLuckyBlock(LBInteractEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR && compareItems(item, LBItem.LB_REMOVER.getItem())) {
            event.getLB().remove(true);
        }

    }
}
