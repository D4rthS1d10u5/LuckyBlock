package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

//TODO сделать хранилище инвентарей вместо регистраци слушателя на КАЖДЫЙ инвентарь
public class IMenu implements Listener {
    private final Inventory inv;
    private final IMenu.ClickEvent click;

    public IMenu(Inventory inv, IMenu.ClickEvent click) {
        this.inv = inv;
        this.click = click;
        Bukkit.getPluginManager().registerEvents(this, LuckyBlockPlugin.instance);
    }

    @EventHandler
    private void onClickItem(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase(this.inv.getTitle()) && event.getWhoClicked() instanceof Player) {
            this.click.onClick((Player) event.getWhoClicked(), event);
        }

    }

    public interface ClickEvent {
        void onClick(Player var1, InventoryClickEvent var2);
    }
}
