package com.mcgamer199.luckyblock.customentity.lct;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;
import com.mcgamer199.luckyblock.logic.ITask;

public class EntityLCTNameTag extends CustomEntity {
    private LuckyCraftingTable lct;

    public EntityLCTNameTag() {
    }

    public void spawn(LuckyCraftingTable lct) {
        this.lct = lct;
        Location l = lct.getBlock().getLocation();
        l.add(0.5D, 1.2D, 0.5D);
        if (l != null) {
            ArmorStand as = (ArmorStand)lct.getBlock().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
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
        c.set("LCT_Block", MyTasks.blockToString(this.lct.getBlock()));
    }

    protected void onLoad(final ConfigurationSection c) {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                String b = c.getString("LCT_Block");
                if (b != null) {
                    if (LuckyCraftingTable.getByBlock(MyTasks.stringToBlock(b)) != null) {
                        EntityLCTNameTag.this.lct = LuckyCraftingTable.getByBlock(MyTasks.stringToBlock(b));
                        ((ArmorStand)EntityLCTNameTag.this.getEntity()).setCustomName(ChatColor.GREEN + MyTasks.val("lct.display_name", false));
                    }
                } else {
                    EntityLCTNameTag.this.remove();
                }

            }
        }, 15L));
    }

    public Immunity[] getImmuneTo() {
        return Immunity.values();
    }
}
