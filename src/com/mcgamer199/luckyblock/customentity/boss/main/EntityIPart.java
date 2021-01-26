package com.mcgamer199.luckyblock.customentity.boss.main;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.logic.ITask;

import java.util.Iterator;
import java.util.UUID;

public class EntityIPart extends CustomEntity {
    private ArmorStand a;
    public double[] offset = new double[3];
    private boolean running;
    public ItemStack item;
    public double amountDegree = 0.0D;
    public int t = 0;
    private Entity attachedEntity;

    public EntityIPart() {
    }

    public EntityIPart(Entity attachedEntity) {
        this.attachedEntity = attachedEntity;
    }

    protected void onChunkLoad() {
        Iterator var2 = this.entity.getWorld().getEntitiesByClass(this.attachedEntity.getClass()).iterator();

        while(var2.hasNext()) {
            Entity e = (Entity)var2.next();
            if (e.getUniqueId().toString().equalsIgnoreCase(this.attachedEntity.getUniqueId().toString())) {
                this.attachedEntity = e;
                break;
            }
        }

    }

    protected Entity spawnFunction(Location loc) {
        ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setBasePlate(false);
        as.setVisible(false);
        as.setMarker(true);
        as.setGravity(false);
        as.setSilent(true);
        this.a = as;
        this.func_tick();
        return as;
    }

    public void run_rotate() {
        this.running = true;
        this.save_def();
        this.run1();
        this.run1();
        this.run1();
        this.run1();
        this.run1();
    }

    private void run1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (EntityIPart.this.running && !EntityIPart.this.a.isDead()) {
                    if (EntityIPart.this.amountDegree > 0.0D) {
                        EntityIPart.this.rotate_head(EntityIPart.this.amountDegree);
                    }
                } else {
                    task.run();
                }

            }
        }, (long)this.t, (long)this.t));
    }

    private void func_tick() {
        this.func_tick1();
    }

    public void update_item() {
        if (this.item != null) {
            this.a.getEquipment().setHelmet(this.item);
        }

    }

    private void func_tick1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!EntityIPart.this.attachedEntity.isDead()) {
                    Location loc = EntityIPart.this.attachedEntity.getLocation();
                    EntityIPart.this.a.teleport(loc.add(EntityIPart.this.offset[0], EntityIPart.this.offset[1], EntityIPart.this.offset[2]));
                } else {
                    task.run();
                    EntityIPart.this.remove();
                }

            }
        }, 1L, 1L));
    }

    private void rotate_head(double degree) {
        EulerAngle r = this.a.getHeadPose();
        double i = r.getY();
        if (i > 360.0D) {
            i -= 360.0D;
        }

        EulerAngle e = new EulerAngle(r.getX(), i + degree, r.getZ());
        this.a.setHeadPose(e);
    }

    public void stop() {
        this.running = false;
    }

    public Entity getAttachedEntity() {
        return this.attachedEntity;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("attachedEntity", this.attachedEntity.getUniqueId().toString());
        c.set("Running", this.running);
        c.set("Offset.X", this.offset[0]);
        c.set("Offset.Y", this.offset[1]);
        c.set("Offset.Z", this.offset[2]);
        c.set("AmountDegree", this.amountDegree);
        c.set("TickTime", this.t);
        c.set("Item", this.item);
    }

    protected void onLoad(final ConfigurationSection c) {
        this.offset[0] = c.getDouble("Offset.X");
        this.offset[1] = c.getDouble("Offset.Y");
        this.offset[2] = c.getDouble("Offset.Z");
        this.running = c.getBoolean("Running");
        this.amountDegree = c.getDouble("AmountDegree");
        this.t = c.getInt("TickTime");
        this.item = c.getItemStack("Item");
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                EntityIPart.this.attachedEntity = CustomEntity.getByUUID(UUID.fromString(c.getString("attachedEntity"))).getEntity();
                EntityIPart.this.a = (ArmorStand)EntityIPart.this.entity;
                EntityIPart.this.func_tick();
                if (EntityIPart.this.running) {
                    EntityIPart.this.run_rotate();
                }

            }
        }, 15L));
    }
}

