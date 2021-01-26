package com.LuckyBlock.customentity.nametag;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.Resources.Trophy;
import com.LuckyBlock.logic.MyTasks;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.core.entity.CustomEntity;
import org.core.entity.Immunity;
import org.core.logic.ITask;

public class EntityTrophyNameTag extends CustomEntity {
    private Trophy t;

    public EntityTrophyNameTag() {
    }

    public void spawn(Trophy t) {
        this.t = t;
        Location l = t.getBlock().getLocation();
        l.add(0.5D, 0.7D, 0.5D);
        if (l != null) {
            ArmorStand as = (ArmorStand)t.getBlock().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
            ItemStack item = t.getItemToDrop();
            if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                this.remove();
                return;
            }

            as.setCustomName(t.getItemToDrop().getItemMeta().getDisplayName());
            as.setCustomNameVisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setVisible(false);
            super.spawn_1(as.getLocation(), as);
        }

    }

    public int getTickTime() {
        return 1;
    }

    protected void onTick() {
        if (this.t != null && !this.t.isValid()) {
            this.remove();
        }

    }

    public Trophy getTrophy() {
        return this.t;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Trophy_Block", MyTasks.blockToString(this.t.getBlock()));
    }

    protected void onLoad(final ConfigurationSection c) {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                String b = c.getString("Trophy_Block");
                if (b != null && Trophy.getByBlock(MyTasks.stringToBlock(b)) != null) {
                    EntityTrophyNameTag.this.t = Trophy.getByBlock(MyTasks.stringToBlock(b));
                }

            }
        }, 15L));
    }

    public Immunity[] getImmuneTo() {
        return Immunity.values();
    }
}

