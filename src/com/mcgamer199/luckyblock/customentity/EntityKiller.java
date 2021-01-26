package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.entity.CustomEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityKiller extends CustomEntity {
    public static List<Item> items = new ArrayList();

    public EntityKiller() {
    }

    public ItemStack[] getDrops() {
        return new ItemStack[]{new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND_SPADE)};
    }

    protected int[] getPercents() {
        return new int[]{20, 20, 20};
    }

    private void remove(final Item i, int time) {
        LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                EntityKiller.items.remove(i);
                i.remove();
            }
        }, (long)time);
    }

    protected void onDamage(EntityDamageEvent event) {
        int x;
        Item d;
        if (event.getDamage() < 100.0D) {
            for(x = (int)((double)LuckyBlock.randoms.nextInt(5) + event.getDamage()); x > 0; --x) {
                d = this.entity.getWorld().dropItem(this.entity.getLocation(), new ItemStack(Material.WOOL, 1, (short)14));
                items.add(d);
                d.setPickupDelay(2000);
                this.remove(d, (LuckyBlock.randoms.nextInt(20) + 6) * 3);
            }
        } else {
            for(x = LuckyBlock.randoms.nextInt(5) + 100; x > 0; --x) {
                d = this.entity.getWorld().dropItem(this.entity.getLocation(), new ItemStack(Material.WOOL, 1, (short)14));
                items.add(d);
                d.setPickupDelay(2000);
                this.remove(d, (LuckyBlock.randoms.nextInt(20) + 6) * 2);
            }
        }

    }

    public String getSpawnEggEntity() {
        return "Endermite";
    }
}
