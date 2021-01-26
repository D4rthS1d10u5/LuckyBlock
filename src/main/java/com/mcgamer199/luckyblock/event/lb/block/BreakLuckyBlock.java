//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.event.lb.block;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.events.LBBreakEvent;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.world.Structures.LuckyWell;
import com.mcgamer199.luckyblock.customentity.nametag.EntityFloatingText;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.yottaevents.LuckyDB;
import com.mcgamer199.luckyblock.yottaevents.YottaEvents;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.logic.SchedulerTask;

import java.util.Iterator;
import java.util.UUID;

public class BreakLuckyBlock extends ColorsClass implements Listener {
    public BreakLuckyBlock() {
        this.injectYottaEvents();
    }

    private void injectYottaEvents() {
        Bukkit.getPluginManager().registerEvents(new YottaEvents(), LuckyBlock.instance);
    }

    @EventHandler(
            ignoreCancelled = true
    )
    public void LuckyBlockEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        Block block = event.getBlock();
        LB l = LB.getByABlock(block);
        if (l != null) {
            openLB(l, player);
        } else {
            if (LB.isLuckyBlock(block)) {
                LBType t = LB.getFromBlock(block).getType();
                if (t.getPermission("Breaking") != null && !player.hasPermission(t.getPermission("Breaking")) && !player.getName().equalsIgnoreCase(pn)) {
                    send(player, "event.breaklb.error.permission");
                    event.setCancelled(true);
                    return;
                }
            }

            LB lb = null;
            Iterator var7 = LBType.getTypes().iterator();

            Material type = block.getType();
            while(var7.hasNext()) {
                LBType t = (LBType)var7.next();
                boolean g = false;
                if (t.getWorlds().contains("*All*")) {
                    g = true;
                }

                for(int x = 0; x < t.getWorlds().size(); ++x) {
                    if (((String)t.getWorlds().get(x)).equalsIgnoreCase(player.getWorld().getName())) {
                        g = true;
                    }
                }

                if (!g) {
                    return;
                }

                if (!t.creativeMode && player.getGameMode() == GameMode.CREATIVE) {
                    return;
                }

                if (t.defaultBlock) {
                    if (type == t.getType()) {
                        if (!LB.isLuckyBlock(block)) {
                            lb = new LB((LBType)LBType.getTypes().get(0), block, 0, player, false, true);
                        } else {
                            lb = LB.getFromBlock(block);
                        }
                    }
                } else if (LB.isLuckyBlock(block)) {
                    lb = LB.getFromBlock(block);
                }
            }

            if (lb == null && !LuckyDB.fixDupe) {
                LBType findType = LBType.getTypes().stream()
                        .filter(lbType -> lbType.getBlockType() == type)
                        .findFirst().orElse(null);
                if (findType != null) {
                    lb = new LB();
                    int luck = ((Number) LuckyBlock.instance.getConfig().get("default-luck", 0)).intValue();
                    lb.init(findType, block, luck, event.getPlayer(), false, true);
                }
            }

            if (lb != null) {
                if (lb.owner != null) {
                    UUID uuid = player.getUniqueId();
                    String u = uuid.toString();
                    String u1 = lb.owner.toString();
                    u = u.replace("-", "");
                    u1 = u1.replace("-", "");
                    if (!u.equalsIgnoreCase(u1) && !player.hasPermission("lb.breakall")) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (type == Material.PORTAL) {
                    return;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                event.setCancelled(true);
                if (!lb.getType().canBreak(item)) {
                    return;
                }

                LBBreakEvent le = new LBBreakEvent(lb, player);
                Bukkit.getPluginManager().callEvent(le);
                if (!le.isCancelled()) {
                    if (lb.getType().getDelay() > 0) {
                        SchedulerTask task = new SchedulerTask();
                        LB finalLb = lb;
                        task.setId(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
                            public void run() {
                                BreakLuckyBlock.openLB(finalLb, player);
                            }
                        }, (long)lb.getType().getDelay()));
                    } else {
                        openLB(lb, player);
                    }
                }
            }
        }

    }

    public static void openLB(LB lb, Player player) {
        if (lb != null) {
            Block block = lb.getBlock();
            LBType types = lb.getType();
            Location bloc = block.getLocation();
            if (player != null) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH) && player.getGameMode() != GameMode.CREATIVE) {
                    if (lb.getType().getPermission("SilkTouch") == null) {
                        HTasks.spawnLB(lb, bloc);
                        lb.remove(true);
                        return;
                    }

                    if (player.hasPermission(lb.getType().getPermission("SilkTouch"))) {
                        HTasks.spawnLB(lb, bloc);
                        lb.remove(true);
                        return;
                    }
                }

                if (player.getGameMode() == GameMode.CREATIVE && !types.creativeMode) {
                    return;
                }
            }

            block.setType(Material.AIR);
            if (types.allowbreaksound && types.breaksound != null) {
                Sound sound = null;
                float vol = 100.0F;
                float pit = 1.0F;
                String[] s = types.breaksound.split(" ");

                try {
                    sound = Sound.valueOf(s[0].toUpperCase());
                } catch (Exception var11) {
                }

                try {
                    vol = Float.parseFloat(s[1]);
                    pit = Float.parseFloat(s[2]);
                } catch (NumberFormatException var10) {
                }

                playFixedSound(bloc, sound, vol, pit);
            }

            lb.remove();
            PortalEvents.removePortal(block.getRelative(BlockFace.UP));
            PortalEvents.removePortal(block.getRelative(BlockFace.EAST));
            PortalEvents.removePortal(block.getRelative(BlockFace.DOWN));
            PortalEvents.removePortal(block.getRelative(BlockFace.WEST));
            PortalEvents.removePortal(block.getRelative(BlockFace.SOUTH));
            PortalEvents.removePortal(block.getRelative(BlockFace.NORTH));
            if (types.allowbreakparticles && types.breakparticles != null) {
                spawnParticle(bloc, types.breakparticles);
            }

            DropEvents.run(block, lb, player, lb.getDrop(), lb.customDrop, true);
        }
    }

    @EventHandler
    public void block(BlockRedstoneEvent event) {
        if (event.getBlock().getType() == Material.IRON_PLATE && LuckyWell.isValid(event.getBlock())) {
            LuckyWell.blocks.remove(ColorsClass.blockToString(event.getBlock()));
            Entity l = null;
            Iterator var4 = event.getBlock().getLocation().getWorld().getNearbyEntities(event.getBlock().getLocation(), 1.0D, 1.0D, 1.0D).iterator();

            while(var4.hasNext()) {
                Entity e = (Entity)var4.next();
                if (e instanceof Item) {
                    Item item = (Item)e;
                    if (item.getItemStack() != null) {
                        ItemStack i = item.getItemStack();
                        if (i.getType() == Material.GOLD_NUGGET && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equalsIgnoreCase("" + gold + bold + "Coin")) {
                            l = e;
                        }
                    }
                }
            }

            if (l != null) {
                int r = random.nextInt(2) + 1;
                Iterator var12 = l.getNearbyEntities(7.0D, 7.0D, 7.0D).iterator();

                while(var12.hasNext()) {
                    Entity p = (Entity)var12.next();
                    if (p instanceof Player) {
                        Player pl = (Player)p;
                        if (r == 1) {
                            send_no(pl, "drops.well.lucky");
                            pl.playSound(pl.getLocation(), getSound("lb_stwell_lucky"), 1.0F, 1.0F);
                        } else {
                            send_no(pl, "drops.well.unlucky");
                            pl.playSound(pl.getLocation(), getSound("lb_stwell_unlucky"), 1.0F, 1.0F);
                        }
                    }
                }

                l.remove();
                Location loc = event.getBlock().getLocation();
                if (r == 1) {
                    Location o = new Location(loc.getWorld(), loc.getX() + 2.0D, loc.getY() + 2.0D, loc.getZ() + 2.0D);
                    o.getWorld().dropItem(o, new ItemStack(Material.DIAMOND, random.nextInt(10) + 4));
                    o.getWorld().dropItem(o, new ItemStack(Material.EMERALD, random.nextInt(8) + 3));
                    o.getWorld().dropItem(o, new ItemStack(Material.IRON_INGOT, random.nextInt(15) + 6));
                    o.getWorld().dropItem(o, new ItemStack(Material.GOLD_INGOT, random.nextInt(12) + 5));
                } else {
                    TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(event.getBlock().getLocation().add(2.0D, 6.0D, 2.0D), EntityType.PRIMED_TNT);
                    TNTPrimed tnt1 = (TNTPrimed)loc.getWorld().spawnEntity(event.getBlock().getLocation().add(-2.0D, 6.0D, 2.0D), EntityType.PRIMED_TNT);
                    TNTPrimed tnt2 = (TNTPrimed)loc.getWorld().spawnEntity(event.getBlock().getLocation().add(2.0D, 6.0D, -2.0D), EntityType.PRIMED_TNT);
                    TNTPrimed tnt3 = (TNTPrimed)loc.getWorld().spawnEntity(event.getBlock().getLocation().add(-2.0D, 6.0D, -2.0D), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(60);
                    tnt1.setFuseTicks(60);
                    tnt2.setFuseTicks(60);
                    tnt3.setFuseTicks(60);
                }
            }
        }

    }

    public static void spawnParticle(Location loc, String particle) {
        try {
            String[] part = particle.split(" ");
            float rx = Float.parseFloat(part[1]);
            float ry = Float.parseFloat(part[2]);
            float rz = Float.parseFloat(part[3]);
            float speed = Float.parseFloat(part[4]);
            int amount = Integer.parseInt(part[5]);
            double lx = Double.parseDouble(part[6]);
            double ly = Double.parseDouble(part[7]);
            double lz = Double.parseDouble(part[8]);
            loc.getWorld().spawnParticle(MyTasks.getParticle(part[0]), loc.add(lx, ly, lz), amount, (double)rx, (double)ry, (double)rz, (double)speed);
        } catch (Exception var14) {
            throw new Error("An unexpected error occured while trying to spawn particles!");
        }
    }

    @EventHandler
    private void onRightClickLB(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && LB.isLuckyBlock(event.getClickedBlock())) {
            LB lb = LB.getFromBlock(event.getClickedBlock());
            if (lb.getType().rightClick) {
                event.setCancelled(true);
                openLB(lb, event.getPlayer());
            }
        }

    }

    public static void changeLBLuck(LB lb, int level) {
        int luck = lb.getLuck();
        int max = lb.getType().getMaxLuck();
        int a = max / 2 / 3;
        int total = level * a;
        total += random.nextInt(14) + 3;
        luck += total;
        if (luck > max) {
            luck = max;
        }

        EntityFloatingText f = new EntityFloatingText();
        f.age = 20;
        f.mode = 1;
        f.b = 100;
        f.text = ChatColor.BLUE + "+" + total + "%";
        f.spawn(lb.getBlock().getLocation().add(0.5D, 1.0D, 0.5D));
        LuckyBlockAPI.spawnLuckyBlockItem(lb, luck, lb.getBlock().getLocation());
    }
}
