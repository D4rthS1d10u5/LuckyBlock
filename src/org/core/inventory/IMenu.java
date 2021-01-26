package org.core.inventory;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class IMenu implements Listener {
    private Inventory inv;
    private IMenu.ClickEvent click;

    public IMenu(Inventory inv, IMenu.ClickEvent click) {
        this.inv = inv;
        this.click = click;
        Bukkit.getPluginManager().registerEvents(this, LuckyBlock.instance);
    }

    @EventHandler
    private void onClickItem(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase(this.inv.getTitle()) && event.getWhoClicked() instanceof Player) {
            this.click.onClick((Player)event.getWhoClicked(), event);
        }

    }

    public interface ClickEvent {
        void onClick(Player var1, InventoryClickEvent var2);
    }
}
