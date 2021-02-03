package com.mcgamer199.luckyblock.customentity.lct;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.customentity.Immunity;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class EntityLCTNameTag extends CustomEntity {
    private LuckyCraftingTable lct;

    public EntityLCTNameTag() {
    }

    public void spawn(LuckyCraftingTable lct) {
        this.lct = lct;
        Location l = lct.getBlock().getLocation();
        l.add(0.5D, 1.2D, 0.5D);
        if (l != null) {
            ArmorStand as = (ArmorStand) lct.getBlock().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
            as.setCustomName(ChatColor.GREEN + MyTasks.val("lct.display_name", false));
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
        if (this.lct != null && !this.lct.isValid()) {
            this.remove();
        }

    }

    public LuckyCraftingTable getLCT() {
        return this.lct;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("LCT_Block", LocationUtils.asString(this.lct.getBlock().getLocation()));
    }

    protected void onLoad(final ConfigurationSection c) {
        Scheduler.later(() -> {
            String b = c.getString("LCT_Block");
            if (b != null) {
                LuckyCraftingTable table = LuckyCraftingTable.getByBlock(LocationUtils.blockFromString(b));
                if(table != null) {
                    this.lct = table;
                    this.getEntity().setCustomName(ChatColor.GREEN + MyTasks.val("lct.display_name", false));
                }
            } else {
                EntityLCTNameTag.this.remove();
            }
        }, 15);
    }

    public Immunity[] getImmuneTo() {
        return Immunity.values();
    }
}
