package com.mcgamer199.luckyblock.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.api.Properties;
import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.api.enums.BlockProperty;
import com.mcgamer199.luckyblock.api.enums.ItemProperty;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.events.LBInteractEvent;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.api.ColorsClass;
import com.mcgamer199.luckyblock.resources.Detector;
import com.mcgamer199.luckyblock.resources.LBItem;
import com.mcgamer199.luckyblock.structures.BossDungeon;
import com.mcgamer199.luckyblock.tags.ChestFiller;
import com.mcgamer199.luckyblock.util.EffectUtils;
import com.mcgamer199.luckyblock.util.EntityUtils;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class LuckyBlockEvents extends ColorsClass implements Listener {

    public LuckyBlockEvents() {}

    @EventHandler
    public void onPlaceRandomChest(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.CHEST && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("" + green + bold + italic + "Random Chest")) {
                Chest chest = (Chest) block.getState();
                ChestFiller c = new ChestFiller(LBType.getFile(LBType.getTypes().get(0), 0).getConfigurationSection("Chests"), chest);
                c.fill();
            }
        }
    }

    @EventHandler
    public void onPlaceExplodingBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.STAINED_CLAY && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(darkred + "Exploding Block")) {
            Block block = event.getBlock();
            this.BombBlock(block, RandomUtils.nextInt(100) + 200);
        }
    }

    @EventHandler
    public void onUseLuckyTool(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (player.getInventory().getItemInMainHand().getType() == Material.CARROT_STICK && this.isMainHand(event) && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null) {
                LuckyBlock luckyBlock;
                int luck;
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Lucky Block Tool")) {
                    if (LuckyBlock.isLuckyBlock(block)) {
                        luckyBlock = LuckyBlock.getByBlock(block);
                        luck = luckyBlock.getLuck();
                        player.sendMessage(blue + "Lucky Block : " + green + "true");
                        player.sendMessage(luckyBlock.getType().getLuckString(luck));
                    } else {
                        player.sendMessage(blue + "Lucky Block : " + red + "false");
                    }
                } else if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Advanced Lucky Block Tool")) {
                    if (LuckyBlock.isLuckyBlock(block)) {
                        luckyBlock = LuckyBlock.getByBlock(block);
                        luck = luckyBlock.getLuck();
                        player.sendMessage(blue + "Lucky Block : " + green + "true");
                        player.sendMessage(luckyBlock.getType().getLuckString(luck));
                        player.sendMessage(yellow + val("command.lbs.data.placedby") + ": " + gold + luckyBlock.getPlacementInfo());

                        if ((luckyBlock.getLuckyBlockDrop() != null || luckyBlock.customDrop != null) && luckyBlock.hasDropOption("Title")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOptions().getString("Title", "&cnull")));
                        }

                        if (!luckyBlock.getDropOptions().isEmpty()) {
                            StringJoiner newLineJoiner = new StringJoiner("\n");
                            Properties properties = luckyBlock.getDropOptions();

                            for (String key : properties.keys()) {
                                if(!LuckyBlock.isHiddenOption(key)) {
                                    newLineJoiner.add(properties.toString(properties.getJsonParams().get(key)));
                                }
                            }

                            new ChatComponent().addText("§a§lDrop Options", Hover.show_text, newLineJoiner.toString()).send(player);
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

                    while (var6.hasNext()) {
                        Entity e = (Entity) var6.next();
                        if (e instanceof LivingEntity) {
                            uuids.add(e.getUniqueId());
                        }
                    }

                    if (uuids.size() > 0) {
                        int thing = RandomUtils.nextInt(uuids.size());
                        Iterator var7 = player.getNearbyEntities(20.0D, 20.0D, 20.0D).iterator();

                        while (var7.hasNext()) {
                            Entity e = (Entity) var7.next();
                            if (e.getUniqueId() == uuids.get(thing)) {
                                Location loc = e.getLocation();
                                Location loca = player.getLocation();
                                player.teleport(loc);
                                e.teleport(loca);
                                if (player.getGameMode() != GameMode.CREATIVE) {
                                    if (item.getAmount() > 1) {
                                        item.setAmount(item.getAmount() - 1);
                                    } else {
                                        player.getInventory().removeItem(item);
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
        for (int x = 0; x < event.blockList().size(); ++x) {
            if (LuckyBlock.getByBlock(event.blockList().get(x)) != null && LuckyBlock.getByBlock(event.blockList().get(x)).getType().getProperties().contains(BlockProperty.EXPLOSION_RESISTANCE)) {
                event.blockList().get(x).getWorld().spawnParticle(Particle.CRIT_MAGIC, event.blockList().get(x).getLocation(), 150, 0.5D, 0.5D, 0.5D, 0.0D);
                event.blockList().remove(x);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType() != Material.AIR) {
            ItemStack item = event.getItem();
            if (item.hasItemMeta() && item.getItemMeta().hasEnchant(LuckyBlockPlugin.enchantment_lightning)) {
                Player player = event.getPlayer();
                if (player.getTargetBlock(null, 200) != null && player.getTargetBlock(null, 100).getType() != Material.AIR) {
                    int level = item.getEnchantmentLevel(LuckyBlockPlugin.enchantment_lightning);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        if (level < 2) {
                            player.getInventory().getItemInMainHand().removeEnchantment(LuckyBlockPlugin.enchantment_lightning);
                        } else {
                            player.getInventory().getItemInMainHand().removeEnchantment(LuckyBlockPlugin.enchantment_lightning);
                            player.getInventory().getItemInMainHand().addEnchantment(LuckyBlockPlugin.enchantment_lightning, level - 1);
                            player.updateInventory();
                        }

                        player.sendMessage(val("event.item.thoraxe.1") + ": " + gold + (level - 1));
                    }

                    player.getWorld().strikeLightning(player.getTargetBlock(null, 200).getLocation());
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

                for (Detector det : LuckyBlockAPI.detectors) {
                    String[] var10;
                    int var9 = (var10 = det.getBlocks()).length;

                    for (int var8 = 0; var8 < var9; ++var8) {
                        String s = var10[var8];
                        if (s != null && LocationUtils.asString(LocationUtils.blockFromString(s).getLocation()).equalsIgnoreCase(LocationUtils.asString(block.getLocation()))) {
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
                    detector.getRange().length();
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
        if (BossDungeon.baseBlocks.contains(LocationUtils.asString(block.getLocation()))) {
            BossDungeon.baseBlocks.remove(LocationUtils.asString(block.getLocation()));

            for (int i = -40; i < 1; ++i) {
                for (int x = -6; x < 7; ++x) {
                    for (int z = -6; z < 7; ++z) {
                        block.getLocation().add(x, i, z).getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onRightClickMap(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            ItemStack item = event.getItem();
            if (item.getType() == Material.MAP && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("" + green + italic + "Treasure Map") && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0 && item.getItemMeta().getLore().get(0).startsWith(blue + "Location:")) {
                String[] d = ChatColor.stripColor(item.getItemMeta().getLore().get(0)).split("Location: ");
                String[] d1 = d[1].split(",");
                Player player = event.getPlayer();
                if (LuckyBlockWorld.equals(player.getWorld().getGenerator())) {
                    int x = Integer.parseInt(d1[0]);
                    int y = Integer.parseInt(d1[1]) + 3;
                    int z = Integer.parseInt(d1[2]);
                    Location loc = new Location(player.getWorld(), x, y, z);
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
        LuckyBlock luckyBlock;
        for (x = 0; x < effected.size(); ++x) {
            block = effected.get(x);
            if (LuckyBlock.isLuckyBlock(block)) {
                luckyBlock = LuckyBlock.getByBlock(block);
                if (!luckyBlock.getType().getProperties().contains(BlockProperty.EXPLOSION_RESISTANCE)) {
                    event.blockList().remove(block);
                    blks.add(block);
                }
            }
        }

        for (x = 0; x < blks.size(); ++x) {
            block = blks.get(x);
            if (LuckyBlock.isLuckyBlock(block)) {
                luckyBlock = LuckyBlock.getByBlock(block);
                luckyBlock.remove(false, false);
                block.setType(Material.AIR);
                if (luckyBlock.getType().hasProperty(BlockProperty.DROP_ON_EXPLODE)) {
                    this.spawnItem(luckyBlock);
                }
            }
        }
    }

    private void spawnItem(LuckyBlock luckyBlock) {
        luckyBlock.getBlock().getWorld().dropItem(luckyBlock.getLocation(), luckyBlock.getType().toItemStack(luckyBlock.getLuck()));
    }

    public void BombBlock(final Block block, int i) {
        LuckyBlockPlugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (block.getType() == Material.STAINED_CLAY) {
                    int x = block.getLocation().getBlockX();
                    int y = block.getLocation().getBlockY();
                    int z = block.getLocation().getBlockZ();

                    try {
                        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionFire");
                        boolean damage = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionDamage");
                        if (damage) {
                            block.getWorld().createExplosion(x, y, z, 4.0F, setFire, breakBlocks);
                        } else {
                            block.getWorld().createExplosion(x, y, z, 0.0F, setFire, breakBlocks);
                        }
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

            }
        }, i);
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
                if (type.hasItemProperty(ItemProperty.SHOW_ITEM_DROPPED_NAME)) {
                    item.setCustomName(type.getName());
                    item.setCustomNameVisible(true);
                }
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    private void onTakeLuckyBlock(LBInteractEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR && compareItems(item, LBItem.LB_REMOVER.getItem())) {
            event.getLB().remove(true, false);
        }
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType().equals(EntityType.FALLING_BLOCK)) {
            FallingBlock entity = (FallingBlock) event.getEntity();
            if(EntityUtils.getOrDefault(entity, "block_rain", false)) {
                MaterialData d = new MaterialData(entity.getMaterial(), entity.getBlockData());
                entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, d);
                EffectUtils.playFixedSound(entity.getLocation(), EffectUtils.getSound("lb_drop_blockrain_land"), 1.0F, 1.0F, 60);
            } else if(EntityUtils.getOrDefault(entity, "knight_bomb", false)) {
                if(entity.getLocation().getBlock().getType().equals(Material.COAL_BLOCK)) {
                    entity.getLocation().getBlock().setType(Material.AIR);
                }

                boolean angry = EntityUtils.getOrDefault(entity, "angry", false);
                entity.getWorld().createExplosion(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), angry ? 12.0F : 7F, false, false);
            } else if(EntityUtils.getOrDefault(entity, "luckyBlockRain", false)) {
                Material material = EntityUtils.getOrDefault(entity, "lbType", null);
                byte data = EntityUtils.getOrDefault(entity, "lbData", (byte) -1);
                if(material != null && data != -1) {
                    new LuckyBlock(LBType.fromMaterialAndData(material, data), entity.getLocation().getBlock(), 0, null, true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(EntityUtils.getOrDefault(event.getPlayer(), "stuck", false)) {
            event.setCancelled(true);
        }
    }
}
