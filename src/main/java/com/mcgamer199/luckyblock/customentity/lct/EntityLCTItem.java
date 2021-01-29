package com.mcgamer199.luckyblock.customentity.lct;

import com.mcgamer199.luckyblock.advanced.LuckyCraftingTable;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityLCTItem extends CustomEntity {
    private LuckyCraftingTable lct;
    private ArmorStand armorStand;

    public EntityLCTItem() {
    }

    public EntityLCTItem(LuckyCraftingTable lct) {
        this.lct = lct;
    }

    protected Entity spawnFunction(Location loc) {
        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setVisible(false);
        this.armorStand = armorStand;
        this.func_loop();
        return armorStand;
    }

    private void func_loop() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (EntityLCTItem.this.lct != null && EntityLCTItem.this.lct.isValid()) {
                    boolean setAir = false;
                    if (EntityLCTItem.this.lct.slot > -1 && EntityLCTItem.this.lct.isRunning() && EntityLCTItem.this.lct.i().getItem(EntityLCTItem.this.lct.slot) != null && EntityLCTItem.this.lct.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                        EntityLCTItem.this.armorStand.setHelmet(EntityLCTItem.this.lct.i().getItem(EntityLCTItem.this.lct.slot));
                        setAir = true;
                    }

                    if (!setAir) {
                        EntityLCTItem.this.armorStand.setHelmet(null);
                    }
                } else {
                    EntityLCTItem.this.remove();
                    task.run();
                }

            }
        }, 20L, 2L));
    }

    protected void onLoad(final ConfigurationSection c) {
        this.armorStand = (ArmorStand) this.entity;
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                EntityLCTItem.this.lct = LuckyCraftingTable.getByBlock(LocationUtils.blockFromString(c.getString("LCT_Block")));
                EntityLCTItem.this.func_loop();
            }
        }, 10L));
    }

    protected void onSave(ConfigurationSection c) {
        c.set("LCT_Block", LocationUtils.asString(this.lct.getBlock().getLocation()));
    }
}
