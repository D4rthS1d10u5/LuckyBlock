package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.UUID;

public class EntityNameTag extends CustomEntity {
    public boolean hideInvis = true;
    protected ArmorStand armorStand;
    private Entity attachedEntity;
    private double[] offset = new double[3];

    public EntityNameTag() {
    }

    public void spawn(Entity attachedEntity, double[] offset) {
        this.attachedEntity = attachedEntity;
        this.offset = offset;
        Location loc = attachedEntity.getLocation();
        final ArmorStand as = (ArmorStand) attachedEntity.getWorld().spawnEntity(loc.add(offset[0], offset[1], offset[2]), EntityType.ARMOR_STAND);
        as.setMarker(true);
        as.setGravity(false);
        as.setVisible(false);
        this.armorStand = as;
        super.spawn_1(loc, as);
        this.func_1();
        this.func_load();
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                as.setCustomNameVisible(true);
            }
        }, 10L));
    }

    protected void onChunkLoad() {
        Iterator var2 = this.entity.getWorld().getEntitiesByClass(this.attachedEntity.getClass()).iterator();

        while (var2.hasNext()) {
            Entity e = (Entity) var2.next();
            if (e.getUniqueId().toString().equalsIgnoreCase(this.attachedEntity.getUniqueId().toString())) {
                this.attachedEntity = e;
                break;
            }
        }

        this.func_load();
    }

    protected void func_load() {
    }

    public Entity getAttachedEntity() {
        return this.attachedEntity;
    }

    private boolean isInvisible() {
        if (this.attachedEntity instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) this.attachedEntity;
            return l.hasPotionEffect(PotionEffectType.INVISIBILITY);
        }

        return false;
    }

    protected final void onTick() {
        if (this.hideInvis && this.isInvisible()) {
            this.armorStand.setCustomNameVisible(false);
        } else {
            this.armorStand.setCustomNameVisible(true);
            this.tick();
        }

    }

    protected void tick() {
    }

    private void func_1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                if (!EntityNameTag.this.attachedEntity.isDead()) {
                    Location l = EntityNameTag.this.attachedEntity.getLocation().add(EntityNameTag.this.offset[0], EntityNameTag.this.offset[1], EntityNameTag.this.offset[2]);
                    EntityNameTag.this.armorStand.teleport(l);
                } else {
                    EntityNameTag.this.remove();
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    protected final void onSave(ConfigurationSection c) {
        c.set("attachedEntity", this.attachedEntity.getUniqueId().toString());
        c.set("Offset.X", this.offset[0]);
        c.set("Offset.Y", this.offset[1]);
        c.set("Offset.Z", this.offset[2]);
        this.onsave(c);
    }

    protected final void onLoad(final ConfigurationSection c) {
        this.offset[0] = c.getDouble("Offset.X");
        this.offset[1] = c.getDouble("Offset.Y");
        this.offset[2] = c.getDouble("Offset.Z");
        this.armorStand = (ArmorStand) this.entity;
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlockPlugin.instance, new Runnable() {
            public void run() {
                EntityNameTag.this.attachedEntity = CustomEntity.getByUUID(UUID.fromString(c.getString("attachedEntity"))).getEntity();
                EntityNameTag.this.onload(c);
                EntityNameTag.this.func_1();
                EntityNameTag.this.func_load();
            }
        }, 15L));
    }

    protected void onsave(ConfigurationSection c) {
    }

    protected void onload(ConfigurationSection c) {
    }
}
