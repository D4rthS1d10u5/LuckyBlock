//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mcgamer199.luckyblock.yottaevents;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.events.LBBreakEvent;
import com.mcgamer199.luckyblock.events.LBPlaceEvent;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.util.EntityUtils;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class YottaEvents implements Listener {
    public YottaEvents() {
    }

    @EventHandler(
            ignoreCancelled = true
    )
    public void checkVeryFast(LBBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("lb.break.cooldown.bypass")) {
            Long last = EntityUtils.getOrSetMeta(player, "last.open", -1L);
            long current = System.currentTimeMillis();
            if (current - last < 2000L) {
                player.sendMessage("§cНельзя так часто открывать лаки блоки.");
                event.setCancelled(true);
            } else {
                EntityUtils.set(player, "last.open", current);
            }

        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void checkRemoveFromInventory(LBPlaceEvent event) {
        ItemStack mainBefore = event.getPlayer().getInventory().getItemInMainHand();
        if (!ItemStackUtils.isNullOrAir(mainBefore) && LBType.isLB(mainBefore) && event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            Bukkit.getScheduler().runTask(LuckyBlockPlugin.instance, () -> {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                if (!ItemStackUtils.isNullOrAir(item) && LBType.isLB(item)) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        item = null;
                    }

                    event.getPlayer().getInventory().setItemInMainHand(item);
                }

            });
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void savePlace(LBPlaceEvent event) {
        ItemStack mainBefore = event.getPlayer().getInventory().getItemInMainHand();
        if (!ItemStackUtils.isNullOrAir(mainBefore) && LBType.isLB(mainBefore)) {
            LuckyDB.BlockData blockData = LuckyDB.getData(mainBefore);
            if (blockData != null) {
                blockData.setPlace(Math.min(blockData.getPlace() + 1, blockData.getAmount()));
                LuckyDB.getDB().put(blockData.getUuid(), blockData);
                LuckyDB.setToSave(true);
            }
        }
    }

    @EventHandler(
            ignoreCancelled = true
    )
    public void checkDupe(LBPlaceEvent event) {
        if (LuckyDB.fixDupe != null && !LuckyDB.fixDupe) {
            return;
        }
        try {
            ItemStack mainBefore = event.getPlayer().getInventory().getItemInMainHand();
            if (!ItemStackUtils.isNullOrAir(mainBefore) && LBType.isLB(mainBefore)) {
                LuckyDB.BlockData blockData = LuckyDB.getData(mainBefore);
                if (blockData == null) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cЭтот лаки блок уже нельзя октрыть, его время ушло :(");
                    event.getPlayer().getInventory().setItemInMainHand(null);
                } else if (blockData.getPlace() >= blockData.getAmount()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cЭтот лаки блок - дюпнутый, такой ставить нельзя :(");
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
            }
        } catch (Exception e) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§4Произошла ошибка, обратитесь к администратору.");
            e.printStackTrace();
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void FramePlace(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME) && event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            ItemFrame rightClicked = (ItemFrame) event.getRightClicked();
            if (ItemStackUtils.isNullOrAir(rightClicked.getItem())) {
                ItemStack mainBefore = null;
                if (event.getHand().equals(EquipmentSlot.HAND)) {
                    mainBefore = event.getPlayer().getInventory().getItemInMainHand();
                } else if (event.getHand().equals(EquipmentSlot.HAND)) {
                    mainBefore = event.getPlayer().getInventory().getItemInOffHand();
                }

                if (!ItemStackUtils.isNullOrAir(mainBefore) && LBType.isLB(mainBefore)) {
                    Bukkit.getScheduler().runTask(LuckyBlockPlugin.instance, () -> {
                        ItemStack item = null;
                        if (event.getHand().equals(EquipmentSlot.HAND)) {
                            item = event.getPlayer().getInventory().getItemInMainHand();
                        } else if (event.getHand().equals(EquipmentSlot.HAND)) {
                            item = event.getPlayer().getInventory().getItemInOffHand();
                        }

                        if (!ItemStackUtils.isNullOrAir(item) && LBType.isLB(item)) {
                            if (item.getAmount() > 1) {
                                item.setAmount(item.getAmount() - 1);
                            } else {
                                item = null;
                            }

                            event.getPlayer().getInventory().setItemInMainHand(item);
                        }

                    });
                }
            }
        }

    }
}
